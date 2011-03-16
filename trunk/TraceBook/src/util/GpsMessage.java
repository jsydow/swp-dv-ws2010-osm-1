package util;

import gui.activity.MapsForgeActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import core.data.DataNode;
import core.data.DataStorage;

/**
 * To send messages to the {@link MapsForgeActivity} this class offers some
 * helpful methods which fill the intent according to the action that is
 * performed.
 */
public class GpsMessage {

    /**
     * Tag of the Intent send by this class.
     */
    public static final String TAG = "de.fu-berlin.inf.tracebook.UPDATE";

    /**
     * type of the Intent that signals a change of the current position.
     */
    public static final int UPDATE_GPS_POS = 0;

    /**
     * type of the Intent that signals an update to an object in
     * {@link DataStorage}.
     */
    public static final int UPDATE_OBJECT = 1;

    /**
     * type of the Intent that signals a way was closed.
     */
    public static final int END_WAY = 2;

    /**
     * Tag of the Intent that signals the start of editing a points location.
     */
    public static final int MOVE_POINT = 3;

    private Intent intent;

    private Context ctx;

    // private boolean one_shot = false;

    /**
     * Creates a new GPS Intent sender helper class.
     * 
     * @param ctx
     *            Context from which the Intents should be sent
     */
    public GpsMessage(Context ctx) {
        this.ctx = ctx;
        intent = new Intent(TAG);
    }

    /**
     * Send the current position to the MapsView.
     * 
     * @param loc
     *            current gps pposition
     */
    public void sendCurrentPosition(Location loc) {
        intent.putExtra("type", UPDATE_GPS_POS);
        intent.putExtra("long", loc.getLongitude());
        intent.putExtra("lat", loc.getLatitude());
        ctx.sendBroadcast(intent);
    }

    /**
     * Signal an update of the way, so that it can be redrawn.
     * 
     * @param id
     *            id of the way that was changed
     */
    public void sendWayUpdate(int id) {
        intent.putExtra("type", UPDATE_OBJECT);
        intent.putExtra("point_id", -1);
        intent.putExtra("way_id", id);
        ctx.sendBroadcast(intent);
    }

    /**
     * Signal an update or creation of a POI, so that it can be displayed.
     * 
     * @param id
     *            the id of the POI that was changed or created.
     */
    public void sendPOIUpdate(int id) {
        intent.putExtra("type", UPDATE_OBJECT);
        intent.putExtra("point_id", id);
        intent.putExtra("way_id", -1);
        ctx.sendBroadcast(intent);
    }

    /**
     * Signal the end of a way, so that e.g. it will be redrawn in a different
     * color.
     * 
     * @param id
     *            id of the way that was just finished
     */
    public void sendEndWay(int id) {
        intent.putExtra("type", END_WAY);
        intent.putExtra("way_id", id);
        ctx.sendBroadcast(intent);
    }

    /**
     * Start moving of a {@link DataNode}.
     * 
     * @param id
     *            the ID of the DataNode that will enter edit mode
     */
    public void sendMovePoint(int id) {
        intent.putExtra("type", MOVE_POINT);
        intent.putExtra("point_id", id);
        ctx.sendBroadcast(intent);
    }
}
