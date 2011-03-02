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
	private static final String LOG_TAG = "LOGSERVICECLIENT";
	private ILoggerService loggerService = null;
	private LoggerServiceConnection conn = null;
	private boolean started = false;

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

		startService();
		initService();
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
		releaseService(); // keep logging service running in background
	}

	/**
	 * Bind the logger service to this activity
	 */
	private void initService() {
		if (conn == null) {
			conn = new LoggerServiceConnection();
			Intent i = new Intent();
			i.setClassName(this.getPackageName(), WaypointLogService.class.getName());
			bindService(i, conn, Context.BIND_AUTO_CREATE);
			Log.d(LOG_TAG, "bindService()");
		} else {
			Toast.makeText(this, "Cannot bind - service already bound", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * release the logger service
	 */
	private void releaseService() {
		if (conn != null) {
			unbindService(conn);
			conn = null;
			Log.d(LOG_TAG, "unbindService()");
		} else {
			Toast.makeText(this, "Cannot unbind - service not bound", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Start the logging service (collect GPS data)
	 */
	private void startService() {
		if (started) {
			Toast.makeText(this, "Service already started", Toast.LENGTH_SHORT).show();
		} else {
			Intent i = new Intent();
			i.setClassName(this.getPackageName(),
					WaypointLogService.class.getName());
			startService(i);
			Log.d(LOG_TAG, "startService()");
			started = true;
		}
	}

	/**
	 * Stop logging service (stop collecting GPS data)
	 */
	private void stopService() {
		if (!started) {
			Toast.makeText(this, "Service not yet started", Toast.LENGTH_SHORT).show();
		} else {

			Intent i = new Intent();
			i.setClassName(this.getPackageName(), WaypointLogService.class.getName());
			stopService(i);
			Log.d(LOG_TAG, "stopService()");
		}		 
	}

	/**
	 * Stub for RPC communication with the logger Service  
	 * @author benpicco
	 *
	 */
	class LoggerServiceConnection implements ServiceConnection {
		public void onServiceConnected(ComponentName className, IBinder boundService) {
			loggerService = ILoggerService.Stub.asInterface((IBinder) boundService);
			Log.d(LOG_TAG, "onServiceConnected");
		}

		public void onServiceDisconnected(ComponentName className) {
			loggerService = null;
			Log.d(LOG_TAG, "onServiceDisconnected");
		}
	}
}
