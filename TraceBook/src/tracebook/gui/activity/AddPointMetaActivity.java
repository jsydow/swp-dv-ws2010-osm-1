package tracebook.gui.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import tracebook.core.data.DataMapObject;
import tracebook.core.data.DataStorage;
import tracebook.core.data.db.HistoryDb;
import tracebook.core.data.db.TagSearchResult;
import tracebook.gui.adapter.GenericAdapter;
import tracebook.gui.adapter.GenericAdapterData;
import tracebook.gui.adapter.GenericItemDescription;
import tracebook.util.Helper;
import Trace.Book.R;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import tracebook.util.LogIt;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

/**
 * In this Activity you can choose your Tags via an AutoComplete feature. Tags
 * and values are grouped together. So the AutoComplete feature for values are
 * depended by their key values. For example when you choose "highway" only
 * highway related values will be given as a list for AutoComplete.
 */
public class AddPointMetaActivity extends ListActivity {

    /**
     * A simple enumeration class for tags.
     */
    enum Tags {
        /**
         * 
         */
        KEY,
        /**
         * 
         */
        USEFUL,
        /**
         * 
         */
        VALUE
    }

    private GenericAdapter adapter;

    /**
     * Reference to the current DataMapObject in use.
     */
    DataMapObject node;

    /**
     * 
     * @param view
     *            not used
     */
    public void cancelBtn(View view) {
        finish();
    }

    /**
     * Give all the tag category's from the Tag-XML.
     * 
     * @return a string array containing the category's
     */
    public String[] getCategoryTags() {
        // Test array
        String[] firstGroupTags = parseTags(Tags.KEY, "");
        return firstGroupTags;
    }

    /**
     * Generate the all linked values for the category tag.
     * 
     * @param category
     *            Category to get the values from
     * @return return a string array with values for the given category
     */
    public String[] getValues(String category) {
        // return the value tags for the selected category tag
        String[] valueTags = parseTags(Tags.VALUE, category);
        return valueTags;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpointmetaactivity);
        setTitle(R.string.string_addpointmetaActivity_title);

        final AutoCompleteTextView autoComplVal = (AutoCompleteTextView) findViewById(R.id.ac_addpointmetaActivity_value);
        final AutoCompleteTextView autoComplCat = (AutoCompleteTextView) findViewById(R.id.ac_addpointmetaActivity_categorie);

        final Bundle extras = getIntent().getExtras();

        if (extras != null) {
            int nodeId = extras.getInt("DataNodeId");
            node = DataStorage.getInstance().getCurrentTrack()
                    .getDataMapObjectById(nodeId);

            if (extras.containsKey("DataNodeKey")) {
                String key = extras.getString("DataNodeKey");
                autoComplCat.setText(key);
            }
            if (extras.containsKey("DataNodeValue")) {
                String value = extras.getString("DataNodeValue");
                autoComplVal.setText(value);
            }

            ArrayAdapter<String> firstGroupAdapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_dropdown_item_1line,
                    getCategoryTags());
            autoComplCat.setAdapter(firstGroupAdapter);

