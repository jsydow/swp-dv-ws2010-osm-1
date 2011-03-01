package core.data;

import java.util.LinkedList;
import java.util.List;

public abstract class DataWayPointList extends DataMapObject{
	List<DataNode> nodes;
	List<DataMedia> media;
	
	public DataWayPointList(int id) {
		super(id);
		nodes = new LinkedList<DataNode>();
		media = new LinkedList<DataMedia>();
	}
}
