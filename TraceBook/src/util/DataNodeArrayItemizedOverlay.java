package util;

import java.util.ArrayList;

import org.mapsforge.android.maps.ArrayItemizedOverlay;
import org.mapsforge.android.maps.ItemizedOverlay;
import org.mapsforge.android.maps.OverlayItem;

import core.data.DataNode;
import core.data.DataStorage;
import core.data.DataTrack;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Pair;

/**
 * ArrayItemizedOverlay is a thread-safe implementation of the {@link ItemizedOverlay} class
 * using an {@link ArrayList} as internal data structure. A default marker for all
 * {@link OverlayItem OverlayItems} without an individual marker can be defined via the
 * constructor.
 * 
 * This Class is derived from the {@link ArrayItemizedOverlay} of MapsForge
 */
public class DataNodeArrayItemizedOverlay extends ItemizedOverlay<OverlayItem> {
    private static final int ARRAY_LIST_INITIAL_CAPACITY = 8;
    private static final String THREAD_NAME = "DataNodeArrayItemizedOverlay";

    private final Context context;
    private AlertDialog.Builder dialog;
    private final ArrayList<Pair<OverlayItem, Integer>> overlayItems;
    private DataTrack currentTrack;

    /**
     * Constructs a new ArrayItemizedOverlay.
     * 
     * @param defaultMarker
     *            the default marker (may be null). This marker is aligned to the center of its
     *            bottom line to allow for conical symbols such as a pin or a needle.
     * @param context
     *            the reference to the application context.
     */
    public DataNodeArrayItemizedOverlay(Drawable defaultMarker, Context context) {
        super(defaultMarker == null ? null : boundCenterBottom(defaultMarker));
        this.context = context;
        this.overlayItems = new ArrayList<Pair <OverlayItem, Integer>>(ARRAY_LIST_INITIAL_CAPACITY);
        this.currentTrack = DataStorage.getInstance().getCurrentTrack();
    }

    /**
     * Adds the given item to the overlay.
     * 
     * @param overlayItem
     *            the item that should be added to the overlay.
     * @param node_id
     *            id of the {@link DataNode} object associated with this Item
     */
    public void addOverlay(OverlayItem overlayItem, int node_id) {
        synchronized (this.overlayItems) {
            this.overlayItems.add(new Pair<OverlayItem, Integer>(overlayItem, new Integer(node_id)));
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
     * @param overlayItem
     *            the item that should be removed from the overlay.
     */
    public void removeOverlay(OverlayItem overlayItem) {
        synchronized (this.overlayItems) {
            this.overlayItems.remove(overlayItem);
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
            return this.overlayItems.get(i).first;
        }
    }

    @Override
    protected boolean onTap(int index) {
        synchronized (this.overlayItems) {
            final int node_id = this.overlayItems.get(index).second.intValue();
            this.dialog = new AlertDialog.Builder(this.context);
            this.dialog.setTitle("id: " + node_id);
            
            String message = "no tags set";
            if(node_id > 0) {
                DataNode node = currentTrack.getNodeById(node_id);
                if(node != null)
                    message = node.getTags().toString();
            } else
                message = "current position";
            this.dialog.setMessage(message);
            this.dialog.show();
            return true;
        }
    }
}