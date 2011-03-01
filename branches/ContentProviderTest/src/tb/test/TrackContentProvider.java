package tb.test;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

class TrackContentProvider extends ContentProvider {

	private final static String BASE_URI = "tb.test.provider";
	private final static int TRACKS = 1;
	private final static int DEL_TRACKS = 1;
	private final static int TRACK = 1;
	private final static int WAY = 1;
	private final static int AREA = 1;
	private final static int POI = 1;
	private final static int MEDIA = 1;
	private final static int WAY_NODES = 1;
	private final static int AREA_NODES = 1;
	
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static
    {
        sURIMatcher.addURI(BASE_URI, "all_tracks", TRACKS);
        sURIMatcher.addURI(BASE_URI, "delete_track/#", DEL_TRACKS);
        sURIMatcher.addURI(BASE_URI, "track/#", TRACK);
        sURIMatcher.addURI(BASE_URI, "way/#", WAY);
        sURIMatcher.addURI(BASE_URI, "area/#", AREA);
        sURIMatcher.addURI(BASE_URI, "poi/#", POI);
        sURIMatcher.addURI(BASE_URI, "media/#", MEDIA);
        sURIMatcher.addURI(BASE_URI, "way_nodes/#", WAY_NODES);
        sURIMatcher.addURI(BASE_URI, "area_nodes/#", AREA_NODES);
    }

	
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
