package minium.co.core.app;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.Trace;

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
 *
 * Created by shahab on 3/17/16.
 */
public abstract class CoreApplication extends MultiDexApplication {

    private final String TRACE_TAG = LogConfig.TRACE_TAG + "CoreApplication";

    private static CoreApplication sInstance;

    public static synchronized CoreApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        init();
    }

    protected void init() {
        // set initial configurations here
        configTracer();
        configCalligraphy();
        configFabric();
        configIconify();
        configureLifecycle();
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
}
