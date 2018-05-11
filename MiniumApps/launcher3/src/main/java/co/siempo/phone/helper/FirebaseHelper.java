package co.siempo.phone.helper;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.app.Launcher3App;
import co.siempo.phone.log.Tracer;

/**
 * Created by Volansys
 */


public class FirebaseHelper {


    //Action
    public static String ACTION_CALL = "call";
    public static String ACTION_SMS = "send_as_sms";
    public static String ACTION_SAVE_NOTE = "save_note";
    public static String ACTION_CONTACT_PICK = "contact_picked";
    public static String ACTION_APPLICATION_PICK = "application_picked";
    public static String SEARCH_PANE = "search_pane";
    private static String SIEMPO_MENU = "siempo_menu";
    private static FirebaseHelper firebaseHelper;
    // Screen Name
    private static String TOOLS_PANE = "tools_pane";
    private static String FAVORITE_PANE = "favorite_pane";
    private static String JUNKFOOD_PANE = "junkfood_pane";
    //Event
    private static String USER_SEARCH = "user_search";
    private static String SCREEN_USAGE = "screen_usage";
    private static String SIEMPO_DEFAULT = "siempo_default";
    private static String SUPPRESSED_NOTIFICATION = "suppressed_notification";
    private static String TEMPO = "tempo";
    public static String INTENTIONS = "intentions";
    public static String HIDE_ICON_BRANDING = "hide_icon_branding";
    public static String RANDOMIZED_JUNK_FOOD = "randomized_junk_food";
    public static String ALLOW_SPECIFIC = "allow_specific";

    //Attribute
    private String SCREEN_NAME = "screen_name";
    private String TIME_SPENT = "time_spent";
    private String APPLICATION_NAME = "application_name";
    private String TOOL_NAME = "tool_name";
    private String INTENT_FROM = "intent_from";
    private String ACTION = "action";
    private String SEARCH_DATA = "search_data";
    private String SUPPRESSED_COUNT = "suppressed_count";
    private String TEMPO_INTERVAL_ONLY_AT = "tempo_interval_onlyat";
    private String TEMPO_INTERVAL = "tempo_interval";
    private String TEMPO_TYPE = "tempo_type";
    private String ENABLE_DISABLE = "enable_disable";
    private String BLOCK_UNBLOCK = "block_unblock";

    public FirebaseHelper() {

    }

    public static FirebaseHelper getInstance() {
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
        long longDifference = getTime(startTime, System.currentTimeMillis());
        if (longDifference != 0) {
            Bundle bundle = new Bundle();
            bundle.putString(SCREEN_NAME, screenName);
            bundle.putLong(TIME_SPENT, longDifference);
            Tracer.i("Firebase:" + SCREEN_USAGE + ": " + bundle.toString());
            getFirebaseAnalytics().logEvent(SCREEN_USAGE, bundle);
        }

    }


    /**
     * Used for suppressed notification count by package name.
     *
     * @param count
     */
    public void logSuppressedNotification(String applicationName, long count) {
        Bundle bundle = new Bundle();
        bundle.putLong(SUPPRESSED_COUNT, count);
        bundle.putString(APPLICATION_NAME, applicationName);
        Tracer.i("Firebase:" + SUPPRESSED_NOTIFICATION + ": " + bundle.toString());
        getFirebaseAnalytics().logEvent(SUPPRESSED_NOTIFICATION, bundle);
    }


