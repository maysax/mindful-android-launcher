package minium.co.messages.app;

import com.moez.QKSMS.QKSMSAppBase;

/**
 * Concrete implementation of {@link CoreApplication}
 * This class will implement specific behaviors that differs with core library
 *
 * Created by shahab on 3/17/16.
 */

public class MessagesApp extends CoreApp {

    //private final String TRACE_TAG = LogConfig.TRACE_TAG + "MessagesApp";


    @Override
    public void onCreate() {
        super.onCreate();

//        Tracer.i("Application Id: " + minium.co.messages.BuildConfig.APPLICATION_ID
//                + " || Version code: " + minium.co.messages.BuildConfig.VERSION_CODE
//                + " || Version name: " + minium.co.messages.BuildConfig.VERSION_NAME
//                + " || Git Sha: " + BuildConfig.GIT_SHA
//                + " || Build time:  " + BuildConfig.BUILD_TIME
//                + " || Build flavor: " + minium.co.messages.BuildConfig.FLAVOR
//                + " || Build type: " + minium.co.messages.BuildConfig.BUILD_TYPE);
    }
}
