/*
 * from http://mylifewithandroid.blogspot.com/2008/02/double-life-of-service.html
 */

package aexp.dualservice;

import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LogServiceClient extends Activity {
	private static final String LOG_TAG = "LOGSERVICECLIENT";
	private ILoggerService loggerService = null;
	private LoggerServiceConnection conn = null;
	private boolean started = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		/* Create Buttons */

		Button startServiceButton = (Button) findViewById(R.id.startservice);
		startServiceButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				startService();
				initService();
			}
		});
		Button stopServiceButton = (Button) findViewById(R.id.stopservice);
		stopServiceButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				releaseService();
				stopService();
			}
		});
		Button invokeServiceButton = (Button) findViewById(R.id.invokeservice);
		invokeServiceButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				invokeService();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		releaseService(); // keep service running in background
	}

	private void initService() {
		if (conn == null) {
			conn = new LoggerServiceConnection();
			Intent i = new Intent();
			i.setClassName(this.getPackageName(), LogService.class.getName());
			bindService(i, conn, Context.BIND_AUTO_CREATE);
			updateServiceStatus();
			Log.d(LOG_TAG, "bindService()");
		} else {
			Toast.makeText(this, "Cannot bind - service already bound", Toast.LENGTH_SHORT).show();
		}
	}

	private void releaseService() {
		if (conn != null) {
			unbindService(conn);
			conn = null;
			updateServiceStatus();
			Log.d(LOG_TAG, "unbindService()");
		} else {
			Toast.makeText(this, "Cannot unbind - service not bound", Toast.LENGTH_SHORT).show();
		}
	}

	private void startService() {
		if (started) {
			Toast.makeText(this, "Service already started", Toast.LENGTH_SHORT).show();
		} else {
			Intent i = new Intent();
			i.setClassName(this.getPackageName(), LogService.class.getName());
			startService(i);
			Log.d(LOG_TAG, "startService()");
			started = true;
			updateServiceStatus();
		}
	}

	private void stopService() {
		if (!started) {
			Toast.makeText(this, "Service not yet started", Toast.LENGTH_SHORT).show();
		} else {
			if(loggerService != null)
				try {
					loggerService.clearList();
				} catch (RemoteException e) {
					Log.e(LOG_TAG, e.getMessage());
				}
			
			Intent i = new Intent();
			i.setClassName(this.getPackageName(), LogService.class.getName());
			stopService(i);
			Log.d(LOG_TAG, "stopService()");
			started = false;
			updateServiceStatus();
		}
	}

	private void invokeService() {
		if (conn == null) {
			Toast.makeText(this, "Cannot invoke - service not bound", Toast.LENGTH_SHORT).show();
		} else if (loggerService == null) {
			Toast.makeText(this, "Cannot invoke - loggerService is null.", Toast.LENGTH_SHORT).show();
		} else {
			try {
				TextView t = (TextView) findViewById(R.id.result);

				List<Location> fixes = loggerService.getPoints();
				if (fixes == null)
					t.setText("List of fixes is null");
				else if (fixes.isEmpty())
					t.setText("List of fixes is empty");
				else
					t.setText("Last fix: " + fixes.get(0).toString() + " ("+fixes.size()+") fixes saved");

			} catch (RemoteException ex) {
				Log.e(LOG_TAG, "DeadObjectException", ex);
			}
		}
	}

	private void updateServiceStatus() {
		String bindStatus = conn == null ? "unbound" : "bound";
		String startStatus = started ? "started" : "not started";
		String statusText = "Service status: " + bindStatus + "," + startStatus;
		TextView t = (TextView) findViewById(R.id.servicestatus);
		t.setText(statusText);
	}

	class LoggerServiceConnection implements ServiceConnection {
		public void onServiceConnected(ComponentName className, IBinder boundService) {
			loggerService = ILoggerService.Stub.asInterface((IBinder) boundService);
			Log.d(LOG_TAG, "onServiceConnected");
		}

		public void onServiceDisconnected(ComponentName className) {
			loggerService = null;
			Log.d(LOG_TAG, "onServiceDisconnected");
			updateServiceStatus();
		}
	};
}
