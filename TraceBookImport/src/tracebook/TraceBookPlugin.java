package tracebook;

import org.openstreetmap.josm.actions.ExtensionFileFilter;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

/**
 * Plugin class which handles TraceBookTrack (*.tbt) files.
 * 
 * @author anubis
 * 
 */
public class TraceBookPlugin extends Plugin {

    /**
     * Standard constructor from the Plugin super class.
     * 
     * @param arg0
     *            PluginInformation passed by JOSM
     */
    public TraceBookPlugin(PluginInformation arg0) {
        super(arg0);
        // ClassLoader cl = this.getClass().getClassLoader();
        //
        // ImageProvider.sources.add(cl);

        ExtensionFileFilter.importers.add(new TraceBookImporter());
    }

}
