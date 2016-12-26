package minium.co.launcher2.app;

import android.os.Build;

import com.orm.SugarContext;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import minium.co.core.BuildConfig;
import minium.co.core.app.CoreApplication;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.config.Config;
import minium.co.core.log.LogConfig;
import minium.co.core.log.Tracer;
import minium.co.core.util.DateUtils;
import minium.co.launcher2.data.ActionItemManager;

/**
 * Created by Shahab on 4/26/2016.
 */
@EApplication
public class Launcher2App extends CoreApplication {

    private final String TRACE_TAG = LogConfig.TRACE_TAG + "Launcher2App";

    @Bean
    ActionItemManager manager;

    @Pref
    DroidPrefs_ prefs;

    @Trace(tag = TRACE_TAG)
    @Override
    public void onCreate() {
        super.onCreate();

        Tracer.i("Application Id: " + minium.co.launcher2.BuildConfig.APPLICATION_ID
                + " || Version code: " + minium.co.launcher2.BuildConfig.VERSION_CODE
                + " || Version name: " + minium.co.launcher2.BuildConfig.VERSION_NAME
                + "\nGit Sha: " + BuildConfig.GIT_SHA
                + " || Build time:  " + BuildConfig.BUILD_TIME
                + " || Build flavor: " + minium.co.launcher2.BuildConfig.FLAVOR
                + " || Build type: " + minium.co.launcher2.BuildConfig.BUILD_TYPE);

        Tracer.i("Model: " + Build.MODEL
                + " || Build No: " + Build.FINGERPRINT
                + " || Brand: " + Build.BRAND
                + " || Device: " + Build.DEVICE
                + " || Build Id: " + Build.ID
                + " || Manufacturer: " + Build.MANUFACTURER);

        loadConfigurationValues();

        manager.init();

        SugarContext.init(this);

        //testCases();
    }

    private void testCases() {
        Tracer.d("Current Time: " + SimpleDateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()));
        DateUtils.nextIntervalMillis(1 * 60 * 1000);
        DateUtils.nextIntervalMillis(2 * 60 * 1000);
        DateUtils.nextIntervalMillis(5 * 60 * 1000);
        DateUtils.nextIntervalMillis(10 * 60 * 1000);
        DateUtils.nextIntervalMillis(15 * 60 * 1000);
        DateUtils.nextIntervalMillis(20 * 60 * 1000);
        DateUtils.nextIntervalMillis(25 * 60 * 1000);
        DateUtils.nextIntervalMillis(30 * 60 * 1000);
        DateUtils.nextIntervalMillis(35 * 60 * 1000);
        DateUtils.nextIntervalMillis(40 * 60 * 1000);
        DateUtils.nextIntervalMillis(45 * 60 * 1000);
        DateUtils.nextIntervalMillis(50 * 60 * 1000);
        DateUtils.nextIntervalMillis(55 * 60 * 1000);
        DateUtils.nextIntervalMillis(60 * 60 * 1000);
        DateUtils.nextIntervalMillis(65 * 60 * 1000);
        DateUtils.nextIntervalMillis(70 * 60 * 1000);
        DateUtils.nextIntervalMillis(80 * 60 * 1000);
    }

    private void loadConfigurationValues() {
        int flowSegmentCount = 4;

        if (Config.DEBUG) {
            prefs.edit()
                    .flowMaxTimeLimitMillis().put(flowSegmentCount * 5 * 1000f)
                    .flowSegmentDurationMillis().put(5 * 1000f)
                    .apply();

        } else {
            prefs.edit()
                    .flowMaxTimeLimitMillis().put(flowSegmentCount * 15 * 60 * 1000f)
                    .flowSegmentDurationMillis().put(15 * 60 * 1000f)
                    .apply();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }
}