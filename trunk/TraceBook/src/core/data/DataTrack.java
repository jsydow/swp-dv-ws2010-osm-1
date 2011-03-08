package core.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import android.location.Location;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

/**
 * A Track. Consists of Ways, Areas, POIs and additional Media. A Track is a
 * "tracking session". Normally the user will edit just one Track for one use of
 * the program. All data he collects with this one usage in then grouped in a
 * Track. A Track is not like a simple Way from place A to B but can contain it.
 * 
 * @author js
 * 
 */
public class DataTrack extends DataMediaHolder {

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
	 * Constructor which initialises the Track, each Track must have a Date
	 * time.
	 * 
	 * @param datetime
	 *            The Date time in string representation. Can be null! Null is
	 *            recommended.
	 */
	public DataTrack() {
		super();
		ways = new LinkedList<DataPointsList>();
		nodes = new LinkedList<DataNode>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		this.name = sdf.format(new Date());
		createNewTrackFolder();
	}

	/**
	 * Initialising constructor.
	 * 
	 * @param datetime
	 *            See constructor DataTrack(Datetime).
	 * @param name
	 *            The display and foldername of the Track.
	 */
	public DataTrack(String name) {
		this();
		this.name = name;
	}

	/**
	 * Initialising constructor.
	 * Note: comment is not implemented yet.
	 * 
	 * @param datetime
	 *            See constructor DataTrack(Datetime).
	 * @param name
	 *            See constructor DataTrack(Datetime,Name).
	 * @param comment
	 *            Comment that may be displayed for this Track.
	 */
	public DataTrack(String name, String comment) {
		this(name);
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
				lit.remove();
				break;
			}
		}
	}

	/**
	 * Serialises a track to a XML-file stored on the SD-card in folder
	 * TraceBook/<track name>.
	 * 
	 * @param shouldSerialiseMedia
	 *            Should media also be serialised? Adding media means that the
	 *            resulting XML-file is not valid to OSM.
	 */
	public void serialise(boolean shouldSerialiseMedia) {

		Log.d("DataTrack", "Ways: " + ways.size() + ", POIs: " + nodes.size());

		File xmlfile = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "TraceBook" + File.separator + name
				+ File.separator + "/track.tbt");
		try {
			if (xmlfile.exists()) {
				if (!xmlfile.delete()) {
					Log.e("TrackSerialisation", "Deleting old file failed");
					return;
				}
			}
			if (!xmlfile.createNewFile()) {
				Log.e("TrackSerialisation", "Creating new XML-file failed");
				return;
			}

		} catch (IOException e) {
			Log.e("TrackSerialisation", "Could not create new file");
		}
		FileOutputStream fileos = null;
		try {
			fileos = new FileOutputStream(xmlfile);
		} catch (FileNotFoundException e) {
			Log.e("TrackSerialisation", "Could not open new file");
		}

		XmlSerializer serializer = Xml.newSerializer();
		try {
			serializer.setOutput(fileos, "UTF-8");
			serializer.startDocument(null, Boolean.valueOf(true));
			serializer.startTag(null, "osm");

			serializer.attribute(null, "version", "0.6");
			serializer.attribute(null, "generator", "TraceBook");

			for (DataNode dn : nodes) {
				dn.serialise(serializer, shouldSerialiseMedia);
			}
			for (DataPointsList dpl : ways) {
				dpl.serialiseNodes(serializer, shouldSerialiseMedia);
			}
			for (DataPointsList dpl : ways) {
				dpl.serialiseWay(serializer, shouldSerialiseMedia);
			}

			serializer.endTag(null, "osm");
			serializer.flush();
		} catch (Exception e) {
			Log.e("TrackSerialisation", "Could not serialise track");
		}

	}

	/**
	 * Deletes a Track with all its contents from the devices memory.
	 */
	public void delete() {
		File track = new File(getTrackDirPath());
		File[] files = track.listFiles();
		for(File f : files) {
			if(f.isFile()) {
				if(!f.delete()) {
					Log.e("DeleteTrackFile", "Could not delete file "+f.getName() +" in track " + getName());
				}
			}
		}
		if(!track.delete()) {
			Log.e("DeleteTrack", "Could not delete track " + getName());
			
		}
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
		List<DataNode> allnodes = new LinkedList<DataNode>();
		File track = new File(getTrackDirPath(name));
		DataTrack ret = new DataTrack(track.getName());
		
		if(track.isFile()) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
			try {
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document dom = builder.parse(track);
				Element osmelement = dom.getDocumentElement(); // root-element
				
				// all nodes
				NodeList nodeelements = osmelement.getElementsByTagName("node");
				for(int i=0; i<nodeelements.getLength();++i) {
					allnodes.add(DataNode.deserialise(nodeelements.item(i)));
				}
				
				// all ways
				NodeList wayelements = osmelement.getElementsByTagName("way");
				for(int i=0; i<wayelements.getLength();++i) {
					DataPointsList dpl = DataPointsList.deserialise(nodeelements.item(i),allnodes);
					ret.addWay(dpl);
				}
				
				// all media
				NodeList medianodes = osmelement.getElementsByTagName("way");
				for(int i=0; i<medianodes.getLength();++i) {
					NamedNodeMap attributes = medianodes.item(i).getAttributes();
					Node path = attributes.getNamedItem("value");
					// misuse of getTrackDirPath
					ret.addMedia(DataMedia.deserialise(DataTrack.getTrackDirPath(path.getNodeValue())));
				}
				
				// nodes -> POIs
				ret.getNodes().addAll(allnodes);

			} catch (IOException e) {
				Log.e("TrackDeserialisation","Error while reading XML file.");
				return null;
			} catch (Exception e) {
				Log.e("TrackDeserialisation", "XML parsing error.");
				return null;
			}
		} else {
			Log.e("TrackDeserialisation", "Track was not found.");
			return null;
		}
		
		return ret;
	}

	public void serialise() {
		serialise(true);
	}

	public void createNewTrackFolder() {
		File dir = new File(DataStorage.getTraceBookDirPath() + File.separator
				+ name);
		if (!dir.isDirectory()) {
			if (!dir.mkdir()) {
				Log.e("DataStorage", "Could not create new track folder "
						+ name);
			}
		}
	}

	/**
	 * Returns the complete absolute path to this Track directory.
	 * @return path to the track directory
	 */
	public String getTrackDirPath() {
		return DataStorage.getTraceBookDirPath() + File.separator + name;
	}
	
	/**
	 * Completes a track directory name to a complete path.
	 * @param dir Name of the track directory 
	 * @return The complete path to the track directory.
	 */
	public static String getTrackDirPath(String dir) {
		return DataStorage.getTraceBookDirPath() + File.separator + dir;
	}

	/**
	 * Get a DataPointsList with a given id.
	 * @param id The id of the DataPointsList
	 * @return The DataPointsList or null if there is none with such an id.
	 */
	public DataPointsList getPointsListById(int id) {
		for(DataPointsList dpl : ways) {
			if(dpl.get_id() == id) {
				return dpl;
			}
		}
		return null;
	}
	
	/**
	 * Get a DataNode with a given id.
	 * @param id The id of the DataNode
	 * @return The DataNode or null if there is none with such an id.
	 */
	public DataNode getNodeById(int id) {
		for(DataNode dn : nodes) {
			if(dn.get_id() == id) {
				return dn;
			}
		}
		return null;
	}
	
	private void addWay(DataPointsList dpl) {
		ways.add(dpl);
	}
	
	/**
	 * Search the whole track for an DataMapObject by id
	 * @param id
	 * @return
	 */
	public DataMapObject getDataMapObjectById( int id ){
		
		DataMapObject res = getNodeById( id );
		if( res != null ) {
			return res;
		}
		
		res = getPointsListById( id );
		return res;		
	}
}