    /**
     * Used fot Tool/App used by user from either IF or 3 panes.
     * from = 0 for Tool Pane.
     * from = 1 for Favorite Pane
     * from = 2 for Junkfood Pane
     * from = 3 for Search Pane
     *
     * @param applicationName
     * @param actionFor
     */
    public void logSiempoMenuUsage(int actionFor, String toolname, String applicationName) {
        Bundle bundle = new Bundle();
        if (actionFor == 0) {
            bundle.putString(INTENT_FROM, TOOLS_PANE);
            bundle.putString(TOOL_NAME, toolname);
            bundle.putString(APPLICATION_NAME, applicationName);
        } else if (actionFor == 1) {
            bundle.putString(INTENT_FROM, FAVORITE_PANE);
            bundle.putString(APPLICATION_NAME, applicationName);
        } else if (actionFor == 2) {
            bundle.putString(INTENT_FROM, JUNKFOOD_PANE);
            bundle.putString(APPLICATION_NAME, applicationName);
        } else if (actionFor == 3) {
            bundle.putString(INTENT_FROM, SEARCH_PANE);
            bundle.putString(TOOL_NAME, toolname);
            bundle.putString(APPLICATION_NAME, applicationName);
        }
        Tracer.i("Firebase:" + SIEMPO_MENU + ": " + bundle.toString());
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
            bundle.putString(SEARCH_DATA, data);
        }
        Tracer.i(USER_SEARCH + ": " + bundle.toString());
        getFirebaseAnalytics().logEvent(USER_SEARCH, bundle);
    }

    /**
     * Siempo as default.
     *
     * @param action
     * @param startTime
     */
    public void logSiempoAsDefault(String action, long startTime) {

        Bundle bundle = new Bundle();
        bundle.putString(ACTION, action);
        if (startTime != 0) {
            long longDifference = getTime(startTime, System.currentTimeMillis());
            if (longDifference != 0) {
                bundle.putLong(TIME_SPENT, longDifference);
                Tracer.i("Firebase:" + SIEMPO_DEFAULT + ": " + bundle.toString());
                getFirebaseAnalytics().logEvent(SIEMPO_DEFAULT, bundle);
            }
        } else {
            Tracer.i("Firebase:" + SIEMPO_DEFAULT + ": " + bundle.toString());
            getFirebaseAnalytics().logEvent(SIEMPO_DEFAULT, bundle);
        }
    }

    /**
     * 2 date difference in day,hh:mm:ss:ms
     *
     * @param startTime
     * @param endTime
     * @return
     */
    private long getTime(long startTime, long endTime) {
        return endTime - startTime;
    }

    /**
     * Used for Log the interval and time stamp when change the tempo setting.
     *
     * @param tempoType             shows the user selected type of tempo 0 for individual,1 batch,2 OnlyAt
     * @param tempo_interval        which used for 0 and 1 tempo type option(individual,batch)
     * @param tempo_interval_onlyat used for OnlyAt tempo type.
     */
    public void logTempoIntervalTime(int tempoType, int tempo_interval, String tempo_interval_onlyat) {
        Bundle bundle = new Bundle();
        bundle.putInt(TEMPO_TYPE, tempoType);
        if (tempoType == 2) {
            bundle.putString(TEMPO_INTERVAL_ONLY_AT, tempo_interval_onlyat);
        } else {
            bundle.putInt(TEMPO_INTERVAL, tempo_interval);
        }
        Tracer.i("Firebase:" + TEMPO + ": " + bundle.toString());
        getFirebaseAnalytics().logEvent(TEMPO, bundle);
    }

    /**
     * This method used when user enable/disable Intention,IconBranding,Randomize Junk-food.
     *
     * @param eventFor
     * @param enableDisable
     */
    public void logIntention_IconBranding_Randomize(String eventFor, int enableDisable) {
        Bundle bundle = new Bundle();
        bundle.putInt(ENABLE_DISABLE, enableDisable);
        if (eventFor.equalsIgnoreCase(INTENTIONS)) {
            Tracer.i("Firebase:" + INTENTIONS + ": " + bundle.toString());
            getFirebaseAnalytics().logEvent(INTENTIONS, bundle);
        } else if (eventFor.equalsIgnoreCase(HIDE_ICON_BRANDING)) {
            Tracer.i("Firebase:" + HIDE_ICON_BRANDING + ": " + bundle.toString());
            getFirebaseAnalytics().logEvent(HIDE_ICON_BRANDING, bundle);
        } else if (eventFor.equalsIgnoreCase(RANDOMIZED_JUNK_FOOD)) {
            Tracer.i("Firebase:" + RANDOMIZED_JUNK_FOOD + ": " + bundle.toString());
            getFirebaseAnalytics().logEvent(RANDOMIZED_JUNK_FOOD, bundle);
        }


    }

    /**
     * Block/Unblock application name log.
     *
     * @param application_name
     * @param block_unblock
     */
    public void logBlockUnblockApplication(String application_name, int block_unblock) {
        Bundle bundle = new Bundle();
        bundle.putInt(BLOCK_UNBLOCK, block_unblock);
        bundle.putString(APPLICATION_NAME, application_name);
        Tracer.i("Firebase:" + ALLOW_SPECIFIC + ": " + bundle.toString());
        getFirebaseAnalytics().logEvent(ALLOW_SPECIFIC, bundle);
    }

}
