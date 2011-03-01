package core.data;

/**
 * A Way as in OSM. The start- and end-nodes are not connected.
 * @author js
 *
 */
public class DataWay extends DataWayPointList {

	/**
	 * 
	 * @param id id the internal id of this object
	 */
	public DataWay(int id) {
		super(id);
	}
	
	/**
	 * This method loads a Way from the devices memory. It uses the 
	 * appropriate ContentProvider.
	 * Note: Currently a stub.
	 * Note: The parameter id may change if another name is better suited
	 * for retrieving the Way correctly.
	 *  
	 * @param id The id of the Way. It is not clear yet if what id or name is needed to load the Way correctly. 
	 * @return The deserialised DataWay object.
	 */
	static DataWay deserialise(String id) {
		/* STUB */
		return null;
	}

}
