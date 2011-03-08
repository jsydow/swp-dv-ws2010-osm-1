package core.data;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.mapsforge.android.maps.GeoPoint;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xmlpull.v1.XmlSerializer;

import android.location.Location;
import android.util.Log;

/**
 * A node. A node can be a POI or an element of a list of waypoints that belong
 * to a Way or Area.
 * 
 * @author js
 * 
 */
public class DataNode extends DataMapObject{

	/**
	 * This constructor initialises the Latitude and Longitude with data from a
	 * Location object.
	 * 
	 * @param loc
	 *            The Location of the new node. Initialises latitude and
	 *            longitude.
	 */
	public DataNode(Location loc) {
		super();
		setLocation(loc);
	}

	/**
	 * default constructor. Longitude and Latitude stay unchanged
	 */
	public DataNode() {
		super();
		setLocation(new Location("TraceBook"));
	}

	/**
	 * The location object associated with this node
	 */
	private Location loc;

	/**
	 * Set the latitude and longitude to the position given by the Location
	 * object which is received from the GPS module.
	 * 
	 * @param loc
	 *            The Location object that the GPS module delivers.
	 */
	public void setLocation(Location loc) {
		this.loc = loc;
	}

	/**
	 * Returns the location Object associated with this node.
	 * 
	 * @return location The Location object that the gps module delivered
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
		if (loc == null)
			return 0;
		return loc.getLongitude();
	}

	/**
	 * Getter-method
	 * 
	 * @return the latitude
	 */
	public double getLat() {
		if (loc == null)
			return 0;
		return loc.getLatitude();
	}

	/**
	 * A Point may have been added uninitialised, in this case it does not
	 * contain any valid positional data - this may be added later once a GPS
	 * fix is obtained.
	 * 
	 * @return true if the Node contains data of a valid GPS fix
	 */
	public boolean isValid() {
		return loc != null;
	}
	
	public void setLat(double newLat) {
		loc.setLatitude(newLat);
	}
	
	public void setLon(double newlon) {
		loc.setLongitude(newlon);
	}

	/**
	 * Serialises a node using a XmlSerializer. It generates a <node>-tag.
	 * 
	 * @param serializer
	 *            An XmlSerializer that is initialised.
	 * @param shouldSerialiseMedia
	 *            Should media also be serialised? Adding media means that the
	 *            resulting XML-file is not valid to OSM.
	 */
	public void serialise(XmlSerializer serializer, boolean shouldSerialiseMedia) {
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		DecimalFormat df = new DecimalFormat("0.0000000", dfs);

		try {
			serializer.startTag(null, "node");
			serializer.attribute(null, "lat", df.format(this.getLat()));
			serializer.attribute(null, "lon", df.format(this.getLon()));
			serializer.attribute(null, "id", Integer.toString(this.get_id()));
			serializer.attribute(null, "timestamp", this.getDatetime());
			serializer.attribute(null, "version", "1");

			serialiseTags(serializer);
			if (shouldSerialiseMedia) {
				serialiseMedia(serializer);
			}

			serializer.endTag(null, "node");

		} catch (IllegalArgumentException e) {
			Log.e("NodeSerialisation", "Should not happen");
		} catch (IllegalStateException e) {
			Log.e("NodeSerialisation", "Illegal state");
		} catch (IOException e) {
			Log.e("NodeSerialisation", "Could not serialise node");
		}
	}

	/**
	 * Converts a DataNode to a GeoPoint
	 * @param node DataNode
	 * @return a GeoPoint with the coordinates of the DataNode 
	 */
	public static GeoPoint toGeoPoint(DataNode n) {
		return new GeoPoint(n.getLat(), n.getLon());
	}

	/**
	 * 
	 * @param item ...
	 * @return
	 */
	public static DataNode deserialise(Node nodenode) {
		DataNode ret = new DataNode();
		
		NamedNodeMap nodeattributes = nodenode.getAttributes();
		ret.setLat(Double.parseDouble(nodeattributes.getNamedItem("lat").getNodeValue()));
		ret.setLon(Double.parseDouble(nodeattributes.getNamedItem("lon").getNodeValue()));
		ret.setDatetime( nodeattributes.getNamedItem("timestamp").getNodeValue() );
		ret.set_id(Integer.parseInt(nodeattributes.getNamedItem("id").getNodeValue()));
		
		// tags and media
		ret.deserialiseMedia(nodenode);
		ret.deserialiseTags(nodenode);
		
		return ret;
	}
}
