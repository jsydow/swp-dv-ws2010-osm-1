package tracebook.core.data;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.OverlayRoute;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import android.location.Location;
import android.util.Log;

/**
 * WayPointList objects are any objects that consist of a series of nodes like
 * Areas and Ways.
 */
public class DataPointsList extends DataMapObject {
    /**
     * The list of nodes of this object. First node is first element in this
     * list.
     */
    protected LinkedList<DataNode> nodes;

    /**
     * Is this Object an Area?
     */
    protected boolean isArea;

    /**
     * Route Object for MapsForge.
     */
    private OverlayRoute overlayRoute;

    /**
     * Default constructor.
     */
    public DataPointsList() {
        super();
        nodes = new LinkedList<DataNode>();
    }

    /**
     * Constructor which initializes the Object as an Area.
     * 
     * @param isArea
     *            Whether object is an Area.
     */
    public DataPointsList(boolean isArea) {
        this();
        this.isArea = isArea;
    }

    /**
     * Getter-method.
     * 
     * @return True if object resembles an Area.
     */
    public boolean isArea() {
        return isArea;
    }

    /**
     * Setter-method.
     * 
     * @param isArea
     *            Whether object is an Area.
     */
    public void setArea(boolean isArea) {
        this.isArea = isArea;
    }

    /**
     * Getter-method that returns a list of all nodes. The returned List is the
     * one stored in this object. Changing the returned List will therefore
     * change this list
     * 
     * @return The list of all nodes stored in this object. (not null)
     */
    public List<DataNode> getNodes() {
        return nodes;
    }

    /**
     * Returns an array of GeoPoints representing the current way for being
     * displayed in a RouteOverlay. If isArea() is true, the first point will be
     * added as last point, this is a requirement of the RouteOverlay.
     * 
     * @return The array of GeoPoints. (not null)
     */
    public GeoPoint[] toGeoPointArray() {
        return toGeoPointArray(null);
    }

    /**
     * Returns an array of GeoPoints representing the current way for being
     * displayed in a RouteOverlay. If isArea() is true, the first point will be
     * added as last point, this is a requirement of the RouteOverlay.
     * 
     * @param additional
     *            additional GeoPoint to be added to the way, may be null
     * 
     * @return The array of GeoPoints. (not null)
     */
    public GeoPoint[] toGeoPointArray(GeoPoint additional) {
        GeoPoint[] tmp = new GeoPoint[nodes.size()
                + (additional != null ? 1 : 0)];
        GeoPoint first = null;

        int i = 0;
        for (DataNode n : nodes) {
            tmp[i] = n.toGeoPoint();
            if (first == null)
                first = tmp[i];
            ++i;
        }

        if (additional != null)
            tmp[i++] = additional;

        return tmp;
    }

    /**
     * Add a new Node at the end of the list. Call this method if you want to
     * extend the Way or Area.
     * 
     * @return The newly created DataNode.
     */
    public DataNode newNode() {
        DataNode dn = new DataNode();
        dn.setDataPointsList(this);
        nodes.add(dn);
        return dn;
    }

    /**
     * Searches for a Node in this Track by the specified id.
     * 
     * @param nodeId
     *            The id of the Node that is being searched for.
     * @return The DataNode where get_id() == id, or null if not found.
     */
    public DataNode getNodeById(int nodeId) {
        for (DataNode dn : nodes) {
            if (dn.getId() == nodeId) {
                return dn;
            }
        }
        return null;
    }

    /**
     * Add a new Node at the end of the list. Call this method if you want to
     * extend the Way or Area. Additionally the Location-constructor of the
     * DataNode is called.
     * 
     * @param location
     *            The Location of this node. Node is initialized with this
     *            location.
     * @return The newly created DataNode.
     */
    public DataNode newNode(Location location) {
        DataNode dn = new DataNode(location, this);
        nodes.add(dn);
        return dn;
    }

