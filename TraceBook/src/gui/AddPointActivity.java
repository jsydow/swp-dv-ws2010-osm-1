package gui;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import Trace.Book.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import core.data.DataMapObject;
import core.data.DataMedia;
import core.data.DataStorage;
import core.data.DataTrack;
import core.data.MetaMedia;
import core.logger.ServiceConnector;

/**
 * @author greentraxas The purpose of this activity is to add and edit tags to
 *         an DataMapObject where an DataMapObject can be anything from poi to
 *         area.
 * 
 */
public class AddPointActivity extends Activity {

    /**
     * Here we save a reference to the current DataMapObject which is in use
     */
    DataMapObject node;

    /**
     * MetaMedia object to create new media objects and to receive it
     */
    MetaMedia mm;

    /**
     * ArrayAdapter object to fill the ListView with MetaInformation
     */
    ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle extras = getIntent().getExtras();

        /*
         * Get the node of the sending Intent
         */
        if (extras != null) {

            if (extras.containsKey("DataNodeId")) {
                int nodeId = extras.getInt("DataNodeId");
                node = DataStorage.getInstance().getCurrentTrack()
                        .getDataMapObjectById(nodeId);
            }

        }

        setContentView(R.layout.addpointactivity);
        mm = new MetaMedia();

        LayoutInflater bInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        LinearLayout layoutHolder = (LinearLayout) findViewById(R.id.metaMediaBtnPoint_ly);
        bInflater.inflate(R.layout.metamediabuttons, layoutHolder);

        // Init ServiceConnector
        ServiceConnector.startService(this);
        ServiceConnector.initService();

        getNodeInformation();
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getNodeInformation());
        listNodeInformation();

    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getNodeInformation());
        listNodeInformation();
    }

    /**
     * Get the last node and create a String-Array with all MetaData from the
     * Meta-HashMap of this Node. If the HashMap contain no MetaData, the method
     * returns an empty array.
     * 
     * @return A Array of Strings, for the Content of the ListeView
     *         allocateMeta_lv
     */
    private String[] getNodeInformation() {

        String meta = null;
        TextView nodeIdTv = (TextView) findViewById(R.id.nodeId_tv);
        TextView nodeInfo = (TextView) findViewById(R.id.allocateMeta_tv);
        int i = 0;

        Map<String, String> tagMap = node.getTags();
        String[] metaInformation = new String[tagMap.size()];
        nodeIdTv.setText(getResources().getString(R.string.nodeId_tv) + " "
                + node.getId());

        if (tagMap.size() != 0) {
            nodeInfo.setText(R.string.MetaData_tv);
            Iterator<Entry<String, String>> iterator = tagMap.entrySet()
                    .iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, String> pairs = iterator.next();
                meta = pairs.getKey() + " - " + pairs.getValue();
                metaInformation[i] = meta;
                i++;
            }

        } else {
            nodeInfo.setText(R.string.noMetaData_tv);

        }

        return metaInformation;
    }

    /**
     * This Method create the ListView with the gernerated Adapter
     * 
     */
    private void listNodeInformation() {
        final Intent intent = new Intent(this, AddPointMetaActivity.class);
        ListView listView = (ListView) findViewById(R.id.allocateMeta_lv);
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);

        // Get selected item and send toast
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                intent.putExtra("DataNodeId", node.getId());
                intent.putExtra("DataNodeKey", adapter.getItem(position));
                startActivity(intent);
                Toast.makeText(getApplicationContext(),
                        ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 
     * @param view
     *            not used
     */
    public void addPointMetaBtn(View view) { // method signature including view
                                             // is required
        final Intent intent = new Intent(this, AddPointMetaActivity.class);

        intent.putExtra("DataNodeId", node.getId());
        startActivity(intent);
    }

    /**
     * 
     * @param view
     *            not used
     */
    public void cancelBtn(View view) { // method signature including view is
                                       // required
        finish();
    }

    /**
     * @param view
     *            not
     */
    public void makeVideoBtn(View view) {
        String filename = mm.takeVideo(this);
        DataTrack currentTrack = DataStorage.getInstance().getCurrentTrack();
        currentTrack.getCurrentWay().addMedia(
                new DataMedia(currentTrack.getTrackDirPath(), filename));
    }

    /**
     * @param view
     */
    public void makeMemoBtn(View view) {
        final Intent intent = new Intent(this, AddMemoActivity.class);
        intent.putExtra("DataNodeId", DataStorage.getInstance()
                .getCurrentTrack().getCurrentWay().getId());
        startActivity(intent);
    }

    /**
     * @param view
     */
    public void makeNoticeBtn(View view) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        alert.setView(input);
        alert.setTitle(getResources().getString(R.string.addNotice_alert));
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString().trim();
                DataStorage.getInstance().getCurrentTrack().getCurrentWay()
                        .getTags().put("Notice", value);
                Toast.makeText(
                        getApplicationContext(),
                        getResources().getString(R.string.addNotice_alert)
                                + " " + value, Toast.LENGTH_SHORT).show();
            }
        });

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
        alert.show();

    }

    /**
     * @param view
     *            not used
     */
    public void makePictureBtn(View view) {
        String filename = mm.takePhoto(this);
        DataTrack currentTrack = DataStorage.getInstance().getCurrentTrack();
        currentTrack.getCurrentWay().addMedia(
                new DataMedia(currentTrack.getTrackDirPath(), filename));
    }

}
