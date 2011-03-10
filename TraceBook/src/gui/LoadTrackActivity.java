package gui;

import Trace.Book.R;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import core.data.DataStorage;

/**
 * The Class LoadTrackActivity.
 * 
 * @author greenTraxas
 */
public class LoadTrackActivity extends ListActivity {

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListAdapter(new ArrayAdapter<String>(this,
                R.layout.loadtrack_listitem, R.id.load_track_item_text,
                DataStorage.getInstance().getAllTracks()));
    }

    /**
     * 
     */
    public void deleteTrackBtn(View v) {
        Log.d("BUTTON", "Pressed #########################");
    }

}
