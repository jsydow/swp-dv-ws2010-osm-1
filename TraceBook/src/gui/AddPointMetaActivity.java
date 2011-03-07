package gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import core.data.DataNode;
import core.data.DataStorage;

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
import android.widget.Toast;


public class AddPointMetaActivity extends Activity {

		/**
		 * fixed Integer-values for tag types, used in parseTags()
		 */
		static final short KEY = 0;
		static final short VALUE = 1;
		static final short USEFUL = 2;
		int nodeID;
		
		DataNode node;
		
		XmlResourceParser parser;
		

		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			
				if( DataStorage.getInstance().getCurrentTrack() != null ){
		       		List<DataNode> data = DataStorage.getInstance().getCurrentTrack().getNodes();
		       		if( data.size() != 0 )
		       			node = data.get(data.size()-1);
		       		else
		       			Toast.makeText(this, "No Node tracked yet", Toast.LENGTH_SHORT).show();
		        		
		        }
			 
			super.onCreate(savedInstanceState);
			setContentView(R.layout.addpointmetaactivity);

			final AutoCompleteTextView autoComplVal = (AutoCompleteTextView) findViewById(R.id.autoComplete_Value);
			final AutoCompleteTextView autoComplCat = (AutoCompleteTextView) findViewById(R.id.autoComplete_Cat);

			ArrayAdapter<String> firstGroupAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_dropdown_item_1line, getCategoryTags());
			autoComplCat.setAdapter(firstGroupAdapter);

			/**
			 * If the focus is at the AutoCompleteTextView autoComplVal we call the
			 * method getValues to generate the AutoComplete String[]
			 */
			autoComplVal.setOnFocusChangeListener(new OnFocusChangeListener() {

				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						String cat = autoComplCat.getText().toString();
						
						 //autoComplVal.setText(cat.toCharArray(),0,cat.length());
						 ArrayAdapter<String> valueTagAdapter = new ArrayAdapter<String>(v.getContext(),android.R.layout.simple_dropdown_item_1line,getValues(cat));
						 autoComplVal.setAdapter(valueTagAdapter);
						 
					}
				}
			});
	}
	
	/**
	 * Give all the tag category's from the Tag-XML.
	 * 
	 * @return
	 */
	public String[] getCategoryTags() {
		// Testarray
		String[] firstGroupTags = parseTags(KEY,"");
		return firstGroupTags;
	}
	
	/**
	 * Generate the appendant values for the category tag.
	 * 
	 * @param category
	 * @return
	 */
	public String[] getValues(String category) {
		// return the value tags for the selected category tag
		String[] valueTags =parseTags(VALUE, category);
		return valueTags;
	}

	/**
	 * Save the MetaData to the Node-Meta and go back to the information
	 * activity.
	 * @param view
	 */
	public void saveBtn(View view) {
		
		final AutoCompleteTextView autoComplVal = (AutoCompleteTextView) findViewById(R.id.autoComplete_Value);
		final AutoCompleteTextView autoComplCat = (AutoCompleteTextView) findViewById(R.id.autoComplete_Cat);
		
		if( node != null) {
			
			node.getTags().put( autoComplCat.getText().toString(),
								autoComplVal.getText().toString());
						
		}	
		
		final Intent intent = new Intent(this, AddPointActivity.class);
		startActivity(intent);
	}

	
	public void cancelBtn(View view) {
		final Intent intent = new Intent(this, AddPointActivity.class);
		startActivity(intent);
	}

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
