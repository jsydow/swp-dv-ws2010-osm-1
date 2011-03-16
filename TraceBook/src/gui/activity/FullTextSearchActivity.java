package gui.activity;

import java.io.File;
import java.util.List;

import Trace.Book.R;
import android.app.ListActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import core.data.DataStorage;
import core.data.db.TagDb;
import core.data.db.TagSearchResult;

/**
 * 
 *
 * 
 */
public class FullTextSearchActivity extends ListActivity {

    /**
     * The text watcher tracks changes in the search edit box. As soon as some
     * text matches with the description text, the result will be displayed in
     * the listview.
     * 
     * 
     * 
     */
    static class MyTextWatcher implements TextWatcher {

        /**
         * reference to the FullTextSearchActivity to update the list view.
         */
        FullTextSearchActivity act;
        boolean firstTime = true;

        /**
         * 
         * @param act
         *            reference to the FullTextSearchActivity to update the list
         *            view.
         */
        public MyTextWatcher(FullTextSearchActivity act) {
            this.act = act;
        }

        public void afterTextChanged(Editable arg0) {
            // TODO Auto-generated method stub

        }

        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                int arg3) {
            // TODO Auto-generated method stub

        }

        public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                int arg3) {
            final String text = arg0.toString();

            Log.d(ACTIVITY_SERVICE, arg0.toString());
            (new Thread() {
                @Override
                public void run() {
                    Log.d("DBTHREAD", "start");
                    long startt = System.currentTimeMillis();
                    TagDb db = new TagDb(act);
                    if (firstTime) {
                        firstTime = false;
                        if (db.getTag(text, "de").size() == 0) {
                            Log.d("DBTHREAD", "init db");
                            db.initDbWithFile(DataStorage.getTraceBookDirPath()
                                    + File.separator + "tags.DE.xml");
                        }
                        Log.d("DBTHREAD", "init finish");
                    }
                    List<TagSearchResult> tags = db.getTag(text, "de");
                    for (TagSearchResult tsr : tags) {
                        Log.d("DB_TEST", tsr.getKey() + "=" + tsr.getValue());
                    }
                    db.closeDb();
                    Log.d("DBTHREAD", "stop tagslistsize=" + tags.size());
                    Log.d("DBTHREAD",
                            "time consumed: "
                                    + (System.currentTimeMillis() - startt));
                }
            }).start();
            // TagDb db = new TagDb(act);
            // if (firstTime) {
            // db.initDbWithFile(DataStorage.getTraceBookDirPath()
            // + File.separator + "tags.DE.xml");
            // firstTime = false;
            // }
            // List<TagSearchResult> tags = db.getTag(arg0.toString(), "de");
            // for (TagSearchResult tsr : tags) {
            // Log.d("DB_TEST", tsr.getKey() + "=" + tsr.getValue());
            // }

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_fulltextsearchactivity);
        setTitle(R.string.string_fulltextsearchActivity_title);

        EditText editBox = (EditText) findViewById(R.id.et_fulltextsearchfullActivity_search);
        editBox.addTextChangedListener(new MyTextWatcher(this));

    }
}
