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

    private BroadcastReceiver gpsReceiver;

    /**
     * List of possible colors for ways and areas the first color in the list is
     * always used for the current way.
     */
    List<Pair<Paint, Paint>> colors;

    /**
     * Show a knob for every waypoint.
     */
    boolean showGnubbel = true;

    private int colorID = 0;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LOG_TAG, "Creating MapActivity");

        File file = new File("/sdcard/", "default.map");
        if (!file.exists()) {
            Toast.makeText(getApplicationContext(),
                    "Unable to open " + file.getAbsolutePath() + "!",
                    Toast.LENGTH_LONG).show();
            return;
        }

        MapView mapView = new MapView(this);
        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(true);
        mapView.setMapFile(file.getAbsolutePath());

        setContentView(mapView);

        mapController = mapView.getController();

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

        mapView.getOverlays().add(routesOverlay);
        mapView.getOverlays().add(pointsOverlay);

        gpsReceiver = new GPSReceiver();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO handle moving of OverlayItems
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
            /*
             * DO SOMETHING
             */
            return true;
        case R.id.centerAtOwnPosition_opt:
            ((GPSReceiver) gpsReceiver).centerOnCurrentPosition();
            /*
             * DO SOMETHING
             */
            return true;
        case R.id.export_opt:
            /*
             * Do SOMETHING
             */
            return true;
        case R.id.pause_opt:

            builder.setMessage(getResources().getString(R.string.pause_alert))
                    .setCancelable(false).setPositiveButton(
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
                            }).setNegativeButton(
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
                    .setCancelable(false).setPositiveButton(
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
                            }).setNegativeButton(
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

    private void addPoints() {
        for (DataNode n : currentTrack().getNodes()) {
            if (n.getOverlayItem() == null)
                n.setOverlayItem(new OverlayItem(n.toGeoPoint(), "", ""));
            pointsOverlay.addOverlay(n.getOverlayItem(), n.getId());
        }
    }

    private void addWays() {
        for (DataPointsList l : currentTrack().getWays()) {
            if (l.getOverlayRoute() == null) {
                Pair<Paint, Paint> col = getColor();
                l.setOverlayRoute(new OverlayRoute(l.toGeoPointArray(),
                        col.first, col.second));
            }
            routesOverlay.addRoute(l.getOverlayRoute());

            if (showGnubbel) {
                for (DataNode n : l.getNodes())
                    pointsOverlay.addOverlay(getOverlayItem(n.toGeoPoint(),
                            R.drawable.marker_blue), n.getId());

            }
        }
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
        final OverlayItem oi = new OverlayItem(pos, "", "");
        Drawable icon = getResources().getDrawable(marker);
        oi.setMarker(ItemizedOverlay.boundCenterBottom(icon));

        return oi;
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

        OverlayItem current_pos = null;
        GeoPoint currentGeoPoint = null;

        int oldWayId = -1;

        void centerOnCurrentPosition() {
            if (currentGeoPoint != null) {
                mapController.setCenter(currentGeoPoint);
                centerMap = false;
            } else
                centerMap = true;
        }

        public GPSReceiver() { /* nothing to do here */
        }

        @Override
        public void onReceive(Context ctx, Intent intend) {

            // Receive current location and do something with it
            if (intend.getAction().equals(WaypointLogService.UPDTAE_GPS_POS)) {
                final double lng = intend.getExtras().getDouble("long");
                final double lat = intend.getExtras().getDouble("lat");

                currentGeoPoint = new GeoPoint(lat, lng);

                Log.d(LOG_TAG, "Location update received "
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
                    Log
                            .e(LOG_TAG,
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
                        if (showGnubbel)
                            pointsOverlay.addOverlay(getOverlayItem(lastPoint
                                    .toGeoPoint(), R.drawable.marker_blue),
                                    lastPoint.getId());
                    }
                } else if (pointId > 0) { // received an updated POI
                    Log.d(LOG_TAG, "Received node update, id=" + pointId);
                    DataNode point = currentTrack().getNodeById(pointId);
                    if (point == null)
                        Log.e(LOG_TAG, "Node with ID " + pointId
                                + " does not exist.");
                    else {
                        Log.d(LOG_TAG, point.toString());
                        pointsOverlay
                                .addOverlay(new OverlayItem(point.toGeoPoint(),
                                        point.getId() + "", ""), pointId);
                    }
                }

            }
        }

        /**
         * Update the color of a way once its not the current way any more.
         * 
         * @param id
         */
        private void reDrawWay(int id) {
            if (id <= 0)
                return;
            DataPointsList way = currentTrack().getPointsListById(id);
            if (way == null)
                return;
            routesOverlay.removeOverlay(way.getOverlayRoute());
            Pair<Paint, Paint> color = getColor();
            way.setOverlayRoute(new OverlayRoute(way.toGeoPointArray(),
                    color.first, color.second));
            routesOverlay.addRoute(way.getOverlayRoute());
        }
    }

    /**
     * Gets the current DataTrack for convenience.
     * 
     * @return the current DataTrack object
     */
    static DataTrack currentTrack() {
        return DataStorage.getInstance().getCurrentTrack();
    }
}
