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

package de.fu.tracebook.gui.view;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.webkit.WebView;
import de.fu.tracebook.R;

/**
 * This class show the HTML help and about site in a web view in subject to the
 * device language.
 */
public class HelpWebView extends Activity {

    /**
     * WebView for our WebView which we use in this activity.
     */
    WebView webview;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String language = "en";
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

        if (!language.equals("en") && !language.equals("de"))
            language = "en";

        setContentView(R.layout.activity_webviewactivity);

        webview = (WebView) findViewById(R.id.wv_helpwebviewActivity_webview);
        webview.getSettings().setJavaScriptEnabled(true);
        if (about) {
            SharedPreferences appPreferences = PreferenceManager
                    .getDefaultSharedPreferences(this);
            switch (Integer.parseInt(appPreferences.getString(
                    "lst_switchTheme", "1"))) {
            case 1:
                webview.loadUrl("file:///android_asset/about/about-dark-"
                        + language + ".html");
                break;
            case 0:
                webview.loadUrl("file:///android_asset/about/about-light-"
                        + language + ".html");
                break;
            case 2:
                webview.loadUrl("file:///android_asset/about/about-dark-"
                        + language + ".html");
                break;
            case 3:
                webview.loadUrl("file:///android_asset/about/about-light-"
                        + language + ".html");
                break;
            case 4:
                webview.loadUrl("file:///android_asset/about/about-light-"
                        + language + ".html");
                break;
            default:

            }
        } else if (help) {
            webview.loadUrl("file:///android_asset/help/help-" + language
                    + ".html");
        }
    }
}
