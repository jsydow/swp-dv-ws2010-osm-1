package core.data;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class DataStorage implements SerialisableContent {
	
	private List<DataTrack> tracks;
	
	/**
	 * Returns a list of the names of all tracks that are currently
	 * stored in this DataStorage object. The names can be used as
	 * argument to getTrack()
	 * @return List of the names of all tracks. If there are no tracks stored then the list will be empty.
	 */
	List<String> getAllTracks(){
		List<String> ret = new LinkedList<String>();
		for(DataTrack dt : tracks) {
			ret.add(dt.name);
		}
		return ret;
	}
	
	/**
	 * This method returns a Track object that specified by a name. Note that
	 * only tracks that the DataStorage currently stores can be returned.
	 * @param name The name of a track as returned by getAllTracks()
	 * @return If such a track exists the Track itself is returned. If the track does not exist however null is returned.
	 */
	DataTrack getTrack(String name){
		for(DataTrack dt : tracks) {
			if( dt.name.equals(name) ) {
				return dt;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param name The name of a track as stored in the Track object or as returned by getAllTracks()
	 */
	void deleteTrack(String name) {
		ListIterator<DataTrack> lit = tracks.listIterator();
		DataTrack dt;
		while(lit.hasNext()) {
			dt = lit.next();
			if( dt.name.equals(name) ) {
				dt.delete();
				lit.remove();
				break;
			}
		}
	}
	
	DataTrack newTrack(){
		DataTrack dt = new DataTrack(null);
		tracks.add(dt);
		return dt;
	}
	
	void deserialiseAll() {
		// TODO 
	}

	public void serialise() {
		for(DataTrack dt : tracks)
			dt.serialise();		
	}

	public void delete() {
		for(DataTrack dt : tracks)
			dt.delete();
		tracks.clear();
	}

}
