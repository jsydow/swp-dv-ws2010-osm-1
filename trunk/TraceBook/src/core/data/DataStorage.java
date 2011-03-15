package core.data;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import android.os.Environment;
import android.util.Log;

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
 *
 * 
 */
public class DataStorage {

    /**
     * Singleton instance.
     */
    private static DataStorage instance;
    /**
     * All loaded Tracks.
     */
    private List<DataTrack> tracks;
    /**
     * A List of all possible track names on the working memory and devices
     * memory.
     */
    private List<String> names;

    /**
     * last given ID for a MapObject.
     */
    private int lastID;

    /**
     * Currently active Track.
     */
    private DataTrack currentTrack;

    /**
     * Default private constructor for Singleton implementation.
     */
    public DataStorage() {
        tracks = new LinkedList<DataTrack>();
        names = new LinkedList<String>();
        retrieveTrackNames();
        lastID = 1;
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
     * This method removes duplicates in a list of Strings.
     * 
     * @param l
     *            the list of Strings
     */
    static void removeDuplicatesInStringList(List<String> l) {
        Set<String> tmp = new HashSet<String>(l);
        l.clear();
        l.addAll(tmp);
        Collections.sort(l);
        return;
    }

    /**
     * Return a String of the path to the TraceBook directory without an ending
     * / Path is like: /sdcard/TraceBook.
     * 
     * @return path of the TraceBook directory
     */
    public static String getTraceBookDirPath() {
        return Environment.getExternalStorageDirectory() + File.separator
                + "TraceBook";
    }

    /**
     * Create a new unique id to use for a new map object.
     * 
     * @return A new unique id.
     */
    public int getID() {
        lastID++;
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
     * Deletes a directory and all files in it. If File is no directory nothing
     * is done.
     * 
     * @param dir
     *            The directory to delete
     */
    static void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File f : files) {
                if (f.isFile()) {
                    if (!f.delete()) {
                        Log.e("DeleteDirectory",
                                "Could not delete file " + f.getName()
                                        + " in directory " + dir.getPath());
                    }
                }
            }
            if (!dir.delete()) {
                Log.e("DeleteDirectory",
                        "Could not delete directory " + dir.getName());

            }
        }
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
        DataTrack dt = new DataTrack();
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
     * Getter-method.
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
        retrieveTrackNames();
        for (String tname : names) {
            deserialiseTrack(DataTrack.getTrackDirPath(tname));
        }
    }

    /**
     * Loads the complete Track (with everything it contains) into working
     * memory. If such a Track does not exist nothing is done.
     * 
     * @param name
     *            The name of the Track.
     * @return the deserialised Track
     */
    public DataTrack deserialiseTrack(String name) {
        DataTrack dt = DataTrack.deserialise(name);
        if (dt != null)
            tracks.add(dt);
        return dt;
    }

    /**
     * Load the list of all Tracks that are stored on the devices memory. It
     * empties the current list of track names. These names can be returned by
     * getAllTracks().
     */
    public void retrieveTrackNames() {
        File tracebookdir = new File(getTraceBookDirPath());

        if (tracebookdir.isDirectory()) {
            names.clear();
            File[] dirs = tracebookdir.listFiles();

            for (File f : dirs) {
                if (f.isDirectory()) {
                    File tracktbt = new File(DataTrack.getPathOfTrackTbTFile(f
                            .getName()));
                    if (tracktbt.isFile()) {
                        names.add(f.getName());
                    } else {
                        deleteDirectory(f);
                    }
                }
            }

        } else {
            Log.w("TraceBookDirectory",
                    "The TraceBook directory path doesn't point to a directory! wtf?");
        }
    }

    /**
     * Updates the list of all names. Normally it is unnecessary to use this
     * method as getAllTracks() calls this method.
     */
    public void updateNames() {
        retrieveTrackNames();
        for (DataTrack dt : tracks) {
            names.add(dt.getName());
        }

        removeDuplicatesInStringList(names);
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
     * 
     */
    public void unloadAllTracks() {
        tracks.clear();
        setCurrentTrack(null);
        updateNames();
    }

}
