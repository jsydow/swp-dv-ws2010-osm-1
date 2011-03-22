package tracebook.gui.activity;

import java.io.File;
import java.util.List;

import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapController;
import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.MapViewMode;
import org.mapsforge.android.maps.OverlayItem;

import tracebook.core.data.DataNode;
import tracebook.core.data.DataPointsList;
import tracebook.core.logger.ServiceConnector;
import tracebook.util.DataNodeArrayItemizedOverlay;
import tracebook.util.DataPointsListArrayRouteOverlay;
import tracebook.util.GpsMessage;
import tracebook.util.Helper;
import tracebook.util.LogIt;
import Trace.Book.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;

/**
 * This class implements a MapsForge Map activity and draws ways and nodes as
 * overlays on it. In the future it will also be possible to modify said nodes
 * and ways.
 */
public class MapsForgeActivity extends MapActivity {
    /**
     * This class receives Broadcast Messages from the WaypointLogService in
     * order to update the current location and overlays if position or overlay
     * data has changed.
     */
    private class GPSReceiver extends BroadcastReceiver {

        /**
         * Centers the map to the current position if true. This will be set to
         * false once the map is centered, it's used to initially center the map
         * when no GPS fix is available yet
         */
        boolean centerMap = true;

        /**
         * The last known {@link GeoPoint}.
         */
        GeoPoint currentGeoPoint = null;

        public GPSReceiver() { /* nothing to do here */
        }

        @Override
        public void onReceive(Context ctx, Intent intend) {
            final int wayId = intend.getExtras().getInt("way_id");
            final int pointId = intend.getExtras().getInt("point_id");

            // Receive current location and do something with it
            switch (intend.getIntExtra("type", -1)) {

            case GpsMessage.UPDATE_GPS_POS:
                final double lng = intend.getExtras().getDouble("long");
                final double lat = intend.getExtras().getDouble("lat");

                currentGeoPoint = new GeoPoint(lat, lng); // we also need this
                // to center the map
                LogIt.d(LOG_TAG,
                        "Location update received "
                                + currentGeoPoint.toString());

                pointsOverlay.setCurrentPosition(currentGeoPoint);

                /*
                 * In one_shot mode, we add the current point to the
                 * visualization
                 */
                if (intend.getExtras().getBoolean("one_shot")) {
                    DataPointsList currentWay = Helper.currentTrack()
                            .getCurrentWay();
                    if (currentWay != null
                            && currentWay.getOverlayRoute() != null)
                        routesOverlay.reDrawWay(currentWay, true,
                                currentGeoPoint);
                }

                if (centerMap)
                    centerOnCurrentPosition();

                break;
            // Receive an update of a way and update the overlay accordingly
            case GpsMessage.UPDATE_OBJECT:
                if (Helper.currentTrack() == null) {
                    LogIt.e(LOG_TAG,
                            "Received UPDATE_OBJECT with no current track present.");
                    return;
                }

                LogIt.d(LOG_TAG, "UPDATE_OBJECT received, way: " + wayId
                        + " node: " + pointId);

                if (wayId > 0)
                    routesOverlay.reDrawWay(wayId);
                else if (pointId > 0)
                    updatePoint(pointId);

                break;
            case GpsMessage.MOVE_POINT:
                if (Helper.currentTrack() != null) {
                    editNode = Helper.currentTrack().getNodeById(pointId);
                    LogIt.d(LOG_TAG, "Enter edit mode for Point " + pointId);
                }

                break;
            case GpsMessage.END_WAY:
                routesOverlay.reDrawWay(intend.getExtras().getInt("way_id"));
                pointsOverlay.removeOrphans();

                break;
            default:
            }
        }

        private void updatePoint(int pointId) {
            LogIt.d(LOG_TAG, "Received node update, id=" + pointId);

            DataNode point = null;
            if (Helper.currentTrack() != null) {
                point = Helper.currentTrack().getNodeById(pointId);
            }
            if (point == null)
                LogIt.e(LOG_TAG, "Node with ID " + pointId + " does not exist.");
            else {
                if (point.getOverlayItem() == null)
                    point.setOverlayItem(new OverlayItem(point.toGeoPoint(),
                            point.getId() + "", ""));
                pointsOverlay.addOverlay(point);
                if (point.getDataPointsList() != null)
                    routesOverlay.reDrawWay(point.getDataPointsList(), false,
                            null);
            }
        }

        /**
         * Request the map to be centered to the current position.
         */
        void centerOnCurrentPosition() {
            if (currentGeoPoint != null) {
                mapController.setCenter(currentGeoPoint);
                centerMap = false;
            } else
                centerMap = true;
        }
    }

    private static final String LOG_TAG = "MapsForgeActivity";
    // TODO: user configurable
    private String defaultMap = "/sdcard/default.map";
    private GPSReceiver gpsReceiver;
    private MapView mapView;
    // TODO: user configurable
    private MapViewMode onlineRenderer = MapViewMode.OSMARENDER_TILE_DOWNLOAD;

    private boolean useInternet = false;

    /**
     * Node currently edited.
     */
    DataNode editNode = null;

    /**
     * MapController object to interact with the Map.
     */
    MapController mapController;

    /**
     * Overlay containing all POIs.
     */
    DataNodeArrayItemizedOverlay pointsOverlay;

