package core.data.db;

import java.util.List;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * @author js
 * 
 */
public class HistoryDb {
    private TagDbOpenHelper helper;
    private SQLiteDatabase db;

    /**
     * Constructor, opens the database.
     * 
     * @param context
     *            A context, probably the activity that uses the database.
     */
    public HistoryDb(Context context) {
        super();
        helper = new TagDbOpenHelper(context);
    }

    private void openDb() {
        if (db != null && db.isOpen()) {
            db.close();
        }

        db = helper.getReadableDatabase();
    }

    /**
     * @return the {@link TagDbOpenHelper} variable
     */
    TagDbOpenHelper getHelper() {
        return helper;
    }

    /**
     * Closes the database. Dot not forget to call this method!
     */
    private void closeDb() {
        db.close();
    }

    /**
     * Retrieves the history of used tags.
     * 
     * @param mostUsed
     *            If resulting list should be ordered by most used tags or
     *            recently used ones.
     * @param length
     *            The length of the returned list.
     * @return The history as list. Only key and value fields of the
     *         TagSearchResult are used.
     */
    public List<TagSearchResult> getHistory(boolean mostUsed, int length) {
        openDb();

        if (db != null && db.isOpen()) {
            List<TagSearchResult> tags = new Vector<TagSearchResult>();

            fillTagListWithHistoryResults(tags, mostUsed, length);

            closeDb();
            return tags;
        } else {
            Log.e("TagDataBase", "Could not open Database.");
            return null;
        }
    }

    private void fillTagListWithHistoryResults(List<TagSearchResult> tags,
            boolean mostUsed, int length) {
        String orderBy = null;
        if (mostUsed) {
            orderBy = "use_count DESC";
        } else {
            orderBy = "last_use DESC";
        }

        Cursor result = db.query(TagDbOpenHelper.getHistoryTableName(),
                TagDbOpenHelper.getTagColumns(), null, null, null, null,
                orderBy, Integer.valueOf(length).toString());

        if (result.moveToFirst()) {
            while (!result.isAfterLast()) {
                // insert row to tags list
                tags.add(new TagSearchResult(result.getString(result
                        .getColumnIndex("key")), result.getString(result
                        .getColumnIndex("value")), null, null, null, null,
                        null, null));

                result.moveToNext();
            }
        }
        result.close();

    }

    /**
     * Returns the number of rows that are in the database with a given tag.
     * 
     * @param key
     *            The value of the tag.
     * @param value
     *            The value of the tag.
     * @return The number of rows.
     */
    private int rowsCountWithTag(String key, String value) {
        int rowCount = 0;

        openDb();

        if (db != null && db.isOpen()) {
            Cursor crs = db.query(TagDbOpenHelper.getHistoryTableName(),
                    new String[] { "COUNT(*)" }, "key=" + key + " AND value="
                            + value, null, null, null, null);

            if (crs.moveToFirst()) {
                rowCount = crs.getInt(0);
            }
            crs.close();
            closeDb();
        } else {
            Log.e("TagDataBase", "Could not open Database.");
        }
        return rowCount;
    }

    /**
     * 
     * @param key
     *            The key of the tag.
     * @param value
     *            The value of the tag.
     */
    public void updateTag(String key, String value) {
        openDb();

        if (db != null && db.isOpen()) {
            if (rowsCountWithTag(key, value) > 0) {
                db.execSQL("UPDATE " + TagDbOpenHelper.getHistoryTableName()
                        + " SET use_count=use_count+1, last_use="
                        + System.currentTimeMillis() + " WHERE key=" + key
                        + " AND value=" + value);
            } else {
                ContentValues values = new ContentValues();
                values.put("last_use", Long.valueOf(System.currentTimeMillis()));
                values.put("key", key);
                values.put("value", value);
                values.put("use_count", Integer.valueOf(1));
                db.insert(TagDbOpenHelper.getHistoryTableName(), null, values);
            }

            closeDb();
        } else {
            Log.e("TagDataBase", "Could not open Database.");
        }
        return;
    }
}
