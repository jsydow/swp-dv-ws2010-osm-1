package TraceBook;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.AutoScaleAction;
import org.openstreetmap.josm.actions.ExtensionFileFilter;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.gpx.GpxData;
import org.openstreetmap.josm.data.gpx.WayPoint;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.WayData;
import org.openstreetmap.josm.gui.layer.GpxLayer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.gui.layer.markerlayer.AudioMarker;
import org.openstreetmap.josm.gui.layer.markerlayer.ImageMarker;
import org.openstreetmap.josm.gui.layer.markerlayer.Marker;
import org.openstreetmap.josm.gui.layer.markerlayer.MarkerLayer;
import org.openstreetmap.josm.gui.progress.NullProgressMonitor;
import org.openstreetmap.josm.gui.progress.ProgressMonitor;
import org.openstreetmap.josm.io.FileImporter;
import org.openstreetmap.josm.io.IllegalDataException;
import org.openstreetmap.josm.tools.DateUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Importer class for TraceBookTrack files
 * 
 * @author anubis
 * 
 */
public class TraceBookImporter extends FileImporter {
    public static final String TRACEBOOK_FILE_EXT = "tbt";
    public static final String TRACEBOOK_FILE_EXT_DOT = ".";

    public TraceBookImporter() {
        this(new ExtensionFileFilter(TRACEBOOK_FILE_EXT, TRACEBOOK_FILE_EXT,
                tr("TraceBook Track Files (*" + TRACEBOOK_FILE_EXT_DOT
                        + TRACEBOOK_FILE_EXT + ")")));
    }

    public TraceBookImporter(ExtensionFileFilter filter) {
        super(filter);
        // TODO Auto-generated constructor stub
    }

