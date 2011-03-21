package gui.activity;

import Trace.Book.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 *
 * 
 */
public class PreferencesActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.addPreferencesFromResource(R.xml.tracebook_preference);
        setTitle(R.string.string_preferencesActivity_title);
    }
}
