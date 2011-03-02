package core.data;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public abstract class DataMediaHolder {
	protected List<DataMedia> media;
	
	DataMediaHolder() {
		media = new LinkedList<DataMedia>();
	}

	/**
	 * @return the media
	 */
	public List<DataMedia> getMedia() {
		return media;
	}

	public void addMedia(DataMedia medium) {
		media.add(medium);
	 }
	 
	 public void deleteMedia(DataMedia medium) {
		 ListIterator<DataMedia> lit = media.listIterator();
		 DataMedia dm;
		 while(lit.hasNext()) {
			 dm = lit.next();
			 if( dm.get_id() == medium.get_id() ) {
				 dm.delete();
				 lit.remove();
				 break;
			 }
		 }
	 }
}
