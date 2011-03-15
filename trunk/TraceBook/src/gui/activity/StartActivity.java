package gui.activity;

import java.io.File;

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
import core.logger.ServiceConnector;

/**
 * Start activity of the application.
 * 
 * @author greenTraxas
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
     * Called if the loadTrack Button pressed. Start the LoadTrackActivity. TODO
     * to be done
     * 
     * @param view
     *            the view
     */
    public void loadTrackBtn(View view) {
        Intent intent = new Intent(this, LoadTrackActivity.class);
        startActivity(intent);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    /**
     * Init optionsmenu for the MainActivity
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
}
