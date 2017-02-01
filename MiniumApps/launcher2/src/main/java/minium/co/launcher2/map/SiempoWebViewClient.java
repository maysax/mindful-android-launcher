package minium.co.launcher2.map;

import android.app.Activity;
import android.webkit.WebViewClient;

/**
 * Created by godslave on 03/04/2016.
 */
public class SiempoWebViewClient extends WebViewClient{
   // String HOME_PAGE = "https://www.google.com/map";
    private Activity callerActivity;
    public SiempoWebViewClient(Activity callerActivity) {
        callerActivity = callerActivity;
    }

    /*
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
     //  return super.shouldOverrideUrlLoading(view, url);
    //        if(Uri.parse(url).getHost().equals(HOME_PAGE)){
    //            return false;
    //        }
        System.out.println("Returned url is :: "+ url);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        callerActivity.startActivity(intent);
        return true;
    }*/

}