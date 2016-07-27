package minium.co.launcher2.app;

import com.orm.SugarContext;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.Trace;

import minium.co.core.BuildConfig;
import minium.co.core.app.CoreApplication;
import minium.co.core.log.LogConfig;
import minium.co.core.log.Tracer;
import minium.co.launcher2.data.ActionItemManager;

/**
 * Created by Shahab on 4/26/2016.
 */
@EApplication
public class Launcher2App extends CoreApplication {

    private final String TRACE_TAG = LogConfig.TRACE_TAG + "Launcher2App";

    @Bean
    ActionItemManager manager;

    @Trace(tag = TRACE_TAG)
    @Override
    public void onCreate() {
        super.onCreate();

        Tracer.i("Application Id: " + minium.co.launcher2.BuildConfig.APPLICATION_ID
                + " || Version code: " + minium.co.launcher2.BuildConfig.VERSION_CODE
                + " || Version name: " + minium.co.launcher2.BuildConfig.VERSION_NAME
                + " || Git Sha: " + BuildConfig.GIT_SHA
                + " || Build time:  " + BuildConfig.BUILD_TIME
                + " || Build flavor: " + minium.co.launcher2.BuildConfig.FLAVOR
                + " || Build type: " + minium.co.launcher2.BuildConfig.BUILD_TYPE);

        manager.init();

        SugarContext.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }
}