package core.logger;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Stub for RPC communication with the logger Service.
 */
public class LoggerServiceConnection implements ServiceConnection {

    /**
     * Reference for loggerService.
     */
    ILoggerService loggerService = null;

    public void onServiceConnected(ComponentName className, IBinder boundService) {
        loggerService = ILoggerService.Stub.asInterface(boundService);
        // Log.d(LOG_TAG, "onServiceConnected");
    }

    public void onServiceDisconnected(ComponentName className) {
        loggerService = null;
        // Log.d(LOG_TAG, "onServiceDisconnected");
    }

    /**
     * @return get a reference to the loggerService
     */
    public ILoggerService getLoggerService() {
        return loggerService;
    }
}
