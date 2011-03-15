package gui.activity;

import Trace.Book.R;
import android.app.ListActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

/**
 * 
 * @author Sahin
 * 
 */
public class FullTextSearchActivity extends ListActivity {

    /**
     * The text watcher tracks changes in the search edit box. As soon as some
     * text matches with the description text, the result will be displayed in
     * the listview.
     * 
     * @author sahin
     * 
     */
    static class MyTextWatcher implements TextWatcher {

        /**
         * reference to the FullTextSearchActivity to update the list view.
         */
        FullTextSearchActivity act;

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

            Log.d(ACTIVITY_SERVICE, arg0.toString());
            // TODO Auto-generated method stub

        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_fulltextsearchactivity);

        EditText editBox = (EditText) findViewById(R.id.et_fulltextsearchfullActivity_search);
        editBox.addTextChangedListener(new MyTextWatcher(this));

    }
}
