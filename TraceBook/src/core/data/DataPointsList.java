package core.data;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.mapsforge.android.maps.GeoPoint;
import org.xmlpull.v1.XmlSerializer;

import android.location.Location;
import android.util.Log;

/**
 * WayPointList objects are any objects that consist of a series of nodes like
 * Areas and Ways.
 * 
 * @author js
 * 
 */
public class DataPointsList extends DataMapObject implements
		SerialisableContent {
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
	 * default constructor
	 */
	public DataPointsList() {
		super();
		nodes = new LinkedList<DataNode>();
	}

	/**
	 * Constructor which initialises the Object as an Area.
	 * 
	 * @param Whether
	 *            object is an Area.
	 */
	public DataPointsList(boolean isArea) {
		this();
		this.isArea = isArea;
	}

	/**
	 * Getter-method
	 * 
	 * @return true if object resembles an Area.
	 */
	public boolean isArea() {
		return isArea;
	}

	/**
	 * Setter-method
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
	 * @return The list of all nodes stored in this object.
	 */
	public List<DataNode> getNodes() {
		return nodes;
	}

	public GeoPoint[] toGeoPoitArray() {
		GeoPoint[] tmp = new GeoPoint[nodes.size()];

		int i = 0;
		for (DataNode n : nodes)
			tmp[i++] = new GeoPoint(n.getLat(), n.getLon());

		return tmp;
	}

	/**
	 * Searches for a Node in this Track by the specified id.
	 * 
	 * @param id
	 *            The id of the Node that is being searched for.
	 * @return The DataNode where get_id() == id, or null if not found.
	 */
	public DataNode getNodeByID(int id) {
		for (DataNode dn : nodes)
			if (dn.get_id() == id)
				return dn;
		return null;
	}

	/**
	 * Add a new Node at the end of the list. Call this method if you want to
	 * extend the Way or Area.
	 * 
	 * @return The newly created DataNode.
	 */
	public DataNode newNode() {
		DataNode dn = new DataNode();
		nodes.add(dn);
		return dn;
	}

	/**
	 * Add a new Node at the end of the list. Call this method if you want to
	 * extend the Way or Area. Additionally the Location-constructor of the
	 * DataNode is called.
	 * 
	 * @return the newly created DataNode.
	 */
	public DataNode newNode(Location loc) {
		DataNode dn = new DataNode(loc);
		nodes.add(dn);
		return dn;
	}

	/**
	 * This method deletes a Node on the working memory and devices memory
	 * completely.
	 * 
	 * @param id
	 *            The id of the node to be deleted. If this node does not exist
	 *            nothing is done.
	 */
	public void deleteNode(int id) {
		ListIterator<DataNode> lit = nodes.listIterator();
		DataNode dn;
		while (lit.hasNext()) {
			dn = lit.next();
			if (dn.get_id() == id) {
				dn.delete();
				lit.remove();
				break;
			}
		}
	}

	/**
	 * Deserialises a DataPointsList from the devices memory.
	 * 
	 * @param id
	 *            the id of the PointsList to load from the devices memory.
	 * @return The loaded DataPointsList object. If there is not such an object
	 *         null is returned.
	 */
	public static DataPointsList deserialise(int id) {
		/* TODO STUB */
		return null;
	}

	public void serialise() {
		// TODO Auto-generated method stub

	}

	/**
	 * Serialises all nodes sequentially.
	 * 
	 * @param serializer
	 *            An XmlSerializer that is initialised.
	 */
	public void serialiseNodes(XmlSerializer serializer,
			boolean shouldSerialiseMedia) {
		for (DataNode dn : nodes) {
			dn.serialise(serializer, shouldSerialiseMedia);
		}
		return;
	}

	/**
	 * Serialises a way as way-tag. The nodes are referenced like in OSM using a
	 * nd-tag with an ref-attribute.
	 * 
	 * @param serializer
	 *            An XmlSerializer that is initialised.
	 * @param shouldSerialiseMedia
	 *            Should media also be serialised? Adding media means that the
	 *            resulting XML-file is not valid to OSM.
	 */
	public void serialiseWay(XmlSerializer serializer,
			boolean shouldSerialiseMedia) {
		try {
			serializer.startTag(null, "way");
			serializer.attribute(null, "version", "1");
			serializer.attribute(null, "timestamp", getDatetime());
			serializer.attribute(null, "id", Integer.toString(get_id()));

			for (DataNode dn : nodes) {
				serializer.startTag(null, "nd");

				serializer
						.attribute(null, "ref", Integer.toString(dn.get_id()));

				serializer.endTag(null, "nd");
			}
			if (this.isArea && nodes.size() > 0) {
				DataNode lastNode = nodes.getFirst();
				serializer.startTag(null, "nd");
				serializer.attribute(null, "ref",
						Integer.toString(lastNode.get_id()));
				serializer.endTag(null, "nd");

				serializer.startTag(null, "tag");
				serializer.attribute(null, "k", "area");
				serializer.attribute(null, "v", "yes");
				serializer.endTag(null, "tag");
			}

			serialiseTags(serializer);
			if (shouldSerialiseMedia) {
				serialiseMedia(serializer);
			}

			serializer.endTag(null, "way");

		} catch (IllegalArgumentException e) {
			Log.e("WaySerialisation", "Should not happen");
		} catch (IllegalStateException e) {
			Log.e("WaySerialisation", "Illegal state");
		} catch (IOException e) {
			Log.e("WaySerialisation", "Could not serialise way");
		}
	}

	public void delete() {
		// TODO Auto-generated method stub

	}
}
