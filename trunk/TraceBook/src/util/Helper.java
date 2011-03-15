package util;

import java.util.List;

import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.ItemizedOverlay;
import org.mapsforge.android.maps.OverlayItem;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;
import core.data.DataNode;
import core.data.DataPointsList;
import core.data.DataStorage;
import core.data.DataTrack;

/**
 * general helper class to feature some useful functions.
 * 
 * @author benpicco
 * 
 */
public final class Helper {
    private Helper() { // do nothing - why checkstyle, why?!
    }

    /**
     * Creates a new OverlayItem.
     * 
     * @param pos
     *            position of the marker
     * @param marker
     *            id of the Graphics object to use
     * @param act
     *            context of the application
     * @return the new OverlayItem
     */
    public static OverlayItem getOverlayItem(GeoPoint pos, int marker,
            Activity act) {
        final OverlayItem oi = new OverlayItem(pos, null, null);
        Drawable icon = act.getResources().getDrawable(marker);
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
    public static OverlayItem getOverlayItem(GeoPoint pos, Drawable marker) {
        final OverlayItem oi = new OverlayItem(pos, null, null);
        oi.setMarker(marker);
        return oi;
    }

    /**
     * Gets the current DataTrack for convenience.
     * 
     * @return the current DataTrack object
     */
    public static DataTrack currentTrack() {
        return DataStorage.getInstance().getCurrentTrack();
    }

    /**
     * gets the current way in the current {@link DataTrack}.
     * 
     * @return the current {@link DataPointsList} ways
     */
    public static List<DataPointsList> getWays() {
        if (DataStorage.getInstance().getCurrentTrack() != null) {
            return DataStorage.getInstance().getCurrentTrack().getWays();
        }
        return null;
    }

    /**
     * gets the list of Nodes in current {@link DataTrack}.
     * 
     * @return the current list of {@link DataNode}s
     */
    public static List<DataNode> getNodes() {
        if (DataStorage.getInstance().getCurrentTrack() != null) {
            return DataStorage.getInstance().getCurrentTrack().getNodes();
        }
        return null;
    }

    /**
     * Do something to handle a fatal exception in user interaction namely show
     * a toast and log the error.
     * 
     * @param context
     *            Context of the Activity
     * @param ex
     *            Exception that occurred
     * @param LOG_TAG
     *            tag of the class the exception occurred in
     */
    public static void handleNastyException(Context context, Exception ex,
            String LOG_TAG) {
        Toast.makeText(
                context,
                "An error occured. Please restart the application and try again.",
                Toast.LENGTH_LONG).show();
        Log.e(LOG_TAG, ex.getMessage());
    }
}
