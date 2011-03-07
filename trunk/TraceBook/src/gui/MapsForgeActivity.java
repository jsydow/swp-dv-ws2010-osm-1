package gui;

import android.os.Bundle;
import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapView;

public class MapsForgeActivity extends MapActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapView mapView = new MapView(this);
        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(true);
        mapView.setMapFile("/sdcard/default.map");
        setContentView(mapView);
    }
}
