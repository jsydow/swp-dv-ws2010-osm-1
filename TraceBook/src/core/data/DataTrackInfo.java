package core.data;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.util.Log;

/**
 * A class that simply holds information of a track. It is a class that only
 * provides information but cannot edit them. Use deserialise to get such an
 * object.
 * 
 * @author js
 * 
 */
public class DataTrackInfo {

    private final static String no_info = "keine Information";

    private DataTrackInfo() {
        timestamp = no_info;
        comment = no_info;
        name = no_info;
        numberOfPOIs = -1;
        numberOfWays = -1;
        numberOfMedia = -1;
    }

    private String timestamp;
    private String name;
    private String comment;
    private int numberOfPOIs;
    private int numberOfWays;
    private int numberOfMedia;

    /**
     * Getter-method for the time stamp of a track.
     * 
     * @return the time stamp as String
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Getter-method for the name of a track.
     * 
     * @return the name as String
     */
    public String getName() {
        return name;
    }

    /**
     * Getter-method for the comment of a track.
     * 
     * @return the comment as String
     */
    public String getComment() {
        return comment;
    }

    /**
     * @return the numberOfPOIs
     */
    public int getNumberOfPOIs() {
        return numberOfPOIs;
    }

    /**
     * @return the numberOfWays
     */
    public int getNumberOfWays() {
        return numberOfWays;
    }

    /**
     * @return the numberOfMedia
     */
    public int getNumberOfMedia() {
        return numberOfMedia;
    }

    /**
     * @param trackname
     *            Name of a track as String.
     * @return The info to this specific track or null if there is not such a
     *         track.
     */
    public static DataTrackInfo deserialise(String trackname) {
        DataTrackInfo info = new DataTrackInfo();
        info.name = trackname;

        File trackinfo = new File(DataTrack.getTrackDirPath(trackname)
                + File.separator + "info.xml");
        if (trackinfo.isFile()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();

            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document dom = builder.parse(trackinfo);

                // get <trackinfo>-element
                Element trackinfoelement = dom.getDocumentElement(); // root-element
                if (trackinfoelement == null)
                    throw new SAXException();

                NodeList nodeelements = trackinfoelement
                        .getElementsByTagName("data");
                if (nodeelements == null)
                    throw new SAXException();
                // for each child
                for (int i = 0; i < nodeelements.getLength(); ++i) {

                    // get key and value attribute
                    NamedNodeMap nnm = nodeelements.item(i).getAttributes();
                    String key = nnm.getNamedItem("key").getNodeValue();
                    String value = nnm.getNamedItem("value").getNodeValue();

                    // what is key?
                    if (key != null && value != null) {
                        if (key.equals("timestamp")) {
                            info.timestamp = value;
                        } else if (key.equals("comment")) {
                            info.comment = value;
                        } else if (key.equals("pois")) {
                            info.numberOfPOIs = Integer.parseInt(value);
                        } else if (key.equals("ways")) {
                            info.numberOfWays = Integer.parseInt(value);
                        } else if (key.equals("media")) {
                            info.numberOfMedia = Integer.parseInt(value);
                        }
                    } else {
                        Log.w("DeserialisingDataTrackInfo",
                                "XML-file is invalid. A data-node has no key-attribute.");
                    }
                }

            } catch (ParserConfigurationException e) {
                Log.e("DeserialisingDataTrackInfo", "This should not happen!");
                info = null;
            } catch (SAXException e) {
                Log.e("DeserialisingDataTrackInfo", "XML-file is not valid.!");
                info = null;
            } catch (IOException e) {
                Log.e("DeserialisingDataTrackInfo",
                        "Erro while reading XML file!");
                info = null;
            }

        } else {
            info = null;
        }
        return info;
    }

}
