package core.logger;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Stub for RPC communication with the logger Service  
 * @author benpicco
 *
 */
class LoggerServiceConnection implements ServiceConnection {
	
	ILoggerService loggerService = null;
	
	
	public void onServiceConnected(ComponentName className, IBinder boundService) {
		loggerService = ILoggerService.Stub.asInterface((IBinder) boundService);
		//Log.d(LOG_TAG, "onServiceConnected");
	}

	public void onServiceDisconnected(ComponentName className) {
		loggerService = null;
		//Log.d(LOG_TAG, "onServiceDisconnected");
	}
	
	public ILoggerService getLoggerService() {
		return loggerService;
	}
}

public class ServiceConnector {
	
	
	
	private static final String LOG_TAG = "LOGSERVICECLIENT";
	private static ILoggerService loggerService = null;
	private static LoggerServiceConnection conn = null;
	private static boolean started = false;
	private static Activity activity = null;
	
	/**
	 * Bind the logger service to this activity
	 */
	public static void initService( Activity act ) {
		activity = act;
		if (conn == null) {
			conn = new LoggerServiceConnection();
			Intent i = new Intent();
			i.setClassName(activity.getPackageName(), WaypointLogService.class.getName());
			activity.bindService(i, conn, Context.BIND_AUTO_CREATE);
			Log.d(LOG_TAG, "bindService()");
		} else {
			Toast.makeText(activity, "Cannot bind - service already bound", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * release the logger service
	 */
	public static void releaseService() {
		if (conn != null) {
			activity.unbindService(conn);
			conn = null;
			Log.d(LOG_TAG, "unbindService()");
		} else {
			Toast.makeText(activity, "Cannot unbind - service not bound", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Start the logging service (collect GPS data)
	 */
	public static void startService() {
		if (started) {
			Toast.makeText(activity, "Service already started", Toast.LENGTH_SHORT).show();
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
	 * Stop logging service (stop collecting GPS data)
	 */
	public static void stopService() {
		if (!started) {
			Toast.makeText(activity, "Service not yet started", Toast.LENGTH_SHORT).show();
		} else {

			Intent i = new Intent();
			i.setClassName(activity.getPackageName(), WaypointLogService.class.getName());
			activity.stopService(i);
			Log.d(LOG_TAG, "stopService()");
		}		 
	}
	
	public static ILoggerService getLoggerService() {
		//danger
		return conn.getLoggerService();
	}

}
