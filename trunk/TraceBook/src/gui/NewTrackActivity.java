package gui;

import Trace.Book.R;
import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.view.View;
import android.os.IBinder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import core.logger.ILoggerService;
import core.logger.WaypointLogService;

public class NewTrackActivity extends TabActivity {
	
	String[] tracks = new String[] {
        "Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra",
        "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina",
        "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan",
        "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium"
      };
	
	@Override
	/**
	 * Init TabView and start tracking
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newtrackactivity);

		// Init TabHost and ListView for EditTab
		TabHost tabHost = getTabHost();
		ListView listView = (ListView) findViewById(R.id.tracks_lvw);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>
						(this,android.R.layout.simple_list_item_1, tracks);
		listView.setAdapter(adapter);
		listView.setTextFilterEnabled(true);
		
		//Get list Element and send Toast
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

	//	startService();
		//initService();
	}
	
	
	 /**
	  * Called if the addPointButton pressed.
	  * Switch to the AddPointActivity, to insert Meta-Tags for the 
	  * last Node.
	  * @param view
	  */
	public void addPointBtn (View view){
		final Intent intent = new Intent (this, AddPointActivity.class);
		startActivity(intent);
	}
	
	/**
	 * 
	 * Called if the stopTrackButton pressed.
	 * Stop the actual tracking and returns to the main activity.
	 * @param view
	 */
	public void stopTrackBtn(View view){
		final Intent intent = new Intent (this, main.class);
		startActivity(intent);
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	
}
