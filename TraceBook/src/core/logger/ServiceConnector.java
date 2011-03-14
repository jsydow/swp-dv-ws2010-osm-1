package core.logger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * This class provides methods for controlling the {@link WaypointLogService}.
 */
public class ServiceConnector {
    private static final String LOG_TAG = "LOGSERVICECLIENT";
    private static LoggerServiceConnection conn = null;
    private static boolean started = false;
    private static Activity activity = null;

    /**
     * Default constructor.
     */
    protected ServiceConnector() {
        super();
    }

    /**
     * Bind the logger service to this activity.
     */
    synchronized public static void initService() {

        if (conn == null) {
            conn = new LoggerServiceConnection();
            Intent i = new Intent();
            i.setClassName(activity.getPackageName(),
                    WaypointLogService.class.getName());
            activity.bindService(i, conn, Context.BIND_AUTO_CREATE);
            Log.d(LOG_TAG, "bindService()");
        } else
            Log.d(LOG_TAG, "Cannot bind - service already bound");
    }

    /**
     * Release the logger service.
     */
    synchronized public static void releaseService() {
        if (conn != null) {
            activity.unbindService(conn);
            conn = null;
            Log.d(LOG_TAG, "unbindService()");
        } else
            Log.d(LOG_TAG, "Cannot unbind - service not bound");
    }

    /**
     * Start the logging service (collect GPS data).
     * 
     * @param act
     *            reference to an activity for which the service should be
     *            binded
     */
    public static void startService(Activity act) {
        activity = act;
        if (started) {
            Log.d(LOG_TAG, "Service already started");
        } else {
            Intent i = new Intent();
            i.setClassName(activity.getPackageName(),
                    WaypointLogService.class.getName());
            activity.startService(i);
            Log.d(LOG_TAG, "startService()");
            started = true;
        }
    }

    /**
     * Stop logging service (stop collecting GPS data).
     */
    public static void stopService() {
        if (!started) {
            Log.d(LOG_TAG, "Service not yet started");
        } else {
            Intent i = new Intent();
            i.setClassName(activity.getPackageName(),
                    WaypointLogService.class.getName());
            activity.stopService(i);
            Log.d(LOG_TAG, "stopService()");
        }
    }

    /**
     * @return get a reference to the loggerService
     */
    public static ILoggerService getLoggerService() {
        // danger - ?!
        return conn.getLoggerService();
    }

}
