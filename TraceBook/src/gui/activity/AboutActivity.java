package gui.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import Trace.Book.R;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 *
 * 
 */
public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_aboutactivity);
        setTitle(R.string.string_aboutActivity_title);
    }

    /**
     * This method parse the textFile with the gplLicense.
     * 
     * @return The OutputStream of the LicenseTxtFile as a String
     */
    String readTxt() {

        InputStream inputStream = getResources().openRawResource(
                R.raw.gpl3_license);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;
        try {
            i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteArrayOutputStream.toString();
    }

    /**
     * This method shows a dialog box with the used license.
     * 
     * @param view
     *            not used
     */
    public void licenseBtn(View view) {

        final Dialog licenseDialog = new Dialog(this);

        licenseDialog.setContentView(R.layout.dialog_license);
        licenseDialog.setTitle(R.string.string_licenseDialog_title);
        licenseDialog.setCancelable(true);

        final TextView license = (TextView) licenseDialog
                .findViewById(R.id.tv_dialogLicense_license);

        license.setText(readTxt());
        licenseDialog.show();

    }
}
