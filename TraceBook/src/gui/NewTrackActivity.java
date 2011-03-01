package gui;

import Trace.Book.R;
import android.app.TabActivity;
import android.os.Bundle;
import android.widget.TabHost;

public class NewTrackActivity extends TabActivity{
	
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.newtrackactivity);
	        
	        TabHost tabHost = getTabHost();
	        
	        tabHost.addTab(tabHost.newTabSpec("map_tab").setIndicator(getResources().getString(R.string.map_tab)).setContent(R.id.map_tab));
	        tabHost.addTab(tabHost.newTabSpec("new_tab").setIndicator(getResources().getString(R.string.new_tab)).setContent(R.id.new_tab));
	        tabHost.addTab(tabHost.newTabSpec("edit_tab").setIndicator(getResources().getString(R.string.edit_tab)).setContent(R.id.edit_tab));
	        
	        tabHost.setCurrentTab(0);
	 }
}
