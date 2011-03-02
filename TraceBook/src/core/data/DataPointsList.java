package core.data;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import android.location.Location;

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
	protected List<DataNode> nodes;

	/**
	 * Is this Object an Area?
	 */
	protected boolean isArea;

	/**
	 * default constructor
	 */
	DataPointsList() {
		super();
		nodes = new LinkedList<DataNode>();
	}

	/**
	 * Constructor which initialises the Object as an Area.
	 * 
	 * @param isArea
	 */
	DataPointsList(boolean isArea) {
		this();
		this.isArea = isArea;
	}

	/**
	 * Getter-method
	 * 
	 * @return the isArea
	 */
	public boolean isArea() {
		return isArea;
	}

	/**
	 * Setter-method
	 * 
	 * @param isArea
	 *            is Object an Area
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

	/**
	 * Add a new Node at the end of the list. Call this method if you want to
	 * extend the Way or Area.
	 * 
	 * @return the newly created DataNode.
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
	 * This method deletes a Node on the working and devices memory completely.
	 * 
	 * @param id
	 *            the id of the node to be deleted.
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
	 * @return the loaded DataPointsList object
	 */
	static DataPointsList deserialise(int id) {
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
