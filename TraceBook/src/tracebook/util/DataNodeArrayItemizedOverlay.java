package tracebook.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mapsforge.android.maps.ArrayItemizedOverlay;
import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.ItemizedOverlay;
import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.OverlayItem;

import tracebook.core.data.DataNode;
import tracebook.core.data.DataPointsList;
import tracebook.core.data.DataStorage;
import tracebook.core.data.DataTrack;
import tracebook.core.logger.ServiceConnector;
import tracebook.gui.activity.AddPointActivity;
import Trace.Book.R;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;

/**
 * ArrayItemizedOverlay is a thread-safe implementation of the
 * {@link ItemizedOverlay} class using an {@link ArrayList} as internal data
 * structure. A default marker for all {@link OverlayItem OverlayItems} without
 * an individual marker can be defined via the constructor.
 * 
 * This Class is derived from the {@link ArrayItemizedOverlay} of MapsForge
 */
public class DataNodeArrayItemizedOverlay extends ItemizedOverlay<OverlayItem> {
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

        private GeoPoint point;

        public CurrentPosListener() {
            // do nothing
        }

        public CharSequence[] getItems() {
            return tagging() ? items_way : items_default;
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
            case 2: // add way point
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

        public void setPos(GeoPoint point) {
            this.point = point;
        }

        private boolean tagging() {
            return Helper.currentTrack().getCurrentWay() != null;
        }
    }

    private class DefaultListener implements DialogInterface.OnClickListener {
        private final CharSequence[] items = {
                context.getResources().getString(
                        R.string.cm_DataNodeArrayItemizedOverlay_tag),
                context.getResources().getString(
                        R.string.cm_DataNodeArrayItemizedOverlay_move),
                context.getResources().getString(
                        R.string.cm_DataNodeArrayItemizedOverlay_delete) };

        private int nodeId = 0;

        private GpsMessage sender;

        public DefaultListener() {
            sender = new GpsMessage(context);
        }

        public CharSequence[] getItems() {
            return items;
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

        public void setNodeId(int id) {
            nodeId = id;
        }
    }

    private static final int ARRAY_LIST_INITIAL_CAPACITY = 8;
    private static final String LOG_TAG = "DNAIO";

    private static final String THREAD_NAME = "DataNodeArrayItemizedOverlay";

    private CurrentPosListener contextMenueCurrentPosListener;

    private DefaultListener contextMenueListener;

    private final ArrayList<Pair<OverlayItem, Integer>> overlayItems;

    /**
     * Context of the MapActivity.
     */
    final MapActivity context;

    /**
     * Reference to the current DataTrack.
     */
    DataTrack currentTrack;

    /**
     * Constructs a new ArrayItemizedOverlay.
     * 
     * @param context
     *            a reference to the MapActivity.
     */
    public DataNodeArrayItemizedOverlay(MapActivity context) {
        super(boundCenterBottom(context.getResources().getDrawable(
                R.drawable.marker_green)));
        this.context = context;
        this.overlayItems = new ArrayList<Pair<OverlayItem, Integer>>(
                ARRAY_LIST_INITIAL_CAPACITY);
        this.currentTrack = DataStorage.getInstance().getCurrentTrack();

        contextMenueListener = new DefaultListener();
        contextMenueCurrentPosListener = new CurrentPosListener();
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
     * Updates the OverlayItem marking the current position.
     * 
     * @param currentPos
     *            current position, marker will be set there
     */
    public void setCurrentPosition(GeoPoint currentPos) {
        updateItem(currentPos, -1, null);
    }

    @Override
    public int size() {
        synchronized (this.overlayItems) {
            return this.overlayItems.size();
        }
    }

    /**
     * When a {@link DataNode} has been changed, update it's visualization on
     * the DataNodeArrayItemizedOverlay.
     * 
     * @param node
     *            the node whose position or OverlayItem has changed
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
     * Update the Item with the given id (change it's position or icon).
     * 
     * @param pos
     *            new position (may be null if only the marker should be
     *            updated)
     * @param id
     *            id of the {@link DataNode}
     * @param marker
     *            new marker (may be null if only the position should be
     *            updated)
     */
    public void updateItem(GeoPoint pos, int id, Drawable marker) {
        synchronized (this.overlayItems) {
            for (Pair<OverlayItem, Integer> oip : overlayItems)
                if (oip.second.intValue() == id) {
                    if (pos != null)
                        oip.first.setPoint(pos);
                    if (marker != null)
                        oip.first.setMarker(marker);

                    populate();
                    return;
                }
        }
        addOverlay(new OverlayItem(pos, null, null, marker), id);
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
}
