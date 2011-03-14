package gui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * The GenericAdapter class helps us it reduce the number of customizable
 * ArrayAdapter classes. You can use it for all of you custom ListView layouts.
 * It needs a layoutInflater and a reference to a list of GenericAdapterData.
 * Each element in the list describes one element in a list view.
 * 
 * @author SakirSoft
 * 
 */
public class GenericAdapter extends ArrayAdapter<GenericAdapterData> {

    /**
     * The layout id where the view elements are stored for on custom item in
     * the list view.
     */
    int layoutId;

    /**
     * A reference to a layoutInflater which will be used to inflate the custom
     * view for an item in the list view.
     * 
     */
    LayoutInflater layoutInflater;

    /**
     * A reference to a list of GenericAdapterData which will be used to fill
     * the list.
     * 
     */
    List<GenericAdapterData> dataList;

    /**
     * The standard constructor for an ArrayAdapter plus a reference to a
     * LayoutInflater.
     * 
     * @param context
     * @param resource
     * @param textViewResourceId
     * @param objects
     * @param layInf
     */
    public GenericAdapter(Context context, int resource,
            int textViewResourceId, List<GenericAdapterData> objects,
            LayoutInflater layInf) {
        super(context, resource, textViewResourceId, objects);
        // TODO Auto-generated constructor stub
        layoutId = resource;
        layoutInflater = layInf;
        dataList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View genView = layoutInflater.inflate(layoutId, null);
        GenericAdapterData data = dataList.get(position);
        data.fillView(genView);

        return genView;
    }

}
