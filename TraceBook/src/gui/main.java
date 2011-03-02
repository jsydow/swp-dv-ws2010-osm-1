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
import android.view.View;

public class main extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ServiceConnector.startService(this);
        ServiceConnector.initService(this);
        
    }
    
    public void newTrack(View view) {
    	
    	LogParameter param = new LogParameter();
    	
    	try {
			ServiceConnector.getLoggerService().addTrack(param, false);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    //	Intent intent = new Intent(this, NewTrackActivity.class);
   // 	startActivity(intent);
    	
    	
    }
    
    public void loadTrack(View view){
    	Intent intent = new Intent (this, LoadTrackActivity.class);
    	startActivity(intent);   	
    }
    
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_optionsmenu, menu);
		return true;
	}
}