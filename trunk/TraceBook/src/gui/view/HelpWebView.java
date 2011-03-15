package gui.view;

import Trace.Book.R;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/**
 *
 * 
 */
public class HelpWebView extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_webviewactivity);

        WebView webview;
        webview = (WebView) findViewById(R.id.wv_helpwebviewActivity_webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl("http://www.google.com");
    }
}
