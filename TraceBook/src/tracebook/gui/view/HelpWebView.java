package tracebook.gui.view;

import Trace.Book.R;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * This class show the HTML help site in a web view in subject to the device
 * language.
 * 
 * @author greenTraxas
 */
public class HelpWebView extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String language = "de";
        boolean about = false;
        boolean help = false;
        super.onCreate(savedInstanceState);

        final Bundle extras = getIntent().getExtras();

        if (extras != null) {
            if (extras.containsKey("About")) {
                language = extras.getString("About");
                about = true;
            }
            if (extras.containsKey("Help")) {
                language = extras.getString("Help");
                help = true;
            }
        }

        setContentView(R.layout.activity_webviewactivity);

        WebView webview;
        webview = (WebView) findViewById(R.id.wv_helpwebviewActivity_webview);
        webview.getSettings().setJavaScriptEnabled(true);
        if (about)
            webview.loadUrl("file:///android_asset/about/about-" + language
                    + ".html");
        else if (help)
            webview.loadUrl("file:///android_asset/help/help-" + language
                    + ".html");

    }
}
