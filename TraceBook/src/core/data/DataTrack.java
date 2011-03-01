package core.data;

import java.util.LinkedList;
import java.util.List;

/**
 * A track. Consists of Ways, Areas, POIs and additional Media. 
 * @author js
 *
 */
class DataTrack implements SerialisableContent {
	List<DataMedia> media;
	List<DataArea> areas;
	List<DataWay> ways;
	List<DataNode> nodes;

	DataTrack() {
		media = new LinkedList<DataMedia>();
		areas = new LinkedList<DataArea>();
		ways = new LinkedList<DataWay>();
		nodes = new LinkedList<DataNode>();
	}

	public void serialise() {
		// TODO Auto-generated method stub
		
	}

	public void delete() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * This method loads a Track from the devices memory. It uses the 
	 * appropriate ContentProvider.
	 * Note: Currently a stub.
	 * Note: The parameter name may change if another name is better suited
	 * for retrieving the Track correctly.
	 *  
	 * @param name The name of the Track as stored on the memory. 
	 * @return The deserialised DataTrack object.
	 */
	static DataTrack deserialise(String name) {
		/* TODO STUB */
		return null;
	}

}
