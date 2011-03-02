package core.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Basic class for any object that is stored in OSM. All
 * objects have an id and some tags.
 * @author js
 *
 */
abstract class DataMapObject {
	/**
	 * An id for this object. It is not an id for osm which is set to -1
	 * for all new objects but it is a program internal id.
	 */
	int _id;
	
	/**
	 * tags stores all meta information of this object. 
	 * These may be the name, timestamp, latitude, longitude and
	 * osm-tags. tags is not equivalent to osm-tags as these tags also
	 * store the timestamp and gps coordinates etc.
	 */
	Map<String,String> tags;
	
	/**
	 * 
	 * @param id the internal id of this object
	 */
	DataMapObject(int id)
	{
		tags = new HashMap<String,String>();
		_id = id;
	}

	public int getID() {
		return _id;
	}
}
