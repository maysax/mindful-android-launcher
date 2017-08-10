package minium.co.contacts.app;

import com.android.contacts.ContactsApplication;

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
public class ContactsApp extends ContactsApplication {

    private final String TRACE_TAG = LogConfig.TRACE_TAG + "ContactsApp";

    @Trace(tag = TRACE_TAG)
    @Override
    public void onCreate() {
        super.onCreate();

        Tracer.i("Application Id: " + minium.co.contacts.BuildConfig.APPLICATION_ID
                + " || Version code: " + minium.co.contacts.BuildConfig.VERSION_CODE
                + " || Version name: " + minium.co.contacts.BuildConfig.VERSION_NAME
                + " || Git Sha: " + BuildConfig.GIT_SHA
                + " || Build time:  " + BuildConfig.BUILD_TIME
                + " || Build flavor: " + minium.co.contacts.BuildConfig.FLAVOR
                + " || Build type: " + minium.co.contacts.BuildConfig.BUILD_TYPE);
    }
}
