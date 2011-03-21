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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_webviewactivity);

        WebView webview;
        webview = (WebView) findViewById(R.id.wv_helpwebviewActivity_webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl("file:///android_asset/help/help-" + language + ".html");
    }
}
