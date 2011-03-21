package tracebook.gui.activity;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tracebook.core.data.DataNode;
import tracebook.core.data.DataPointsList;
import tracebook.core.data.DataStorage;
import tracebook.core.data.DataTrack;
import tracebook.core.logger.ServiceConnector;
import tracebook.core.media.PictureRecorder;
import tracebook.core.media.Recorder;
import tracebook.gui.adapter.GenericAdapter;
import tracebook.gui.adapter.GenericAdapterData;
import tracebook.gui.adapter.GenericItemDescription;
import tracebook.util.Helper;
import tracebook.util.LogIt;
import Trace.Book.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * The NewTrackActivity is the main activity to record , edit and see your ways,
 * areas and POIS. The activity is divided in three part via tabs. The first one
 * is the map view where you can see your collected way points in a convenient
 * way. The second one is the main tab where you can set the your POI's, ways
 * and areas. In the third one you can choose your collected, add new tags,
 * remove tags remove POIs etc.
 */
public class NewTrackActivity extends TabActivity {

    /**
     * The purpose of this OnTabListener is to update the different tab views
     * when changing the tabs. So you see always an updated view of your data.
     * 
     * 
     */
    static class MyListener implements OnTabChangeListener {

        /**
         * We use this to have a reference of our NewTrackActivity.
         */
        NewTrackActivity act;

        /**
         * Here we save a reference to our tab object in our NewTrackActivity.
         */
        TabHost tab;

        /**
         * We use a dirty trick to have a reference to our NewTrackActivty and
         * to our TabHost which is associated to it.
         * 
         * @param act
         *            reference to the NewTrackActivity
         * @param tab
         *            reference to the TabHost if the NewTrackActivty
         */
        public MyListener(NewTrackActivity act, TabHost tab) {
            this.act = act;
            this.tab = tab;
        }

        public void onTabChanged(String tabId) {
            String currentTab = tab.getCurrentTabTag();
            if (currentTab.equals(tabId)) {
                act.initListView();
                tab.invalidate();
            }
        }
    }

    /**
     * TextView which shows the current text for media buttons.
     */
    TextView mediaData;

    /**
     * Reference to a pictureRecorder to record pictures.
     */
    PictureRecorder pictureRecorder = new PictureRecorder();

    /**
     * 
     */
    GenericAdapter adapter;

