package co.siempo.phone.map;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.ViewById;

import co.siempo.phone.R;
import minium.co.core.ui.CoreActivity;

import static android.view.View.GONE;

@Fullscreen
@EActivity
public class SiempoMapActivity extends CoreActivity {
    String HOME_PAGE = "https://www.google.com/maps";

    //  @ViewById
    WebView mWebView;

    @ViewById
    ProgressBar pBar;

//    @ViewById
//    ImageView imgLogo;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_siempo);

        mWebView = (WebView) findViewById(R.id.mWebView);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setSupportZoom(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //    webview.setWebViewClient(new GeoWebViewClient());
        mWebView.setWebChromeClient(new GeoWebChromeClient());
        webSettings.setDatabaseEnabled(true);
        webSettings.setGeolocationDatabasePath(this.getFilesDir().getPath());


        mWebView.setVerticalScrollbarPosition(2);

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                imgLogo.setVisibility(GONE);
                pBar.setVisibility(GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //if (url.contains("google")) return true;
//                return false;
                view.loadUrl(url);
                return true;
            }
        });

        mWebView.loadUrl(HOME_PAGE);
    }

    //@AfterViews
    void afterViews() {
        mWebView.setVerticalScrollbarPosition(2);

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                imgLogo.setVisibility(GONE);
                pBar.setVisibility(GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //if (url.contains("google")) return true;
//                return false;
                view.loadUrl(url);
                return true;
            }
        });

        // Brower niceties -- pinch / zoom, follow links in place
//        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        mWebView.getSettings().setBuiltInZoomControls(true);
//        mWebView.setWebChromeClient(new GeoWebChromeClient());
//        // Below required for geolocation
//        mWebView.getSettings().setJavaScriptEnabled(true);
//        mWebView.getSettings().setGeolocationEnabled(true);

        mWebView.loadUrl(HOME_PAGE);
    }


    /**
     * WebChromeClient subclass handles UI-related calls
     * Note: think chrome as in decoration, not the Chrome browser
     */
    public class GeoWebChromeClient extends WebChromeClient {
        @Override
        public void onGeolocationPermissionsShowPrompt(final String origin,
                                                       final GeolocationPermissions.Callback callback) {
            // Always grant permission since the app itself requires location
            // permission and the user has therefore already granted it
            callback.invoke(origin, true, false);

        }
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
