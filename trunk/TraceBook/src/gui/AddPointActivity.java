package gui;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import Trace.Book.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import core.data.DataMapObject;
import core.data.DataStorage;
import core.logger.ServiceConnector;

/**
 * @author greentraxas
 * The purpose of this activity is to add and edit tags to an DataMapObject where
 * an DataMapObject can be anything from poi to area.
 *
 */
public class AddPointActivity extends Activity {
	
    
	/**
	 * Here we save a reference to the current DataMapObject which is in use
	 */
	DataMapObject node;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Bundle extras = getIntent().getExtras();
		
		/*
		 * Get the node of the sending Intent
		 */
		if (extras != null) {

			if (extras.containsKey("DataNodeId")) {				
				int nodeId = extras.getInt("DataNodeId");
				node = DataStorage.getInstance().getCurrentTrack().getDataMapObjectById(nodeId);
			}
			
		}

		setContentView(R.layout.addpointactivity);

		// Init ServiceConnector
		ServiceConnector.startService(this);
		ServiceConnector.initService();

		getNodeInformation();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, getNodeInformation());
		listNodeInformation(adapter);

	}

	/**
	 * Get the last node and create a String-Array with all MetaData from the
	 * Meta-HashMap of this Node. If the HashMap contain no MetaData, the method
	 * returns an empty array.
	 * 
	 * @return A Array of Strings, for the Content of the ListeView
	 *         allocateMeta_lv
	 */
	private String[] getNodeInformation() {

		String meta = null;
		TextView nodeIdTv = (TextView) findViewById(R.id.nodeId_tv);
		TextView nodeInfo = (TextView) findViewById(R.id.allocateMeta_tv);
		int i = 0;

		Map<String, String> tagMap = node.getTags();
		String[] metaInformation = new String[tagMap.size()];
		nodeIdTv.setText(getResources().getString(R.string.nodeId_tv) + " " + node.getId());

		if (tagMap.size() != 0) {
			nodeInfo.setText(R.string.MetaData_tv);
			Iterator<Entry<String, String>> iterator = tagMap.entrySet()
					.iterator();

			while (iterator.hasNext()) {
				Map.Entry<String, String> pairs = iterator.next();
				meta = pairs.getKey() + " - " + pairs.getValue();
				metaInformation[i] = meta;
				i++;
			}

		} else {
			nodeInfo.setText(R.string.noMetaData_tv);

		}

		return metaInformation;
	}

	/**
	 * This Method create the ListView with the gernerated Adapter
	 * 
	 * @param adapter
	 */
	private void listNodeInformation(final ArrayAdapter<String> adapter) {
		final Intent intent = new Intent(this, AddPointMetaActivity.class);
		ListView listView = (ListView) findViewById(R.id.allocateMeta_lv);
		listView.setAdapter(adapter);
		listView.setTextFilterEnabled(true);

		// Get selected item and send toast
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				intent.putExtra("DataNodeId", node.getId());
				intent.putExtra("DataNodeKey", adapter.getItem(position));
				startActivity(intent);
				Toast.makeText(getApplicationContext(),
						((TextView) view).getText(), Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 
	 * @param view not used
	 */
	public void addPointMetaBtn(View view) { // method signature including view
												// is required
		final Intent intent = new Intent(this, AddPointMetaActivity.class);
		
		
		intent.putExtra("DataNodeId", node.getId());
		startActivity(intent);
	}

	/**
	 * 
	 * @param view not used
	 */
	public void cancelBtn(View view) { // method signature including view is
										// required
		final Intent intent = new Intent(this, NewTrackActivity.class);
		startActivity(intent);
		finish();
	}

}
