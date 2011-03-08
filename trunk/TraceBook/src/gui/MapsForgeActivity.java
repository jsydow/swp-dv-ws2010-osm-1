package gui;

import java.io.File;

import org.mapsforge.android.maps.ArrayItemizedOverlay;
import org.mapsforge.android.maps.ArrayRouteOverlay;
import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.ItemizedOverlay;
import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapController;
import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.OverlayItem;
import org.mapsforge.android.maps.OverlayRoute;

import Trace.Book.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.DashPathEffect;
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
	ArrayRouteOverlay routesOverlay;
	ArrayItemizedOverlay pointsOverlay;
	BroadcastReceiver gpsReceiver;
	
	static Paint paintFill;	// TODO: different colors for different tracks
	static Paint paintOutline;
	
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
		
		final Drawable defaultMarker = getResources().getDrawable(R.drawable.marker_red);	

		pointsOverlay = new ArrayItemizedOverlay(defaultMarker, this);
		
		// create the paint objects for the RouteOverlay and set all parameters
		paintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintFill.setStyle(Paint.Style.STROKE);
		paintFill.setColor(Color.BLUE);
		paintFill.setAlpha(160);
		paintFill.setStrokeWidth(7);
		paintFill.setStrokeCap(Paint.Cap.BUTT);
		paintFill.setStrokeJoin(Paint.Join.ROUND);
		paintFill.setPathEffect(new DashPathEffect(new float[] { 20, 20 }, 0));

		paintOutline = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintOutline.setStyle(Paint.Style.STROKE);
		paintOutline.setColor(Color.BLUE);
		paintOutline.setAlpha(96);
		paintOutline.setStrokeWidth(7);
		paintOutline.setStrokeCap(Paint.Cap.BUTT);
		paintOutline.setStrokeJoin(Paint.Join.ROUND);
		
		routesOverlay = new ArrayRouteOverlay(paintFill, paintOutline);
		
		addPoints(pointsOverlay); // when adding a POI this activity is destroyed
		addWays(routesOverlay);

		mapView.getOverlays().add(routesOverlay);
		mapView.getOverlays().add(pointsOverlay);
		
		gpsReceiver = new GPSReceiver();
	}
	
	private static void addPoints(ArrayItemizedOverlay overlay) {
		for(DataNode n : currentTrack().getNodes()) {
			if(n.getOverlayItem() == null)
				n.setOverlayItem(new OverlayItem(n.toGeoPoint(), "", ""));
			overlay.addOverlay(n.getOverlayItem());
		}
	}
	
	private static void addWays(ArrayRouteOverlay overlay) {
		for(DataPointsList l : currentTrack().getWays()) {
			if(l.getOverlayRoute() == null)
				l.setOverlayRoute(new OverlayRoute(l.toGeoPointArray(), paintFill, paintOutline));
			overlay.addRoute(l.getOverlayRoute());
		}
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
		OverlayItem current_pos = null;

		/**
		 * creates a new OverlayItem
		 * @param pos position of the marker
		 * @param marker id of the Graphics object to use
		 * @return
		 */
		private OverlayItem getOverlayItem(GeoPoint pos, int marker) {
			final OverlayItem oi = new OverlayItem(pos, "", "");
			Drawable icon = getResources().getDrawable(marker);	
			oi.setMarker(ItemizedOverlay.boundCenterBottom(icon));
			
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
				
				Log.d(LOG_TAG, "Location update received " + pos.toString());
				
				if(current_pos != null)
					pointsOverlay.removeOverlay(current_pos);
				current_pos = getOverlayItem(pos, R.drawable.marker_green);
				pointsOverlay.addOverlay(current_pos);
				
				
			// Receive an update of a way and update the overlay accordingly 
			} else if(intend.getAction().equals(WaypointLogService.UPDTAE_OBJECT)) {
				if(currentTrack() == null) {
					Log.e(LOG_TAG, "Received UPDATE_OBJECT with no current track present.");
					return;
				}
				
				int way_id = intend.getExtras().getInt("way_id");
				if(way_id > 0) {
					Log.d(LOG_TAG, "Received way update, id="+way_id);
					DataPointsList way = currentTrack().getPointsListById(way_id);
					if(way == null)
						Log.e(LOG_TAG, "Way with ID " + way_id + " does not exist.");
					else {
						if(way.getOverlayRoute() != null)	// we can not change the route, thus create a new one
							routesOverlay.removeOverlay(way.getOverlayRoute());
						way.setOverlayRoute(new OverlayRoute(way.toGeoPointArray(), paintFill, paintOutline));
						routesOverlay.addRoute(way.getOverlayRoute());
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