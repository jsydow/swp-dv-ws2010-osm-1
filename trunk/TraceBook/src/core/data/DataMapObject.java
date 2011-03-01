package core.data;

import java.util.HashMap;
import java.util.Map;

public abstract class DataMapObject {
	int _id;
	Map<String,String> tags;
	
	DataMapObject(int id)
	{
		tags = new HashMap<String,String>();
		_id = id;
	}
}
