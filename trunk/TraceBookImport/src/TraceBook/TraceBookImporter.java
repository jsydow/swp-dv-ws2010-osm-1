package TraceBook;
import org.openstreetmap.josm.actions.ExtensionFileFilter;
import org.openstreetmap.josm.io.FileImporter;


public class TraceBookImporter extends FileImporter{
	public static final String TRACEBOOK_FILE_EXT = "tbt";
	public static final String TRACEBOOK_FILE_EXT_DOT=".";
	public TraceBookImporter(){
		this(new ExtensionFileFilter(TRACEBOOK_FILE_EXT, TRACEBOOK_FILE_EXT,tr("TraceBook Track Files (*"+TRACEBOOK_FILE_EXT_DOT+TRACEBOOK_FILE_EXT+")")));
	}
	public TraceBookImporter(ExtensionFileFilter filter) {
		super(filter);
		// TODO Auto-generated constructor stub
	}

}
