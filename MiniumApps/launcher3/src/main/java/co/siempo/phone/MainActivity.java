package co.siempo.phone;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;

import co.siempo.phone.app.Constants;
import co.siempo.phone.app.Launcher3App;
import co.siempo.phone.app.Launcher3Prefs_;
import co.siempo.phone.db.DBUtility;
import co.siempo.phone.db.NotificationSwipeEvent;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.main.MainSlidePagerAdapter;
import co.siempo.phone.msg.SmsObserver;
import co.siempo.phone.pause.PauseActivity_;
import co.siempo.phone.service.ApiClient_;
import co.siempo.phone.service.SiempoNotificationListener_;
import co.siempo.phone.ui.SiempoPermissionActivity_;
import co.siempo.phone.util.PermissionUtil;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.CoreApplication;
import minium.co.core.event.AppInstalledEvent;
import minium.co.core.event.CheckVersionEvent;
import minium.co.core.event.NFCEvent;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.UIUtils;

import static minium.co.core.log.LogConfig.TRACE_TAG;

@EActivity(R.layout.activity_main)
public class MainActivity extends CoreActivity implements SmsObserver.OnSmsSentListener {


    public static final String IS_FROM_HOME = "isFromHome";
    private static final String TAG = "MainActivity";
    public static int currentItem = -1;
    public static String isTextLenghGreater = "";
    @Pref
    Launcher3Prefs_ launcher3Prefs;
    @ViewById
    ViewPager pager;


    MainSlidePagerAdapter sliderAdapter;

    @SystemService
    TelephonyManager telephonyManager;

    @SystemService
    ConnectivityManager connectivityManager;

    @SystemService
    NotificationManager notificationManager;
    @SystemService
    AudioManager audioManager;

