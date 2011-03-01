package core.data;

import java.util.List;

public class DataNode extends DataMapObject{
	DataNode(int id) {
		super(id);
		// TODO Auto-generated constructor stub
	}
	double lon;
	double lat;
	List<DataMedia> media;
}
