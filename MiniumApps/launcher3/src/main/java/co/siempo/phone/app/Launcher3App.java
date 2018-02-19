package co.siempo.phone.app;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
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
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;

import co.siempo.phone.BuildConfig;
import co.siempo.phone.R;
import co.siempo.phone.db.DaoMaster;
import co.siempo.phone.db.DaoSession;
import co.siempo.phone.db.GreenDaoOpenHelper;
import co.siempo.phone.event.DefaultAppUpdate;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.log.LogConfig;
import co.siempo.phone.log.Tracer;
import co.siempo.phone.old.PreferenceListAdapter;
import co.siempo.phone.utils.PackageUtil;
import co.siempo.phone.utils.PrefSiempo;
import de.greenrobot.event.EventBus;

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
    //    @Pref
//    Launcher3Prefs_ launcherPrefs;
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


    /**
     * Configure the default application when application installed
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

        String notesPackage = getNotesPackageName();
        if (!notesPackage.equalsIgnoreCase("") && prefs.notesPackage().get().equalsIgnoreCase(""))
            prefs.notesPackage().put(notesPackage);

    }

    /**
     * Dialog to show the change the default application
     *
     * @param context
     * @param menuId
     * @param isOkayShow
     */
    public void showPreferenceAppListDialog(final Context context, final int menuId, final boolean isOkayShow) {
        resolveInfo = null;
        int pos = -1;
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new Dialog(context, R.style.MaterialDialogSheet);
        dialog.setContentView(R.layout.dialog_open_with);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
        }
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
        } else if (menuId == Constants.NOTES_PACKAGE) {
            appList = getNotesPackageList();
            if (isOkayShow) {
                for (int i = 0; i < appList.size(); i++) {
                    if (appList.get(i) != null && appList.get(i).activityInfo.packageName.equalsIgnoreCase(prefs.emailPackage().get())) {
                        resolveInfo = appList.get(i);
                        pos = i;
                    } else {
                        resolveInfo = null;
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
        final PreferenceListAdapter preferenceListAdapter = new PreferenceListAdapter(context, listView, appList, menuId);
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
                    Toast.makeText(context, R.string.please_select_application, Toast.LENGTH_SHORT).show();
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
                    } else if (menuId == Constants.NOTES_PACKAGE) {
                        prefs.notesPackage().put(resolveInfo.activityInfo.packageName);
                    } else if (menuId == Constants.EMAIL_PACKAGE) {
                        prefs.emailPackage().put(resolveInfo.activityInfo.packageName);
                        prefs.isEmailClicked().put(true);
                        prefs.isMessageClickedFirstTime().put(true);
                    }
                    dialog.dismiss();
                    EventBus.getDefault().post(new DefaultAppUpdate(true));
                } else {
                    if (menuId == 6) {
                        if (resolveInfo == null) {
                            prefs.notesPackage().put(context.getResources().getString(R.string.notes));
                        } else {
                            prefs.notesPackage().put(resolveInfo.activityInfo.packageName);
                        }

                        dialog.dismiss();
                        EventBus.getDefault().post(new DefaultAppUpdate(true));
                    } else {
                        Toast.makeText(context, R.string.please_select_application, Toast.LENGTH_SHORT).show();
                    }

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
                    } else if (menuId == Constants.CALENDER_PACKAGE) {
                        prefs.calenderPackage().put(resolveInfo.activityInfo.packageName);
                        prefs.isCalenderClicked().put(true);
                    } else if (menuId == Constants.CONTACT_PACKAGE) {
                        prefs.contactPackage().put(resolveInfo.activityInfo.packageName);
                        prefs.isContactClicked().put(true);
                    } else if (menuId == Constants.MAP_PACKAGE) {
                        prefs.mapPackage().put(resolveInfo.activityInfo.packageName);
                        prefs.isMapClicked().put(true);
                    } else if (menuId == Constants.PHOTOS_PACKAGE) {
                        prefs.photosPackage().put(resolveInfo.activityInfo.packageName);
                        prefs.isPhotosClicked().put(true);
                    } else if (menuId == Constants.CAMERA_PACKAGE) {
                        prefs.cameraPackage().put(resolveInfo.activityInfo.packageName);
                        prefs.isCameraClicked().put(true);
                    } else if (menuId == Constants.BROWSER_PACKAGE) {
                        prefs.browserPackage().put(resolveInfo.activityInfo.packageName);
                        prefs.isBrowserClicked().put(true);
                    } else if (menuId == Constants.CLOCK_PACKAGE) {
                        prefs.clockPackage().put(resolveInfo.activityInfo.packageName);
                        prefs.isClockClicked().put(true);
                    }
                    dialog.dismiss();
                    EventBus.getDefault().post(new DefaultAppUpdate(true));
                    new ActivityHelper(context).openAppWithPackageName(resolveInfo.activityInfo.packageName);

                } else {
                    Toast.makeText(context, R.string.please_select_application, Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
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
                PrefSiempo.getInstance(activity).write(PrefSiempo
                        .IS_APP_DEFAULT_OR_FRONT, true);
//                launcherPrefs.isAppDefaultOrFront().put(true);

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
                    PrefSiempo.getInstance(activity).write(PrefSiempo
                            .IS_APP_DEFAULT_OR_FRONT, true);
//                    launcherPrefs.isAppDefaultOrFront().put(true);

                } else {
                    PrefSiempo.getInstance(activity).write(PrefSiempo
                            .IS_APP_DEFAULT_OR_FRONT, false);
//                    launcherPrefs.isAppDefaultOrFront().put(false);
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
