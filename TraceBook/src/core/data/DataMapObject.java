package core.data;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import android.util.Log;

/**
 * Basic class for any object that is stored in OSM. All objects have an id and
 * some tags. Additionally all objects can have media attached.
 * 
 * @author js
 * 
 */
public abstract class DataMapObject extends DataMediaHolder implements
        Comparable<Integer> {
    /**
     * An id for this object. It is not an id for OSM which is set to -1 for all
     * new objects but it is a program internal id. It should be unique.
     * DataStorage.getID() creates one.
     */
    protected int id;

    /**
     * tags stores all meta information of this object. These may be the name,
     * time stamp, latitude, longitude and OSM-tags. tags is not equivalent to
     * OSM-tags as these tags also store the time stamp and GPS coordinates etc.
     */
    protected Map<String, String> tags;

    /**
     * Getter-method.
     * 
     * @return the id of the object
     */
    public int getId() {
        return id;
    }

    /**
     * Method to set the id. Do not use! If some MapObjects have the same id
     * errors might occur. It is intended to use to initialise a DataMapObject
     * from an old id which is unique.
     * 
     * @param id
     *            The new id
     */
    void setId(int id) {
        this.id = id;
    }

    /**
     * Getter-method for a all tags stored as a Map of String. Tags that are no
     * tags in OSM are: name, lat, lon, timestamp. Mind that changes in the
     * returned Map change this object in the same way.
     * 
     * @return map of all tags
     */
    public Map<String, String> getTags() {
        return tags;
    }

    /**
     * Default constructor.
     */
    public DataMapObject() {
        super();
        tags = new HashMap<String, String>();
        id = DataStorage.getInstance().getID();
    }

    /**
     * Comparable-implementation.
     */
    public int compareTo(Integer arg0) {
        if (id < arg0.intValue())
            return -1;
        if (id > arg0.intValue())
            return 1;
        return 0;
    }

    /**
     * Add Tag-tags like <tag k="..." v="..." /> to the XmlSerializer. Make sure
     * the enclosing tag is opened.
     * 
     * @param serializer
     *            An XmlSerializer that is initialised.
     */
    public void serialiseTags(XmlSerializer serializer) {
        try {
            for (String tag : tags.keySet()) {

                serializer.startTag(null, "tag");
                serializer.attribute(null, "k", tag);
                serializer.attribute(null, "v", tags.get(tag));
                serializer.endTag(null, "tag");
            }
        } catch (IllegalArgumentException e) {
            Log.e("TagSerialisation", "Should not happen");
        } catch (IllegalStateException e) {
            Log.e("TagSerialisation", "Illegal state");
        } catch (IOException e) {
            Log.e("TagSerialisation", "Could not serialise tags");
        }
        return;
    }

    /**
     * a_node is a Node which has <tag>-children. This method retrieves the tags
     * out of these <tag>s
     * 
     * @param aNode
     *            An XML-node
     */
    public void deserialiseTags(Node aNode) {
        NodeList metanodes = aNode.getChildNodes();
        for (int i = 0; i < metanodes.getLength(); ++i) {
            if (metanodes.item(i).getNodeName().equals("tag")) {

                NamedNodeMap attributes = metanodes.item(i).getAttributes();
                Node key = attributes.getNamedItem("k");
                Node value = attributes.getNamedItem("v");
                getTags().put(key.getNodeValue(), value.getNodeValue());
            }
        }
    }
}
