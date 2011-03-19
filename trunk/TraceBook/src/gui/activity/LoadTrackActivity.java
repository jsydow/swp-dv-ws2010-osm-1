package gui.activity;

import gui.adapter.GenericAdapter;
import gui.adapter.GenericAdapterData;
import gui.adapter.GenericItemDescription;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Trace.Book.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import core.data.DataStorage;
import core.data.DataTrack;
import core.data.DataTrackInfo;

/**
 * The Class LoadTrackActivity list all saved Track in a list view. With a
 * context menu the user have following options:
 * 
 * 1. delete a track 2. rename a track 3. show all track information 4. load a
 * track
 * 
 */
public class LoadTrackActivity extends ListActivity {

    /**
     * GenericAdapter for our ListView which we use in this activity.
     */
    GenericAdapter adapter;

    /**
     * 
     */
    boolean sortByName = true;
    /**
     * 
     */
    String searchText = "";

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateAdapter();
        setTitle(R.string.string_loadtrackActivity_title);
        setContentView(R.layout.layout_loadtrackactivity);
        registerForContextMenu(getListView());

        EditText etFilter = (EditText) findViewById(R.id.et_loadtrackactivity_filter);
        if (etFilter == null) {
            Log.e("##", "etfilter is null");
            return;
        }
        etFilter.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                // nothing done here
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                // nothing done here
            }

            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
                searchText = s.toString();
                adapter.getFilter().filter(searchText);
            }

        });
    }

    /**
     * Create ContextMenu for this activity.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contextmenu_loadtrackactivity, menu);
        menu.setHeaderIcon(android.R.drawable.ic_menu_compass);
        menu.setHeaderTitle(getResources().getString(
                R.string.cm_loadtrackActivity_title));
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
        case R.id.cm_loadtrackActivity_load:
            DataTrack track = DataStorage.getInstance().deserialiseTrack(
                    trackname);
            if (track != null) {
                DataStorage.getInstance().setCurrentTrack(track);
                final Intent intent = new Intent(this, NewTrackActivity.class);
                startActivity(intent);
            } else {
                Log.e("RenameTrack",
                        "Track to load was not found or is corrupt.");
                Toast.makeText(
                        getApplicationContext(),
                        "Track to load could not be opened. Missing or corrupt.",
                        Toast.LENGTH_SHORT).show();
            }

            return true;

        case R.id.cm_loadtrackActivity_rename:

            final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            final EditText input = new EditText(this);
            alert.setView(input);
            alert.setTitle(getResources().getString(
                    R.string.alert_loadtrackActivity_rename));
            alert.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                int whichButton) {
                            String value = input.getText().toString().trim();

                            int res = DataTrack.rename(trackname, value);
                            switch (res) {
                            case 0:
                                break;
                            case -1:
                                Log.e("RenameTrack",
                                        "Track to rename was not found or is corrupt.");
                                break;
                            case -2:
                                Log.e("RenameTrack",
                                        "There is already a track with this name.");
                                break;
                            case -3:
                                Log.e("RenameTrack",
                                        "Track could not be renamed.");
                                break;
                            default:
                                break;
                            }
                            /*
                             * DataTrack renametrack = DataTrack
                             * .deserialise(trackname); if (renametrack != null)
                             * { if(renametrack.setName(value) != 0) {
                             * 
                             * } } else { Log.e("RenameTrack",
                             * "Track to rename was not found or is corrupt.");
                             * Toast.makeText(getApplicationContext(),
                             * "Track to rename could not be opened.",
                             * Toast.LENGTH_SHORT).show(); }
                             */

                            updateAdapter();

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

        case R.id.cm_loadtrackActivity_info:
            DataTrackInfo trackinfo = DataTrackInfo.deserialise(trackname);

            final Dialog infoDialog = new Dialog(this);
            // dialog.getWindow().setGravity(Gravity.FILL);
            infoDialog.setContentView(R.layout.dialog_trackinfo);
            infoDialog.setTitle(R.string.string_trackInfoDialog_title);
            infoDialog.setCancelable(true);

            // set up name
            TextView textname = (TextView) infoDialog
                    .findViewById(R.id.tv_trackInfoDialog_name);
            textname.setText(trackinfo.getName());

            // set up comment
            TextView textcomment = (TextView) infoDialog
                    .findViewById(R.id.tv_trackInfoDialog_comment);
            textcomment.setText(trackinfo.getComment());

            // set up time
            TextView texttime = (TextView) infoDialog
                    .findViewById(R.id.tv_trackInfoDialog_timestamp);
            texttime.setText(trackinfo.getTimestamp());

            // set up pois
            TextView textpois = (TextView) infoDialog
                    .findViewById(R.id.tv_trackInfoDialog_pois);
            textpois.setText(Integer.toString(trackinfo.getNumberOfPOIs()));

            // set up ways
            TextView textways = (TextView) infoDialog
                    .findViewById(R.id.tv_trackInfoDialog_ways);
            textways.setText(Integer.toString(trackinfo.getNumberOfWays()));

            // set up media
            TextView textmedia = (TextView) infoDialog
                    .findViewById(R.id.tv_trackInfoDialog_media);
            textmedia.setText(Integer.toString(trackinfo.getNumberOfMedia()));

            // set up button
            Button button = (Button) infoDialog
                    .findViewById(R.id.btn_trackInfoDialog_back);
            button.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    infoDialog.cancel();
                }
            });
            // now that the dialog is set up, it's time to show it
            infoDialog.show();

            return true;
        case R.id.cm_loadtrackActivity_delete:
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(
                    getResources().getString(
                            R.string.alert_loadtrackActivity_deleteTrack))
                    .setCancelable(false)
                    .setPositiveButton(
                            getResources().getString(R.string.alert_global_yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int id) {

                                    DataTrack.delete(trackname);
                                    // may crash here (did so previously).
                                    updateAdapter();

                                }
                            })
                    .setNegativeButton(
                            getResources().getString(R.string.alert_global_no),
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface d, int which) {
                                    d.cancel();
                                }
                            });
            builder.show();
            break;
        default:
            break; // do nothing
        }
        return super.onContextItemSelected(item);
    }

    /**
     * @param v
     *            used to get the text of the view. Could be dangerous.
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
                getResources().getString(
                        R.string.alert_loadtrackActivity_deleteTrack))
                .setCancelable(false)
                .setPositiveButton(
                        getResources().getString(R.string.alert_global_yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                DataTrack.delete(trname);
                                Log.d("DEBUG", "delete " + trname);
                                // updateAdapter();
                            }
                        })
                .setNegativeButton(
                        getResources().getString(R.string.alert_global_no),
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
        final Activity thisActivity = this;
        (new Thread() {
            @Override
            public void run() {
                GenericItemDescription desc = new GenericItemDescription();

                desc.addResourceId("TrackName", R.id.tv_listviewloadtrack_track);
                desc.addResourceId("TrackComment",
                        R.id.tv_listviewloadtrack_comment);
                desc.setNameTag("TrackName");
                String comment = null;
                final List<GenericAdapterData> data = new ArrayList<GenericAdapterData>();
                DataStorage.getInstance().unloadAllTracks();

                // get all TrackInfo-objects
                List<DataTrackInfo> trackInfos = new ArrayList<DataTrackInfo>();
                List<String> names = new ArrayList<String>(DataStorage
                        .getInstance().getAllTracks());
                for (String name : names) {
                    DataTrackInfo trackinfo = DataTrackInfo.deserialise(name);
                    if (trackinfo != null) {
                        trackInfos.add(trackinfo);
                    }
                }

                // sort
                if (sortByName) {
                    Collections.sort(trackInfos,
                            new Comparator<DataTrackInfo>() {
                                public int compare(DataTrackInfo arg0,
                                        DataTrackInfo arg1) {
                                    return arg0.getName().compareToIgnoreCase(
                                            arg1.getName());
                                }
                            });
                } else {
                    Collections.sort(trackInfos,
                            new Comparator<DataTrackInfo>() {
                                public int compare(DataTrackInfo arg0,
                                        DataTrackInfo arg1) {
                                    return arg0.getTimestamp()
                                            .compareToIgnoreCase(
                                                    arg1.getTimestamp());
                                }
                            });
                }

                // fill adapter
                for (DataTrackInfo trackinfo : trackInfos) {
                    GenericAdapterData dataItem = new GenericAdapterData(desc);
                    dataItem.setText("TrackName", trackinfo.getName());

                    if (trackinfo.getComment().length() > 80) {
                        comment = "Kommentar: "
                                + trackinfo.getComment().trim()
                                        .substring(0, 77) + "...";
                    } else if (trackinfo.getComment().length() > 0) {
                        comment = "Kommentar: " + trackinfo.getComment()
                                + "...";
                    } else {
                        comment = "Kein Kommentar vorhanden";
                    }

                    dataItem.setText("TrackComment", comment);

                    data.add(dataItem);

                }

                thisActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                        adapter = new GenericAdapter(thisActivity,
                                R.layout.listview_loadtrack, R.id.list, data,
                                layoutInflater);
                        adapter.getFilter().filter(searchText);

                        setListAdapter(adapter);

                        getListView().setTextFilterEnabled(true);

                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    /**
     * If a ListItem selected, the method will load the track.
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        GenericAdapterData data = adapter.getItem(position);
        final String trackname = data.getText("TrackName");
        DataTrack track = DataStorage.getInstance().deserialiseTrack(trackname);
        if (track != null) {
            DataStorage.getInstance().setCurrentTrack(track);
            final Intent intent = new Intent(this, NewTrackActivity.class);
            startActivity(intent);
        } else {
            Log.e("RenameTrack", "Track to load was not found or is corrupt.");
            Toast.makeText(getApplicationContext(),
                    "Track to load could not be opened. Missing or corrupt.",
                    Toast.LENGTH_SHORT).show();
        }
        super.onListItemClick(l, v, position, id);
    }

    @Override
    protected void onResume() {
        updateAdapter();
        super.onResume();
    }
}
