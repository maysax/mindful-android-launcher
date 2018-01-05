package co.siempo.phone.helper;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import co.siempo.phone.app.Launcher3App;
import minium.co.core.app.CoreApplication;
import minium.co.core.log.Tracer;

/**
 * Created by Volansys
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
    private static String MENU_SCREEN = "menu_screen";
    public static String IF_SCREEN = "if_screen";


    //Event
    private static String IF_ACTION = "if_action";
    public static String SIEMPO_MENU = "siempo_menu";
    private static String THIRD_PARTY_APPLICATION = "third_party";
    private static String SCREEN_USAGE = "screen_usage";
    private static String SIEMPO_DEFAULT = "siempo_default";
    private static String SUPPRESSED_NOTIFICATION = "suppressed_notification";

    //Attribute
    private String SCREEN_NAME = "screen_name";
    private String TIME_SPENT = "time_spent";
    private String APPLICATION_NAME = "application_name";
    private String MENU_NAME = "menu_name";
    private String INTENT_FROM = "intent_from";
    private String ACTION = "action";
    private String IF_DATA = "if_data";
    private String SUPPRESSED_COUNT = "suppressed_count";

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
        if(CoreApplication.getInstance().getSharedPref().getBoolean("isFireBaseAnalyticsEnable",true)) {
            long longDifference = getTime(startTime, System.currentTimeMillis());
            if (longDifference != 0) {
                Bundle bundle = new Bundle();
                bundle.putString(SCREEN_NAME, screenName);
                bundle.putLong(TIME_SPENT, longDifference);
                Tracer.d("Firebase:" + SCREEN_USAGE + ": " + bundle.toString());
                getFirebaseAnalytics().logEvent(SCREEN_USAGE, bundle);
            }
        }

    }


    /**
     * Used for suppressed notification count by package name.
     *
     * @param count
     */
    public void logSuppressedNotification(String applicationName, long count) {
        if(CoreApplication.getInstance().getSharedPref().getBoolean("isFireBaseAnalyticsEnable",true)) {
            Bundle bundle = new Bundle();
            bundle.putLong(SUPPRESSED_COUNT, count);
            bundle.putString(APPLICATION_NAME, applicationName);
            Tracer.d("Firebase:" + SUPPRESSED_NOTIFICATION + ": " + bundle.toString());
            getFirebaseAnalytics().logEvent(SUPPRESSED_NOTIFICATION, bundle);
        }
    }

    /**
     * Used for Third party application open by User from application list.
     *
     * @param applicationName
     */
    public void logAppUsage(String applicationName) {
        if(CoreApplication.getInstance().getSharedPref().getBoolean("isFireBaseAnalyticsEnable",true)) {
            Bundle bundle = new Bundle();
            bundle.putString(APPLICATION_NAME, applicationName);
            Tracer.d("Firebase:" + THIRD_PARTY_APPLICATION + ": " + bundle.toString());
            getFirebaseAnalytics().logEvent(THIRD_PARTY_APPLICATION, bundle);
        }
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
        if(CoreApplication.getInstance().getSharedPref().getBoolean("isFireBaseAnalyticsEnable",true)) {
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
    }

    /**
     * Used for log the IF action of user.
     *
     * @param action
     * @param applicationName
     */
    public void logIFAction(String action, String applicationName, String data) {
        if(CoreApplication.getInstance().getSharedPref().getBoolean("isFireBaseAnalyticsEnable",true)) {
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

    /**
     * Siempo as default.
     *
     * @param action
     * @param startTime
     */
    public void logSiempoAsDefault(String action, long startTime) {
        if(CoreApplication.getInstance().getSharedPref().getBoolean("isFireBaseAnalyticsEnable",true)) {
            Bundle bundle = new Bundle();
            bundle.putString(ACTION, action);
            if (startTime != 0) {
                long longDifference = getTime(startTime, System.currentTimeMillis());
                if (longDifference != 0) {
                    bundle.putLong(TIME_SPENT, longDifference);
                    Tracer.d("Firebase:" + SIEMPO_DEFAULT + ": " + bundle.toString());
                    getFirebaseAnalytics().logEvent(SIEMPO_DEFAULT, bundle);
                }
            } else {
                Tracer.d("Firebase:" + SIEMPO_DEFAULT + ": " + bundle.toString());
                getFirebaseAnalytics().logEvent(SIEMPO_DEFAULT, bundle);
            }
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
        String strTime = "0";
        //        try {
//            long msInSecond = 1000;
//            long msInMinute = msInSecond * 60;
//            long msInHour = msInMinute * 60;
//            long msInDay = msInHour * 24;
//
//            long days = duration / msInDay;
//            duration = duration % msInDay;
//
//            long hours = duration / msInHour;
//            duration = duration % msInHour;
//
//            long minutes = duration / msInMinute;
//            duration = duration % msInMinute;
//
//            double seconds = (double) duration / msInSecond;
//            String strMilli = "" + seconds;
//            long strSecond;
//            String strMilliSecond;
//            String str[] = strMilli.split("\\.");
//            strSecond = Long.parseLong(str[0]);
//            strMilliSecond = str[1];
//            if (days != 0 || hours != 0 || minutes != 0 || strSecond != 0) {
//                strTime = "" + String.format("%02d", days) + "," + String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", strSecond) + ":" + strMilliSecond;
//            }
//        } catch (Exception e) {
//            CoreApplication.getInstance().logException(e);
//            e.printStackTrace();
//        }
        return endTime - startTime;
    }

}
