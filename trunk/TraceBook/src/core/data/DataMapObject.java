package core.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Basic class for any object that is stored in OSM. All
 * objects have an id and some tags.
 * @author js
 *
 */
public abstract class DataMapObject extends DataMediaHolder{
	/**
	 * An id for this object. It is not an id for osm which is set to -1
	 * for all new objects but it is a program internal id.
	 */
	protected int _id;
	
	/**
	 * tags stores all meta information of this object. 
	 * These may be the name, timestamp, latitude, longitude and
	 * osm-tags. tags is not equivalent to osm-tags as these tags also
	 * store the timestamp and gps coordinates etc.
	 */
	protected Map<String,String> tags;
	
	public int get_id() {
		return _id;
	}

	public Map<String, String> getTags() {
		return tags;
	}

	/**
	 * 
	 * @param id the internal id of this object
	 */
	DataMapObject()
	{
		super();
		tags = new HashMap<String,String>();
		_id = DataStorage.getInstance().getID();
	}

	public int getID() {
		return _id;
	}
}
