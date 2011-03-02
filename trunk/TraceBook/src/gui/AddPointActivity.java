package gui;

import Trace.Book.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

public class AddPointActivity extends Activity{

	String[] firstGroupTags = {};
	String[] tagValues = {};
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addpointactivity);
        
      
        ArrayAdapter<String> firstGroupAdapter = new ArrayAdapter(this,
	            android.R.layout.simple_dropdown_item_1line, firstGroupTags);
        ArrayAdapter<String> valueTags = new ArrayAdapter(this,
	            android.R.layout.simple_dropdown_item_1line, tagValues);

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
