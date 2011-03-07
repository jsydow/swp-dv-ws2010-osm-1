package gui;

import java.io.File;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapView;

import core.data.DataStorage;

public class MapsForgeActivity extends MapActivity {
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
				Toast.makeText(getApplicationContext(), loc.getLatitude() + " - " + loc.getLongitude(), Toast.LENGTH_SHORT).show();
			}
			
			serviceHandler.postDelayed(this, 1000L);
		}
	}
}
