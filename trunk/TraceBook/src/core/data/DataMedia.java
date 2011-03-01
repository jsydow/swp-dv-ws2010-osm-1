package core.data;

/**
 * This is an object that refers to a medium. The medium itself is stored on the
 * background memory of the device. Only the name and path of the actual medium
 * is stored in this object.
 * @author js
 *
 */
class DataMedia {
	/**
	 * internal id
	 */
	int _id;
	/**
	 * the path to the file of the medium on the memory. This path should be
	 * sufficient to open the file (contains basename + filename).
	 */
	String path;
	/**
	 * The name is the displayed name. May be equal to the filename without
	 * extension.
	 */
	String name;
	
	/**
	 * 
	 * @param _id the internal id.
	 * @param path full path to the file (basename + filename + extension)
	 * @param name name of the medium.
	 */
	DataMedia(int _id, String path, String name) {
		super();
		this._id = _id;
		this.path = path;
		this.name = name;
	}

	/**
	 * default constructor
	 */
	DataMedia() {
	}
	
}
