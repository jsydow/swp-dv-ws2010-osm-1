package gui;

import Trace.Book.R;
import android.app.ListActivity;
import android.os.Bundle;

public class FullTextSearchActivity extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_fulltextsearch);
    }

}
