package gui.adapter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author SakirSoft
 * 
 */
public class GenericItemDescription {
    /**
     * Map to associate a String tag with an id.
     * 
     */
    Map<String, Integer> resourceIds = new HashMap<String, Integer>();

    /**
     * @param tag
     * @param id
     */
    public void addResourceId(String tag, int id) {
        resourceIds.put(tag, Integer.valueOf(id));
    }

    /**
     * @param tag
     * @return return the id of a resource which is associated with a given tag
     */
    public int getResourceId(String tag) {
        return resourceIds.get(tag).intValue();
    }
}
