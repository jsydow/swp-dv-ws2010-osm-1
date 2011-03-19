package gui.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import util.Helper;
import Trace.Book.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * The Class AboutActivity shows the about layout with the version number,
 * credits, all used resources and the license of TraceBook. This class called
 * from the option menu of the StartActivity.
 * 
 */
public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_aboutactivity);
        setTitle(R.string.string_aboutActivity_title);
        setTextViews();
        // Set status bar
        Helper.setStatusBar(this,
                getResources().getString(R.string.tv_statusbar_aboutTitle),
                getResources().getString(R.string.tv_statusbar_aboutDesc),
                R.id.ly_aboutActivity_statusbar, false);

    }

    /**
     * This method sets the text of the TextView resources with the resource.txt
     * from the assets directory.
     */
    void setTextViews() {
        TextView resources = (TextView) findViewById(R.id.tv_aboutActivity_usedResources);
        try {
            resources.setText(readTxt("used_resources.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method parse the textFile with the gplLicense.
     * 
     * @param dataname
     *            String - which file has to be parsed
     * 
     * @return The OutputStream of the LicenseTxtFile as a String
     * @throws IOException
     *             not used
     */
    String readTxt(String dataname) throws IOException {

        InputStream inputStream = getAssets().open(dataname);

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

        try {
            license.setText(readTxt("gpl3_license.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        licenseDialog.show();

    }

    /**
     * The Method for the preference image Button from the status bar. The
     * Method starts the PreferenceActivity.
     * 
     * @param view
     *            not used
     */
    public void statusBarPrefBtn(View view) {
        final Intent intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
    }

    /**
     * This Method for the two (title and description) button from the status
     * bar. This method starts the dialog with all activity informations.
     * 
     * @param v
     *            not used
     */
    public void statusBarTitleBtn(View v) {
        Helper.setActivityInfoDialog(this,
                getResources().getString(R.string.tv_statusbar_aboutTitle),
                getResources().getString(R.string.tv_statusbar_aboutDesc));
    }
}