    /**
     * Create activity.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_newtrackactivity);
        setTitle(R.string.string_newtrackActivity_title);
        // Init TabHost
        initTabHost();

        // Init ListView
        initListView();

        // Init ServiceConnector
        ServiceConnector.startService(this);
        ServiceConnector.initService();

        setButtonList(false, 0);

        TabHost myTabHost = getTabHost();
        myTabHost.setOnTabChangedListener(new MyListener(this, myTabHost));

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contextmenu_editmapobjects, menu);
        menu.setHeaderIcon(android.R.drawable.ic_menu_edit);
        menu.setHeaderTitle(getResources().getString(
                R.string.cm_editmapobjects_title));
        setGpsStatus();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();

        GenericAdapterData data = adapter.getItem((int) info.id);

        int nodeId = Integer.parseInt(data.getText("NodeId"));

        switch (item.getItemId()) {
        case R.id.cm_editmapobjects_delete:
            DataStorage.getInstance().getCurrentTrack().deleteNode(nodeId);
            DataStorage.getInstance().getCurrentTrack().deleteWay(nodeId);
            initListView();
            return true;
        case R.id.cm_editmapobjects_edit:
            final Intent intent = new Intent(this, AddPointActivity.class);
            intent.putExtra("DataNodeId", nodeId);
            startActivity(intent);
            return true;
        default:
            return super.onContextItemSelected(item);
        }
    }

    private void setGpsStatus() {
        LocationManager loc = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GpsStatus gps = loc.getGpsStatus(null);
        Iterator<GpsSatellite> it = gps.getSatellites().iterator();
        int i = 0;
        float sum = 0;
        while (it.hasNext()) {
            GpsSatellite sat = it.next();
            i++;
            sum += sat.getSnr();
        }

        TextView tv = (TextView) findViewById(R.id.tv_newtrackActivity_gpsStatus);
        tv.setText(getResources().getString(
                R.string.tv_newtrackactivity_signalstrength_strength)
                + sum
                + getResources().getString(
                        R.string.tv_newtrackactivity_signalstrength_count)
                + i
                + " " + it.toString());
    }

    /**
     * This method set the visibility of the media buttons at the bottom of the
     * activity for street and area mapping. In top of the Buttons, the TextView
     * mediaData signals the user for for what type of mapping the mediaData
     * will be saved.
     * 
     * @param active
     *            turn the visibility of the ButtonList and TextView on/off
     * @param button
     *            signals the method which button was selected. 1 for street
     *            ToggleButton 2 for area toggle button and else 0
     */
    private void setButtonList(boolean active, int button) {
        int visible = 8;
        if (active)
            visible = 1;

        mediaData = (TextView) findViewById(R.id.tv_newtrackActivity_setButtonList);
        if (button == 1)
            mediaData.setText(R.string.tv_newtrackActivity_setButtonList1);
        else if (button == 2)
            mediaData.setText(R.string.tv_newtrackActivity_setButtonList2);

        mediaData.setVisibility(visible);

        LayoutInflater bInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layoutHolder = (LinearLayout) findViewById(R.id.ly_newtrackActivity_metaMediaBtnNew);
        bInflater.inflate(R.layout.dynamic_metamediabuttons, layoutHolder);

        Button makePictureBtn = (Button) layoutHolder
                .findViewById(R.id.btn_addMetaMedia_makePicture);
        makePictureBtn.setVisibility(visible);
        Button makeVideoBtn = (Button) layoutHolder
                .findViewById(R.id.btn_addMetaMedia_makeVideo);
        makeVideoBtn.setVisibility(visible);
        Button makeMemoBtn = (Button) layoutHolder
                .findViewById(R.id.btn_addMetaMedia_makeMemo);
        makeMemoBtn.setVisibility(visible);
        Button makeNoticeBtn = (Button) layoutHolder
                .findViewById(R.id.btn_addMetaMedia_makeNotice);
        makeNoticeBtn.setVisibility(visible);
    }

    /**
     * Init ListView and Adapter with the list of saved POI, streets and areas.
     * To show a customizable ListView the method use the GenericAdapter from
     * gui.adapter. The Method implements also the OnItemClickListener to edit
     * the selected item.
     */
    void initListView() {
        final Intent intent = new Intent(this, AddPointActivity.class);
        // Init ListView for EditTab
        ListView listView = (ListView) findViewById(R.id.tracks_lvw);
        registerForContextMenu(listView);
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);

        GenericItemDescription desc = new GenericItemDescription();
        desc.addResourceId("NodeId", R.id.tv_listviewedit_id);
        desc.addResourceId("NodeCoord", R.id.tv_listviewedit_coordinates);
        desc.addResourceId("NodeImg", R.id.iv_listviewedit_image);
        desc.addResourceId("NodeStats", R.id.tv_listviewedit_stats);
        desc.addResourceId("WayPOIs", R.id.tv_listviewedit_poiCount);

        List<DataNode> nodeList = DataStorage.getInstance().getCurrentTrack()
                .getNodes();

        List<DataPointsList> wayList = DataStorage.getInstance()
                .getCurrentTrack().getWays();

        List<GenericAdapterData> listData = new ArrayList<GenericAdapterData>();

        for (DataNode dn : nodeList) {
            GenericAdapterData item = new GenericAdapterData(desc);

            item.setText("NodeId", "" + dn.getId());
            item.setText(
                    "NodeCoord",
                    getResources().getString(
                            R.string.string_newtrackactivity_list_lat)
                            + nf.format(dn.getLat())
                            + getResources().getString(
                                    R.string.string_newtrackactivity_list_lon)
                            + nf.format(dn.getLon()));

            item.setImage("NodeImg", R.drawable.node_icon);
            item.setText(
                    "NodeStats",
                    getResources().getString(
                            R.string.string_newtrackactivity_list_media)
                            + dn.getMedia().size());

            listData.add(item);
        }

