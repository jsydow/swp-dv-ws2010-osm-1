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
     * 
     * @param view
     */
    public void onRecordStop(View view) {
        recorder.stop();
        recorder.appendFileToObject(node);

        camera.lock();
        camera.stopPreview();

        finish();
    }

    /**
     * 
     * @param view
     */
    public void onRecordBtn(View view) {
        camera.unlock();

        try {
            recorder.prepare(camera, surfaceHolder.getSurface());
            recorder.start();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        // TODO Auto-generated method stub

        Camera.Parameters p = camera.getParameters();
        p.setPreviewSize(320, 240);
        p.setPreviewFormat(PixelFormat.JPEG);
        camera.setParameters(p);

        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            // TODO Auto-generated catch block
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

    /**
     *
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Nothing's gonna happen here.
    }

}
