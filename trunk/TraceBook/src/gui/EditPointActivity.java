package gui;

import Trace.Book.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TextView;

/**
 * The Class EditPointActivity.
 */
public class EditPointActivity extends Activity {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editpointactivity);
	}

	@Override
	public void registerForContextMenu(View view) {
		// TODO Auto-generated method stub
		TextView metaCategory = (TextView) findViewById(R.id.metaCategory_tv);
		TextView metaValue = (TextView) findViewById(R.id.metaValue_tv);

		super.registerForContextMenu(view);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.deleteTag_cm:

			return true;
		case R.id.renameTag_cm:

			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.editmeta_contextmenu, menu);
	}

}
