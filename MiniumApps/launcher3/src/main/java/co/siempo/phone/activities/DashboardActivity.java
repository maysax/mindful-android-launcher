package co.siempo.phone.activities;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
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

import java.security.interfaces.DSAKey;
import java.util.ArrayList;

import co.siempo.phone.BuildConfig;
import co.siempo.phone.MainActivity;
import co.siempo.phone.R;
import co.siempo.phone.adapters.DashboardPagerAdapter;
import co.siempo.phone.app.Constants;
import co.siempo.phone.app.Launcher3App;
import co.siempo.phone.app.Launcher3Prefs;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.service.ApiClient_;
import co.siempo.phone.service.SiempoNotificationListener_;
import co.siempo.phone.utils.PermissionUtil;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.CoreApplication;
import minium.co.core.event.AppInstalledEvent;
import minium.co.core.event.CheckVersionEvent;
import minium.co.core.event.NFCEvent;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.UIUtils;

public class DashboardActivity extends CoreActivity {

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    PermissionUtil permissionUtil;

    private SharedPreferences launcher3Prefs;


    public static final String IS_FROM_HOME = "isFromHome";
    private String TAG = "DashboardActivity";

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private DashboardPagerAdapter mPagerAdapter;

    ConnectivityManager connectivityManager;

    AppUpdaterUtils appUpdaterUtils;


    boolean isApplicationLaunch = false;


    private AlertDialog notificationDialog;