        for (DataPointsList dn : wayList) {
            GenericAdapterData item = new GenericAdapterData(desc);
            item.setText("NodeId", "" + dn.getId());

            if (dn.getNodes().size() > 0) {
                DataNode start = dn.getNodes().get(0);
                DataNode end = dn.getNodes().get(dn.getNodes().size() - 1);

                String endCoord = dn.isArea() ? "" : (getResources().getString(
                        R.string.string_newtrackactivity_list_end)
                        + getResources().getString(
                                R.string.string_newtrackactivity_list_lat)
                        + nf.format(end.getLat())
                        + " "
                        + getResources().getString(
                                R.string.string_newtrackactivity_list_lon) + nf
                        .format(end.getLon()));

                item.setText(
                        "NodeCoord",
                        getResources().getString(
                                R.string.string_newtrackactivity_list_start)
                                + getResources()
                                        .getString(
                                                R.string.string_newtrackactivity_list_lat)
                                + nf.format(start.getLat())
                                + " "
                                + getResources()
                                        .getString(
                                                R.string.string_newtrackactivity_list_lon)
                                + nf.format(start.getLon()) + endCoord);
            }

            item.setImage("NodeImg", dn.isArea() ? R.drawable.area_icon
                    : R.drawable.way_icon);
            item.setText(
                    "NodeStats",
                    getResources().getString(
                            R.string.string_newtrackactivity_list_media)
                            + dn.getMedia().size());
            item.setText(
                    "WayPOIs",
                    getResources().getString(
                            R.string.string_newtrackactivity_list_media)
                            + dn.getNodes().size());
            listData.add(item);

        }

        adapter = new GenericAdapter(this, R.layout.listview_edit,
                R.id.tracks_lvw, listData);

        listView.setAdapter(adapter);

