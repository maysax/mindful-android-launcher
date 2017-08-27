package minium.co.launcher.app;

import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.Trace;

import minium.co.core.BuildConfig;
import minium.co.core.app.CoreApplication;
import minium.co.core.log.LogConfig;
import minium.co.core.log.Tracer;

/**
 * Concrete implementation of {@link CoreApplication}
 * This class will implement specific behaviors that differs with core library
 * <p>
 * Created by shahab on 3/17/16.
 */
@EApplication
public class LauncherApp extends CoreApplication {

    private final String TRACE_TAG = LogConfig.TRACE_TAG + "LauncherApp";

    @Trace(tag = TRACE_TAG)
    @Override
    public void onCreate() {
        super.onCreate();

        Tracer.i("Application Id: " + minium.co.launcher.BuildConfig.APPLICATION_ID
                + " || Version code: " + minium.co.launcher.BuildConfig.VERSION_CODE
                + " || Version name: " + minium.co.launcher.BuildConfig.VERSION_NAME
                + " || Git Sha: " + BuildConfig.GIT_SHA
                + " || Build time:  " + BuildConfig.BUILD_TIME
                + " || Build flavor: " + minium.co.launcher.BuildConfig.FLAVOR
                + " || Build type: " + minium.co.launcher.BuildConfig.BUILD_TYPE);
    }
}
