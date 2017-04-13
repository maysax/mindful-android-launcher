package co.minium.launcher3.inbox;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import co.minium.launcher3.R;
import co.minium.launcher3.map.SiempoMapActivity;
import minium.co.core.ui.CoreActivity;

import static android.view.View.GONE;

@EActivity(R.layout.activity_google_inbox)
public class GoogleInboxActivity extends CoreActivity {

    String HOME_PAGE = "https://mail.google.com/mail";

    @ViewById
    WebView webView;

    @ViewById
    ProgressBar pBar;

    @AfterViews
    void afterViews() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pBar.setVisibility(GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        webView.loadUrl(HOME_PAGE);
    }
}
