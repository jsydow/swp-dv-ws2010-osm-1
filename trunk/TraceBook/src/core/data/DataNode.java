package core.data;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.OverlayItem;
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
public class DataNode extends DataMapObject {

    /**
     * This constructor initializes the Latitude and Longitude with data from a
     * Location object, as well as the DataPointsList that contains this node.
     * 
     * @param loc
     *            The {@link Location} of the new node. Initializes latitude and
     *            longitude.
     * @param way
     *            The {@link DataPointsList} this node is belongs to
     */
    public DataNode(Location loc, DataPointsList way) {
        super();
        setLocation(loc);
        setDataPointsList(way);
    }

    /**
     * This constructor initializes the Latitude and Longitude with data from a
     * Location object.
     * 
     * @param loc
     *            The Location of the new node. Initializes latitude and
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
     * The {@link Location} object associated with this node.
     */
    private Location loc;

    /**
     * The {@link DataPointsList} object associated with this node. Null if this
     * node is not part of a DataPointsList.
     */
    private DataPointsList way;

    /**
     * The overlay Item used by the gui, associated with a certain poi.
     */
    private OverlayItem overlayItem;

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
     * Associates this DataNode with a {@link DataPointsList}, meaning this
     * point is part of the way.
     * 
     * @param way
     *            the way that contains this point
     */
    public void setDataPointsList(DataPointsList way) {
        this.way = way;
    }

    /**
     * If this node is part of a {@link DataPointsList}, this function will
     * return a reference to this object. Otherwise the return value is null.
     * 
     * @return the DataPointsList this point is in
     */
    public DataPointsList getDataPointsList() {
        return way;
    }

    /**
     * Getter-method.
     * 
     * @return The longitude
     */
    public double getLon() {
        if (loc == null)
            return 0;
        return loc.getLongitude();
    }

    /**
     * Getter-method.
     * 
     * @return The latitude
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

    /**
     * Change the latitude value of this node.
     * 
     * @param newLat
     *            The new latitude for this node
     */
    public void setLat(double newLat) {
        loc.setLatitude(newLat);
    }

    /**
     * Change the longitude value of this node.
     * 
     * @param newlon
     *            The new longitude for this node
     */
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
            serializer.attribute(null, "id", Integer.toString(this.getId()));
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
     * Returns a String with id and longitude and latitude of this node.
     * 
     * @return String "id=<id> (<longitude>, <latitude>)"
     */
    @Override
    public String toString() {
        return "id=" + getId() + " (" + getLon() + ", " + getLat() + ")";
    }

    /**
     * Converts the DataNode to a GeoPoint.
     * 
     * @return a GeoPoint with the coordinates of the DataNode
     */
    public GeoPoint toGeoPoint() {
        return new GeoPoint(getLat(), getLon());
    }

    /**
     * Getter-method.
     * 
     * @return a reference to the OverlayItem that is drawn and handled by
     *         MapsForge's overlay
     */
    public OverlayItem getOverlayItem() {
        return overlayItem;
    }

    /**
     * Set the OverlayItem, used by MapsForgeActivity.
     * 
     * @param overlayItem
     */
    public void setOverlayItem(OverlayItem overlayItem) {
        this.overlayItem = overlayItem;
    }

    /**
     * nodenode is a XML-node labeled "node". This method restores a DataNode
     * from such a XML-Node.
     * 
     * @param nodenode
     *            The XML-node
     * @return The new DataNode-object
     */
    public static DataNode deserialise(Node nodenode) {
        // the returned DataNode, must be initialised
        DataNode ret = new DataNode();

        // get all attributes
        NamedNodeMap nodeattributes = nodenode.getAttributes();
        // get Latitude
        ret.setLat(Double.parseDouble(nodeattributes.getNamedItem("lat")
                .getNodeValue()));
        // get Longitude
        ret.setLon(Double.parseDouble(nodeattributes.getNamedItem("lon")
                .getNodeValue()));
        // get time stamp
        ret.setDatetime(nodeattributes.getNamedItem("timestamp").getNodeValue());
        // get id
        ret.setId(Integer.parseInt(nodeattributes.getNamedItem("id")
                .getNodeValue()));

        // tags and media
        ret.deserialiseMedia(nodenode);
        ret.deserialiseTags(nodenode);

        return ret;
    }
}
