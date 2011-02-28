/*
 * from http://mylifewithandroid.blogspot.com/2008/02/double-life-of-service.html
 */

package aexp.dualservice;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DualServiceClient extends Activity {
	private static final String LOG_TAG = "DUALSERVICECLIENT";
	private ICounterService counterService;
	private CounterServiceConnection conn;
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
			}
		});
		Button stopServiceButton = (Button) findViewById(R.id.stopservice);
		stopServiceButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				stopService();
			}
		});
		Button bindServiceButton = (Button) findViewById(R.id.bindservice);
		bindServiceButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				initService();
			}
		});
		Button unbindServiceButton = (Button) findViewById(R.id.unbindservice);
		unbindServiceButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				releaseService();
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
		releaseService();	// keep service running in background
	}

	private void initService() {
		if (conn == null) {
			conn = new CounterServiceConnection();
			Intent i = new Intent();
			i.setClassName("aexp.dualservice", "aexp.dualservice.DualService");
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
			Toast.makeText(this, "Cannot unbind - service not bound",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void startService() {
		if (started) {
			Toast.makeText(this, "Service already started", Toast.LENGTH_SHORT).show();
		} else {
			Intent i = new Intent();
			i.setClassName("aexp.dualservice", "aexp.dualservice.DualService");
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
			Intent i = new Intent();
			i.setClassName("aexp.dualservice", "aexp.dualservice.DualService");
			stopService(i);
			Log.d(LOG_TAG, "stopService()");
			started = false;
			updateServiceStatus();
		}
	}

	private void invokeService() {
		if (conn == null) {
			Toast.makeText(this, "Cannot invoke - service not bound", Toast.LENGTH_SHORT).show();
		} else {
			try {
				int ctr = counterService.getCounterValue();
				TextView t = (TextView) findViewById(R.id.result);
				t.setText("Counter value: " + Integer.toString(ctr));
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

	class CounterServiceConnection implements ServiceConnection {
		public void onServiceConnected(ComponentName className, IBinder boundService) {
			counterService = ICounterService.Stub.asInterface((IBinder) boundService);
			Log.d(LOG_TAG, "onServiceConnected");
		}

		public void onServiceDisconnected(ComponentName className) {
			counterService = null;
			Log.d(LOG_TAG, "onServiceDisconnected");
			updateServiceStatus();
		}
	};
}
