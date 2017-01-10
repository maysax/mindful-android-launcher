package com.itconquest.tracking;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import minium.co.core.event.CheckVersionEvent;
import minium.co.core.event.DownloadApkEvent;
import com.itconquest.tracking.listener.NotificationListener_;
import com.itconquest.tracking.services.ApiClient_;
import com.itconquest.tracking.services.GlobalTouchService_;
import com.itconquest.tracking.services.HomePressService_;
import com.itconquest.tracking.services.ScreenOnOffService_;
import com.itconquest.tracking.util.FileUtil;
import com.itconquest.tracking.util.TrackingLogger;
import com.itconquest.tracking.util.TrackingPref_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.greenrobot.event.Subscribe;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.DateUtils;
import minium.co.core.util.ServiceUtils;
import minium.co.core.util.UIUtils;

@EActivity(resName = "activity_main_tracking")
public class MainActivity extends CoreActivity {

    public final static int REQUEST_CODE = 20;

    @ViewById
    Toolbar toolbar;

    @Pref
    TrackingPref_ trackingPrefs;

    @ViewById
    FloatingActionButton fab;

    @ViewById
    TextView txtVersion;

    @SystemService
    UsageStatsManager usageStatsManager;

    @SystemService
    ConnectivityManager connectivityManager;

    boolean isDisplayedActionNotificationListenerDialog = false;
    boolean isDisplayedPermitUsageAccessDialog = false;

    @AfterViews
    void afterViews() {
        setSupportActionBar(toolbar);
        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission, app can not provide you the seamless integration.\n\nPlease consider turn on permissions at Setting > Permission")
                .setPermissions(Manifest.permission.SYSTEM_ALERT_WINDOW,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();

        txtVersion.setText(String.format(Locale.getDefault(), "Version: %s", BuildConfig.VERSION_NAME));
        new FileUtil().deleteOldApk();

    }

    @Background
    void startServices() {
        if (trackingPrefs.isTrackingRunning().get()) {
            GlobalTouchService_.intent(getApplication()).start();
            ScreenOnOffService_.intent(getApplication()).start();
            HomePressService_.intent(getApplication()).start();

        } else {
            getAppUsage();
            GlobalTouchService_.intent(getApplication()).stop();
            ScreenOnOffService_.intent(getApplication()).stop();
            HomePressService_.intent(getApplication()).stop();
            startUpload();
        }
    }

    private void getAppUsage() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        List<UsageStats> queryUsageStats = usageStatsManager
                .queryUsageStats(UsageStatsManager.INTERVAL_BEST, cal.getTimeInMillis(),
                        System.currentTimeMillis());

        for (UsageStats stat : queryUsageStats) {

            if (stat.getTotalTimeInForeground() != 0) {

                String info = "Package: " + stat.getPackageName()
                        + " Usage time: " + DateUtils.interval(stat.getTotalTimeInForeground())
                        + " Last time used: " + SimpleDateFormat.getDateTimeInstance().format(new Date(stat.getLastTimeUsed()));

                TrackingLogger.log(info, null);
                Tracer.d(info);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isEnabled(this) && !isDisplayedActionNotificationListenerDialog) {
            isDisplayedActionNotificationListenerDialog = true;
            UIUtils.confirm(this, "Tracker service is not enabled. Please allow Tracking to access notification service", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                    dialog.dismiss();
                }
            });
        } else if (!checkUserStatPermission() && !isDisplayedPermitUsageAccessDialog) {
            isDisplayedPermitUsageAccessDialog = true;
            UIUtils.confirm(this, "Tracker usage stats is not enabled. Please allow Tracking to access usage stats", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                    dialog.dismiss();
                }
            });
        }
    }

    private boolean checkUserStatPermission() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            Tracer.d("Usage stat permission: " + mode);
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            Tracer.d("Usage stat permission: " + e.getMessage());
            return false;
        }
    }

    /** @return True if {@link NotificationListener_} is enabled. */
    public static boolean isEnabled(Context mContext) {
        return ServiceUtils.isNotificationListenerServiceRunning(mContext, NotificationListener_.class);
    }

    void loadViews() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (trackingPrefs.isTrackingRunning().get()) {
                    Snackbar.make(view, "Tracking paused", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    fab.setImageResource(android.R.drawable.ic_media_play);

                } else {
                    Snackbar.make(view, "Tracking started", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    fab.setImageResource(android.R.drawable.ic_media_pause);
                }

                trackingPrefs.isTrackingRunning().put(!trackingPrefs.isTrackingRunning().get());
                startServices();

            }
        });

        if (trackingPrefs.isTrackingRunning().get()) {
            fab.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            fab.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_tracking, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_upload) {
            startUpload();
        }

        return super.onOptionsItemSelected(item);
    }

    @UiThread
    void startUpload() {
        UIUtils.toast(this, "Uploading file...");
        uploadFileToFTP();
        uploadFileToAWS();
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            loadViews();
            checkVersion();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            UIUtils.toast(MainActivity.this, "Permission denied");
        }
    };

    @Background
    void uploadFileToFTP() {
        ApiClient_.getInstance_(this).uploadFileToFTP();
    }

    @Background
    void uploadFileToAWS() {
        ApiClient_.getInstance_(this).uploadFileToAWS();
    }

    void checkVersion() {
        ApiClient_.getInstance_(this).checkAppVersion();
    }

    @Subscribe
    public void checkVersionEvent(CheckVersionEvent event) {
        Tracer.d("Installed version: " + BuildConfig.VERSION_CODE + " Found: " + event.getVersion());
        if (event.getVersion() > BuildConfig.VERSION_CODE) {
            UIUtils.toast(this, "New version found! Downloading apk...");
            downloadApk();
        }
    }

    private void downloadApk() {
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
            ApiClient_.getInstance_(this).downloadApk();
        }
    }

    @Subscribe
    public void downloadApkEvent(DownloadApkEvent event) {
        try {
            Intent installIntent = new Intent(Intent.ACTION_VIEW);
            installIntent.setDataAndType(Uri.fromFile(new File(event.getPath())),
                    "application/vnd.android.package-archive");
            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(installIntent);
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }
    }
}
