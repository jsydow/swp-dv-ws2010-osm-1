package gui;

import java.io.File;

import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapController;
import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.RouteOverlay;

import core.data.DataStorage;
import core.logger.WaypointLogService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MapsForgeActivity extends MapActivity {
	MapController mapController;
	RouteOverlay rOverlay;
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
		
		Paint outline = new Paint();
		outline.setARGB(150, 0, 0, 255);
		Paint filling = new Paint();
		filling.setARGB(100, 255, 0, 0);
		
		rOverlay = new RouteOverlay(filling, outline);
		
		mapView.getOverlays().add(rOverlay);
		
		gpsReceiver = new GPSReceiver();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(gpsReceiver, new IntentFilter(WaypointLogService.UPDTAE_GPS));
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
			
			if(intend.getAction().equals(WaypointLogService.UPDTAE_GPS)) {
				double lng = intend.getExtras().getDouble("long");
				double lat = intend.getExtras().getDouble("lat");
				
				mapController.setCenter(new GeoPoint(lat, lng));
				
				Log.d("GPS_REC", "Location update received (" + lng +  ", " + lat +")");
			} else if(intend.getAction().equals(WaypointLogService.UPDTAE_WAY)) {
				int id = intend.getExtras().getInt("way_id");
				rOverlay.setRouteData(DataStorage.getInstance().getCurrentTrack().getWays().get(id).toGeoPoitArray());
			}
		}
	}
}