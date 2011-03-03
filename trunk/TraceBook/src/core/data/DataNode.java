package core.data;

import java.text.DecimalFormat;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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
	 * This constructor initializes the Latitude and Longitude with data from a
	 * Location object.
	 * 
	 * @param loc
	 *            The Location of the new node. Initializes latitude and
	 *            longitude. Must not be null!
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
	 * The location object associated with this node
	 */
	private Location loc;

	/**
	 * Set the latitude and longitude to the position given by the Location
	 * object which is received from the gps module.
	 * 
	 * @param loc
	 *            The Location object that the gps module delivers.
	 */
	public void setLocation(Location loc) {
		this.loc = loc;
	}
	
	/**
	 * Returns the location Object associated with this node.
	 * 
	 * @return location
	 * 				The Location object that the gps module delivered
	 */
	public Location getLocation() {
		return loc;
	}

	/**
	 * Getter-method
	 * 
	 * @return the longitude
	 */
	public double getLon() {
		if(loc == null)
			return 0;
		return loc.getLongitude();
	}

	/**
	 * Getter-method
	 * 
	 * @return the latitude
	 */
	public double getLat() {
		if(loc == null)
			return 0;
		return loc.getLatitude();
	}

	/**
	 * A Point may have been added uninitialized, in this case
	 * it does not contain any valid positional data - this may
	 * be added later once a GPS fix is obtained.
	 * 
	 * @return true if the Node contains data of a valid GPS fix
	 */
	public boolean isValid() {
		return loc != null;
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
	 * @return The deserialized DataNode object or null if there is not such a
	 *         node.
	 */
	static DataNode deserialise(int id) {
		/* TODO STUB */
		return null;
	}

	public void serialise() {
		// TODO Auto-generated method stub

	}
	
	public Node serialiseToXmlNode(Document doc) {
		Element ret = doc.createElement("node");
		DecimalFormat df = new DecimalFormat("0.0000000");
		
		ret.setAttribute("lat",df.format(this.getLat()));
		ret.setAttribute("lon",df.format(this.getLon()));
		ret.setAttribute("id", Integer.toString(this.get_id()));
		// TODO
		
		return ret;
	}

	public void delete() {
		// TODO Auto-generated method stub

	}
}
