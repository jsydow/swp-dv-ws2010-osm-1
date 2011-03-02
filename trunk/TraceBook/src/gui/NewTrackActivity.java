package gui;

import core.logger.ILoggerService;
import core.logger.WaypointLogService;
import Trace.Book.R;
import android.app.TabActivity;
import android.content.Intent;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.view.View;
import android.os.IBinder;
import android.util.Log;
import android.widget.TabHost;
import android.widget.Toast;

public class NewTrackActivity extends TabActivity {
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newtrackactivity);

		TabHost tabHost = getTabHost();

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
	
	public void addPointBtn (View view){
		final Intent intent = new Intent (this, AddPointActivity.class);
		startActivity(intent);
	}
	
	public void stopTrackBtn(View view){
		final Intent intent = new Intent (this, main.class);
		startActivity(intent);
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//releaseService(); // keep logging service running in background
	}

	

	
}
