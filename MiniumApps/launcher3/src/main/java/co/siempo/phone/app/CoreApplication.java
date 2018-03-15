package co.siempo.phone.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.UserManager;
import android.provider.AlarmClock;
import android.provider.Settings;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.LruCache;

import com.androidnetworking.AndroidNetworking;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import co.siempo.phone.R;
import co.siempo.phone.event.AppInstalledEvent;
import co.siempo.phone.log.Tracer;
import co.siempo.phone.models.AppMenu;
import co.siempo.phone.models.MainListItem;
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
    private List<String> packagesList = new ArrayList<>();
    private ArrayList<String> disableNotificationApps = new ArrayList<>();
    private Set<String> blockedApps = new HashSet<>();
    private LruCache<String, Bitmap> mMemoryCache;
    private ArrayList<String> junkFoodList = new ArrayList<>();
    private ArrayList<MainListItem> toolItemsList = new ArrayList<>();
    private ArrayList<MainListItem> toolBottomItemsList = new ArrayList<>();
    private ArrayList<MainListItem> favoriteItemsList = new ArrayList<>();
    private boolean isHideIconBranding = true;
    private boolean isRandomize = true;

    public static synchronized CoreApplication getInstance() {
        return sInstance;
    }

    public boolean isHideIconBranding() {
        return isHideIconBranding;
    }

    public void setHideIconBranding(boolean hideIconBranding) {
        isHideIconBranding = hideIconBranding;
    }

    public boolean isRandomize() {
        return isRandomize;
    }

    public void setRandomize(boolean randomize) {
        this.isRandomize = randomize;
    }

    public ArrayList<String> getJunkFoodList() {
        return junkFoodList;
    }

    public void setJunkFoodList(ArrayList<String> junkFoodList) {
        this.junkFoodList = junkFoodList;
    }

    public ArrayList<MainListItem> getToolItemsList() {
        return toolItemsList;
    }

    public void setToolItemsList(ArrayList<MainListItem> toolItemsList) {
        this.toolItemsList = toolItemsList;
    }

    public ArrayList<MainListItem> getFavoriteItemsList() {
        return favoriteItemsList;
    }

    public void setFavoriteItemsList(ArrayList<MainListItem> favoriteItemsList) {
        this.favoriteItemsList = favoriteItemsList;
    }

    public ArrayList<MainListItem> getToolBottomItemsList() {
        return toolBottomItemsList;
    }

    public void setToolBottomItemsList(ArrayList<MainListItem> toolBottomItemsList) {
        this.toolBottomItemsList = toolBottomItemsList;
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

    public String getDeviceId() {
        return Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
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
        setHideIconBranding(PrefSiempo.getInstance(sInstance).read(PrefSiempo.IS_ICON_BRANDING, true));
        setRandomize(PrefSiempo.getInstance(sInstance).read(PrefSiempo.IS_RANDOMIZE_JUNKFOOD, true));
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
            map.put(1, new AppMenu(true, false, CoreApplication.getInstance
                    ().getApplicationByCategory(1).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(1).get(0).activityInfo.packageName : ""));
            map.put(2, new AppMenu(true, false, CoreApplication.getInstance
                    ().getApplicationByCategory(2).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(2).get(0).activityInfo.packageName : ""));
            map.put(3, new AppMenu(true, false, CoreApplication.getInstance
                    ().getApplicationByCategory(3).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(3).get(0).activityInfo.packageName : ""));
            map.put(4, new AppMenu(true, false, CoreApplication.getInstance
                    ().getApplicationByCategory(4).size() == 1 ?
                    CoreApplication.getInstance().getApplicationByCategory(4).get(0).activityInfo.packageName : ""));
            map.put(5, new AppMenu(true, false, getString(R.string.notes)));
            map.put(6, new AppMenu(false, false, CoreApplication
                    .getInstance().getApplicationByCategory(6).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(6).get(0).activityInfo.packageName : ""));
            map.put(7, new AppMenu(true, false, CoreApplication.getInstance
                    ().getApplicationByCategory(7).size() == 1 ?
                    CoreApplication.getInstance().getApplicationByCategory(7).get(0).activityInfo.packageName : ""));
            map.put(8, new AppMenu(true, false, CoreApplication.getInstance
                    ().getApplicationByCategory(8).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(8).get(0).activityInfo.packageName : ""));
            map.put(9, new AppMenu(false, false, CoreApplication
                    .getInstance().getApplicationByCategory(9).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(9).get(0).activityInfo.packageName : ""));
            map.put(10, new AppMenu(true, false, CoreApplication
                    .getInstance().getApplicationByCategory(10).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(10).get(0).activityInfo.packageName : ""));
            map.put(11, new AppMenu(false, false, CoreApplication
                    .getInstance().getApplicationByCategory(11).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(11).get(0).activityInfo.packageName : ""));
            map.put(12, new AppMenu(true, false, CoreApplication
                    .getInstance().getApplicationByCategory(12).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(12).get(0).activityInfo.packageName : ""));
            map.put(13, new AppMenu(true, true, CoreApplication.getInstance
                    ().getApplicationByCategory(13).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(13).get(0).activityInfo.packageName : ""));
            map.put(14, new AppMenu(true, true, CoreApplication.getInstance
                    ().getApplicationByCategory(14).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(14).get(0).activityInfo.packageName : ""));
            map.put(15, new AppMenu(true, true, CoreApplication.getInstance
                    ().getApplicationByCategory(15).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(15).get(0).activityInfo.packageName : ""));
            map.put(16, new AppMenu(true, true, CoreApplication.getInstance
                    ().getApplicationByCategory(16).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(16).get(0).activityInfo.packageName : ""));
            String hashMapToolSettings = new Gson().toJson(map);
            PrefSiempo.getInstance(this).write(PrefSiempo.TOOLS_SETTING, hashMapToolSettings);


            /*
              SSA-1321: Adding the mentioned apps in junk food by default
             */
            Set<String> junkfoodList = new HashSet<>();

            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> pkgAppsList = getPackageManager().queryIntentActivities(mainIntent, 0);


            for (ResolveInfo resolveInfo : pkgAppsList) {

                String packageName = !TextUtils.isEmpty(resolveInfo.activityInfo
                        .packageName) ? resolveInfo.activityInfo
                        .packageName : "";
                if (packageName.contains("com.facebook") || packageName
                        .contains("com.king")) {
                    if (UIUtils.isAppInstalledAndEnabled(getApplicationContext(), packageName)) {
                        junkfoodList.add(packageName);
                    }
                } else {

                    switch (packageName) {
                        case Constants.SNAP_PACKAGE:
                        case Constants.INSTAGRAM_PACKAGE:
                        case Constants.LINKEDIN_PACKAGE:
                        case Constants.CLASH_ROYAL_PACKAGE:
                        case Constants.HINGE_PACKAGE:
                        case Constants.NETFLIX_PACKAGE:
                        case Constants.REDDIT_PACKAGE:
                        case Constants.TINDER_PACKAGE:
                        case Constants.GRINDR_PACKAGE:
                        case Constants.YOUTUBE_PACKAGE:
                        case Constants.BUMBLE_PACKAGE:
                            if (UIUtils.isAppInstalledAndEnabled(getApplicationContext(), packageName)) {
                                junkfoodList.add(packageName);
                            }
                            break;
                    }

                }
            }


            PrefSiempo.getInstance(this).write(PrefSiempo.JUNKFOOD_APPS, junkfoodList);
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

    public List<String> getPackagesList() {
        return packagesList;
    }

    public void setPackagesList(List<String> packagesList) {
        try {
            Collections.sort(packagesList, new Comparator<String>() {
                public int compare(String v1, String v2) {

                    return v1.toLowerCase().compareTo(v2.toLowerCase());
                }
            });
            this.packagesList = packagesList;


            blockedApps = PrefSiempo.getInstance(this).read(PrefSiempo
                    .BLOCKED_APPLIST, new HashSet<String>());
            boolean isAppInstallFirstTime = PrefSiempo.getInstance(this).read(PrefSiempo
                    .IS_APP_INSTALLED_FIRSTTIME, true);
            if (isAppInstallFirstTime) {
                blockedApps.clear();
            }
            for (String applicationInfo : packagesList) {
                if (isAppInstallFirstTime) {
                    blockedApps.add(applicationInfo);
                }
            }

            PrefSiempo.getInstance(this).write(PrefSiempo
                    .BLOCKED_APPLIST, blockedApps);
        } catch (Exception e) {
        }
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

                if (UIUtils.isAppInstalledAndEnabled(this, "com.google.android.keep")) {
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
                getPackagesList().add(appInfo.packageName);
            } else {
                if (getPackagesList().contains(packageName)) {
                    getPackagesList().remove(packageName);
                }
            }
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

    public void includeTaskPool(AsyncTask asyncTask, Object object) {
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, object);
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    private class LoadApplications extends AsyncTask<Object, Object, Set<String>> {

        @Override
        protected Set<String> doInBackground(Object... params) {
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> pkgAppsList = getPackageManager().queryIntentActivities(mainIntent, 0);
            Set<String> applist = new HashSet<>();
            for (ResolveInfo appInfo : pkgAppsList) {
                try {
                    String packageName = appInfo.activityInfo.packageName;
                    if (!packageName.equalsIgnoreCase(getPackageName())) {
                        Drawable drawable = null;
                        try {
                            drawable = appInfo.loadIcon
                                    (getPackageManager());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (drawable != null) {
                            Bitmap bitmap = PackageUtil.drawableToBitmap(drawable);
                            addBitmapToMemoryCache(packageName, bitmap);
                        }
                        applist.add(packageName);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            return applist;
        }


        @Override
        protected void onPostExecute(Set<String> applicationInfos) {
            super.onPostExecute(applicationInfos);
//            packagesList.clear();
            List<String> apps = new ArrayList<String>(applicationInfos);
            setPackagesList(apps);
            EventBus.getDefault().post(new AppInstalledEvent(true));
        }
    }
}
