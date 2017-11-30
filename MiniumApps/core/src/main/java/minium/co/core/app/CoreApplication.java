package minium.co.core.app;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.UserManager;
import android.os.Vibrator;
import android.provider.AlarmClock;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.crashlytics.android.Crashlytics;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.fabric.sdk.android.BuildConfig;
import io.fabric.sdk.android.Fabric;
import minium.co.core.R;
import minium.co.core.config.Config;
import minium.co.core.event.AppInstalledEvent;
import minium.co.core.log.LogConfig;
import minium.co.core.log.Tracer;
import minium.co.core.ui.LifecycleHandler;
import minium.co.core.util.FontUtils;
import minium.co.core.util.UIUtils;
import minium.co.core.util.UserHandle;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Each application should contain an {@link Application} class instance
 * All applications of this project should extend their own application from this class
 * This will be first class where we can initialize all necessary first time configurations
 * <p>
 * Created by shahab on 3/17/16.
 */

public abstract class CoreApplication extends MultiDexApplication {

    private final String TRACE_TAG = LogConfig.TRACE_TAG + "CoreApplication";

    private static CoreApplication sInstance;

    public static synchronized CoreApplication getInstance() {
        return sInstance;
    }

    private RefWatcher refWatcher;
    public boolean siempoBarLaunch = true;
    UserManager userManager;
    LauncherApps launcherApps;

    private boolean isCallisRunning = false;

    public boolean isIfScreen = false;

    public String DISABLE_APPLIST="DISABLE_APPLIST";
    public String SOCIAL_DISABLE_COUNT="SOCIAL_DISABLE_COUNT";
    public String MESSENGER_DISABLE_COUNT="MESSENGER_DISABLE_COUNT";
    public String APP_DISABLE_COUNT="APP_DISABLE_COUNT";
    public String HEADER_APPLIST="HEADER_APPLIST";

    private List<ApplicationInfo> packagesList = new ArrayList<>();
    public HashMap<String, Bitmap> iconList = new HashMap<>();
    Handler handler;
    // include the vibration pattern when call ringing
    private Vibrator vibrator;
    long[] pattern = {0, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500};

    SharedPreferences sharedPref;
    private ArrayList<String> silentList = new ArrayList<>();
    private ArrayList<String> vibrateList = new ArrayList<>();
    private ArrayList<String> normalModeList = new ArrayList<>();
    private ArrayList<ResolveInfo> callPackageList = new ArrayList<>();
    private ArrayList<ResolveInfo> messagePackageList = new ArrayList<>();
    private ArrayList<ResolveInfo> calenderPackageList = new ArrayList<>();
    private ArrayList<ResolveInfo> contactPackageList = new ArrayList<>();
    private ArrayList<ResolveInfo> mapPackageList = new ArrayList<>();
    private ArrayList<ResolveInfo> photosPackageList = new ArrayList<>();
    private ArrayList<ResolveInfo> cameraPackageList = new ArrayList<>();
    private ArrayList<ResolveInfo> browserPackageList = new ArrayList<>();
    private ArrayList<ResolveInfo> clockPackageList = new ArrayList<>();
    private ArrayList<ResolveInfo> emailPackageList = new ArrayList<>();
    private ArrayList<ResolveInfo> notesPackageList = new ArrayList<>();
    private boolean isEditNotOpen = false;
    AudioManager audioManager;
    NotificationManager notificationManager;


    public boolean isEditNotOpen() {
        return isEditNotOpen;
    }

    public void setEditNotOpen(boolean editNotOpen) {
        isEditNotOpen = editNotOpen;
    }

    public void setmMediaPlayer(MediaPlayer mMediaPlayer) {
        this.mMediaPlayer = mMediaPlayer;

    }

    public MediaPlayer mMediaPlayer;

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public boolean isCallisRunning() {
        return isCallisRunning;
    }

    public void setCallisRunning(boolean callisRunning) {
        isCallisRunning = callisRunning;
    }

