package gui;

import java.util.List;

import Trace.Book.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import core.data.DataNode;
import core.data.DataPointsList;
import core.data.DataStorage;
import core.data.DataTrack;
import core.logger.ServiceConnector;
import core.media.PictureRecorder;
import core.media.Recorder;

/**
 * The NewTrackActivity is the main activity to record , edit and see your ways,
 * areas and POIS. The activity is divided in three part via tabs. The first one
 * is the map view where you can see your collected way points in a convenient
 * way. The second one is the main tab where you can set the your POI's, ways
 * and areas. In the third one you can choose your collected, add new tags,
 * remove tags remove pois etc.
 * 
 * @author greentraxas
 * 
 * 
 */
public class NewTrackActivity extends TabActivity {

    /**
     * @author greentraxas The purpose of this OnTabListener is to update the
     *         different tab views when changing the tabs. So you see always an
     *         updated view of your data.
     */
    static class MyListener implements OnTabChangeListener {

        /**
         * We use this to have a reference of our NewTrackActivity.
         */
        NewTrackActivity act;

        /**
         * Here we save a reference to our tab object in our NewTracActivity.
         */
        TabHost tab;

        /**
         * We use a dirty trick to have a reference to our NewTracActivty and to
         * our TabHost which is associated to it.
         * 
         * @param act
         * @param tab
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
     * Create activity.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_newtrackactivity);

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
    protected void onResume() {
        super.onResume();
        initListView();
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

    /**
     * This method set the visibilty of the MediaButtons at the bottom of the
     * activity for street and area mapping. In top of the Buttons, the textview
     * mediaData signals the user for for what typ of mapping the mediaData will
     * be saved.
     * 
     * @param active
     *            turn the visibilty of the buttonlist and textview on/off
     * @param button
     *            signals the method which button was selected. 1 for street
     *            togglebutton 2 for area toggle button and else 0
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
     * Init ListView with the list of saved POI, streets and areas.
     */
    void initListView() {
        final Intent intent = new Intent(this, AddPointActivity.class);
        // Init ListView for EditTab
        ListView listView = (ListView) findViewById(R.id.tracks_lvw);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getPOIList());
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);

        // Get selected item and send toast
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                String itemText = adapter.getItem(position);
                String[] cut = itemText.split(": ");

                intent.putExtra("DataNodeId", Integer.parseInt(cut[0]));

                startActivity(intent);
                Toast.makeText(getApplicationContext(),
                        ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * Init Tabhost with all three tabs: 1. mapView (mapsforge) 2. NewTab 3.
     * EditTab
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
     * Method is called if startWay-Togglebutton pressed. Start and stop way
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
     * Method is called if startArea-Togglebutton pressed. Start and stop area
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
                ServiceConnector.getLoggerService().beginArea();

            } catch (RemoteException e) {

                e.printStackTrace();
            }
        } else {
            streetToggle.setClickable(true);
            setButtonList(false, 0);
            try {
                ServiceConnector.getLoggerService().endArea();
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
     *            unused
     */
    public void stopTrackBtn(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.alert_global_exit))
                .setCancelable(false)
                .setPositiveButton(
                        getResources().getString(R.string.alert_global_yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                setTrackName();

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
     * Set Trackname for the currentTrack. Finish NewTrackActivity.
     */
    void setTrackName() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        alert.setView(input);
        alert.setTitle(getResources().getString(
                R.string.alert_newtrackActivity_setTrackName));
        input.setHint(DataStorage.getInstance().getCurrentTrack().getName());
        alert.setPositiveButton(
                getResources().getString(R.string.alert_global_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString().trim();
                        if (!value.equals("")) {
                            DataStorage.getInstance().getCurrentTrack()
                                    .setName(value);
                        }

                        Toast.makeText(
                                getApplicationContext(),
                                getResources()
                                        .getString(
                                                R.string.alert_newtrackActivity_trackName)
                                        + " " + value, Toast.LENGTH_SHORT)
                                .show();

                        finish();
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
                        Toast.makeText(
                                getApplicationContext(),
                                getResources().getString(
                                        R.string.alert_global_addedNotice)
                                        + " " + value, Toast.LENGTH_SHORT)
                                .show();
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

    /**
     * Generate String Array for ListView to list all POI's Way's and Area's
     * which available at the actual track.
     * 
     * @return String Array of all POI's, Area's and Way's at the actual track
     */
    public String[] getPOIList() {

        List<DataNode> nodeList = DataStorage.getInstance().getCurrentTrack()
                .getNodes();
        List<DataPointsList> wayList = DataStorage.getInstance()
                .getCurrentTrack().getWays();
        String[] poiList = new String[nodeList.size() + wayList.size()];
        int i = 0;
        for (DataNode dn : nodeList) {
            poiList[i] = dn.getId() + ": "
                    + getResources().getString(R.string.string_global_poi);
            i++;
        }

        for (DataPointsList wl : wayList) {
            if (wl.isArea())
                poiList[i] = wl.getId() + ": "
                        + getResources().getString(R.string.string_global_area);
            else
                poiList[i] = wl.getId() + ": "
                        + getResources().getString(R.string.string_global_way);
            i++;
        }

        return poiList;
    }

    @Override
    public void finish() {
        try {
            ServiceConnector.getLoggerService().stopTrack();
        } catch (RemoteException e) {
            Log.w("StopTrack", "Could not connect to LoggerService.");
        }
        super.finish();
    }
}
