/**
 * 
 */
package core.media;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.view.Surface;

/**
 * @author Huy Dinh
 * 
 */
public class VideoRecorder extends Recorder {
    private MediaRecorder recorder = new MediaRecorder();

    /**
     * 
     */
    boolean isReady = false;

    /**
     * 
     * @param camera
     * @param surface
     * @throws IllegalStateException
     * @throws IOException
     */
    public void prepare(final Camera camera, final Surface surface)
            throws IllegalStateException, IOException {
        filename = getNewFilename();

        // Set camera.
        // TODO: Not sure if needed, tbh.
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
