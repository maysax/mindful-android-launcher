package co.siempo.phone.app;

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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import co.siempo.phone.R;
import co.siempo.phone.event.AppInstalledEvent;
import co.siempo.phone.log.LogConfig;
import co.siempo.phone.log.Tracer;
import co.siempo.phone.models.AppMenu;
import co.siempo.phone.utils.FontUtils;
import co.siempo.phone.utils.LifecycleHandler;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.UIUtils;
import co.siempo.phone.utils.UserHandle;
import de.greenrobot.event.EventBus;
import io.fabric.sdk.android.BuildConfig;
import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Each application should contain an {@link Application} class instance
 * All applications of this project should extend their own application from this class
 * This will be first class where we can initialize all necessary first time configurations
 * <p>
 * Created by shahab on 3/17/16.
 */

public abstract class CoreApplication extends MultiDexApplication {

    private static CoreApplication sInstance;
    private final String TRACE_TAG = LogConfig.TRACE_TAG + "CoreApplication";
    public boolean siempoBarLaunch = true;
    public boolean isIfScreen = false;
    //    public String DISABLE_APPLIST="DISABLE_APPLIST";
//    public String SOCIAL_DISABLE_COUNT="SOCIAL_DISABLE_COUNT";
//    public String MESSENGER_DISABLE_COUNT="MESSENGER_DISABLE_COUNT";
//    public String APP_DISABLE_COUNT="APP_DISABLE_COUNT";
//    public String HEADER_APPLIST="HEADER_APPLIST";
    public String DISABLE_APPLIST = "DISABLE_APPLIST";
    public String BLOCKED_APPLIST = "BLOCKED_APPLIST";
    public HashMap<String, Bitmap> iconList = new HashMap<>();
    public MediaPlayer mMediaPlayer;
    UserManager userManager;
    LauncherApps launcherApps;
    Handler handler;
    long[] pattern = {0, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500};
    SharedPreferences sharedPref;
    AudioManager audioManager;
    NotificationManager notificationManager;
    MediaPlayer notificationMediaPlayer;
    private Crashlytics crashlytics;
    private RefWatcher refWatcher;
    private boolean isCallisRunning = false;
    private List<ApplicationInfo> packagesList = new ArrayList<>();
    // include the vibration pattern when call ringing
    private Vibrator vibrator;
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
    private ArrayList<String> disableNotificationApps = new ArrayList<>();
    private ArrayList<String> blockedApps = new ArrayList<>();

    public static synchronized CoreApplication getInstance() {
        return sInstance;
    }

