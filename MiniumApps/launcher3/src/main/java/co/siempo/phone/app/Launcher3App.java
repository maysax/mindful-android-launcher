package co.siempo.phone.app;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.evernote.client.android.EvernoteSession;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;

import co.siempo.phone.BuildConfig;
import co.siempo.phone.db.DaoMaster;
import co.siempo.phone.db.DaoSession;
import co.siempo.phone.db.GreenDaoOpenHelper;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.log.LogConfig;
import co.siempo.phone.log.Tracer;
import co.siempo.phone.utils.PackageUtil;

/**
 * Created by Shahab on 2/16/2017.
 */
@EApplication
public class Launcher3App extends CoreApplication {

    public static final String DND_START_STOP_ACTION = "siempo.intent.action.DND_START_STOP";
    private final String TRACE_TAG = LogConfig.TRACE_TAG + "Launcher3App";
    private final String TAG = "SiempoActivityLifeCycle";
    @Pref
    public DroidPrefs_ prefs;
    public Dialog dialog;
    @Pref
    Launcher3Prefs_ launcherPrefs;
    @SystemService
    AudioManager audioManager;
    @SystemService
    NotificationManager notificationManager;
    private DaoSession daoSession;
    private FirebaseAnalytics mFirebaseAnalytics;
    private boolean isSiempoLauncher = false;
    private long startTime;
    private ResolveInfo resolveInfo;
    private ArrayList<ResolveInfo> appList;

    @Trace(tag = TRACE_TAG)
    @Override
    public void onCreate() {
        super.onCreate();
        Tracer.i("Application Id: " + BuildConfig.APPLICATION_ID
                + " || Version code: " + BuildConfig.VERSION_CODE
                + " || Version name: " + BuildConfig.VERSION_NAME
                + "\nGit Sha: " + BuildConfig.GIT_SHA
                + " || Build time:  " + BuildConfig.BUILD_TIME
                + " || Build flavor: " + BuildConfig.FLAVOR
                + " || Build type: " + BuildConfig.BUILD_TYPE);

        Tracer.i("Model: " + Build.MODEL
                + " || Build No: " + Build.FINGERPRINT
                + " || Brand: " + Build.BRAND
                + " || Device: " + Build.DEVICE
                + " || Build Id: " + Build.ID
                + " || Manufacturer: " + Build.MANUFACTURER);

        loadConfigurationValues();
        configureEverNote();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        GreenDaoOpenHelper helper2 = new GreenDaoOpenHelper(this, "noti-db", null);
        Database db = helper2.getWritableDb();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        //setAllDefaultMenusApplication();
        AppLifecycleTracker handler = new AppLifecycleTracker();
        registerActivityLifecycleCallbacks(handler);
        PackageUtil.enableAlarm(this);

    }


    public DaoSession getDaoSession() {
        return daoSession;
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
                .setEvernoteService(EverNoteConfig.EVERNOTE_SERVICE)
                .setSupportAppLinkedNotebooks(true)
                .setForceAuthenticationInThirdPartyApp(true)
                .build(EverNoteConfig.CONSUMER_KEY, EverNoteConfig.CONSUMER_SECRET)
                .asSingleton();

    }

    public FirebaseAnalytics getFirebaseAnalytics() {
        return mFirebaseAnalytics;
    }

    class AppLifecycleTracker implements Application.ActivityLifecycleCallbacks {

        private int numStarted = 0;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (numStarted == 0) {
                // app went to foreground
                Log.d(TAG, "Siempo is on foreground");
                launcherPrefs.isAppDefaultOrFront().put(true);

            }
            numStarted++;
        }

        @Override
        public void onActivityResumed(Activity activity) {
            if (PackageUtil.isSiempoLauncher(getApplicationContext())) {
                isSiempoLauncher = true;
                if (startTime == 0) {
                    startTime = System.currentTimeMillis();
                    FirebaseHelper.getIntance().logSiempoAsDefault("On", 0);
                }
            } else {
                isSiempoLauncher = false;
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
            if (PackageUtil.isSiempoLauncher(getApplicationContext())) {
                isSiempoLauncher = true;
            } else {
                if (isSiempoLauncher && startTime != 0) {
                    FirebaseHelper.getIntance().logSiempoAsDefault("Off", startTime);
                    startTime = 0;
                }
                isSiempoLauncher = false;
            }
        }

        @Override
        public void onActivityStopped(Activity activity) {
            numStarted--;

            if (numStarted == 0) {
                Log.d(TAG, "Siempo is on background");

                if (PackageUtil.isSiempoLauncher(getApplicationContext())) {
                    isSiempoLauncher = true;
                    launcherPrefs.isAppDefaultOrFront().put(true);

                } else {
                    launcherPrefs.isAppDefaultOrFront().put(false);
                }
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }

}
