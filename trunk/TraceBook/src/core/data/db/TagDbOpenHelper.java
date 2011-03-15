package core.data.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author js
 * 
 */
public class TagDbOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "db_tracebook_tags";
    private static final String TABLE_NAME = "dictionary";
    private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME
            + " (" + "key" + " TEXT, " + "value" + " TEXT, " + "name"
            + " TEXT, " + "description" + " TEXT, " + "wikilink" + " TEXT, "
            + "image" + " TEXT, " + "language" + " TEXT, " + "keywords"
            + " TEXT, " + "value_type" + " TEXT" + ");";
    private static final String TABLE_DROP = "DROP TABLE IF EXISTS "
            + DATABASE_NAME + "." + TABLE_NAME;

    /**
     * @return the tableName
     */
    static String getTableName() {
        return TABLE_NAME;
    }

    /**
     * @return all columns
     */
    static String[] getColumns() {
        return new String[] { "key", "value", "name", "description",
                "wikilink", "image", "language", "value_type" };
    }

    /**
     * @return The selection String for the query-call of the SQLiteDatabase.
     */
    static String getSelection() {
        return " (name LIKE ? OR keywords LIKE ? OR description LIKE ?) AND language EQ ?";
    }

    /**
     * @param result
     *            The initialised cursor.
     * @return The result object of the row
     */
    static TagSearchResult getResultFromCursor(Cursor result) {
        String name = result.getString(result.getColumnIndex("name"));
        String key = result.getString(result.getColumnIndex("key"));
        String value = result.getString(result.getColumnIndex("value"));
        String description = result.getString(result
                .getColumnIndex("description"));
        String wikilink = result.getString(result.getColumnIndex("wikilink"));
        String image = result.getString(result.getColumnIndex("image"));
        String language = result.getString(result.getColumnIndex("language"));
        String valueType = result
                .getString(result.getColumnIndex("value_type"));

        return new TagSearchResult(key, value, name, description, wikilink,
                image, language, valueType);
    }

    /**
     * @param context
     *            A Context, probably the Activity that starts the SQL
     *            connection
     */
    TagDbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TABLE_DROP);
        onCreate(db);
    }
}
