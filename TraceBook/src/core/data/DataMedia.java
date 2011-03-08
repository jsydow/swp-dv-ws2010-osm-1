package core.data;

import java.io.File;
import java.io.IOException;

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
	 * Media type constants.
	 */
	public static final int TYPE_VIDEO = 3;
	public static final int TYPE_AUDIO = 2;
	public static final int TYPE_TEXT = 0;
	public static final int TYPE_PICTURE = 1;
	private static String[] typesAsString = { "text", "picture", "audio",
			"video" };

	/**
	 * The internal id for this medium.
	 */
	private int _id;

	/**
	 * The path to the file of the medium on the memory. This path+name should be
	 * sufficient to open the file. Path is therefore the base name.
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
	 * @param type
	 *            The type-variable of this class/object.
	 * @return The type as String
	 */
	public String typeToString(int p_type) {
		if (p_type > TYPE_VIDEO || p_type < TYPE_TEXT)
			return "";
		return typesAsString[p_type];
	}

	/**
	 * Constructor that initialises the medium.
	 * 
	 * @param path
	 *             path to the file (basename)
	 * @param name
	 *            name of the medium (filename).
	 */
	public DataMedia(String path, String name) {
		super();
		this._id = DataStorage.getInstance().getID();
		this.path = path;
		this.name = name;
	}

	/**
	 * default constructor
	 */
	public DataMedia() {
		// nothing to do
	}

	/**
	 * Getter-method
	 * 
	 * @return The type of the medium
	 */
	public int getType() {
		return type;
	}

	/**
	 * Setter-method
	 * 
	 * @param The
	 *            new type of this medium
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * Getter-method
	 * 
	 * @return The unique id of the medium.
	 */
	public int get_id() {
		return _id;
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
		return path+name;
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
	 * @param name
	 *            The new name for the medium.
	 */
	public void setName(String newname) {
		File oldfile = new File(getFullPath());
		File newfile = new File(getPath()+newname);
		boolean success = oldfile.renameTo(newfile);
		if (!success) {
			Log.e("MediaRenaming", "Could not rename medium.");
		}
		else {
			this.name = newname;
		}
	}

	/**
	 * This method loads a medium reference from the devices memory. It uses the
	 * appropriate ContentProvider. Note: Currently a stub. Note:
	 * 
	 * @param name
	 *            The filename without extension of the medium.
	 * @return The deserialised DataMedia object.
	 */
	public static DataMedia deserialise(String name) {
		/* TODO STUB */
		return null;
	}

	/**
	 * Deletes a medium on the devices memory. Uses the appropriate
	 * ContentProvider Note: Currently a stub.
	 */
	public void delete() {
		/* TODO */
	}

	/**
	 * Generates a <link>-tag for this medium.
	 * 
	 * @param serializer
	 */
	public void serialise(XmlSerializer serializer) {
		try {
			serializer.startTag(null, "link");
			serializer.attribute(null, "type", typeToString(type));
			serializer.attribute(null, "value", name);
			serializer.endTag(null, "tag");
		} catch (IllegalArgumentException e) {
			Log.e("MediaSerialisation", "Should not happen");
		} catch (IllegalStateException e) {
			Log.e("MediaSerialisation", "Illegal state");
		} catch (IOException e) {
			Log.e("MediaSerialisation", "Could not serialise medium " + name);
		}
	}
}
