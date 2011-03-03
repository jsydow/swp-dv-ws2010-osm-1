package core.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import android.location.Location;
import android.os.Environment;

/**
 * A Track. Consists of Ways, Areas, POIs and additional Media. A Track is a
 * "tracking session". Normally the user will edit just one Track for one use of
 * the program. All data he collects with this one usage in then grouped in a
 * Track. A Track is not like a simple Way from place A to B but can contain it.
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
	 * Display name of the Track. Serves as id and should therefore be unique.
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

	/**
	 * Constructor which initialises the Track, each Track must have a Datetime.
	 * 
	 * @param datetime
	 *            The Datetime in string representation. Can be null! Null is
	 *            recommended.
	 */
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

	/**
	 * Initialising constructor.
	 * 
	 * @param datetime
	 *            See constructor DataTrack(Datetime).
	 * @param name
	 *            The display and foldername of the Track.
	 */
	DataTrack(String datetime, String name) {
		this(datetime);
		this.name = name;
	}

	/**
	 * Initialising constructor.
	 * 
	 * @param datetime
	 *            See constructor DataTrack(Datetime).
	 * @param name
	 *            See constructor DataTrack(Datetime,Name).
	 * @param comment
	 *            Comment that may be displayed for this Track.
	 */
	DataTrack(String datetime, String name, String comment) {
		this(datetime, name);
		this.comment = comment;
	}

	/**
	 * Getter-method
	 * 
	 * @return The name of the Track.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter-method
	 * 
	 * @param name
	 *            The new name of the DataTrack
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Getter-method
	 * 
	 * @return The comment of the Track.
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Setter-method
	 * 
	 * @param comment
	 *            The new comment of the Track.
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * Getter-method that returns a list of all nodes. The returned List is the
	 * one stored in this object. Changing the returned List will therefore
	 * change this list
	 * 
	 * @return All POI's of this Track.
	 */
	public List<DataNode> getNodes() {
		return nodes;
	}

	/**
	 * Getter-method that returns a list of all Ways. The returned List is the
	 * one stored in this object. Changing the returned List will therefore
	 * change this list
	 * 
	 * @return All ways of this Track.
	 */
	public List<DataPointsList> getWays() {
		return ways;
	}

	/**
	 * Create a new Node (i.e. POI) and add it to the Track.
	 * 
	 * @return The newly created POI.
	 */
	public DataNode newNode() {
		DataNode dn = new DataNode();
		nodes.add(dn);
		return dn;
	}

	/**
	 * Create a new Node (i.e. POI) and add it to the Track
	 * 
	 * @param loc
	 *            The Location object to be used for constructing the new Node.
	 * @return The newly created POI.
	 */
	public DataNode newNode(Location loc) {
		DataNode dn = new DataNode(loc);
		nodes.add(dn);
		return dn;
	}

	/**
	 * This method deletes a Node (POI) of this Track from the devices memory
	 * and the working memory. If this node does not exist nothing is done.
	 * 
	 * @param id
	 *            The id of the POI to delete.
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
	 * @return The newly created Way.
	 */
	public DataPointsList newWay() {
		DataPointsList dpl = new DataPointsList();
		ways.add(dpl);
		return dpl;
	}

	/**
	 * This method deletes a Way of this Track from the devices memory and the
	 * working memory. If the Way does not exist nothing is done.
	 * 
	 * @param id
	 *            The id of the Way to delete.
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
	 * @return The datetime string.
	 */
	public String getDatetime() {
		return datetime;
	}

	public void serialise() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return;
		}
		Document document = builder.newDocument();
		
		// TODO
		document.appendChild(document.createElement("test"));

		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			DOMSource        source = new DOMSource( document );
			FileOutputStream os     = new FileOutputStream( new File( Environment.getExternalStorageDirectory().getAbsolutePath()+File.pathSeparator+"fu.xml" ) );
			StreamResult     result = new StreamResult( os );
			transformer.transform( source, result );
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void delete() {
		// TODO Auto-generated method stub

	}

	/**
	 * Getter-method. The currently edited Way.
	 * 
	 * @return The current Way. Current Way can be null if not initialised.
	 */
	public DataPointsList getCurrentWay() {
		return currentWay;
	}

	/**
	 * Sets a Way as currently edited Way. Setter-method.
	 * 
	 * @param currentWay
	 *            The new currently edited Way.
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
