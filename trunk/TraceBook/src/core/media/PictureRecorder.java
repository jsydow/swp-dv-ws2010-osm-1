/**
 * 
 */
package core.media;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * @author Huy Dinh
 * 
 */
public class PictureRecorder extends Recorder {

    /*
     * (non-Javadoc)
     * 
     * @see core.media.Recorder#getNewFilename()
     */
    @Override
    protected String getNewFilename() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String newFilename = sdf.format(new Date());

        newFilename = "image_" + newFilename + ".jpg";

        return newFilename;
    }

    /**
     * 
     * @param activity
     */
    public void startIntent(Activity activity) {
        final Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        filename = getNewFilename();

        i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(getPath())));
        activity.startActivityForResult(i, TAKE_PHOTO_CODE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see core.media.Recorder#start()
     */
    @Override
    public String start() {
        // Nothing to start.
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see core.media.Recorder#stop()
     */
    @Override
    public void stop() {
        // Nothing to stop.
    }
}
