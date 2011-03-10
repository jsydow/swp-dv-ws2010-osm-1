package gui;

import java.io.IOException;

import Trace.Book.R;
import android.app.Activity;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import core.data.DataMapObject;
import core.data.DataStorage;
import core.media.VideoRecorder;

/**
 * Activity that starts recording a video and stops recording it upon hitting a
 * button each. This activity is closed automatically once recording the video
 * has been stopped.
 * 
 * @author Sahin Vardar & Huy Dinh
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

        setContentView(R.layout.recordvideoactivity);

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surface_camera);

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
        camera.unlock();

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
