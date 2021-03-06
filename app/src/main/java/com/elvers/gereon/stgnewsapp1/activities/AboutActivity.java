package com.elvers.gereon.stgnewsapp1.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.elvers.gereon.stgnewsapp1.R;
import com.elvers.gereon.stgnewsapp1.utils.ArticleWebViewClient;
import com.elvers.gereon.stgnewsapp1.utils.Utils;

/**
 * Activity that shows "About" information like sources and feedback links
 * <p>
 * Points a WebView to a static WordPress-Post that contains this info.
 * This makes edit easier in the future, because it increases parity between website and App (no need to maintain content twice)
 *
 * @author Gereon Elvers
 */
public class AboutActivity extends AppCompatActivity {

    public static final String ABOUT_URL = "https://stg-sz.net/ueber-uns/?inapp";
    public WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.updateNightMode(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Set up ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setTitle(R.string.about_string);
        }

        webView = findViewById(R.id.about_content_wv);
        final View loadingIndicator = findViewById(R.id.about_loading_circle);

        /*
         * Javascript is necessary for some dynamic components that might be implemented in the future,
         * creates parity between the custom WebView and regular browser and, more importantly, makes sure the "?inapp"-parameter works as expected
         * But the most important reason to enable JavaScript is the hacky dark theme ^^
         */
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new ArticleWebViewClient(this));
        webView.setVisibility(View.INVISIBLE);
        // Setting up loading indicator (spinning circle)
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView webView, int progress) {
                if (progress == 100) {
                    // Hide loading indicator and show WebView once loading is finished
                    loadingIndicator.setVisibility(View.INVISIBLE);
                    webView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Start loading URL
        webView.loadUrl(ABOUT_URL);
    }


}
