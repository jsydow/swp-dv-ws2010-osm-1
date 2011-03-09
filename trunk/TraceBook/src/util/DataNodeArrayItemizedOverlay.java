package util;

import gui.AddPointActivity;

import java.util.ArrayList;
import java.util.Iterator;

import org.mapsforge.android.maps.ArrayItemizedOverlay;
import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.ItemizedOverlay;
import org.mapsforge.android.maps.OverlayItem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import android.widget.Toast;
import core.data.DataNode;
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

    /**
     * context of the MapActivity
     */
    final Context context;

    /**
     * reference to the current DataTrack
     */
    DataTrack currentTrack;

    /**
     * Constructs a new ArrayItemizedOverlay.
     * 
     * @param defaultMarker
     *            the default marker (may be null). This marker is aligned to
     *            the center of its bottom line to allow for conical symbols
     *            such as a pin or a needle.
     * @param context
     *            the reference to the application context.
     */
    public DataNodeArrayItemizedOverlay(Drawable defaultMarker, Context context) {
        super(defaultMarker == null ? null : boundCenterBottom(defaultMarker));
        this.context = context;
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
                    new Integer(nodeId)));
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
     * removes an {@link OverlayItem} by the ID of the associated
     * {@link DataNode}
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

    private final CharSequence[] items = { "Tag this", "Delete this" };

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
                case 0:
                    final Intent intent = new Intent(context,
                            AddPointActivity.class);
                    if (nodeId < 0)
                        intent.putExtra("DataNodeId",
                                currentTrack.newNode(point).getId());
                    else
                        intent.putExtra("DataNodeId", nodeId);

                    context.startActivity(intent);
                    break;
                case 1:
                    if (nodeId < 0)
                        Toast.makeText(context,
                                "can not delete current location",
                                Toast.LENGTH_SHORT).show();
                    else {
                        if (currentTrack.deleteNode(nodeId))
                            remove(nodeId);
                        else
                            Toast.makeText(context,
                                    "Can not delete Node id=" + nodeId,
                                    Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        builder.show();
        return true;
    }
}
