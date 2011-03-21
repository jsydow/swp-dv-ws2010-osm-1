package tracebook.core.media;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.view.Surface;

/**
 * This class provides required methods for interacting with the MediaRecorder
 * in order to acquire a video file.
 */
public class VideoRecorder extends Recorder {
    private MediaRecorder recorder = new MediaRecorder();
    private boolean isReady = false;

    /**
     * Because of the nature of recording a video with MediaRecorder, we have to
     * make sure a few certain properties have been set. Furthermore, we require
     * knowledge of the Surface object to show the preview in, as well as an
     * explicit object of the camera to use. All those necessary steps are taken
     * care of during preparation.
     * 
     * @param camera
     *            An object to the camera object we are going to use.
     * @param surface
     *            The surface object we are going to display our video preview
     *            in.
     * @throws IOException
     *             not used
     */
    public void prepare(final Camera camera, final Surface surface)
            throws IOException {
        filename = getNewFilename();

        // Set camera to be used for recording.
        recorder.setCamera(camera);

        // Set media sources.
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Set output.
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(getPath());

        recorder.setVideoFrameRate(25);
        recorder.setVideoSize(320, 240);

        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);

        recorder.setPreviewDisplay(surface);

        recorder.prepare();

        isReady = true;
    }

    /*
     * You can only start recording a video if prepare/2 has been called before.
     * Returns the filename if recording did start, null otherwise.
     * 
     * @see core.media.Recorder#start()
     */
    @Override
    public String start() {
        if (isReady) {
            recorder.start();
            isRecording = true;

            return filename;
        }

        return null;
    }

    @Override
    public void stop() {
        if (isRecording) {
            recorder.stop();
            recorder.reset();
            recorder.release();

            isRecording = false;
        }
    }

    @Override
    protected String getNewFilename() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String newFilename = sdf.format(new Date());

        newFilename = "video_" + newFilename + ".mp4";

        return newFilename;
    }
}
