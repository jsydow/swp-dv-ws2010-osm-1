package core.data;

import java.util.LinkedList;
import java.util.List;

public class DataNode extends DataMapObject{
	DataNode(int id) {
		super(id);
		media = new LinkedList<DataMedia>();
	}
	double lon;
	double lat;
	List<DataMedia> media;
}
