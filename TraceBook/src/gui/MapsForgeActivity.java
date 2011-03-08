package gui;

import java.io.File;

import org.mapsforge.android.maps.ArrayItemizedOverlay;
import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapController;
import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.OverlayItem;
import org.mapsforge.android.maps.RouteOverlay;

import Trace.Book.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import core.data.DataNode;
import core.data.DataPointsList;
import core.data.DataStorage;
import core.data.DataTrack;
import core.logger.WaypointLogService;

/**
 * This class implements a MapsForge Map activity and draws ways and nodes
 * as overlays on it.
 * In the future it will also be possible to modify said nodes and ways. 
 * @author benpicco
 *
 */
public class MapsForgeActivity extends MapActivity {
	final static String LOG_TAG = "MapsForgeActivity";
	
	MapController mapController;
	RouteOverlay routesOverlay;
	ArrayItemizedOverlay pointsOverlay;
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
		
		if(android.os.Build.MODEL.equals("sdk")) {
			Log.d(LOG_TAG, "Running in emulator - disabling SD cache");
			mapView.setMemoryCardCacheSize(0);
		}
		
		setContentView(mapView);
		
		mapController = mapView.getController();
		
		Paint outline = new Paint();
		outline.setARGB(150, 0, 0, 255);
		Paint filling = new Paint();
		filling.setARGB(100, 255, 0, 0);
		
		final Drawable defaultMarker = getResources().getDrawable(R.drawable.marker_red);
		
		routesOverlay = new RouteOverlay(filling, outline);
		pointsOverlay = new ArrayItemizedOverlay(defaultMarker, this);
		
		mapView.getOverlays().add(routesOverlay);
		mapView.getOverlays().add(pointsOverlay);
		
		gpsReceiver = new GPSReceiver();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(gpsReceiver, new IntentFilter(WaypointLogService.UPDTAE_GPS_POS));
		registerReceiver(gpsReceiver, new IntentFilter(WaypointLogService.UPDTAE_OBJECT));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(gpsReceiver);
	}
	
	/**
	 * This class receives Broadcast Messages from the WaypointLogService in order
	 * to update the current location and overlays if position or overlay data has
	 * changed.
	 * @author benpicco
	 *
	 */
	private class GPSReceiver extends BroadcastReceiver {
		OverlayItem current_pos;

		/**
		 * creates a new OverlayItem
		 * @param pos postition of the marker
		 * @param marker id of the Graphics object to use
		 * @return
		 */
		private OverlayItem getOverlayItem(GeoPoint pos, int marker) {
			OverlayItem oi = new OverlayItem(pos, "foo", "bar");
			oi.setMarker(getResources().getDrawable(marker));
			
			return oi;
		}
		
		public GPSReceiver() { /* nothing to do here */ }

		@Override
		public void onReceive(Context ctx, Intent intend) {
			
			// Receive current location and do something with it
			if(intend.getAction().equals(WaypointLogService.UPDTAE_GPS_POS)) {
				final double lng = intend.getExtras().getDouble("long");
				final double lat = intend.getExtras().getDouble("lat");
				
				final GeoPoint pos = new GeoPoint(lat, lng);
				
				mapController.setCenter(pos);
				
				Log.d(LOG_TAG, "Location update received (" + lng +  ", " + lat +")");
				
				pointsOverlay.removeOverlay(current_pos);
				pointsOverlay.addOverlay(getOverlayItem(pos, R.drawable.marker_green));
				
				
			// Receive an update of a way or a node and update the overlay accordingly 
			} else if(intend.getAction().equals(WaypointLogService.UPDTAE_OBJECT)) {
				if(currentTrack() == null) {
					Log.e(LOG_TAG, "Received UPDATE_OBJECT with no current track present.");
					return;
				}
				
				int way_id = intend.getExtras().getInt("way_id");
				int point_id = intend.getExtras().getInt("point_id");
				if(way_id > 0) {
					Log.d(LOG_TAG, "Received way update, id="+way_id);
					DataPointsList way = currentTrack().getPointsListById(way_id);
					if(way == null)
						Log.e(LOG_TAG, "Way with ID " + way_id + " does not exist.");
					else {
						routesOverlay.setRouteData(way.toGeoPoitArray());
					}
					
				} else if(point_id > 0) {
					Log.d("LOG_TAG", "Received node update, id="+point_id);
					DataNode point = currentTrack().getNodeById(point_id);
					if(point == null)
						Log.e(LOG_TAG, "Node with ID " + point_id + " does not exist.");
					else {
						pointsOverlay.addOverlay(
								new OverlayItem(new GeoPoint(point.getLat(), point.getLon()),
								point.get_id() + "", ""));
					}
				}
			}
		}
	}
	
	/**
	 * gets the current DataTrack for convenience
	 * @return
	 */
	static DataTrack currentTrack() { 
		return DataStorage.getInstance().getCurrentTrack();
	}
}