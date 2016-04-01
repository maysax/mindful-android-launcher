package minium.co.messages.app;

import android.location.Country;

import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.Trace;

import java.util.Locale;

import minium.co.core.BuildConfig;
import minium.co.core.app.CoreApplication;
import minium.co.core.log.LogConfig;
import minium.co.core.log.Tracer;
import minium.co.messages.common.google.DraftCache;
import minium.co.messages.data.Contact;
import minium.co.messages.data.Conversation;

/**
 * Concrete implementation of {@link CoreApplication}
 * This class will implement specific behaviors that differs with core library
 *
 * Created by shahab on 3/17/16.
 */
@EApplication
public class MessagesApp extends CoreApplication {

    private final String TRACE_TAG = LogConfig.TRACE_TAG + "MessagesApp";

    private String mCountryIso;


    @Trace(tag = TRACE_TAG)
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

        // Figure out the country *before* loading contacts and formatting numbers
        Country country = new Country(Locale.getDefault().getCountry(), Country.COUNTRY_SOURCE_LOCALE);
        mCountryIso = country.getCountryIso();
    }

    @Override
    protected void init() {
        super.init();
        Contact.init(this);
        DraftCache.init(this);
        Conversation.init(this);
    }

    // This function CAN return null.
    public String getCurrentCountryIso() {
        if (mCountryIso == null) {
            Country country = new Country(Locale.getDefault().getCountry(), Country.COUNTRY_SOURCE_LOCALE);
            mCountryIso = country.getCountryIso();
        }
        return mCountryIso;
    }
}
