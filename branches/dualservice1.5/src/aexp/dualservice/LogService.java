package aexp.dualservice;

import java.util.LinkedList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class LogService extends Service implements LocationListener {
	private static final String LOG_TAG = "LOGSERVICE";
	private List<Location> fixes = new LinkedList<Location>();
	
	boolean tracking = false;
	LocationListener ll = this;

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.d(LOG_TAG, "onStart");
		((LocationManager) getSystemService(Context.LOCATION_SERVICE)).requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(LOG_TAG, "onCreate");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(LOG_TAG, "onDestroy");
		((LocationManager) getSystemService(Context.LOCATION_SERVICE)).removeUpdates(ll);
	}

	/**
	 * The IAdderService is defined through IDL
	 */
	private final ILoggerService.Stub binder = new ILoggerService.Stub() {
		public List<Location> getPoints() {
			return fixes;
		}
		
		public void clearList() {
			fixes.clear();
		}
	};

	/** GPS related Methods **/
	
	@Override
	public void onLocationChanged(Location arg0) {
		Log.d(LOG_TAG, "GPS location changed");
		fixes.add(arg0);
	}

	@Override
	public void onProviderDisabled(String arg0) {
		Log.d(LOG_TAG, "GPS Provider Disabled: " + arg0);
	}

	@Override
	public void onProviderEnabled(String arg0) {
		Log.d(LOG_TAG, "GPS Provider Enabled: " + arg0);
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		Log.d(LOG_TAG, "GPS Status Changed: " + arg0);
	}
}