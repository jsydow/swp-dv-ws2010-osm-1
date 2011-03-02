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
        
        ServiceConnector.initService(this);
    }
    
    public void newTrack(View view) {
    	Intent intent = new Intent(this, NewTrackActivity.class);
    	startActivity(intent);
    	
    	LogParameter param = new LogParameter();
    	
    	try {
			ServiceConnector.getLoggerService().addTrack(param, false);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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