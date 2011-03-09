package core.data;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.xmlpull.v1.XmlSerializer;

import android.util.Log;

/**
 * This is an object that refers to a medium. The medium itself is stored on the
 * background memory of the device. Only the name and path of the actual medium
 * is stored in this object.
 * 
 * @author js
 * 
 */
public class DataMedia {

	/**
	 * Media type constants. Type video.
	 */
	public static final int TYPE_VIDEO = 3;
	/**
     * Media type constants. Type audio.
     */
	public static final int TYPE_AUDIO = 2;
	/**
     * Media type constants. Type text.
     */
	public static final int TYPE_TEXT = 0;
	/**
     * Media type constants. Type picture.
     */
	public static final int TYPE_PICTURE = 1;
	
	private static String[] typesAsString = { "text", "picture", "audio",
			"video" };
	private static String[] extensions = { ".txt", ".jpg", ".m4a", ".mp4" };

	/**
	 * The internal id for this medium.
	 */
	private int id;

	/**
	 * The path to the file of the medium on the memory. This path+name should
	 * be sufficient to open the file. Path is therefore the base name.
	 */
	private String path;

	/**
	 * This name is the displayed name and filename (contains extension).
	 */
	private String name;

	/**
	 * This is the type of the medium. Use the TYPE_****-constants!
	 */
	private int type;

	/**
	 * Basically a toString()-method of the type-variable.
	 * 
	 * @param p_type
	 *            The type-variable of this class/object.
	 * @return The type as String or empty String if parameter type has illegal
	 *         value.
	 */
	public static String typeToString(int p_type) {
		if (p_type > TYPE_VIDEO || p_type < TYPE_TEXT)
			return "";
		return typesAsString[p_type];
	}

	/**
	 * Returns the extension of files of this type. Format is ".***" like
	 * ".jpg".
	 * 
	 * @param p_type
	 *            The type-variable of this class/object.
	 * @return The extension String or empty String if parameter type has
	 *         illegal value.
	 */
	public static String typeToExtension(int p_type) {
		if (p_type > TYPE_VIDEO || p_type < TYPE_TEXT)
			return "";
		return extensions[p_type];
	}

	/**
	 * Given a filename this method retrieves the type of the medium.
	 * 
	 * @param filename
	 *            The filename of the medium.
	 * @return The type of the medium coded using the constants as integer,
	 *         returns -1 if type of medium is not known or understood by
	 *         TraceBook.
	 */
	public static int getTypeFromFilename(String filename) {
		if (filename.endsWith(typeToExtension(TYPE_TEXT))) {
			return TYPE_TEXT;
		}
		if (filename.endsWith(typeToExtension(TYPE_AUDIO))) {
			return TYPE_AUDIO;
		}
		if (filename.endsWith(typeToExtension(TYPE_VIDEO))) {
			return TYPE_VIDEO;
		}
		if (filename.endsWith(typeToExtension(TYPE_PICTURE))) {
			return TYPE_PICTURE;
		}
		return -1;
	}

	/**
	 * Constructor that initialises the medium.
	 * 
	 * @param path
	 *            path to the file (basename)
	 * @param name
	 *            name of the medium (filename).
	 */
	public DataMedia(String path, String name) {
		super();
		this.id = DataStorage.getInstance().getID();
		this.path = path;
		this.name = name;
		this.type = getTypeFromFilename(name);
	}

	/**
	 * Getter-method.
	 * 
	 * @return The type of the medium
	 */
	public int getType() {
		return type;
	}

	/**
	 * Setter-method.
	 * 
	 * @param type The new type of this medium
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * Getter-method.
	 * 
	 * @return The unique id of the medium.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Getter-method. Returns path to the directory the medium is in.
	 * 
	 * @return The path to the medium on the devices medium.
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Getter-method. The returned String is enough to open the file.
	 * 
	 * @return The path to the medium on the devices medium.
	 */
	public String getFullPath() {
		return path + name;
	}

	/**
	 * Setter-method. The path should normally not be changed but one never
	 * knows.
	 * 
	 * @param path
	 *            The new path of the medium
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Getter-method.
	 * 
	 * @return The name of the medium as it is displayed.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter-method. Changing the name may have no impact on serialisation. On
	 * next deserialisation the old name may appear again.
	 * 
	 * @param newname
	 *            The new name for the medium.
	 */
	public void setName(String newname) {
		File oldfile = new File(getFullPath());
		File newfile = new File(getPath() + newname);
		boolean success = oldfile.renameTo(newfile);
		if (!success) {
			Log.e("MediaRenaming", "Could not rename medium.");
		} else {
			this.name = newname;
		}
	}

	/**
	 * This method loads a medium reference from the devices memory.
	 * 
	 * @param path
	 *            The complete path to the medium.
	 * @return The deserialised DataMedia object or null if medium doesn't
	 *         exist.
	 */
	public static DataMedia deserialise(String path) {
		File medium = new File(path);
		if (medium.exists()) {
			return new DataMedia(medium.getParent(), medium.getName());
		}
		Log.w("Media", "Medium was not found. Was trying to load a medium.");
		return null;
	}

	/**
	 * Deletes a medium on the devices memory. Note: Make sure that there is no
	 * reference to this medium anymore.
	 */
	public void delete() {
		File medium = new File(getFullPath());
		if (medium.isFile()) {
			if (!medium.delete()) {
				Log.w("Media", "Could not delete medium");
			}
		}
	}

	/**
	 * Give a pathname of a track (like .../TraceBook/TrackName) this method
	 * retrieves a list of all media in that directory. If the folder does not
	 * exist an empty list is returned.
	 * 
	 * @param dirpath
	 *            The path of the folder.
	 * @return A list of all Media in that folder.
	 */
	public List<DataMedia> listAllMedia(String dirpath) {
		File dir = new File(dirpath);
		List<DataMedia> ret = new LinkedList<DataMedia>();

		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (File f : files) {
				if (getTypeFromFilename(f.getPath()) != -1) {
					ret.add(DataMedia.deserialise(f.getPath()));
				}
			}
		} else {
			Log.w("Media", "Given pathname is no diretory.");
		}

		return ret;
	}

	/**
	 * Generates a <link>-tag for this medium.
	 * 
	 * @param serializer
	 *            The initialised XmlSerialiser
	 */
	public void serialise(XmlSerializer serializer) {
		try {
			serializer.startTag(null, "link");
			// serializer.attribute(null, "type", typeToString(type)); not used
			serializer.attribute(null, "href", name);
			serializer.endTag(null, "link");
		} catch (IllegalArgumentException e) {
			Log.e("MediaSerialisation", "Should not happen");
		} catch (IllegalStateException e) {
			Log.e("MediaSerialisation", "Illegal state");
		} catch (IOException e) {
			Log.e("MediaSerialisation", "Could not serialise medium " + name);
		}
	}
}