    NotificationManager notificationManager;


    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Log.d(TAG, "Permission granted");
            loadViews();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            UIUtils.toast(DashboardActivity.this, "Permission denied");
            new TedPermission(DashboardActivity.this)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("If you reject permission, app can not provide you the seamless integration.\n\nPlease consider turn on permissions at Setting > Permission")
                    .setPermissions(Constants.PERMISSIONS)
                    .check();
        }
    };


    @Override
    protected void onResume() {
        super.onResume();

        permissionUtil = new PermissionUtil(this);
        if (!permissionUtil.hasGiven(PermissionUtil.CONTACT_PERMISSION)
                || !permissionUtil.hasGiven(PermissionUtil.CALL_PHONE_PERMISSION) || !permissionUtil.hasGiven(PermissionUtil.SEND_SMS_PERMISSION)
                || !permissionUtil.hasGiven(PermissionUtil.CAMERA_PERMISSION) || !permissionUtil.hasGiven(PermissionUtil.WRITE_EXTERNAL_STORAGE_PERMISSION)
                || !permissionUtil.hasGiven(PermissionUtil.NOTIFICATION_ACCESS) || !permissionUtil.hasGiven(PermissionUtil.DRAWING_OVER_OTHER_APPS)
                ) {

            Intent intent = new Intent(DashboardActivity.this, SiempoPermissionActivity_
                    .class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra(IS_FROM_HOME, true);
            startActivity(intent);

        } else {
            Log.d(TAG, "onResume.. ");

            loadViews();
//            startTime = System.currentTimeMillis();
            /**
             * Below logic is disable for NFC which is not used in existing development.
             */
//            try {
//                enableNfc(true);
//            } catch (Exception e) {
//                Tracer.e(e);
//                CoreApplication.getInstance().logException(e);
//            }
            // prevent keyboard up on old menu screen when coming back from other launcher
//            if (pager != null) pager.setCurrentItem(currentItem, true);
            //  currentIndex = currentItem;
        }
    }

    @Subscribe
    public void appInstalledEvent(AppInstalledEvent event) {
        if (event.isRunning()) {
            ((Launcher3App) CoreApplication.getInstance()).setAllDefaultMenusApplication();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        initView();
    }





    private void initView() {

        connectivityManager = (ConnectivityManager) getSystemService(Context
                .CONNECTIVITY_SERVICE);


        notificationManager =

                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        isApplicationLaunch = true;

        launcher3Prefs =
                getSharedPreferences("Launcher3Prefs", 0);
        checknavigatePermissions();
    }

    public void loadViews(){
        mPager = findViewById(R.id.pager);
        mPagerAdapter = new DashboardPagerAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(1);
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            mPager.setCurrentItem(1);
        }
    }

    public void checknavigatePermissions() {

        if (!launcher3Prefs.getBoolean("isAppInstalledFirstTime",true)) {
            Log.d(TAG, "Display upgrade dialog.");
            if (isApplicationLaunch) {
                checkUpgradeVersion();
            }
        }


        if (!isEnabled(DashboardActivity.this)) {

            notificatoinAccessDialog();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Show alert dialog to the user saying a separate permission is needed
            // Launch the settings activity if the user prefers
            if (!Settings.canDrawOverlays(DashboardActivity.this)) {
                Toast.makeText(DashboardActivity.this, R.string.msg_overlay_settings, Toast
                        .LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 102);
            } else {
                checkAppLoadFirstTime();
            }
        }
    }


    private void checkAppLoadFirstTime() {
        if (launcher3Prefs.getBoolean("isAppInstalledFirstTime",true)) {
            launcher3Prefs.edit().putBoolean("isAppInstalledFirstTime",false).apply();
            ((Launcher3App) CoreApplication.getInstance()).checkProfile();
            final ActivityHelper activityHelper = new ActivityHelper(DashboardActivity.this);
            if (!UIUtils.isMyLauncherDefault(DashboardActivity.this)) {

                android.os.Handler handler = new android.os.Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activityHelper.handleDefaultLauncher(DashboardActivity.this);
                        loadDialog();
                    }
                }, 1000);
            }
        } else {
            if (launcher3Prefs.getInt("getCurrentVersion",0) != 0) {
                if (!UIUtils.isMyLauncherDefault(this)
                        && BuildConfig.VERSION_CODE > launcher3Prefs.getInt("getCurrentVersion",0)) {
                    new ActivityHelper(this).handleDefaultLauncher(this);
                    loadDialog();
                    launcher3Prefs.edit().putInt("getCurrentVersion",UIUtils
                            .getCurrentVersionCode(this)).apply();
                } else {
                    launcher3Prefs.edit().putInt("getCurrentVersion",UIUtils
                            .getCurrentVersionCode(this)).apply();
                }
            } else {
                launcher3Prefs.edit().putInt("getCurrentVersion",UIUtils
                        .getCurrentVersionCode(this)).apply();
            }
        }
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
                                    ApiClient_.getInstance_(DashboardActivity.this)
                                            .checkAppVersion(CheckVersionEvent.ALPHA);
                                } else if (BuildConfig.FLAVOR.equalsIgnoreCase(getString(R.string.beta))) {
                                    ApiClient_.getInstance_(DashboardActivity.this)
                                            .checkAppVersion(CheckVersionEvent.BETA);
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


    /**
     * @return True if {@link android.service.notification.NotificationListenerService} is enabled.
     */
    public static boolean isEnabled(Context mContext) {

        ComponentName cn = new ComponentName(mContext, SiempoNotificationListener_.class);
        String flat = Settings.Secure.getString(mContext.getContentResolver(), "enabled_notification_listeners");
        return flat != null && flat.contains(cn.flattenToString());

        //return ServiceUtils.isNotificationListenerServiceRunning(mContext, SiempoNotificationListener_.class);
    }


    @Override
    protected void onStop() {
        if (notificationDialog != null && notificationDialog.isShowing()) {
            notificationDialog.dismiss();
        }
        super.onStop();
    }

    public void notificatoinAccessDialog() {
        notificationDialog = new AlertDialog.Builder(DashboardActivity.this)
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




    @Subscribe
    public void checkVersionEvent(CheckVersionEvent event) {
        Log.d(TAG, "Check Version event...");
        if (event.getVersionName() != null && event.getVersionName().equalsIgnoreCase(CheckVersionEvent.ALPHA)) {
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
                        launcher3Prefs.edit().putBoolean("updatePrompt",false)
                                .apply();
                        new ActivityHelper(DashboardActivity.this).openBecomeATester();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            if (isEnabled(DashboardActivity.this)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(DashboardActivity.this)) {
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
                if (!Settings.canDrawOverlays(DashboardActivity.this)) {
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




}
