package gui.activity;

import java.io.File;
import java.util.List;

import Trace.Book.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import core.data.DataStorage;
import core.data.DataTrack;
import core.data.db.TagDb;
import core.logger.ServiceConnector;

/**
 * Start activity of the application.
 * 
 * 
 */
public class StartActivity extends Activity {

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_startactivity);
        setTitle(R.string.string_startActivity_title);
        final TagDb db = new TagDb(this);

        (new Thread() {
            @Override
            public void run() {

                if (db.getRowCountForLanguage("de") < 1) {
                    db.initDbWithFile(R.raw.tags_de);
                }
                if (db.getRowCountForLanguage("en") < 1) {
                    db.initDbWithFile(R.raw.tags_en);
                }

                if (db.getRowCountForLanguage("tr") < 1) {
                    db.initDbWithFile(R.raw.tags_tr);
                }

                if (db.getRowCountForLanguage("pl") < 1) {
                    db.initDbWithFile(R.raw.tags_pl);
                }

                if (db.getRowCountForLanguage("fr") < 1) {
                    db.initDbWithFile(R.raw.tags_fr);
                }

            }
        }).start();

        // Init ServiceConnector
        ServiceConnector.startService(this);
        ServiceConnector.initService();

        // create TraceBook-folder
        File dir = new File(Environment.getExternalStorageDirectory()
                + File.separator + "TraceBook");
        if (!dir.isDirectory()) {
            if (!dir.mkdir()) {
                Log.e("TraceBookMainActiviy",
                        "Could not create TraceBook-directory");
            }
        }

    }

    /**
     * Called if the newTrack Button pressed. Start the NewTrackActivity.
     * 
     * @param view
     *            the view
     */
    public void newTrackBtn(View view) {

        try {
            ServiceConnector.getLoggerService().addTrack();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this, NewTrackActivity.class);
        startActivity(intent);

    }

    /**
     * Called if the loadTrack Button pressed. Start the LoadTrackActivity.
     * 
     * @param view
     *            the view
     */
    public void loadTrackBtn(View view) {
        Intent intent = new Intent(this, LoadTrackActivity.class);
        startActivity(intent);
    }

    /**
     * 
     * @param view
     *            not used
     */
    public void resumeTrackBtn(View view) {
        List<String> trackList = DataStorage.getInstance().getAllTracks();
        String trackname = trackList.get(trackList.size() - 1);
        DataTrack track = DataStorage.getInstance().deserialiseTrack(trackname);
        if (track != null) {
            DataStorage.getInstance().setCurrentTrack(track);
            final Intent intent = new Intent(this, NewTrackActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this,
                    getResources().getString(R.string.toast_noTrackFound),
                    Toast.LENGTH_SHORT).show();

        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    /**
     * Initialization of the option menu for the MainActivity
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionsmenu_startactivity, menu);
        return true;
    }

    /**
     * Functionality of all Optionmenuitems.
     * 
     * @param item
     *            the item
     * @return true, if successful
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final Intent intent;
        switch (item.getItemId()) {
        case R.id.opt_startActivity_close:

            try {
                ServiceConnector.getLoggerService().stopTrack();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            ServiceConnector.stopService();
            finish();
            return true;
        case R.id.opt_startActivity_about:
            intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        case R.id.opt_startActivity_preferences:
            intent = new Intent(this, PreferencesActivity.class);
            startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // ServiceConnector.releaseService();
    }

    @Override
    protected void onResume() {
        Button resume = (Button) findViewById(R.id.btn_startActivity_resumeTrack);
        resume.setVisibility(1);
        super.onResume();
    }
}
