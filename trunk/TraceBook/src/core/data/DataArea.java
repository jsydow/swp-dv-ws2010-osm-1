package core.data;

/**
 * An area. It is a closed way with same starting and end-node. In OSM
 * such an Area is a Way with landuse=... and/or area=yes 
 * @author js
 *
 */
class DataArea extends DataWayPointList {

	/**
	 * 
	 * @param id id the internal id of this object
	 */
	DataArea(int id) {
		super(id);
	}
	
	/**
	 * This method loads an Area from the devices memory. It uses the 
	 * appropriate ContentProvider.
	 * Note: Currently a stub.
	 * Note: The parameter id may change if another name is better suited
	 * for retrieving the Area correctly.
	 *  
	 * @param id The id of the Area. It is not clear yet if what id or name is needed to load the Area correctly. 
	 * @return The deserialised DataArea object.
	 */
	static DataArea deserialise(String id) {
		/* STUB */
		return null;
	}

}