    public Vibrator getVibrator() {
        return vibrator;
    }

    public void setVibrator(Vibrator vibrator) {
        this.vibrator = vibrator;
    }


    public ArrayList<ResolveInfo> getCallPackageList() {
        return callPackageList;
    }

    public void setCallPackageList(ArrayList<ResolveInfo> callPackageList) {
        this.callPackageList = callPackageList;
    }

    public ArrayList<ResolveInfo> getMessagePackageList() {
        return messagePackageList;
    }

    public void setMessagePackageList(ArrayList<ResolveInfo> messagePackageList) {
        this.messagePackageList = messagePackageList;
    }

    public ArrayList<ResolveInfo> getCalenderPackageList() {
        return calenderPackageList;
    }

    public void setCalenderPackageList(ArrayList<ResolveInfo> calenderPackageList) {
        this.calenderPackageList = calenderPackageList;
    }

    public ArrayList<ResolveInfo> getContactPackageList() {
        return contactPackageList;
    }

    public void setContactPackageList(ArrayList<ResolveInfo> contactPackageList) {
        this.contactPackageList = contactPackageList;
    }

    public ArrayList<ResolveInfo> getMapPackageList() {
        return mapPackageList;
    }

    public void setMapPackageList(ArrayList<ResolveInfo> mapPackageList) {
        this.mapPackageList = mapPackageList;
    }

    public ArrayList<ResolveInfo> getPhotosPackageList() {
        return photosPackageList;
    }

    public void setPhotosPackageList(ArrayList<ResolveInfo> photosPackageList) {
        this.photosPackageList = photosPackageList;
    }

    public ArrayList<ResolveInfo> getCameraPackageList() {
        return cameraPackageList;
    }

    public void setCameraPackageList(ArrayList<ResolveInfo> cameraPackageList) {
        this.cameraPackageList = cameraPackageList;
    }

    public ArrayList<ResolveInfo> getBrowserPackageList() {
        return browserPackageList;
    }

    public void setBrowserPackageList(ArrayList<ResolveInfo> browserPackageList) {
        this.browserPackageList = browserPackageList;
    }

    public ArrayList<ResolveInfo> getClockPackageList() {
        return clockPackageList;
    }

    public void setClockPackageList(ArrayList<ResolveInfo> clockPackageList) {
        this.clockPackageList = clockPackageList;
    }

    public ArrayList<ResolveInfo> getEmailPackageList() {
        return emailPackageList;
    }

    public void setEmailPackageList(ArrayList<ResolveInfo> emailPackageList) {
        this.emailPackageList = emailPackageList;
    }

    public ArrayList<ResolveInfo> getNotesPackageList() {
        return notesPackageList;
    }

    public void setNotesPackageList(ArrayList<ResolveInfo> notesPackageList) {
        this.notesPackageList = notesPackageList;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPref = getSharedPreferences("DroidPrefs", 0);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        refWatcher = LeakCanary.install(this);
        userManager = (UserManager) getSystemService(Context.USER_SERVICE);
        launcherApps = (LauncherApps) getSystemService(Context.LAUNCHER_APPS_SERVICE);
        sInstance = this;
        init();
        getAllApplicationPackageName();


    }

    /**
     * This method is used for fetch all installed application package list.
     */
    public void getAllApplicationPackageName() {
        iconList.clear();
        packagesList.clear();
        new LoadApplications().execute();
    }


