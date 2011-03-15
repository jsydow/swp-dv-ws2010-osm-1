package core.logger;

import util.Helper;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import core.data.DataNode;
import core.data.DataPointsList;
import core.data.DataStorage;

/**
 * This background service logs GPS data and stores it in the
 * {@link DataStorage} object.
 * 
 * @author benpicco
 * 
 */
public class WaypointLogService extends Service implements LocationListener {
    private static final String LOG_TAG = "LOGSERVICE";
    private static final String BASETAG = "de.fu-berlin.inf.tracebook";

    /**
     * Tag of the Intent that signals a change of the current position.
     */
    public static final String UPDATE_GPS_POS = BASETAG + ".UPDATE_GPS_POS";

    /**
     * Tag of the Intent that signals an update to an object in
     * {@link DataStorage}.
     */
    public static final String UPDATE_OBJECT = BASETAG + ".UPDATE_OBJECT";

    /**
     * Tag of the Intent that signals a way was closed.
     */
    public static final String END_WAY = BASETAG + ".END_WAY";

    /**
     * Reference to the {@link DataStorage} singleton.
     */
    DataStorage storage = DataStorage.getInstance();

    /**
     * Current node, null if no node with missing GPS location is present,
     * otherwise it contains a reference to the {@link DataNode} waiting for a
     * GPS fix.
     */
    DataNode current_node = null;

    /**
     * Parameters for GPS update intervals.
     */
    protected int deltaDistance = 0;

    /**
     * 
     */
    protected int deltaTime = 0;

    private final Intent gps_intent = new Intent(UPDATE_GPS_POS);
    private final Intent update_intent = new Intent(UPDATE_OBJECT);
    /**
     * intent used to signal the end of a way.
     */
    final Intent end_way = new Intent(END_WAY);

    private boolean gps_on = false;

    /**
     * One shot mode - no continuous tracking, points are only added to the way
     * on request.
     */
    boolean one_shot = false;

    private LocationListener ll = this;

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(LOG_TAG, "onStart");
    }

    @Override
    public IBinder onBind(Intent intent) {
        startGPS();
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
        stopGPS();
    }

    /**
     * Enables GPS updates from the {@link LocationManager}.
     */
    void startGPS() {
        if (!gps_on)
            ((LocationManager) getSystemService(Context.LOCATION_SERVICE))
                    .requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            deltaTime, deltaDistance, ll);
        gps_on = true;
    }

    /**
     * Disables GPS updates from the {@link LocationManager}.
     */
    void stopGPS() {
        if (gps_on)
            ((LocationManager) getSystemService(Context.LOCATION_SERVICE))
                    .removeUpdates(ll);
        gps_on = false;
    }

    /**
     * first tries to stop and than to start receiving GPS updates. This
     * effectively reloads the settings for the {@link LocationManager}
     */
    void restartGPS() {
        stopGPS();
        startGPS();
    }

    /**
     * Convenience function to get the current way out of the
     * {@link DataStorage} object.
     * 
     * @return the current {@link DataPointsList} way
     */
    DataPointsList currentWay() {
        if (storage == null || storage.getCurrentTrack() == null)
            return null;
        return storage.getCurrentTrack().getCurrentWay();
    }

    /**
     * The IAdderService is defined through IDL.
     */
    private final ILoggerService.Stub binder = new ILoggerService.Stub() {

        public void addTrackWithGpsParams(int dTime, int dDistance) {
            deltaTime = dTime;
            deltaDistance = dDistance;
            addTrack();
        }

        public void addTrack() {
            restartGPS();

            storage.setCurrentTrack(storage.newTrack());
        }

        public int stopTrack() {
            stopGPS();

            if (storage.getCurrentTrack() != null) {
                storage.getCurrentTrack().serialise();
                storage.unloadAllTracks();
                return 1;
            }

            return -1;
        }

        public int createPOI(boolean onWay) {
            if (onWay && currentWay() != null)
                current_node = currentWay().newNode();
            else
                current_node = storage.getCurrentTrack().newNode();

            return current_node.getId();
        }

        public int beginWay(boolean doOneShot) {
            one_shot = doOneShot;

            if (currentWay() == null) // start a new way
                storage.getCurrentTrack().setCurrentWay(
                        storage.getCurrentTrack().newWay());

            if (one_shot) // in one_shot mode, add a new point
                current_node = currentWay().newNode();

            return currentWay().getId();
        }

        public synchronized int endWay() {
            DataPointsList tmp = currentWay();

            storage.getCurrentTrack().setCurrentWay(null);

            if (tmp != null)
                /* do not store empty ways */
                if (tmp.getNodes().size() == 0)
                    storage.getCurrentTrack().deleteWay(tmp.getId());
                else {
                    if (!one_shot)
                        Helper.smoothenPoints(tmp.getNodes(), 3, 3);
                    end_way.putExtra("way_id", tmp.getId());
                    sendBroadcast(end_way);
                    return tmp.getId();
                }
            return -1;
        }

        public synchronized int beginArea() {
            if (currentWay() == null) // start a new way
                storage.getCurrentTrack().setCurrentWay(
                        storage.getCurrentTrack().newWay());

            currentWay().setArea(true);
            return currentWay().getId();

        }

        public synchronized int endArea() { // do we really need this? TODO
                                            // merge with endWay()
            storage.getCurrentTrack().setCurrentWay(null);
            DataPointsList tmp = currentWay();

            if (tmp != null)
                return tmp.getId();
            return 0;
        }

        public boolean isWayLogging() {
            if (storage.getCurrentTrack() != null) {
                if (currentWay() != null) {
                    return !currentWay().isArea();
                }
            }
            return false;
        }

        public boolean isAreaLogging() {
            if (storage.getCurrentTrack() != null) {
                if (currentWay() != null) {
                    return currentWay().isArea();
                }
            }
            return false;
        }

    };

    /** GPS related Methods. **/

    public synchronized void onLocationChanged(Location loc) {
        Log.d(LOG_TAG, "GPS location changed");

        gps_intent.putExtra("long", loc.getLongitude());
        gps_intent.putExtra("lat", loc.getLatitude());
        sendBroadcast(gps_intent);

        if (current_node != null) { // one_shot or POI mode
            current_node.setLocation(loc); // update node with proper gps fix

            if (currentWay() == null) { // not one_shot mode
                // inform the MapActivity about the new POI
                update_intent.putExtra("point_id", current_node.getId());
                update_intent.putExtra("way_id", -1);
                sendBroadcast(update_intent);
            }

            current_node = null; // no node waiting for gps pos any more
        } else if (currentWay() != null && !one_shot) { // Continuous mode
            currentWay().newNode(loc); // poi in track was already added before
        }

        if (currentWay() != null) { // call for an update of the way
            update_intent.putExtra("way_id", currentWay().getId());
            update_intent.putExtra("point_id", -1);
            sendBroadcast(update_intent);
        }
    }

    public void onProviderDisabled(String arg0) {
        Log.d(LOG_TAG, "GPS Provider Disabled: " + arg0);
    }

    public void onProviderEnabled(String arg0) {
        Log.d(LOG_TAG, "GPS Provider Enabled: " + arg0);
    }

    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        Log.d(LOG_TAG, "GPS Status Changed: " + arg0);
    }
}