    /**
     * Overlay containing all areas and ways.
     */
    DataPointsListArrayRouteOverlay routesOverlay;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // When a node is edited, the user can move it by
        // moving his finger on the display
        if (editNode != null) {
            GeoPoint projection = mapView.getProjection().fromPixels(
                    (int) ev.getX(), (int) ev.getY());

            // We will never need to specify a icon as the OverlayItem already
            // must have had one, otherwise we couldn't have grabbed it in the
            // first place.
            pointsOverlay.updateItem(projection, editNode.getId(), 0);

            if (ev.getAction() == MotionEvent.ACTION_UP) {
                LogIt.d(LOG_TAG,
                        "Exiting edit mode for point " + editNode.getId());
                editNode = null;
            }

            return true;
        } else
            return super.dispatchTouchEvent(ev);
    }

    /**
     * This method inflate the options menu for this activity.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionsmenu_mapsforgeactivity, menu);
        return true;
    }

    /* MapActivity methods */

    /**
     * Catches the selected MenuItem from the options menu and 1. activate the
     * Internet to get more map data 2. Center the Map to the actual own
     * position 3. Stop tracking, show alert and go back to MainActivity 4.
     * Pause tracking and show alert 5. Export actual session to...
     * 
     * @param item
     *            the item
     * @return true, if successful
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.opt_mapsforgeActivity_activateMobileInternet:

            if (useInternet) {
                item.setTitle(getResources().getString(
                        R.string.opt_mapsforgeActivity_activateMobileInternet));
                changeMapViewToOfflineRendering();
            } else {
                item.setTitle(getResources()
                        .getString(
                                R.string.opt_mapsforgeActivity_deactivateMobileInternet));
                changeMapViewMode(onlineRenderer, null);
            }
            useInternet = !useInternet;

            return true;

        case R.id.opt_mapsforgeActivity_centerAtOwnPosition:

            gpsReceiver.centerOnCurrentPosition();
            return true;
        case R.id.opt_mapsforgeActivity_showToggleWayPoints:

            routesOverlay.toggleWaypoints();
            return true;
        case R.id.opt_mapsforgeActivity_export:
            /*
             * Do SOMETHING TODO
             */
            return true;

        case R.id.opt_mapsforgeActivity_pause:
            try {
                if (ServiceConnector.getLoggerService().isLogging()) {
                    item.setTitle(getResources().getString(
                            R.string.opt_mapsforgeActivity_resume));
                    item.setIcon(android.R.drawable.ic_media_play);
                    ServiceConnector.getLoggerService().pauseLogging();
                } else {
                    item.setTitle(getResources().getString(
                            R.string.opt_mapsforgeActivity_pause));
                    item.setIcon(android.R.drawable.ic_media_pause);
                    ServiceConnector.getLoggerService().resumeLogging();
                }
            } catch (RemoteException ex) {
                Helper.handleNastyException(this, ex, LOG_TAG);
            }
            return true;
        case R.id.opt_mapsforgeActivity_stopTrack:
            Helper.alertStopTracking(this);
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void changeMapViewToOfflineRendering() {
        changeMapViewMode(MapViewMode.CANVAS_RENDERER, new File(defaultMap));
    }

    private void fillOverlays() {
        List<DataNode> nodes = Helper.currentTrack().getNodes();
        if (nodes != null) {
            pointsOverlay.addPoints(nodes);
        }
        List<DataPointsList> ways = Helper.currentTrack().getWays();
        if (nodes != null) {
            routesOverlay.addWays(ways);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogIt.d(LOG_TAG, "Creating MapActivity");

        pointsOverlay = new DataNodeArrayItemizedOverlay(this, routesOverlay);
        routesOverlay = new DataPointsListArrayRouteOverlay(this, pointsOverlay);
        pointsOverlay.setRoutesOverlay(routesOverlay);

        // as this activity is destroyed when adding a POI, we get all POIs here
        fillOverlays();

        mapView = new MapView(this);
        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(true);
        mapView.setScaleBar(true);

        mapView.getOverlays().add(routesOverlay);
        mapView.getOverlays().add(pointsOverlay);

        mapController = mapView.getController();

        setContentView(mapView);

        gpsReceiver = new GPSReceiver();

        changeMapViewToOfflineRendering();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogIt.d(LOG_TAG, "Destroying map activity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogIt.d(LOG_TAG, "Pausing MapActivity");
        unregisterReceiver(gpsReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogIt.d(LOG_TAG, "Resuming MapActivity");

        // redraw all overlays to account for the events we've missed paused
        routesOverlay.clear();
        pointsOverlay.clear();
        fillOverlays();

        registerReceiver(gpsReceiver, new IntentFilter(GpsMessage.TAG));
    }

    /**
     * Changes the render mode of the map. Possible modes are specified in
     * {@link MapViewMode}, if file is specified and the CANVAS_RENDERER mode is
     * selected, the map will be rendered off-line. If the file does not exist,
     * it will default to fetching the tiles from the Internet
     * 
     * @param mode
     *            {@link MapViewMode} render mode
     * @param file
     *            map file for off-line rendering
     */
    void changeMapViewMode(MapViewMode mode, File file) {
        MapViewMode modeLocal = mode;

        if (mode == MapViewMode.CANVAS_RENDERER) {
            if (file == null || !file.exists()) {
                LogIt.popup(
                        this,
                        getResources().getString(
                                R.string.toast_loadingOnlineMap));
                modeLocal = onlineRenderer;
            } else {
                mapView.setMapViewMode(modeLocal); // MapsForge crashes if we
                // specify a mapsfile when in
                // Online mode
                mapView.setMapFile(file.getAbsolutePath());
            }
        }
        mapView.setMapViewMode(modeLocal);

        gpsReceiver.centerOnCurrentPosition();
    }
}
