package gui;

import java.io.File;

import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapController;
import org.mapsforge.android.maps.MapView;

import core.logger.WaypointLogService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MapsForgeActivity extends MapActivity {
	MapController mapController;
	BroadcastReceiver gpsReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		File file = new File("/sdcard/", "default.map");
		if (!file.exists()) {
			Toast.makeText(getApplicationContext(),
					"Unable to open " + file.getAbsolutePath() + "!",
					Toast.LENGTH_LONG).show();
			return;
		}

		MapView mapView = new MapView(this);
		mapView.setClickable(true);
		mapView.setBuiltInZoomControls(true);
		mapView.setMapFile(file.getAbsolutePath());

		setContentView(mapView);
		
		mapController = mapView.getController();
		
		gpsReceiver = new GPSReceiver();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(gpsReceiver, new IntentFilter(WaypointLogService.BROADCAST_ACTION));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(gpsReceiver);
	}
	
	private class GPSReceiver extends BroadcastReceiver {
		public GPSReceiver() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onReceive(Context ctx, Intent intend) {
			double lng = intend.getExtras().getDouble("long");
			double lat = intend.getExtras().getDouble("lat");
			
			mapController.setCenter(new GeoPoint(lat, lng));
			
			Log.d("GPS_REC", "Location update received (" + lng +  ", " + lat +")");
		}
	}
}