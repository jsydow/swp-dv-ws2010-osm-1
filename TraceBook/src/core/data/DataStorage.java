package core.data;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * The class that holds all Data.
 * 
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
	 * default private constructor
	 */
	DataStorage() {
		tracks = new LinkedList<DataTrack>();
		names = new LinkedList<String>();
		retrieveTrackNames();
		// TODO load lastID
	}

	/**
	 * Singleton implementation
	 * 
	 * @return instance of this class
	 */
	public static DataStorage getInstance() {
		if (instance == null)
			instance = new DataStorage();
		return instance;
	}

	/**
	 * Create a new unique id to use for a new map object.
	 * 
	 * @return a new unique id
	 */
	public int getID() {
		lastID++;
		// TODO save last id
		return lastID;
	}

	/**
	 * Returns a list of the names of all tracks that are currently stored in
	 * this DataStorage object. The names can be used as argument to getTrack()
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
	 *            returned by getAllTracks()
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
	 * @return the new Track
	 */
	public DataTrack newTrack() {
		DataTrack dt = new DataTrack(null);
		tracks.add(dt);
		return dt;
	}

	/**
	 * Setter-method for the currently edited Track
	 * 
	 * @param currentTrack
	 * @return the parameter currentTrack
	 */
	public DataTrack setCurrentTrack(DataTrack currentTrack) {
		this.currentTrack = currentTrack;
		return currentTrack;
	}

	/**
	 * Getter-method
	 * 
	 * @return the currently edited Track
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
	 *            The name of the Track
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
		// TODO
	}

	/**
	 * Will serialize all tracks that are currently stored in this DataStorage.
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

}
