package tracebook.gui.activity;

import java.io.IOException;

import tracebook.core.data.DataMapObject;
import tracebook.core.data.DataStorage;
import tracebook.core.media.VideoRecorder;
import Trace.Book.R;
import android.app.Activity;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
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

    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private VideoRecorder recorder = new VideoRecorder();
    private DataMapObject node;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle extras = getIntent().getExtras();

        if (extras != null) {
            int nodeId = extras.getInt("DataNodeId");
            node = DataStorage.getInstance().getCurrentTrack()
                    .getDataMapObjectById(nodeId);
        }

        setContentView(R.layout.layout_recordvideoactivity);
        setTitle(R.string.string_startActivity_title);

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.sfv_recordvideoActivity_camera);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /**
     * This function is called when the "Stop recording" button is clicked.
     * 
     * @param view
     *            Not used.
     */
    public void onRecordStop(View view) {
        if (recorder.isRecording()) {
            recorder.stop();
            recorder.appendFileToObject(node);

            // TODO: l10n!
            Toast.makeText(this, "Video recorded successfully.",
                    Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    /**
     * This function is called when the "Start recording" button is clicked.
     * 
     * @param view
     *            Not used.
     */
    public void onRecordBtn(View view) {
        if (recorder.isRecording()) {
            camera.unlock();
        }

        try {
            recorder.prepare(camera, surfaceHolder.getSurface());
            recorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        Camera.Parameters p = camera.getParameters();
        p.setPreviewSize(320, 240);
        p.setPreviewFormat(PixelFormat.JPEG);
        camera.setParameters(p);

        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        camera.startPreview();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();
        if (camera != null) {
            Camera.Parameters params = camera.getParameters();
            camera.setParameters(params);
        } else {
            finish();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.lock();
        camera.stopPreview();
        camera.release();
    }
}
