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

import java.util.List;

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
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * General helper class to feature some useful functions.
 */
public final class Helper {

    /**
     * Notification ID for the tracking notification.
     */
    static final int TRACKING_NOTIFY_ID = 1;

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
                .setCancelable(false)
                .setPositiveButton(
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
                                LogIt.popup(
                                        activity,
                                        activity.getResources()
                                                .getString(
                                                        R.string.alert_global_trackName)
                                                + " "
                                                + DataStorage.getInstance()
                                                        .getCurrentTrack()
                                                        .getName());

                                // stop logging
                                try {
                                    ServiceConnector.getLoggerService()
                                            .stopTrack();
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }

                                activity.finish();

                            }
                        })
                .setNegativeButton(
                        activity.getResources().getString(
                                R.string.alert_global_no),
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                    int which) {
                                dialog.cancel();
                            }
                        })
                .setNeutralButton(
                        activity.getResources().getString(
                                R.string.alert_global_notSaveAndClose),
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                    int which) {
                                dialog.cancel();

                                String trackname = DataStorage.getInstance()
                                        .getCurrentTrack().getName();
                                try {
                                    ServiceConnector.getLoggerService()
                                            .stopTrack();
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }

                                DataStorage.getInstance()
                                        .deserializeTrack(trackname).delete();

                                activity.finish();
                            }
                        });

        builder.show();

    }

    /**
     * This method check's the status of the visibility status bar.
     * 
     * @param activity
     *            context of the application
     * @return Returns visibility of the status bar which was checked/unchecked
     *         by the user at the preferences.
     */
    public static boolean checkStatusbarVisibility(Activity activity) {
        // Get the app's shared preferences
        SharedPreferences appPreferences = PreferenceManager
                .getDefaultSharedPreferences(activity);
        return appPreferences.getBoolean("check_visbilityStatusbar", false);

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
     * This method cut the given String to a maximum character number.
     * 
     * @param toCut
     *            String to cut
     * @param maxChar
     *            Maximum character number to cut the given string
     * @return String which have only maxChar characters
     */
    public static String cutString(String toCut, int maxChar) {
        if (toCut != null && toCut.length() > maxChar)
            return toCut.trim().subSequence(0, maxChar) + "…";
        else
            return toCut;
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
     * Creates a new {@link OverlayItem}.
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
        // TODO: unhardcode
        LogIt.popup(context,
                "An error occured. Please restart the application and try again.");
        LogIt.e(logTag, ex.getMessage());
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
     * This method check the global preferences. If the user checked the
     * position "show status bar" the method will activate the status bar with
     * much information for the given activity. If you want to show the status
     * bar in your activity please notice that you have to implement following
     * methods:
     * <p>
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
                final ImageView seperator = (ImageView) activity
                        .findViewById(R.id.ib_statusbar_seperator);
                seperator.setVisibility(8);

            }

            Button title = (Button) activity
                    .findViewById(R.id.btn_statusbar_activityTitle);
            Button desc = (Button) activity
                    .findViewById(R.id.btn_statusbar_activityDescription);
            title.setText(activityTitle);
            desc.setText(cutString(activityDesc, 40));
        }

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

    private Helper() { // Do nothing - why Checkstyle, why?!
    }
}
