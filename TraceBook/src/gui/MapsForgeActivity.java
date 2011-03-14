package gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.mapsforge.android.maps.ArrayRouteOverlay;
import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.ItemizedOverlay;
import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapController;
import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.MapViewMode;
import org.mapsforge.android.maps.OverlayItem;
import org.mapsforge.android.maps.OverlayRoute;

import util.DataNodeArrayItemizedOverlay;
import Trace.Book.R;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;
import core.data.DataNode;
import core.data.DataPointsList;
import core.data.DataStorage;
import core.data.DataTrack;
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
    private String defaultMap = "/sdcard/default.map";
    private MapView mapView;
    private GPSReceiver gpsReceiver;
    private int colorID = 0;
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
    ArrayRouteOverlay routesOverlay;

    /**
     * Overlay containing all POIs.
     */
    DataNodeArrayItemizedOverlay pointsOverlay;

    /**
     * List of possible colors for ways and areas the first color in the list is
     * always used for the current way.
     */
    List<Pair<Paint, Paint>> colors;

    /**
     * Show a knob for every waypoint.
     */
    boolean showGnubbel = true;

    /**
     * Gets a color from the rotating color array.
     * 
     * @return a {@link Pair} of {@link Paint} where the first element is the
     *         FillPaint and the second one the OutlinePaint
     */
    Pair<Paint, Paint> getColor() {
        colorID = colorID % (colors.size() - 1) + 1;
        return colors.get(colorID);
    }

    /**
     * generates a pair of paint objects with the same color, but different
     * levels of transparency
     */
    private static Pair<Paint, Paint> getPaintPair(int color) {
        Paint paintOutline = new Paint();
        paintOutline.setAntiAlias(true);
        paintOutline.setStyle(Paint.Style.STROKE);
        paintOutline.setStrokeWidth(7);
        paintOutline.setStrokeCap(Paint.Cap.BUTT);
        paintOutline.setStrokeJoin(Paint.Join.ROUND);
        paintOutline.setColor(color);
        paintOutline.setAlpha(96);

        Paint paintFill = new Paint(paintOutline);
        // paintFill.setStyle(Paint.Style.FILL);
        paintFill.setAlpha(160);

        return new Pair<Paint, Paint>(paintFill, paintOutline);
    }

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
                modeLocal = MapViewMode.OSMARENDER_TILE_DOWNLOAD;
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

    /* Overlay related methods */

    private void addPoints() {
        for (DataNode n : currentTrack().getNodes()) {
            if (n.getOverlayItem() == null)
                n.setOverlayItem(new OverlayItem(n.toGeoPoint(), "", ""));
            pointsOverlay.addOverlay(n);
        }
    }

    private void addWays() {
        for (DataPointsList l : currentTrack().getWays()) {
            if (l.getNodes().size() == 0) // skip empty ways
                continue;
            if (l.getOverlayRoute() == null) {
                Pair<Paint, Paint> col = getColor();
                l.setOverlayRoute(new OverlayRoute(l.toGeoPointArray(),
                        col.first, col.second));
            }
            routesOverlay.addRoute(l.getOverlayRoute());

            if (showGnubbel)
                addGnubbel(l);
        }
    }

    /**
     * Update the color of a way once its not the current way any more.
     * 
     * @param id
     */
    void reDrawWay(int id) {
        if (id <= 0)
            return;
        reDrawWay(currentTrack().getPointsListById(id));
    }

    /**
     * Redraw the given way.
     * 
     * @param way
     */
    void reDrawWay(DataPointsList way) {
        if (way == null)
            return;
        routesOverlay.removeOverlay(way.getOverlayRoute());
        final Pair<Paint, Paint> color = getColor();
        way.setOverlayRoute(new OverlayRoute(way.toGeoPointArray(),
                color.first, color.second));
        routesOverlay.addRoute(way.getOverlayRoute());
    }

    private void addGnubbel(DataPointsList way) {
        for (DataNode n : way.getNodes()) {
            if (n.getOverlayItem() == null)
                n.setOverlayItem(getOverlayItem(n.toGeoPoint(),
                        R.drawable.marker_blue));
            pointsOverlay.addOverlay(n);
        }
    }

    private void removeGnubbel(DataPointsList way) {
        for (DataNode n : way.getNodes())
            pointsOverlay.removeOverlay(n.getId());
    }

    /**
     * Creates a new OverlayItem.
     * 
     * @param pos
     *            position of the marker
     * @param marker
     *            id of the Graphics object to use
     * @return the new OverlayItem
     */
    OverlayItem getOverlayItem(GeoPoint pos, int marker) {
        final OverlayItem oi = new OverlayItem(pos, null, null);
        Drawable icon = getResources().getDrawable(marker);
        oi.setMarker(ItemizedOverlay.boundCenterBottom(icon));

        return oi;
    }

    /**
     * Creates a new OverlayItem.
     * 
     * @param pos
     *            position of the marker
     * @param marker
     *            Graphics object to be displayed at the position
     * @return the new OverlayItem
     */
    OverlayItem getOverlayItem(GeoPoint pos, Drawable marker) {
        final OverlayItem oi = new OverlayItem(pos, null, null);
        oi.setMarker(marker);
        return oi;
    }

    /**
     * Gets the current DataTrack for convenience.
     * 
     * @return the current DataTrack object
     */
    static DataTrack currentTrack() {
        return DataStorage.getInstance().getCurrentTrack();
    }

    /* MapActivity methods */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LOG_TAG, "Creating MapActivity");

        // create paint list
        colors = new ArrayList<Pair<Paint, Paint>>();
        colors.add(getPaintPair(Color.rgb(0, 255, 0)));
        colors.add(getPaintPair(Color.rgb(0, 0, 230)));
        colors.add(getPaintPair(Color.rgb(0, 0, 200)));
        colors.add(getPaintPair(Color.rgb(0, 0, 170)));

        pointsOverlay = new DataNodeArrayItemizedOverlay(getResources()
                .getDrawable(R.drawable.marker_red), this);
        routesOverlay = new ArrayRouteOverlay(null, null);

        // as this activity is destroyed when adding a POI, we get all POIs here
        addPoints();
        addWays();

        mapView = new MapView(this);
        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(true);

        mapView.getOverlays().add(routesOverlay);
        mapView.getOverlays().add(pointsOverlay);

        mapController = mapView.getController();

        setContentView(mapView);

        gpsReceiver = new GPSReceiver();

        changeMapViewMode(MapViewMode.CANVAS_RENDERER, new File(defaultMap));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "Resuming MapActivity");

        // redraw all overlays to account for the events we've missed paused
        routesOverlay.clear();
        pointsOverlay.clear();
        addPoints();
        addWays();

        registerReceiver(gpsReceiver, new IntentFilter(
                WaypointLogService.UPDTAE_GPS_POS));
        registerReceiver(gpsReceiver, new IntentFilter(
                WaypointLogService.UPDTAE_OBJECT));
        registerReceiver(gpsReceiver, new IntentFilter(
                DataNodeArrayItemizedOverlay.UPDTAE_WAY));
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
        if (editNode != null) {
            GeoPoint projection = mapView.getProjection().fromPixels(
                    (int) ev.getX(), (int) ev.getY());

            editNode.setLocation(projection);
            // it is save to assume we have a marker here as this is the only
            // way we could make this node editable
            final Drawable marker = editNode.getOverlayItem().getMarker();
            editNode.setOverlayItem(getOverlayItem(projection, marker));

            pointsOverlay.removeOverlay(editNode.getId());
            pointsOverlay.addOverlay(editNode);

            if (editNode.getDataPointsList() != null)
                reDrawWay(editNode.getDataPointsList());
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
     * @author greentraxas
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mapsforgeactivity_optionsmenu, menu);
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
     * 
     * @author greentraxas
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.activateMobileInternet_opt:

            if (useInternet) {
                item.setTitle("Use online rendering");
                changeMapViewMode(MapViewMode.CANVAS_RENDERER, new File(
                        defaultMap));
            } else {
                item.setTitle("Use offline rendering");
                changeMapViewMode(MapViewMode.MAPNIK_TILE_DOWNLOAD, null);
            }
            useInternet = !useInternet;

            return true;
        case R.id.centerAtOwnPosition_opt:
            gpsReceiver.centerOnCurrentPosition();
            return true;
        case R.id.showToggleWayPoints_opt:
            showGnubbel = !showGnubbel;
            pointsOverlay.clear();

            for (DataPointsList dpl : currentTrack().getWays())
                if (showGnubbel)
                    addGnubbel(dpl);
                else
                    removeGnubbel(dpl);
            return true;
        case R.id.export_opt:
            /*
             * Do SOMETHING
             */
            return true;
        case R.id.pause_opt:

            builder.setMessage(getResources().getString(R.string.pause_alert))
                    .setCancelable(false)
                    .setPositiveButton(
                            getResources().getString(R.string.yes_alert),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int id) {
                                    try {
                                        ServiceConnector.getLoggerService()
                                                .stopTrack();
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
                                    /*
                                     * DO SOMETHING
                                     */
                                }
                            })
                    .setNegativeButton(
                            getResources().getString(R.string.no_alert),
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    dialog.cancel();
                                }
                            });
            builder.show();
            return true;
        case R.id.stopTrack_opt:
            builder.setMessage(getResources().getString(R.string.exit_alert))
                    .setCancelable(false)
                    .setPositiveButton(
                            getResources().getString(R.string.yes_alert),
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
                            getResources().getString(R.string.no_alert),
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

        OverlayItem current_pos = null;
        int oldWayId = -1;

        public GPSReceiver() { /* nothing to do here */
        }

        @Override
        public void onReceive(Context ctx, Intent intend) {

            // Receive current location and do something with it
            if (intend.getAction().equals(WaypointLogService.UPDTAE_GPS_POS)) {
                final double lng = intend.getExtras().getDouble("long");
                final double lat = intend.getExtras().getDouble("lat");

                currentGeoPoint = new GeoPoint(lat, lng);

                Log.d(LOG_TAG,
                        "Location update received "
                                + currentGeoPoint.toString());

                if (current_pos != null)
                    pointsOverlay.removeOverlay(-1);
                current_pos = getOverlayItem(currentGeoPoint,
                        R.drawable.marker_green);
                pointsOverlay.addOverlay(current_pos, -1);

                if (centerMap)
                    centerOnCurrentPosition();

                // Receive an update of a way and update the overlay accordingly
            } else if (intend.getAction().equals(
                    WaypointLogService.UPDTAE_OBJECT)) {
                if (currentTrack() == null) {
                    Log.e(LOG_TAG,
                            "Received UPDATE_OBJECT with no current track present.");
                    return;
                }

                int wayId = intend.getExtras().getInt("way_id");
                int pointId = intend.getExtras().getInt("point_id");

                if (wayId > 0) {
                    if (wayId != oldWayId) {
                        reDrawWay(oldWayId);
                        oldWayId = wayId;
                    }
                    Log.d(LOG_TAG, "Received way update, id=" + wayId);
                    DataPointsList way = currentTrack()
                            .getPointsListById(wayId);
                    if (way == null)
                        Log.e(LOG_TAG, "Way with ID " + wayId
                                + " does not exist.");
                    else {
                        if (way.getOverlayRoute() != null) // we can not change
                            // the route, thus
                            // create a new one
                            routesOverlay.removeOverlay(way.getOverlayRoute());
                        way.setOverlayRoute(new OverlayRoute(way
                                .toGeoPointArray(), colors.get(0).first, colors
                                .get(0).first));
                        routesOverlay.addRoute(way.getOverlayRoute());
                        final DataNode lastPoint = way.getNodes().get(
                                way.getNodes().size() - 1);
                        Log.d(LOG_TAG, "new node in current way: " + lastPoint);
                        if (showGnubbel) {
                            lastPoint.setOverlayItem(getOverlayItem(
                                    lastPoint.toGeoPoint(),
                                    R.drawable.marker_blue));
                            pointsOverlay.addOverlay(lastPoint);
                        }
                    }
                } else if (pointId > 0) { // received an updated POI -
                    // when does this actually happen?
                    Log.d(LOG_TAG, "Received node update, id=" + pointId);
                    DataNode point = currentTrack().getNodeById(pointId);
                    if (point == null)
                        Log.e(LOG_TAG, "Node with ID " + pointId
                                + " does not exist.");
                    else {
                        if (point.getOverlayItem() == null)
                            point.setOverlayItem(new OverlayItem(point
                                    .toGeoPoint(), point.getId() + "", ""));
                        pointsOverlay.addOverlay(point);
                    }
                }

            } else if (intend.getAction().equals(
                    DataNodeArrayItemizedOverlay.UPDTAE_WAY)) {
                int wayId = intend.getExtras().getInt("way_id");
                Log.d(LOG_TAG, "Nodes of way " + wayId + " have changed.");

                reDrawWay(wayId);
            } else if (intend.getAction().equals(
                    DataNodeArrayItemizedOverlay.MOVE_POINT)) {
                final int nodeId = intend.getExtras().getInt("point_id");
                editNode = currentTrack().getNodeById(nodeId);
                Log.d(LOG_TAG, "Enter edit mode for Point " + nodeId);
            }
        }
    }
}
