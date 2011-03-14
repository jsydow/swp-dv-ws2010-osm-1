package util;

import gui.AddPointActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mapsforge.android.maps.ArrayItemizedOverlay;
import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.ItemizedOverlay;
import org.mapsforge.android.maps.OverlayItem;

import Trace.Book.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Pair;
import android.widget.Toast;
import core.data.DataNode;
import core.data.DataPointsList;
import core.data.DataStorage;
import core.data.DataTrack;

/**
 * ArrayItemizedOverlay is a thread-safe implementation of the
 * {@link ItemizedOverlay} class using an {@link ArrayList} as internal data
 * structure. A default marker for all {@link OverlayItem OverlayItems} without
 * an individual marker can be defined via the constructor.
 * 
 * This Class is derived from the {@link ArrayItemizedOverlay} of MapsForge
 */
public class DataNodeArrayItemizedOverlay extends ItemizedOverlay<OverlayItem> {
    private static final int ARRAY_LIST_INITIAL_CAPACITY = 8;
    private static final String THREAD_NAME = "DataNodeArrayItemizedOverlay";
    private final ArrayList<Pair<OverlayItem, Integer>> overlayItems;

    private static final String BASETAG = "de.fu-berlin.inf.tracebook";

    /**
     * Tag of the Intent that signals an update of a way.
     */
    public static final String UPDATE_WAY = BASETAG + ".UPDATE_WAY";

    /**
     * Tag of the Intent that signals the start of editing a points location.
     */
    public static final String MOVE_POINT = BASETAG + ".MODE_POINT";

    /**
     * Context of the MapActivity.
     */
    final Activity context;

    /**
     * Reference to the current DataTrack.
     */
    DataTrack currentTrack;

    /**
     * Intent to send updated ways to the MapsForgeView.
     */
    final Intent way_intent = new Intent(UPDATE_WAY);

    /**
     * Intent to move a point.
     */
    final Intent move_intent = new Intent(MOVE_POINT);

    private DataPointsListArrayRouteOverlay routesOverlay;

    /**
     * Constructs a new ArrayItemizedOverlay.
     * 
     * @param context
     *            the reference to the application context.
     * @param routesOverlay
     */
    public DataNodeArrayItemizedOverlay(Activity context,
            DataPointsListArrayRouteOverlay routesOverlay) {
        super(boundCenterBottom(context.getResources().getDrawable(
                R.drawable.marker_red)));
        this.context = context;
        this.routesOverlay = routesOverlay;
        this.overlayItems = new ArrayList<Pair<OverlayItem, Integer>>(
                ARRAY_LIST_INITIAL_CAPACITY);
        this.currentTrack = DataStorage.getInstance().getCurrentTrack();
    }

    /**
     * Adds the given item to the overlay.
     * 
     * @param overlayItem
     *            the item that should be added to the overlay.
     * @param nodeId
     *            id of the {@link DataNode} object associated with this Item
     */
    public void addOverlay(OverlayItem overlayItem, int nodeId) {
        synchronized (this.overlayItems) {
            this.overlayItems.add(new Pair<OverlayItem, Integer>(overlayItem,
                    Integer.valueOf(nodeId)));
        }
        populate();
    }

    /**
     * Adds the DataNode to the overlay, assuming a OverlayItem was already
     * specified.
     * 
     * @param node
     *            The DataNode - must contain a OverlayItem as well as a unique
     *            ID
     */
    public void addOverlay(DataNode node) {
        addOverlay(node.getOverlayItem(), node.getId());
    }

    /**
     * Updates the OverlayItem marking the current position.
     * 
     * @param currentPos
     */
    public void setCurrentPosition(GeoPoint currentPos) {
        updateItem(currentPos, -1, R.drawable.marker_green);
    }

    /**
     * Update the Item with the given id (change it's position or icon).
     * 
     * @param currentPos
     * @param id
     * @param icon
     */
    public void updateItem(GeoPoint currentPos, int id, int icon) {
        for (int i = 0; i < overlayItems.size(); ++i)
            if (overlayItems.get(i).second.intValue() == id) {
                OverlayItem oi = Helper.getOverlayItem(currentPos,
                        overlayItems.get(i).first.getMarker());
                overlayItems.set(i, new Pair<OverlayItem, Integer>(oi,
                        overlayItems.get(i).second));

                if (id > 0) { // redraw way when point was moved
                    DataNode node = Helper.currentTrack().getNodeById(id);
                    if (node != null) {
                        node.setLocation(currentPos);
                        node.setOverlayItem(oi);

                        if (node.getDataPointsList() != null)
                            routesOverlay.reDrawWay(node.getDataPointsList()
                                    .getId());
                    }
                }
                populate();
                return;
            }
        // no OverlayItem was yet added
        addOverlay(Helper.getOverlayItem(currentPos, icon, context), id);
        populate();
    }

