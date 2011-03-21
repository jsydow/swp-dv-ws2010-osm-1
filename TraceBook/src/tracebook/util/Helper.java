package tracebook.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.ItemizedOverlay;
import org.mapsforge.android.maps.OverlayItem;

import tracebook.core.data.DataNode;
import tracebook.core.data.DataPointsList;
import tracebook.core.data.DataStorage;
import tracebook.core.data.DataTrack;
import tracebook.core.logger.ServiceConnector;
import Trace.Book.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * General helper class to feature some useful functions.
 */
public final class Helper {

    /**
     * Notification ID for the tracking notification.
     */
    final static int TRACKING_NOTIFY_ID = 1;

    private Helper() { // do nothing - why Checkstyle, why?!
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
     * Gets the current way in the current {@link DataTrack}.
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
     * Gets the list of Nodes in current {@link DataTrack}.
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
        Toast
                .makeText(
                        context,
                        "An error occured. Please restart the application and try again.",
                        Toast.LENGTH_LONG).show();
        Log.e(logTag, ex.getMessage());
    }

    /**
     * Smoothen the {@link DataNode}s by calculating the mean of 3 consecutive
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
        smoothenPointsMiddle(nodes, weight, bufferSize);
    }

    /**
     * Smoothen the {@link DataNode}s by calculating the mean of 3 consecutive
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
    public static void smoothenPointsFirst(List<DataNode> nodes, int weight,
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

            // the ring buffer is an element smaller now
            ringbuffer.poll().setLocation(
                    new GeoPoint(
                            latsum / ((double) ringbuffer.size() + weight),
                            lonsum / ((double) ringbuffer.size() + weight)));

        }
    }

    /**
     * Smoothen the {@link DataNode}s by calculating the mean of multiple
     * consecutive points.
     * 
     * @param nodes
     *            The list of DataNodes representing the way
     * @param weight
     *            the factor by which the original point is higher weighted then
     *            it's bufferSize-1 successors
     * @param bufferSize
     *            the amount of points to use for the calculation
     */
    public static void smoothenPointsMiddle(List<DataNode> nodes, int weight,
            int bufferSize) {
        if (nodes == null)
            return;

        LinkedList<DataNode> ringbuffer = new LinkedList<DataNode>();

        for (DataNode n : nodes) {
            if (!n.isValid())
                continue;

            ringbuffer.add(n);

            if (ringbuffer.size() < bufferSize)
                continue;

            double latsum = 0;
            double lonsum = 0;
            for (int i = 0; i < ringbuffer.size(); i++)
                if (i == ringbuffer.size() / 2) {
                    latsum += ringbuffer.get(i).getLat() * weight;
                    lonsum += ringbuffer.get(i).getLon() * weight;
                } else {
                    latsum += ringbuffer.get(i).getLat();
                    lonsum += ringbuffer.get(i).getLon();
                }

            // the ringbuffer is an element smaller now
            ringbuffer
                    .get(ringbuffer.size() / 2)
                    .setLocation(
                            new GeoPoint(
                                    latsum
                                            / ((double) (ringbuffer.size() - 1) + weight),
                                    lonsum
                                            / ((double) (ringbuffer.size() - 1) + weight)));
            ringbuffer.poll();

        }
    }