    @Pref
    Launcher3Prefs_ launcherPrefs;
    ActivityState state;
    long startTime;
    boolean isApplicationLaunch = false;
    /**
     * Below function is use to check if latest version is available from play store or not
     * 1) It will check first with Appupdater library if it fails to identify then
     * 2) It will check with AWS logic.
     */
    AppUpdaterUtils appUpdaterUtils;
    private PermissionUtil permissionUtil;
    private AlertDialog notificationDialog;
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Log.d(TAG, "Permission granted");
            loadViews();
            logFirebase();
            checknavigatePermissions();

        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            UIUtils.toast(MainActivity.this, "Permission denied");
            new TedPermission(MainActivity.this)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("If you reject permission, app can not provide you the seamless integration.\n\nPlease consider turn on permissions at Setting > Permission")
                    .setPermissions(Constants.PERMISSIONS)
                    .check();
        }
    };

    /**
     * @return True if {@link android.service.notification.NotificationListenerService} is enabled.
     */
    public static boolean isEnabled(Context mContext) {

        ComponentName cn = new ComponentName(mContext, SiempoNotificationListener_.class);
        String flat = Settings.Secure.getString(mContext.getContentResolver(), "enabled_notification_listeners");
        return flat != null && flat.contains(cn.flattenToString());

        //return ServiceUtils.isNotificationListenerServiceRunning(mContext, SiempoNotificationListener_.class);
    }

    @Trace(tag = TRACE_TAG)
    @AfterViews
    void afterViews() {
        isApplicationLaunch = true;
        state = ActivityState.AFTERVIEW;
        Log.d(TAG, "afterViews event called");


//        new TedPermission(this)
//                .setPermissionListener(permissionlistener)
//                .setDeniedMessage("If you reject permission, app can not provide you the seamless integration.\n\nPlease consider turn on permissions at Setting > Permission")
//                .setPermissions(Constants.PERMISSIONS)
//                .check();

        loadViews();
        logFirebase();
        checknavigatePermissions();

        launcherPrefs.updatePrompt().put(true);

    }

    @Subscribe
    public void appInstalledEvent(AppInstalledEvent event) {
        if (event.isRunning()) {
            ((Launcher3App) CoreApplication.getInstance()).setAllDefaultMenusApplication();
        }
    }

    private void checkAppLoadFirstTime() {
        if (launcherPrefs.isAppInstalledFirstTime().get()) {
            launcherPrefs.isAppInstalledFirstTime().put(false);
            ((Launcher3App) CoreApplication.getInstance()).checkProfile();
            final ActivityHelper activityHelper = new ActivityHelper(MainActivity.this);
            if (!UIUtils.isMyLauncherDefault(MainActivity.this)) {

                android.os.Handler handler = new android.os.Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activityHelper.handleDefaultLauncher(MainActivity.this);
                        loadDialog();
                    }
                }, 1000);
            }
        } else {
            if (launcherPrefs.getCurrentVersion().get() != 0) {
                if (!UIUtils.isMyLauncherDefault(this)
                        && BuildConfig.VERSION_CODE > launcherPrefs.getCurrentVersion().get()) {
                    new ActivityHelper(this).handleDefaultLauncher(this);
                    loadDialog();
                    launcherPrefs.getCurrentVersion().put(UIUtils.getCurrentVersionCode(this));
                } else {
                    launcherPrefs.getCurrentVersion().put(UIUtils.getCurrentVersionCode(this));
                }
            } else {
                launcherPrefs.getCurrentVersion().put(UIUtils.getCurrentVersionCode(this));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        state = ActivityState.ACTIVITY_RESULT;

        if (requestCode == 100) {
            if (isEnabled(MainActivity.this)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(MainActivity.this)) {
                        Toast.makeText(this, R.string.msg_overlay_settings, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 102);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                                && !notificationManager.isNotificationPolicyAccessGranted()) {
                            Intent intent = new Intent(
                                    android.provider.Settings
                                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                            startActivityForResult(intent, 103);
                        } else {
                            checkAppLoadFirstTime();
                        }
                    }
                }

            } else {
                notificatoinAccessDialog();
            }
        }
        if (requestCode == 102) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                checkAppLoadFirstTime();
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(MainActivity.this)) {
                    Toast.makeText(this, R.string.msg_overlay_settings, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 102);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                            && !notificationManager.isNotificationPolicyAccessGranted()) {
                        Intent intent = new Intent(
                                android.provider.Settings
                                        .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                        startActivityForResult(intent, 103);
                    } else {
                        checkAppLoadFirstTime();
                    }
                }
            }
        }

        if (requestCode == 103) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                    && !notificationManager.isNotificationPolicyAccessGranted()) {
                Intent intent = new Intent(
                        android.provider.Settings
                                .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivityForResult(intent, 103);
            } else {
                checkAppLoadFirstTime();
            }
        }
    }

    @UiThread(delay = 500)
    void loadViews() {
        sliderAdapter = new MainSlidePagerAdapter(getFragmentManager());
        pager.setAdapter(sliderAdapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (currentItem != -1 && currentItem != position) {
                    if (position == 0) {
                        FirebaseHelper.getIntance().logScreenUsageTime(FirebaseHelper.SIEMPO_MENU, startTime);
                        startTime = System.currentTimeMillis();
                    } else if (position == 1) {
                        FirebaseHelper.getIntance().logScreenUsageTime(FirebaseHelper.IF_SCREEN, startTime);
                        startTime = System.currentTimeMillis();
                    }
                }
                currentItem = position;
                try {
                    if (position == 1 && getCurrentFocus() != null)
                        //noinspection ConstantConditions
                        UIUtils.hideSoftKeyboard(MainActivity.this, getCurrentFocus().getWindowToken());
                } catch (Exception e) {
                    e.printStackTrace();
                    CoreApplication.getInstance().logException(e);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void logFirebase() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                Tracer.d(String.format("Device Id ::%s", telephonyManager.getDeviceId()));
                FirebaseHelper.getIntance().getFirebaseAnalytics().setUserId(telephonyManager.getDeviceId());
            }
        } else {
            Tracer.d(String.format("Device Id ::%s", telephonyManager.getDeviceId()));
            FirebaseHelper.getIntance().getFirebaseAnalytics().setUserId(telephonyManager.getDeviceId());
        }
    }


    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
    }

    /**
     * Below function is use for further development when pause feature will enable.
     *
     * @KeyDown(KeyEvent.KEYCODE_VOLUME_UP) void volumeUpPressed() {
     * Tracer.i("Volume up pressed in MainActivity");
     * PauseActivity_.intent(this).start();
     * }
     */

    @Subscribe
    public void checkVersionEvent(CheckVersionEvent event) {
        Log.d(TAG, "Check Version event...");
        if (event.getVersionName().equalsIgnoreCase(CheckVersionEvent.ALPHA)) {
            if (event.getVersion() > UIUtils.getCurrentVersionCode(this)) {
                Tracer.d("Installed version: " + UIUtils.getCurrentVersionCode(this) + " Found: " + event.getVersion());
                showUpdateDialog(CheckVersionEvent.ALPHA);
                appUpdaterUtils = null;
            } else {
                ApiClient_.getInstance_(this).checkAppVersion(CheckVersionEvent.BETA);
            }
        } else {
            if (event.getVersion() > UIUtils.getCurrentVersionCode(this)) {
                Tracer.d("Installed version: " + UIUtils.getCurrentVersionCode(this) + " Found: " + event.getVersion());
                showUpdateDialog(CheckVersionEvent.BETA);
                appUpdaterUtils = null;
            } else {
                Tracer.d("Installed version: " + "Up to date.");
            }
        }
    }

    private void showUpdateDialog(String str) {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            UIUtils.confirmWithCancel(this, "", str.equalsIgnoreCase(CheckVersionEvent.ALPHA) ? "New alpha version found! Would you like to update Siempo?" : "New beta version found! Would you like to update Siempo?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        launcherPrefs.updatePrompt().put(false);
                        new ActivityHelper(MainActivity.this).openBecomeATester();
                    }
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    isApplicationLaunch = false;
                }
            });
        } else {
            Log.d(TAG, getString(R.string.nointernetconnection));
        }
    }


    @Override
    public void onSmsSent(int threadId) {
//        try {
// Don't remove this code,this code is needed for feature refrence.
//            UIUtils.hideSoftKeyboard(MainActivity.this,getWindow().getDecorView().getWindowToken());
//            Intent defineIntent = new Intent(Intent.ACTION_VIEW);
//            defineIntent.setData(Uri.parse("content://mms-sms/conversations/"+threadId));
//            defineIntent.setData(Uri.parse("smsto:" + manager.get(TokenItemType.CONTACT).getExtra2()));
//            defineIntent.setType("vnd.android-dir/mms-sms");
//            defineIntent.addCategory(Intent.CATEGORY_DEFAULT);
//            startActivity(defineIntent);
        //manager.clear();
//        } catch (Exception e) {
//        CoreApplication.getInstance().logException(e);
//            Tracer.e(e, e.getMessage());
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.isTextLenghGreater = "";

    }

    @Override
    protected void onStop() {
        state = ActivityState.STOP;
        if (notificationDialog != null && notificationDialog.isShowing()) {
            notificationDialog.dismiss();
        }
        super.onStop();
    }


    @Override
    protected void onResume() {
        super.onResume();

        permissionUtil = new PermissionUtil(this);
        if (!permissionUtil.hasGiven(PermissionUtil.CONTACT_PERMISSION)
                || !permissionUtil.hasGiven(PermissionUtil.CALL_PHONE_PERMISSION) || !permissionUtil.hasGiven(PermissionUtil.SEND_SMS_PERMISSION)
                || !permissionUtil.hasGiven(PermissionUtil.CAMERA_PERMISSION) || !permissionUtil.hasGiven(PermissionUtil.WRITE_EXTERNAL_STORAGE_PERMISSION)
                || !permissionUtil.hasGiven(PermissionUtil.NOTIFICATION_ACCESS) || !permissionUtil.hasGiven(PermissionUtil.DRAWING_OVER_OTHER_APPS)
                ) {

            Intent intent = new Intent(MainActivity.this, SiempoPermissionActivity_.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra(IS_FROM_HOME, true);
            startActivity(intent);

        } else {
            Log.d(TAG, "onResume.. ");
            startTime = System.currentTimeMillis();
            try {
                enableNfc(true);
            } catch (Exception e) {
                Tracer.e(e);
                CoreApplication.getInstance().logException(e);
            }
            // prevent keyboard up on old menu screen when coming back from other launcher
            if (pager != null) pager.setCurrentItem(currentItem, true);
            //  currentIndex = currentItem;
        }


    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "ACTION ONPAUSE");
        enableNfc(false);
        if (currentItem == 0) {
            FirebaseHelper.getIntance().logScreenUsageTime(FirebaseHelper.IF_SCREEN, startTime);
        } else if (currentItem == 1) {
            FirebaseHelper.getIntance().logScreenUsageTime(FirebaseHelper.SIEMPO_MENU, startTime);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        currentItem = 0;
        long notificationCount = DBUtility.getTableNotificationSmsDao().count() + DBUtility.getCallStorageDao().count();
        if (notificationCount == 0) {
            EventBus.getDefault().post(new NotificationSwipeEvent(true));
        }
        Log.d(TAG, "ACTION onNewIntent");
        if (intent.getAction() != null && intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            Tracer.i("NFC Tag detected");
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            EventBus.getDefault().post(new NFCEvent(true, tag));
        }
    }

    private void enableNfc(boolean isEnable) {
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter != null) {
            if (isEnable) {
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                        getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
                IntentFilter filter = new IntentFilter();
                filter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
                filter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
                filter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
                nfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[]{filter}, techList);
            } else {
                nfcAdapter.disableForegroundDispatch(this);
            }
        }
    }

    @Subscribe
    public void nfcEvent(NFCEvent event) {
        if (event.isConnected()) {
            PauseActivity_.intent(this).tag(event.getTag()).start();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            //Below snippet is use to remove notification fragment (Siempo Notification Screen) if visible on screen
            if (pager != null && pager.getCurrentItem() == 1) {
                pager.setCurrentItem(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            CoreApplication.getInstance().logException(e);
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (state != ActivityState.AFTERVIEW && state != ActivityState.ACTIVITY_RESULT) {
//            checkAllPermissions();
            Log.d(TAG, "Restart ... ");
        }

    }

    public void checkVersionFromAppUpdater() {
        new AppUpdater(this)
                .setDisplay(Display.DIALOG)
                .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                .showEvery(5)
                .setTitleOnUpdateAvailable("Update available")
                .setContentOnUpdateAvailable("New version found! Would you like to update Siempo?")
                .setTitleOnUpdateNotAvailable("Update not available")
                .setContentOnUpdateNotAvailable("No update available. Check for updates again later!")
                .setButtonUpdate("Update")
                .setButtonDismiss("Maybe later")
                .start();
    }

    public void checkUpgradeVersion() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null) {
            Log.d(TAG, "Active network..");
            appUpdaterUtils = new AppUpdaterUtils(this)
                    .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                    .withListener(new AppUpdaterUtils.UpdateListener() {
                        @Override
                        public void onSuccess(Update update, Boolean isUpdateAvailable) {
                            Log.d(TAG, "on success");
                            if (update.getLatestVersionCode() != null) {
                                Log.d(TAG, "check version from AppUpdater library");
                                checkVersionFromAppUpdater();
                                appUpdaterUtils = null;
                            } else {
                                Log.d(TAG, "check version from AWS");
                                if (BuildConfig.FLAVOR.equalsIgnoreCase(getString(R.string.alpha))) {
                                    ApiClient_.getInstance_(MainActivity.this).checkAppVersion(CheckVersionEvent.ALPHA);
                                } else if (BuildConfig.FLAVOR.equalsIgnoreCase(getString(R.string.beta))) {
                                    ApiClient_.getInstance_(MainActivity.this).checkAppVersion(CheckVersionEvent.BETA);
                                }
                            }
                        }

                        @Override
                        public void onFailed(AppUpdaterError error) {
                            if (BuildConfig.DEBUG) {
                                Log.d(TAG, " AppUpdater Error ::: " + error.toString());
                            }
                        }
                    });

            appUpdaterUtils.start();
        } else {
            Log.d(TAG, getString(R.string.nointernetconnection));
        }
    }

    public void notificatoinAccessDialog() {
        notificationDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle(null)
                .setMessage(getString(R.string.msg_noti_service_dialog))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivityForResult(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"), 100);
                    }
                })
                .show();
    }

    public void checknavigatePermissions() {
        if (!launcherPrefs.isAppInstalledFirstTime().get()) {
            Log.d(TAG, "Display upgrade dialog.");
            if (isApplicationLaunch) {
                checkUpgradeVersion();
            }
        }


        if (!isEnabled(MainActivity.this)) {

            notificatoinAccessDialog();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Show alert dialog to the user saying a separate permission is needed
            // Launch the settings activity if the user prefers
            if (!Settings.canDrawOverlays(MainActivity.this)) {
                Toast.makeText(MainActivity.this, R.string.msg_overlay_settings, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 102);
            } else {
                checkAppLoadFirstTime();
            }
        }
    }

    public void checkAllPermissions() {
        new TedPermission(MainActivity.this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        logFirebase();
                        checknavigatePermissions();

                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        UIUtils.toast(MainActivity.this, "Permission denied");
                        checkAllPermissions();
                    }
                })
                .setDeniedMessage("If you reject permission, app can not provide you the seamless integration.\n\nPlease consider turn on permissions at Setting > Permission")
                .setPermissions(Constants.PERMISSIONS)
                .check();
    }


    private enum ActivityState {
        AFTERVIEW,
        RESTART,
        ACTIVITY_RESULT,
        STOP
    }

}
