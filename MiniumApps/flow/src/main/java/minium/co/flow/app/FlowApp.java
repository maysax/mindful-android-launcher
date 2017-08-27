package minium.co.flow.app;

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
 * Created by Shahab on 5/12/2016.
 */
@EApplication
public class FlowApp extends CoreApplication {

    private final String TRACE_TAG = LogConfig.TRACE_TAG + "FlowApp";

    @Trace(tag = TRACE_TAG)
    @Override
    public void onCreate() {
        super.onCreate();

        Tracer.i("Application Id: " + minium.co.flow.BuildConfig.APPLICATION_ID
                + " || Version code: " + minium.co.flow.BuildConfig.VERSION_CODE
                + " || Version name: " + minium.co.flow.BuildConfig.VERSION_NAME
                + " || Git Sha: " + BuildConfig.GIT_SHA
                + " || Build time:  " + BuildConfig.BUILD_TIME
                + " || Build flavor: " + minium.co.flow.BuildConfig.FLAVOR
                + " || Build type: " + minium.co.flow.BuildConfig.BUILD_TYPE);
    }
}
