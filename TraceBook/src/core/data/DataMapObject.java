package core.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Basic class for any object that is stored in OSM. All objects have an id and
 * some tags. Additionally all objects can have media attached.
 * 
 * @author js
 * 
 */
public abstract class DataMapObject extends DataMediaHolder  implements Comparable<Integer> {
	/**
	 * An id for this object. It is not an id for osm which is set to -1 for all
	 * new objects but it is a program internal id. It should be unique.
	 * DataStorage.getID() creates one.
	 */
	protected int _id;

	/**
	 * tags stores all meta information of this object. These may be the name,
	 * timestamp, latitude, longitude and osm-tags. tags is not equivalent to
	 * osm-tags as these tags also store the timestamp and gps coordinates etc.
	 */
	protected Map<String, String> tags;

	/**
	 * Getter-method
	 * 
	 * @return the id of the object
	 */
	public int get_id() {
		return _id;
	}

	/**
	 * Getter-method for a all tags stored as a Map of String. Tags that are no
	 * tags in osm are: name, lat, lon, timestamp
	 * 
	 * @return map of all tags
	 */
	public Map<String, String> getTags() {
		return tags;
	}

	/**
	 * default constructor
	 */
	DataMapObject() {
		super();
		tags = new HashMap<String, String>();
		_id = DataStorage.getInstance().getID();
	}
	
	public int compareTo(Integer arg0) {
		if(_id < arg0.intValue())
			return -1;
		if(_id > arg0.intValue())
			return 1;
		return 0;
	}
	
	public List<Node> serialiseTagsToXmlNode(Document doc){
		//TODO: do something + javadoc!!
		return new LinkedList<Node>();
	}
}
