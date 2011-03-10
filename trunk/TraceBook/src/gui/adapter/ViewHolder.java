package gui.adapter;

import Trace.Book.R;
import android.view.View;
import android.widget.TextView;

public class ViewHolder {

    /**
     * We need a ViewHolder to get easily the views out of the used layout.
     * 
     * @author greenTraxas
     * 
     */
    private View mRow;
    private TextView title = null;
    private TextView detail = null;

    public ViewHolder(View row) {
        mRow = row;
    }

    public TextView gettitle() {
        if (null == title) {
            title = (TextView) mRow.findViewById(R.id.title);
        }
        return title;
    }

    public TextView getdetail() {
        if (null == detail) {
            detail = (TextView) mRow.findViewById(R.id.detail);
        }
        return detail;
    }
}