    public void restoreDefaultApplication() {
        String callPackage = CoreApplication.getInstance().getCallPackageName();
        if (!sharedPref.getString("callPackage", "").equalsIgnoreCase("")
                && !UIUtils.isAppInstalled(this, sharedPref.getString("callPackage", ""))) {
            sharedPref.edit().putString("callPackage", callPackage).apply();
        }
        if (sharedPref.getString("callPackage", "").equalsIgnoreCase("")) {
            sharedPref.edit().putString("callPackage", callPackage).apply();
        }

        String messagePackage = CoreApplication.getInstance().getMessagePackageName();
        if (!sharedPref.getString("messagePackage", "").equalsIgnoreCase("")
                && !UIUtils.isAppInstalled(this, sharedPref.getString("messagePackage", ""))) {
            sharedPref.edit().putString("messagePackage", messagePackage).apply();
        }
        if (sharedPref.getString("messagePackage", "").equalsIgnoreCase("")) {
            sharedPref.edit().putString("messagePackage", messagePackage).apply();
        }

        String calenderPackage = CoreApplication.getInstance().getCalenderPackageName();
        if (!sharedPref.getString("calenderPackage", "").equalsIgnoreCase("")
                && !UIUtils.isAppInstalled(this, sharedPref.getString("calenderPackage", ""))) {
            sharedPref.edit().putString("calenderPackage", calenderPackage).apply();
        }
        if (sharedPref.getString("calenderPackage", "").equalsIgnoreCase("")) {
            sharedPref.edit().putString("calenderPackage", calenderPackage).apply();
        }

        String contactPackage = CoreApplication.getInstance().getContactPackageName();
        if (!sharedPref.getString("contactPackage", "").equalsIgnoreCase("")
                && !UIUtils.isAppInstalled(this, sharedPref.getString("contactPackage", ""))) {
            sharedPref.edit().putString("contactPackage", contactPackage).apply();
        }
        if (sharedPref.getString("contactPackage", "").equalsIgnoreCase("")) {
            sharedPref.edit().putString("contactPackage", contactPackage).apply();
        }

        String mapPackage = CoreApplication.getInstance().getMapPackageName();
        if (!sharedPref.getString("mapPackage", "").equalsIgnoreCase("")
                && !UIUtils.isAppInstalled(this, sharedPref.getString("mapPackage", ""))) {
            sharedPref.edit().putString("mapPackage", mapPackage).apply();
        }
        if (sharedPref.getString("mapPackage", "").equalsIgnoreCase("")) {
            sharedPref.edit().putString("mapPackage", mapPackage).apply();
        }

        String photosPackage = CoreApplication.getInstance().getPhotosPackageName();
        if (!sharedPref.getString("photosPackage", "").equalsIgnoreCase("")
                && !UIUtils.isAppInstalled(this, sharedPref.getString("photosPackage", ""))) {
            sharedPref.edit().putString("photosPackage", photosPackage).apply();
        }
        if (sharedPref.getString("photosPackage", "").equalsIgnoreCase("")) {
            sharedPref.edit().putString("photosPackage", photosPackage).apply();
        }

        String cameraPackage = CoreApplication.getInstance().getCameraPackageName();
        if (!sharedPref.getString("cameraPackage", "").equalsIgnoreCase("")
                && !UIUtils.isAppInstalled(this, sharedPref.getString("cameraPackage", ""))) {
            sharedPref.edit().putString("cameraPackage", cameraPackage).apply();
        }
        if (sharedPref.getString("cameraPackage", "").equalsIgnoreCase("")) {
            sharedPref.edit().putString("cameraPackage", cameraPackage).apply();
        }


        String browserPackage = CoreApplication.getInstance().getBrowserPackageName();
        if (!sharedPref.getString("browserPackage", "").equalsIgnoreCase("")
                && !UIUtils.isAppInstalled(this, sharedPref.getString("browserPackage", ""))) {
            sharedPref.edit().putString("browserPackage", browserPackage).apply();
        }
        if (sharedPref.getString("browserPackage", "").equalsIgnoreCase("")) {
            sharedPref.edit().putString("browserPackage", browserPackage).apply();
        }


        String clockPackage = CoreApplication.getInstance().getClockPackageName();
        if (!sharedPref.getString("clockPackage", "").equalsIgnoreCase("")
                && !UIUtils.isAppInstalled(this, sharedPref.getString("clockPackage", ""))) {
            sharedPref.edit().putString("clockPackage", clockPackage).apply();
        }
        if (sharedPref.getString("clockPackage", "").equalsIgnoreCase("")) {
            sharedPref.edit().putString("clockPackage", clockPackage).apply();
        }

        String emailPackage = CoreApplication.getInstance().getMailPackageName();
        if (!sharedPref.getString("emailPackage", "").equalsIgnoreCase("")
                && !UIUtils.isAppInstalled(this, sharedPref.getString("emailPackage", ""))) {
            sharedPref.edit().putString("emailPackage", emailPackage).apply();
        }
        if (sharedPref.getString("emailPackage", "").equalsIgnoreCase("")) {
            sharedPref.edit().putString("emailPackage", emailPackage).apply();
        }

        String notesPackage = CoreApplication.getInstance().getNotesPackageName();
        if (!sharedPref.getString("notesPackage", "").equalsIgnoreCase("")
                && !sharedPref.getString("notesPackage", "").equalsIgnoreCase("Notes")
                && !UIUtils.isAppInstalled(this, sharedPref.getString("notesPackage", ""))) {
            sharedPref.edit().putString("notesPackage", notesPackage).apply();
        }
        if (sharedPref.getString("notesPackage", "").equalsIgnoreCase("")) {
            sharedPref.edit().putString("notesPackage", notesPackage).apply();
        }
    }


