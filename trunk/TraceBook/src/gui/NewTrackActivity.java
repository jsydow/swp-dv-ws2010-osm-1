package gui;

import java.util.List;

import Trace.Book.R;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import core.data.DataNode;
import core.data.DataStorage;
import core.logger.ServiceConnector;

public class NewTrackActivity extends TabActivity {
	
	class MyListener implements OnTabChangeListener {
		
		NewTrackActivity  act;
		TabHost		tab;
		String editTab;
		String currentTab;
		public MyListener(NewTrackActivity act, TabHost tab){
			this.act = act;
			this.tab = tab;
		}

		public void onTabChanged(String tabId) {
			editTab = "edit_tab";
			currentTab = tab.getCurrentTabTag();// TODO Auto-generated method stub
			if (currentTab.equals(tabId)){ 
			act.initListView();
			tab.invalidate();
			}
		}	
	}
	
	private void initToggleButtons() {
		
		ToggleButton startWay =(ToggleButton)findViewById(R.id.startWay_Tbtn);
		ToggleButton startArea =(ToggleButton)findViewById(R.id.startArea_Tbtn);
		
		try {
			startWay.setChecked( ServiceConnector.getLoggerService().isWayLogging()) ;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			startArea.setChecked(ServiceConnector.getLoggerService().isAreaLogging());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	TabHost myTabHost;
	@Override
	/**
	 * Create activity
	 */
	public void onCreate(Bundle savedInstanceState) {
		myTabHost = getTabHost();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newtrackactivity);	
		// Init TabHost
		initTabHost();
		
		// Init ListView
		initListView();
		
		//Init ServiceConnector
        ServiceConnector.startService(this);
        ServiceConnector.initService();
        
        initToggleButtons();
        
        myTabHost.setOnTabChangedListener(new MyListener(this, myTabHost) );
		
	}

	
	void initListView(){
		//Init ListView for EditTab
		ListView listView = (ListView) findViewById(R.id.tracks_lvw);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>
						(this,android.R.layout.simple_list_item_1, getPOIList());
		listView.setAdapter(adapter);
		listView.setTextFilterEnabled(true);
		
		//Get selected item and send toast
		listView.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view,
		        int position, long id) {
		      Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
		          Toast.LENGTH_SHORT).show();
		    }
		  });

	}
	
	private void initTabHost(){
		// Init TabHost
		TabHost tabHost = getTabHost();
	
		//Init TabHost
		
		tabHost.addTab(tabHost.newTabSpec("map_tab")
				.setIndicator(getResources().getString(R.string.map_tab))
				.setContent(new Intent(this, MapsForgeActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("new_tab")
				.setIndicator(getResources().getString(R.string.new_tab))
				.setContent(R.id.new_tab));
		tabHost.addTab(tabHost.newTabSpec("edit_tab")
				.setIndicator(getResources().getString(R.string.edit_tab))
				.setContent(R.id.edit_tab));

		tabHost.setCurrentTab(1);

	}
	
	/**
	 * Method is called if StartWay-Togglebutton pressed. 
	 * Start and stop way tracking.
	 * @param view
	 */
	public void startWayTbtn(View view){
        ToggleButton streetToggle = (ToggleButton) findViewById(R.id.startWay_Tbtn);
        ToggleButton areaToggle = (ToggleButton) findViewById(R.id.startArea_Tbtn);
		String check = streetToggle.getText().toString();
        if(check.equals(streetToggle.getTextOn().toString())){
        		areaToggle.setClickable(false);
        		try {
					ServiceConnector.getLoggerService().beginWay(false);
					
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }else{
        	areaToggle.setClickable(true);
        	try {
				ServiceConnector.getLoggerService().endWay();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}
	
	/**
	 * Method is called if StartArea-Togglebutton pressed.
	 * Start and stop area tracking.
	 * @param view
	 */
	public void startAreaTbtn(View view){
	    ToggleButton areaToggle = (ToggleButton) findViewById(R.id.startArea_Tbtn);
	    ToggleButton streetToggle = (ToggleButton) findViewById(R.id.startWay_Tbtn);
		String check = areaToggle.getText().toString();
        if(check.equals(areaToggle.getTextOn().toString())){
        	streetToggle.setClickable(false);
	        try{
	        	ServiceConnector.getLoggerService().beginArea();
	        
	        }catch (RemoteException e) {
				// TODO: handle exception
	        	e.printStackTrace();
	        }
        }else  {
        	streetToggle.setClickable(true);
        	 try{
 	        	ServiceConnector.getLoggerService().endArea();
 	        
 	        }catch (RemoteException e) {
 				// TODO: handle exception
 	        	e.printStackTrace();
 	        }
        	
        }
        	
	}
	
	
	 /**
	  * Called if the addPointButton pressed.
	  * Switch to the AddPointActivity, to insert Meta-Tags for the 
	  * last Node.
	  * @param view
	  */
	public void addPointBtn (View view){
		int nodeId = 0;
		
		try {
			nodeId = ServiceConnector.getLoggerService().createPOI(false);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		final Intent intent = new Intent (this, AddPointActivity.class);
		startActivity(intent);
	}
	
	/**
	 * 
	 * Called if the stopTrackButton pressed.
	 * Stop the actual tracking and returns to the main activity.
	 * @param view
	 * @throws RemoteException 
	 */
	public void stopTrackBtn(View view){
		try {
			ServiceConnector.getLoggerService().stopTrack();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final Intent intent = new Intent (this, main.class);
		startActivity(intent);
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	
	/**
	 * Generate String Array for ListView to list all POI's Way's and Area's.
	 * @return
	 */
	public String[] getPOIList (){
		
		List<DataNode> nodeList = DataStorage.getInstance().getCurrentTrack().getNodes();
		String[] poiList = new String[nodeList.size()];
		int i = 0;
		for( DataNode dn : nodeList) {
			poiList[i] = "POI's: "	+ dn.get_id();
			i++;
		}		
		return poiList;
	}
}
