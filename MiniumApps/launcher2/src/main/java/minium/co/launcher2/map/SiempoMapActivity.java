package minium.co.launcher2.map;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import minium.co.core.ui.CoreActivity;
import minium.co.launcher2.R;

import static android.view.View.GONE;

@EActivity(R.layout.activity_main_map)
public class SiempoMapActivity extends CoreActivity {
    String HOME_PAGE = "https://www.google.com/maps";

    @ViewById
    WebView mWebView;

    @ViewById
    ProgressBar pBar;

    @ViewById
    ImageView imgLogo;

    @AfterViews
    void afterViews() {
        mWebView.setVerticalScrollbarPosition(2);
        mWebView.loadUrl(HOME_PAGE);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                imgLogo.setVisibility(GONE);
                pBar.setVisibility(GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //if (url.contains("google")) return true;
                return false;
            }
        });
        mWebView.getSettings().setJavaScriptEnabled(true);
    }


    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
        }
}
