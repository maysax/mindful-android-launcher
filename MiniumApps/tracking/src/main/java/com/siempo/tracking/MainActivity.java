package com.siempo.tracking;

import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import minium.co.core.event.CheckVersionEvent;

import com.siempo.tracking.permission.PermissionActivity_;
import com.siempo.tracking.services.ApiClient_;
import com.siempo.tracking.services.TrackingService_;
import com.siempo.tracking.util.FileUtil;
import com.siempo.tracking.util.PermissionUtil;
import com.siempo.tracking.util.TrackingPref_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.Locale;

import de.greenrobot.event.Subscribe;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
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
        TrackingService_.getInstance_(getApplication()).startServices();
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
