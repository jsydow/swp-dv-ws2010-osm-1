package gui.adapter;

public class RowData {

    /**
     * This class save our RowData for the CustomAdapter. We save the title
     * (Category) and the detail (Value). Later we can save although imageIds,
     * to display images for each entry at our ListView
     */
    protected int mId;
    protected String mTitle;
    protected String mDetail;

    public RowData(int id, String title, String detail) {
        mId = id;
        mTitle = title;
        mDetail = detail;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmDetail() {
        return mDetail;
    }

    public int getmId() {
        return mId;
    }

    /**
     * The Method build a String out of our RowData Useful for Toast or other
     * notification's.
     */
    public String toString() {
        return mId + " " + mTitle + " " + mDetail;
    }
}
