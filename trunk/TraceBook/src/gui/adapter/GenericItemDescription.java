package gui.adapter;

import java.util.HashMap;
import java.util.Map;

public class GenericItemDescription {
    Map<String, Integer> resourceIds = new HashMap<String, Integer>();

    public void addResourceId(String tag, int id) {
        resourceIds.put(tag, id);
    }

    public int getResourceId(String tag) {
        return resourceIds.get(tag);
    }
}
