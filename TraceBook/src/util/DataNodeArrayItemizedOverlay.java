package util;

import gui.activity.AddPointActivity;

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
import android.os.RemoteException;
import android.widget.Toast;
import core.data.DataNode;
import core.data.DataPointsList;
import core.data.DataStorage;
import core.data.DataTrack;
import core.logger.ServiceConnector;

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
    private static final String LOG_TAG = "DNAIO";
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
     *            a reference to the MapActivity.
     * @param routesOverlay
     *            reference to the routesOverlay associated with the MapActivity
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

        contextMenueListener = new DefaultListener();
        contextMenueCurrentPosListener = new CurrentPosListener();
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
     *            current position, marker will be set there
     */
    public void setCurrentPosition(GeoPoint currentPos) {
        updateItem(currentPos, -1, R.drawable.marker_green);
    }

    /**
     * Update the Item with the given id (change it's position or icon).
     * 
     * @param pos
     *            new position
     * @param id
     *            id of the {@link DataNode}
     * @param icon
     *            (new) icon of the marker
     */
    public void updateItem(GeoPoint pos, int id, int icon) {
        for (int i = 0; i < overlayItems.size(); ++i)
            if (overlayItems.get(i).second.intValue() == id) {
                OverlayItem oi = Helper.getOverlayItem(pos,
                        overlayItems.get(i).first.getMarker());
                overlayItems.get(i).first = oi; // update the OverlayItem (not
                                                // necessarily has a DataNode)

                if (id > 0) { // redraw way when point was moved
                    DataNode node = Helper.currentTrack().getNodeById(id);
                    if (node != null) {
                        node.setLocation(pos); // update the actual DataNode
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
        addOverlay(Helper.getOverlayItem(pos, icon, context), id);
        populate();
    }

    /**
     * Add a list of POIs to the overlay.
     * 
     * @param nodes
     *            {@link DataNode}s to be added to overlay
     */
    public void addPoints(List<DataNode> nodes) {
        synchronized (this.overlayItems) {
            for (DataNode n : nodes) {
                if (n.getOverlayItem() == null)
                    n.setOverlayItem(new OverlayItem(n.toGeoPoint(), "", ""));
                this.overlayItems.add(new Pair<OverlayItem, Integer>(n
                        .getOverlayItem(), Integer.valueOf(n.getId())));
            }
        }
        populate();
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

    private DefaultListener contextMenueListener;
    private CurrentPosListener contextMenueCurrentPosListener;

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

        if (nodeId > 0) {
            contextMenueListener.setNodeId(nodeId);
            builder.setItems(contextMenueListener.getItems(),
                    contextMenueListener);
        } else {
            contextMenueCurrentPosListener.setPos(point);
            builder.setItems(contextMenueCurrentPosListener.getItems(),
                    contextMenueCurrentPosListener);
        }

        builder.show();
        return true;
    }

    private class DefaultListener implements DialogInterface.OnClickListener {
        private final CharSequence[] items = {
                context.getResources().getString(
                        R.string.cm_DataNodeArrayItemizedOverlay_tag),
                context.getResources().getString(
                        R.string.cm_DataNodeArrayItemizedOverlay_move),
                context.getResources().getString(
                        R.string.cm_DataNodeArrayItemizedOverlay_delete) };

        public DefaultListener() {
            // do nothing
        }

        public CharSequence[] getItems() {
            return items;
        }

        private int nodeId = 0;

        public void setNodeId(int id) {
            nodeId = id;
        }

        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
            case 0: // Tag this
                final Intent intent = new Intent(context,
                        AddPointActivity.class);
                intent.putExtra("DataNodeId", nodeId);

                context.startActivity(intent);
                break;
            case 1: // move this
                move_intent.putExtra("point_id", nodeId);
                context.sendBroadcast(move_intent);
                break;
            case 2: // delete this
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
                    Toast.makeText(context, "Can not delete Node id=" + nodeId,
                            Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
            }
        }
    }

    private class CurrentPosListener implements DialogInterface.OnClickListener {
        private final CharSequence[] items_default = {
                context.getResources().getString(
                        R.string.cm_DataNodeArrayItemizedOverlay_tag),
                context.getResources().getString(
                        R.string.cm_DataNodeArrayItemizedOverlay_way_start) };
        private final CharSequence[] items_way = {
                context.getResources().getString(
                        R.string.cm_DataNodeArrayItemizedOverlay_tag),
                context.getResources().getString(
                        R.string.cm_DataNodeArrayItemizedOverlay_way_end),
                context.getResources().getString(
                        R.string.cm_DataNodeArrayItemizedOverlay_way_add) };

        public CurrentPosListener() {
            // do nothing
        }

        public CharSequence[] getItems() {
            return tagging() ? items_way : items_default;
        }

        private GeoPoint point;

        public void setPos(GeoPoint point) {
            this.point = point;
        }

        private boolean tagging() {
            return Helper.currentTrack().getCurrentWay() != null;
        }

        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
            case 0: // Tag this
                final Intent intent = new Intent(context,
                        AddPointActivity.class);
                intent.putExtra("DataNodeId", currentTrack.newNode(point)
                        .getId());

                context.startActivity(intent);
                break;
            case 1: // Start/Stop way
                try {
                    if (tagging())
                        ServiceConnector.getLoggerService().endWay();
                    else
                        ServiceConnector.getLoggerService().beginWay(true);
                } catch (RemoteException e) {
                    Helper.handleNastyException(context, e, LOG_TAG);
                }
                break;
            case 2: // add waypoint
                try {
                    ServiceConnector.getLoggerService().beginWay(true);
                } catch (RemoteException e) {
                    Helper.handleNastyException(context, e, LOG_TAG);
                }
                break;
            default:
                break;
            }
        }
    }

    /**
     * we need to set the RoutesOverlay as the class will be created after this
     * one.
     * 
     * @param routesOverlay
     *            the routesOverlay also on the MapActivity
     */
    public void setRoutesOverlay(DataPointsListArrayRouteOverlay routesOverlay) {
        this.routesOverlay = routesOverlay;
    }
}
