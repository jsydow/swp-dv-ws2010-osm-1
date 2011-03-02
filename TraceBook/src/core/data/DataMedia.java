package core.data;

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
	 * The internal id for this medium.
	 */
	private int _id;
	/**
	 * The path to the file of the medium on the memory. This path should be
	 * sufficient to open the file (contains basename + filename).
	 */
	private String path;
	/**
	 * This name is the displayed name. May be equal to the filename without
	 * extension.
	 */
	private String name;

	/**
	 * Constructor that initialises the medium.
	 * 
	 * @param path
	 *            full path to the file (basename + filename + extension)
	 * @param name
	 *            name of the medium.
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
	DataMedia() {
		// nothing to do
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
	 * Getter-method
	 * 
	 * @return The path to the medium on the devices medium.
	 */
	public String getPath() {
		return path;
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
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * This method loads a medium reference from the devices memory. It uses the
	 * appropriate ContentProvider. Note: Currently a stub. Note:
	 * 
	 * @param name
	 *            The filename without extension of the medium.
	 * @return The deserialised DataMedia object.
	 */
	static DataMedia deserialise(String name) {
		/* TODO STUB */
		return null;
	}

	/**
	 * Deletes a medium on the devices memory. Uses the appropriate
	 * ContentProvider Note: Currently a stub.
	 */
	void delete() {
		/* TODO */
	}

}
