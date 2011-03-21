package tracebook.core.data.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * The DB open helper automatically creates or recreates the database. Use it to
 * get a SQLiteDataBase-object.
 */
public class TagDbOpenHelper extends SQLiteOpenHelper {

    /**
     * By incrementing this number, the database is reseted. Never decrease!
     */
    private static final int DATABASE_VERSION = 24;
    /**
     * The name of the database used by TraceBook.
     */
    private static final String DATABASE_NAME = "db_tracebook_tags";
    /**
     * Table that contains all information for tags.
     */
    private static final String DICT_TABLE_NAME = "dictionary";
    /**
     * Table that contains the history of used tags.
     */
    private static final String HISTORY_TABLE_NAME = "history";
    /**
     * String to create table.
     */
    private static final String DICT_TABLE_CREATE = "CREATE TABLE "
            + DICT_TABLE_NAME
            + " (key TEXT, value TEXT, name TEXT, description TEXT, wikilink TEXT, image TEXT, language TEXT, keywords TEXT, value_type TEXT);";
    /**
     * String to create table.
     */
    private static final String HISTORY_TABLE_CREATE = "CREATE TABLE "
            + HISTORY_TABLE_NAME
            + " (key TEXT, value TEXT, use_count INT, last_use INT);";
    /**
     * String to drop table.
     */
    private static final String DICT_TABLE_DROP = "DROP TABLE IF EXISTS "
            + DICT_TABLE_NAME;
    /**
     * String to drop table.
     */
    private static final String HISTORY_TABLE_DROP = "DROP TABLE IF EXISTS "
            + HISTORY_TABLE_NAME;

    /**
     * Returns the name of the table.
     * 
     * @return the tableName
     */
    static String getDictTableName() {
        return DICT_TABLE_NAME;
    }

    /**
     * Returns the name of the table.
     * 
     * @return the tableName
     */
    static String getHistoryTableName() {
        return HISTORY_TABLE_NAME;
    }

    /**
     * Returns a list of all columns for the dictionary table.
     * 
     * @return all columns
     */
    static String[] getDictColumns() {
        return new String[] { "key", "value", "name", "description",
                "wikilink", "image", "language", "value_type" };
    }

    /**
     * Returns a list of key and value columns.
     * 
     * @return key and value
     */
    static String[] getTagColumns() {
        return new String[] { "key", "value" };
    }

    /**
     * Creates a TagSearchResult object out of a cursor position.
     * 
     * @param result
     *            The initialised cursor.
     * @return The result object of the row.
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
     * Default constructor.
     * 
     * @param context
     *            A Context, probably the Activity that starts the SQL
     *            connection
     */
    TagDbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DICT_TABLE_DROP);
        db.execSQL(HISTORY_TABLE_DROP);
        db.execSQL(DICT_TABLE_CREATE);
        db.execSQL(HISTORY_TABLE_CREATE);

        createIndex(db, "language_idx", DICT_TABLE_NAME, "language");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DICT_TABLE_DROP);
        db.execSQL(HISTORY_TABLE_DROP);
        onCreate(db);
    }

    /**
     * Creates an index on the given table, if it does not exist yet.
     * 
     * @param db
     *            SQLite database object.
     * @param idx
     *            Name of the index to be created.
     * @param table
     *            Table to create the index on.
     * @param columns
     *            Columns to be included in the index. Order matters.
     */
    private void createIndex(SQLiteDatabase db, String idx, String table,
            String columns) {
        db.execSQL(String.format("CREATE INDEX IF NOT EXISTS %s ON %s (%s);",
                idx, table, columns));
    }
}
