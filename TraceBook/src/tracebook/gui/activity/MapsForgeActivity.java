/*======================================================================
 *
 * This file is part of TraceBook.
 *
 * TraceBook is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * TraceBook is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TraceBook. If not, see <http://www.gnu.org/licenses/>.
 *
 =====================================================================*/

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
import tracebook.core.data.DataStorage;
import tracebook.core.logger.ServiceConnector;
import tracebook.util.DataPointsListArrayRouteOverlay;
import tracebook.util.GpsMessage;
import tracebook.util.Helper;
import tracebook.util.LogIt;
import tracebook.util.NewDataNodeArrayItemizedOverlay;
import Trace.Book.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

        private int oldWayId = -1;

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

        OverlayItem currentPosOI = null;

        public GPSReceiver() {
            // Do nothing
        }

        @Override
        public void onReceive(Context ctx, Intent intend) {
            final int wayId = intend.getExtras().getInt("way_id");
            final int pointId = intend.getExtras().getInt("point_id");

            // Receive current location and do something with it
            switch (intend.getIntExtra("type", -1)) {

            case GpsMessage.UPDATE_GPS_POS: // periodic position update
                final double lng = intend.getExtras().getDouble("long");
                final double lat = intend.getExtras().getDouble("lat");

                currentGeoPoint = new GeoPoint(lat, lng); // we also need this
                // to center the map

                if (currentPosOI == null)
                    currentPosOI = Helper.getOverlayItem(currentGeoPoint,
                            R.drawable.marker_green, MapsForgeActivity.this);
                currentPosOI.setPoint(currentGeoPoint);
                pointsOverlay.addItem(currentPosOI);

                /*
                 * In one_shot mode, we add the current point to the
                 * visualization
                 */
                if (intend.getExtras().getBoolean("one_shot")) {
                    DataPointsList currentWay = Helper.currentTrack()
                            .getCurrentWay();
                    if (currentWay != null) {
                        currentWay.updateOverlayRoute(currentGeoPoint);
                        routesOverlay.requestRedraw();
                    }
                }

                if (centerMap)
                    centerOnCurrentPosition();

                break;
            // Receive an update of a way and update the overlay accordingly
            case GpsMessage.UPDATE_OBJECT:
                LogIt.d(LOG_TAG, "UPDATE_OBJECT received, way: " + wayId
                        + " node: " + pointId);

                if (wayId > 0) {
                    DataPointsList way = Helper.currentTrack()
                            .getPointsListById(wayId);
                    if (way != null) {
                        way.updateOverlayRoute();
                        if (oldWayId != wayId) {
                            oldWayId = wayId;
                            routesOverlay.addWay(way, true);
                        } else
                            routesOverlay.requestRedraw();

                        if (pointId > 0) { // new waypoint
                            DataNode node = Helper.currentTrack().getNodeById(
                                    pointId);
                            if (node != null)
                                routesOverlay.putWaypoint(node);
                        }

                    } else
                        LogIt.d(LOG_TAG, "Way can not be found.");
                }

                // } else if (pointId > 0) { // New POI - this does actually not
                // // get called as we update all POIs
                // // onResume(), we currently only get
                // // new POIs by another Activity
                // LogIt.d(LOG_TAG, "update point " + pointId);
                // DataNode node = Helper.currentTrack().getNodeById(pointId);
                // if (node != null) {
                // if (node.getOverlayItem() == null)
                // node.setOverlayItem(Helper.getOverlayItem(
                // node.toGeoPoint(),
                // ItemizedOverlay
                // .boundCenterBottom(getResources()
                // .getDrawable(
                // R.drawable.marker_red))));
                // pointsOverlay.addItem(node.getOverlayItem());
                // } else
                // LogIt.d(LOG_TAG, "point is null");
                // }

                break;
            case GpsMessage.MOVE_POINT:
                LogIt.d(LOG_TAG, "Enter edit mode for Point " + pointId);

                editNode = Helper.currentTrack().getNodeById(pointId);

                break;
            case GpsMessage.END_WAY:
                LogIt.d(LOG_TAG, "End way for way " + wayId + " received.");

                DataPointsList way = Helper.currentTrack().getPointsListById(
                        wayId);
                if (way != null) {
                    way.updateOverlayRoute();
                    routesOverlay.color(way, false);
                    routesOverlay.requestRedraw();
                }

                break;
            case GpsMessage.REMOVE_INVALIDS:
                LogIt.d(LOG_TAG, "Request to remove invalid nodes");

                List<OverlayItem> invalids = Helper.currentTrack()
                        .clearInvalidItems();
                for (OverlayItem oi : invalids)
                    pointsOverlay.removeItem(oi);
                break;
            default:
                LogIt.e(LOG_TAG,
                        "unhandled Message, ID="
                                + intend.getIntExtra("type", -1));
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
    NewDataNodeArrayItemizedOverlay pointsOverlay;

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

            editNode.setLocation(projection);
            if (editNode.getDataPointsList() != null) {
                editNode.getDataPointsList().updateOverlayRoute();
                LogIt.d(LOG_TAG, "Requesting redraw");
                routesOverlay.requestRedraw();
            }

            pointsOverlay.requestRedraw();

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
            DataStorage.getInstance().serialize();
            LogIt.popup(
                    this,
                    getResources().getString(
                            R.string.popup_mapsforgeactivity_saved));
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
        for (DataNode n : Helper.currentTrack().getNodes()) {
            if (n.getOverlayItem() == null)
                n.setOverlayItem(Helper.getOverlayItem(this));
            pointsOverlay.addItem(n.getOverlayItem());
        }

        routesOverlay.addWays(Helper.currentTrack().getWays());
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null) {
            return info.isConnectedOrConnecting();
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogIt.d(LOG_TAG, "Creating MapActivity");

        pointsOverlay = new NewDataNodeArrayItemizedOverlay(this);
        routesOverlay = new DataPointsListArrayRouteOverlay(this, pointsOverlay);

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
        } else {
            if (!isOnline()) {
                LogIt.popup(
                        this,
                        getResources().getString(
                                R.string.toast_noInternetAccess));
            }
        }
        mapView.setMapViewMode(modeLocal);

        gpsReceiver.centerOnCurrentPosition();
    }

}