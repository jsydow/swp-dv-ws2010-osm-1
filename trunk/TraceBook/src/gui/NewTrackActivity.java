package gui;

import java.io.File;
import java.util.List;

import Trace.Book.R;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import core.data.DataMedia;
import core.data.DataNode;
import core.data.DataPointsList;
import core.data.DataStorage;
import core.data.DataTrack;
import core.data.MetaMedia;
import core.logger.ServiceConnector;

public class NewTrackActivity extends TabActivity {

	class MyListener implements OnTabChangeListener {

		NewTrackActivity act;
		TabHost tab;
		String editTab;
		String currentTab;

		public MyListener(NewTrackActivity act, TabHost tab) {
			this.act = act;
			this.tab = tab;
		}

		public void onTabChanged(String tabId) {
			editTab = "edit_tab";
			currentTab = tab.getCurrentTabTag();// TODO Auto-generated method
												// stub
			if (currentTab.equals(tabId)) {
				act.initListView();
				tab.invalidate();
			}
		}
	}

	TabHost myTabHost;
	TextView mediaData;
	
	MetaMedia mm = new MetaMedia();

	@Override
	/**
	 * Create activity
	 */
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.newtrackactivity);
		
		// Init TabHost
		initTabHost();

		// Init ListView
		initListView();

		// Init ServiceConnector
		ServiceConnector.startService(this);
		ServiceConnector.initService();

		setButtonList(false, 0);
		initToggleButtons();
		

		myTabHost = getTabHost();
		myTabHost.setOnTabChangedListener(new MyListener(this, myTabHost));

	}

	/**
	 * This method set the visibilty of the MediaButtons at the bottom of the
	 * activity for street and area mapping. In top of the Buttons, the textview
	 * mediaData signals the user for for what typ of mapping the mediaData will
	 * be saved.
	 * 
	 * @param active
	 *            turn the visibilty of the buttonlist and textview off/on
	 * @param button
	 *            signals the method which button was selected. 1 for street
	 *            togglebutton 2 for area toggle button and else 0
	 */
	private void setButtonList(boolean active, int button) {
		int visible = 8;
		if (active)
			visible = 1;

		mediaData = (TextView) findViewById(R.id.setButtonList_tv);
		if (button == 1)
			mediaData.setText(R.string.setButtonList1_tv);
		else if (button == 2)
			mediaData.setText(R.string.setButtonList2_tv);

		mediaData.setVisibility(visible);
		Button makePictureBtn = (Button) findViewById(R.id.makePicture_btn);
		makePictureBtn.setVisibility(visible);
		Button makeVideoBtn = (Button) findViewById(R.id.makeVideo_btn);
		makeVideoBtn.setVisibility(visible);
		Button makeMemoBtn = (Button) findViewById(R.id.makeMemo_btn);
		makeMemoBtn.setVisibility(visible);
		Button makeNoticeBtn = (Button) findViewById(R.id.makeNotice_btn);
		makeNoticeBtn.setVisibility(visible);
	}

	/**
	 * Init Toggle Buttons for startWay and startArea
	 */
	private void initToggleButtons() {

		ToggleButton startWay = (ToggleButton) findViewById(R.id.startWay_Tbtn);
		ToggleButton startArea = (ToggleButton) findViewById(R.id.startArea_Tbtn);
		
		ToggleButton streetToggle = (ToggleButton) findViewById(R.id.startWay_Tbtn);
		ToggleButton areaToggle = (ToggleButton) findViewById(R.id.startArea_Tbtn);

		try {
			boolean toggle = ServiceConnector.getLoggerService().isWayLogging();			
			startWay.setChecked(toggle);
			areaToggle.setClickable(!toggle);
			
			if( toggle )
				setButtonList(true, 1);
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			
			boolean toggle = ServiceConnector.getLoggerService().isAreaLogging();
			
			startArea.setChecked(toggle);
			streetToggle.setClickable(!toggle);
			
			if( toggle )
				setButtonList(true, 2);
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Init ListView with the list of saved POI, streets and areas
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
		tabHost.addTab(tabHost.newTabSpec("map_tab")
				.setIndicator(getResources().getString(R.string.map_tab))
				.setContent(new Intent(this, MapsForgeActivity.class)));
		// new Intent(this, MapsForgeActivity.class))
		tabHost.addTab(tabHost.newTabSpec("new_tab")
				.setIndicator(getResources().getString(R.string.new_tab))
				.setContent(R.id.new_tab));
		tabHost.addTab(tabHost.newTabSpec("edit_tab")
				.setIndicator(getResources().getString(R.string.edit_tab))
				.setContent(R.id.edit_tab));

		tabHost.setCurrentTab(1);

	}

	/**
	 * Method is called if StartWay-Togglebutton pressed. Start and stop way
	 * tracking.
	 * 
	 * @param view
	 */
	public void startWayTbtn(View view) {
		ToggleButton streetToggle = (ToggleButton) findViewById(R.id.startWay_Tbtn);
		ToggleButton areaToggle = (ToggleButton) findViewById(R.id.startArea_Tbtn);
		String check = streetToggle.getText().toString();
		if (check.equals(streetToggle.getTextOn().toString())) {
			areaToggle.setClickable(false);
			setButtonList(true, 1);
			try {
				ServiceConnector.getLoggerService().beginWay(false);

			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			areaToggle.setClickable(true);
			setButtonList(false, 0);
			try {
				ServiceConnector.getLoggerService().endWay();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method is called if StartArea-Togglebutton pressed. Start and stop area
	 * tracking.
	 * 
	 * @param view
	 */
	public void startAreaTbtn(View view) {
		ToggleButton areaToggle = (ToggleButton) findViewById(R.id.startArea_Tbtn);
		ToggleButton streetToggle = (ToggleButton) findViewById(R.id.startWay_Tbtn);
		String check = areaToggle.getText().toString();
		if (check.equals(areaToggle.getTextOn().toString())) {
			streetToggle.setClickable(false);
			setButtonList(true, 2);
			try {
				ServiceConnector.getLoggerService().beginArea();

			} catch (RemoteException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		} else {
			streetToggle.setClickable(true);
			setButtonList(false, 0);
			try {
				ServiceConnector.getLoggerService().endArea();

			} catch (RemoteException e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		}

	}

	/**
	 * Called if the addPointButton pressed. Switch to the AddPointActivity, to
	 * insert Meta-Tags for the last Node.
	 * 
	 * @param view
	 */
	public void addPointBtn(View view) {
		int nodeId = 0;

		try {
			nodeId = ServiceConnector.getLoggerService().createPOI(false);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final Intent intent = new Intent(this, AddPointActivity.class);
		intent.putExtra("DataNodeId", nodeId);
		startActivity(intent);
		finish();
	}

	/**
	 * 
	 * Called if the stopTrackButton pressed. Stop the actual tracking and
	 * returns to the main activity.
	 * 
	 * @param view
	 * @throws RemoteException
	 */
	public void stopTrackBtn(View view) {
		try {
			ServiceConnector.getLoggerService().stopTrack();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final Intent intent = new Intent(this, main.class);
		startActivity(intent);

	}

	public void makePictureBtn(View view) {
		mm.takePhoto(this);
	}

	public void makeVideoBtn(View view) {
		mm.takeVideo(this);
	}

	public void makeMemoBtn(View view) {
		
	}

	public void makeNoticeBtn(View view) {

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		DataTrack dt = DataStorage.getInstance().getCurrentTrack();
		
		switch (requestCode) {
			case MetaMedia.TAKE_PHOTO_CODE:
				if (resultCode == Activity.RESULT_OK) {
					mm.appendToObject(dt.getCurrentWay());
				}
				break;
			case MetaMedia.TAKE_VIDEO_CODE:
				if (resultCode == Activity.RESULT_OK) {
					mm.appendToObject(dt.getCurrentWay());
				}
				break;
		}
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
			poiList[i] = dn.get_id() + ": " + getResources().getString(R.string.POI);
			i++;
		}

		for (DataPointsList wl : wayList) {
			if (wl.isArea())
				poiList[i] = wl.get_id() + ": " + getResources().getString(R.string.Area);
			else
				poiList[i] = wl.get_id() + ": " + getResources().getString(R.string.Way);
			i++;
		}

		return poiList;
	}
}
