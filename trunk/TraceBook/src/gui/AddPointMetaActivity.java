package gui;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import Trace.Book.R;
import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import core.data.DataMapObject;
import core.data.DataStorage;

/**
 * In this Activty you can choose your Tags via an autocomplete feature. Tags
 * and values are grouped together. So the autocomplete feature for values are
 * depended by their key values. For example when you choose "highway" only
 * highway related values will be given as a list for autocomplete.
 * 
 * @author greentraxas
 */
public class AddPointMetaActivity extends Activity {

    /**
     * @author greentraxas A simple enum class for tags
     */
    enum Tags {
        /**
         * 
         */
        KEY,
        /**
         * 
         */
        VALUE,
        /**
         * 
         */
        USEFUL

    }

    /**
     * Reference to the current DataMapObject in use.
     */
    DataMapObject node;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addpointmetaactivity);

        final AutoCompleteTextView autoComplVal = (AutoCompleteTextView) findViewById(R.id.autoComplete_Value);
        final AutoCompleteTextView autoComplCat = (AutoCompleteTextView) findViewById(R.id.autoComplete_Cat);

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
    }

    /**
     * Give all the tag category's from the Tag-XML.
     * 
     * @return a string array containing the category's
     */
    public String[] getCategoryTags() {
        // Testarray
        String[] firstGroupTags = parseTags(Tags.KEY, "");
        return firstGroupTags;
    }

    /**
     * Generate the appendant values for the category tag.
     * 
     * @param category
     * @return return a string array with values for the given category
     */
    public String[] getValues(String category) {
        // return the value tags for the selected category tag
        String[] valueTags = parseTags(Tags.VALUE, category);
        return valueTags;
    }

    /**
     * Save the MetaData to the Node-Meta and go back to the information
     * activity.
     * 
     * @param view
     */
    public void saveBtn(View view) {

        final AutoCompleteTextView autoComplVal = (AutoCompleteTextView) findViewById(R.id.autoComplete_Value);
        final AutoCompleteTextView autoComplCat = (AutoCompleteTextView) findViewById(R.id.autoComplete_Cat);

        if (node != null) {

            node.getTags().put(autoComplCat.getText().toString(),
                    autoComplVal.getText().toString());

        }

        finish();
    }

    /**
     * 
     * @param view
     *            not used
     */
    public void cancelBtn(View view) { // method signature including view is
        // required
        finish();
    }

    /**
     * This method parses all the tags in the XML-MetaTag files.
     * 
     * @param tagType
     * @param parentName
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
            Log.e("PARSE_TAGS", "Couldn't parse tags from xml");
            return new String[0];
        } catch (IOException e) {
            parser.close();
            Log.e("PARSE_TAGS", "Couldn't parse tags from xml");
            return new String[0];
        } finally {
            parser.close();
        }
        String[] tagStringsArray = new String[tagStrings.size()];
        return tagStrings.toArray(tagStringsArray);
    }

}
