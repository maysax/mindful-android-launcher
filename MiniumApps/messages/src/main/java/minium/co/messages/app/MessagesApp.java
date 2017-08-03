package minium.co.messages.app;

import org.androidannotations.annotations.EApplication;

import minium.co.core.BuildConfig;
import minium.co.core.app.CoreApplication;
import minium.co.core.log.Tracer;

/**
 * Concrete implementation of {@link CoreApplication}
 * This class will implement specific behaviors that differs with core library
 * <p>
 * Created by shahab on 3/17/16.
 */
@EApplication
public class MessagesApp extends CoreApplication {

    //private final String TRACE_TAG = LogConfig.TRACE_TAG + "MessagesApp";


    @Override
    public void onCreate() {
        super.onCreate();

        Tracer.i("Application Id: " + minium.co.messages.BuildConfig.APPLICATION_ID
                + " || Version code: " + minium.co.messages.BuildConfig.VERSION_CODE
                + " || Version name: " + minium.co.messages.BuildConfig.VERSION_NAME
                + " || Git Sha: " + BuildConfig.GIT_SHA
                + " || Build time:  " + BuildConfig.BUILD_TIME
                + " || Build flavor: " + minium.co.messages.BuildConfig.FLAVOR
                + " || Build type: " + minium.co.messages.BuildConfig.BUILD_TYPE);
    }
}
