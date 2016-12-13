package com.itconquest.tracking;

import android.Manifest;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.itconquest.tracking.listener.NotificationListener_;
import com.itconquest.tracking.services.GlobalTouchService_;
import com.itconquest.tracking.services.ScreenOnOffService_;
import com.itconquest.tracking.util.TrackingLogger;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import minium.co.core.app.DroidPrefs_;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.ServiceUtils;
import minium.co.core.util.UIUtils;

@EActivity(R.layout.activity_main)
public class MainActivity extends CoreActivity {

    public final static int REQUEST_CODE = 20;

    @ViewById
    Toolbar toolbar;

    @Pref
    DroidPrefs_ prefs;

    @ViewById
    FloatingActionButton fab;

    @SystemService
    UsageStatsManager usageStatsManager;

    @AfterViews
    void afterViews() {
        setSupportActionBar(toolbar);
        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission, app can not provide you the seamless integration.\n\nPlease consider turn on permissions at Setting > Permission")
                .setPermissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
                .check();
    }

    private void startServices() {
        if (prefs.isTrackingRunning().get()) {
            GlobalTouchService_.intent(getApplication()).start();
            ScreenOnOffService_.intent(getApplication()).start();
            getAppUsage();
        } else {
            GlobalTouchService_.intent(getApplication()).stop();
            ScreenOnOffService_.intent(getApplication()).stop();
        }
    }

    private void getAppUsage() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        List<UsageStats> queryUsageStats = usageStatsManager
                .queryUsageStats(UsageStatsManager.INTERVAL_DAILY, cal.getTimeInMillis(),
                        System.currentTimeMillis());

        for (UsageStats stat : queryUsageStats) {
            TrackingLogger.log("Package name: " + stat.getPackageName() + " Total time: " + stat.getTotalTimeInForeground(), null);
            Tracer.d("Package name: " + stat.getPackageName() + " Total time: " + stat.getTotalTimeInForeground());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isEnabled(this)) {
            UIUtils.confirm(this, "Tracker service is not enabled. Please allow Tracking to access notification service", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                }
            });
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
                if (prefs.isTrackingRunning().get()) {
                    Snackbar.make(view, "Tracking paused", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    fab.setImageResource(android.R.drawable.ic_media_play);

                } else {
                    Snackbar.make(view, "Tracking started", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    fab.setImageResource(android.R.drawable.ic_media_pause);
                }

                prefs.isTrackingRunning().put(!prefs.isTrackingRunning().get());
                startServices();

            }
        });

        if (prefs.isTrackingRunning().get()) {
            fab.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            fab.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

/*    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                // continue here - permission was granted
                loadViews();
            }
        }
    }*/

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            loadViews();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            UIUtils.toast(MainActivity.this, "Permission denied");
        }
    };
}
