package core.data;

/**
 * An area. It is a closed way with same starting and end-node. In OSM
 * such an Area is a Way with landuse=... and/or area=yes 
 * @author js
 *
 */
public class DataArea extends DataWayPointList {

	/**
	 * 
	 * @param id id the internal id of this object
	 */
	public DataArea(int id) {
		super(id);
	}

}
