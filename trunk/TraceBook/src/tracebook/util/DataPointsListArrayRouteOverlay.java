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

package tracebook.util;

import java.util.ArrayList;
import java.util.List;

import org.mapsforge.android.maps.ArrayItemizedOverlay;
import org.mapsforge.android.maps.ArrayWayOverlay;
import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.OverlayWay;

import tracebook.core.data.DataNode;
import tracebook.core.data.DataPointsList;
import tracebook.gui.activity.MapsForgeActivity;
import Trace.Book.R;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Class wrapping the ·{@link ArrayWayOverlay} to get some methods out of
 * {@link MapsForgeActivity}.
 * 
 * 
 */
public class DataPointsListArrayRouteOverlay extends ArrayWayOverlay {
    /**
     * Generates a pair of paint objects with the same color, but different
     * levels of transparency.
     */
    private static Pair<Paint, Paint> getPaintPair(int color, boolean area) {
        Paint paintOutline = new Paint();
        paintOutline.setAntiAlias(true);
        paintOutline.setStyle(Paint.Style.STROKE);
        paintOutline.setStrokeWidth(4);
        paintOutline.setStrokeCap(Paint.Cap.BUTT);
        paintOutline.setStrokeJoin(Paint.Join.ROUND);
        paintOutline.setColor(color);
        paintOutline.setAlpha(96);

        Paint paintFill = new Paint(paintOutline);
        if (area)
            paintFill.setStyle(Paint.Style.FILL);
        paintFill.setAlpha(160);

        return new Pair<Paint, Paint>(paintFill, paintOutline);
    }

    private List<Pair<Paint, Paint>> areaColors;
    private int colorID = 0;

    private Activity context;

    private ArrayItemizedOverlay pointsOverlay;

    /**
     * List of possible colors for ways and areas the first color in the list is
     * always used for the current way.
     */
    private List<Pair<Paint, Paint>> wayColors;

    /**
     * Show a knob for every way point.
     */
    boolean showWaypoints = false;

    /**
     * Sets overlays and generates color array.
     * 
     * @param context
     *            a reference to the MapActivity
     * @param pointsOverlay
     *            a reference to the pointsOverlay that also is bound to the
     *            MapActivity
     * 
     */
    public DataPointsListArrayRouteOverlay(Activity context,
            ArrayItemizedOverlay pointsOverlay) {
        super(null, null);

        this.pointsOverlay = pointsOverlay;
        this.context = context;

        // create paint list
        wayColors = new ArrayList<Pair<Paint, Paint>>();
        wayColors.add(getPaintPair(Color.rgb(0, 255, 0), false));
        wayColors.add(getPaintPair(Color.rgb(0, 0, 230), false));
        wayColors.add(getPaintPair(Color.rgb(0, 0, 200), false));
        wayColors.add(getPaintPair(Color.rgb(0, 0, 170), false));

        areaColors = new ArrayList<Pair<Paint, Paint>>();
        areaColors.add(getPaintPair(Color.rgb(0, 255, 0), true));
        areaColors.add(getPaintPair(Color.rgb(230, 0, 0), true));
        areaColors.add(getPaintPair(Color.rgb(200, 0, 0), true));
        areaColors.add(getPaintPair(Color.rgb(170, 0, 0), true));
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
                Pair<Paint, Paint> col = getColor(false, l.isArea());
                l.setOverlayRoute(new OverlayWay(l.toGeoPointArray(),
                        col.first, col.second));
            }
            this.addWay(l.getOverlayRoute());

            if (showWaypoints)
                addWaypoints(l);
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
        if (way == null)
            return;

        this.removeWay(way.getOverlayRoute());
        final Pair<Paint, Paint> color = getColor(editing, way.isArea());
        way.setOverlayRoute(new OverlayWay(way.toGeoPointArray(additional),
                color.first, color.second));
        this.addWay(way.getOverlayRoute());

        if (showWaypoints)
            for (DataNode n : way.getNodes())
                putWaypoint(n);

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

    /**
     * Enable/disable the drawing of way point markers.
     */
    public void toggleWaypoints() {
        showWaypoints = !showWaypoints;

        for (DataPointsList dpl : Helper.getWays())
            if (showWaypoints)
                addWaypoints(dpl);
            else
                removeWaypoints(dpl);
    }

    private void addWaypoints(DataPointsList way) {
        for (DataNode n : way.getNodes())
            putWaypoint(n);
    }

    /**
     * Creates a new OverlayItem for n if it has none yet.
     * 
     * @param n
     *            DataNode that should get a marker
     */
    private void putWaypoint(DataNode n) {
        if (n.getOverlayItem() == null)
            n.setOverlayItem(Helper.getOverlayItem(n.toGeoPoint(),
                    R.drawable.dot_blue, context, true));
        pointsOverlay.addItem(n.getOverlayItem());
    }

    private void removeWaypoints(DataPointsList way) {
        for (DataNode n : way.getNodes())
            pointsOverlay.removeItem(n.getOverlayItem());
    }

    /**
     * Gets a color from the rotating color array.
     * 
     * @param editing
     *            true if the track is currently edited and the color pair for
     *            the current way should be used
     * @param area
     *            true if the color should be used for an area
     * 
     * @return a {@link Pair} of {@link Paint} where the first element is the
     *         FillPaint and the second one the OutlinePaint
     */
    Pair<Paint, Paint> getColor(boolean editing, boolean area) {
        List<Pair<Paint, Paint>> colors = area ? areaColors : wayColors;
        // the first color is used for the current track, so rotate over the
        // remaining array fields.
        colorID = editing ? 0 : colorID % (colors.size() - 1) + 1;
        return colors.get(colorID);
    }

}
