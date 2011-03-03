package gui;

import core.data.LogParameter;
import core.logger.ServiceConnector;
import Trace.Book.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class main extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Init ServiceConnector
        ServiceConnector.startService(this);
        ServiceConnector.initService();
    }
    
	/**
	 * Called if the newTrack Button pressed. 
	 * Start the NewTrackActivity. 
	 * @param view
	 */
    public void newTrackBtn(View view) {
    	
    	LogParameter param = new LogParameter();
    	
    	try {
			ServiceConnector.getLoggerService().addTrack(param, false);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Intent intent = new Intent(this, NewTrackActivity.class);
		startActivity(intent);

    }
   
	/**
	 * Called if the loadTrack Button pressed. 
	 * Start the LoadTrackActivity. 
	 * @param view
	 */
    public void loadTrackBtn(View view){
    	Intent intent = new Intent (this, LoadTrackActivity.class);
    	startActivity(intent);   	
    }
    
    @Override
    /**
     * Init optionsmenu for the MainActivity
     */
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_optionsmenu, menu);
		return true;
	}
    
    /**
     * Functionality of all Optionmenuitems
     */
    public boolean onOptionsItemSelected(MenuItem item){
    	
    	switch(item.getItemId()){
    	case R.id.close_opt:
    		finish();
    		return true;
    	case R.id.preferences_opt:
    		final Intent intent = new Intent(this,PreferencesActivity.class);
    		startActivity(intent);
    		return true;
		default:
			return super.onOptionsItemSelected(item);
    	}
    }
    
    @Override
    protected void onDestroy() {
            super.onDestroy();
            ServiceConnector.releaseService();
    }
}