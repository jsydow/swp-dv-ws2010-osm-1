package gui;

import gui.adapter.GenericAdapter;
import gui.adapter.GenericAdapterData;
import gui.adapter.GenericItemDescription;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import Trace.Book.R;
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

/**
 * @author greentraxas The purpose of this activity is to add and edit tags to
 *         an DataMapObject where an DataMapObject can be anything from poi to
 *         area.
 * 
 */
public class AddPointActivity extends ListActivity {

    /**
     * Here we use the LayoutInflater to inflate the Layout for the ListView.
     */
    private LayoutInflater mInflater;

    /**
     * The String[] "category" save all CategoryTags of the node. First we need
     * this to list all Items in our ListView. Second to send the Tag with an
     * intent to the AddPointMetaActivity
     */
    static String[] category;

    /**
     * The String[] "value" save all ValueTags of the node. First we need this
     * to list all Items in our ListView. Second to send the Tag with an intent
     * to the AddPointMetaActivity
     */
    static String[] value;

    /**
     * The Integer[] "image" will be used for the reference to the imageIcon of
     * the used CategoryTag.
     */
    static Integer[] image;

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
            }

        }

        setContentView(R.layout.addpointactivity);

        LayoutInflater bInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layoutHolder = (LinearLayout) findViewById(R.id.metaMediaBtnPoint_ly);
        bInflater.inflate(R.layout.metamediabuttons, layoutHolder);

        // Initial ServiceConnector
        ServiceConnector.startService(this);
        ServiceConnector.initService();
        registerForContextMenu(getListView());
        getNodeInformation();

        // Initial Adapter
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initAdapter();
    }

    /**
     * Init our CustomAdapter and fill the RowData with the MetaInformation out
     * of the two Stringarrays category and value. Set this Adapter for our
     * ListView.
     */
    private void initAdapter() {
        // Vector<RowData> data = new Vector<RowData>();
        // for (int i = 0; i < category.length; i++) {
        // try {
        // rd = new RowData(i, category[i], value[i]);
        // } catch (ParseException e) {
        // e.printStackTrace();
        // }
        //
        // data.add(rd);
        // }
        // adapter = new CustomAdapter(this, R.layout.addpointlistview,
        // R.id.list,
        // data, mInflater);
        // setListAdapter(adapter);

        GenericItemDescription desc = new GenericItemDescription();

        desc.addResourceId("NodeKey", R.id.title);
        desc.addResourceId("NodeValue", R.id.detail);

        List<GenericAdapterData> data = new ArrayList<GenericAdapterData>();

        for (int i = 0; i < category.length; i++) {

            GenericAdapterData itemData = new GenericAdapterData(desc);
            itemData.SetText("NodeKey", category[i]);
            itemData.SetText("NodeValue", value[i]);

            data.add(itemData);
        }

        adapter = new GenericAdapter(this, R.layout.addpointlistview,
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
        inflater.inflate(R.menu.editmeta_contextmenu, menu);
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
        case R.id.deleteTag_cm:

            node.getTags().remove(itemData.getText("NodeKey"));
            getNodeInformation();
            initAdapter();
            adapter.notifyDataSetChanged();
            return true;
        case R.id.renameTag_cm:
            final Intent intent = new Intent(this, AddPointMetaActivity.class);

            intent.putExtra("DataNodeId", node.getId()); //
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
    private void getNodeInformation() {
        TextView nodeIdTv = (TextView) findViewById(R.id.nodeId_tv);
        TextView nodeInfo = (TextView) findViewById(R.id.allocateMeta_tv);
        TextView titleCat = (TextView) findViewById(R.id.titleListViewCat_tv);
        TextView titleVal = (TextView) findViewById(R.id.titleListViewVal_tv);
        int i = 0;

        Map<String, String> tagMap = node.getTags();
        category = new String[tagMap.size()];
        value = new String[tagMap.size()];

        nodeIdTv.setText(getResources().getString(R.string.nodeId_tv) + " "
                + node.getId());

        if (tagMap.size() != 0) {
            nodeInfo.setText(R.string.MetaData_tv);
            titleCat.setVisibility(1);
            titleVal.setVisibility(1);
            Iterator<Entry<String, String>> iterator = tagMap.entrySet()
                    .iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, String> pairs = iterator.next();
                category[i] = pairs.getKey();
                value[i] = pairs.getValue();
                i++;
            }

        } else {
            nodeInfo.setText(R.string.noMetaData_tv);
            titleCat.setVisibility(8);
            titleVal.setVisibility(8);

        }

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
        final Intent intent = new Intent(this, RecordVideoActivity.class);
        intent.putExtra("DataNodeId", node.getId());
        startActivity(intent);
    }

    /**
     * @param view
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
     */
    public void makeNoticeBtn(View view) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        alert.setView(input);
        alert.setTitle(getResources().getString(R.string.addNotice_alert));
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
     * @param view
     *            not used
     */
    public void makePictureBtn(View view) {
        pictureRecorder.startIntent(this);
    }

    /*
     * @Override protected void onActivityResult(int requestCode, int
     * resultCode, Intent intent) { switch (requestCode) { case
     * MetaMedia.TAKE_PHOTO_CODE: if (resultCode == Activity.RESULT_OK) {
     * mm.appendToObject(node); } break; case MetaMedia.TAKE_VIDEO_CODE: if
     * (resultCode == Activity.RESULT_OK) { mm.appendToObject(node); } break; }
     * }
     */

    /**
     * OnResume is called when the activity was adjournment and we come back to
     * this activity. This method update the ListInformation with the MetaTags
     * of the Node.
     */
    @Override
    protected void onResume() {
        super.onResume();
        getNodeInformation();
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

}
