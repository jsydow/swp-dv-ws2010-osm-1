package core.logger;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import core.data.DataNode;
import core.data.DataPointsList;
import core.data.DataStorage;
import core.data.LogParameter;

public class WaypointLogService extends Service implements LocationListener {
	private static final String LOG_TAG = "LOGSERVICE";
	
	DataStorage storage			= DataStorage.getInstance();
	DataPointsList current_way	= null;
	DataNode current_node		= null;
	
	LogParameter params;
	
	boolean gps_on = false;
	boolean one_shot = false;
		
	LocationListener ll = this;

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.d(LOG_TAG, "onStart");
	}

	@Override
	public IBinder onBind(Intent intent) {
		startGPS();
		return binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(LOG_TAG, "onCreate");
		params = new LogParameter();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(LOG_TAG, "onDestroy");
		stopGPS();
	}
	
	void startGPS() {
		if(!gps_on)
			((LocationManager) getSystemService(Context.LOCATION_SERVICE)).requestLocationUpdates(LocationManager.GPS_PROVIDER, params.delta_distance, params.delta_time, ll);
		gps_on = true;
	}
	
	void stopGPS() {
		if(gps_on)
			((LocationManager) getSystemService(Context.LOCATION_SERVICE)).removeUpdates(ll);
		gps_on = false;
	}
	
	void restartGPS() {
		stopGPS(); startGPS();
	}

	/**
	 * The IAdderService is defined through IDL
	 */
	private final ILoggerService.Stub binder = new ILoggerService.Stub() {

		public int addTrack(LogParameter param, boolean do_one_shot) {
			params = param;
			one_shot = do_one_shot;
			
			restartGPS();
			
			if(storage.getCurrentTrack() == null)	// create a new track XXX - more logic in DataStorage
				storage.setCurrentTrack(storage.newTrack());
			
			if(current_way == null)	// start a new way
				current_way = storage.getCurrentTrack().newWay();
			
			if(one_shot)			// in one_shot mode, add a new point
				current_node = current_way.newNode();
			
 			return current_way.get_id();
		}

		public int stopTrack() {
			stopGPS();
			
			DataPointsList tmp = current_way;
			current_way = null;
			
			if(tmp != null)
				return tmp.get_id();
			return -1;
		}
		
		public int createPOI(boolean on_way) {
			if(on_way && current_way != null)
				current_node = current_way.newNode();
			else
				current_node = storage.getCurrentTrack().newNode();
			
			return current_node.get_id();
		}

		public boolean isLogging() {
			return current_way != null;
		}
	};

	/** GPS related Methods **/
	
	public void onLocationChanged(Location loc) {
		Log.d(LOG_TAG, "GPS location changed");
		
		if(current_node != null) {				// one_shot or POI mode
			current_node.setLocation(loc);
			if(one_shot || current_way == null)	// one_shot or poi
				stopGPS();						// else: poi on track
			current_node = null;
		} else if(current_way != null) {		// Continuous mode
			current_way.newNode(loc);			// poi in track was already added before
		}
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