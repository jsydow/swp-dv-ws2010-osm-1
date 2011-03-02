package core.logger;

import core.data.DataNode;
import core.data.DataStorage;
import core.data.DataWay;
import core.data.LogParameter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class WaypointLogService extends Service implements LocationListener {
	private static final String LOG_TAG = "LOGSERVICE";
	
	DataStorage storage 	= new DataStorage();
	DataWay current_way 	= null;
	DataNode current_node 	= null;
	
	boolean tracking = false;
	LocationListener ll = this;

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.d(LOG_TAG, "onStart");
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
		stopGPS();
	}
	
	void startGPS(int delta_distance, int delta_time) {
		if(!tracking)
			((LocationManager) getSystemService(Context.LOCATION_SERVICE)).requestLocationUpdates(LocationManager.GPS_PROVIDER, delta_distance, delta_time, ll);
		tracking = true;
	}
	
	void stopGPS() {
		if(tracking)
			((LocationManager) getSystemService(Context.LOCATION_SERVICE)).removeUpdates(ll);
		tracking = false;
	}

	/**
	 * The IAdderService is defined through IDL
	 */
	private final ILoggerService.Stub binder = new ILoggerService.Stub() {

		public void startLog(LogParameter param) {
			startGPS(param.delta_distance, param.delta_time);
// 			current_way = storage.getCurrentTrack().newWay();
		}

		public int stopLog() {
			stopGPS();
			if(current_way != null)
				return current_way.getID();
			return -1;
		}

		public int createPOI(boolean onWay) {
			// TODO Auto-generated method stub
			return 0;
		}

		public boolean isLogging() {
			return tracking;
		}
	};

	/** GPS related Methods **/
	
	public void onLocationChanged(Location arg0) {
		Log.d(LOG_TAG, "GPS location changed");
		
//		current_node = new DataNode(storage.getID(), loc);
//		
//		if(current_way != null)
//			current_way.add(current_node);
	}

	public void onProviderDisabled(String arg0) {
		Log.d(LOG_TAG, "GPS Provider Disabled: " + arg0);
	}

	public void onProviderEnabled(String arg0) {
		Log.d(LOG_TAG, "GPS Provider Enabled: " + arg0);
	}

	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		Log.d(LOG_TAG, "GPS Status Changed: " + arg0);
	}
}