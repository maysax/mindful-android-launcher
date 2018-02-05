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
    public static final String IS_INTENTION_ENABLE = "isIntentionEnable";
    public static final String DEFAULT_INTENTION = "defaultIntention";
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
        editor.commit();
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
        editor.commit();
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
        editor.commit();
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
        editor.commit();
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
        editor.commit();
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
