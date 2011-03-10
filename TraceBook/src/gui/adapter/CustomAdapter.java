package gui.adapter;

import java.util.List;

import Trace.Book.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<RowData> {

    private LayoutInflater mInflater;

    public CustomAdapter(Context context, int resource, int textViewResourceId,
            List<RowData> objects, LayoutInflater inflater) {
        super(context, resource, textViewResourceId, objects);
        this.mInflater = inflater;
    }

    /**
     * For every View we used in our CustomAdapter (addpointlistview), we set
     * the text in this method. The text is comming out of the RowData at the
     * position of the adapter.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        TextView title = null;
        TextView detail = null;

        RowData rowData = getItem(position);
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.addpointlistview, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        title = holder.gettitle();
        title.setText(rowData.mTitle);
        detail = holder.getdetail();
        detail.setText(rowData.mDetail);
        return convertView;
    }

}
