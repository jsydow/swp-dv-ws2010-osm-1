package tracebook.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import tracebook.core.data.DataStorage;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * This a general logging class and should be used for all logging action. It
 * can make popups that are visible to the user and logs that create a log file
 * or log to logcat. There is a log level which is an integer from 1 to 5. 5 is
 * the highest level and is equivalent to error in logcat. 1 is equivalent to
 * verbose in logcat.
 */
public final class LogIt {
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
    public synchronized LogIt getInstance() {
        if (instance == null) {
            instance = new LogIt();
        }
        return instance;
    }

    /**
     * Gets an instance of this Singleton. Shortcut for getInstance().
     * 
     * @return A LogIt instance.
     */
    public synchronized LogIt get() {
        return getInstance();
    }

    private static final String LOG_PREFIX = "TraceBook";

    /**
     * Log using the Android logging method.
     */
    public static final int LOGMETHOD_ANDROID = 1;
    /**
     * Log to file.
     */
    public static final int LOGMETHOD_FILE = 2;

    /**
     * Minimum value for a logging level.
     */
    public static final int MIN_LOG_LEVEL = 1;
    /**
     * Maximum value for a logging level.
     */
    public static final int MAX_LOG_LEVEL = 5;

    /**
     * Sets the method of logging. Use the constants above.
     * 
     * @param newMethod
     *            the new logging method.
     */
    public synchronized void setLogMethod(int newMethod) {
        this.method = newMethod;
    }

    /**
     * If you want to log only less important messages set the maximum log level
     * lower.
     * 
     * @param logLevel
     *            the new maximum logging level.
     */
    public synchronized void setMaxLogLevel(int logLevel) {
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
    public synchronized void setMinLogLevel(int logLevel) {
        if (logLevel < MIN_LOG_LEVEL) {
            this.minLogLevel = MIN_LOG_LEVEL;
        } else {
            this.minLogLevel = logLevel;
        }
    }

    /**
     * Shows a toast with a given message.
     * 
     * @param app
     *            The activity that shows the toast.
     * @param msg
     *            The message to display.
     */
    public static void popup(Context app, String msg) {
        Toast.makeText(app.getApplicationContext(), msg, Toast.LENGTH_SHORT)
                .show();
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
    public synchronized void log(String prefix, String message, int logLevel) {
        if ((logLevel <= maxLogLevel) || (logLevel <= minLogLevel)) {
            switch (method) {
            case LOGMETHOD_FILE:
                File logFile = new File(DataStorage.getTraceBookDirPath()
                        + File.separator + "log.txt");
                try {
                    FileWriter fw = new FileWriter(logFile);
                    fw.append(prefix + " : " + message);
                    fw.close();
                } catch (IOException e) {
                    Log.e(LOG_PREFIX, "Logging error : Could not log to file!");
                }
                break;
            case LOGMETHOD_ANDROID:
                switch (logLevel) {
                case 1:
                    Log.v(LOG_PREFIX, prefix + " : " + message);
                    break;
                case 2:
                    Log.d(LOG_PREFIX, prefix + " : " + message);
                    break;
                case 3:
                    Log.i(LOG_PREFIX, prefix + " : " + message);
                    break;
                case 4:
                    Log.w(LOG_PREFIX, prefix + " : " + message);
                    break;
                case 5:
                    Log.e(LOG_PREFIX, prefix + " : " + message);
                    break;
                default:
                    break;
                }
                break;
            default:
                break;
            }
        }
    }
}