    /**
     * Add a list of POIs to the overlay.
     * 
     * @param nodes
     */
    public void addPoints(List<DataNode> nodes) {
        for (DataNode n : nodes) {
            if (n.getOverlayItem() == null)
                n.setOverlayItem(new OverlayItem(n.toGeoPoint(), "", ""));
            addOverlay(n);
        }
    }

    /**
     * Removes all items from the overlay.
     */
    public void clear() {
        synchronized (this.overlayItems) {
            this.overlayItems.clear();
        }
        populate();
    }

    @Override
    public String getThreadName() {
        return THREAD_NAME;
    }

    /**
     * Removes the given item from the overlay.
     * 
     * @param id
     *            the id of the {@link DataNode} object
     */
    public void removeOverlay(int id) {
        remove(id);
        populate();
    }

    /**
     * Removes an {@link OverlayItem} by the ID of the associated
     * {@link DataNode}.
     * 
     * @param id
     *            ID of the {@link DataNode} associated with the
     *            {@link OverlayItem}, -1 for the marker of the current position
     *            which is not associated with any DataNode object
     */
    void remove(int id) {
        synchronized (this.overlayItems) {
            Iterator<Pair<OverlayItem, Integer>> iter = overlayItems.iterator();
            while (iter.hasNext())
                if (iter.next().second.intValue() == id) {
                    iter.remove();
                    break;
                }
        }
    }

    @Override
    public int size() {
        synchronized (this.overlayItems) {
            return this.overlayItems.size();
        }
    }

    @Override
    protected OverlayItem createItem(int i) {
        synchronized (this.overlayItems) {
            return this.overlayItems.get(i).first;
        }
    }

    private final CharSequence[] items = { "Tag this", "Move this",
            "Delete this" };

    @Override
    protected boolean onTap(int index) {
        final int nodeId;
        final GeoPoint point;
        synchronized (this.overlayItems) {
            nodeId = overlayItems.get(index).second.intValue();
            point = overlayItems.get(index).first.getPoint();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setTitle("id: " + nodeId);

        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                case 0: // Tag this
                    final Intent intent = new Intent(context,
                            AddPointActivity.class);
                    if (nodeId < 0)
                        intent.putExtra("DataNodeId",
                                currentTrack.newNode(point).getId());
                    else
                        intent.putExtra("DataNodeId", nodeId);

                    context.startActivity(intent);
                    break;
                case 1: // move this
                    if (nodeId < 0) {
                        Toast.makeText(context,
                                "You can't move around that way!",
                                Toast.LENGTH_SHORT).show();
                        break;
                    }

                    move_intent.putExtra("point_id", nodeId);
                    context.sendBroadcast(move_intent);
                    break;
                case 2: // delete this
                    if (nodeId < 0) {
                        Toast.makeText(context,
                                "can not delete current location",
                                Toast.LENGTH_SHORT).show();
                        break;
                    }

                    final DataNode node = currentTrack.getNodeById(nodeId);
                    DataPointsList way = null;
                    if (node != null)
                        way = node.getDataPointsList();
                    if (currentTrack.deleteNode(nodeId)) {
                        remove(nodeId);
                        if (way != null) { // we have to redraw the way
                            way_intent.putExtra("way_id", way.getId());
                            context.sendBroadcast(way_intent);
                        }
                    } else
                        Toast.makeText(context,
                                "Can not delete Node id=" + nodeId,
                                Toast.LENGTH_SHORT).show();

                    break;
                default:
                    break;
                }
            }
        });

        builder.show();
        return true;
    }

    /**
     * we need to set the RoutesOverlay as the class will be created after this
     * one.
     * 
     * @param routesOverlay
     */
    public void setRoutesOverlay(DataPointsListArrayRouteOverlay routesOverlay) {
        this.routesOverlay = routesOverlay;
    }
}
