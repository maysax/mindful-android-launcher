package co.siempo.phone.app;

import android.Manifest;

/**
 * Created by tkb on 2017-04-26.
 */

public class Constants {


    public static String[] PERMISSIONS = {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.CAMERA,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.RECEIVE_MMS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE};

    public static final String WHATSAPP_PACKAGE = "com.whatsapp";
    public static final String GOOGLE_CALENDAR_PACKAGES = "com.google.android.calendar";
    public static final String FACEBOOK_PACKAGE = "com.facebook.katana";
    public static final String FACEBOOK_MESSENGER_PACKAGE = "com.facebook.orca";
    public static final String FACEBOOK_LITE_PACKAGE = "com.facebook.mlite";
    public static final String GOOGLE_HANGOUTS_PACKAGES = "com.google.android.talk";


    public static final String GOOGLE_MAP_PACKAGE = "com.google.android.apps.maps";
    public static final String GOOGLE_PHOTOS = "com.google.android.apps.photos";
    public static final String GOOGLE_CAMERA = " com.android.camera";
    public static final String CALL_APP_PACKAGE = "com.google.android.dialer";
    public static final String GOOGLE_GMAIL_PACKAGE = "com.google.android.gm";
    public static final String CONTACT_APP_PACKAGE = "com.android.contacts";
    public static final String SETTINGS_APP_PACKAGE = "com.android.settings";

    public static final int DEFAULT_TEMPO_MINUTE = 15;
    public static final String[] CALL_APP_PACKAGES = new String[]{"com.google.android.dialer", "com.android.dialer"};
    public static final String[] CALENDAR_APP_PACKAGES = new String[]{"com.google.android.calendar"};
    public static final String[] CLOCK_APP_PACKAGES = new String[]{"com.google.android.deskclock", "com.asus.deskclock", "com.sonyericsson.organizer", "com.sonyericsson.organizer.Organizer_WorldClock",
            "com.sec.android.app.clockpackage", "com.motorola.blur.alarmclock.AlarmClock", "com.android.deskclock.DeskClock", "com.android.deskclock",
            "com.htc.android.worldclock", "com.htc.android.worldclock.WorldClockTabControl"};


    public static final int CALL_PACKAGE = 2;
    public static final int MESSAGE_PACKAGE = 1;
    public static final int CALENDER_PACKAGE = 20;
    public static final int CONTACT_PACKAGE = 3;
    public static final int MAP_PACKAGE = 11;
    public static final int NOTES_PACKAGE = 6;
    public static final int PHOTOS_PACKAGE = 22;
    public static final int CAMERA_PACKAGE = 23;
    public static final int BROWSER_PACKAGE = 24;
    public static final int CLOCK_PACKAGE = 21;
    public static final int EMAIL_PACKAGE = 16;
    public static final String WHATSAPP = "WhatsApp";
    public static final String RISALATAN = "رسالتان";
}
