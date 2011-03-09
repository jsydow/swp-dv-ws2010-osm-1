package gui;

import Trace.Book.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import core.data.DataMapObject;
import core.data.DataStorage;
import core.data.MetaMedia;

public class AddMemoActivity extends Activity {

    MetaMedia mm = new MetaMedia();
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

    public void startMemo() {
        final ProgressDialog dialog = ProgressDialog.show(this,
                "Bitte warten..", "Aufnahme beginnt in 3 Sekunden...", true,
                false);
        new Thread() {
            @Override
            public void run() {
                try {

                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                dialog.dismiss();
                mm.startAudio();
            }
        }.start();

    }

    public void stopMemoBtn(View view) {
        final Intent intent = new Intent(this, NewTrackActivity.class);
        startActivity(intent);
        mm.stopAudio(node);
        finish();
    }

}