    /**
     * Calculates the area spanned by the parallelogram defined by the points a,
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
     * Removes all insignificant points from the way, a threshold is calculated
     * automatically by the average derivation of the points.
     * 
     * @param nodes
     *            List of {@link DataNode}s representing the way or area
     * @param weight
     *            Weight with which the average derivation of points is
     *            multiplied to form the threshold
     */
    public static void filterPoints(List<DataNode> nodes, double weight) {
        boolean calibrate = true;
        double threshold = 0;

        if (nodes.size() < 3)
            return;

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
                    } else if (calculateArea(firstNode.toGeoPoint(), pending
                            .toGeoPoint(), n.toGeoPoint()) < threshold * weight
                            && !n.hasAdditionalInfo() && iter.hasNext())
                        iter.remove();
                    firstNode = pending;
                }

                pending = n;
            }
            threshold /= nodes.size();
            Log.d("Helper", "Average: " + threshold);
        }
    }

    /**
     * This Method show a alert dialog to save the Track and give a name for the
     * current track.
     * 
     * Shows a dialog box that saves the current track.
     * 
     * @param activity
     *            Activity in which the Dialog has to be display The activity
     *            that starts the dialog.
     */
    public static void alertStopTracking(final Activity activity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final EditText input = new EditText(activity);
        input.setHint(DataStorage.getInstance().getCurrentTrack().getName());
        builder.setView(input);
        builder.setTitle(activity.getResources().getString(
                R.string.alert_newtrackActivity_saveSetTrack));
        builder.setMessage(
                activity.getResources().getString(R.string.alert_global_exit))
                .setCancelable(false).setPositiveButton(
                        activity.getResources().getString(
                                R.string.alert_global_yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                // set track name
                                String value = input.getText().toString()
                                        .trim();
                                if (!value.equals("")) {
                                    DataStorage.getInstance().getCurrentTrack()
                                            .setName(value);
                                }

                                // send notification toast for user
                                Toast
                                        .makeText(
                                                activity
                                                        .getApplicationContext(),
                                                activity
                                                        .getResources()
                                                        .getString(
                                                                R.string.alert_global_trackName)
                                                        + " "
                                                        + DataStorage
                                                                .getInstance()
                                                                .getCurrentTrack()
                                                                .getName(),
                                                Toast.LENGTH_SHORT).show();

                                // stop logging
                                try {
                                    ServiceConnector.getLoggerService()
                                            .stopTrack();
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }

                                activity.finish();

                            }
                        }).setNegativeButton(
                        activity.getResources().getString(
                                R.string.alert_global_no),
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                    int which) {
                                dialog.cancel();
                            }
                        });
        builder.show();

    }

    /**
     * This method check the global preferences. If the user checked the
     * position "show status bar" the method will activate the status bar with
     * much information for the given activity. If you want to show the status
     * bar in your activity please notice that you have to implement following
     * methods:
     * 
     * public void statusBarTitle (View v), public void statusBarDescription
     * (View v), public void statusBarPrefBtn(View v), public void
     * statusBarSearchBtn(View v) - if boolean searchBox = true
     * statusBarSearchfunc() - if boolean searchBox = true
     * 
     * @param activity
     *            The activity in the status bar to display.
     * @param activityTitle
     *            The title of the activity, that will displayed in the status
     *            bar.
     * @param activityDesc
     *            The description of the activity, that will displayed in the
     *            status bar.
     * @param layoutPosition
     *            The resource to inflate the menu at this position.
     * @param searchBox
     *            If a search window will appear. Notice - you have to implement
     *            the functionality of the EditText in your activity.
     */
    public static void setStatusBar(final Activity activity,
            String activityTitle, String activityDesc, int layoutPosition,
            boolean searchBox) {

        // Get the app's shared preferences
        SharedPreferences appPreferences = PreferenceManager
                .getDefaultSharedPreferences(activity);

        // Get the value for the status bar check box - default false
        if (appPreferences.getBoolean("check_visbilityStatusbar", false)) {
            LayoutInflater statusListInflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout statuslayoutHolder = (LinearLayout) activity
                    .findViewById(layoutPosition);
            statusListInflater.inflate(R.layout.statusbar_global,
                    statuslayoutHolder);

            // Set visibility of the search button in the status bar.
            if (!searchBox) {
                final ImageButton searchBtn = (ImageButton) activity
                        .findViewById(R.id.ib_statusbar_searchBtn);
                searchBtn.setVisibility(8);
            }

            Button title = (Button) activity
                    .findViewById(R.id.btn_statusbar_activityTitle);
            Button desc = (Button) activity
                    .findViewById(R.id.btn_statusbar_activityDescription);
            title.setText(activityTitle);
            desc.setText(cutString(activityDesc, 50));
        }

    }

    /**
     * This method cut the given String to a maximum character number.
     * 
     * @param toCut
     *            String to cut
     * @param maxChar
     *            Maximum character number to cut the given string
     * @return String which have only maxChar characters
     */
    public static String cutString(String toCut, int maxChar) {
        if (toCut.length() > maxChar)
            return toCut.trim().subSequence(0, maxChar) + "...";
        else if (toCut.length() > 0)
            return toCut + "...";
        else
            return toCut;
    }

    /**
     * 
     * This Method display a dialog box with all information about the activity
     * for the user. This help's for a better understanding of the functionality
     * of the active activity.
     * 
     * @param activity
     *            The activity in which the info dialog has to be displayed
     * @param title
     *            The title of the activity which has to be displayed in the
     *            dialog box.
     * @param desc
     *            The description of the activity which has to be displayed in
     *            the dialog box
     * 
     */
    public static void setActivityInfoDialog(Activity activity, String title,
            String desc) {
        final Dialog activityInfoDialog = new Dialog(activity);
        activityInfoDialog.setContentView(R.layout.dialog_activityinfo);
        activityInfoDialog.setTitle("Activity Informationen");
        activityInfoDialog.setCancelable(true);
        TextView dialogTitle = (TextView) activityInfoDialog
                .findViewById(R.id.tv_dialogactivityinfo_activityTitle);
        TextView dialogDesc = (TextView) activityInfoDialog
                .findViewById(R.id.tv_dialogactivityinfo_activityDescription);
        dialogTitle.setText(activity.getResources().getString(
                R.string.string_statusDialog_here)
                + ": " + title);
        dialogDesc.setText(activity.getResources().getString(
                R.string.string_global_descriptionTitle)
                + "\n" + desc);
        activityInfoDialog.show();
    }

    /**
     * 
     * This method start the tracking notification for the user.
     * 
     * @param activity
     *            The activity from which the method called.
     * @param icon
     *            The icon id which will show in the notification bar.
     * @param cls
     *            The class which will be called for the intent, if the user
     *            click at the notification.
     */
    public static void startUserNotification(Activity activity, int icon,
            Class<?> cls) {

        // User notification
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) activity
                .getSystemService(ns);
        CharSequence tickerText = activity.getResources().getString(
                R.string.not_startActivity_tickerText);
        long when = System.currentTimeMillis();

        Notification notification = new Notification(icon, tickerText, when);

        Context context = activity.getApplicationContext();
        CharSequence contentTitle = activity.getResources().getString(
                R.string.not_startActivity_contentTitle);
        CharSequence contentText = activity.getResources().getString(
                R.string.not_startActivity_contentText);

        Context con = activity.getApplicationContext();
        Intent notificationIntent = new Intent(con, cls);
        PendingIntent contentIntent = PendingIntent.getActivity(con, 0,
                notificationIntent, 0);

        notification.setLatestEventInfo(context, contentTitle, contentText,
                contentIntent);

        mNotificationManager.notify(TRACKING_NOTIFY_ID, notification);
    }

    /**
     * This method start the tracking notification for the user.
     * 
     * @param activity
     *            The activity from which the method called.
     */
    public static void stopUserNotification(Activity activity) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) activity
                .getSystemService(ns);
        mNotificationManager.cancel(TRACKING_NOTIFY_ID);

    }
}
