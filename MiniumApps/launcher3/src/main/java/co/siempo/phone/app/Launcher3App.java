package co.siempo.phone.app;

import android.content.Context;
import android.os.Build;

import com.evernote.client.android.EvernoteSession;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.greenrobot.greendao.database.Database;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import co.siempo.phone.db.DaoMaster;
import co.siempo.phone.db.DaoSession;
import co.siempo.phone.db.GreenDaoOpenHelper;
import co.siempo.phone.kiss.IconsHandler;
import co.siempo.phone.token.TokenManager;
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
    public static final String DND_START_STOP_ACTION = "siempo.intent.action.DND_START_STOP";

    private DaoSession daoSession;

    @Pref
    DroidPrefs_ prefs;

    @Bean
    TokenManager manager;

    private FirebaseAnalytics mFirebaseAnalytics;
    private static IconsHandler iconsPackHandler;

    @Trace(tag = TRACE_TAG)
    @Override
    public void onCreate() {
        super.onCreate();

        Tracer.i("Application Id: " + co.siempo.phone.BuildConfig.APPLICATION_ID
                + " || Version code: " + co.siempo.phone.BuildConfig.VERSION_CODE
                + " || Version name: " + co.siempo.phone.BuildConfig.VERSION_NAME
                + "\nGit Sha: " + BuildConfig.GIT_SHA
                + " || Build time:  " + BuildConfig.BUILD_TIME
                + " || Build flavor: " + co.siempo.phone.BuildConfig.FLAVOR
                + " || Build type: " + co.siempo.phone.BuildConfig.BUILD_TYPE);

        Tracer.i("Model: " + Build.MODEL
                + " || Build No: " + Build.FINGERPRINT
                + " || Brand: " + Build.BRAND
                + " || Device: " + Build.DEVICE
                + " || Build Id: " + Build.ID
                + " || Manufacturer: " + Build.MANUFACTURER);

        loadConfigurationValues();
        configureEverNote();
        manager.init();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

/*        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,"noti-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
        */
        GreenDaoOpenHelper helper2 = new GreenDaoOpenHelper(this, "noti-db", null);
        //DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,"noti-db");

        Database db = helper2.getWritableDb();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
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

    public FirebaseAnalytics getFirebaseAnalytics() {
        return mFirebaseAnalytics;
    }

    public static IconsHandler getIconsHandler(Context ctx) {
        if (iconsPackHandler == null) {
            iconsPackHandler = new IconsHandler(ctx);
        }

        return iconsPackHandler;
    }
}
