package co.siempo.phone.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by rajeshjadi on 2/2/18.
 * This class is used to store the local preference data.
 */

public class PrefSiempo {
    //Key Names

    // This field is used for show/hide the IF
    public static final String IS_INTENTION_ENABLE = "isIntentionEnable";

    // This field is used to store IF data.
    public static final String DEFAULT_INTENTION = "defaultIntention";

    // This field is used to store check application installed first time or not.
    public static final String IS_APP_INSTALLED_FIRSTTIME = "is_app_installed_firsttime";

    // This field is used for icon branding : True to show siempo icon and False for default application icon
    public static final String IS_ICON_BRANDING = "is_icon_branding";

    // This field is used for App icons will have a different position each time you open the junk-food menu
    public static final String IS_RANDOMIZE_JUNKFOOD = "is_randomize_junkfood";

    // This field is used for to store tools pane visible/hide and its connected application.
    public static final String TOOLS_SETTING = "tools_setting";

    // This field is used for to store junkfood application package name.
    public static final String JUNKFOOD_APPS = "junkfood_apps";
    //
    //This field is used for to store search List.
    public static final String SEARCH_LIST = "searchList";

    //
    public static final String SORTED_MENU = "sortedMenu";


    //Launcher 3 preferences
    public static final String IS_PAUSE_ACTIVE = "isPauseActive";
    public static final String IS_PAUSE_ALLOW_FAVORITE_CHECKED =
            "isPauseAllowFavoriteChecked";
    public static final String IS_PAUSE_ALLOW_CALLS_CHECKED =
            "isPauseAllowCallsChecked";
    public static final String IS_NOTIFICATION_BLOCKER_RUNNING =
            "isNotificationBlockerRunning";
    public static final String IS_TEMPO_ACTIVE = "isTempoActive";
    public static final String TEMPO_NEXT_NOTIFICATION_MILLIS =
            "tempoNextNotificationMillis";
    public static final String TEMPO_ALLOW_FAVORITES = "tempoAllowFavorites";
    public static final String TEMPO_ALLOW_CALLS = "tempoAllowCalls";
    public static final String TEMPO_INTERVAL_MINUTES = "tempoIntervalMinutes";
    public static final String IS_AWAYCHECKED = "isAwayChecked";
    public static final String TIME = "time";
    public static final String UPDATE_PROMPT = "updatePrompt";
    public static final String IS_APPINSTALLED_FIRST_TIME =
            "isAppInstalledFirstTime";
    public static final String IS_KEYBOARD_DISPLAY = "isKeyBoardDisplay";
    public static final String IS_APP_DEFAULT_OR_FRONT = "isAppDefaultOrFront";
    public static final String GET_CURRENT_VERSION = "getCurrentVersion";
    public static final String IS_PERMISSION_GIVEN_AND_CONTINUED =
            "isPermissionGivenAndContinued";


