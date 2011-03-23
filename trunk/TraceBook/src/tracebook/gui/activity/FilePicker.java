package tracebook.gui.activity;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import tracebook.util.LogIt;
import Trace.Book.R;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author js
 * 
 */
public class FilePicker extends ListActivity {

    private class FilePickerArrayAdapter extends ArrayAdapter<FileWrapper> {

        public FilePickerArrayAdapter(Context context, List<FileWrapper> objects) {
            super(context, R.layout.listview_filepicker, R.id.tv_listrow,
                    objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);

            FileWrapper f = getItem(position);
            ImageView iv = (ImageView) v.findViewById(R.id.iv_listrow);
            if (f.file.isDirectory()) {
                iv.setImageResource(R.drawable.folder);
            } else {
                iv.setImageResource(R.drawable.file);
            }

            return v;
        }

    }

    private class FileWrapper {
        public File file;

        FileWrapper(File f) {
            file = f;
        }

        @Override
        public String toString() {
            return file.getName();
        }
    }

    /**
	 * 
	 */
    public final static String EXTENSIONS = "extensions";
    /**
	 * 
	 */
    public final static String PATH = "path";

    /**
	 * 
	 */
    protected final static String RESULT_CODE_ERROR = "error";

    /**
	 * 
	 */
    protected final static String RESULT_CODE_FILE = "file";

    /**
     * 
     */
    protected File currentFile;

    /**
     * 
     */
    protected String[] extensions;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_filepicker);

        Bundle arguments = this.getIntent().getExtras();
        currentFile = Environment.getExternalStorageDirectory();
        if (arguments != null) {
            String path = arguments.getString(PATH);
            extensions = arguments.getStringArray(EXTENSIONS);
            if (path != null) {
                setPath(path);
            }
        }

        TextView tvPath = (TextView) this.findViewById(R.id.tv_filepicker_path);
        tvPath.setText(currentFile.getAbsolutePath());

        updateAdapter();
    }

    /**
     * @param msg
     *            todo
     */
    protected void cancel(String msg) {
        Intent result = new Intent();
        result.putExtra(RESULT_CODE_ERROR, msg);
        setResult(RESULT_CANCELED, result);
        finish();
    }

    /**
     * @return todo
     */
    protected List<FileWrapper> getFileList() {
        File[] dirfiles = currentFile.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) {
                    return true;
                }
                if (extensions != null)
                    for (String ext : extensions) {
                        if (pathname.getName().endsWith(ext)) {
                            return true;
                        }
                    }
                else {
                    return true;
                }
                return false;
            }
        });

        if (dirfiles != null) {
            List<FileWrapper> res = new ArrayList<FileWrapper>();
            for (File f : dirfiles) {
                res.add(new FileWrapper(f));
            }

            Collections.sort(res, new Comparator<FileWrapper>() {
                public int compare(FileWrapper object1, FileWrapper object2) {
                    if (object1.file.isDirectory()
                            && !object2.file.isDirectory()) {
                        return -1;
                    }
                    if (!object1.file.isDirectory()
                            && object2.file.isDirectory()) {
                        return 1;
                    }
                    return object1.file.compareTo(object2.file);
                }
            });

            return res;
        }
        return new ArrayList<FileWrapper>();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
        case RESULT_OK:
            LogIt.d("FilePicker",
                    "got: " + data.getExtras().getString(RESULT_CODE_FILE));
            returnWithResult(data.getExtras().getString(RESULT_CODE_FILE));
            break;
        case RESULT_CANCELED:
            break;
        default:
            break;
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final FileWrapper file = (FileWrapper) l.getItemAtPosition(position);

        if (file.file.isFile()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(file.file.getName());
            builder.setMessage(this.getResources().getString(
                    R.string.alert_filepicker_message));

            builder.setPositiveButton(
                    this.getResources().getString(R.string.alert_global_ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            returnWithResult(file.file.getAbsolutePath());
                            dialog.cancel();
                        }
                    });

            builder.setNegativeButton(
                    this.getResources().getString(R.string.alert_global_cancel),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            builder.show();
        } else if (file.file.isDirectory()) {
            Intent i = new Intent(this, FilePicker.class);
            i.putExtra(PATH, file.file.getAbsolutePath());
            i.putExtra(EXTENSIONS, extensions);
            this.startActivityForResult(i, RESULT_OK);
        }

        super.onListItemClick(l, v, position, id);
    }

    /**
     * @param path
     *            todo
     */
    protected void returnWithResult(String path) {
        Intent result = new Intent();
        result.putExtra(RESULT_CODE_FILE, path);
        setResult(RESULT_OK, result);
        LogIt.d("FilePicker", "Returns: " + path);
        finish();
    }

    /**
     * @param path
     *            todo
     */
    protected void setPath(String path) {
        currentFile = new File(path);
        if (!currentFile.isDirectory()) {
            cancel(getResources().getString(
                    R.string.string_filepicker_error_invalid_path));
        }
    }

    /**
     * 
     */
    protected void updateAdapter() {
        setListAdapter(new FilePickerArrayAdapter(this, getFileList()));
    }
}
