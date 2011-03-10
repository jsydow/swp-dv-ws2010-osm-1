package gui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class GenericAdapter extends ArrayAdapter<GenericAdapterData> {
	
	int layoutId;
	LayoutInflater layoutInflater;
	List<GenericAdapterData> dataList;
	
	
	public GenericAdapter(Context context, int resource,
			int textViewResourceId, List<GenericAdapterData> objects,LayoutInflater layInf) {
		super(context, resource, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		layoutId 		= resource;
		layoutInflater 	= layInf;
		dataList		= objects;
	}
	
	 @Override
     public View getView(int position, View convertView, ViewGroup parent) {
		 
		 View genView		 		= layoutInflater.inflate(layoutId, null);
		 GenericAdapterData	data	= dataList.get(position);
		 data.fillView(genView);
		 
		 return genView;
	 }

}
