package gui.activity;

import java.io.File;
import java.util.List;

import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapController;
import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.MapViewMode;
import org.mapsforge.android.maps.OverlayItem;

import util.DataNodeArrayItemizedOverlay;
import util.DataPointsListArrayRouteOverlay;
import util.Helper;
import Trace.Book.R;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;
import core.data.DataNode;
import core.data.DataPointsList;
import core.logger.ServiceConnector;
import core.logger.WaypointLogService;

/**
 * This class implements a MapsForge Map activity and draws ways and nodes as
 * overlays on it. In the future it will also be possible to modify said nodes
 * and ways.
 * 
 * @author benpicco
 * 
 */
public class MapsForgeActivity extends MapActivity {
    private static final String LOG_TAG = "MapsForgeActivity";
    // TODO: user configurable
    private String defaultMap = "/sdcard/default.map";
    // TODO: user configurable
    private MapViewMode onlineRenderer = MapViewMode.OSMARENDER_TILE_DOWNLOAD;
    private MapView mapView;
    private GPSReceiver gpsReceiver;
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
     * Overlay containing all areas and ways.
     */
    DataPointsListArrayRouteOverlay routesOverlay;

    /**
     * Overlay containing all POIs.
     */
    DataNodeArrayItemizedOverlay pointsOverlay;

