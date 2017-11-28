package co.siempo.phone.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.concurrent.TimeUnit;

import co.siempo.phone.app.Launcher3App;
import minium.co.core.app.CoreApplication;

/**
 * Created by Shahab on 5/8/2017.
 */


public class FirebaseHelper {


    private static FirebaseHelper firebaseHelper;
    public static String INTENTION_FIELD = "Intention Field";
    public static String SIEMPO_MENU = "Siempo Menu";
    public static String SIEMPO_DEFAULT = "Siempo As Default";

    //Action
    public static String ACTION_CALL = "Call";
    public static String ACTION_SMS = "Send as SMS";
    public static String ACTION_SAVE_NOTE = "Save Note";
    public static String ACTION_CREATE_CONTACT = "Create Contact";
    public static String ACTION_CONTACT_PICK = "Contact Picked";
    public static String ACTION_APPLICATION_PICK = "Contact Picked";

    private FirebaseHelper() {

    }

    public static FirebaseHelper getIntance() {
        if (firebaseHelper == null) {
            firebaseHelper = new FirebaseHelper();
        }
        return firebaseHelper;
    }


    public FirebaseAnalytics getFirebaseAnalytics() {
        return ((Launcher3App) CoreApplication.getInstance()).getFirebaseAnalytics();
    }

    public void appOpenEvent() {
        Bundle bundle = new Bundle();
        getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);
    }

    /**
     * Screen name with time spent per screen wise.
     *
     * @param screenName
     * @param startTime
     */
    public void logScreenUsageTime(String screenName, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(duration),
                TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
                TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
        Bundle bundle = new Bundle();
        bundle.putString("Screen Name", screenName);
        bundle.putString("Time Spent", "" + hms);
        getFirebaseAnalytics().logEvent("Screen Usage", bundle);

    }

    /**
     * Print third party application name log.
     *
     * @param applicationName
     * @param from            0=tray and 1=IF
     */
    public void logAppUsage(String applicationName, int from) {
        Bundle bundle = new Bundle();
        bundle.putString("Application Name", applicationName);
        getFirebaseAnalytics().logEvent("Third Party Application", bundle);
    }

    /**
     * Siempo menu Usage from menu list.
     *
     * @param applicationName
     * @param from            0=Menu List, 1=IF Screen
     */
    public void logSiempoMenuUsage(String applicationName, int from) {
        Bundle bundle = new Bundle();
        bundle.putString("Menu Name", applicationName);
        if (from == 0) {
            bundle.putString("From", "Menu List");
        } else {
            bundle.putString("From", "IF Screen");
        }
        getFirebaseAnalytics().logEvent("Siempo Menu", bundle);
    }

    /**
     * @param action
     */
    public void logIFAction(String action, String from) {
        Bundle bundle = new Bundle();
        bundle.putString("Action", action);
        if (from.equalsIgnoreCase("")) {
//            bundle.putString("Action", action);
        } else {
            bundle.putString("Application Name", from);
        }
        getFirebaseAnalytics().logEvent("IF Action", bundle);
    }

}
