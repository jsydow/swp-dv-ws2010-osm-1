package core.data;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import android.location.Location;

/**
 * The class that holds all Data. The class has 0 to several Tracks. Each Track
 * consists of Nodes and PointLists (Area/Way). PointsLists consist of Nodes.
 * 
 * In the implementation u see there is a difference between the track names and
 * the tracks themselves. The names-list contains all the names of the tracks
 * that are in the working memory and on the devices memory. It may not be
 * perfectly synchronised with the actual tracks available as it is updated only
 * when needed. These names can be used to actually load a Track completely into
 * memory. The primary reason for the names is the list of all Tracks without
 * loading them all.
 * 
 * It is a Singleton!
 * 
 * @author js
 * 
 */
public class DataStorage implements SerialisableContent {

	/**
	 * Singleton instance
	 */
	private static DataStorage instance;
	/**
	 * All loaded Tracks
	 */
	private List<DataTrack> tracks;
	/**
	 * A List of all possible track names on the working memory and devices
	 * memory
	 */
	private List<String> names;

	/**
	 * last given ID for a MapObject.
	 */
	private int lastID;

	/**
	 * currently active Track
	 */
	private DataTrack currentTrack;
	
	/**
	 * last known gps location, may be updated independent of ongoing logging 
	 */
	private Location lastLocation;

	/**
	 * Default private constructor for Singleton implementation.
	 */
	DataStorage() {
		tracks = new LinkedList<DataTrack>();
		names = new LinkedList<String>();
		retrieveTrackNames();
		// TODO load lastID
	}

	/**
	 * Singleton implementation. This method returns the one and only instance
	 * of this class.
	 * 
	 * @return The instance of this class.
	 */
	public static DataStorage getInstance() {
		if (instance == null)
			instance = new DataStorage();
		return instance;
	}

	/**
	 * Create a new unique id to use for a new map object.
	 * 
	 * @return A new unique id.
	 */
	public int getID() {
		lastID++;
		// TODO save last id
		return lastID;
	}

	/**
	 * Returns a list of the names of all tracks that are currently stored in
	 * this DataStorage object. The names can be used as argument to getTrack().
	 * 
	 * @return List of the names of all tracks. If there are no tracks stored
	 *         then the list will be empty.
	 */
	public List<String> getAllTracks() {
		updateNames();
		return names;
	}

	/**
	 * This method returns a Track object that specified by a name. Note that
	 * only tracks that the DataStorage currently stores can be returned.
	 * 
	 * @param name
	 *            The name of a track as returned by getAllTracks()
	 * @return If such a track exists the Track itself is returned. If the track
	 *         does not exist however null is returned.
	 */
	public DataTrack getTrack(String name) {
		for (DataTrack dt : tracks) {
			if (dt.getName().equals(name)) {
				return dt;
			}
		}
		return null;
	}

	/**
	 * Deletes a Track from working memory and devices memory.
	 * 
	 * @param name
	 *            The name of a track as stored in the Track object or as
	 *            returned by getAllTracks().
	 */
	public void deleteTrack(String name) {
		ListIterator<DataTrack> lit = tracks.listIterator();
		DataTrack dt;
		while (lit.hasNext()) {
			dt = lit.next();
			if (dt.getName().equals(name)) {
				dt.delete();
				lit.remove();
				break;
			}
		}
	}

	/**
	 * Create a new Track in working memory. Don't forget to serialise it!
	 * 
	 * @return The newly created Track.
	 */
	public DataTrack newTrack() {
		DataTrack dt = new DataTrack(null);
		tracks.add(dt);
		return dt;
	}

	/**
	 * Setter-method for the currently edited Track.
	 * 
	 * @param currentTrack
	 *            The new currently edited Track.
	 * @return The parameter currentTrack is simple returned for further use.
	 */
	public DataTrack setCurrentTrack(DataTrack currentTrack) {
		this.currentTrack = currentTrack;
		return currentTrack;
	}

	/**
	 * Getter-method
	 * 
	 * @return The currently edited Track is returned.
	 */
	public DataTrack getCurrentTrack() {
		return currentTrack;
	}

	/**
	 * Loads all Tracks. Caution this can be a lot of Data! If only the names
	 * are needed use retrieveTrackNames()
	 */
	public void deserialiseAll() {
		// TODO
	}

	/**
	 * Loads the complete Track (with everything it contains) into working
	 * memory. If such a Track does not exist nothing is done.
	 * 
	 * @param name
	 *            The name of the Track.
	 */
	public void deserialiseTrack(String name) {
		DataTrack dt = DataTrack.deserialise(name);
		if (dt != null)
			tracks.add(dt);
	}

	/**
	 * Load the list of all Tracks that are stored on the devices memory. These
	 * names can be returned by getAllTracks().
	 */
	public void retrieveTrackNames() {
		// TODO
	}

	/**
	 * Updates the list of all names. Normally it is unnecessary to use this
	 * method as getAllTracks() calls this method.
	 */
	public void updateNames() {
		names.clear();
		for (DataTrack dt : tracks) {
			names.add(dt.getName());
		}
		// TODO also update names from tracks on devices memory
	}

	/**
	 * Will serialise all tracks that are currently stored in this DataStorage.
	 */
	public void serialise() {
		for (DataTrack dt : tracks)
			dt.serialise();
	}

	/**
	 * Will delete ALL tracks! Therefore resets the all data that have been
	 * stored on the device.
	 */
	public void delete() {
		for (DataTrack dt : tracks)
			dt.delete();
		tracks.clear();
	}
	
	/**
	 * Sets the current location, this is used for visualizing the last position
	 * even if there is no logging going on
	 * @param loc
	 */
	public void setLastLocation(Location loc) {
		lastLocation = loc;
	}

	/**
	 * returns the last known GPS location
	 * @return
	 */
	public Location getLastLocation() {
		return lastLocation;
	}

}
