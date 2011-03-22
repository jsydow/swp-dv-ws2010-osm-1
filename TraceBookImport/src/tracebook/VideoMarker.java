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
 * A class for displaying embedded video data from TraceBookTrack(*.tbt) files
 * recorded with android.
 * 
 * @author anubis
 */
public class VideoMarker extends ButtonMarker {
    private URL videourl;

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
     *            URL to the video file
     * @param parentLayer
     *            The layer to create the VideoMarker on
     * @param time
     *            undocumented parameter for the super constructor
     * @param offset
     *            undocumented parameter for the super constructor
     * @return A new VideoMarker or null on fail
     */
    public static VideoMarker create(LatLon ll, String text, String url,
            MarkerLayer parentLayer, double time, double offset) {

        try {
            return new VideoMarker(ll, text, new URL(url), parentLayer, time,
                    offset);
        } catch (MalformedURLException e) {
            return null;
        }

    }

    /**
     * Creates a new VideoMarker with the given ParentLayer.
     * 
     * @param ll
     *            LatLon value for the location
     * @param text
     *            Text to display for the Marker
     * @param videourl
     *            URL to the video file
     * @param parentlayer
     *            The layer to create the VideoMarker on
     * @param time
     *            undocumented parameter for the super constructor
     * @param offset
     *            undocumented parameter for the super constructor
     */
    public VideoMarker(LatLon ll, String text, URL videourl,
            MarkerLayer parentlayer, double time, double offset) {
        super(ll, text, "tracebook_video.png", parentlayer, time, offset);
        this.videourl = videourl;
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
        // play();
        Main.debug("The video marker for file " + videourl.getFile());

        Desktop desktop = null;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        } else {
            Main
                    .debug("Failed to get a Desktop to determine the file handler!");
            return;
        }

        try {
            desktop.open(new File(videourl.toURI()));
        } catch (IOException e) {
            Main.debug("IOException while trying to open videolink: "
                    + videourl.toString());
            e.printStackTrace();
        } catch (URISyntaxException e) {
            Main.debug("URISyntaxException while trying to open videolink: "
                    + videourl.toString());
            e.printStackTrace();
        }

    }
}
