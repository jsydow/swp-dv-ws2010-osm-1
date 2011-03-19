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

    /**
     * Context of the MapActivity.
     */
    final Activity context;

    /**
     * Reference to the current DataTrack.
     */
    DataTrack currentTrack;

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
        synchronized (this.overlayItems) {
            for (int i = 0; i < overlayItems.size(); ++i)
                if (overlayItems.get(i).second.intValue() == id) {
                    OverlayItem oi = Helper.getOverlayItem(pos,
                            overlayItems.get(i).first.getMarker());
                    overlayItems.get(i).first = oi; // update the OverlayItem
                                                    // (not
                                                    // necessarily has a
                                                    // DataNode)

                    if (id > 0) { // redraw way when point was moved
                        DataNode node = Helper.currentTrack().getNodeById(id);
                        if (node != null) {
                            node.setLocation(pos); // update the actual DataNode
                            node.setOverlayItem(oi);

                            if (node.getDataPointsList() != null)
                                routesOverlay.reDrawWay(node
                                        .getDataPointsList().getId());
                        }
                    }
                    populate();
                    return;
                }
        }
        // no OverlayItem was yet added
        addOverlay(Helper.getOverlayItem(pos, icon, context), id);
        populate();
    }

    /**
     * When a {@link DataNode} has been changed, update it's visualization on
     * the DataNodeArrayItemizedOverlay.
     * 
     * @param node
     *            the node whichs position or OverlayItem has changed
     */
    public void updateItem(DataNode node) {
        boolean found = false;
        synchronized (this.overlayItems) {
            for (Pair<OverlayItem, Integer> item : overlayItems)
                if (item.second.intValue() == node.getId()) {
                    item.first = node.getOverlayItem();
                    found = true;
                    break;
                }
        }
        if (!found)
            addOverlay(node); // addOverlay populates the Overlay on its own
        else
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

    /**
     * This function removes all OverlayItems, which are not associated with any
     * {@link DataNode}. It also updates all OverlayItems associated with a
     * DataNode to the DataNodes position.
     * <p>
     * Warning: This function effectively has a runtime of n³!
     * </p>
     */
    public void removeOrphans() {
        synchronized (this.overlayItems) {
            Iterator<Pair<OverlayItem, Integer>> iter = overlayItems.iterator();
            while (iter.hasNext()) {
                Pair<OverlayItem, Integer> item = iter.next();
                int id = item.second.intValue();
                if (id > 0) {
                    DataNode node = Helper.currentTrack().getNodeById(id);
                    if (node == null) // remove orphan
                        iter.remove();
                    else {
                        // update OverlayItem
                        item.first = Helper.getOverlayItem(node.toGeoPoint(),
                                item.first.getMarker());
                        node.setOverlayItem(item.first);
                    }
                }
            }
        }
        populate();
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
            if (i < overlayItems.size())
                return this.overlayItems.get(i).first;
            else
                return null;
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

        if (nodeId > 0) {
            builder.setTitle("id: " + nodeId);
            contextMenueListener.setNodeId(nodeId);
            builder.setItems(contextMenueListener.getItems(),
                    contextMenueListener);
        } else {
            builder.setTitle(context.getResources().getString(
                    R.string.cm_DataNodeArrayItemizedOverlay_my_pos));
            contextMenueCurrentPosListener.setPos(point);
            builder.setItems(contextMenueCurrentPosListener.getItems(),
                    contextMenueCurrentPosListener);
        }

        builder.show();
        return true;
    }

    private class DefaultListener implements DialogInterface.OnClickListener {
        private GpsMessage sender;

        private final CharSequence[] items = {
                context.getResources().getString(
                        R.string.cm_DataNodeArrayItemizedOverlay_tag),
                context.getResources().getString(
                        R.string.cm_DataNodeArrayItemizedOverlay_move),
                context.getResources().getString(
                        R.string.cm_DataNodeArrayItemizedOverlay_delete) };

        public DefaultListener() {
            sender = new GpsMessage(context);
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
                sender.sendMovePoint(nodeId);
                break;
            case 2: // delete this
                final DataNode node = currentTrack.getNodeById(nodeId);
                DataPointsList way = null;
                if (node != null)
                    way = node.getDataPointsList();
                if (currentTrack.deleteNode(nodeId)) {
                    remove(nodeId);
                    if (way != null) // we have to redraw the way
                        sender.sendWayUpdate(way.getId());
                } else
                    LogIt.popup(context, "Can not delete Node id=" + nodeId);
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
