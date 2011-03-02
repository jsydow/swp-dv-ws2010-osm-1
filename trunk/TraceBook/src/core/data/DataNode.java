package core.data;

import android.location.Location;

/**
 * A node. A node can be a POI or an element of a list of waypoints that belong
 * to a Way or Area.
 * 
 * @author js
 * 
 */
public class DataNode extends DataMapObject implements SerialisableContent {

	/**
	 * This constructor initialises the Latitude and Longitude with data from a
	 * Location object
	 */
	DataNode(Location loc) {
		super();
		setLocation(loc);
	}

	/**
	 * default constructor. Longitude and Latitude stay unchanged
	 */
	DataNode() {
		super();
	}

	/**
	 * Longitude. OSM specifies 7 digits precision. Must be in interval [-180;
	 * 180] Note: This could also be stored as a tag lon=... . This is a matter
	 * of taste that the gps coordinates are stored as double. It can still be
	 * changed if a string representation is more useful.
	 */
	private double lon;

	/**
	 * Latitude. OSM specifies 7 digits precision. Must be in interval [-90; 90]
	 * Note: This could also be stored as a tag lat=... . This is a matter of
	 * taste that the gps coordinates are stored as double. It can still be
	 * changed if a string representation is more useful.
	 */
	private double lat;

	/**
	 * Set the latitude and longitude to the position given by the Location
	 * object which is received from the gps module.
	 * 
	 * @param loc
	 *            the Location object that the gps module delivers
	 */
	public void setLocation(Location loc) {
		this.lon = loc.getLongitude();
		this.lat = loc.getLatitude();
	}

	/**
	 * Getter-method
	 * 
	 * @return the longitude
	 */
	public double getLon() {
		return lon;
	}

	/**
	 * Setter-method
	 * 
	 * @param lon
	 *            the longitude to set
	 */
	public void setLon(double lon) {
		this.lon = lon;
	}

	/**
	 * Getter-method
	 * 
	 * @return the latitude
	 */
	public double getLat() {
		return lat;
	}

	/**
	 * Setter-method
	 * 
	 * @param lat
	 *            the latitude to set
	 */
	public void setLat(double lat) {
		this.lat = lat;
	}

	/**
	 * This method loads a Node from the devices memory. It uses the appropriate
	 * ContentProvider. Note: Currently a stub. Note: The parameter id may
	 * change if another name is better suited for retrieving the Node
	 * correctly.
	 * 
	 * @param id
	 *            The id of the Node. It is not clear yet if what id or name is
	 *            needed to load the Node correctly.
	 * @return The deserialised DataNode object.
	 */
	static DataNode deserialise(int id) {
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
