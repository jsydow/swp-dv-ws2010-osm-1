package core.data;

import java.util.LinkedList;
import java.util.List;

/**
 * A node. A node can be a POI or an element of a list of waypoints that belong
 * to a Way or Area. 
 * @author js
 *
 */
public class DataNode extends DataMapObject{
	
	/**
	 * 
	 * @param id the internal id of this object
	 */
	DataNode(int id) {
		super(id);
		media = new LinkedList<DataMedia>();
	}
	/**
	 * Longitude. OSM specifies 7 digits precision. Must be in interval [-180; 180]
	 * Note: This could also be stored as a tag lon=... . This is a matter of 
	 * taste that the gps coordinates are stored as double. It can still be changed
	 * if a string representation is more useful. 
	 */
	double lon;
	
	/**
	 * Latitude. OSM specifies 7 digits precision. Must be in interval [-90; 90]
	 * Note: This could also be stored as a tag lat=... . This is a matter of 
	 * taste that the gps coordinates are stored as double. It can still be changed
	 * if a string representation is more useful. 
	 */
	double lat;
	
	/**
	 * List of all media that belong to that object.
	 */
	List<DataMedia> media;
	
	/**
	 * This method loads a Node from the devices memory. It uses the 
	 * appropriate ContentProvider.
	 * Note: Currently a stub.
	 * Note: The parameter id may change if another name is better suited
	 * for retrieving the Node correctly.
	 *  
	 * @param id The id of the Node. It is not clear yet if what id or name is needed to load the Node correctly. 
	 * @return The deserialised DataNode object.
	 */
	static DataNode deserialise(String id){
		/* STUB */
		return null;
	}
}
