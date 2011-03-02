package core.data;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * WayPointList objects are any objects that consist of a series of nodes
 * like Areas and Ways. 
 * @author js
 *
 */
public class DataPointsList extends DataMapObject implements SerialisableContent{
	/**
	 * The list of nodes of this object. First node is first element
	 * in this list. 
	 */
	protected List<DataNode> nodes;
	
	protected boolean isArea;
	
	/**
	 * 
	 * @param id the internal id of this object
	 */
	 DataPointsList() {
		super();
		nodes = new LinkedList<DataNode>();
	}
	 
	 DataPointsList(boolean isArea) {
			this();
			this.isArea = isArea;
		}

	/**
	 * @return the isArea
	 */
	public boolean isArea() {
		return isArea;
	}

	/**
	 * @param isArea the isArea to set
	 */
	public void setArea(boolean isArea) {
		this.isArea = isArea;
	}

	public List<DataNode> getNodes() {
		return nodes;
	}
	 
	 public DataNode newNode() {
		 DataNode dn = new DataNode();
		 nodes.add(dn);
		 return dn;
	 }
	 
	 public void deleteNode(int id) {
		 ListIterator<DataNode> lit = nodes.listIterator();
		 DataNode dn;
		 while(lit.hasNext()) {
			 dn = lit.next();
			 if( dn.get_id() == id ) {
				 dn.delete();
				 lit.remove();
				 break;
			 }
		 }
	 }

	 static DataPointsList deserialise(int id) {
		/* TODO STUB */
		return null;
	}
	 
	public void serialise() {
		// TODO Auto-generated method stub
		
	}

	public void delete() {
		// TODO Auto-generated method stub
		
	} 
}
