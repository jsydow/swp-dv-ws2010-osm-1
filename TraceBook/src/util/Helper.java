package util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
 * 
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
     * @param center
     *            the center of the icon is at the given pos
     * @return the new OverlayItem
     */
    public static OverlayItem getOverlayItem(GeoPoint pos, int marker,
            Activity act, boolean center) {
        final OverlayItem oi = new OverlayItem(pos, null, null);
        Drawable icon = act.getResources().getDrawable(marker);
        if (center)
            oi.setMarker(ItemizedOverlay.boundCenter(icon));
        else
            oi.setMarker(ItemizedOverlay.boundCenterBottom(icon));

        return oi;
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
        return getOverlayItem(pos, marker, act, false);
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
     * @param logTag
     *            tag of the class the exception occurred in
     */
    public static void handleNastyException(Context context, Exception ex,
            String logTag) {
        Toast.makeText(
                context,
                "An error occured. Please restart the application and try again.",
                Toast.LENGTH_LONG).show();
        Log.e(logTag, ex.getMessage());
    }

    /**
     * smoothen the {@link DataNode}s by calculating the mean of 3 consecutive
     * points.
     * 
     * @param nodes
     *            The list of DataNodes representing the way
     * @param weight
     *            the factor by which the original point is higher weighted then
     *            it's bufferSize-1 successors
     * @param bufferSize
     *            the amount of points to use for the calculation
     */
    public static void smoothenPoints(List<DataNode> nodes, int weight,
            int bufferSize) {
        if (nodes == null)
            return;

        Queue<DataNode> ringbuffer = new LinkedList<DataNode>();

        for (DataNode n : nodes) {
            if (!n.isValid())
                continue;

            ringbuffer.add(n);

            if (ringbuffer.size() < bufferSize)
                continue;

            double latsum = 0;
            double lonsum = 0;
            boolean first = true;
            for (DataNode nr : ringbuffer)
                if (first) {
                    first = false;
                    latsum += nr.getLat() * weight;
                    lonsum += nr.getLon() * weight;
                } else {
                    latsum += nr.getLat();
                    lonsum += nr.getLon();
                }

            // the ringbuffer is an element smaller now
            ringbuffer.poll().setLocation(
                    new GeoPoint(
                            latsum / ((double) ringbuffer.size() + weight),
                            lonsum / ((double) ringbuffer.size() + weight)));

        }
    }

    /**
     * calculates the area spanned by the parallelogram defined by the points a,
     * b and c.
     * 
     * @param a
     *            first point
     * @param b
     *            second point
     * @param c
     *            third point
     * @return the area of the parallelogram (twice the area of the triangle)
     *         defined by those 3 points.
     */
    static double calculateArea(GeoPoint a, GeoPoint b, GeoPoint c) {
        return Math.abs((a.getLongitude() - c.getLongitude())
                * (b.getLatitude() - a.getLatitude())
                - (a.getLongitude() - b.getLongitude())
                * (c.getLatitude() - a.getLatitude()));
    }

    /**
     * removes all insignificant points from the way, a threshold is calculated
     * automatically by the average derivation of the points.
     * 
     * @param nodes
     *            List of {@link DataNode}s representing the way or area
     */
    public static void filterPoints(List<DataNode> nodes) {
        boolean calibrate = true;
        double threshold = 0;

        // we first iterate once to get the threshold, in the second run we
        // actually remove the points
        while (calibrate) {
            if (threshold != 0)
                calibrate = false;

            DataNode firstNode = null;
            DataNode pending = null;
            Iterator<DataNode> iter = nodes.iterator();

            while (iter.hasNext()) {
                DataNode n = iter.next();
                if (n == null || !n.isValid()) {
                    iter.remove();
                    continue;
                }

                if (firstNode == null) {
                    firstNode = n;
                    continue;
                }

                if (pending != null) {
                    if (calibrate) {
                        threshold += calculateArea(firstNode.toGeoPoint(),
                                pending.toGeoPoint(), n.toGeoPoint());
                    } else if (calculateArea(firstNode.toGeoPoint(),
                            pending.toGeoPoint(), n.toGeoPoint()) < threshold
                            && !n.hasAdditionalInfo())
                        iter.remove();
                    firstNode = pending;
                }

                pending = n;
            }
            threshold /= nodes.size();
            Log.d("Helper", "Average: " + threshold);
        }
    }
}