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
