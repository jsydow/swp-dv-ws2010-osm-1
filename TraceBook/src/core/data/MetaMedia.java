package core.data;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * This class takes care of how different media files are created. Basically,
 * there are two different ways to create a new file:
 * 
 * a) We create an intent and let it handle the whole process. This is the
 * preferred way for audio and video files, because there already are actions
 * for those (MediaStore.ACTION_IMAGE_CAPTURE and
 * MediaStore.ACTION_VIDEO_CAPTURE).
 * 
 * b) We provide everything necessary to acquire media files ourselves. This
 * (unfortunately) is how we have to take care of sound recording, because we
 * cannot use intents to achieve that.
 * 
 * @author Huy Dinh
 */
public class MetaMedia {
    /**
     * Request code for the onActivityResult callback. TAKE_AUDIO_CODE = 100000;
     */
    public static final int TAKE_AUDIO_CODE = 100000;

    /**
     * Request code for the onActivityResult callback. TAKE_PHOTO_CODE = 100001;
     */
    public static final int TAKE_PHOTO_CODE = 100001;

    /**
     * Request code for the onActivityResult callback. TAKE_VIDIO_CODE = 100002;
     */
    public static final int TAKE_VIDEO_CODE = 100002;

    /**
     * Base directory for our media files.
     */
    private String baseDir;

    /**
     * Name of the most recent media file.
     */
    private String currentFilename = "";

    /**
     * The MediaRecorder object that takes care of audio recording.
     */
    private MediaRecorder recorder;

    /**
     * A flag that tracks whether any audio recording undergoing right now.
     */
    private boolean isRecordingAudio = false;

    /**
     * Constructor. Initializes the recorder object for audio recording and sets
     * the base directory for media files.
     */
    public MetaMedia() {
        baseDir = DataStorage.getInstance().getCurrentTrack().getTrackDirPath();
        recorder = new MediaRecorder();
    }

    /**
     * Launches an activity to take a picture (usually the camera app).
     * 
     * @param activity
     *            The activity that eventually fires the intent and that handles
     *            the onActivityResult callback.
     * @return Name of the created media file.
     */
    public final String takePhoto(final Activity activity) {
        return recordVideoOrPhoto(activity, MediaStore.ACTION_IMAGE_CAPTURE,
                TAKE_PHOTO_CODE);
    }

    /**
     * Launches an activity to start recording a video (usually the stock video
     * recording app).
     * 
     * @param activity
     *            The activity that eventually fires the intent and that handles
     *            the onActivityResult callback.
     * @return Name of the created media file.
     */
    public final String takeVideo(final Activity activity) {
        return recordVideoOrPhoto(activity, MediaStore.ACTION_VIDEO_CAPTURE,
                TAKE_VIDEO_CODE);
    }

    /**
     * Starts recording an audio file at the given path.
     * 
     * @return Name of the created media file.
     */
    public final String startAudio() {
        if (!isRecordingAudio) {
            currentFilename = getNewFilename(TAKE_AUDIO_CODE);
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // Possible output formats are 3gpp and MPEG4, e. g.
            recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setOutputFile(getPath());

            try {
                recorder.prepare();
                recorder.start();
                isRecordingAudio = true;

                return currentFilename;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "";
        }

        return "";
    }

    /**
     * Stops recording the audio file that is being recorded and releases the
     * recorder object. Furthermore, this inserts a new media file into our
     * current track, because startAudio() and stopAudio() are not intents. As
     * such, the onActivityResult callback will not be invoked for these.
     * 
     * @param parent
     *            DataMediaHolder object to append the media file to.
     */
    public final void stopAudio(DataMediaHolder parent) {
        if (isRecordingAudio) {
            recorder.stop();
            recorder.reset();
            recorder.release();

            appendToObject(parent);

            isRecordingAudio = false;
        }
    }

    /**
     * @return Returns base directory for media files.
     */
    public final String getBaseDir() {
        return baseDir;
    }

    /**
     * @return File name of the current media file.
     */
    public final String getCurrentFilename() {
        return currentFilename;
    }

    /**
     * @return Path to the current media file.
     */
    public final String getPath() {
        return baseDir + File.separator + currentFilename;
    }

    /**
     * @return Whether an audio file is being recorded at the moment.
     */
    public final boolean isRecordingAudio() {
        return isRecordingAudio;
    }

    /**
     * Appends this media file to a given DataMediaHolder object.
     * 
     * @param parent
     *            Object to append a DataMedia object to.
     * @return Object that has been appended.
     */
    public final DataMedia appendToObject(DataMediaHolder parent) {
        DataMedia dm = new DataMedia(getPath(), currentFilename);

        parent.addMedia(dm);
        return dm;
    }

    /**
     * Creates the intent and launches the necessary activity for acquiring the
     * media file in question.
     * 
     * @param activity
     *            The activity that eventually fires the intent and that handles
     *            the onActivityResult callback.
     * @param action
     *            Action for the intent to be created.
     * @param requestCode
     *            The request code that is passed to onActivityResult when the
     *            launched activity has terminated.
     * @return Name of the created media file.
     */
    private String recordVideoOrPhoto(final Activity activity,
            final String action, final int requestCode) {
        final Intent i = new Intent(action);
        currentFilename = getNewFilename(requestCode);

        i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(getPath())));
        activity.startActivityForResult(i, requestCode);

        return currentFilename;
    }

    /**
     * Makes up a new filename for the given media type.
     * 
     * @param mediaType
     *            Media type to make up a new filename for.
     * @return A filename for the provided media type.
     */
    private String getNewFilename(final int mediaType) {
        String newFilename = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timestamp = sdf.format(new Date());

        switch (mediaType) {
        case TAKE_AUDIO_CODE:
            newFilename += "audio_" + timestamp + ".m4a";
            break;
        case TAKE_PHOTO_CODE:
            newFilename += "image_" + timestamp + ".jpg";
            break;
        case TAKE_VIDEO_CODE:
            newFilename += "video_" + timestamp + ".mp4";
            break;
        default:
            break;
        }

        return newFilename;
    }
}
