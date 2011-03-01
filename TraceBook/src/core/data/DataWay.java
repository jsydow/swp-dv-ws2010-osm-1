package core.data;

/**
 * A Way as in OSM. The start- and end-nodes are not connected.
 * @author js
 *
 */
class DataWay extends DataWayPointList implements SerialisableContent {

	/**
	 * 
	 * @param id id the internal id of this object
	 */
	DataWay(int id) {
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
		/* TODO STUB */
		return null;
	}

	public void serialise() {
		// TODO Auto-generated method stub
		
	}

	public void delete() {
		// TODO Auto-generated method stub
		
	}

}
