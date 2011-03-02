package core.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import android.location.Location;

/**
 * A track. Consists of Ways, Areas, POIs and additional Media.
 * 
 * @author js
 * 
 */
public class DataTrack extends DataMediaHolder implements SerialisableContent {

	/**
	 * All Ways and Areas
	 */
	private List<DataPointsList> ways;
	/**
	 * All POI's
	 */
	private List<DataNode> nodes;

	/**
	 * The currently edited Way.
	 */
	private DataPointsList currentWay;

	/**
	 * display name of the Track. Serves as id and should therefore be unique.
	 * Is initialised with the DateTime of the first creation of this object.
	 */
	private String name;
	/**
	 * A Comment of this track
	 */
	private String comment;
	/**
	 * The Creation time.
	 */
	private String datetime;

	DataTrack(String datetime) {
		ways = new LinkedList<DataPointsList>();
		nodes = new LinkedList<DataNode>();

		if (datetime != null) {
			this.datetime = datetime;
		} else {
			// Maybe this doesn't work.
			SimpleDateFormat sdf = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			this.datetime = sdf.format(new Date());

		}
		this.name = this.datetime;
	}

	DataTrack(String datetime, String name) {
		this(datetime);
		this.name = name;
	}

	DataTrack(String datetime, String name, String comment) {
		this(datetime, name);
		this.comment = comment;
	}

	/**
	 * Getter-method
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter-method
	 * 
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Getter-method
	 * 
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Setter-method
	 * 
	 * @param comment
	 *            the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * Getter-method that returns a list of all nodes. The returned List is the
	 * one stored in this object. Changing the returned List will therefore
	 * change this list
	 * 
	 * @return all POI's of this Track
	 */
	public List<DataNode> getNodes() {
		return nodes;
	}

	/**
	 * Getter-method that returns a list of all Ways. The returned List is the
	 * one stored in this object. Changing the returned List will therefore
	 * change this list
	 * 
	 * @return all ways of this Track
	 */
	public List<DataPointsList> getWays() {
		return ways;
	}

	/**
	 * Create a new Node (i.e. POI) and add it to the Track
	 * 
	 * @return the newly created POI
	 */
	public DataNode newNode() {
		DataNode dn = new DataNode();
		nodes.add(dn);
		return dn;
	}

	/**
	 * Create a new Node (i.e. POI) and add it to the Track
	 * 
	 * @return the newly created POI
	 */
	public DataNode newNode(Location loc) {
		DataNode dn = new DataNode(loc);
		nodes.add(dn);
		return dn;
	}

	/**
	 * This method deletes a Node (POI) of this Track from the devices memory
	 * and the working memory.
	 * 
	 * @param id
	 *            the id of the POI to delete
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
	 * Create a new Way/Area in this Track
	 * 
	 * @return the newly created Way
	 */
	public DataPointsList newWay() {
		DataPointsList dpl = new DataPointsList();
		ways.add(dpl);
		return dpl;
	}

	/**
	 * This method deletes a Way of this Track from the devices memory and the
	 * working memory.
	 * 
	 * @param id
	 *            the id of the Way to delete
	 */
	public void deleteWay(int id) {
		ListIterator<DataPointsList> lit = ways.listIterator();
		DataPointsList dpl;
		while (lit.hasNext()) {
			dpl = lit.next();
			if (dpl.get_id() == id) {
				dpl.delete();
				lit.remove();
				break;
			}
		}
	}

	/**
	 * Getter-method. The creation time string.
	 * 
	 * @return the datetime
	 */
	public String getDatetime() {
		return datetime;
	}

	public void serialise() {
		// TODO Auto-generated method stub

	}

	public void delete() {
		// TODO Auto-generated method stub

	}

	/**
	 * Getter-method. The currently edited Way
	 * 
	 * @return the currentWay
	 */
	public DataPointsList getCurrentWay() {
		return currentWay;
	}

	/**
	 * Sets a Way as currently edited Way. Setter-method
	 * 
	 * @param currentWay
	 *            the currentWay to set
	 */
	public DataPointsList setCurrentWay(DataPointsList currentWay) {
		this.currentWay = currentWay;
		return currentWay;
	}

	/**
	 * This method loads a Track from the devices memory. It uses the
	 * appropriate ContentProvider. Note: Currently a stub. Note: The parameter
	 * name may change if another name is better suited for retrieving the Track
	 * correctly.
	 * 
	 * @param name
	 *            The name of the Track as stored on the memory.
	 * @return The deserialised DataTrack object or null if such a Track does
	 *         not exist
	 */
	static DataTrack deserialise(String name) {
		/* TODO STUB */
		return null;
	}

	/**
	 * This method returns a list of the name of all Tracks that are stored on
	 * the devices memory. These names can be used to deserialise a Track.
	 * 
	 * @return A list of the names of all available Tracks
	 */
	public static List<String> allTracks() {
		/* TODO STUB */
		return null;
	}

}
