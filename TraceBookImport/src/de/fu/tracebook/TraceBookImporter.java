/*======================================================================
 *
 * This file is part of TraceBook.
 *
 * TraceBook is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * TraceBook is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TraceBook. If not, see <http://www.gnu.org/licenses/>.
 *
 =====================================================================*/

package de.fu.tracebook;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.AutoScaleAction;
import org.openstreetmap.josm.actions.ExtensionFileFilter;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.gpx.GpxData;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.WayData;
import org.openstreetmap.josm.gui.layer.GpxLayer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.gui.layer.markerlayer.ImageMarker;
import org.openstreetmap.josm.gui.layer.markerlayer.Marker;
import org.openstreetmap.josm.gui.layer.markerlayer.MarkerLayer;
import org.openstreetmap.josm.gui.progress.NullProgressMonitor;
import org.openstreetmap.josm.gui.progress.ProgressMonitor;
import org.openstreetmap.josm.io.FileImporter;
import org.openstreetmap.josm.io.IllegalDataException;
import org.openstreetmap.josm.tools.DateUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Importer class for TraceBookTrack files.
 * 
 * @author anubis
 * 
 */
public class TraceBookImporter extends FileImporter {
    /**
     * Static file extension key for TraceBook files.
     */
    public static final String TRACEBOOK_FILE_EXT = "tbt";
    /**
     * Static file extension separator for TraceBook files.
     */
    public static final String TRACEBOOK_FILE_EXT_DOT = ".";

    /**
     * The empty standard constructor. Defines the default
     * {@link ExtensionFileFilter} for TraceBookTrack files
     */
    public TraceBookImporter() {
        this(new ExtensionFileFilter(TRACEBOOK_FILE_EXT, TRACEBOOK_FILE_EXT,
                tr("TraceBook Track files (*" + TRACEBOOK_FILE_EXT_DOT
                        + TRACEBOOK_FILE_EXT + ")")));
    }

