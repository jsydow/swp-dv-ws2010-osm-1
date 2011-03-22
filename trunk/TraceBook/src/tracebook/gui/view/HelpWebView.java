/*======================================================================
 *
 * This file is part of TraceBook.
 *
 * TraceBook is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * TraceBook is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TraceBook. If not, see <http://www.gnu.org/licenses/>.
 *
 =====================================================================*/

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
