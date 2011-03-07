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
	public static final String BROADCAST_ACTION = "de.fu-berlin.inf.tracebook.UPDTAE_GPS";
	
	DataStorage storage			= DataStorage.getInstance();
	DataNode current_node		= null;
	
	LogParameter params;
	Intent gps_intent;
	
	boolean gps_on = false;
	boolean one_shot = false;
			
	LocationListener ll = this;

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.d(LOG_TAG, "onStart");
		
		gps_intent = new Intent(BROADCAST_ACTION);
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
			((LocationManager) getSystemService(Context.LOCATION_SERVICE)).requestLocationUpdates(LocationManager.GPS_PROVIDER, params.delta_time, params.delta_distance, ll);
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
	
	DataPointsList current_way() {
		if(storage == null || storage.getCurrentTrack() == null)
			return null;
		return storage.getCurrentTrack().getCurrentWay();
	}

	/**
	 * The IAdderService is defined through IDL
	 */
	private final ILoggerService.Stub binder = new ILoggerService.Stub() {

		public void addTrack(LogParameter param) {
			params = param;		
			restartGPS();
 			
			storage.setCurrentTrack(storage.newTrack());
		}

		public int stopTrack() {
			stopGPS();
			
			storage.getCurrentTrack().serialise();
			
			return endWay();
		}
		
		public int createPOI(boolean on_way) {
			if(on_way && current_way() != null)
				current_node = current_way().newNode();
			else
				current_node = storage.getCurrentTrack().newNode();
			
			return current_node.get_id();
		}

		public int beginWay(boolean do_one_shot) {
			one_shot = do_one_shot;
			
			if(current_way() == null)	// start a new way
				storage.getCurrentTrack().setCurrentWay(storage.getCurrentTrack().newWay());
			
			if(one_shot)			// in one_shot mode, add a new point
				current_node = current_way().newNode();
				
			return current_way().get_id();
		}

		public synchronized int endWay() {
			storage.getCurrentTrack().setCurrentWay(null);
			
			DataPointsList tmp = current_way();
			
			if(tmp != null)
				return tmp.get_id();
			return -1;
		}


		public synchronized int beginArea() {
			// TODO Auto-generated method stub			
			if(current_way() == null)	// start a new way
				storage.getCurrentTrack().setCurrentWay(storage.getCurrentTrack().newWay());
			
			current_way().setArea(true);			
			return current_way().get_id();
		
		}

		public synchronized int endArea() {
			// TODO Auto-generated method stub
								
			storage.getCurrentTrack().setCurrentWay(null);			
			DataPointsList tmp = current_way();
			
			if(tmp != null)
				return tmp.get_id();			
			return 0;
		}

		public boolean isWayLogging() {
			if( storage.getCurrentTrack() != null ){
				if(current_way() != null){
					return !current_way().isArea();
				}
			}
			return false;
		}

		public boolean isAreaLogging() {
			if( storage.getCurrentTrack() != null ){
				if(current_way() != null){
					return current_way().isArea();
				}
			}
			return false;
		}

	};

	/** GPS related Methods **/
	
	public synchronized void onLocationChanged(Location loc) {
		Log.d(LOG_TAG, "GPS location changed");
		
		gps_intent.putExtra("long",	loc.getLongitude());
		gps_intent.putExtra("lat",	loc.getLatitude());
		sendBroadcast(gps_intent);
				
		if(current_node != null) {				// one_shot or POI mode
			current_node.setLocation(loc);
			current_node = null;
		} else if(current_way() != null) {		// Continuous mode
			current_way().newNode(loc);			// poi in track was already added before
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