package gui;

import gui.adapter.GenericAdapter;
import gui.adapter.GenericAdapterData;
import gui.adapter.GenericItemDescription;

import java.util.ArrayList;
import java.util.List;

import Trace.Book.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import core.data.DataStorage;
import core.data.DataTrack;
import core.data.DataTrackInfo;

/**
 * The Class LoadTrackActivity.
 * 
 * @author greenTraxas
 */
public class LoadTrackActivity extends ListActivity {

    /**
     * GenericAdapter for our ListView which we use in this activity.
     */
    GenericAdapter adapter;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        updateAdapter();
        registerForContextMenu(getListView());

    }

    /**
     * Create ContextMenu for this activity.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.loadtrack_contextmenu, menu);
    }

    /**
     * This method create the ContextMenu for the Item which was selected. Fill
     * the Adapter with the new MetaData and draw the ListView again.
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();

        GenericAdapterData data = adapter.getItem((int) info.id);

        final String trackname = data.getText("TrackName");

        switch (item.getItemId()) {
        case R.id.loadTrack_load_cm:
            DataTrack track = DataStorage.getInstance().deserialiseTrack(
                    trackname);
            DataStorage.getInstance().setCurrentTrack(track);
            final Intent intent = new Intent(this, NewTrackActivity.class);
            startActivity(intent);

            return true;

        case R.id.loadTrack_rename_cm:

            final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            final EditText input = new EditText(this);
            alert.setView(input);
            alert.setTitle(getResources().getString(R.string.rename_alert));
            alert.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                int whichButton) {
                            String value = input.getText().toString().trim();

                            DataTrack.deserialise(trackname).setName(value);

                            updateAdapter();

                            Toast.makeText(
                                    getApplicationContext(),
                                    getResources().getString(
                                            R.string.addNotice_alert)
                                            + " " + value, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });

            alert.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                int whichButton) {
                            dialog.cancel();
                        }
                    });
            alert.show();

            return true;

        case R.id.loadTrack_info_cm:
            DataTrackInfo trackinfo = DataTrackInfo.deserialise(trackname);

            final Dialog infoDialog = new Dialog(this);
            // dialog.getWindow().setGravity(Gravity.FILL);
            infoDialog.setContentView(R.layout.trackinfo_dialog);
            infoDialog.setTitle(R.string.trackInfoDialog_title);
            infoDialog.setCancelable(true);

            // set up name
            TextView textname = (TextView) infoDialog
                    .findViewById(R.id.trackInfoDialog_name);
            textname.setText(trackinfo.getName());

            // set up comment
            TextView textcomment = (TextView) infoDialog
                    .findViewById(R.id.trackInfoDialog_comment);
            textcomment.setText(trackinfo.getComment());

            // set up time
            TextView texttime = (TextView) infoDialog
                    .findViewById(R.id.trackInfoDialog_timestamp);
            texttime.setText(trackinfo.getTimestamp());

            // set up pois
            TextView textpois = (TextView) infoDialog
                    .findViewById(R.id.trackInfoDialog_pois);
            textpois.setText(Integer.toString(trackinfo.getNumberOfPOIs()));

            // set up ways
            TextView textways = (TextView) infoDialog
                    .findViewById(R.id.trackInfoDialog_ways);
            textways.setText(Integer.toString(trackinfo.getNumberOfWays()));

            // set up media
            TextView textmedia = (TextView) infoDialog
                    .findViewById(R.id.trackInfoDialog_media);
            textmedia.setText(Integer.toString(trackinfo.getNumberOfMedia()));

            // set up button
            Button button = (Button) infoDialog
                    .findViewById(R.id.trackInfoDialog_backbtn);
            button.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    infoDialog.cancel();
                }
            });
            // now that the dialog is set up, it's time to show it
            infoDialog.show();

            return true;
        case R.id.loadTrack_delete_cm:
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(
                    getResources()
                            .getString(R.string.alert_really_delete_track))
                    .setCancelable(false)
                    .setPositiveButton(
                            getResources().getString(R.string.yes_alert),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int id) {

                                    DataTrack.deserialise(trackname).delete();
                                    // may crash here (did so previously).
                                    updateAdapter();

                                }
                            })
                    .setNegativeButton(
                            getResources().getString(R.string.no_alert),
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    dialog.cancel();
                                }
                            });
            builder.show();
            break;
        default:
            break;
        }
        return super.onContextItemSelected(item);
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

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        GenericItemDescription desc = new GenericItemDescription();

        desc.addResourceId("TrackName", R.id.load_track_item_text);

        List<GenericAdapterData> data = new ArrayList<GenericAdapterData>();

        for (String name : DataStorage.getInstance().getAllTracks()) {
            GenericAdapterData dataItem = new GenericAdapterData(desc);
            dataItem.setText("TrackName", name);

            data.add(dataItem);

        }

        adapter = new GenericAdapter(this, R.layout.loadtrack_listitem,
                R.id.list, data, layoutInflater);

        setListAdapter(adapter);

        getListView().setTextFilterEnabled(true);

        /*
         * for (String name : DataStorage.getInstance().getAllTracks()) {
         * adapter.add(name); }
         */
        adapter.notifyDataSetChanged();
    }
}
