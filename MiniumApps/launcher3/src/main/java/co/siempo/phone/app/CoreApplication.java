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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.UserManager;
import android.provider.AlarmClock;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.androidnetworking.AndroidNetworking;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.event.AppInstalledEvent;
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
    public String DISABLE_APPLIST = "DISABLE_APPLIST";
    public String BLOCKED_APPLIST = "BLOCKED_APPLIST";
    public HashMap<String, Bitmap> iconList = new HashMap<>();
    UserManager userManager;
    LauncherApps launcherApps;
    SharedPreferences sharedPref;
    NotificationManager notificationManager;
    private Crashlytics crashlytics;
    private List<ApplicationInfo> packagesList = new ArrayList<>();
    private boolean isEditNotOpen = false;
    private ArrayList<String> disableNotificationApps = new ArrayList<>();
    private ArrayList<String> blockedApps = new ArrayList<>();

    public static synchronized CoreApplication getInstance() {
        return sInstance;
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

    public SharedPreferences getSharedPref() {
        return sharedPref;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPref = getSharedPreferences("DroidPrefs", 0);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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
     * @param packageName
     * @return application name
     */
    public String getApplicationNameFromPackageName(String packageName) {
        PackageManager packageManager = getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
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


    private class LoadApplications extends AsyncTask<Object, Object, List<ApplicationInfo>> {

        @Override
        protected List<ApplicationInfo> doInBackground(Object... params) {
            List<ApplicationInfo> applist = new ArrayList<>();
            for (android.os.UserHandle profile : userManager.getUserProfiles()) {
                UserHandle user = new UserHandle(userManager.getSerialNumberForUser(profile), profile);
                for (LauncherActivityInfo activityInfo : launcherApps.getActivityList(null, profile)) {
                    ApplicationInfo appInfo = activityInfo.getApplicationInfo();
                    appInfo.name = activityInfo.getLabel().toString();
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