    /**
     * Imports data from a TraceBookTrack (*.tbt) file.
     * 
     * @param file
     *            The file reference to use for opening.
     * @param progressMonitor
     *            The ProgressMonitor used to track the loading progress.
     * @throws IllegalDataException
     *             Is thrown when the handled data is not parseable.
     * @throws IOException
     *             Is thrown when the file can't be read for some reason.
     */
    @Override
    public void importData(File file, ProgressMonitor progressMonitor)
            throws IOException, IllegalDataException {
        String fn = file.getPath();

        if (progressMonitor == null) { // make sure that there is a progress
            // monitor...
            progressMonitor = NullProgressMonitor.INSTANCE;
        }

        progressMonitor.beginTask(String.format("Importing TBT file %s...",
                file.getName(), 4));
        progressMonitor.setTicksCount(1);

        if (fn.toLowerCase().endsWith(TRACEBOOK_FILE_EXT)) {
            try {
                // ColumbusCSVReader r = new ColumbusCSVReader();
                //
                // // transform CSV into GPX

                DataSet osmdata = new DataSet();

                GpxData gpxData = new GpxData();// r.transformColumbusCSV(fn);
                GpxLayer gpxLayer = new GpxLayer(gpxData, file.getName());
                Main.main.addLayer(gpxLayer);
                MarkerLayer ml = null;
                ml = new MarkerLayer(gpxData, tr("Markers from {0}", file
                        .getName()), file, gpxLayer);

                progressMonitor.setTicksCount(3);
                DocumentBuilderFactory fac = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder dom = fac.newDocumentBuilder();
                Document doc = dom.parse(new File(fn));
                NodeList nl = doc.getElementsByTagName("node");
                DecimalFormatSymbols decsymb = new DecimalFormatSymbols();
                decsymb.setDecimalSeparator('.');

                DecimalFormat decform = new DecimalFormat("0.0000000", decsymb);
                HashMap<String, org.openstreetmap.josm.data.osm.Node> NodesMap = new HashMap<String, org.openstreetmap.josm.data.osm.Node>();
                for (int i = 0; i < nl.getLength(); i++) {

                    NamedNodeMap attributes = nl.item(i).getAttributes();
                    Node lat = attributes.getNamedItem("lat");
                    Node lon = attributes.getNamedItem("lon");
                    LatLon latlon = new LatLon(decform
                            .parse(lat.getNodeValue()).doubleValue(), decform
                            .parse(lon.getNodeValue()).doubleValue());

                    org.openstreetmap.josm.data.osm.Node newosmnode = new org.openstreetmap.josm.data.osm.Node();
                    newosmnode.setOsmId(Long.parseLong(((Attr) nl.item(i)
                            .getAttributes().getNamedItem("id")).getValue()),
                            Integer.parseInt(((Attr) nl.item(i).getAttributes()
                                    .getNamedItem("version")).getValue()));
                    // new org.openstreetmap.josm.data.osm.Node(Long
                    // .parseLong(((Attr) attributes.getNamedItem("id"))
                    // .getValue()));
                    // newosmnode.setVisible(true);
                    newosmnode.setCoor(latlon);
                    Main.main.debug("created new node: "
                            + Long.toString(newosmnode.getId()));
                    NodeList childs = nl.item(i).getChildNodes();
                    HashMap<String, String> tags = null;
                    for (int a = 0; a < childs.getLength(); a++) {

                        if (childs.item(a).getNodeName()
                                .equalsIgnoreCase("tag")) {
                            if (tags == null)
                                tags = new HashMap<String, String>();
                            tags.put(((Attr) childs.item(a).getAttributes()
                                    .getNamedItem("k")).getValue(),
                                    ((Attr) childs.item(a).getAttributes()
                                            .getNamedItem("v")).getValue());
                        }

                        if (childs.item(a).getNodeName().equalsIgnoreCase(
                                "link")) {
                            WayPoint x = new WayPoint(latlon);
                            Main.main.debug("child: "
                                    + childs.item(a).getNodeName()
                                    + " : "
                                    + file.getParent()
                                    + File.separatorChar
                                    + ((Attr) childs.item(a).getAttributes()
                                            .getNamedItem("href")).getValue());
                            File relativePath = new File(file.getParent()
                                    + File.separatorChar
                                    + ((Attr) childs.item(a).getAttributes()
                                            .getNamedItem("href")).getValue());
                            String uri = relativePath.toURI().toString();
                            Main.main.debug(uri);
                            if (uri.endsWith(".png") || uri.endsWith(".jpg")
                                    || uri.endsWith(".jpeg")
                                    || uri.endsWith(".gif")) {
                                Marker mr = ImageMarker.create(latlon, uri, ml,
                                        1.0, 1.0);
                                if (mr == null)
                                    Main.main
                                            .debug("THIS FUCKING MARKER IS EMPTY!!!");
                                ml.data.add(mr);
                            } else if (uri.endsWith(".wav")) {
                                Marker mr = AudioMarker.create(latlon,
                                        "Audio comment", uri, ml, 1.0, 1.0);
                                if (mr == null)
                                    Main.main
                                            .debug("THIS FUCKING MARKER IS EMPTY!!!");
                                ml.data.add(mr);
                            }

                        }
                    }

                    if (tags != null) {
                        newosmnode.setKeys(tags);
                    }
                    newosmnode.setTimestamp(DateUtils.fromString(((Attr) nl
                            .item(i).getAttributes().getNamedItem("timestamp"))
                            .getValue()));
                    osmdata.addPrimitive(newosmnode);
                    Main.main
                            .debug("new nodes id: "
                                    + ((Attr) attributes.getNamedItem("id"))
                                            .getValue());
                    NodesMap.put(((Attr) attributes.getNamedItem("id"))
                            .getValue(), newosmnode);
                }
                NodeList nlw = doc.getElementsByTagName("way");
                for (int i = 0; i < nlw.getLength(); i++) {

                    // WayData wd = new WayData();
                    // readCommon(atts, wd);
                    // Way w = new Way(wd.getId(), wd.getVersion());
                    // w.setVisible(wd.isVisible());
                    // w.load(wd);
                    // externalIdMap.put(wd.getPrimitiveId(), w);
                    // ways.put(wd.getUniqueId(), new ArrayList<Long>());
                    // currentPrimitive = w;
                    // currentExternalId = wd.getUniqueId();
                    WayData wd = new WayData();
                    wd.setVersion(Integer.parseInt(((Attr) nlw.item(i)
                            .getAttributes().getNamedItem("version"))
                            .getValue()));

                    org.openstreetmap.josm.data.osm.Way newway = new org.openstreetmap.josm.data.osm.Way();
                    newway.load(wd);
                    Date date = new Date();

                    newway.setOsmId(1l, wd.getVersion());
                    List<org.openstreetmap.josm.data.osm.Node> waynodes = new LinkedList<org.openstreetmap.josm.data.osm.Node>();
                    NodeList childs = nlw.item(i).getChildNodes();
                    HashMap<String, String> tags = null;
                    for (int a = 0; a < childs.getLength(); a++) {

                        if (childs.item(a).getNodeName().equalsIgnoreCase("nd")) {
                            String Key = ((Attr) childs.item(a).getAttributes()
                                    .getNamedItem("ref")).getValue();
                            if (NodesMap.get(Key) == null)
                                Main.main
                                        .debug("Hey we got a null node, impossible to add it to a way!");
                            else {
                                waynodes.add(NodesMap.get(Key));
                                // newway.addNode(NodesMap.get(Key));
                                Main.main.debug("Adding node " + Key + " ("
                                        + "" + ") to way " + newway.getId());
                            }
                        }
                        if (childs.item(a).getNodeName()
                                .equalsIgnoreCase("tag")) {
                            if (tags == null)
                                tags = new HashMap<String, String>();
                            tags.put(((Attr) childs.item(a).getAttributes()
                                    .getNamedItem("k")).getValue(),
                                    ((Attr) childs.item(a).getAttributes()
                                            .getNamedItem("v")).getValue());
                        }
                    }
                    if (nlw.item(i).getAttributes().getNamedItem("timestamp") != null)
                        newway.setTimestamp(DateUtils.fromString(((Attr) nlw
                                .item(i).getAttributes().getNamedItem(
                                        "timestamp")).getValue()));
                    newway.setVisible(true);
                    newway.setNodes(waynodes);
                    if (tags != null) {
                        newway.setKeys(tags);
                    }
                    osmdata.addPrimitive(newway);

                }

                // doc.getDocumentElement().normalize();
                Main.main.debug("Items in the GpxLayer: "
                        + gpxLayer.data.waypoints.size());
                progressMonitor.setTicksCount(1);
                //
                // r.dropBufferLists();
                //
                progressMonitor.setTicksCount(2);
                OsmDataLayer osmdatalayer = new OsmDataLayer(osmdata, fn, file);

                // add layer to show way points
                // Main.main.addLayer(gpxLayer);
                Main.main.addLayer(osmdatalayer);
                Main.main.removeLayer(gpxLayer);// Dirty hack to avoid
                // nullpointer exception in
                // markerlayer
                //

                //
                // // ... and scale view appropriately - if wished by user
                // if (!ColumbusCSVPreferences.dontZoom())
                {
                    AutoScaleAction action = new AutoScaleAction("data");
                    action.autoScale();
                }
                progressMonitor.setTicksCount(4);

                if (Main.pref.getBoolean("marker.makeautomarkers", true)) {
                    Main.main.debug("makeautomarkers was true");

                    // Main.main.addLayer(ml);
                    if (ml.data.size() > 0) {
                        Main.main.debug("There were markers in the GPXDATA");
                        for (Marker m : ml.data) {
                            if (m == null)
                                Main.main.debug("Null Marker!");
                            else
                                Main.main.debug("Valid Marker!");
                        }
                        Main.main.addLayer(ml);
                    }
                }
            } catch (Exception e) {
                // catch and forward exception
                throw new IllegalDataException(e);
            } finally { // take care of monitor...
                progressMonitor.finishTask();
            }
        } else {
            throw new IOException(
                    tr(String
                            .format(
                                    "Unsupported file extension (file '%s' does not end with '%s')!",
                                    file.getName(), TRACEBOOK_FILE_EXT)));
        }
    }
}