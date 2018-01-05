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


    //Action
    public static String ACTION_CALL = "call";
    public static String ACTION_SMS = "send_as_sms";
    public static String ACTION_SAVE_NOTE = "save_note";
    public static String ACTION_CREATE_CONTACT = "create_contact";
    public static String ACTION_CONTACT_PICK = "contact_picked";
    public static String ACTION_APPLICATION_PICK = "application_picked";
    public static String IF_SCREEN = "if_screen";
    public static String SIEMPO_MENU = "siempo_menu";
    private static FirebaseHelper firebaseHelper;
    // Screen Name
    private static String MENU_SCREEN = "menu_screen";
    //Event
    private static String IF_ACTION = "if_action";
    private static String THIRD_PARTY_APPLICATION = "third_party";
    private static String SCREEN_USAGE = "screen_usage";
    private static String SIEMPO_DEFAULT = "siempo_default";
    private static String SUPPRESSED_NOTIFICATION = "suppressed_notification";
    private static String TEMPO = "tempo";

    //Attribute
    private String SCREEN_NAME = "screen_name";
    private String TIME_SPENT = "time_spent";
    private String APPLICATION_NAME = "application_name";
    private String MENU_NAME = "menu_name";
    private String INTENT_FROM = "intent_from";
    private String ACTION = "action";
    private String IF_DATA = "if_data";
    private String SUPPRESSED_COUNT = "suppressed_count";
    private String TEMPO_INTERVAL_ONLY_AT = "tempo_interval_onlyat";
    private String TEMPO_INTERVAL = "tempo_interval";
    private String TEMPO_TYPE = "tempo_type";


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
        long longDifference = getTime(startTime, System.currentTimeMillis());
        if (longDifference != 0) {
            Bundle bundle = new Bundle();
            bundle.putString(SCREEN_NAME, screenName);
            bundle.putLong(TIME_SPENT, longDifference);
            Tracer.d("Firebase:" + SCREEN_USAGE + ": " + bundle.toString());
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
        Tracer.d("Firebase:" + SUPPRESSED_NOTIFICATION + ": " + bundle.toString());
        getFirebaseAnalytics().logEvent(SUPPRESSED_NOTIFICATION, bundle);
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
                Tracer.d("Firebase:" + SIEMPO_DEFAULT + ": " + bundle.toString());
                getFirebaseAnalytics().logEvent(SIEMPO_DEFAULT, bundle);
            }
        } else {
            Tracer.d("Firebase:" + SIEMPO_DEFAULT + ": " + bundle.toString());
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

    /**
     * Used for Log the interval and time stamp when change the tempo setting.
     *
     * @param tempoType             shows the user selected type of tempo 0 for individual,1 batch,2 OnlyAt
     * @param tempo_interval        which used for 0 and 1 tempo type option.
     * @param tempo_interval_onlyat used for 2 tempo type option.
     */
    public void logTempoIntervalTime(int tempoType, int tempo_interval, String tempo_interval_onlyat) {
        Bundle bundle = new Bundle();
        bundle.putInt(TEMPO_TYPE, tempoType);
        if (tempoType == 2) {
            bundle.putString(TEMPO_INTERVAL_ONLY_AT, tempo_interval_onlyat);
        } else {
            bundle.putInt(TEMPO_INTERVAL, tempo_interval);
        }
        Tracer.d("Firebase:" + TEMPO + ": " + bundle.toString());
        //  getFirebaseAnalytics().logEvent(TEMPO, bundle);
    }

}
