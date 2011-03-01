package core.data;

import java.util.LinkedList;
import java.util.List;

/**
 * WayPointList objects are any objects that consist of a series of nodes
 * like Areas and Ways. 
 * @author js
 *
 */
public abstract class DataWayPointList extends DataMapObject{
	/**
	 * The list of nodes of this object. First node is first element
	 * in this list. 
	 */
	List<DataNode> nodes;
	
	/**
	 * List of all media that belong to that object.
	 */
	List<DataMedia> media;
	
	/**
	 * 
	 * @param id the internal id of this object
	 */
	public DataWayPointList(int id) {
		super(id);
		nodes = new LinkedList<DataNode>();
		media = new LinkedList<DataMedia>();
	}
}