            /**
             * If the focus is at the AutoCompleteTextView autoComplVal we call
             * the method getValues to generate the AutoComplete String[]
             */
            autoComplVal.setOnFocusChangeListener(new OnFocusChangeListener() {

                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        String cat = autoComplCat.getText().toString();

                        // autoComplVal.setText(cat.toCharArray(),0,cat.length());
                        ArrayAdapter<String> valueTagAdapter = new ArrayAdapter<String>(
                                v.getContext(),
                                android.R.layout.simple_dropdown_item_1line,
                                getValues(cat));
                        autoComplVal.setAdapter(valueTagAdapter);

                    }
                }
            });
        }

        fillListView();

        // Set status bar
        Helper.setStatusBar(
                this,
                getResources().getString(
                        R.string.tv_statusbar_addpointmetaTitle),
                getResources()
                        .getString(R.string.tv_statusbar_addpointmetaDesc),
                R.id.ly_addpointMetaAtivity_statusbar, false);

    }

    /**
     * Save the MetaData to the Node-Meta and go back to the information
     * activity.
     * 
     * @param view
     *            not used
     */
    public void saveBtn(View view) {
        final AutoCompleteTextView autoComplVal = (AutoCompleteTextView) findViewById(R.id.ac_addpointmetaActivity_value);
        final AutoCompleteTextView autoComplCat = (AutoCompleteTextView) findViewById(R.id.ac_addpointmetaActivity_categorie);

        if (node != null) {
            node.getTags().put(autoComplCat.getText().toString(),
                    autoComplVal.getText().toString());
        }
        HistoryDb db = new HistoryDb(this);
        db.updateTag(autoComplCat.getText().toString(), autoComplVal.getText()
                .toString());
        finish();
    }

    /**
     * Start FullTextSearch Activity.
     * 
     * @param view
     *            not used
     */
    public void searchBtn(View view) {
        final Intent intent = new Intent(AddPointMetaActivity.this,
                FullTextSearchActivity.class);
        // It is important to use a number > -1 to get the
        // startActivityForResult working right
        startActivityForResult(intent, 0);

    }

    /**
     * The Method for the preference image Button from the status bar. The
     * Method starts the PreferenceActivity.
     * 
     * @param view
     *            not used
     */
    public void statusBarPrefBtn(View view) {
        final Intent intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
    }

    /**
     * This Method for the two (title and description) button from the status
     * bar. This method starts the dialog with all activity informations.
     * 
     * @param v
     *            not used
     */
    public void statusBarTitleBtn(View v) {
        Helper.setActivityInfoDialog(
                this,
                getResources().getString(
                        R.string.tv_statusbar_addpointmetaTitle),
                getResources()
                        .getString(R.string.tv_statusbar_addpointmetaDesc));
    }

    private void fillListView() {
        HistoryDb db = new HistoryDb(this);
        // TODO user can change most used vs recently used
        List<TagSearchResult> result = db.getHistory(false, 10);

        GenericItemDescription desc = new GenericItemDescription();
        desc.addResourceId("Key", R.id.tv_history_key);
        desc.addResourceId("Value", R.id.tv_history_value);

        List<GenericAdapterData> data = new ArrayList<GenericAdapterData>();

        for (TagSearchResult res : result) {
            GenericAdapterData item = new GenericAdapterData(desc);

            item.setText("Key", res.getKey());
            item.setText("Value", res.getValue());
            data.add(item);
        }

        adapter = new GenericAdapter(this, R.layout.listview_taghistory,
                R.id.list, data);

        setListAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    /**
     * This method parses all the tags in the XML-MetaTag files.
     * 
     * @param tagType
     *            tag type in which we are interested in
     * @param parentName
     *            parent name means the tag value for which we are searching
     * @return a String array with all tags
     */
    private String[] parseTags(Tags tagType, String parentName) {
        int next;
        boolean inParent = false;
        XmlResourceParser parser = this.getResources().getXml(R.xml.tags);
        ArrayList<String> tagStrings = new ArrayList<String>();

        try {
            String tag = "";
            next = parser.getEventType();
            while (next != XmlPullParser.END_DOCUMENT) {
                if (next == XmlPullParser.START_TAG) {
                    tag = parser.getName();
                    switch (tagType) {
                    case KEY:
                        if (tag.equals("key")) {
                            tagStrings.add(parser.getAttributeValue(null, "v"));
                        }
                        break;
                    case VALUE:
                        if ((tag.equals("key"))
                                && (parser.getAttributeValue(null, "v"))
                                        .equals(parentName)) {
                            inParent = true;
                        } else if ((tag.equals("key"))
                                && !(parser.getAttributeValue(null, "v"))
                                        .equals(parentName)) {
                            inParent = false;
                        } else if (inParent && (tag.equals("value"))) {
                            tagStrings.add(parser.getAttributeValue(null, "v"));
                        }
                        break;
                    case USEFUL:
                        if ((tag.equals("value"))
                                && (parser.getAttributeValue(null, "v"))
                                        .equals(parentName)) {
                            inParent = true;
                        } else if ((tag.equals("value"))
                                && !(parser.getAttributeValue(null, "v"))
                                        .equals(parentName)) {
                            inParent = false;
                        } else if (inParent && (tag.equals("useful"))) {
                            tagStrings.add(parser.getAttributeValue(null, "v"));
                        }
                        break;
                    default:
                        break;
                    }
                }

                next = parser.next();
            }

        } catch (XmlPullParserException xe) {
            parser.close();
            LogIt.e("PARSE_TAGS", "Couldn't parse tags from xml");
            return new String[0];
        } catch (IOException e) {
            parser.close();
            LogIt.e("PARSE_TAGS", "Couldn't parse tags from xml");
            return new String[0];
        } finally {
            parser.close();
        }
        String[] tagStringsArray = new String[tagStrings.size()];
        return tagStrings.toArray(tagStringsArray);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // data == null when the user has used the back button to exit the
        // previous activity
        if (data == null)
            return;

        final AutoCompleteTextView autoComplVal = (AutoCompleteTextView) findViewById(R.id.ac_addpointmetaActivity_value);
        final AutoCompleteTextView autoComplCat = (AutoCompleteTextView) findViewById(R.id.ac_addpointmetaActivity_categorie);

        final Bundle extras = data.getExtras();

        if (extras != null) {
            if (extras.containsKey("DataNodeKey")) {
                String key = extras.getString("DataNodeKey");
                autoComplCat.setText(key);
            }
            if (extras.containsKey("DataNodeValue")) {
                String value = extras.getString("DataNodeValue");
                autoComplVal.setText(value);
            }
        }

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        final AutoCompleteTextView autoComplVal = (AutoCompleteTextView) findViewById(R.id.ac_addpointmetaActivity_value);
        final AutoCompleteTextView autoComplCat = (AutoCompleteTextView) findViewById(R.id.ac_addpointmetaActivity_categorie);

        GenericAdapterData data = adapter.getItem(position);
        autoComplVal.setText(data.getText("Value"));
        autoComplCat.setText(data.getText("Key"));
        // TODO fill key + value edit

        super.onListItemClick(l, v, position, id);
    }

}
