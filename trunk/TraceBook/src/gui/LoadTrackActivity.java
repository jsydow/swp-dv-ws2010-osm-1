package gui;

import Trace.Book.R;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import core.data.DataStorage;
import core.data.DataTrack;

/**
 * The Class LoadTrackActivity.
 * 
 * @author greenTraxas
 */
public class LoadTrackActivity extends ListActivity {

    /**
     * 
     */
    LoadTrackArrayAdapter adapter;

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

        adapter = new LoadTrackArrayAdapter(this);
        setListAdapter(adapter);

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
        deleteTrack((String) v.getTag());
    }

    private void deleteTrack(final String trname) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(
                getResources().getString(R.string.alert_really_delete_track))
                .setCancelable(false)
                .setPositiveButton(
                        getResources().getString(R.string.yes_alert),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                DataTrack.delete(trname);
                                Log.d("DEBUG", "delete " + trname);
                                // updateAdapter();
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.no_alert),
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                    int which) {
                                dialog.cancel();
                            }
                        });
        builder.show();
    }

    /**
     * 
     */
    void updateAdapter() {
        adapter.clear();
        for (String name : DataStorage.getInstance().getAllTracks()) {
            adapter.add(name);
        }
        adapter.notifyDataSetChanged();
    }

}