    /**
     * Changes the render mode of the map. Possible modes are specified in
     * {@link MapViewMode}, if file is specified and the CANVAS_RENDERER mode is
     * selected, the map will be rendered offline. If the file does not exist,
     * it will default to fetching the tiles from the Internet
     * 
     * @param mode
     *            {@link MapViewMode} render mode
     * @param file
     *            map file for offline rendering
     */
    void changeMapViewMode(MapViewMode mode, File file) {
        MapViewMode modeLocal = mode;

        if (mode == MapViewMode.CANVAS_RENDERER) {
            if (file == null || !file.exists()) {
                Toast.makeText(
                        getApplicationContext(),
                        "Unable to open map file, fetching tiles from Internet.",
                        Toast.LENGTH_LONG).show();
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

    /* MapActivity methods */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LOG_TAG, "Creating MapActivity");

        pointsOverlay = new DataNodeArrayItemizedOverlay(this, routesOverlay);
        routesOverlay = new DataPointsListArrayRouteOverlay(this, pointsOverlay);
        pointsOverlay.setRoutesOverlay(routesOverlay);

        // as this activity is destroyed when adding a POI, we get all POIs here
        fillOverlays();

        mapView = new MapView(this);
        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(true);

        mapView.getOverlays().add(routesOverlay);
        mapView.getOverlays().add(pointsOverlay);

        mapController = mapView.getController();

        setContentView(mapView);

        gpsReceiver = new GPSReceiver();

        changeMapViewToOfflineRendering();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "Resuming MapActivity");

        // redraw all overlays to account for the events we've missed paused
        routesOverlay.clear();
        pointsOverlay.clear();
        fillOverlays();

        registerReceiver(gpsReceiver, new IntentFilter(
                WaypointLogService.UPDATE_GPS_POS));
        registerReceiver(gpsReceiver, new IntentFilter(
                WaypointLogService.UPDATE_OBJECT));
        registerReceiver(gpsReceiver, new IntentFilter(
                DataNodeArrayItemizedOverlay.UPDATE_WAY));
        registerReceiver(gpsReceiver, new IntentFilter(
                DataNodeArrayItemizedOverlay.MOVE_POINT));

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "Pausing MapActivity");
        unregisterReceiver(gpsReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "Destroying map activity");
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // when a node is edited, the user can move it by
        // moving his finger on the display
        if (editNode != null) {
            GeoPoint projection = mapView.getProjection().fromPixels(
                    (int) ev.getX(), (int) ev.getY());

            // we will never need to specify a icon as the OverlayItem already
            // must have had one, otherwise we couldn't have grabbed it in the
            // first place.
            pointsOverlay.updateItem(projection, editNode.getId(), 0);

            if (ev.getAction() == MotionEvent.ACTION_UP) {
                Log.d(LOG_TAG,
                        "Exiting edit mode for point " + editNode.getId());
                editNode = null;
            }

            return true;
        } else
            return super.dispatchTouchEvent(ev);
    }

    /**
     * This method inflate the options menu for this activity.
     * 
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionsmenu_mapsforgeactivity, menu);
        return true;
    }

    /**
     * This method catch the selected MenuItem from the options menu and 1.
     * activate the Internet to get more Mapmaterial 2. Center the Map to the
     * actual own position 3. Stop tracking, show alert and go back to
     * MainActivity 4. Pause tracking and show alert 5. Export actual Seassion
     * to...
     * 
     * @param item
     *            the item
     * @return true, if successful
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.opt_mapsforgeActivity_activateMobileInternet:

            if (useInternet) {
                // TODO: use string ressource!
                item.setTitle("Use online rendering");
                changeMapViewToOfflineRendering();
            } else {
                item.setTitle("Use offline rendering");
                changeMapViewMode(onlineRenderer, null);
            }
            useInternet = !useInternet;

            return true;

        case R.id.opt_mapsforgeActivity_centerAtOwnPosition:

            gpsReceiver.centerOnCurrentPosition();
            return true;
        case R.id.opt_mapsforgeActivity_showToggleWayPoints:

            routesOverlay.toggleGnubbel();
            return true;
        case R.id.opt_mapsforgeActivity_export:
            /*
             * Do SOMETHING TODO
             */
            return true;

        case R.id.opt_mapsforgeActivity_pause:

            builder.setMessage(
                    getResources().getString(
                            R.string.alert_mapsforgeActivity_pause))
                    .setCancelable(false)
                    .setPositiveButton(
                            getResources().getString(R.string.alert_global_yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int id) {
                                    try {
                                        // TODO really stop track?
                                        // stopping track does unload track from
                                        // memory
                                        // and serialise current track
                                        ServiceConnector.getLoggerService()
                                                .stopTrack();
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
                                    /*
                                     * DO SOMETHING TODO
                                     */
                                }
                            })
                    .setNegativeButton(
                            getResources().getString(R.string.alert_global_no),
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    dialog.cancel();
                                }
                            });
            builder.show();
            return true;
        case R.id.opt_mapsforgeActivity_stopTrack:
            builder.setMessage(
                    getResources().getString(R.string.alert_global_exit))
                    .setCancelable(false)
                    .setPositiveButton(
                            getResources().getString(R.string.alert_global_yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int id) {
                                    try {
                                        ServiceConnector.getLoggerService()
                                                .stopTrack();
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
                                    finish();

                                }
                            })
                    .setNegativeButton(
                            getResources().getString(R.string.alert_global_no),
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    dialog.cancel();
                                }
                            });
            builder.show();
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This class receives Broadcast Messages from the WaypointLogService in
     * order to update the current location and overlays if position or overlay
     * data has changed.
     * 
     * @author benpicco
     * 
     */
    private class GPSReceiver extends BroadcastReceiver {

        /**
         * will center the map to the current position if true. This will be set
         * to false once the map is centered, it's used to initially center the
         * map when no GPS fix is available yet
         */
        boolean centerMap = true;

        /**
         * The last known {@link GeoPoint}.
         */
        GeoPoint currentGeoPoint = null;

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

        int oldWayId = -1;

        public GPSReceiver() { /* nothing to do here */
        }

        @Override
        public void onReceive(Context ctx, Intent intend) {

            // Receive current location and do something with it
            if (intend.getAction().equals(WaypointLogService.UPDATE_GPS_POS)) {
                final double lng = intend.getExtras().getDouble("long");
                final double lat = intend.getExtras().getDouble("lat");

                currentGeoPoint = new GeoPoint(lat, lng); // we also need this
                                                          // to center the map
                Log.d(LOG_TAG,
                        "Location update received "
                                + currentGeoPoint.toString());

                pointsOverlay.setCurrentPosition(currentGeoPoint);

                if (centerMap)
                    centerOnCurrentPosition();

                // Receive an update of a way and update the overlay accordingly
            } else if (intend.getAction().equals(
                    WaypointLogService.UPDATE_OBJECT)) {
                if (Helper.currentTrack() == null) {
                    Log.e(LOG_TAG,
                            "Received UPDATE_OBJECT with no current track present.");
                    return;
                }

                int wayId = intend.getExtras().getInt("way_id");
                int pointId = intend.getExtras().getInt("point_id");

                if (wayId > 0) {
                    updateWay(wayId);

                } else if (pointId > 0) {
                    updatePoint(pointId);
                }

            } else if (intend.getAction().equals(
                    DataNodeArrayItemizedOverlay.UPDATE_WAY)) {
                int wayId = intend.getExtras().getInt("way_id");
                Log.d(LOG_TAG, "Nodes of way " + wayId + " have changed.");

                routesOverlay.reDrawWay(wayId);
            } else if (intend.getAction().equals(
                    DataNodeArrayItemizedOverlay.MOVE_POINT)) {
                final int nodeId = intend.getExtras().getInt("point_id");
                if (Helper.currentTrack() != null) {
                    editNode = Helper.currentTrack().getNodeById(nodeId);
                    Log.d(LOG_TAG, "Enter edit mode for Point " + nodeId);
                }
            }
        }

        private void updateWay(int newWayId) {
            // TODO comment here please
            if (newWayId != oldWayId) { // TODO
                routesOverlay.reDrawWay(oldWayId);
                oldWayId = newWayId;
            }
            Log.d(LOG_TAG, "Received way update, id=" + newWayId);
            routesOverlay.reDrawWay(newWayId);
        }

        private void updatePoint(int pointId) {
            // received an updated POI -
            // when does this actually happen?
            Log.d(LOG_TAG, "Received node update, id=" + pointId);

            DataNode point = null;
            if (Helper.currentTrack() != null) {
                point = Helper.currentTrack().getNodeById(pointId);
            }
            if (point == null)
                Log.e(LOG_TAG, "Node with ID " + pointId + " does not exist.");
            else {
                if (point.getOverlayItem() == null)
                    point.setOverlayItem(new OverlayItem(point.toGeoPoint(),
                            point.getId() + "", ""));
                pointsOverlay.addOverlay(point);
            }
        }
    }
}