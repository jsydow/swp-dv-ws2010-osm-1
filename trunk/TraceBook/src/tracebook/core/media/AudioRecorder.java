package tracebook.core.media;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.media.MediaRecorder;

/**
 * This class provides required methods for interacting with the MediaRecorder
 * in order to acquire an audio file.
 */
public class AudioRecorder extends Recorder {
    private boolean isReady = false;
    private MediaRecorder recorder;

    /**
     * Prepares the recorder.
     * 
     * @param maxDuration
     *            Maximum duration of the audio file to be recorded in seconds.
     * @throws IOException
     *             Not used.
     */
    public void prepare(int maxDuration) throws IOException {
        filename = getNewFilename();
        recorder = new MediaRecorder();

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        // Possible output formats are 3gpp and MPEG4, e. g.
        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(getPath());

        if (maxDuration > 0) {
            recorder.setMaxDuration(maxDuration * 1000);
        }

        recorder.prepare();

        isReady = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see core.media.Recorder#start()
     */
    @Override
    public String start() {
        if (isReady && !isRecording) {
            recorder.start();
            isRecording = true;

            return filename;
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see core.media.Recorder#stop(core.data.DataMediaHolder)
     */
    @Override
    public void stop() {
        if (isRecording) {
            recorder.stop();
            recorder.reset();
            recorder.release();

            isRecording = false;
        }
    }

    /*
     * (non-Javadoc)!
     * 
     * @see core.media.Recorder#getNewFilename()
     */
    @Override
    protected String getNewFilename() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String newFilename = sdf.format(new Date());

        newFilename = "audio_" + newFilename + ".m4a";

        return newFilename;
    }
}
