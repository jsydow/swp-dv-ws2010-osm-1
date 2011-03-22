package tracebook.gui.activity;

import java.io.IOException;

import tracebook.core.data.DataMapObject;
import tracebook.core.data.DataStorage;
import tracebook.core.media.VideoRecorder;
import tracebook.util.LogIt;
import Trace.Book.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

/**
 * Activity that starts recording a video and stops recording it upon hitting a
 * button each. This activity is closed automatically once recording the video
 * has been stopped.
 */
public class RecordVideoActivity extends Activity implements
        SurfaceHolder.Callback {

    private DataMapObject node;
    private SharedPreferences preferences;
    private VideoRecorder recorder = new VideoRecorder();
    private SurfaceHolder surfaceHolder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle extras = getIntent().getExtras();

        if (extras != null) {
            int nodeId = extras.getInt("DataNodeId");
            node = DataStorage.getInstance().getCurrentTrack()
                    .getDataMapObjectById(nodeId);
        }

        preferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        setContentView(R.layout.activity_recordvideoactivity);
        setTitle(R.string.string_startActivity_title);

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.sfv_recordvideoActivity_camera);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void onDestroy() {
        stopRecording();
        super.onDestroy();
    }

    /**
     * This function is called when the "Start recording" button is clicked.
     * 
     * @param view
     *            Not used.
     */
    public void onRecordBtn(View view) {
        if (!recorder.isRecording()) {
            recorder.start();
        }
    }

    /**
     * This function is called when the "Stop recording" button is clicked.
     * 
     * @param view
     *            Not used.
     */
    public void onRecordStop(View view) {
        if (recorder.isRecording()) {
            stopRecording();
        }

        finish();
    }

    @Override
    public void onStop() {
        stopRecording();
        super.onStop();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        // Does nothing. Literally.
    }

    public void surfaceCreated(SurfaceHolder holder) {
        int maxDuration = 60 * Integer.parseInt(preferences.getString(
                "lst_maxVideoRecording", "0"));

        try {
            recorder.prepare(maxDuration, holder.getSurface());
        } catch (IOException e) {
            LogIt.e("TraceBook", e.toString());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Does nothing. Literally.
    }

    private void stopRecording() {
        if (recorder.isRecording()) {
            recorder.stop();
            recorder.appendFileToObject(node);

            // TODO: l10n!
            Toast.makeText(this, "Recording successfully.", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
