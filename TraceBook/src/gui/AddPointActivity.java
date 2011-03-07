package gui;

import java.util.List;

import core.logger.ServiceConnector;
import Trace.Book.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import core.data.DataNode;
import core.data.DataStorage;

public class AddPointActivity extends Activity {

	TextView textView;
	DataNode node;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addpointactivity);

		//Init ServiceConnector
        ServiceConnector.startService(this);
        ServiceConnector.initService();
        
        listNodeInformation();
	}
	
	public void listNodeInformation(){
		textView = (TextView) findViewById(R.id.nodeTags);
		List<DataNode> nodeList = DataStorage.getInstance().getCurrentTrack().getNodes();
		if( nodeList.size() != 0 ){
   			node = nodeList.get(nodeList.size()-1);
			textView.setText("NodeID: " + node.get_id());
		}
   		else
   			Toast.makeText(this, "No Node tracked yet", Toast.LENGTH_SHORT).show();
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