    /**
     * The standard constructor.
     * 
     * @param filter
     *            The {@link ExtensionFileFilter} for TraceBookTrack files.
     */
    public TraceBookImporter(ExtensionFileFilter filter) {
        super(filter);
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
        ProgressMonitor myProgressMonitor;
        if (progressMonitor == null) { // make sure that there is a progress
            // monitor...
            myProgressMonitor = NullProgressMonitor.INSTANCE;
        } else {
            myProgressMonitor = progressMonitor;
        }

        if (fn.toLowerCase().endsWith(TRACEBOOK_FILE_EXT)) {
            try {
                DataSet osmdata = new DataSet();

                GpxData gpxData = new GpxData();
                GpxLayer gpxLayer = new GpxLayer(gpxData, file.getName());
                Main.main.addLayer(gpxLayer);
                MarkerLayer ml = null;
                ml = new MarkerLayer(gpxData, tr("Markers from {0}",
                        file.getName()), file, gpxLayer);

                DocumentBuilderFactory fac = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder dom = fac.newDocumentBuilder();
                Document doc = dom.parse(new File(fn));
                // getting nodes and ways to determine overall progress
                NodeList nl = doc.getElementsByTagName("node");
                NodeList nlw = doc.getElementsByTagName("way");
                DecimalFormatSymbols decsymb = new DecimalFormatSymbols();
                decsymb.setDecimalSeparator('.');

                DecimalFormat decform = new DecimalFormat("0.0000000", decsymb);
                HashMap<String, org.openstreetmap.josm.data.osm.Node> nodesMap = new HashMap<String, org.openstreetmap.josm.data.osm.Node>();
                myProgressMonitor.beginTask(
                        String.format("Importing Track data %s...",
                                file.getName(), nl.getLength()), nl.getLength()
                                + nlw.getLength());
                myProgressMonitor.subTask("nodes...");
                for (int i = 0; i < nl.getLength(); i++) {
                    myProgressMonitor.worked(1);
                    NamedNodeMap attributes = nl.item(i).getAttributes();
                    myProgressMonitor.setExtraText(((Attr) nl.item(i)
                            .getAttributes().getNamedItem("id")).getValue());
                    Node lat = attributes.getNamedItem("lat");
                    Node lon = attributes.getNamedItem("lon");
                    LatLon latlon = new LatLon(decform
                            .parse(lat.getNodeValue()).doubleValue(), decform
                            .parse(lon.getNodeValue()).doubleValue());

                    org.openstreetmap.josm.data.osm.Node newosmnode = new org.openstreetmap.josm.data.osm.Node();
                    newosmnode.setOsmId(
                            Long.parseLong(((Attr) nl.item(i).getAttributes()
                                    .getNamedItem("id")).getValue()),
                            Integer.parseInt(((Attr) nl.item(i).getAttributes()
                                    .getNamedItem("version")).getValue()));
                    newosmnode.setCoor(latlon);
                    Main.debug("created new node: "
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

                        if (childs.item(a).getNodeName()
                                .equalsIgnoreCase("link")) {
                            Main.debug("child: "
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
                            Main.debug(uri);
                            if (uri.endsWith(".png") || uri.endsWith(".jpg")
                                    || uri.endsWith(".jpeg")
                                    || uri.endsWith(".gif")) {
                                Main.debug("Trying to parse imagemarker");
                                Marker mr = ImageMarker.create(latlon, uri, ml,
                                        1.0, 1.0);
                                if (mr == null)
                                    Main.debug("THIS MARKER IS EMPTY!!!");
                                ml.data.add(mr);
                            } else if (uri.endsWith(".wav")
                                    || uri.endsWith(".m4a")) {
                                Main.debug("Trying to parse audiomarker");
                                Marker mr = tracebook.AudioMarker.create(
                                        latlon, "Audio comment", uri, ml, 1.0,
                                        1.0);
                                if (mr == null)
                                    Main.debug("THIS MARKER IS EMPTY!!!");
                                ml.data.add(mr);
                            } else if (uri.endsWith(".3gp")
                                    || uri.endsWith(".mp4")) {
                                Main.debug("Trying to parse videomarker");
                                Marker mr = VideoMarker.create(latlon,
                                        "Video comment", uri, ml, 1.0, 1.0);
                                if (mr == null)
                                    Main.debug("THIS MARKER IS EMPTY!!!");
                                ml.data.add(mr);
                            } else if (uri.endsWith(".txt")) {
                                Main.debug("Trying to parse textmarker");
                                Marker mr = TextMarker.create(latlon,
                                        "Text comment", uri, ml, 1.0, 1.0);
                                if (mr == null)
                                    Main.debug("THIS F-ING MARKER IS EMPTY!!!");
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
                    Main.debug("new nodes id: "
                            + ((Attr) attributes.getNamedItem("id")).getValue());
                    nodesMap.put(
                            ((Attr) attributes.getNamedItem("id")).getValue(),
                            newosmnode);
                }

                myProgressMonitor.subTask("ways...");
                for (int i = 0; i < nlw.getLength(); i++) {
                    myProgressMonitor.worked(1);
                    myProgressMonitor.setExtraText(((Attr) nlw.item(i)
                            .getAttributes().getNamedItem("id")).getValue());
                    WayData wd = new WayData();
                    wd.setVersion(Integer.parseInt(((Attr) nlw.item(i)
                            .getAttributes().getNamedItem("version"))
                            .getValue()));

                    org.openstreetmap.josm.data.osm.Way newway = new org.openstreetmap.josm.data.osm.Way();
                    newway.load(wd);
                    newway.setOsmId(1L, wd.getVersion());
                    List<org.openstreetmap.josm.data.osm.Node> waynodes = new LinkedList<org.openstreetmap.josm.data.osm.Node>();
                    NodeList childs = nlw.item(i).getChildNodes();
                    HashMap<String, String> tags = null;
                    for (int a = 0; a < childs.getLength(); a++) {

                        if (childs.item(a).getNodeName().equalsIgnoreCase("nd")) {
                            String key = ((Attr) childs.item(a).getAttributes()
                                    .getNamedItem("ref")).getValue();
                            if (nodesMap.get(key) == null)
                                Main.debug("Hey we got a null node, impossible to add it to a way!");
                            else {
                                waynodes.add(nodesMap.get(key));
                                Main.debug("Adding node " + key + " (" + ""
                                        + ") to way " + newway.getId());
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
                                .item(i).getAttributes()
                                .getNamedItem("timestamp")).getValue()));
                    newway.setVisible(true);
                    newway.setNodes(waynodes);
                    if (tags != null) {
                        newway.setKeys(tags);
                    }
                    osmdata.addPrimitive(newway);

                }

                Main.debug("Items in the GpxLayer: "
                        + gpxLayer.data.waypoints.size());

                OsmDataLayer osmdatalayer = new OsmDataLayer(osmdata, fn, file);

                Main.main.addLayer(osmdatalayer);
                Main.main.removeLayer(gpxLayer);
                // Dirty hack to avoid
                // {@link NullPointerException} in
                // {@link MarkerLayer}
                //

                //

                AutoScaleAction action = new AutoScaleAction("data");
                action.autoScale();

                // myProgressMonitor.setTicksCount(4);

                if (Main.pref.getBoolean("marker.makeautomarkers", true)) {
                    Main.debug("makeautomarkers was true");

                    if (ml.data.size() > 0) {
                        Main.debug("There were markers in the GPXDATA");
                        for (Marker m : ml.data) {
                            if (m == null)
                                Main.debug("Null Marker!");
                            else
                                Main.debug("Valid Marker!");
                        }
                        Main.main.addLayer(ml);
                    }
                }
            } catch (IOException e) {
                // catch and forward exception
                throw new IllegalDataException(e);
            } catch (NullPointerException e) {
                // catch and forward exception
                throw new IllegalDataException(e);
            } catch (ParserConfigurationException e) {
                // catch and forward exception
                throw new IllegalDataException(e);
            } catch (SAXException e) {
                // catch and forward exception
                throw new IllegalDataException(e);
            } catch (DOMException e) {
                // catch and forward exception
                throw new IllegalDataException(e);
            } catch (ParseException e) {
                // catch and forward exception
                throw new IllegalDataException(e);
            } finally { // take care of monitor...

                myProgressMonitor.finishTask();
            }
        } else {
            throw new IOException(
                    tr(String
                            .format("Unsupported file extension (file '%s' does not end with '%s')!",
                                    file.getName(), TRACEBOOK_FILE_EXT)));
        }
    }
}
