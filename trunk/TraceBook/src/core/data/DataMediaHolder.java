package core.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.xmlpull.v1.XmlSerializer;

/**
 * Any object that can have media attached. Media can be added and retrieved.
 * This class is abstract.
 * 
 * @author js
 * 
 */
public abstract class DataMediaHolder {
	/**
	 * The list of Media.
	 */
	protected List<DataMedia> media;

	/**
	 * default constructor
	 */
	public DataMediaHolder() {
		media = new LinkedList<DataMedia>();
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		this.datetime = sdf.format(new Date());
	}

	/**
	 * The Creation time.
	 */
	private String datetime;

	/**
	 * Getter-method that returns a list of all media. The returned List is the
	 * one stored in the MediaHolder. Changing the returned List will therefore
	 * change this list.
	 * 
	 * @return The list of all media
	 */
	public List<DataMedia> getMedia() {
		return media;
	}

	/**
	 * Getter-method. The creation time string.
	 * 
	 * @return The creation time of this object as String.
	 */
	public String getDatetime() {
		return datetime;
	}

	/**
	 * Add a new medium to this object.
	 * 
	 * @param medium
	 *            The Media object.
	 */
	public void addMedia(DataMedia medium) {
		media.add(medium);
	}

	/**
	 * This method generates the media-tags (<link>) for a
	 * DataMediaHolder-object. The enclosing tag must be opened.
	 * 
	 * @param serializer
	 *            An XmlSerializer that is initialised.
	 */
	public void serialiseMedia(XmlSerializer serializer) {
		for (DataMedia m : media) {
			m.serialise(serializer);
		}
	}

	/**
	 * Deletes a medium from the working memory (i.e. that object) and the
	 * devices memory. Warning: Make sure no other object has a reference to
	 * this medium!
	 * 
	 * @param id
	 *            The id of the medium to be deleted.
	 */
	public void deleteMedia(int id) {
		ListIterator<DataMedia> lit = media.listIterator();
		DataMedia dm;
		while (lit.hasNext()) {
			dm = lit.next();
			if (dm.get_id() == id) {
				dm.delete();
				lit.remove();
				break;
			}
		}
	}
}
