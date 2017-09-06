package minium.co.core.app;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.UserManager;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import minium.co.core.R;
import minium.co.core.config.Config;
import minium.co.core.log.LogConfig;
import minium.co.core.log.Tracer;
import minium.co.core.ui.LifecycleHandler;
import minium.co.core.util.FontUtils;
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


    UserManager userManager;


    LauncherApps launcherApps;

    private List<ApplicationInfo> packagesList = new ArrayList<>();

    public HashMap<String, Bitmap> iconList = new HashMap<>();


    @Override
    public void onCreate() {
        super.onCreate();

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
    private void getAllApplicationPackageName() {
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
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(Config.DEBUG)
                .build();
        Fabric.with(fabric);
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

                return (v1.loadLabel(getPackageManager()).toString()).compareTo(v2.loadLabel(getPackageManager()).toString());
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
        for (ApplicationInfo applicationInfo : getPackagesList()) {
            if (applicationInfo.packageName.equalsIgnoreCase(packagename)) {
                return applicationInfo.name;
            }
        }
        return "";
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
                        } catch (PackageManager.NameNotFoundException e) {
                            Log.e("check", "error getting Hi Res Icon :", e);
                            drawable = appInfo.loadIcon(getPackageManager());
                        }
                        Bitmap bitmap = drawableToBitmap(drawable);
                        if(!TextUtils.isEmpty(activityInfo.getApplicationInfo().packageName)){
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
        }

        private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
            ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
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

        public Bitmap drawableToBitmap(Drawable drawable) {
            Bitmap bitmap = null;

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
