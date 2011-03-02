package gui;

import Trace.Book.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

public class AddPointActivity extends Activity{

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addpointactivity);
        
        final AutoCompleteTextView autoComplVal = (AutoCompleteTextView) findViewById(R.id.autoComplete_Value);
    	final AutoCompleteTextView autoComplCat = (AutoCompleteTextView) findViewById(R.id.autoComplete_Cat);
    	
        ArrayAdapter<String> firstGroupAdapter = new ArrayAdapter(this,
	            android.R.layout.simple_dropdown_item_1line, getCategoryTags());
        autoComplCat.setAdapter(firstGroupAdapter);

        
        /**
         * If the focus is at the AutoCompleteTextView autoComplVal 
         * we call the method getValues to generate the AutoComplete String[]
         */
        autoComplVal.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
    			String cat = autoComplCat.getText().toString();
    		/*	autoComplVal.setText(cat.toCharArray(),0,cat.length());
    	        ArrayAdapter<String> valueTagAdapter = new ArrayAdapter(this,
    		            android.R.layout.simple_dropdown_item_1line, getValues(cat));
    	        autoComplVal.setAdapter(valueTagAdapter);*/
				}
			}
		}); 

    }
 
	/**
	 * Give all the tag category's from the Tag-XML.
	 * @return
	 */
	public String[] getCategoryTags(){
		// Testarray
		String[] firstGroupTags = {"Test", "Tast", "Tost", "Temst", "Tunnel", "Tag", "Titel", "Tjkld", "Tsipoaid", "Taskjk"};
		return  firstGroupTags;
	}
	
	/**
	 * Generate the appendant values for the category tag. 
	 * @param category
	 * @return
	 */
	public String[] getValues(String category){
		//return Testarray
		String[] valueTags = {"jdakls", "jkal", "jdlaks", "jdkas", "jdlka"};
		return valueTags;
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
