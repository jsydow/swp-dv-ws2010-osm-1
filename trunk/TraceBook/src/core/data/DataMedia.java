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
		// nothing to do
	}
	
	/**
	 * This method loads a Media reference from the devices memory. It uses the 
	 * appropriate ContentProvider.
	 * Note: Currently a stub.
	 * Note: The parameter id may change if another name is better suited
	 * for retrieving the Media correctly.
	 *  
	 * @param name The name of the Media. It is not clear yet if what id or name is needed to load the Media correctly. 
	 * @return The deserialised DataMedia object.
	 */
	static DataMedia deserialise(String name){
		/* TODO STUB */
		return null;
	}
	
	void delete() {
		/* TODO */
	}
	
}
