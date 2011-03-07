package core.data;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * This class takes care of how different media files are created. Basically,
 * there are two different ways to create a new file:
 *
 * a) We create an intent and let it handle the whole process.
 *    This is the preferred way for audio and video files, because there
 *    already are actions for those (MediaStore.ACTION_IMAGE_CAPTURE and
 *    MediaStore.ACTION_VIDEO_CAPTURE).
 *
 * b) We provide everything necessary to acquire media files ourselves. This
 *    (unfortunately) is how we have to take care of sound recording, because
 *    we cannot use intents to achieve that.
 *
 * @author Huy Dinh
 */
public class MetaMedia {
	/**
	 *  Request Codes for the onActivityResult callback.
	 */
	public static final int TAKE_PHOTO_CODE = 1;
	public static final int TAKE_VIDEO_CODE = 2;

	/**
	 * The MediaRecorder object that takes care of audio recording.
	 */
	private MediaRecorder recorder;
	
	/**
	 *  A flag that tracks whether any audio recording undergoing right now.
	 */
	private boolean isRecordingAudio = false;

	/**
	 * Constructor. Initializes the recorder object for audio recording.
	 */
	MetaMedia() {
		recorder = new MediaRecorder();
	}

	/**
	 * Launches an activity to take a picture (usually the camera app).
	 *
	 * @param activity The activity that eventually fires the intent and
	 *                 that handles the onActivityResult callback.
	 * @param path     Path to the file the image is going to be saved to.
	 */
	public final void takePhoto(final Activity activity, final String path) {
		recordVideoOrPhoto(activity, path, MediaStore.ACTION_IMAGE_CAPTURE,
				           TAKE_PHOTO_CODE);
	}

	/**
	 * Launches an activity to start recording a video (usually the stock
	 * video recording app).
	 *
	 * @param activity The activity that eventually fires the intent and
	 *                 that handles the onActivityResult callback.
	 * @param path     Path to the file the video is going to be saved to.
	 */
	public final void takeVideo(final Activity activity, final String path) {
		recordVideoOrPhoto(activity, path, MediaStore.ACTION_VIDEO_CAPTURE,
				           TAKE_VIDEO_CODE);
	}

	/**
	 * Starts recording an audio file at the given path.
	 *
	 * @param path     Path to the file the audio recording is going to be
	 *                 saved to.
	 */
	public final void startAudio(final String path) {
		if (!isRecordingAudio) {
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			// Possible output formats are 3gpp and MPEG4, e. g.
			recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			recorder.setOutputFile(path);

			try {
				recorder.prepare();
			} catch (IllegalStateException e) {
				// 	TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			recorder.start();
			isRecordingAudio = true;
		}
	}

	/**
	 * Stops recording the audio file that is being recorded and releases
	 * the recorder object.
	 */
	public final void stopAudio() {
		if (isRecordingAudio) {
			recorder.stop();
			recorder.reset();
			recorder.release();
			isRecordingAudio = false;
		}
	}

	/**
	 * @return Whether an audio file is being recorded at the moment.
	 */
	public final boolean isRecordingAudio() {
		return isRecordingAudio;
	}

	/**
	 * Creates the intent and launches the necessary activity for acquiring
	 * the media file in question.
	 *
	 * @param activity     The activity that eventually fires the intent and that
	 *                     handles the onActivityResult callback.
	 * @param path         Path to the file the video is going to be saved to.
	 * @param action       Action for the intent to be created.
	 * @param requestCode  The request code that is passed to onActivityResult
	 *                     when the launched activity has terminated.
	 */
	private void recordVideoOrPhoto(final Activity activity, final String path,
			final String action, final int requestCode) {
		final Intent i = new Intent(action);
		i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(path)));
		activity.startActivityForResult(i, requestCode);
	}
}
