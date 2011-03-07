package util;

import org.mapsforge.android.maps.GeoPoint;
import android.location.Location;

public class helper {
	/**
	 * Convert a Location object into a MapsForge GeoPoint
	 * @param loc
	 * @return
	 */
	public static GeoPoint geoPointFromLocation(Location loc) {
		return new GeoPoint(loc.getLatitude() * 1000000, loc.getLongitude() * 1000000);
	}
}
