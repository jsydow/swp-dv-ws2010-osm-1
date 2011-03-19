package gui.activity;

import gui.adapter.GenericAdapter;
import gui.adapter.GenericAdapterData;
import gui.adapter.GenericItemDescription;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import util.LogIt;
import Trace.Book.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import core.data.DataMapObject;
import core.data.DataStorage;
import core.logger.ServiceConnector;
import core.media.PictureRecorder;
import core.media.Recorder;

/**
 * The purpose of this activity is to add and edit tags to an DataMapObject
 * where an DataMapObject can be anything from POI to area.
 * 
 */
public class AddPointActivity extends ListActivity {

    /**
     * Here we use the LayoutInflater to inflate the Layout for the ListView.
     */
    private LayoutInflater mInflater;

    /**
     * Here we save a reference to the current DataMapObject which is in use.
     */
    DataMapObject node;

    /**
     * GenericAdapter for our ListView which we use in this activity.
     */
    GenericAdapter adapter;

    /**
     * PicutreRecoder to take pictures.
     */
    PictureRecorder pictureRecorder = new PictureRecorder();

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
                if (node == null) {
                    LogIt.popup(this, "Node does not exist!");
                    finish();
                }
            }
        } else
            finish();

        setContentView(R.layout.layout_addpointactivity);
        setTitle(R.string.string_addpointActivity_title);

        LayoutInflater bInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layoutHolder = (LinearLayout) findViewById(R.id.ly_addpointaAtivity_metaMediaBtnPoint);
        bInflater.inflate(R.layout.dynamic_metamediabuttons, layoutHolder);

        // Initial ServiceConnector
        ServiceConnector.startService(this);
        ServiceConnector.initService();

        registerForContextMenu(getListView());
        setNodeInformation();

        // Initial Adapter
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initAdapter();
    }

    /**
     * Initialization of our CustomAdapter and fill the RowData with the
     * MetaInformation out of the two StringArrays category and value. Set this
     * Adapter for our ListView.
     */
    private void initAdapter() {
        GenericItemDescription desc = new GenericItemDescription();

        desc.addResourceId("NodeKey", R.id.title);
        desc.addResourceId("NodeValue", R.id.detail);

        List<GenericAdapterData> data = new ArrayList<GenericAdapterData>();
        Iterator<Entry<String, String>> iterator = node.getTags().entrySet()
                .iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, String> pairs = iterator.next();

            GenericAdapterData item = new GenericAdapterData(desc);
            item.setText("NodeKey", pairs.getKey());
            item.setText("NodeValue", pairs.getValue());

            data.add(item);
        }
        adapter = new GenericAdapter(this, R.layout.listview_addpoint,
                R.id.list, data, mInflater);

        setListAdapter(adapter);
        getListView().setTextFilterEnabled(true);
    }

    /**
     * Create ContextMenu for this activity.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contextmenu_addpointactivity, menu);
        menu.setHeaderIcon(android.R.drawable.ic_menu_edit);
        menu.setHeaderTitle(getResources().getString(
                R.string.cm_addpointActivity_title));
    }

    /**
     * This method create the ContextMenu for the Item which was selected. 1.
     * deleteTag: delete the selected MetaTag from the node. 2. renameTag: open
     * AddPointMetaActivity to rename the selected MetaTag. Fill the Adapter
     * with the new MetaData and draw the ListView again.
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();

        GenericAdapterData itemData = adapter.getItem((int) info.id);

        switch (item.getItemId()) {
        case R.id.cm_addpointActivity_deleteTag:

            node.getTags().remove(itemData.getText("NodeKey"));
            setNodeInformation();
            initAdapter();
            adapter.notifyDataSetChanged();
            return true;
        case R.id.cm_addpointActivity_renameTag:
            final Intent intent = new Intent(this, AddPointMetaActivity.class);

            intent.putExtra("DataNodeId", node.getId());
            intent.putExtra("DataNodeKey", itemData.getText("NodeKey"));
            intent.putExtra("DataNodeValue", itemData.getText("NodeValue"));
            startActivity(intent);

            //$FALL-THROUGH$
        default:
            return super.onContextItemSelected(item);
        }
    }

    /**
     * Get the last node and create a String-Array with all MetaData from the
     * Meta-HashMap of this Node. If the HashMap contain no MetaData, the method
     * returns an empty array.
     */
    private void setNodeInformation() {
        TextView nodeIdTv = (TextView) findViewById(R.id.tv_addpointActivity_nodeId);
        TextView nodeInfo = (TextView) findViewById(R.id.tv_addpointActivity_allocateMeta);
        TextView titleCat = (TextView) findViewById(R.id.tv_addpointActivity_titleListViewCat);
        TextView titleVal = (TextView) findViewById(R.id.tv_addpointActivity_titleListViewVal);

        Map<String, String> tagMap = node.getTags();
        nodeIdTv.setText(getResources().getString(
                R.string.tv_addpointActivity_nodeId)
                + " " + node.getId());

        if (tagMap.size() != 0) {
            nodeInfo.setText(R.string.tv_addpointActivity_MetaData);
            titleCat.setVisibility(1);
            titleVal.setVisibility(1);

        } else {
            nodeInfo.setText(R.string.tv_addpointActivity_noMetaData);
            titleCat.setVisibility(8);
            titleVal.setVisibility(8);

        }

    }

    /**
     * The Method for the AddPointMeta Button, to save MetaData for this node.
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
     * The Method for the cancel Button to finish the Activity.
     * 
     * @param view
     *            not used
     */
    public void cancelBtn(View view) { // method signature including view is
        // required
        finish();
    }

    /**
     * The Method for the makeVideo Button (MediaTags) to start the
     * RecordActivity.
     * 
     * @param view
     *            not used
     */
    public void makeVideoBtn(View view) {
        final Intent intent = new Intent(this, RecordVideoActivity.class);
        intent.putExtra("DataNodeId", node.getId());
        startActivity(intent);
    }

    /**
     * The Method for the makeMemo Button (MediaTags) to start recording a Memo
     * at the AddMemoActivty.
     * 
     * @param view
     *            unused
     */
    public void makeMemoBtn(View view) {
        final Intent intent = new Intent(this, AddMemoActivity.class);
        intent.putExtra("DataNodeId", node.getId());
        startActivity(intent);
    }

    /**
     * This method create a AlertDailog Box to fill in the notice for the actual
     * node.
     * 
     * @param view
     *            not used
     */
    public void makeNoticeBtn(View view) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        alert.setView(input);
        alert.setTitle(getResources()
                .getString(R.string.alert_global_addNotice));
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String values = input.getText().toString().trim();

                node.addMedia(DataStorage.getInstance().getCurrentTrack()
                        .saveText(values));
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
     * The Method for the makePicture Button. The Method start the standard
     * cameraActivty.
     * 
     * @param view
     *            not used
     */
    public void makePictureBtn(View view) {
        pictureRecorder.startIntent(this);
    }

    /**
     * OnResume is called when the activity was adjournment and we come back to
     * this activity. This method update the ListInformation with the MetaTags
     * of the Node.
     */
    @Override
    protected void onResume() {
        super.onResume();
        setNodeInformation();
        initAdapter();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case Recorder.TAKE_PHOTO_CODE:
            if (resultCode == Activity.RESULT_OK) {
                pictureRecorder.appendFileToObject(node);
            }
            break;
        default:
            break;
        }
    }
}
