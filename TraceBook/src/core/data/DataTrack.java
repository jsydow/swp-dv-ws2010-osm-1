package core.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mapsforge.android.maps.GeoPoint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import android.location.Location;
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
     * All Ways and Areas of this track.
     */
    private List<DataPointsList> ways;
    /**
     * All POI's of this track.
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
     * A Comment of this track.
     */
    private String comment;

    /**
     * Constructor which initialises the Track, each Track must have a Date
     * time.
     */
    public DataTrack() {
        super();
        ways = new LinkedList<DataPointsList>();
        nodes = new LinkedList<DataNode>();
        this.name = getFilenameCompatibleTimeStamp();
        createNewTrackFolder();
    }

    /**
     * Creates a time stamp of the current time which can be used as a filename.
     * 
     * @return The time stamp String.
     */
    public static String getFilenameCompatibleTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        return sdf.format(new Date());
    }

    /**
     * Initialising constructor.
     * 
     * @param name
     *            The display and foldername of the Track.
     */
    public DataTrack(String name) {
        this();
        this.name = name;
    }

    /**
     * Initialising constructor. Note: comment is not implemented yet.
     * 
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
     * Getter-method.
     * 
     * @return The name of the Track.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter-method. Renames a Track in the devices and working memory.
     * 
     * @param newname
     *            The new name of the DataTrack
     */
    public void setName(String newname) {
        renameTrack(newname);
        this.name = newname;
    }

    /**
     * Renames a Track on the devices memory.
     * 
     * @param newname
     *            The new name of the Track
     */
    private void renameTrack(String newname) {
        File trackdir = new File(getTrackDirPath());
        if (trackdir.isDirectory()) {
            if (!trackdir.renameTo(new File(getTrackDirPath(newname)))) {
                Log.w("RenamingTrack", "Could not rename Track.");
            }
        } else {
            Log.w("RenamingTrack", "Could not find Track " + getName());
        }
    }

    /**
     * Getter-method.
     * 
     * @return The comment of the Track.
     */
    public String getComment() {
        return comment;
    }

    /**
     * Setter-method.
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
     * Create a new Node (i.e. POI) and add it to the Track
     * 
     * @param loc
     *            The GeoPoint object to be used for constructing the new Node
     * @return The newly created POI
     */
    public DataNode newNode(GeoPoint loc) {
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
     * @return true if a node with the id was found and deleted, false if no
     *         such node did exist
     */
    public boolean deleteNode(int id) {
        ListIterator<DataNode> lit = nodes.listIterator();
        DataNode dn;
        while (lit.hasNext()) {
            dn = lit.next();
            if (dn.getId() == id) {
                lit.remove();
                return true;
            }
        }

        for (DataPointsList dpl : getWays())
            if (dpl.deleteNode(id))
                return true;

        return false;
    }

    /**
     * Create a new Way/Area in this Track.
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
            if (dpl.getId() == id) {
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

        if (!(new File(getTrackDirPath()).isDirectory())) {
            createNewTrackFolder();
        }

        File xmlfile = new File(getPathOfTrackTbTFile(name));

        FileOutputStream fileos = openFile(xmlfile);
        if (fileos == null) {
            return;
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
        } catch (IllegalArgumentException e) {
            Log.e("DataTrackSerialisation",
                    "Should not happen. Internal error.");
        } catch (IllegalStateException e) {
            Log.e("DataTrackSerialisation",
                    "Should not happen. Internal error.");
        } catch (IOException e) {
            Log.e("DataTrackSerialisation", "Error while reading file.");
        } finally {
            try {
                fileos.close();
            } catch (IOException e) {
                // do nothing
            }
        }

    }

    private static FileOutputStream openFile(File file) {
        try {
            if (file.exists()) {
                if (!file.delete()) {
                    Log.e("OpenFile", "Deleting old file " + file.getName()
                            + " failed");
                    return null;
                }
            }
            if (!file.createNewFile()) {
                Log.e("OpenFile", "Creating new file " + file.getName()
                        + " failed");
                return null;
            }

        } catch (IOException e) {
            Log.e("OpenFile", "Could not create new file " + file.getPath());
        }
        FileOutputStream fileos = null;
        try {
            fileos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            Log.e("OpenFile", "Could not open new file " + file.getPath());
        }
        return fileos;
    }

    /**
     * Deletes a Track with all its contents from the devices memory.
     */
    public void delete() {
        DataStorage.deleteDirectory(new File(getTrackDirPath()));
    }

    /**
     * Deletes a Track with all its contents from the devices memory.
     * 
     * @param trackname
     */
    public static void delete(String trackname) {
        DataStorage.deleteDirectory(new File(getTrackDirPath(trackname)));
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
     * @return Returns the parameter currentWay for further use.
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
    public static DataTrack deserialise(String name) {
        // cache all nodes
        List<DataNode> allnodes = new LinkedList<DataNode>();
        // XML-file
        File track = new File(getPathOfTrackTbTFile(name));
        // Track that should be filled/initialised
        DataTrack ret = new DataTrack(track.getParentFile().getName());

        if (track.isFile()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();

            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document dom = builder.parse(track);

                // get <osm>-element
                Element osmelement = dom.getDocumentElement(); // root-element
                if (osmelement == null)
                    throw new SAXException();

                // get all nodes
                NodeList nodeelements = osmelement.getElementsByTagName("node");
                if (nodeelements == null)
                    throw new SAXException();
                for (int i = 0; i < nodeelements.getLength(); ++i) {
                    // generate a node
                    allnodes.add(DataNode.deserialise(nodeelements.item(i)));
                }

                // all ways
                NodeList wayelements = osmelement.getElementsByTagName("way");
                if (wayelements == null)
                    throw new SAXException();
                for (int i = 0; i < wayelements.getLength(); ++i) {
                    // generate ways
                    DataPointsList dpl = DataPointsList.deserialise(
                            wayelements.item(i), allnodes);
                    // add them
                    ret.addWay(dpl);
                }

                // all media
                NodeList medianodes = osmelement.getElementsByTagName("link");
                if (medianodes == null)
                    throw new SAXException();
                for (int i = 0; i < medianodes.getLength(); ++i) {
                    // get attributes of <link>-node
                    NamedNodeMap attributes = medianodes.item(i)
                            .getAttributes();
                    // path to medium is value of href-attribute
                    Node path = attributes.getNamedItem("href");
                    // path to medium is path to track directory + media name
                    ret.addMedia(DataMedia.deserialise(ret.getTrackDirPath()
                            + File.separator + path.getNodeValue()));
                }

                // nodes -> POIs, all nodes that are still in allnodes are POIs
                // DataNode.deserialise erase those, that are part of a way
                ret.getNodes().addAll(allnodes);

            } catch (IOException e) {
                Log.e("TrackDeserialisation", "Error while reading XML file.");
                return null;
            } catch (ParserConfigurationException e) {
                Log.e("TrackDeserialisation", "XML parser doesn't work.");
                return null;
            } catch (SAXException e) {
                Log.e("TrackDeserialisation", "Error while parsing XML file.");
                return null;
            }
        } else {
            Log.e("TrackDeserialisation",
                    "Track was not found. Path should be " + track.getPath());
            return null;
        }

        return ret;
    }

    /**
     * Serialises a track to a XML-file stored on the SD-card in folder
     * TraceBook/<track name>. Also serialises all Media. The XML-file is
     * therefore not OSM compatible.
     */
    public void serialise() {
        serialise(true);
    }

    /**
     * Creates new folder in .../TraceBook for this Track. Such a directory must
     * exist when track is serialised.
     */
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
     * 
     * @return path to the track directory
     */
    public String getTrackDirPath() {
        return DataStorage.getTraceBookDirPath() + File.separator + name;
    }

    /**
     * Completes a track directory name to a complete path. Note: Do not changed
     * as this method is misused somewhere.
     * 
     * @param dir
     *            Name of the track directory
     * @return The complete path to the track directory.
     */
    public static String getTrackDirPath(String dir) {
        return DataStorage.getTraceBookDirPath() + File.separator + dir;
    }

    /**
     * Returns the complete String of the path to the track.tbt file of a track.
     * 
     * @param trackname
     *            The String of the track name
     * @return The path as String
     */
    public static String getPathOfTrackTbTFile(String trackname) {
        return getTrackDirPath(trackname) + File.separator + "track.tbt";
    }

    /**
     * Get a DataPointsList with a given id.
     * 
     * @param id
     *            The id of the DataPointsList
     * @return The DataPointsList or null if there is none with such an id.
     */
    public DataPointsList getPointsListById(int id) {
        for (DataPointsList dpl : ways) {
            if (dpl.getId() == id) {
                return dpl;
            }
        }
        return null;
    }

    /**
     * Get a DataNode with a given id.
     * 
     * @param id
     *            The id of the DataNode
     * @return The DataNode or null if there is none with such an id.
     */
    public DataNode getNodeById(int id) {
        for (DataNode dn : nodes) {
            if (dn.getId() == id) {
                return dn;
            }
        }

        for (DataPointsList dpl : ways) {
            DataNode dn = dpl.getNodeById(id);
            if (dn != null)
                return dn;
        }

        return null;
    }

    /**
     * Adds a way to the ways of this Track.
     * 
     * @param way
     *            the DataPointsList to be added
     */
    private void addWay(DataPointsList way) {
        ways.add(way);
    }

    /**
     * Search the whole track for an DataMapObject by id. This may be a DataNode
     * or DataPointsList.
     * 
     * @param id
     *            The id of the DataMapObject that is being searched for.
     * @return The DataMapObject where get_id()==id or null if there is not such
     *         an object.
     */
    public DataMapObject getDataMapObjectById(int id) {

        DataMapObject res = getNodeById(id);
        if (res != null) {
            return res;
        }

        for (DataPointsList dpl : getWays()) {
            res = dpl.getNodeById(id);
            if (res != null) {
                return res;
            }
        }

        res = getPointsListById(id);
        return res;
    }

    /**
     * This method saves a String to a .txt-file and generates a
     * DataMedia-object which can be added to any DataMediaHolder.
     * 
     * @param text
     *            The text to save
     * @return DataMedia object which references the Text just saved.
     */
    public DataMedia saveText(String text) {
        File txtfile = new File(getTrackDirPath() + File.separator
                + getFilenameCompatibleTimeStamp() + ".txt");
        try {
            if (txtfile.createNewFile()) {
                BufferedWriter buf = new BufferedWriter(new FileWriter(txtfile));
                buf.write(text);
                buf.close();
            } else {
                Log.w("MediaSavingText",
                        "Text file with this timestamp already exists.");
            }
        } catch (IOException e) {
            Log.e("MediaSavingText", "Error while writing text file.");
            return null;
        }
        return new DataMedia(txtfile.getParent(), txtfile.getName());
    }
}
