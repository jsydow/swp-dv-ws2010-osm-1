package tracebook.gui.activity;

import java.io.IOException;

import tracebook.core.data.DataMapObject;
import tracebook.core.data.DataStorage;
import tracebook.core.media.AudioRecorder;
import tracebook.util.Helper;
import tracebook.util.LogIt;
import Trace.Book.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;

/**
 * The Class AddMemoActivity, start the recording of the notice. The user can
 * talk after a 2 seconds progress bar dialog.
 * 
 */
public class AddMemoActivity extends Activity {
    /**
     * Here we save a reference to the current DataMapObject which is in use.
     */
    DataMapObject node;

    /**
     * Preferences for this activity.
     */
    SharedPreferences preferences;
    /**
     * The object that is responsible for recording (and attaching) the audio
     * file to our data structure.
     */
    AudioRecorder recorder = new AudioRecorder();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // If status bar visible remove the activity title bar.
        if (Helper.checkStatusbarVisibility(this))
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        final Bundle extras = getIntent().getExtras();

        if (extras != null) {
            int nodeId = extras.getInt("DataNodeId");
            node = DataStorage.getInstance().getCurrentTrack()
                    .getDataMapObjectById(nodeId);
        }

        preferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        setContentView(R.layout.activity_addmemoactivity);
        setTitle(R.string.string_addmemoActivity_title);
        startMemo();
    }

    @Override
    public void onDestroy() {
        stopMemo();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        stopMemo();

        // TODO: l10n!
        LogIt.popup(this, "Recording finished.");

        super.onStop();
    }

    /**
     * This method show for 2 seconds a progressDialog. After 2 Seconds the
     * recording will be started.
     */
    public void startMemo() {
        final int maxDuration = 1000 * 60 * Integer.parseInt(preferences
                .getString("lst_maxVideoRecording", "0"));

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMessage(getResources().getString(
                R.string.alert_addmemoactivity_progressdialog));
        dialog.setCancelable(false);
        dialog.show();
        (new Thread() {
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
                    e.printStackTrace();
                }
                dialog.dismiss();

                try {
                    recorder.prepare(maxDuration);

                    if (maxDuration > 0) {
                        (new Thread() {
                            @Override
                            public void run() {
                                recorder.start();

                                try {
                                    Thread.sleep(maxDuration);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                stopMemo();
                                finish();
                            }
                        }).start();
                    } else {
                        recorder.start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * This method starts the audio recording/.
     * 
     * @param view
     *            no used
     */
    public void startMemoBtn(View view) {
        if (!recorder.isRecording()) {
            startMemo();
        }
    }

    /**
     * This method stops the audio recording and finish the {@link Activity}.
     * 
     * @param view
     *            no used
     */
    public void stopMemoBtn(View view) {
        stopMemo();

        finish();
    }

    /**
     * Stops recording the audio file and appends the new media object to our
     * node, if we were recording, at all.
     */
    void stopMemo() {
        if (recorder.isRecording()) {
            recorder.stop();
            recorder.appendFileToObject(node);
        }
    }
}