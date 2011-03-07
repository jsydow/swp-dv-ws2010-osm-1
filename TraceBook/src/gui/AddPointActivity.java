package gui;

import Trace.Book.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AddPointActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addpointactivity);

	}
	
	public void addPointMetaBtn(View view){
		final Intent intent = new Intent(this,AddPointMetaActivity.class);
		startActivity(intent);
	}
	
	public void cancelBtn(View view) {
		final Intent intent = new Intent(this, NewTrackActivity.class);
		startActivity(intent);
	}

}
