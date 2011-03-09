package gui;

import Trace.Book.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * @author greentraxas
 *
 */
public class PreferencesActivity extends PreferenceActivity{

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.addPreferencesFromResource(R.xml.tracebook_preference);
       
    }
}
