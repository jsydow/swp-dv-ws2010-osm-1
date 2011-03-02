package core.logger;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Stub for RPC communication with the logger Service  
 * @author benpicco
 *
 */
public class LoggerServiceConnection implements ServiceConnection {
	
	ILoggerService loggerService = null;
	
	
	public void onServiceConnected(ComponentName className, IBinder boundService) {
		loggerService = ILoggerService.Stub.asInterface(boundService);
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
