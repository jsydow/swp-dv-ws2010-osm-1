/**
 * 
 */
package core.media;

import java.io.File;

import core.data.DataMedia;
import core.data.DataMediaHolder;
import core.data.DataStorage;

/**
 * @author Huy Dinh
 * 
 */
public abstract class Recorder {
    /**
     * Request code for the onActivityResult callback. TAKE_AUDIO_CODE = 100000;
     */
    public static final int RECORD_AUDIO_CODE = 100000;

    /**
     * Request code for the onActivityResult callback. TAKE_VIDIO_CODE = 100002;
     */
    public static final int RECORD_VIDEO_CODE = 100001;

    /**
     * Base directory for our media file.
     */
    protected String baseDir = "";

    /**
     * File name of our media file.
     */
    protected String filename = "";

    /**
     * Are we currently recording?
     */
    protected boolean isRecording = false;

    /**
     * Construct0r!
     */
    public Recorder() {
        baseDir = DataStorage.getInstance().getCurrentTrack().getTrackDirPath();
    }

    /**
     * 
     */
    public abstract void init();

    /**
     * 
     * @return Filename.
     */
    public abstract String start();

    /**
     * 
     */
    public abstract void stop();

    /**
     * 
     * @param parent
     * @return No clue.
     */
    public DataMedia appendFileToObject(DataMediaHolder parent) {
        DataMedia dm = new DataMedia(getPath(), getFilename());

        parent.addMedia(dm);
        return dm;
    }

    /**
     * 
     * @return Directory our media file is put into.
     */
    public final String getBaseDir() {
        return baseDir;
    }

    /**
     * 
     * @return Filename.
     */
    public final String getFilename() {
        return filename;
    }

    /**
     * @return Full path.
     */
    public final String getPath() {
        return getBaseDir() + File.separator + getFilename();
    }

    /**
     * @return New filename.
     */
    protected abstract String getNewFilename();
}
