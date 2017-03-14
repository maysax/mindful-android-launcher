package co.minium.launcher3.app;

import android.app.Application;
import android.os.Build;

import com.evernote.client.android.EvernoteSession;
import com.orm.SugarContext;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.greenrobot.greendao.database.Database;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import co.minium.launcher3.db.DaoMaster;
import co.minium.launcher3.db.DaoSession;
import co.minium.launcher3.token.TokenManager;
import minium.co.core.BuildConfig;
import minium.co.core.app.CoreApplication;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.config.Config;
import minium.co.core.log.LogConfig;
import minium.co.core.log.Tracer;
import minium.co.core.util.DateUtils;


/**
 * Created by Shahab on 2/16/2017.
 */
@EApplication
public class Launcher3App extends CoreApplication {

    private final String TRACE_TAG = LogConfig.TRACE_TAG + "Launcher3App";



    private DaoSession daoSession;




    @Pref
    DroidPrefs_ prefs;

    @Bean
    TokenManager manager;

    @Trace(tag = TRACE_TAG)
    @Override
    public void onCreate() {
        super.onCreate();

        Tracer.i("Application Id: " + co.minium.launcher3.BuildConfig.APPLICATION_ID
                + " || Version code: " + co.minium.launcher3.BuildConfig.VERSION_CODE
                + " || Version name: " + co.minium.launcher3.BuildConfig.VERSION_NAME
                + "\nGit Sha: " + BuildConfig.GIT_SHA
                + " || Build time:  " + BuildConfig.BUILD_TIME
                + " || Build flavor: " + co.minium.launcher3.BuildConfig.FLAVOR
                + " || Build type: " + co.minium.launcher3.BuildConfig.BUILD_TYPE);

        Tracer.i("Model: " + Build.MODEL
                + " || Build No: " + Build.FINGERPRINT
                + " || Brand: " + Build.BRAND
                + " || Device: " + Build.DEVICE
                + " || Build Id: " + Build.ID
                + " || Manufacturer: " + Build.MANUFACTURER);

        loadConfigurationValues();
        configureEverNote();
        manager.init();

        SugarContext.init(this);


        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,"noti-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();

        //testCases();
    }

    public DaoSession getDaoSession() {
        return daoSession;
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

    private void configureEverNote() {
        new EvernoteSession.Builder(this)
                .setEvernoteService(minium.co.notes.app.Config.EVERNOTE_SERVICE)
                .setSupportAppLinkedNotebooks(true)
                .setForceAuthenticationInThirdPartyApp(true)
                .build(minium.co.notes.app.Config.CONSUMER_KEY, minium.co.notes.app.Config.CONSUMER_SECRET)
                .asSingleton();

//        registerActivityLifecycleCallbacks(new LoginChecker());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }
}
