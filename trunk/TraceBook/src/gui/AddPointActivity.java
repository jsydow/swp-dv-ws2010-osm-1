package gui;

import java.util.Iterator;
import java.util.List;
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
import core.data.DataNode;
import core.data.DataStorage;
import core.logger.ServiceConnector;

public class AddPointActivity extends Activity {

	ListView listView;
	TextView nodeId;
	TextView nodeInfo;
	DataNode node;
	String[] metaInformation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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

		String meta = new String();
		nodeId = (TextView) findViewById(R.id.nodeId_tv);
		nodeInfo = (TextView) findViewById(R.id.allocateMeta_tv);
		List<DataNode> nodeList = DataStorage.getInstance().getCurrentTrack()
				.getNodes();
		int i = 0;
		if (nodeList.size() != 0) {
			node = nodeList.get(nodeList.size() - 1);
			Map<String, String> tagMap = node.getTags();
			metaInformation = new String[tagMap.size()];
			nodeId.setText(R.id.nodeId_tv + " " + node.get_id());

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

		} else {
			Toast.makeText(this, "No Node tracked yet", Toast.LENGTH_SHORT)
					.show();
		}

		return metaInformation;
	}

	/**
	 * This Method create the ListView with the gernerated Adapter
	 * 
	 * @param adapter
	 */
	private void listNodeInformation(ArrayAdapter<String> adapter) {
		listView = (ListView) findViewById(R.id.allocateMeta_lv);
		listView.setAdapter(adapter);
		listView.setTextFilterEnabled(true);

		// Get selected item and send toast
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(getApplicationContext(),
						((TextView) view).getText(), Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void addPointMetaBtn(View view) { // method signature including view
												// is required
		final Intent intent = new Intent(this, AddPointMetaActivity.class);
		startActivity(intent);
	}

	public void cancelBtn(View view) { // method signature including view is
										// required
		final Intent intent = new Intent(this, NewTrackActivity.class);
		startActivity(intent);
		finish();
	}

}
