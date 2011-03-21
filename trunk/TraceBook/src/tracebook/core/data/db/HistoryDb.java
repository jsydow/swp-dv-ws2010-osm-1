package tracebook.core.data.db;

import java.util.List;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Provide comfortable access to the history database.
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

    /**
     * Establishes read only access to the database.
     */
    private void openDb() {
        if (db != null && db.isOpen()) {
            db.close();
        }

        db = helper.getReadableDatabase();
    }

    /**
     * Returns the TagDbOpenHelper object. Should not be used.
     * 
     * @return The {@link TagDbOpenHelper} variable.
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

    /**
     * Selfexplaining. Fills a given tag list with tag that are retrieved from
     * the database.
     * 
     * @param tags
     *            The list that should be filled.
     * @param mostUsed
     *            Ordering: most used is first in list, or recently used is
     *            first. True means most used tag will be first.
     * @param length
     *            The number of tags that are returned.
     */
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
                    new String[] { "COUNT(*)" }, "key='" + key
                            + "' AND value='" + value + "'", null, null, null,
                    null);

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
     * If a tag is used by the user. This method should be called so that the
     * database is updated.
     * 
     * @param key
     *            The key of the tag.
     * @param value
     *            The value of the tag.
     */
    public void updateTag(String key, String value) {

        if (rowsCountWithTag(key, value) > 0) {
            SQLiteDatabase wdb = helper.getWritableDatabase();
            if (wdb != null && wdb.isOpen()) {
                wdb.execSQL("UPDATE " + TagDbOpenHelper.getHistoryTableName()
                        + " SET use_count=use_count+1, last_use="
                        + System.currentTimeMillis() + " WHERE key='" + key
                        + "' AND value='" + value + "'");
                wdb.close();
            } else {
                Log.e("HistoryDb", "Could not open database to write.");
            }
        } else {
            SQLiteDatabase wdb = helper.getWritableDatabase();
            if (wdb != null && wdb.isOpen()) {
                ContentValues values = new ContentValues();
                values.put("last_use", Long.valueOf(System.currentTimeMillis()));
                values.put("key", key);
                values.put("value", value);
                values.put("use_count", Integer.valueOf(1));
                wdb.insert(TagDbOpenHelper.getHistoryTableName(), null, values);
                wdb.close();
            } else {
                Log.e("HistoryDb", "Could not open database to write.");
            }
        }
        return;
    }
}
