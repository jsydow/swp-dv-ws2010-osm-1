package core.data.db;

import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * @author js
 * 
 */
public class TagDb {

    private Context context;
    private SQLiteDatabase db;

    /**
     * @param context
     *            A context, probably the activity that uses the db.
     */
    TagDb(Context context) {
        super();
        this.context = context;
        openDb();
    }

    private void openDb() {
        TagDbOpenhelper helper = new TagDbOpenhelper(context);
        db = helper.getReadableDatabase();
    }

    /**
     * @param searchText
     *            The text to search for.
     * @return The
     */
    public List<TagSearchResult> getTag(String searchText) {
        if (db == null) {
            openDb();
        }

        if (db != null) {
            List<TagSearchResult> tags = new Vector<TagSearchResult>();

            fillTagListWithSearchResults(searchText, tags);

            return tags;
        } else {
            Log.e("TagDataBase", "Could not open Database.");
            return null;
        }
    }

    private void fillTagListWithSearchResults(String searchText,
            List<TagSearchResult> tags) {

        Cursor result = db.query(TagDbOpenhelper.getTableName(),
                TagDbOpenhelper.getColumns(), TagDbOpenhelper.getSelection(),
                new String[] { searchText, searchText, searchText }, null,
                null, null);

        result.moveToFirst();
        while (result.isAfterLast()) {
            // insert row to tags list
            tags.add(TagDbOpenhelper.getResultFromCursor(result));

            result.moveToNext();
        }
        result.close();
    }
}
