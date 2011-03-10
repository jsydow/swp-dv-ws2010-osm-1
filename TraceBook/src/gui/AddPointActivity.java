package gui;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import Trace.Book.R;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
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
     * We save our RowData, to fill the CustomAdapter with this.
     */
    RowData rd;

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
     * MetaMedia object to create new media objects and to receive it.
     */
    // MetaMedia mm;

    /**
     * CustomAdapter for our ListView which we use in this activity.
     */
    CustomAdapter adapter;

    /**
    * 
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
        Vector<RowData> data = new Vector<RowData>();
        for (int i = 0; i < category.length; i++) {
            try {
                rd = new RowData(i, category[i], value[i]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            data.add(rd);
            adapter = new CustomAdapter(this, R.layout.addpointlistview,
                    R.id.list, data);
            setListAdapter(adapter);
            getListView().setTextFilterEnabled(true);
        }
    }

    /**
     * If a User select a item, we start the AddPointMetaActivty to edit the
     * MetaData for this Node. The Intent get the Extras: 1. DataNodeId 2.
     * DataNodeKey (CategoryKey of the selected node) 3. DataNodeValue (the
     * Value for the CategoryKey of the selected node)
     */
    @Override
    public void onListItemClick(ListView parent, View v, int position, long id) {
        final Intent intent = new Intent(this, AddPointMetaActivity.class);
        RowData rowData = adapter.getItem(position);
        Toast.makeText(
                getApplicationContext(),
                "You have selected " + (position + 1) + "th item" + " "
                        + rowData.toString(), Toast.LENGTH_SHORT).show();

        intent.putExtra("DataNodeId", node.getId()); //
        intent.putExtra("DataNodeKey", rowData.mTitle);
        intent.putExtra("DataNodeValue", rowData.mDetail);
        startActivity(intent);

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

    /**
     * This class save our RowData for the CustomAdapter. We save the title
     * (Category) and the detail (Value). Later we can save although imageIds,
     * to display images for each entry at our ListView
     */
    private class RowData {
        protected int mId;
        protected String mTitle;
        protected String mDetail;

        RowData(int id, String title, String detail) {
            mId = id;
            mTitle = title;
            mDetail = detail;
        }

        @Override
        /**
         * The Method build a String out of our RowData 
         * Useful for Toast or other notification's.
         */
        public String toString() {
            return mId + " " + mTitle + " " + mDetail;
        }
    }

    /**
     * Our CustomAdapter extends the normal ArrayAdapter with out RowData.
     * 
     * @author greenTraxas
     * 
     */
    private class CustomAdapter extends ArrayAdapter<RowData> {
        public CustomAdapter(Context context, int resource,
                int textViewResourceId, List<RowData> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        /**
         * For every View we used in our CustomAdapter (addpointlistview), we
         * set the text in this method. The text is comming out of the RowData
         * at the position of the adapter.
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            TextView title = null;
            TextView detail = null;

            RowData rowData = getItem(position);
            if (null == convertView) {
                convertView = mInflater
                        .inflate(R.layout.addpointlistview, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            holder = (ViewHolder) convertView.getTag();
            title = holder.gettitle();
            title.setText(rowData.mTitle);
            detail = holder.getdetail();
            detail.setText(rowData.mDetail);
            return convertView;
        }
    }

    /**
     * We need a ViewHolder to get easily the views out of the used layout.
     * 
     * @author greenTraxas
     * 
     */
    private class ViewHolder {
        private View mRow;
        private TextView title = null;
        private TextView detail = null;

        public ViewHolder(View row) {
            mRow = row;
        }

        public TextView gettitle() {
            if (null == title) {
                title = (TextView) mRow.findViewById(R.id.title);
            }
            return title;
        }

        public TextView getdetail() {
            if (null == detail) {
                detail = (TextView) mRow.findViewById(R.id.detail);
            }
            return detail;
        }
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
