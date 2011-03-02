package core.data;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * A track. Consists of Ways, Areas, POIs and additional Media. 
 * @author js
 *
 */
public class DataTrack extends DataMediaHolder implements SerialisableContent {
	
	private List<DataPointsList> ways;
	private List<DataNode> nodes;
	
	private DataPointsList currentWay;
	
	private String name;
	private String comment;
	private String datetime;

	DataTrack(String datetime) {
		ways = new LinkedList<DataPointsList>();
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
	
	

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the nodes
	 */
	public List<DataNode> getNodes() {
		return nodes;
	}

	/**
	 * @return the ways
	 */
	public List<DataPointsList> getWays() {
		return ways;
	}
	
	public DataNode newNode() {
		 DataNode dn = new DataNode();
		 nodes.add(dn);
		 return dn;
	 }
	 
	 public void deleteNode(int id) {
		 ListIterator<DataNode> lit = nodes.listIterator();
		 DataNode dn;
		 while(lit.hasNext()) {
			 dn = lit.next();
			 if( dn.get_id() == id ) {
				 dn.delete();
				 lit.remove();
				 break;
			 }
		 }
	 }
	 
	 public DataPointsList newWay() {
		 DataPointsList dpl = new DataPointsList();
		 ways.add(dpl);
		 return dpl;
	 }
	 
	 public void deleteWay(int id) {
		 ListIterator<DataPointsList> lit = ways.listIterator();
		 DataPointsList dpl;
		 while(lit.hasNext()) {
			 dpl = lit.next();
			 if( dpl.get_id() == id ) {
				 dpl.delete();
				 lit.remove();
				 break;
			 }
		 }
	 }

	/**
	 * @return the datetime
	 */
	public String getDatetime() {
		return datetime;
	}

	public void serialise() {
		// TODO Auto-generated method stub
		
	}

	public void delete() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @return the currentWay
	 */
	public DataPointsList getCurrentWay() {
		return currentWay;
	}

	/**
	 * @param currentWay the currentWay to set
	 */
	public DataPointsList setCurrentWay(DataPointsList currentWay) {
		this.currentWay = currentWay;
		return currentWay;
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
	public static List<String> allTracks(){
		/* TODO STUB */
		return null;
	}

}
