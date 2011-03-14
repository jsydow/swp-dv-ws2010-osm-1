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

    public TraceBookPlugin(PluginInformation arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
        ExtensionFileFilter.importers.add(new TraceBookImporter());
    }

}