    //DroidPrefs
    public static final String SELECTED_THEME_COLOR = "selectedThemeColor";
    public static final String SELECTED_THEME_ID = "selectedThemeId";
    public static final String NOTIFICATION_SCHEDULE_INDEX =
            "notificationScheduleIndex";
    public static final String NOTIFICATION_SCHEDULER_VALUE =
            "notificationSchedulerValue";
    public static final String NOTIFICATION_SCHEULER_NEXT_MILLIS =
            "notificationScheulerNextMillis";
    public static final String NOTIFICATION_SCHEDULER_SUPRESSCALLS =
            "notificationSchedulerSupressCalls";
    public static final String NOTIFICATION_SCHEDULER_SUPRESS_SMS =
            "notificationSchedulerSupressSMS";
    public static final String IS_FLOW_RUNNING = "isFlowRunning";
    public static final String FLOW_MAX_TIME_LIMIT_MILLIS =
            "flowMaxTimeLimitMillis";
    public static final String FLOW_SEGMENT_DURATION_MILLIS =
            "flowSegmentDurationMillis";
    public static final String IS_NOTIFICATION_SUPRESSED =
            "isNotificationSupressed";
    public static final String IS_NOTIFICATION_SCHEDULER_ENABLED =
            "isNotificationSchedulerEnabled";
    public static final String IS_SIEMPO_NOTIFICATION_SERVICE_RUNNING =
            "isSiempoNotificationServiceRunning";
    public static final String HAS_SHOWN_INTRO_SCREEN = "hasShownIntroScreen";
    public static final String IS_MENU_GRID = "isMenuGrid";
    public static final String IS_GRID = "isGrid";
    public static final String IS_APP_UPDATED = "isAppUpdated";
    public static final String CALL_PACKAGE = "callPackage";
    public static final String IS_CALL_CLICKED = "isCallClicked";
    public static final String IS_CALLCLICKED_FIRST_TIME =
            "isCallClickedFirstTime";
    public static final String MESSAGE_PACKAGE = "messagePackage";
    public static final String IS_MESSAGE_CLICKED = "isMessageClicked";
    public static final String IS_MESSAGE_CLICKED_FIRST_TIME =
            "isMessageClickedFirstTime";
    public static final String CALENDER_PACKAGE = "calenderPackage";
    public static final String IS_CALENDER_CLICKED = "isCalenderClicked";
    public static final String CONTACT_PACKAGE = "contactPackage";
    public static final String IS_CONTACT_CLICKED = "isContactClicked";
    public static final String MAP_PACKAGE = "mapPackage";
    public static final String IS_MAP_CLICKED = "isMapClicked";
    public static final String PHOTOS_PACKAGE = "photosPackage";
    public static final String IS_PHOTOS_CLICKED = "isPhotosClicked";
    public static final String CAMERA_PACKAGE = "cameraPackage";
    public static final String IS_CAMERA_CLICKED = "isCameraClicked";
    public static final String BROWSER_PACKAGE = "browserPackage";
    public static final String IS_BROWSER_CLICKED = "isBrowserClicked";
    public static final String CLOCK_PACKAGE = "clockPackage";
    public static final String IS_CLOCK_CLICKED = "isClockClicked";
    public static final String EMAIL_PACKAGE = "emailPackage";
    public static final String NOTES_PACKAGE = "notesPackage";
    public static final String IS_EMAIL_CLICKED = "isEmailClicked";
    public static final String IS_EMAILCLICKED_FIRST_TIME =
            "isEmailClickedFirstTime";
    public static final String IS_FACEBOOK_ALLOWED = "isFacebookAllowed";
    public static final String IS_FACEBOOKMESSANGER_ALLOWED =
            "isFacebooKMessangerAllowed";
    public static final String IS_FACEBOOKMESSANGERLITE_ALLOWED =
            "isFacebooKMessangerLiteAllowed";
    public static final String IS_WHATSAPP_ALLOWED = "isWhatsAppAllowed";
    public static final String IS_HANGOUT_ALLOWED = "isHangOutAllowed";
    public static final String IS_ALPHA_SETTING_ENABLE = "isAlphaSettingEnable";
    public static final String IS_FIREBASE_ANALYTICS_ENABLE =
            "isFireBaseAnalyticsEnable";
    public static final String IS_TEMPO_NOTIFICATION_CONTROLS_DISABLED =
            "isTempoNotificationControlsDisabled";
    public static final String TEMPO_TYPE = "tempoType";
    public static final String BATCH_TIME = "batchTime";
    public static final String ONLY_AT = "onlyAt";
    public static final String TEMPO_SOUNDPROFILE = "tempoSoundProfile";
    public static final String USER_EMAILID = "userEmailId";






    private static final PrefSiempo ourInstance = new PrefSiempo();
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private PrefSiempo() {
        //prevent creating multiple instances by making the constructor private
    }

    //The context passed into the getInstance should be application level context.
    public static PrefSiempo getInstance(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
        return ourInstance;
    }

