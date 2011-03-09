package TraceBook;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.AutoScaleAction;
import org.openstreetmap.josm.actions.ExtensionFileFilter;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.gpx.GpxData;
import org.openstreetmap.josm.data.gpx.GpxLink;
import org.openstreetmap.josm.data.gpx.WayPoint;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.layer.GpxLayer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.gui.layer.markerlayer.ImageMarker;
import org.openstreetmap.josm.gui.layer.markerlayer.Marker;
import org.openstreetmap.josm.gui.layer.markerlayer.MarkerLayer;
import org.openstreetmap.josm.gui.progress.NullProgressMonitor;
import org.openstreetmap.josm.gui.progress.ProgressMonitor;
import org.openstreetmap.josm.io.FileImporter;
import org.openstreetmap.josm.io.IllegalDataException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
				
				
				progressMonitor.setTicksCount(3);
				DocumentBuilderFactory fac = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder dom = fac.newDocumentBuilder();
				Document doc = dom.parse(new File(fn));
				NodeList nl = doc.getElementsByTagName("node");
				DecimalFormatSymbols decsymb = new DecimalFormatSymbols();
				decsymb.setDecimalSeparator('.');
				DecimalFormat decform = new DecimalFormat("0.0000000", decsymb);
				HashMap<String,Node> NodesMap=new HashMap<String,Node>();
				for (int i = 0; i < nl.getLength(); i++) {
					java.util.LinkedList<GpxLink> links = null;
					WayPoint x = null;
					Marker m;
					
					NamedNodeMap attributes = nl.item(i).getAttributes();
					Node lat = attributes.getNamedItem("lat");
					Node lon = attributes.getNamedItem("lon");
					LatLon latlon = new LatLon(decform
							.parse(lat.getNodeValue()).doubleValue(), decform
							.parse(lon.getNodeValue()).doubleValue());

					org.openstreetmap.josm.data.osm.Node newosmnode = new org.openstreetmap.josm.data.osm.Node();
					newosmnode.setCoor(latlon);
					
					
					NodeList childs = nl.item(i).getChildNodes();
					HashMap<String, String> tags=null;
					for (int a = 0; a < childs.getLength(); a++) {

						if (childs.item(a).getNodeName()
								.equalsIgnoreCase("tag")) {
							if (tags==null)tags	 = new HashMap<String, String>();
							tags.put(((Attr)childs.item(a)
									.getAttributes().getNamedItem("k")).getValue(), ((Attr)childs.item(a)
									.getAttributes().getNamedItem("v")).getValue());
						}

						if (childs.item(a).getNodeName().equalsIgnoreCase(
								"link")) {
							if (links == null)
								links = new LinkedList<GpxLink>();
							if (x == null)
								x = new WayPoint(latlon);
							Main.main.debug("child: "+childs.item(a).getNodeName()+" : "+((Attr)childs.item(a)
									.getAttributes().getNamedItem("href")).getValue());
							GpxLink link = new GpxLink(((Attr)childs.item(a)
									.getAttributes().getNamedItem("href")).getValue());
				
							links.add(link);

						}
					}
					if (links != null && x != null) {

						x.attr.put(GpxData.META_TIME, attributes.getNamedItem(
								"timestamp").getNodeName());
						x.setTime();
						x.attr.put(GpxData.META_LINKS, links);
						gpxData.waypoints.add(x);
					}
					if(tags!=null){
						newosmnode.setKeys(tags);
					}
					
					osmdata.addPrimitive(newosmnode);
		
					
					NodesMap.put(attributes.getNamedItem("id").getNodeName(), nl.item(i));
				}
				NodeList nlw = doc.getElementsByTagName("way");
				for (int i = 0; i < nlw.getLength(); i++) {
					org.openstreetmap.josm.data.osm.Way newway=new org.openstreetmap.josm.data.osm.Way ();
					
				}
				// doc.getDocumentElement().normalize();

				progressMonitor.setTicksCount(1);
				//
				// r.dropBufferLists();
				//
				progressMonitor.setTicksCount(2);
				OsmDataLayer osmdatalayer = new OsmDataLayer(osmdata, fn, file);
				

				// add layer to show way points
				//Main.main.addLayer(gpxLayer);
				Main.main.addLayer(osmdatalayer);


				//

				//
				// // ... and scale view appropriately - if wished by user
				// if (!ColumbusCSVPreferences.dontZoom())
				{
					AutoScaleAction action = new AutoScaleAction("data");
					action.autoScale();
				}
				progressMonitor.setTicksCount(4);

//				if (Main.pref.getBoolean("marker.makeautomarkers", true)) {
//					Main.main.debug("makeautomarkers was true");
//					MarkerLayer ml = new MarkerLayer(gpxData, tr(
//							"Markers from {0}", file.getName()), file, gpxLayer);
//					if (ml.data.size() > 0) {
//						Main.main.debug("There were markers in the GPXDATA");
//						Main.main.addLayer(ml);
//					}
//				}
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