        // Get selected item and send toast
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {

                GenericAdapterData data = adapter.getItem(position);
                int nodeId = Integer.parseInt(data.getText("NodeId"));
                intent.putExtra("DataNodeId", nodeId);
                startActivity(intent);
            }
        });

    }

    /**
     * Initialization the TabHost with all three tabs: 1. mapView (MapsForge) 2.
     * NewTab 3. EditTab
     */
    private void initTabHost() {
        // Init TabHost
        TabHost tabHost = getTabHost();

        // Init TabHost
        tabHost.addTab(tabHost
                .newTabSpec("map_tab")
                .setIndicator(
                        getResources().getString(
                                R.string.tab_newtrackActivity_map))
                .setContent(new Intent(this, MapsForgeActivity.class)));
        // new Intent(this, MapsForgeActivity.class))
        tabHost.addTab(tabHost
                .newTabSpec("new_tab")
                .setIndicator(
                        getResources().getString(
                                R.string.tab_newtrackActivity_new))
                .setContent(R.id.tab_newtrackActivity_new));
        tabHost.addTab(tabHost
                .newTabSpec("edit_tab")
                .setIndicator(
                        getResources().getString(
                                R.string.tab_newtrackActivity_edit))
                .setContent(R.id.tab_newtrackactivity_edit));

        // set the default tap to our MapTab
        tabHost.setCurrentTab(1);

    }

    /**
     * Method is called if startWay-ToggleButton pressed. Start and stop way
     * tracking.
     * 
     * @param view
     *            not used
     */
    public void startWayTbtn(View view) {
        ToggleButton streetToggle = (ToggleButton) findViewById(R.id.tbtn_newtrackActivity_startWay);
        ToggleButton areaToggle = (ToggleButton) findViewById(R.id.tbtn_newtrackActivity_startArea);
        String check = streetToggle.getText().toString();
        if (check.equals(streetToggle.getTextOn().toString())) {
            areaToggle.setClickable(false);
            setButtonList(true, 1);
            try {
                ServiceConnector.getLoggerService().beginWay(false);

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            areaToggle.setClickable(true);
            setButtonList(false, 0);
            try {
                ServiceConnector.getLoggerService().endWay();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method is called if startArea-ToggleButton pressed. Start and stop area
     * tracking.
     * 
     * @param view
     *            not used
     */
    public void startAreaTbtn(View view) {
        ToggleButton areaToggle = (ToggleButton) findViewById(R.id.tbtn_newtrackActivity_startArea);
        ToggleButton streetToggle = (ToggleButton) findViewById(R.id.tbtn_newtrackActivity_startWay);
        String check = areaToggle.getText().toString();
        if (check.equals(areaToggle.getTextOn().toString())) {
            streetToggle.setClickable(false);
            setButtonList(true, 2);
            try {
                ServiceConnector.getLoggerService().beginWayA(false, true);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            streetToggle.setClickable(true);
            setButtonList(false, 0);
            try {
                ServiceConnector.getLoggerService().endWay();
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * Called if the addPointButton pressed. Switch to the AddPointActivity, to
     * insert Meta-Tags for the last Node.
     * 
     * @param view
     *            not used
     */
    public void addPointBtn(View view) {
        int nodeId = 0;

        try {
            nodeId = ServiceConnector.getLoggerService().createPOI(false);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        final Intent intent = new Intent(this, AddPointActivity.class);
        intent.putExtra("DataNodeId", nodeId);
        startActivity(intent);
    }

    /**
     * 
     * Called if the stopTrackButton pressed. Stop the actual tracking and
     * returns to the main activity.
     * 
     * @param view
     *            not used
     */
    public void stopTrackBtn(View view) {

        Helper.alertStopTracking(this);

    }

    /**
     * @param view
     *            not used
     */
    public void editCommentBtn(View view) {
        final DataTrack track = DataStorage.getInstance().getCurrentTrack();
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setText(track.getComment());
        alert.setView(input);
        alert.setTitle(getResources().getString(
                R.string.alert_newtrackActivity_addTrackNotice));
        alert.setPositiveButton(
                getResources().getString(R.string.alert_global_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString().trim();

                        track.setComment(value);

                    }
                });

        alert.setNegativeButton(
                getResources().getString(R.string.alert_global_cancel),
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
     * @param view
     *            not used
     */
    public void makeVideoBtn(View view) {
        final Intent intent = new Intent(this, RecordVideoActivity.class);
        intent.putExtra("DataNodeId", DataStorage.getInstance()
                .getCurrentTrack().getCurrentWay().getId());
        startActivity(intent);
    }

    /**
     * @param view
     *            unused
     */
    public void makeMemoBtn(View view) {
        final Intent intent = new Intent(this, AddMemoActivity.class);
        intent.putExtra("DataNodeId", DataStorage.getInstance()
                .getCurrentTrack().getCurrentWay().getId());
        startActivity(intent);
    }

    /**
     * @param view
     *            unused
     */
    public void makeNoticeBtn(View view) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        alert.setView(input);
        alert.setTitle(getResources()
                .getString(R.string.alert_global_addNotice));
        alert.setPositiveButton(
                getResources().getString(R.string.alert_global_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString().trim();

                        DataStorage
                                .getInstance()
                                .getCurrentTrack()
                                .getCurrentWay()
                                .addMedia(
                                        DataStorage.getInstance()
                                                .getCurrentTrack()
                                                .saveText(value));
                        LogIt.popup(getApplicationContext(), getResources()
                                .getString(R.string.alert_global_addedNotice));
                    }
                });

        alert.setNegativeButton(
                getResources().getString(R.string.alert_global_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
        alert.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Helper.startUserNotification(this, R.drawable.ic_notification,
                NewTrackActivity.class);
        initListView();
        setGpsStatus();
        /* setGpsStatus(); */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        DataTrack dt = DataStorage.getInstance().getCurrentTrack();

        switch (requestCode) {
        case Recorder.TAKE_PHOTO_CODE:
            if (resultCode == Activity.RESULT_OK) {
                pictureRecorder.appendFileToObject(dt.getCurrentWay());
            }
            break;
        default:
            break;
        }
    }
}
