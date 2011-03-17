package util;

import android.util.Log;

/**
 * @author js
 * 
 */
public class LogIt {
    private LogIt instance;
    private int method;
    private int minLogLevel;
    private int maxLogLevel;

    private LogIt() {
        setLogMethod(LOGMETHOD_ANDROID);
        setMaxLogLevel(1000);
        setMinLogLevel(-1000);
    }

    /**
     * Gets an instance of this Singleton.
     * 
     * @return A LogIt instance.
     */
    public LogIt getInstance() {
        if (instance == null) {
            instance = new LogIt();
        }
        return instance;
    }

    private final static String LOG_PREFIX = "TraceBook";

    /**
     * Log using the Android logging method.
     */
    public final static int LOGMETHOD_ANDROID = 1;
    /**
     * Log to file.
     */
    public final static int LOGMETHOD_FILE = 2;

    /**
     * Minimum value for a logging level.
     */
    public final static int MIN_LOG_LEVEL = 1;
    /**
     * Maximum value for a logging level.
     */
    public final static int MAX_LOG_LEVEL = 5;

    /**
     * Sets the method of logging. Use the constants above.
     * 
     * @param newMethod
     *            the new logging method.
     */
    public void setLogMethod(int newMethod) {
        this.method = newMethod;
    }

    /**
     * If you want to log only less important messages set the maximum log level
     * lower.
     * 
     * @param logLevel
     *            the new maximum logging level.
     */
    public void setMaxLogLevel(int logLevel) {
        if (logLevel > MAX_LOG_LEVEL) {
            this.maxLogLevel = MAX_LOG_LEVEL;
        } else {
            this.maxLogLevel = logLevel;
        }
    }

    /**
     * If you want to log only more important messages set minimum log level
     * higher.
     * 
     * @param logLevel
     *            The new minimum logging level.
     */
    public void setMinLogLevel(int logLevel) {
        if (logLevel < MIN_LOG_LEVEL) {
            this.minLogLevel = MIN_LOG_LEVEL;
        } else {
            this.minLogLevel = logLevel;
        }
    }

    /**
     * Log a message.
     * 
     * @param prefix
     *            The prefix specifying the the origin of the log message.
     * @param message
     *            The actual message.
     * @param logLevel
     *            The importance of the log message. 0-5
     */
    public void log(String prefix, String message, int logLevel) {
        if ((logLevel <= maxLogLevel) || (logLevel <= minLogLevel)) {
            switch (method) {
            case LOGMETHOD_FILE:
                // TODO
                break;
            case LOGMETHOD_ANDROID:
                switch (logLevel) {
                case 1:
                    Log.v(LOG_PREFIX, prefix + ":" + message);
                    break;
                case 2:
                    Log.d(LOG_PREFIX, prefix + ":" + message);
                    break;
                case 3:
                    Log.i(LOG_PREFIX, prefix + ":" + message);
                    break;
                case 4:
                    Log.w(LOG_PREFIX, prefix + ":" + message);
                    break;
                case 5:
                    Log.e(LOG_PREFIX, prefix + ":" + message);
                    break;
                }
                break;
            default:
                break;
            }
        }
    }

}
