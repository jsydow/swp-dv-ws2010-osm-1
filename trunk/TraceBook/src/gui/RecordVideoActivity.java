package gui;

import java.io.File;
import java.io.IOException;

import Trace.Book.R;
import android.app.Activity;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import core.data.DataStorage;
import core.data.MetaMedia;

public class RecordVideoActivity extends Activity implements
        SurfaceHolder.Callback {

    SurfaceHolder surfaceHolder;
    Camera camera;
    MediaRecorder recorder;

    MetaMedia media;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.recordvideoactivity);

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surface_camera);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // media.startVideo(surfaceView.getHolder().getSurface());
    }

    public void onRecordStop(View view) {
        // recorder.stop();
        // camera.lock();

        media.stopVideo(DataStorage.getInstance().getCurrentTrack()
                .getCurrentWay());

    }

    public void onRecordBtn(View view) {

        recorder = new MediaRecorder();

        recorder.setCamera(camera);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);

        recorder.setMaxDuration(1000 * 60);

        File tempFile = new File(DataStorage.getInstance().getCurrentTrack()
                .getTrackDirPath(), "video.3gp");

        recorder.setOutputFile(tempFile.getPath());

        recorder.setVideoFrameRate(15);
        recorder.setVideoSize(320, 240);

        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);

        recorder.setPreviewDisplay(surfaceHolder.getSurface());

        // recorder.setMaxFileSize(maxFileSizeInBytes);

        try {
            recorder.prepare();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        recorder.start();

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

    public void surfaceCreated(SurfaceHolder arg0) {
        // TODO Auto-generated method stub

        camera = Camera.open();
        if (camera != null) {
            Camera.Parameters params = camera.getParameters();
            camera.setParameters(params);
        } else {
            // Toast.makeText(getApplicationContext(), "Camera not available!",
            // Toast.LENGTH_LONG).show();
            finish();
        }

    }

    public void surfaceDestroyed(SurfaceHolder arg0) {

    }

}
