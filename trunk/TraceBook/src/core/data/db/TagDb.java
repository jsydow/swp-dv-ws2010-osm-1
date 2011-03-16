package core.data.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Xml;

/**
 * Provides access to the database containing all the tags with their
 * description.
 * 
 */
public class TagDb {

    private TagDbOpenHelper helper;
    private Context context;
    private SQLiteDatabase db;

    /**
     * Constructor, opens the database.
     * 
     * @param context
     *            A context, probably the activity that uses the database.
     */
    public TagDb(Context context) {
        super();
        this.context = context;
        openDb();
    }

    private void openDb() {
        helper = new TagDbOpenHelper(context);
        db = helper.getReadableDatabase();
    }

    /**
     * Closes the database. Dot not forget to call this method!
     */
    public void closeDb() {
        db.close();
    }

    /**
     * Searches the database for a text in its description, keywords and name.
     * 
     * @param searchText
     *            The text to search for.
     * @param language
     *            The language abbreviation as string like "de" or "en".
     * @return The list of search results.
     */
    public List<TagSearchResult> getTag(String searchText, String language) {
        if (db == null || !db.isOpen()) {
            openDb();
        }

        if (db != null && db.isOpen()) {
            List<TagSearchResult> tags = new Vector<TagSearchResult>();

            fillTagListWithSearchResults(searchText, language, tags);

            return tags;
        } else {
            Log.e("TagDataBase", "Could not open Database.");
            return null;
        }
    }

    private void fillTagListWithSearchResults(String searchText,
            String language, List<TagSearchResult> tags) {

        if (searchText.length() >= 2) {
            Cursor result = db.query(TagDbOpenHelper.getTableName(),
                    TagDbOpenHelper.getColumns(), "(name LIKE '%" + searchText
                            + "%' OR keywords LIKE '%" + searchText
                            + "%' OR description LIKE '%" + searchText
                            + "%') AND language LIKE '" + language + "'", null,
                    null, null, null, "20");

            if (result.moveToFirst()) {
                while (!result.isAfterLast()) {
                    // insert row to tags list
                    tags.add(TagDbOpenHelper.getResultFromCursor(result));

                    result.moveToNext();
                }
            }
            result.close();
        }
    }

    /**
     * @param language
     * @return
     */
    public int getRowCountForLanguage(String language) {
        int rowCount = -1;
        Cursor crs = db.query("dictionary", new String[] { "COUNT(*)" },
                "language LIKE ?", new String[] { language }, null, null, null);
        if (crs.getCount() > 0) {
            crs.moveToFirst();
            rowCount = crs.getInt(0);
        }
        crs.close();
        return rowCount;
    }

    /**
     * Loads an XML file into the database.
     * 
     * @param xmlFile
     *            The XML file to parse.
     */
    public void initDbWithFile(String xmlFile) {
        try {
            File file = new File(xmlFile);

            Xml.parse(new InputStreamReader(new FileInputStream(file)),
                    new DefaultHandler() {
                        SQLiteDatabase writeDb;

                        /*
                         * (non-Javadoc)
                         * 
                         * @see org.xml.sax.helpers.DefaultHandler#endDocument()
                         */
                        @Override
                        public void endDocument() throws SAXException {
                            writeDb.setTransactionSuccessful();
                            writeDb.endTransaction();
                            writeDb.close();
                            super.endDocument();
                        }

                        /*
                         * (non-Javadoc)
                         * 
                         * @see
                         * org.xml.sax.helpers.DefaultHandler#startDocument()
                         */
                        @Override
                        public void startDocument() throws SAXException {
                            writeDb = helper.getWritableDatabase();
                            writeDb.beginTransaction();
                            super.startDocument();
                        }

                        String language = null;
                        String key = null;
                        String value = null;
                        String link = null;
                        String description = null;
                        String type = null;
                        int depth = 0;
                        boolean descriptionTagOpened = false;
                        boolean uriTagOpened = false;

                        @Override
                        public void characters(char[] ch, int start, int length) {
                            String tmp = new String(ch);

                            if (descriptionTagOpened) {
                                description += tmp;

                            } else if (uriTagOpened) {
                                link += tmp;
                            }
                        }

                        @Override
                        public void endElement(String uri, String lname,
                                String qname) {
                            if (lname.equals("key")) {
                                key = null;

                            } else if (lname.equals("value")) {
                                ContentValues row = new ContentValues();
                                row.put("key", key);
                                row.put("language", language);
                                row.put("value", value);
                                row.put("wikilink", link);
                                row.put("description", description);
                                row.put("value_type", type);
                                row.put("name", key);
                                writeDb.insert(TagDbOpenHelper.getTableName(),
                                        "", row);

                            } else if (lname.equals("description")) {
                                descriptionTagOpened = false;

                            } else if (lname.equals("uri")) {
                                uriTagOpened = false;

                            }
                            depth--;
                        }

                        @Override
                        public void startElement(String uri, String lname,
                                String qname, Attributes attributes) {

                            if (lname.equals("map_features")) {
                                language = attributes.getValue("lang");

                            } else if (lname.equals("key")) {
                                key = attributes.getValue("v");

                            } else if (lname.equals("value")) {
                                value = attributes.getValue("v");
                                type = attributes.getValue("type");

                            } else if (lname.equals("description")) {
                                descriptionTagOpened = true;
                                description = "";

                            } else if (lname.equals("uri")) {
                                link = "";
                                uriTagOpened = true;
                            }
                            depth++;
                        }
                    });
        } catch (SAXException e) {
            e.printStackTrace();
            Log.e("XMLFileParsing", "XML parsing error.");
        } catch (FileNotFoundException e) {
            Log.e("XMLFileParsing", "XML file not found.");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("XMLFileParsing", "Error while reading XML file");
            e.printStackTrace();
        }

        openDb();
        Cursor crs = db.query("dictionary", new String[] { "COUNT(*)" }, null,
                null, null, null, null);
        if (crs.getCount() > 0) {
            crs.moveToFirst();
            Log.d("ROWCOUNTQUERY", "query rows: " + crs.getCount());
            Log.d("ROWCOUNTQUERY", crs.getString(0));
        }
        crs.close();
        closeDb();
    }
}
