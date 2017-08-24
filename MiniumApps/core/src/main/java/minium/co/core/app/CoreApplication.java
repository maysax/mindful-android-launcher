package minium.co.core.app;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.multidex.MultiDexApplication;

import com.androidnetworking.AndroidNetworking;
import com.crashlytics.android.Crashlytics;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import minium.co.core.R;
import minium.co.core.config.Config;
import minium.co.core.log.LogConfig;
import minium.co.core.log.Tracer;
import minium.co.core.ui.LifecycleHandler;
import minium.co.core.util.FontUtils;
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


    private List<ApplicationInfo> packagesList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        refWatcher = LeakCanary.install(this);

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
        // configFabric();
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
        this.packagesList = packagesList;
    }

    private class LoadApplications extends AsyncTask<Object, Object, List<ApplicationInfo>> {

        @Override
        protected List<ApplicationInfo> doInBackground(Object... params) {
            List<ApplicationInfo> applist = checkForLaunchIntent(getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA));
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
                    if (null != getPackageManager().getLaunchIntentForPackage(info.packageName)) {
                        applist.add(info);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return applist;
        }

    }
}
