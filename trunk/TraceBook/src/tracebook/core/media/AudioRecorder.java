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
    private MediaRecorder recorder = new MediaRecorder();

    /*
     * (non-Javadoc)
     * 
     * @see core.media.Recorder#start()
     */
    @Override
    public String start() {
        if (isRecording) {
            return null;
        }

        filename = getNewFilename();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // Possible output formats are 3gpp and MPEG4, e. g.
        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(getPath());

        try {
            recorder.prepare();
            recorder.start();
            isRecording = true;

            return filename;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
