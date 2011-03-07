package gui;

import java.io.File;

import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapController;
import org.mapsforge.android.maps.MapView;

import util.helper;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import core.data.DataStorage;

public class MapsForgeActivity extends MapActivity {
	MapController mapController;
	Handler serviceHandler = null;

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

		serviceHandler = new Handler();
		serviceHandler.postDelayed(new RunTask(), 1000L);
	}

	/**
	 * We check every second for an updated GPS location
	 * XXX maybe there is a better way to do this
	 * @author benpicco
	 *
	 */
	class RunTask implements Runnable {
		public void run() {
			Location loc = DataStorage.getInstance().getLastLocation();
			
			if(loc != null) {
				GeoPoint current = helper.geoPointFromLocation(loc);
				mapController.setCenter(current);
			}
			
			serviceHandler.postDelayed(this, 1000L);
		}
	}
}