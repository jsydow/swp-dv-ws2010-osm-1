package gui.adapter;

import java.util.HashMap;
import java.util.Map;

/**
 * This class defines and handles the mapping of string tags with view ressource
 * ids. A reference of this class have to be connected to each instance of a
 * GenericAdapterData. So each item in a ListView can associate its view
 * elements.
 */
public class GenericItemDescription {
    /**
     * Map to associate a String tag with an id.
     * 
     */
    Map<String, Integer> resourceIds = new HashMap<String, Integer>();

    private String itemNameTag = null;

    /**
     * Sets the name of the tag that is used for the toString() method of the
     * AdapterData.
     * 
     * @param tag
     *            The name of the tag.
     */
    public void setNameTag(String tag) {
        itemNameTag = tag;
    }

    /**
     * Returns the item tag that is used for toString() in the AdapterData.
     * 
     * @return The name of the tag that names the data.
     */
    public String getNameTag() {
        return itemNameTag;
    }

    /**
     * @param tag
     *            tag to associate the resource id
     * @param id
     *            the resource id
     */
    public void addResourceId(String tag, int id) {
        resourceIds.put(tag, Integer.valueOf(id));
    }

    /**
     * @param tag
     *            tag which is associated with a given resources id.
     * @return return the id of a resource which is associated with a given tag
     */
    public int getResourceId(String tag) {
        return resourceIds.get(tag).intValue();
    }
}