    protected void init() {
        // set initial configurations here
        configTracer();
        configCalligraphy();
        configFabric();
        configIconify();
        configureLifecycle();
        configureNetworking();
    }

    private void configureNetworking() {
        AndroidNetworking.initialize(getApplicationContext());
    }

    private void configureLifecycle() {
        registerActivityLifecycleCallbacks(new LifecycleHandler());
    }

    private void configTracer() {
        Tracer.init();
    }

    private void configCalligraphy() {
        CalligraphyConfig
                .initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath(getString(FontUtils.DEFAULT_FONT_PATH_RES))
                        .setFontAttrId(R.attr.fontPath)
                        .build());
    }

    private void configFabric() {
        if (!BuildConfig.DEBUG) {
            final Fabric fabric = new Fabric.Builder(this)
                    .kits(new Crashlytics())
                    .debuggable(Config.DEBUG)
                    .build();
            Fabric.with(fabric);
        }
    }


    private void configIconify() {
        Iconify.with(new FontAwesomeModule());
    }

    public static RefWatcher getRefWatcher(Context context) {
        CoreApplication application = (CoreApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    public List<ApplicationInfo> getPackagesList() {
        return packagesList;
    }

    public void setPackagesList(List<ApplicationInfo> packagesList) {
        Collections.sort(packagesList, new Comparator<ApplicationInfo>() {
            public int compare(ApplicationInfo v1, ApplicationInfo v2) {

                return (v1.name.toLowerCase()).compareTo(v2.name.toLowerCase());
            }
        });
        this.packagesList = packagesList;
    }

    /**
     * Return the application name by providing it's package name.
     *
     * @param packagename
     * @return application name
     */
    public String getApplicationNameFromPackageName(String packagename) {
        if (packagename != null && !packagename.equalsIgnoreCase("")) {
            for (ApplicationInfo applicationInfo : getPackagesList()) {
                if (applicationInfo.packageName.equalsIgnoreCase(packagename)) {
                    return applicationInfo.name;
                }
            }
        }
        return "";
    }

    public ArrayList<String> getSilentList() {
        return silentList;
    }

    public void setSilentList(ArrayList<String> silentList) {
        this.silentList = silentList;
    }

    public ArrayList<String> getVibrateList() {
        return vibrateList;
    }

    public void setVibrateList(ArrayList<String> vibrateList) {
        this.vibrateList = vibrateList;
    }

    public ArrayList<String> getNormalModeList() {
        return normalModeList;
    }

    public void setNormalModeList(ArrayList<String> normalModeList) {
        this.normalModeList = normalModeList;
    }

    public void changeProfileToNormalMode() {
        int currentMode = audioManager.getRingerMode();
        if (currentMode != AudioManager.RINGER_MODE_NORMAL) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                    && !notificationManager.isNotificationPolicyAccessGranted()) {
            } else {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            }
        }

    }

    public void changeProfileToVibrateMode() {
        int currentMode = audioManager.getRingerMode();
        if (currentMode != AudioManager.RINGER_MODE_VIBRATE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                    && !notificationManager.isNotificationPolicyAccessGranted()) {
            } else {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            }
        }
    }

    public void changeProfileToSilentMode() {
        int currentMode = audioManager.getRingerMode();
        if (currentMode != AudioManager.RINGER_MODE_SILENT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                    && !notificationManager.isNotificationPolicyAccessGranted()) {
            } else {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }
        }

    }

    private class LoadApplications extends AsyncTask<Object, Object, List<ApplicationInfo>> {

        @Override
        protected List<ApplicationInfo> doInBackground(Object... params) {
            List<ApplicationInfo> applist = new ArrayList<>();
            for (android.os.UserHandle profile : userManager.getUserProfiles()) {
                UserHandle user = new UserHandle(userManager.getSerialNumberForUser(profile), profile);
                for (LauncherActivityInfo activityInfo : launcherApps.getActivityList(null, profile)) {
                    ApplicationInfo appInfo = activityInfo.getApplicationInfo();
                    appInfo.name = activityInfo.getLabel().toString();
                    String defSMSApp = Settings.Secure.getString(getContentResolver(), "sms_default_application");
                    String defDialerApp = Settings.Secure.getString(getContentResolver(), "dialer_default_application");
                    if (appInfo.packageName.equalsIgnoreCase(defSMSApp)
                            || appInfo.packageName.contains("com.google.android.calendar")
                            || appInfo.packageName.contains("com.whatsapp")
                            || appInfo.packageName.contains("com.facebook.orca")
                            || appInfo.packageName.contains("com.google.android.talk")
                            || appInfo.packageName.contains("com.facebook.mlite")
                            ) {
                        getVibrateList().add(appInfo.packageName);
                    } else if (appInfo.packageName.contains("telecom") || appInfo.packageName.contains("dialer")) {
                        getNormalModeList().add(appInfo.packageName);
                    } else {
                        getSilentList().add(appInfo.packageName);
                    }
                    if (!appInfo.packageName.equalsIgnoreCase("co.siempo.phone")) {
                        Drawable drawable;
                        try {
                            Resources resourcesForApplication = getPackageManager().getResourcesForApplication(appInfo);
                            Configuration config = resourcesForApplication.getConfiguration();
                            Configuration originalConfig = new Configuration(config);

                            DisplayMetrics displayMetrics = resourcesForApplication.getDisplayMetrics();
                            DisplayMetrics originalDisplayMetrics = resourcesForApplication.getDisplayMetrics();
                            displayMetrics.densityDpi = DisplayMetrics.DENSITY_HIGH;
                            resourcesForApplication.updateConfiguration(config, displayMetrics);
                            if (appInfo.icon != 0) {
                                drawable = resourcesForApplication.getDrawable(appInfo.icon, null);
                            } else {
                                drawable = appInfo.loadIcon(getPackageManager());
                            }
                            resourcesForApplication.updateConfiguration(originalConfig, originalDisplayMetrics);
                        } catch (PackageManager.NameNotFoundException e) {
                            Log.e("check", "error getting Hi Res Icon :", e);
                            drawable = appInfo.loadIcon(getPackageManager());
                        }
                        Bitmap bitmap = drawableToBitmap(drawable);
                        if (!TextUtils.isEmpty(activityInfo.getApplicationInfo().packageName)) {
                            iconList.put(activityInfo.getApplicationInfo().packageName, bitmap);
                        }
                        applist.add(appInfo);
                    }
                }
            }
            return applist;
        }


        @Override
        protected void onPostExecute(List<ApplicationInfo> applicationInfos) {
            super.onPostExecute(applicationInfos);
            packagesList.clear();
            setPackagesList(applicationInfos);
            EventBus.getDefault().post(new AppInstalledEvent(true));
        }

        private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
            ArrayList<ApplicationInfo> applist = new ArrayList<>();
            for (ApplicationInfo info : list) {
                try {
                    if (null != getPackageManager().getLaunchIntentForPackage(info.packageName) && isSystemPackage(info)) {
                        applist.add(info);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return applist;
        }

        private boolean isSystemPackage(ApplicationInfo packageInfo) {
            return ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
        }

        Bitmap drawableToBitmap(Drawable drawable) {
            Bitmap bitmap;

            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                if (bitmapDrawable.getBitmap() != null) {
                    return bitmapDrawable.getBitmap();
                }
            }

            if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }

    }

    public void playAudio() {
        try {
            if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
                Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                if (mMediaPlayer == null) {
                    mMediaPlayer = new MediaPlayer();
                    mMediaPlayer.setDataSource(this, alert);
                    final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) != 0) {
                        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mMediaPlayer.setVolume(100, 100);
                        mMediaPlayer.setScreenOnWhilePlaying(true);
                        mMediaPlayer.prepare();
                        mMediaPlayer.start();
                        vibrator.vibrate(pattern, 0);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * get all default Call application package name
     */
    public String getCallPackageName() {
        Uri number = Uri.parse("tel:");
        Intent dial = new Intent(Intent.ACTION_DIAL, number);
        getCallPackageList().clear();
        getCallPackageList().addAll(getPackageManager().queryIntentActivities(dial, 0));
        for (ResolveInfo res : getCallPackageList()) {
            Log.d("Default App Name", "Call : " + res.activityInfo.name + " :" + res.activityInfo.packageName + " : " + res.activityInfo.name);
            return res.activityInfo.packageName;
        }
        return "";
    }


    /**
     * get all default message application package name
     */
    public String getMessagePackageName() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + ""));
        intent.putExtra("sms_body", "Test text...");
        getMessagePackageList().clear();
        getMessagePackageList().addAll(getPackageManager().queryIntentActivities(intent, 0));
        for (ResolveInfo res : getMessagePackageList()) {
            Log.d("Default App Name", "Message : " + res.activityInfo.packageName + " : " + res.activityInfo.name);
            return res.activityInfo.packageName;
        }
        return "";
    }

    /**
     * get all default Calender application package name
     */
    public String getCalenderPackageName() {
        Intent dial = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse("content://com.android.calendar/time/"));
        getCalenderPackageList().clear();
        getCalenderPackageList().addAll(getPackageManager().queryIntentActivities(dial, 0));
        for (ResolveInfo res : getCalenderPackageList()) {
            Log.d("Default App Name", "Calender : " + res.activityInfo.packageName + " : " + res.activityInfo.name);
            return res.activityInfo.packageName;
        }
        return "";
    }


    /**
     * get all default Contact application package name
     */
    public String getContactPackageName() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        getContactPackageList().clear();
        getContactPackageList().addAll(getPackageManager().queryIntentActivities(intent, 0));
        for (ResolveInfo res : getContactPackageList()) {
            Log.d("Default App Name", "Contact : " + res.activityInfo.packageName + " : " + res.activityInfo.name);
            return res.activityInfo.packageName;
        }
        return "";
    }

    /**
     * get all default Contact application package name
     */
    public String getMapPackageName() {
        Double myLatitude = 44.433106;
        Double myLongitude = 26.103687;
        String labelLocation = "Jorgesys @ Bucharest";
        String urlAddress = "http://maps.google.com/maps?q=" + myLatitude + "," + myLongitude + "(" + labelLocation + ")&iwloc=A&hl=es";
        getMapPackageList().clear();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlAddress));
        getMapPackageList().addAll(getPackageManager().queryIntentActivities(intent, 0));
        for (ResolveInfo res : getMapPackageList()) {
            Log.d("Default App Name", "Map : " + res.activityInfo.packageName + " : " + res.activityInfo.name);
            return res.activityInfo.packageName;
        }
        return "";
    }

    /**
     * get all default Contact application package name
     */
    public String getPhotosPackageName() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/* video/*");
        getPhotosPackageList().clear();
        getPhotosPackageList().addAll(getPackageManager().queryIntentActivities(pickIntent, 0));
        for (ResolveInfo res : getPhotosPackageList()) {
            Log.d("Default App Name", "Photos : " + res.activityInfo.packageName + " : " + res.activityInfo.name);
            return res.activityInfo.packageName;
        }
        return "";
    }


    /**
     * get all default Contact application package name
     */
    public String getCameraPackageName() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        getCameraPackageList().clear();
        getCameraPackageList().addAll(getPackageManager().queryIntentActivities(intent, 0));
        for (ResolveInfo res : getCameraPackageList()) {
            Log.d("Default App Name", "Camera : " + res.activityInfo.packageName + " : " + res.activityInfo.name);
            return res.activityInfo.packageName;
        }
        return "";
    }

    /**
     * get all Browser application package name
     */
    public String getBrowserPackageName() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/"));
        getBrowserPackageList().clear();
        getBrowserPackageList().addAll(getPackageManager().queryIntentActivities(intent, 0));
        for (ResolveInfo res : getBrowserPackageList()) {
            Log.d("Default App Name", "Browser : " + res.activityInfo.packageName + " : " + res.activityInfo.name);
            return res.activityInfo.packageName;
        }
        return "";
    }

    /**
     * get all Clock application package name
     */
    public String getClockPackageName() {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        getClockPackageList().clear();
        getClockPackageList().addAll(getPackageManager().queryIntentActivities(intent, 0));
        for (ResolveInfo res : getClockPackageList()) {
            Log.d("Default App Name", "Clock : " + res.activityInfo.packageName + " : " + res.activityInfo.name);
            return res.activityInfo.packageName;
        }
        return "";
    }


    /**
     * get all Mail application package name
     */
    public String getMailPackageName() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("mailto:recipient@example.com?subject=" + "" + "&body=" + "");
        intent.setData(data);
        getEmailPackageList().clear();
        getEmailPackageList().addAll(getPackageManager().queryIntentActivities(intent, 0));
        for (ResolveInfo res : getEmailPackageList()) {
            Log.d("Default App Name", "Mail : " + res.activityInfo.packageName + " : " + res.activityInfo.name);
            return res.activityInfo.packageName;
        }
        return "";
    }

    /**
     * get all Notes application package name
     */
    public String getNotesPackageName() {
        String filepath = "mnt/sdcard/doc.txt";
        File file = new File(filepath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setDataAndType(Uri.fromFile(file), "text/plain");
        getNotesPackageList().clear();
        getNotesPackageList().add(null);
        getNotesPackageList().addAll(getPackageManager().queryIntentActivities(intent, 0));
        for (ResolveInfo res : getNotesPackageList()) {
//            Log.d("Default App Name", "Notes : " + res.activityInfo.packageName + " : " + res.activityInfo.name);
            return res != null ? res.activityInfo.packageName : "Notes";
        }
        return "";
    }


    public void declinePhone() {

        try {
            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";
            Class<?> telephonyClass;
            Class<?> telephonyStubClass;
            Class<?> serviceManagerClass;
            Class<?> serviceManagerNativeClass;
            Method telephonyEndCall;
            Object telephonyObject;
            Object serviceManagerObject;
            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
            Method getService = // getDefaults[29];
                    serviceManagerClass.getMethod("getService", String.class);
            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");
            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
            telephonyObject = serviceMethod.invoke(null, retbinder);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            telephonyEndCall.invoke(telephonyObject);
            Log.d("Testting ", "Testting22222");
        } catch (Exception e) {
            Tracer.d("Decline call exception.." + e.toString());
            e.printStackTrace();
        }
    }

}
