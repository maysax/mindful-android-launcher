package co.siempo.phone.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.greenrobot.greendao.database.Database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import co.siempo.phone.MainActivity;
import co.siempo.phone.R;
import co.siempo.phone.SiempoNotificationBar.ViewService_;
import co.siempo.phone.db.DaoMaster;
import co.siempo.phone.db.DaoSession;
import co.siempo.phone.db.GreenDaoOpenHelper;
import co.siempo.phone.event.DefaultAppUpdate;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.kiss.IconsHandler;
import co.siempo.phone.old.PreferenceListAdapter;
import co.siempo.phone.util.PackageUtil;
import de.greenrobot.event.EventBus;
import minium.co.core.BuildConfig;
import minium.co.core.app.CoreApplication;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.config.Config;
import minium.co.core.log.LogConfig;
import minium.co.core.log.Tracer;
import minium.co.core.util.DateUtils;
import co.siempo.phone.SiempoNotificationBar.ApplicationLifecycleHandler;

/**
 * Created by Shahab on 2/16/2017.
 */
@EApplication
public class Launcher3App extends CoreApplication {

    private final String TRACE_TAG = LogConfig.TRACE_TAG + "Launcher3App";
    public static final String DND_START_STOP_ACTION = "siempo.intent.action.DND_START_STOP";
    private DaoSession daoSession;
    private final String TAG = "SiempoActivityLifeCycle";

    @Pref
    public DroidPrefs_ prefs;

    @Pref
    Launcher3Prefs_ launcherPrefs;

    private FirebaseAnalytics mFirebaseAnalytics;

    @SuppressLint("StaticFieldLeak")
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

        //if (launcherPrefs.isAppInstalledFirstTime().get()) {
        setAllDefaultMenusApplication();
        //}



        AppLifecycleTracker handler = new AppLifecycleTracker();

