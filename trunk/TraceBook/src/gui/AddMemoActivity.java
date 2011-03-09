package gui;

import Trace.Book.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import core.data.DataMapObject;
import core.data.DataStorage;
import core.data.MetaMedia;

/**
 * @author greentraxas
 * 
 */
public class AddMemoActivity extends Activity {

    /**
     * MetaMedia object to create new media objects and to receive it.
     */
    MetaMedia mm = new MetaMedia();

    /**
     * Here we save a reference to the current DataMapObject which is in use.
     */
    DataMapObject node;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle extras = getIntent().getExtras();

        if (extras != null) {
            int nodeId = extras.getInt("DataNodeId");
            node = DataStorage.getInstance().getCurrentTrack()
                    .getDataMapObjectById(nodeId);
        }

        setContentView(R.layout.addmemoactivity);

        startMemo();

    }

    /**
     * This method show for 2 seconds a progressDialog. After 2 Seconds the
     * recording will be start.
     */
    public void startMemo() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMessage("Bitte warten...\nAufnahme startet gleich...");
        dialog.setCancelable(false);
        dialog.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    int step = 0;
                    while (step < 50) {
                        Thread.sleep(2000 / 50);
                        step++;
                        dialog.incrementProgressBy(2);
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                dialog.dismiss();
                mm.startAudio();
            }
        }.start();

    }

    /**
     * This method stop the audio recording and finish the Acitivty.
     * 
     * @param view
     */
    public void stopMemoBtn(View view) {
        mm.stopAudio(node);
        finish();
    }

}
