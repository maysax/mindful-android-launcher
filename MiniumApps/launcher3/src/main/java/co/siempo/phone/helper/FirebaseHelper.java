package co.siempo.phone.helper;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.concurrent.TimeUnit;

import co.siempo.phone.app.Launcher3App;
import minium.co.core.app.CoreApplication;
import minium.co.core.log.Tracer;

/**
 * Created by Shahab on 5/8/2017.
 */


public class FirebaseHelper {


    private static FirebaseHelper firebaseHelper;

    //Action
    public static String ACTION_CALL = "call";
    public static String ACTION_SMS = "send_as_sms";
    public static String ACTION_SAVE_NOTE = "save_note";
    public static String ACTION_CREATE_CONTACT = "create_contact";
    public static String ACTION_CONTACT_PICK = "contact_picked";
    public static String ACTION_APPLICATION_PICK = "application_picked";

    // Screen Name
    public static String MENU_SCREEN = "menu_screen";
    public static String IF_SCREEN = "if_screen";
    public static String SIEMPO_DEFAULT = "siempo_default";

    //Event
    public static String IF_ACTION = "if_action";
    public static String SIEMPO_MENU = "siempo_menu";
    public static String THIRD_PARTY_APPLICATION = "third_party";
    public static String SCREEN_USAGE = "screen_usage";

    //Attribute
    private String SCREEN_NAME = "screen_name";
    private String TIME_SPENT = "time_spent";
    private String APPLICATION_NAME = "application_name";
    private String MENU_NAME = "menu_name";
    private String INTENT_FROM = "intent_from";
    private String ACTION = "action";
    private String IF_DATA = "if_data";

    public FirebaseHelper() {

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

    /**
     * Used for how long user spent time on specific screen by screen name.
     *
     * @param screenName
     * @param startTime
     */
    public void logScreenUsageTime(String screenName, long startTime) {
        long currentTime = System.currentTimeMillis();
        long duration = currentTime - startTime;


        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long day = duration / daysInMilli;
        duration = duration % daysInMilli;

        long hours = duration / hoursInMilli;
        duration = duration % hoursInMilli;

        long minute = duration / minutesInMilli;
        duration = duration % minutesInMilli;

        long second = duration / secondsInMilli;

        if (day == 0 && hours == 0 && minute == 0 && second == 0) {
            Tracer.d("Firebase:" + SCREEN_USAGE + ": No Difference");
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(SCREEN_NAME, screenName);
            bundle.putString(TIME_SPENT, "" + String.format("%02d", day) + "," + String.format("%02d", hours) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", second));
            Tracer.d("Firebase:" + currentTime + ": " + startTime);
            Tracer.d("Firebase:" + SCREEN_USAGE + ": " + bundle.toString());
            getFirebaseAnalytics().logEvent(SCREEN_USAGE, bundle);
        }

    }

    /**
     * Used for Third party application open by User from application list.
     *
     * @param applicationName
     */
    public void logAppUsage(String applicationName) {
        Bundle bundle = new Bundle();
        bundle.putString(APPLICATION_NAME, applicationName);
        Tracer.d("Firebase:" + THIRD_PARTY_APPLICATION + ": " + bundle.toString());
        getFirebaseAnalytics().logEvent(THIRD_PARTY_APPLICATION, bundle);
    }

    /**
     * Used fot menu used by user from either IF or menu list based in from.
     * from = 0 for Menu List
     * from = 1 for IF Screen
     *
     * @param applicationName
     * @param actionFor
     */
    public void logSiempoMenuUsage(String applicationName, int actionFor) {
        Bundle bundle = new Bundle();
        bundle.putString(MENU_NAME, applicationName);
        if (actionFor == 0) {
            bundle.putString(INTENT_FROM, MENU_SCREEN);
        } else {
            bundle.putString(INTENT_FROM, IF_SCREEN);
        }
        Tracer.d("Firebase:" + SIEMPO_MENU + ": " + bundle.toString());
        getFirebaseAnalytics().logEvent(SIEMPO_MENU, bundle);
    }

    /**
     * Used for log the IF action of user.
     *
     * @param action
     * @param applicationName
     */
    public void logIFAction(String action, String applicationName, String data) {

        Bundle bundle = new Bundle();
        bundle.putString(ACTION, action);
        if (!applicationName.equalsIgnoreCase("")) {
            bundle.putString(APPLICATION_NAME, applicationName);
        } else {
            bundle.putString(IF_DATA, data);
        }
        Tracer.d(IF_ACTION + ": " + bundle.toString());
        getFirebaseAnalytics().logEvent(IF_ACTION, bundle);

    }

}
