package minium.co.core.app;

import android.app.Application;

import org.androidannotations.annotations.EApplication;

import minium.co.core.R;
import minium.co.core.config.Config;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Each application should contain an {@link Application} class instance
 * All applications of this project should extend their own application from this class
 * This will be first class where we can initialize all necessary first time configurations
 *
 * Created by shahab on 3/17/16.
 */
@EApplication
public abstract class CoreApplication extends Application {

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
        configCalligraphy();
    }

    private void configCalligraphy() {
        CalligraphyConfig
                .initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath(getString(Config.DEFAULT_FONT_PATH_RES))
                        .setFontAttrId(R.attr.fontPath)
                        .build());
    }
}
