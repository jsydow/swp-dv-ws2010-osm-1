package gui;

import java.io.File;

import android.os.Bundle;
import android.widget.Toast;

import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapView;

public class MapsForgeActivity extends MapActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        File file = new File("/sdcard/", "default.map");
        if(!file.exists()) {
		      Toast.makeText(getApplicationContext(), "Unable to open "+file.getAbsolutePath()+"!", Toast.LENGTH_LONG).show();
		      return;
        }
        
        MapView mapView = new MapView(this);
        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(true);
        mapView.setMapFile(file.getAbsolutePath());
        
        setContentView(mapView);
    }
}
