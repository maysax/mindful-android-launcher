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

    // This field is used to store check application installed first time or not.
    public static final String IS_APP_INSTALLED_FIRSTTIME_SHOW_TOOLTIP = "is_app_installed_firsttime_show_tooltip";

    // This field is used to store check application installed first time or not.
    public static final String IS_JUNKFOOD_FIRSTTIME = "is_junkfood_firsttime";

    // This field is used for icon branding : True to show siempo icon and False for default application icon
    public static final String IS_ICON_BRANDING = "is_icon_branding";

    // This field is used for App icons will have a different position each time you open the junk-food menu
    public static final String IS_RANDOMIZE_JUNKFOOD = "is_randomize_junkfood";

    // This field is used for to store tools pane visible/hide and its connected application.
    public static final String TOOLS_SETTING = "tools_setting";

    // This field is used for to store junkfood application package name.
    public static final String JUNKFOOD_APPS = "junkfood_apps";

    // This field is used for to store favorites application package name.
    public static final String FAVORITE_APPS = "favorite_apps";

    // This field is used for to store search List.
    public static final String RECENT_ITEM_LIST = "recentItemList";

    // used for sorting of tools menu.
    public static final String SORTED_MENU = "sortedMenu";

    public static final String FAVORITE_SORTED_MENU = "favoriteSortedMenu";

    // used for Allow peaking.
    public static final String ALLOW_PEAKING = "Allowpeaking";


    //Launcher 3 preferences
    public static final String UPDATE_PROMPT = "updatePrompt";
    public static final String CALL_RUNNING = "CALLRUNNING";
    public static final String IS_APP_DEFAULT_OR_FRONT = "isAppDefaultOrFront";
    //DroidPrefs
    public static final String SELECTED_THEME_ID = "selectedThemeId";
    public static final String FLOW_MAX_TIME_LIMIT_MILLIS =
            "flowMaxTimeLimitMillis";
    public static final String FLOW_SEGMENT_DURATION_MILLIS =
            "flowSegmentDurationMillis";
    public static final String IS_APP_UPDATED = "isAppUpdated";
    public static final String IS_ALPHA_SETTING_ENABLE = "isAlphaSettingEnable";
    public static final String IS_FIREBASE_ANALYTICS_ENABLE =
            "isFireBaseAnalyticsEnable";
    public static final String TEMPO_TYPE = "tempoType";
    public static final String BATCH_TIME = "batchTime";
    public static final String ONLY_AT = "onlyAt";
    public static final String USER_EMAILID = "userEmailId";
    public static final String IS_CONTACT_UPDATE = "isContactUpdate";
    private static final PrefSiempo ourInstance = new PrefSiempo();


    public static String HELPFUL_ROBOTS = "HELPFUL_ROBOTS";
    public static String BLOCKED_APPLIST = "BLOCKED_APPLIST";
    public static String MESSENGER_DISABLE_COUNT = "MESSENGER_DISABLE_COUNT";
    public static String APP_DISABLE_COUNT = "APP_DISABLE_COUNT";
    public static String HEADER_APPLIST = "HEADER_APPLIST";
    public static String TOGGLE_LEFTMENU = "toggle_leftmenu";
    public static String USER_SEEN_EMAIL_REQUEST = "user_seen_email_request";

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
    public int read(String key, int defValue) {
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
        editor.apply();
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
