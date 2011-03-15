package tracebook;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.gui.layer.markerlayer.ButtonMarker;
import org.openstreetmap.josm.gui.layer.markerlayer.MarkerLayer;

/**
 * 
 * A class for displaying embedded text data from TraceBookTrack(*.tbt) files
 * recorded with android.
 * 
 * @author anubis
 */
public class TextMarker extends ButtonMarker {
    private URL texturl;

    /**
     * Verifies the parameter whether a new AudioMarker can be created and
     * return one or return <code>null</code>.
     * 
     * @param ll
     *            LatLon value for the location
     * 
     * @param text
     *            Text to display for the Marker
     * @param url
     *            URL to the text file
     * @param parentLayer
     *            The layer to create the TextMarker on
     * @param time
     *            undocumented parameter for the super constructor
     * @param offset
     *            undocumented parameter for the super constructor
     * @return A new TextMarker or null on fail
     */
    public static TextMarker create(LatLon ll, String text, String url,
            MarkerLayer parentLayer, double time, double offset) {

        try {
            return new TextMarker(ll, text, new URL(url), parentLayer, time,
                    offset);
        } catch (MalformedURLException e) {
            return null;
        }

    }

    /**
     * Creates a new TextMarker with the given ParentLayer.
     * 
     * @param ll
     *            LatLon value for the location
     * @param text
     *            Text to display for the Marker
     * @param texturl
     *            URL to the text file
     * @param parentlayer
     *            The layer to create the TextMarker on
     * @param time
     *            undocumented parameter for the super constructor
     * @param offset
     *            undocumented parameter for the super constructor
     */
    public TextMarker(LatLon ll, String text, URL texturl,
            MarkerLayer parentlayer, double time, double offset) {
        super(ll, text, "text.png", parentlayer, time, offset);
        this.texturl = texturl;
        // TODO Auto-generated constructor stub
    }

    /**
     * Callback function for starting the playback.
     * 
     * @param ev
     *            ActionEvent info how the action was performed.
     */
    @Override
    public void actionPerformed(ActionEvent ev) {
        Desktop desktop = null;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        } else {
            Main
                    .debug("Failed to get a Desktop to determine the file handler!");
            return;
        }

        try {
            desktop.open(new File(texturl.toURI()));
        } catch (IOException e) {
            Main.debug("IOException while trying to open textlink: "
                    + texturl.toString());
            e.printStackTrace();
        } catch (URISyntaxException e) {
            Main.debug("URISyntaxException while trying to open textlink: "
                    + texturl.toString());
            e.printStackTrace();
        }

    }
}
