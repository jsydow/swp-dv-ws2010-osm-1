package util;

import java.util.List;

import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.ItemizedOverlay;
import org.mapsforge.android.maps.OverlayItem;

import android.app.Activity;
import android.graphics.drawable.Drawable;
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
        return DataStorage.getInstance().getCurrentTrack().getWays();
    }

    /**
     * gets the list of Nodes in current {@link DataTrack}.
     * 
     * @return the current list of {@link DataNode}s
     */
    public static List<DataNode> getNodes() {
        return DataStorage.getInstance().getCurrentTrack().getNodes();
    }
}