    /**
     * This method deletes a Node on the working memory and devices memory
     * completely.
     * 
     * @param nodeId
     *            The id of the node to be deleted. If this node does not exist
     *            nothing is done.
     * @return True if the node was found and deleted, false if no node with the
     *         id could be found
     */
    public boolean deleteNode(int nodeId) {
        ListIterator<DataNode> lit = nodes.listIterator();
        DataNode dn;
        while (lit.hasNext()) {
            dn = lit.next();
            if (dn.getId() == nodeId) {
                lit.remove();
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the route Object used by MapsForge to display this way.
     * 
     * @return The OverlayRoute of this way. (can be null)
     */
    public OverlayRoute getOverlayRoute() {
        return overlayRoute;
    }

    /**
     * Sets the OverlayRoute, an object used by MapsForge for visualization.
     * 
     * @param overlayRoute
     *            The new OverlayRoute.
     */
    public void setOverlayRoute(OverlayRoute overlayRoute) {
        this.overlayRoute = overlayRoute;
    }

    /**
     * Serializes all nodes sequentially.
     * 
     * @param serializer
     *            An XmlSerializer that is initialized.
     * @param shouldSerialiseMedia
     *            Should the media be also serialized? If yes then the resulting
     *            XML-file is not conform to OSM.
     */
    public void serializeNodes(XmlSerializer serializer,
            boolean shouldSerialiseMedia) {
        for (DataNode dn : nodes) {
            dn.serialize(serializer, shouldSerialiseMedia);
        }
        return;
    }

    /**
     * Serializes a way as way-tag. The nodes are referenced like in OSM using a
     * nd tag with a ref attribute.
     * 
     * @param serializer
     *            An XmlSerializer that is initialized.
     * @param shouldSerialiseMedia
     *            Should media also be serialized? Adding media means that the
     *            resulting XML-file is not valid to OSM.
     */
    public void serializeWay(XmlSerializer serializer,
            boolean shouldSerialiseMedia) {
        if (nodes.size() > 0) {
            try {
                serializer.startTag(null, "way");
                serializer.attribute(null, "version", "1");
                serializer.attribute(null, "timestamp", getDatetime());
                serializer.attribute(null, "id", Integer.toString(getId()));

                for (DataNode dn : nodes) {
                    serializer.startTag(null, "nd");

                    serializer.attribute(null, "ref", Integer.toString(dn
                            .getId()));

                    serializer.endTag(null, "nd");
                }
                if (this.isArea && nodes.size() > 0) {
                    DataNode lastNode = nodes.getFirst();
                    serializer.startTag(null, "nd");
                    serializer.attribute(null, "ref", Integer.toString(lastNode
                            .getId()));
                    serializer.endTag(null, "nd");

                    serializer.startTag(null, "tag");
                    serializer.attribute(null, "k", "area");
                    serializer.attribute(null, "v", "yes");
                    serializer.endTag(null, "tag");
                }

                serializeTags(serializer);
                if (shouldSerialiseMedia) {
                    serializeMedia(serializer);
                }

                serializer.endTag(null, "way");

            } catch (IllegalArgumentException e) {
                Log.e("WaySerialisation", "Should not happen");
            } catch (IllegalStateException e) {
                Log.e("WaySerialisation", "Illegal state");
            } catch (IOException e) {
                Log.e("WaySerialisation", "Could not serialize way");
            }
        }
    }

    /**
     * Way node is a XML-node labeled "way". This method restores a
     * DataPointsList from such a XML-Node.
     * 
     * @param waynode
     *            A XML-node
     * @param allnodes
     *            All DataNodes that were already retrieved from that XML-file
     * @return The new DataPointsList
     */
    public static DataPointsList deserialize(Node waynode,
            List<DataNode> allnodes) {
        // the returned DataPointsList
        DataPointsList ret = new DataPointsList();

        // get all attributes
        NamedNodeMap nodeattributes = waynode.getAttributes();
        // get time stamp
        ret
                .setDatetime(nodeattributes.getNamedItem("timestamp")
                        .getNodeValue());
        // get id
        ret.setId(Integer.parseInt(nodeattributes.getNamedItem("id")
                .getNodeValue()));

        // tags and media
        ret.deserializeMedia(waynode);
        ret.deserializeTags(waynode);

        // node references
        // for all <nd>-child nodes
        NodeList metanodes = waynode.getChildNodes();
        for (int i = 0; i < metanodes.getLength(); ++i) {

            // is <nd>-node?
            if (metanodes.item(i).getNodeName().equals("nd")) {

                // get id of the node referenced
                int nodeId = Integer.parseInt(metanodes.item(i).getAttributes()
                        .getNamedItem("ref").getNodeValue());
                // search for this node in allnodes
                ListIterator<DataNode> it = allnodes.listIterator();

                while (it.hasNext()) {
                    DataNode dn = it.next();
                    if (dn.getId() == nodeId) {
                        // remove from allnodes this node
                        it.remove();
                        // add
                        ret.nodes.add(dn);
                    }
                }
            }
        }

        // is this Way an Area?
        String value = ret.getTags().get("key");
        if (value != null) {
            if (value.equals("yes")) {
                ret.setArea(true);
            }
        }

        return ret;
    }
}
