package gui;

import Trace.Book.R;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import core.data.DataStorage;

/**
 * The Class LoadTrackActivity.
 * 
 * @author greenTraxas
 */
public class LoadTrackActivity extends ListActivity {

    /**
     * @author js
     * 
     */
    public static class LoadTrackArrayAdapter extends ArrayAdapter<String> {
        /**
         * @param context
         */
        LoadTrackArrayAdapter(Context context) {
            super(context, R.layout.loadtrack_listitem,
                    R.id.load_track_item_text, DataStorage.getInstance()
                            .getAllTracks());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            String trackname = ((TextView) (view
                    .findViewById(R.id.load_track_item_text))).getText()
                    .toString();
            view.findViewById(R.id.load_track_delete_button).setTag(trackname);
            return view;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListAdapter(new LoadTrackArrayAdapter(this));

    }

    /**
     * @param v
     * 
     */
    public void deleteTrackBtn(View v) {
        if (v == null) {
            Log.w("BUTTON", "view is null");
            return;
        }
        if (v.getTag() == null) {
            Log.w("BUTTON", "tag is null");
            return;
        }
        Log.w("BUTTON", (String) v.getTag());
    }

}
