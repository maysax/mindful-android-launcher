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
import com.itconquest.tracking.permission.PermissionActivity_;
import com.itconquest.tracking.services.ApiClient_;
import com.itconquest.tracking.services.GlobalTouchService_;
import com.itconquest.tracking.services.HomePressService_;
import com.itconquest.tracking.services.ScreenOnOffService_;
import com.itconquest.tracking.util.FileUtil;
import com.itconquest.tracking.util.PermissionUtil;
import com.itconquest.tracking.util.TrackingLogger;
import com.itconquest.tracking.util.TrackingPref_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
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
import minium.co.core.service.CoreAPIClient;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.DateUtils;
import minium.co.core.util.ServiceUtils;
import minium.co.core.util.UIUtils;

@OptionsMenu(R.menu.menu_tracking)
@EActivity(resName = "activity_main_tracking")
public class MainActivity extends CoreActivity {


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

    @AfterViews
    void afterViews() {
        setSupportActionBar(toolbar);
        txtVersion.setText(String.format(Locale.getDefault(), "Version: %s", BuildConfig.VERSION_NAME));
        new FileUtil().deleteOldApk();
        loadViews();
        if (!new PermissionUtil(this).isAllPermissionGiven()) {
            UIUtils.confirm(this, "The Siempo Tracking app needs permissions to read data from your device to function properly. You can grant the permission(s) in the following prompt(s).", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE)
                        PermissionActivity_.intent(MainActivity.this).start();
                }
            });

        }
        checkVersion();
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
        cal.add(Calendar.DATE, -1);
        List<UsageStats> queryUsageStats = usageStatsManager
                .queryUsageStats(UsageStatsManager.INTERVAL_DAILY, cal.getTimeInMillis(),
                        System.currentTimeMillis());

        for (UsageStats stat : queryUsageStats) {

            if (stat.getTotalTimeInForeground() != 0) {

                String info = "Usage event\t" + stat.getPackageName()
                        + "\tDuration: " + DateUtils.interval(stat.getTotalTimeInForeground())
                        + "\tFrom: " + SimpleDateFormat.getDateTimeInstance().format(new Date(stat.getFirstTimeStamp()))
                        + "\tTo: " + SimpleDateFormat.getDateTimeInstance().format(new Date(stat.getLastTimeStamp()))
                        + "\tLast: " + SimpleDateFormat.getDateTimeInstance().format(new Date(stat.getLastTimeUsed()));


                TrackingLogger.log(info, null);
                Tracer.d(info);
            }
        }
    }


    void loadViews() {
        if (trackingPrefs.isTrackingRunning().get()) {
            fab.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            fab.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    @Click
    void fab() {
        if (!new PermissionUtil(this).isAllPermissionGiven()) {
            UIUtils.confirm(MainActivity.this, "You need to allow the app to access everything it asks for.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) PermissionActivity_.intent(MainActivity.this).start();
                }
            });

            return;
        }

        if (trackingPrefs.isTrackingRunning().get()) {
            Snackbar.make(fab, "All tracking has stopped.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            fab.setImageResource(android.R.drawable.ic_media_play);

        } else {
            Snackbar.make(fab, "Tracking started", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            fab.setImageResource(android.R.drawable.ic_media_pause);
        }

        trackingPrefs.isTrackingRunning().put(!trackingPrefs.isTrackingRunning().get());
        startServices();
    }

    @UiThread
    void startUpload() {
        UIUtils.toast(this, "Uploading file...");
        uploadFileToFTP();
        uploadFileToAWS();
    }



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
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
                UIUtils.toast(this, "New version found! Downloading apk...");
                ApiClient_.getInstance_(this).downloadApk();
            } else {
                UIUtils.toast(this, "New version found! Skipping for now because of metered connection");
            }
        }
    }

    @OptionsItem
    void actionPermission() {
        PermissionActivity_.intent(this).start();
    }

}
