package gui;

import java.util.List;

import Trace.Book.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import core.data.DataStorage;
import core.data.DataTrack;

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

        String lastTrack = "";
        DataStorage storage = DataStorage.getInstance();
        storage.retrieveTrackNames();
        List<String> list = storage.getAllTracks();
        StringBuilder sb = new StringBuilder();
        for (String str : list) {
            sb.append(str);
            sb.append("\n");
            lastTrack = str;
        }
        sb.append("\n");
        storage.deserialiseTrack(lastTrack);
        DataTrack track = storage.getTrack(lastTrack);
        storage.setCurrentTrack(track);

        sb.append("Last Track is: " + track.getName() + "\n");
        sb.append("Nodes: " + track.getNodes().size() + "\n");
        sb.append("Media: " + track.getMedia().size() + "\n");
        sb.append("Ways: " + track.getWays().size() + "\n");

        ((TextView) findViewById(R.id.track_textview)).setText(sb);
        storage.setCurrentTrack(track);
    }

    /**
     * just for testing
     * 
     * @param view
     *            ...
     */
    public void loadLastTrack(View view) {
        Intent intent = new Intent(this, NewTrackActivity.class);
        startActivity(intent);
    }
}
