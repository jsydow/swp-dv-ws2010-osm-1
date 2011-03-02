package core.data;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Any object that can have media attached. Media can be added and retrieved.
 * This class is abstract.
 * 
 * @author js
 * 
 */
public abstract class DataMediaHolder {
	/**
	 * The list of Media
	 */
	protected List<DataMedia> media;

	/**
	 * default constructor
	 */
	DataMediaHolder() {
		media = new LinkedList<DataMedia>();
	}

	/**
	 * Getter-method that returns a list of all media. The returned List is the
	 * one stored in the MediaHolder. Changing the returned List will therefore
	 * change this list
	 * 
	 * @return the list of all media
	 */
	public List<DataMedia> getMedia() {
		return media;
	}

	/**
	 * Add a new medium to this object.
	 * 
	 * @param medium
	 *            the Media object
	 */
	public void addMedia(DataMedia medium) {
		media.add(medium);
	}

	/**
	 * Deletes a medium from the working memory (i.e. that object) and the
	 * devices memory. Warning: Make sure no other object has a reference to
	 * this medium!
	 * 
	 * @param medium
	 */
	public void deleteMedia(DataMedia medium) {
		ListIterator<DataMedia> lit = media.listIterator();
		DataMedia dm;
		while (lit.hasNext()) {
			dm = lit.next();
			if (dm.get_id() == medium.get_id()) {
				dm.delete();
				lit.remove();
				break;
			}
		}
	}
}
