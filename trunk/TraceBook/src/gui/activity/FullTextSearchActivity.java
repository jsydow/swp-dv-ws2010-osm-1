package gui.activity;

import gui.adapter.GenericAdapter;
import gui.adapter.GenericAdapterData;
import gui.adapter.GenericItemDescription;

import java.util.ArrayList;
import java.util.List;

import Trace.Book.R;
import android.app.ListActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import core.data.db.TagDb;
import core.data.db.TagSearchResult;

/**
 * 
 *
 * 
 */
public class FullTextSearchActivity extends ListActivity {

    private int currResIndex;

    /**
     * A simple thread class which deals with search jobs in our database.
     * 
     * @author sahin
     * 
     */
    static class searchThread extends Thread {

        /**
         * Reference to the FullSearchActivity
         */
        FullTextSearchActivity act;

        /**
         * Current resolution index of this thread to prevent old thread from
         * overriding result form new threads.
         */
        int resIndex;

        /**
         * Reference to a search string.
         */
        String searchText;

        /**
         * 
         * The constructor takes different references to perform the search task
         * and to tell the gui to update its content.
         * 
         * @param act
         *            Reference to the FullTextSearchActivity
         * @param resIndex
         *            current result index
         * @param searchText
         *            search text to search for
         */
        public searchThread(FullTextSearchActivity act, int resIndex,
                String searchText) {
            this.act = act;
            this.resIndex = resIndex;
            this.searchText = searchText;
        }

        @Override
        public void run() {
            TagDb db = new TagDb(act);

            List<TagSearchResult> result = db.getTag(searchText, "de");
            db.closeDb();
            act.fillResultsToList(result, resIndex);

        }
    }

    /**
     * The text watcher tracks changes in the search edit box. As soon as some
     * text matches with the description text, the result will be displayed in
     * the list view.
     */
    static class MyTextWatcher implements TextWatcher {

        /**
         * Reference to the FullTextSearchActivity to update the list view.
         */
        FullTextSearchActivity act;

        /**
         * indicates whether the XML file containing tag names etc. has to be
         * read to update the database
         */
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
            final String searchText = arg0.toString();
            new searchThread(act, act.increaseIndex(), searchText).start();
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

    /**
     * Fill the ListView with the tag results. Since we are using threads to do
     * our search it can happen the we have multiple thread which are trying to
     * update the list view. To prevent this we generate result indexes to
     * prevent older thread from overwriting data from new tracks.
     * 
     * @param tags
     *            List of the TagSearchResult
     * @param resIndex
     *            current result index of the thread
     */
    public synchronized void fillResultsToList(List<TagSearchResult> tags,
            int resIndex) {

        // an old thread is trying to give us a result so we prevent it.
        if (getResIndex() < resIndex)
            return;

        GenericItemDescription desc = new GenericItemDescription();
        desc.addResourceId("Comment", R.id.tv_fulltextsearch_comment);
        desc.addResourceId("Category", R.id.tv_fulltextsearch_category);
        desc.addResourceId("Value", R.id.tv_fulltextsearch_value);

        List<GenericAdapterData> data = new ArrayList<GenericAdapterData>();

        for (TagSearchResult res : tags) {
            GenericAdapterData item = new GenericAdapterData(desc);

            item.setText("Comment", res.getDescription());
            item.setText("Category", res.getKey());
            item.setText("Value", res.getValue());
            data.add(item);
        }

        final GenericAdapter adapter = new GenericAdapter(this,
                R.layout.listview_fulltextsearch, R.id.list, data, null);

        this.runOnUiThread(new Runnable() {
            public void run() {
                setListAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

        });
    }

    /**
     * @return return the current result index
     */
    public synchronized int getResIndex() {
        return currResIndex;
    }

    /**
     * 
     * @return returns the next result index
     */
    public synchronized int increaseIndex() {
        return ++currResIndex;

    }
}
