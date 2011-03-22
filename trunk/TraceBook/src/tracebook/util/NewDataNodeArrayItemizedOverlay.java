package tracebook.util;

import org.mapsforge.android.maps.ArrayItemizedOverlay;
import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.OverlayItem;

import tracebook.core.data.DataNode;
import tracebook.core.data.DataPointsList;
import tracebook.core.logger.ServiceConnector;
import tracebook.gui.activity.AddPointActivity;
import Trace.Book.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.RemoteException;

/**
 * This class extends the {@link ArrayItemizedOverlay} in order to overwrite
 * it's {@link #onTap(int)} method.
 */
public class NewDataNodeArrayItemizedOverlay extends ArrayItemizedOverlay {
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
                intent.putExtra("DataNodeId",
                        Helper.currentTrack().newNode(point).getId());

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

        private DataNode node = null;

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
                intent.putExtra("DataNodeId", node.getId());
                context.startActivity(intent);
                break;
            case 1: // move this
                sender.sendMovePoint(node.getId());
                break;
            case 2: // delete this
                DataPointsList way = null;
                if (node != null)
                    way = node.getDataPointsList();
                if (Helper.currentTrack().deleteNode(node.getId()) != null) {
                    removeItem(node.getOverlayItem());
                    if (way != null) // we have to redraw the way
                        sender.sendWayUpdate(way.getId());
                } else
                    LogIt.popup(context,
                            "Can not delete Node id=" + node.getId());
                break;
            default:
                break;
            }
        }

        public void setNode(DataNode node) {
            this.node = node;
        }
    }

    private static final String LOG_TAG = "DNAIO";

    private CurrentPosListener contextMenueCurrentPosListener;
    private DefaultListener contextMenueListener;

    /**
     * Reference to the {@link MapActivity}.
     */
    Context context;

    /**
     * The inherited constructor, sets context and default marker.
     * 
     * @param context
     *            reference to the {@link MapActivity}
     */
    public NewDataNodeArrayItemizedOverlay(Context context) {
        super(null, context);
        this.context = context;

        contextMenueListener = new DefaultListener();
        contextMenueCurrentPosListener = new CurrentPosListener();
    }

    @Override
    protected boolean onTap(int index) {
        final OverlayItem item = createItem(index);
        final DataNode node = Helper.currentTrack().getNodeByOverlayItem(item);

        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);

        if (node != null) {
            builder.setTitle("id: " + node.getId());
            contextMenueListener.setNode(node);
            builder.setItems(contextMenueListener.getItems(),
                    contextMenueListener);
        } else {
            builder.setTitle(context.getResources().getString(
                    R.string.cm_DataNodeArrayItemizedOverlay_my_pos));
            contextMenueCurrentPosListener.setPos(item.getPoint());
            builder.setItems(contextMenueCurrentPosListener.getItems(),
                    contextMenueCurrentPosListener);
        }

        builder.show();
        return true;
    }
}