        registerActivityLifecycleCallbacks(handler);

    }


    class AppLifecycleTracker implements Application.ActivityLifecycleCallbacks  {

        private int numStarted = 0;


        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (numStarted == 0) {
                // app went to foreground
                Log.d(TAG,"Siempo is on foreground");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(getApplicationContext())) {
                        Log.d(TAG,"Display Siempo Status bar");
                        ViewService_.intent(getApplicationContext()).showMask().start();
                    }
                    else{
                        Log.d(TAG,"Overlay is off");
                    }
                }
                else{
                    Log.d(TAG,"Display Siempo Status bar");
                    ViewService_.intent(getApplicationContext()).showMask().start();
                }

            }
            numStarted++;
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            numStarted--;
            if (numStarted == 0) {
                Log.d(TAG,"Siempo is on background");
                // app went to background
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(getApplicationContext())) {
                        if(!PackageUtil.isSiempoLauncher(getApplicationContext())) {
                            Log.d(TAG,"Hide Siempo Status bar");
                            ViewService_.intent(getApplicationContext()).hideMask().start();
                        }
                    }
                }
                else{
                    Log.d(TAG,"Hide Siempo Status Bar");
                    ViewService_.intent(getApplicationContext()).hideMask().start();
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
    /**
     * Configure the default application when application insatlled
     */
    public void setAllDefaultMenusApplication() {
        String callPackage = getCallPackageName();
        if (!callPackage.equalsIgnoreCase("") && prefs.callPackage().get().equalsIgnoreCase(""))
            prefs.callPackage().put(callPackage);

        String messagePackage = getMessagePackageName();
        if (!messagePackage.equalsIgnoreCase("") && prefs.messagePackage().get().equalsIgnoreCase(""))
            prefs.messagePackage().put(messagePackage);

        String calenderPackage = getCalenderPackageName();
        if (!calenderPackage.equalsIgnoreCase("") && prefs.calenderPackage().get().equalsIgnoreCase(""))
            prefs.calenderPackage().put(calenderPackage);

        String contactPackage = getContactPackageName();
        if (!contactPackage.equalsIgnoreCase("") && prefs.contactPackage().get().equalsIgnoreCase(""))
            prefs.contactPackage().put(contactPackage);

        String mapPackage = getMapPackageName();
        if (!mapPackage.equalsIgnoreCase("") && prefs.mapPackage().get().equalsIgnoreCase(""))
            prefs.mapPackage().put(mapPackage);

        String photosPackage = getPhotosPackageName();
        if (!photosPackage.equalsIgnoreCase("") && prefs.photosPackage().get().equalsIgnoreCase(""))
            prefs.photosPackage().put(photosPackage);

        String cameraPackage = getCameraPackageName();
        if (!cameraPackage.equalsIgnoreCase("") && prefs.cameraPackage().get().equalsIgnoreCase(""))
            prefs.cameraPackage().put(cameraPackage);

        String browserPackage = getBrowserPackageName();
        if (!browserPackage.equalsIgnoreCase("") && prefs.browserPackage().get().equalsIgnoreCase(""))
            prefs.browserPackage().put(browserPackage);

        String clockPackage = getClockPackageName();
        if (!clockPackage.equalsIgnoreCase("") && prefs.clockPackage().get().equalsIgnoreCase(""))
            prefs.clockPackage().put(clockPackage);

        String emailPackage = getMailPackageName();
        if (!emailPackage.equalsIgnoreCase("") && prefs.emailPackage().get().equalsIgnoreCase(""))
            prefs.emailPackage().put(emailPackage);
    }

    ResolveInfo resolveInfo;
    ArrayList<ResolveInfo> appList;
    int pos = -1;

    /**
     * Dialog to show the change the default application
     *
     * @param context
     * @param menuId
     * @param isOkayShow
     */
    public void showPreferenceAppListDialog(final Context context, final int menuId, final boolean isOkayShow) {
        resolveInfo = null;
        pos = -1;
        final Dialog dialog = new Dialog(context, R.style.MaterialDialogSheet);
        dialog.setContentView(R.layout.dialog_open_with);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        appList = new ArrayList<>();
        if (menuId == Constants.CALL_PACKAGE) {
            appList = getCallPackageList();
            if (isOkayShow) {
                for (int i = 0; i < appList.size(); i++) {
                    if (appList.get(i).activityInfo.packageName.equalsIgnoreCase(prefs.callPackage().get())) {
                        resolveInfo = appList.get(i);
                        pos = i;
                    }
                }
            }
        } else if (menuId == Constants.MESSAGE_PACKAGE) {
            appList = getMessagePackageList();
            if (isOkayShow) {
                for (int i = 0; i < appList.size(); i++) {
                    if (appList.get(i).activityInfo.packageName.equalsIgnoreCase(prefs.messagePackage().get())) {
                        resolveInfo = appList.get(i);
                        pos = i;
                    }
                }
            }
        } else if (menuId == Constants.CALENDER_PACKAGE) {
            appList = getCalenderPackageList();
            if (isOkayShow) {
                for (int i = 0; i < appList.size(); i++) {
                    if (appList.get(i).activityInfo.packageName.equalsIgnoreCase(prefs.calenderPackage().get())) {
                        resolveInfo = appList.get(i);
                        pos = i;
                    }
                }
            }
        } else if (menuId == Constants.CONTACT_PACKAGE) {
            appList = getContactPackageList();
            if (isOkayShow) {
                for (int i = 0; i < appList.size(); i++) {
                    if (appList.get(i).activityInfo.packageName.equalsIgnoreCase(prefs.contactPackage().get())) {
                        resolveInfo = appList.get(i);
                        pos = i;
                    }
                }
            }
        } else if (menuId == Constants.MAP_PACKAGE) {
            appList = getMapPackageList();
            if (isOkayShow) {
                for (int i = 0; i < appList.size(); i++) {
                    if (appList.get(i).activityInfo.packageName.equalsIgnoreCase(prefs.mapPackage().get())) {
                        resolveInfo = appList.get(i);
                        pos = i;
                    }
                }
            }
        } else if (menuId == Constants.PHOTOS_PACKAGE) {
            appList = getPhotosPackageList();
            if (isOkayShow) {
                for (int i = 0; i < appList.size(); i++) {
                    if (appList.get(i).activityInfo.packageName.equalsIgnoreCase(prefs.photosPackage().get())) {
                        resolveInfo = appList.get(i);
                        pos = i;
                    }
                }
            }
        } else if (menuId == Constants.CAMERA_PACKAGE) {
            appList = getCameraPackageList();
            if (isOkayShow) {
                for (int i = 0; i < appList.size(); i++) {
                    if (appList.get(i).activityInfo.packageName.equalsIgnoreCase(prefs.cameraPackage().get())) {
                        resolveInfo = appList.get(i);
                        pos = i;
                    }
                }
            }
        } else if (menuId == Constants.BROWSER_PACKAGE) {
            appList = getBrowserPackageList();
            if (isOkayShow) {
                for (int i = 0; i < appList.size(); i++) {
                    if (appList.get(i).activityInfo.packageName.equalsIgnoreCase(prefs.browserPackage().get())) {
                        resolveInfo = appList.get(i);
                        pos = i;
                    }
                }
            }
        } else if (menuId == Constants.CLOCK_PACKAGE) {
            appList = getClockPackageList();
            if (isOkayShow) {
                for (int i = 0; i < appList.size(); i++) {
                    if (appList.get(i).activityInfo.packageName.equalsIgnoreCase(prefs.clockPackage().get())) {
                        resolveInfo = appList.get(i);
                    }
                }
            }
        } else if (menuId == Constants.EMAIL_PACKAGE) {
            appList = getEmailPackageList();
            if (isOkayShow) {
                for (int i = 0; i < appList.size(); i++) {
                    if (appList.get(i).activityInfo.packageName.equalsIgnoreCase(prefs.emailPackage().get())) {
                        resolveInfo = appList.get(i);
                        pos = i;
                    }
                }
            }
        }
        final ListView listView = dialog.findViewById(R.id.listApps);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        Button btnJustOnce = dialog.findViewById(R.id.btnJustOnce);
        Button btnAlways = dialog.findViewById(R.id.btnAlways);
        Button btnOkay = dialog.findViewById(R.id.btnOkay);
        if (isOkayShow) {
            btnOkay.setVisibility(View.VISIBLE);
            btnAlways.setVisibility(View.GONE);
            btnJustOnce.setVisibility(View.GONE);
        } else {
            btnAlways.setVisibility(View.VISIBLE);
            btnJustOnce.setVisibility(View.VISIBLE);
            btnOkay.setVisibility(View.GONE);
        }
        final PreferenceListAdapter preferenceListAdapter = new PreferenceListAdapter(context, listView, appList, pos);
        listView.setAdapter(preferenceListAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setItemChecked(pos, true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                resolveInfo = appList.get(i);
            }
        });
        btnJustOnce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (resolveInfo != null) {
                    if (menuId == Constants.CALL_PACKAGE) {
                        prefs.callPackage().put(resolveInfo.activityInfo.packageName);
                        prefs.isCallClickedFirstTime().put(true);
                    } else if (menuId == Constants.MESSAGE_PACKAGE) {
                        prefs.messagePackage().put(resolveInfo.activityInfo.packageName);
                        prefs.isMessageClickedFirstTime().put(true);
                    } else if (menuId == Constants.EMAIL_PACKAGE) {
                        prefs.emailPackage().put(resolveInfo.activityInfo.packageName);
                        prefs.isEmailClickedFirstTime().put(true);
                    }
                    dialog.dismiss();
                    new ActivityHelper(context).openAppWithPackageName(resolveInfo.activityInfo.packageName);
                    EventBus.getDefault().post(new DefaultAppUpdate(true));
                } else {
                    Toast.makeText(context, "Please select application.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (resolveInfo != null) {
                    if (menuId == Constants.CALL_PACKAGE) {
                        prefs.callPackage().put(resolveInfo.activityInfo.packageName);
                        prefs.isCallClicked().put(true);
                        prefs.isCallClickedFirstTime().put(true);
                    } else if (menuId == Constants.MESSAGE_PACKAGE) {
                        prefs.messagePackage().put(resolveInfo.activityInfo.packageName);
                        prefs.isMessageClicked().put(true);
                        prefs.isMessageClickedFirstTime().put(true);
                    } else if (menuId == Constants.CALENDER_PACKAGE) {
                        prefs.calenderPackage().put(resolveInfo.activityInfo.packageName);
                    } else if (menuId == Constants.CONTACT_PACKAGE) {
                        prefs.contactPackage().put(resolveInfo.activityInfo.packageName);
                    } else if (menuId == Constants.MAP_PACKAGE) {
                        prefs.mapPackage().put(resolveInfo.activityInfo.packageName);
                    } else if (menuId == Constants.PHOTOS_PACKAGE) {
                        prefs.photosPackage().put(resolveInfo.activityInfo.packageName);
                    } else if (menuId == Constants.CAMERA_PACKAGE) {
                        prefs.cameraPackage().put(resolveInfo.activityInfo.packageName);
                    } else if (menuId == Constants.BROWSER_PACKAGE) {
                        prefs.browserPackage().put(resolveInfo.activityInfo.packageName);
                    } else if (menuId == Constants.CLOCK_PACKAGE) {
                        prefs.clockPackage().put(resolveInfo.activityInfo.packageName);
                    } else if (menuId == Constants.EMAIL_PACKAGE) {
                        prefs.emailPackage().put(resolveInfo.activityInfo.packageName);
                        prefs.isEmailClicked().put(true);
                        prefs.isMessageClickedFirstTime().put(true);
                    }
                    dialog.dismiss();
                    EventBus.getDefault().post(new DefaultAppUpdate(true));
                } else {
                    Toast.makeText(context, "Please select application.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnAlways.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (resolveInfo != null) {
                    if (menuId == Constants.CALL_PACKAGE) {
                        prefs.callPackage().put(resolveInfo.activityInfo.packageName);
                        prefs.isCallClicked().put(true);
                        prefs.isCallClickedFirstTime().put(true);
                    } else if (menuId == Constants.MESSAGE_PACKAGE) {
                        prefs.messagePackage().put(resolveInfo.activityInfo.packageName);
                        prefs.isMessageClicked().put(true);
                        prefs.isMessageClickedFirstTime().put(true);
                    } else if (menuId == Constants.EMAIL_PACKAGE) {
                        prefs.emailPackage().put(resolveInfo.activityInfo.packageName);
                        prefs.isEmailClicked().put(true);
                        prefs.isEmailClickedFirstTime().put(true);
                    }
                    dialog.dismiss();
                    EventBus.getDefault().post(new DefaultAppUpdate(true));
                    new ActivityHelper(context).openAppWithPackageName(resolveInfo.activityInfo.packageName);

                } else {
                    Toast.makeText(context, "Please select application.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
//        listView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (pos != -1 && isOkayShow) {
//                    listView.performItemClick(listView, pos, listView.getItemIdAtPosition(pos));
//                    listView.setItemChecked(pos,true);
//                }
//            }
//        },2000);

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
