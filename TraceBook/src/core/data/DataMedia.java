package core.data;

/**
 * This is an object that refers to a medium. The medium itself is stored on the
 * background memory of the device. Only the name and path of the actual medium
 * is stored in this object.
 * @author js
 *
 */
public class DataMedia {
	/**
	 * internal id
	 */
	private int _id;
	/**
	 * the path to the file of the medium on the memory. This path should be
	 * sufficient to open the file (contains basename + filename).
	 */
	private String path;
	/**
	 * The name is the displayed name. May be equal to the filename without
	 * extension.
	 */
	private String name;

	/**
	 * 
	 * @param path full path to the file (basename + filename + extension)
	 * @param name name of the medium.
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
	 * @return the id
	 */
	public int get_id() {
		return _id;
	}
	
	/**
	 * Getter-method
	 * @return the path to the medium
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Setter-method. The path shouldn't be changed but one never knows.
	 * @param path new path
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Getter-method
	 * @return the name of the medium
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter-method
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * This method loads a medium reference from the devices memory. It uses the 
	 * appropriate ContentProvider.
	 * Note: Currently a stub.
	 * Note: The parameter id may change if another name is better suited
	 * for retrieving the medium correctly.
	 *  
	 * @param name The name of the medium. It is not clear yet if what id or name is needed to load the Media correctly. 
	 * @return The deserialised DataMedia object.
	 */
	static DataMedia deserialise(String name){
		/* TODO STUB */
		return null;
	}
	
	/**
	 * Deletes a medium on the devices memory. Uses the appropriate ContentProvider
	 * Note: Currently a stub. 
	 */
	void delete() {
		/* TODO */
	}
	
}
