package co.siempo.phone.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.UserManager;
import android.provider.AlarmClock;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

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
import co.siempo.phone.utils.PackageUtil;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.UIUtils;
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
    UserManager userManager;
    LauncherApps launcherApps;
    private List<ApplicationInfo> packagesList = new ArrayList<>();
    private ArrayList<String> disableNotificationApps = new ArrayList<>();
    private ArrayList<String> blockedApps = new ArrayList<>();
    private LruCache<String, Bitmap> mMemoryCache;

    public static synchronized CoreApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        userManager = (UserManager) getSystemService(Context.USER_SERVICE);
        launcherApps = (LauncherApps) getSystemService(Context.LAUNCHER_APPS_SERVICE);
        sInstance = this;
        init();
        initMemoryCatch();
        getAllApplicationPackageName();
    }

    /**
     * This method is used for fetch all installed application package list.
     */
    public void getAllApplicationPackageName() {
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
            Crashlytics crashlytics = new Crashlytics();
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

                return (v1.loadLabel(getPackageManager()).toString().toLowerCase()).compareTo(v2.loadLabel(getPackageManager()).toString().toLowerCase());
            }
        });
        this.packagesList = packagesList;


        String disable_AppList = PrefSiempo.getInstance(this).read
                (PrefSiempo.DISABLE_APPLIST, "");
        if (!TextUtils.isEmpty(disable_AppList)) {
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            disableNotificationApps = new Gson().fromJson(disable_AppList, type);
        }
        String block_AppList = PrefSiempo.getInstance(this).read(PrefSiempo
                .BLOCKED_APPLIST, "");
        if (!TextUtils.isEmpty(block_AppList)) {
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            blockedApps = new Gson().fromJson(block_AppList, type);
        }
        boolean isAppInstallFirstTime = PrefSiempo.getInstance(this).read(PrefSiempo
                .IS_APP_INSTALLED_FIRSTTIME, true);
        if (isAppInstallFirstTime) {
            blockedApps.clear();
        }
        for (ApplicationInfo applicationInfo : packagesList) {

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
        PrefSiempo.getInstance(this).write(PrefSiempo
                .BLOCKED_APPLIST, blockedList);

        String disableList = new Gson().toJson(disableNotificationApps);
        PrefSiempo.getInstance(this).write(PrefSiempo
                .DISABLE_APPLIST, disableList);

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

    /**
     * Get List of installed application related to specific category.
     *
     * @param id
     * @return
     */
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

    public void addOrRemoveApplicationInfo(boolean addingOrDelete, String packageName) {
        try {
            if (addingOrDelete) {
                ApplicationInfo appInfo = getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
                getPackagesList().add(appInfo);
            } else {
                getPackagesList().remove(getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA));
            }
            setPackagesList(getPackagesList());
            EventBus.getDefault().post(new AppInstalledEvent(true));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initMemoryCatch() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 4;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }




    public void includeTaskPool(AsyncTask asyncTask) {
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }


    private class LoadApplications extends AsyncTask<Object, Object, List<ApplicationInfo>> {

        @Override
        protected List<ApplicationInfo> doInBackground(Object... params) {
            List<ApplicationInfo> applist = new ArrayList<>();

            for (android.os.UserHandle profile : userManager.getUserProfiles()) {
                for (LauncherActivityInfo activityInfo : launcherApps.getActivityList(null, profile)) {
                    ApplicationInfo appInfo = activityInfo.getApplicationInfo();
                    appInfo.name = activityInfo.getLabel().toString();
                    if (!appInfo.packageName.equalsIgnoreCase("co.siempo.phone")) {
                        Drawable drawable = null;
                        drawable = getPackageManager().getApplicationIcon(appInfo);
                        Bitmap bitmap = PackageUtil.drawableToBitmap(drawable);
                        addBitmapToMemoryCache(appInfo.packageName, bitmap);
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
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
}
