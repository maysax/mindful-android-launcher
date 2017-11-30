package co.siempo.phone.helper;

import android.os.Bundle;

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
    public static String ACTION_APPLICATION_PICK = "Application Picked";

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
     * Used for how long user spent time on specific screen by screen name.
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
     * Used for Third party application open by User from application list.
     * @param applicationName
     */
    public void logAppUsage(String applicationName) {
        Bundle bundle = new Bundle();
        bundle.putString("Application Name", applicationName);
        getFirebaseAnalytics().logEvent("Third Party Application", bundle);
    }

    /**
     * Used fot menu used by user from either IF or menu list based in from.
     * from = 0 for Menu List
     * from = 1 for IF Screen
     * @param applicationName
     * @param actionFor
     */
    public void logSiempoMenuUsage(String applicationName, int actionFor) {
        Bundle bundle = new Bundle();
        bundle.putString("Menu Name", applicationName);
        if (actionFor == 0) {
            bundle.putString("From", "Menu List");
        } else {
            bundle.putString("From", "IF Screen");
        }
        getFirebaseAnalytics().logEvent("Siempo Menu", bundle);
    }

    /**
     * Used for log the IF action of user.
     * @param action
     * @param applicationName
     */
    public void logIFAction(String action, String applicationName,String data) {

        Bundle bundle = new Bundle();
        bundle.putString("Action", action);
        if (!applicationName.equalsIgnoreCase("")) {
            bundle.putString("Application Name", applicationName);
        }else{
            bundle.putString("Data", data);
        }
        getFirebaseAnalytics().logEvent("IF Action", bundle);
    }

}
