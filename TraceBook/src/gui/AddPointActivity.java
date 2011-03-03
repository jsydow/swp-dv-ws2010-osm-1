package gui;

import Trace.Book.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

public class AddPointActivity extends Activity{


	String[] valueTags = {"jdakls", "jkal", "jdlaks", "jdkas", "jdlka"};
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addpointactivity);
    	
        final AutoCompleteTextView autoComplVal = (AutoCompleteTextView) findViewById(R.id.autoComplete_Value);
    	final AutoCompleteTextView autoComplCat = (AutoCompleteTextView) findViewById(R.id.autoComplete_Cat);
        final ArrayAdapter<String> firstGroupAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getCategoryTags());
		final ArrayAdapter<String> valueTagAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, valueTags);
        autoComplCat.setAdapter(firstGroupAdapter);


        
        /**
         * If the focus is at the AutoCompleteTextView autoComplVal 
         * we call the method getValues to generate the AutoComplete String[]
         */
        autoComplVal.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
			        autoComplVal.setAdapter(valueTagAdapter);
				}
			}
		}); 

    }
 
	/**
	 * Give all the tag category's from the Tag-XML.
	 * @return
	 */
	protected String[] getCategoryTags(){
		// Testarray, will replace with the tags of the tags.xml
		String[] firstGroupTags = {"Test", "Tast", "Tost", "Temst", "Tunnel", "Tag", "Titel", "Tjkld", "Tsipoaid", "Taskjk"};
		return  firstGroupTags;
	}
	
    public void saveBtn(View view){
    	final Intent intent = new Intent (this, NewTrackActivity.class);
    	startActivity(intent);
    }
    
    public void cancelBtn(View view){
    	final Intent intent = new Intent (this, NewTrackActivity.class);
    	startActivity(intent);
    }
    
    
}
