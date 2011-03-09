package gui;

import java.util.List;

import core.data.DataStorage;
import Trace.Book.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * The Class LoadTrackActivity.
 */
public class LoadTrackActivity extends Activity {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loadtrackactivity);
		
		DataStorage storage = DataStorage.getInstance();
		storage.retrieveTrackNames();
		List<String> list = storage.getAllTracks();
		StringBuilder sb = new StringBuilder();
		for(String str : list) {
		    sb.append(str);
		    sb.append("\n");
		}
		((TextView)findViewById(R.id.track_textview)).setText(sb);
	}
}
