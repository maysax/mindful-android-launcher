package minium.co.core.app;

import android.app.Application;

/**
 * Each application should contain an {@link Application} class instance
 * All applications of this project should extend their own application from this class
 * This will be first class where we can initialize all necessary first time configurations
 *
 * Created by shahab on 3/17/16.
 */

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
    }
}
