package core.data.db;

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
 * @author js
 * 
 */
public class TagDb {

    /**
     * 
     */
    TagDbOpenhelper helper;
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
        helper = new TagDbOpenhelper(context);
        db = helper.getReadableDatabase();
    }

    /**
     * @param searchText
     *            The text to search for.
     * @param language
     *            s
     * @return The
     */
    public List<TagSearchResult> getTag(String searchText, String language) {
        if (db == null) {
            openDb();
        }

        if (db != null) {
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

        Cursor result = db.query(TagDbOpenhelper.getTableName(),
                TagDbOpenhelper.getColumns(), TagDbOpenhelper.getSelection(),
                new String[] { searchText, searchText, searchText, language },
                null, null, null);

        result.moveToFirst();
        while (result.isAfterLast()) {
            // insert row to tags list
            tags.add(TagDbOpenhelper.getResultFromCursor(result));

            result.moveToNext();
        }
        result.close();
    }

    /**
     * @param xmlFile
     *            The XML file to parse.
     */
    public void initDbWithFile(String xmlFile) {
        try {
            Xml.parse(xmlFile, new DefaultHandler() {

                SQLiteDatabase writeDb;

                /*
                 * (non-Javadoc)
                 * 
                 * @see org.xml.sax.helpers.DefaultHandler#endDocument()
                 */
                @Override
                public void endDocument() throws SAXException {
                    writeDb.close();
                    super.endDocument();
                }

                /*
                 * (non-Javadoc)
                 * 
                 * @see org.xml.sax.helpers.DefaultHandler#startDocument()
                 */
                @Override
                public void startDocument() throws SAXException {
                    super.startDocument();
                    writeDb = helper.getWritableDatabase();
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
                public void endElement(String uri, String lname, String qname) {
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
                        writeDb.insert(TagDbOpenhelper.getTableName(), "", row);

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
            // nothing happens
        }
    }
}