    /**
     * Store the boolean data in local preference
     * e.g PrefSiempo.getInstance(this).write(PrefSiempo.Key,value);
     *
     * @param key   name to store in preference
     * @param value user provided value
     */
    public void write(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Retrieve the boolean data from local preference
     * e.g PrefSiempo.getInstance(this).read(PrefSiempo.Key,defValue);
     *
     * @param key      name of retrieve from preference
     * @param defValue user provided default value
     */
    public boolean read(String key, boolean defValue) {
        return sharedPreferences.getBoolean(key, defValue);
    }

    /**
     * Store the boolean data in local preference
     * e.g PrefSiempo.getInstance(this).write(PrefSiempo.Key,value);
     *
     * @param key   name to store in preference
     * @param value user provided value
     */
    public void write(String key, float value) {
        editor.putFloat(key, value);
        editor.apply();
    }

    /**
     * Retrieve the float data from local preference
     * e.g PrefSiempo.getInstance(this).read(PrefSiempo.Key,defValue);
     *
     * @param key      name of retrieve from preference
     * @param defValue user provided default value
     */
    public float read(String key, float defValue) {
        return sharedPreferences.getFloat(key, defValue);
    }

    /**
     * Store the boolean data in local preference
     * e.g PrefSiempo.getInstance(this).write(PrefSiempo.Key,value);
     *
     * @param key   name to store in preference
     * @param value user provided value
     */
    public void write(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * Retrieve the int data from local preference
     * e.g PrefSiempo.getInstance(this).read(PrefSiempo.Key,defValue);
     *
     * @param key      name of retrieve from preference
     * @param defValue user provided default value
     */
    public float read(String key, int defValue) {
        return sharedPreferences.getInt(key, defValue);
    }

    /**
     * Store the boolean data in local preference
     * e.g PrefSiempo.getInstance(this).write(PrefSiempo.Key,value);
     *
     * @param key   name to store in preference
     * @param value user provided value
     */
    public void write(String key, long value) {
        editor.putLong(key, value);
        editor.apply();
    }

    /**
     * Retrieve the long data from local preference
     * e.g PrefSiempo.getInstance(this).read(PrefSiempo.Key,defValue);
     *
     * @param key      name of retrieve from preference
     * @param defValue user provided default value
     */
    public long read(String key, long defValue) {
        return sharedPreferences.getLong(key, defValue);
    }

    /**
     * Store the boolean data in local preference
     * e.g PrefSiempo.getInstance(this).write(PrefSiempo.Key,value);
     *
     * @param key   name to store in preference
     * @param value user provided value
     */
    public void write(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Retrieve the String data from local preference
     * e.g PrefSiempo.getInstance(this).read(PrefSiempo.Key,defValue);
     *
     * @param key      name of retrieve from preference
     * @param defValue user provided default value
     */
    public String read(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    /**
     * Store the boolean data in local preference
     * e.g PrefSiempo.getInstance(this).write(PrefSiempo.Key,value);
     *
     * @param key   name to store in preference
     * @param value user provided value
     */
    public void write(String key, Set<String> value) {
        editor.putStringSet(key, value);
        editor.commit();
    }

    /**
     * Retrieve the Set<String> data from local preference
     * e.g PrefSiempo.getInstance(this).read(PrefSiempo.Key,defValue);
     *
     * @param key      name of retrieve from preference
     * @param defValue user provided default value
     */
    public Set<String> read(String key, Set<String> defValue) {
        return sharedPreferences.getStringSet(key, defValue);
    }

    /**
     * This method is used to delete object from preference.
     * e.g PrefSiempo.getInstance(this).remove(PrefSiempo.Key);
     *
     * @param key user provided key.
     */
    public void remove(String key) {
        editor.remove(key);
        editor.commit();
    }

    /**
     * User to clear all local preference data.
     * e.g PrefSiempo.getInstance(this).clearAll();
     */
    public void clearAll() {
        editor.clear();
        editor.commit();
    }
}
