package co.siempo.phone.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by Shahab on 5/8/2017.
 */

@SuppressWarnings("ALL")
public class FirebaseHelper {

    private FirebaseAnalytics mFirebaseAnalytics;
    private Context context;

    public FirebaseHelper(Context context) {
        this.context = context;
    }

    public FirebaseAnalytics getFirebaseAnalytics() {
        if (mFirebaseAnalytics == null) mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        return mFirebaseAnalytics;
    }

    public void appOpenEvent() {
        Bundle bundle = new Bundle();
        getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);
    }

    public void testEvent1() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "5");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Hello");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @SuppressLint("InvalidAnalyticsName")
    public void testEvent2() {
        Bundle params = new Bundle();
        params.putString("usage time", "messages");
        params.putString("full_text", "Messages used 10 times");
        getFirebaseAnalytics().logEvent("share_image", params);
    }
}
