package core.data;

import java.util.LinkedList;
import java.util.List;

/**
 * A track. Consists of Ways, Areas, POIs and additional Media. 
 * @author js
 *
 */
class DataTrack implements SerialisableContent {
	private List<DataMedia> media;
	private List<DataArea> areas;
	private List<DataWay> ways;
	private List<DataNode> nodes;
	
	String name;
	String comment;
	String datetime;

	DataTrack(String datetime) {
		media = new LinkedList<DataMedia>();
		areas = new LinkedList<DataArea>();
		ways = new LinkedList<DataWay>();
		nodes = new LinkedList<DataNode>();
		
		if(datetime != null) {
			this.datetime = datetime;
		} else {
			// TODO set Time
		}
		this.name = this.datetime;
	}

	DataTrack(String datetime, String name) {
		this(datetime);
		this.name = name;
	}

	DataTrack(String datetime, String name, String comment) {
		this(datetime, name);
		this.comment = comment;
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
	 * @return The deserialised DataTrack object or null if such a Track does not exist
	 */
	static DataTrack deserialise(String name) {
		/* TODO STUB */
		return null;
	}
	
	/**
	 * This method returns a list of the name of all Tracks that are stored on 
	 * the devices memory. These names can be used to deserialise a Track.
	 * @return A list of the names of all available Tracks 
	 */
	static List<String> allTracks(){
		/* TODO STUB */
		return null;
	}

}
