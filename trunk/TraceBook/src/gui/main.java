package gui;

import Trace.Book.R;
import Trace.Book.R.layout;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

public class main extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    public void newTrack(View view){
    	/** Called when the Button newTrack was pressed */
    	Intent intent = new Intent(this, NewTrackActivity.class);
    	startActivity(intent);
    }
    
    public void loadTrack(View view){
    	Intent intent = new Intent (this, LoadTrackActivity.class);
    	startActivity(intent);
    	
    }
    
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_optionsmenu, menu);
		return true;
	}
}