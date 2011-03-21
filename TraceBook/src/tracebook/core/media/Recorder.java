package tracebook.core.media;

import java.io.File;

import tracebook.core.data.DataMedia;
import tracebook.core.data.DataMediaHolder;
import tracebook.core.data.DataStorage;


/**
 * Mother of all TraceBook media files. This class is the common ancestor of all
 * implementations for acquiring media files in TraceBook.
 * 
 *
 */
public abstract class Recorder {
    /**
     * Request code for onActivityResult callback. RECORD_AUDIO_CODE = 100000;
     */
    public static final int RECORD_AUDIO_CODE = 100000;

    /**
     * Request code for onActivityResult callback. RECORD_VIDIO_CODE = 100001;
     */
    public static final int RECORD_VIDEO_CODE = 100001;

    /**
     * Request code for onActivityResult callback. TAKE_PHOTO_CODE = 100002;
     */
    public static final int TAKE_PHOTO_CODE = 100002;

    /**
     * Base directory of our media file.
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
     * Construct0r! Sets the base directory according to our current track.
     */
    public Recorder() {
        baseDir = DataStorage.getInstance().getCurrentTrack().getTrackDirPath();
    }

    /**
     * Attaches the recorded media file to the given node of our data structure.
     * 
     * @param parent
     *            Node to attach the recorded file to.
     * @return A reference to the created media file object in the global data
     *         structure.
     */
    public DataMedia appendFileToObject(DataMediaHolder parent) {
        DataMedia dm = new DataMedia(getPath(), getFilename());

        parent.addMedia(dm);
        return dm;
    }

    /**
     * @return Base directory of the media file.
     */
    public final String getBaseDir() {
        return baseDir;
    }

    /**
     * @return Name of the media file.
     */
    public final String getFilename() {
        return filename;
    }

    /**
     * @return Full path to the media file.
     */
    public final String getPath() {
        return getBaseDir() + File.separator + getFilename();
    }

    /**
     * @return Whether the recorder is recording.
     */
    public final boolean isRecording() {
        return isRecording;
    }

    /**
     * This method starts recording a media file, if it is a continuous process,
     * such as recording audio or video files.
     * 
     * For one-shot acquisition (e. g. taking pictures), start() and stop()
     * should not be used, because of misleading semantics.
     * 
     * @return Filename of the created media file.
     */
    public abstract String start();

    /**
     * This method stops recording a media file, if it is a continuous process,
     * such as recording audio or video files.
     * 
     * For one-shot acquisition (e. g. taking pictures), start() and stop()
     * should not be used, because of misleading semantics.
     */
    public abstract void stop();

    /**
     * WARNING: Because of the nature of how the full path to the file is
     * determined, we blatantly assume the returned filename to *NOT* point to
     * an existing file yet. If such a file does exist for any reason, it will
     * most likely be overwritten!
     * 
     * @return New filename for the media file to be created.
     */
    protected abstract String getNewFilename();
}