    public static RefWatcher getRefWatcher(Context context) {
        CoreApplication application = (CoreApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    public Crashlytics getCrashlytics() {
        return crashlytics;
    }

    public boolean isEditNotOpen() {
        return isEditNotOpen;
    }

    public void setEditNotOpen(boolean editNotOpen) {
        isEditNotOpen = editNotOpen;
    }

    public void setMediaPlayerNull() {
        this.mMediaPlayer = null;

    }

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

    public SharedPreferences getSharedPref() {
        return sharedPref;
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
        //audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
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
        configureToolsPane();
    }

    /**
     * first time called when user launch the application to set the default value for the
     * application show/hide bind tools to specific package name.
     */
    private void configureToolsPane() {
        if (PrefSiempo.getInstance(this).read(PrefSiempo.TOOLS_SETTING, "").equalsIgnoreCase("")) {
            HashMap<Integer, AppMenu> map = new HashMap<>();
            //by default on install, the "Recorder", "Payment", and "Browser" tools are hidden
            // (they may be revealed via the tool-selection screen (see tool-selection below)).
            map.put(1, new AppMenu(true, false, CoreApplication.getInstance().getApplicationByCategory(1).size() >= 1 ? CoreApplication.getInstance().getApplicationByCategory(1).get(0).activityInfo.packageName : ""));
            map.put(2, new AppMenu(true, false, CoreApplication.getInstance().getApplicationByCategory(2).size() >= 1 ? CoreApplication.getInstance().getApplicationByCategory(2).get(0).activityInfo.packageName : ""));
            map.put(3, new AppMenu(true, false, CoreApplication.getInstance().getApplicationByCategory(3).size() >= 1 ? CoreApplication.getInstance().getApplicationByCategory(3).get(0).activityInfo.packageName : ""));
            map.put(4, new AppMenu(true, false, CoreApplication.getInstance().getApplicationByCategory(4).size() >= 1 ? CoreApplication.getInstance().getApplicationByCategory(4).get(0).activityInfo.packageName : ""));
            map.put(5, new AppMenu(true, false, getString(R.string.notes)));
            map.put(6, new AppMenu(false, false, CoreApplication.getInstance().getApplicationByCategory(6).size() >= 1 ? CoreApplication.getInstance().getApplicationByCategory(6).get(0).activityInfo.packageName : ""));
            map.put(7, new AppMenu(true, false, CoreApplication.getInstance().getApplicationByCategory(7).size() >= 1 ? CoreApplication.getInstance().getApplicationByCategory(7).get(0).activityInfo.packageName : ""));
            map.put(8, new AppMenu(true, false, CoreApplication.getInstance().getApplicationByCategory(8).size() >= 1 ? CoreApplication.getInstance().getApplicationByCategory(8).get(0).activityInfo.packageName : ""));
            map.put(9, new AppMenu(false, false, CoreApplication.getInstance().getApplicationByCategory(9).size() >= 1 ? CoreApplication.getInstance().getApplicationByCategory(9).get(0).activityInfo.packageName : ""));
            map.put(10, new AppMenu(true, false, CoreApplication.getInstance().getApplicationByCategory(10).size() >= 1 ? CoreApplication.getInstance().getApplicationByCategory(10).get(0).activityInfo.packageName : ""));
            map.put(11, new AppMenu(false, false, CoreApplication.getInstance().getApplicationByCategory(11).size() >= 1 ? CoreApplication.getInstance().getApplicationByCategory(11).get(0).activityInfo.packageName : ""));
            map.put(12, new AppMenu(true, false, CoreApplication.getInstance().getApplicationByCategory(12).size() >= 1 ? CoreApplication.getInstance().getApplicationByCategory(12).get(0).activityInfo.packageName : ""));
            map.put(13, new AppMenu(true, true, CoreApplication.getInstance().getApplicationByCategory(13).size() >= 1 ? CoreApplication.getInstance().getApplicationByCategory(13).get(0).activityInfo.packageName : ""));
            map.put(14, new AppMenu(true, true, CoreApplication.getInstance().getApplicationByCategory(14).size() >= 1 ? CoreApplication.getInstance().getApplicationByCategory(14).get(0).activityInfo.packageName : ""));
            map.put(15, new AppMenu(true, true, CoreApplication.getInstance().getApplicationByCategory(15).size() >= 1 ? CoreApplication.getInstance().getApplicationByCategory(15).get(0).activityInfo.packageName : ""));
            map.put(16, new AppMenu(true, true, CoreApplication.getInstance().getApplicationByCategory(16).size() >= 1 ? CoreApplication.getInstance().getApplicationByCategory(16).get(0).activityInfo.packageName : ""));
            String hashMapToolSettings = new Gson().toJson(map);
            PrefSiempo.getInstance(this).write(PrefSiempo.TOOLS_SETTING, hashMapToolSettings);

//            Set<String> junkfoodList = new HashSet<>();
//            junkfoodList.add("net.sourceforge.opencamera");
//            PrefSiempo.getInstance(this).write(PrefSiempo.JUNKFOOD_APPS, junkfoodList);
        }
    }

    /**
     * HashMap to store tools settings data.
     *
     * @return HashMap<Integer, AppMenu>
     */
    public HashMap<Integer, AppMenu> getToolsSettings() {
        String storedHashMapString = PrefSiempo.getInstance(this).read(PrefSiempo.TOOLS_SETTING, "");
        java.lang.reflect.Type type = new TypeToken<HashMap<Integer, AppMenu>>() {
        }.getType();
        return new Gson().fromJson(storedHashMapString, type);
    }

    /**
     * get junk food application package name list from preference
     *
     * @return
     */
    public Set<String> getJunkFoodAppList() {
        return PrefSiempo.getInstance(this).read(PrefSiempo.JUNKFOOD_APPS, new HashSet<String>());
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
            crashlytics = new Crashlytics();
            final Fabric fabric = new Fabric.Builder(this)
                    .kits(crashlytics)
                    .debuggable(Config.DEBUG)
                    .build();
            Fabric.with(fabric);
        }
    }

    public void logException(Throwable e) {
        Crashlytics.logException(e);
    }

    private void configIconify() {
        Iconify.with(new FontAwesomeModule());
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

        SharedPreferences sharedPreferences = getSharedPreferences("Launcher3Prefs", 0);


        String disable_AppList = sharedPreferences.getString(DISABLE_APPLIST, "");
        if (!TextUtils.isEmpty(disable_AppList)) {
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            disableNotificationApps = new Gson().fromJson(disable_AppList, type);
        }
        String block_AppList = sharedPreferences.getString(BLOCKED_APPLIST, "");
        if (!TextUtils.isEmpty(block_AppList)) {
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            blockedApps = new Gson().fromJson(block_AppList, type);
        }

        boolean isAppInstallFirstTime = sharedPreferences.getBoolean("isAppInstalledFirstTime", true);
        if (isAppInstallFirstTime) {
            blockedApps.clear();
        }
        for (ApplicationInfo applicationInfo : CoreApplication.getInstance().getPackagesList()) {


            if (isAppInstallFirstTime) {

                blockedApps.add(applicationInfo.packageName);
            } else {
                if (blockedApps.size() > 0) {
                    for (String blockedApp : blockedApps) {
                        if (!applicationInfo.packageName.equalsIgnoreCase(blockedApp)) {
                            disableNotificationApps.add(applicationInfo.packageName);
                        }
                    }

                } else {

                    disableNotificationApps.add(applicationInfo.packageName);
                }

            }
        }


        String blockedList = new Gson().toJson(blockedApps);
        sharedPreferences.edit().putString(BLOCKED_APPLIST, blockedList).apply();

        String disableList = new Gson().toJson(disableNotificationApps);
        sharedPreferences.edit().putString(DISABLE_APPLIST, disableList).apply();


    }

    /**
     * Return the application name by providing it's package name.
     *
     * @param packagename
     * @return application name
     */
    public String getApplicationNameFromPackageName(String packagename) {
        PackageManager packageManager = getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(packagename, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "");
    }

    /**
     * Return the application icon by providing it's package name.
     *
     * @param packagename
     * @return application name
     */
    public Drawable getApplicationIconFromPackageName(String packagename) {
        Drawable icon = null;
        try {
            icon = getPackageManager().getApplicationIcon(packagename);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return icon;
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


    public ArrayList<ResolveInfo> getApplicationByCategory(int id) {
        ArrayList<ResolveInfo> list = new ArrayList<>();
        switch (id) {
            case 1:// Map
                Double myLatitude = 44.433106;
                Double myLongitude = 26.103687;
                String labelLocation = "Jorgesys @ Bucharest";
                String urlAddress = "http://maps.google.com/maps?q=" + myLatitude + "," + myLongitude + "(" + labelLocation + ")&iwloc=A&hl=es";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlAddress));
                list.addAll(getPackageManager().queryIntentActivities(intent, 0));
                break;
            case 2:// Transport
                break;
            case 3://Calender
                Intent calenderIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://com.android.calendar/time/"));
                list.addAll(getPackageManager().queryIntentActivities(calenderIntent, 0));
                break;
            case 4://Weather
                break;
            case 5://Notes
                String filepath = "mnt/sdcard/doc.txt";
                File file = new File(filepath);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        CoreApplication.getInstance().logException(e);
                        e.printStackTrace();
                    }
                }

                Intent intentNotes = new Intent(Intent.ACTION_EDIT);
                intentNotes.setDataAndType(Uri.fromFile(file), "text/plain");
//                list.clear();
                list.add(null);
                list.addAll(getPackageManager().queryIntentActivities(intentNotes, 0));

                if (UIUtils.isAppInstalled(this, "com.google.android.keep")) {
                    Intent keepIntent = new Intent();
                    keepIntent.setPackage("com.google.android.keep");
                    List<ResolveInfo> resolveInfo = getPackageManager().queryIntentActivities(keepIntent, 0);
                    if (resolveInfo != null && resolveInfo.size() > 0) {
                        list.add(resolveInfo.get(0));
                    }
                }
                break;
            case 6://Recorder
                break;
            case 7://Camera
                Intent intentCamera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                list.addAll(getPackageManager().queryIntentActivities(intentCamera, 0));
                break;
            case 8://Photos
                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/* video/*");
                list.addAll(getPackageManager().queryIntentActivities(pickIntent, 0));
                break;
            case 9://Payment
                break;
            case 10://Wellness
                break;
            case 11://Browser
                Intent intentBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/"));
                getBrowserPackageList().clear();
                list.addAll(getPackageManager().queryIntentActivities(intentBrowser, 0));
                break;
            case 12:// blank
                break;
            case 13://Call
                Uri number = Uri.parse("tel:");
                Intent dial = new Intent(Intent.ACTION_DIAL, number);
                list.addAll(getPackageManager().queryIntentActivities(dial, 0));
                break;
            case 14://Clock
                Intent intentClock = new Intent(AlarmClock.ACTION_SET_ALARM);
                list.addAll(getPackageManager().queryIntentActivities(intentClock, 0));
                break;
            case 15://message
                Intent message = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + ""));
                message.putExtra("sms_body", "Test text...");
                list.addAll(getPackageManager().queryIntentActivities(message, 0));
                break;
            case 16://email
                Intent intentEmail = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:recipient@example.com?subject=" + "" + "&body=" + "");
                intentEmail.setData(data);
                list.addAll(getPackageManager().queryIntentActivities(intentEmail, 0));
                break;
            default:
                break;
        }
        return list;
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
        Intent dial = new Intent(Intent.ACTION_VIEW, Uri.parse("content://com.android.calendar/time/"));
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
                CoreApplication.getInstance().logException(e);
                e.printStackTrace();
            }
        }

        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setDataAndType(Uri.fromFile(file), "text/plain");
        getNotesPackageList().clear();
        getNotesPackageList().add(null);
        getNotesPackageList().addAll(getPackageManager().queryIntentActivities(intent, 0));

        if (UIUtils.isAppInstalled(this, "com.google.android.keep")) {
            Intent keepIntent = new Intent();
            keepIntent.setPackage("com.google.android.keep");
            List<ResolveInfo> resolveInfo = getPackageManager().queryIntentActivities(keepIntent, 0);
            if (resolveInfo != null && resolveInfo.size() > 0) {
                getNotesPackageList().add(resolveInfo.get(0));
            }
        }


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
        } catch (Exception e) {
            CoreApplication.getInstance().logException(e);
            Tracer.d("Decline call exception.." + e.toString());
            e.printStackTrace();
        }
    }

    public void playNotificationSoundVibrate() {
//        try {
//            if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
//                Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                notificationMediaPlayer = new MediaPlayer();
//                notificationMediaPlayer.setDataSource(this, alert);
//                final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//                if (audioManager != null && audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
//                    notificationMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                    int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, max, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
//                    notificationMediaPlayer.setLooping(false);
//                    notificationMediaPlayer.prepare();
//                    if (!notificationMediaPlayer.isPlaying()) {
//                        notificationMediaPlayer.start();
//                    }
//                    vibrator.vibrate(200);
//                    notificationMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                        @Override
//                        public void onCompletion(MediaPlayer mp) {
//                            if (notificationMediaPlayer != null) {
//                                notificationMediaPlayer.stop();
//                                notificationMediaPlayer.release();
//                            }
//                            notificationMediaPlayer = null;
//                        }
//                    });
//                }
//            }
//
//        } catch (Exception e) {
//            CoreApplication.getInstance().logException(e);
//            e.printStackTrace();
//        }
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
                        } catch (Exception e) {
                            CoreApplication.getInstance().logException(e);
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
                    CoreApplication.getInstance().logException(e);
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

}
