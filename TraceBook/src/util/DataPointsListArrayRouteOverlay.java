package util;

import gui.activity.MapsForgeActivity;

import java.util.ArrayList;
import java.util.List;

import org.mapsforge.android.maps.ArrayRouteOverlay;
import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.OverlayRoute;

import Trace.Book.R;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import core.data.DataNode;
import core.data.DataPointsList;

/**
 * Class wrapping the ·{@link ArrayRouteOverlay} to get some methods out of
 * {@link MapsForgeActivity}.
 * 
 * 
 */
public class DataPointsListArrayRouteOverlay extends ArrayRouteOverlay {
    private int colorID = 0;
    /**
     * List of possible colors for ways and areas the first color in the list is
     * always used for the current way.
     */
    private List<Pair<Paint, Paint>> colors;

    /**
     * Show a knob for every waypoint.
     */
    boolean showGnubbel = false;

    private Activity context;

    private DataNodeArrayItemizedOverlay pointsOverlay;

    /**
     * sets overlays and generates color array.
     * 
     * @param context
     *            a reference to the MapActivity
     * @param pointsOverlay
     *            a reference to the pointsOverlay that also is bound to the
     *            MapActivity
     * 
     */
    public DataPointsListArrayRouteOverlay(Activity context,
            DataNodeArrayItemizedOverlay pointsOverlay) {
        super(null, null);

        this.pointsOverlay = pointsOverlay;
        this.context = context;

        // create paint list
        colors = new ArrayList<Pair<Paint, Paint>>();
        colors.add(getPaintPair(Color.rgb(0, 255, 0)));
        colors.add(getPaintPair(Color.rgb(0, 0, 230)));
        colors.add(getPaintPair(Color.rgb(0, 0, 200)));
        colors.add(getPaintPair(Color.rgb(0, 0, 170)));
    }

    /**
     * Gets a color from the rotating color array.
     * 
     * @return a {@link Pair} of {@link Paint} where the first element is the
     *         FillPaint and the second one the OutlinePaint
     */
    Pair<Paint, Paint> getColor() {
        // the first color is used for the current track, so rotate over the
        // remaining array fields.
        colorID = colorID % (colors.size() - 1) + 1;
        return colors.get(colorID);
    }

    /**
     * generates a pair of paint objects with the same color, but different
     * levels of transparency.
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
     * Add a list of ways to the Overlay.
     * 
     * @param ways
     *            a list of the {@link DataPointsList}s, adding all of them to
     *            the overlay
     */
    public void addWays(List<DataPointsList> ways) {
        for (DataPointsList l : ways) {
            if (l.getNodes().size() == 0) // skip empty ways
                continue;
            if (l.getOverlayRoute() == null) {
                Pair<Paint, Paint> col = getColor();
                l.setOverlayRoute(new OverlayRoute(l.toGeoPointArray(),
                        col.first, col.second));
            }
            addRoute(l.getOverlayRoute());

            if (showGnubbel)
                addGnubbel(l);
        }
    }

    /**
     * Redraw the given way.
     * 
     * @param way
     *            way to be redrawn
     * @param editing
     *            is the way the currently edited way?
     * @param additional
     *            additional Point to be added to the way, may be null
     */
    public void reDrawWay(DataPointsList way, boolean editing,
            GeoPoint additional) {
        Log.d("DPÖARO", "reDrawWay called");
        if (way == null)
            return;

        removeOverlay(way.getOverlayRoute());
        final Pair<Paint, Paint> color = editing ? colors.get(0) : getColor();
        way.setOverlayRoute(new OverlayRoute(way.toGeoPointArray(additional),
                color.first, color.second));
        addRoute(way.getOverlayRoute());

        if (showGnubbel)
            for (DataNode n : way.getNodes()) {
                putGnubbel(n);
                pointsOverlay.updateItem(n);
            }
    }

    /**
     * Update the color of a way once its not the current way any more.
     * 
     * @param id
     *            id of the {@link DataPointsList} to be redrawn
     */
    public void reDrawWay(int id) {
        if (id <= 0)
            return;
        DataPointsList currentWay = Helper.currentTrack().getCurrentWay();
        reDrawWay(Helper.currentTrack().getPointsListById(id),
                currentWay != null && currentWay.getId() == id, null);
    }

    private void addGnubbel(DataPointsList way) {
        for (DataNode n : way.getNodes()) {
            putGnubbel(n);
            pointsOverlay.addOverlay(n);
        }
    }

    private void removeGnubbel(DataPointsList way) {
        for (DataNode n : way.getNodes())
            pointsOverlay.removeOverlay(n.getId());
    }

    /**
     * enable/disable the drawing of waypoint markers.
     */
    public void toggleGnubbel() {
        showGnubbel = !showGnubbel;

        for (DataPointsList dpl : Helper.getWays())
            if (showGnubbel)
                addGnubbel(dpl);
            else
                removeGnubbel(dpl);
    }

    /**
     * Creates a new OverlayItem for n if it has none yet.
     * 
     * @param n
     *            DataNode that should get a gnubbel
     */
    private void putGnubbel(DataNode n) {
        if (n.getOverlayItem() == null)
            n.setOverlayItem(Helper.getOverlayItem(n.toGeoPoint(),
                    R.drawable.dot_blue, context, true));
    }

}
