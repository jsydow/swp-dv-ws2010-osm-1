package gui;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import Trace.Book.R;
import android.app.Activity;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

/**
 * The Class AddPointActivity. This activity is evoked, when new points are added (ways, areas or POI).
 * First the user chooses a category (e.g. highway) and than a value (e.g. motorway). The user is aided 
 * by automatic suggestions for the input as suggested in http://wiki.openstreetmap.org/wiki/Map_Features. 
 * TODO input of multiple tags for a point will be added in the next version
 */
public class AddPointActivity extends Activity {

	/** fixed Integer-values for tag types, used in parseTags(). */
	static final short KEY = 0;
	static final short VALUE = 1;
	static final short USEFUL = 2;

	/** The node id. */
	int nodeID;

	/** The tag parser. */
	XmlResourceParser parser;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Create Bundle for extras of the intent
		final Bundle extras = getIntent().getExtras();
		// Get nodeID of the intent.extras
		if (extras != null) {
			nodeID = extras.getInt("NodeId");
		}

		
		setContentView(R.layout.addpointactivity);
		//get the AutocompletViews for Tag Values and Categories
		final AutoCompleteTextView autoComplVal = (AutoCompleteTextView) findViewById(R.id.autoComplete_Value);
		final AutoCompleteTextView autoComplCat = (AutoCompleteTextView) findViewById(R.id.autoComplete_Cat);
		//set autocomplete options for category
		ArrayAdapter<String> firstGroupAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, getCategoryTags());
		autoComplCat.setAdapter(firstGroupAdapter);

		/*
		 * If the focus is at the AutoCompleteTextView autoComplVal we call the
		 * method getValues to generate the AutoComplete String[] for all useful values 
		 * corrsponding to the current category
		 */
		autoComplVal.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					String cat = autoComplCat.getText().toString();
					ArrayAdapter<String> valueTagAdapter = new ArrayAdapter<String>(v
							.getContext(),
							android.R.layout.simple_dropdown_item_1line,
							getValues(cat));
					autoComplVal.setAdapter(valueTagAdapter);

				}
			}
		});

	}

	/**
	 * Give all the tag category's from the Tag-XML.	 * 
	 * @return the category tags
	 */
	private String[] getCategoryTags() {
		return parseTags(KEY, "");
	}

	/**
	 * Generate the appendant values for the category tag.
	 * @param category the category
	 * @return the values
	 */
	private String[] getValues(String category) {
		return parseTags(VALUE, category);
	}

	/**
	 * Save btn.
	 * 
	 * @param view
	 *            the view
	 */
	public void saveBtn(View view) {
		final Intent intent = new Intent(this, NewTrackActivity.class);
		startActivity(intent);
	}

	/**
	 * Cancel btn.
	 * 
	 * @param view
	 *            the view
	 */
	public void cancelBtn(View view) {
		final Intent intent = new Intent(this, NewTrackActivity.class);
		startActivity(intent);
	}

	/**
	 * Parses the tags.
	 * 
	 * @param tagType
	 *            the tag type
	 * @param parentName
	 *            the parent name
	 * @return the string[]
	 */
	private String[] parseTags(int tagType, String parentName) {
		int next;
		boolean inParent = false;
		parser = this.getResources().getXml(R.xml.tags);
		ArrayList<String> tagStrings = new ArrayList<String>();

		try {
			String tag = "";
			next = parser.getEventType();
			while (next != XmlPullParser.END_DOCUMENT) {
				if (next == XmlPullParser.START_TAG) {
					tag = parser.getName();
					switch (tagType) {
					case KEY: {
						if (tag.equals("key")) {
							tagStrings.add(parser.getAttributeValue(null, "v"));
						}
						break;
					}
					case VALUE: {
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
					}
					case USEFUL: {
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
					}
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
