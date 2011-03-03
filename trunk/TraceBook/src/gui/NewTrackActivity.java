package gui;

import core.logger.ServiceConnector;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class NewTrackActivity extends TabActivity {
	
	// Samplearray, will replace with the list of POI's, Way's and Area's
	String[] tracks = new String[] {
        "Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra",
        "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina",
        "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan",
        "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium"
      };
	
	@Override
	/**
	 * Init TabView and ListView
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newtrackactivity);

		// Init TabHost
		TabHost tabHost = getTabHost();
		
		//Init ListView for EditTab
		ListView listView = (ListView) findViewById(R.id.tracks_lvw);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>
						(this,android.R.layout.simple_list_item_1, tracks);
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

		//Init TabHost
		tabHost.addTab(tabHost.newTabSpec("map_tab")
				.setIndicator(getResources().getString(R.string.map_tab))
				.setContent(R.id.map_tab));
		tabHost.addTab(tabHost.newTabSpec("new_tab")
				.setIndicator(getResources().getString(R.string.new_tab))
				.setContent(R.id.new_tab));
		tabHost.addTab(tabHost.newTabSpec("edit_tab")
				.setIndicator(getResources().getString(R.string.edit_tab))
				.setContent(R.id.edit_tab));

		tabHost.setCurrentTab(1);

		//Init ServiceConnector
        ServiceConnector.startService(this);
        ServiceConnector.initService();
        }
	
	/**
	 * Method is called if StartWay-Togglebutton pressed. 
	 * Start and stop way tracking.
	 * @param view
	 */
	public void startWayTbtn(View view){
        ToggleButton streetToggle = (ToggleButton) findViewById(R.id.startWay_Tbtn);
		String check = streetToggle.getText().toString();
        if(check.equals(streetToggle.getTextOn().toString())){
        		try {
					ServiceConnector.getLoggerService().beginWay();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }else{
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
		String check = areaToggle.getText().toString();
        if(check.equals(areaToggle.getTextOn().toString())){
        try{
        	ServiceConnector.getLoggerService().beginWay();
        
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

	
}