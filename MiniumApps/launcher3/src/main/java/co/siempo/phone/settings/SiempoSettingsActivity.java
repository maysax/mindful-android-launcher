package co.siempo.phone.settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;
import com.joanzapata.iconify.IconDrawable;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import co.siempo.phone.BuildConfig;
import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3App;
import co.siempo.phone.app.Launcher3Prefs_;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.main.MainListItemLoader;
import co.siempo.phone.service.ApiClient_;
import co.siempo.phone.util.PackageUtil;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.CoreApplication;
import minium.co.core.event.AppInstalledEvent;
import minium.co.core.event.CheckVersionEvent;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.UIUtils;


/**
 * This class contain all the siempo settings feature.
 * 1. Switc home app
 * 2. Keyboard hide & show in IF Screen when launch
 * 3. Version of Current App & update
 */
@EActivity(R.layout.activity_siempo_settings)
public class SiempoSettingsActivity extends CoreActivity {
    private final String TAG = "SiempoSettingsActivity";
    @SystemService
    ConnectivityManager connectivityManager;
    @Pref
    Launcher3Prefs_ launcherPrefs;
    private Context context;
    private ImageView icon_launcher, icon_KeyBoardNotification, icon_Faq, icon_Feedback, icon_version, icon_changeDefaultApp, icon_AppNotifications, icon_SuppressedNotifications;
    private TextView txt_version;
    private LinearLayout ln_launcher, ln_version, ln_Feedback, ln_Faq, ln_changeDefaultApp, ln_AppListNotifications;
    private ProgressDialog pd;
    private AppUpdaterUtils appUpdaterUtils;
    private ImageView icon_hideNotification;
    private SwitchCompat switch_notification;
    private SwitchCompat switch_KeyBoardnotification;
    private long startTime = 0;

    @Subscribe
    public void appInstalledEvent(AppInstalledEvent event) {
        if (event.isRunning()) {

            ((Launcher3App) CoreApplication.getInstance()).setAllDefaultMenusApplication();
        }
    }

    @AfterViews
    void afterViews() {
        initView();
        onClickEvents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentIndex = 0;
    }


    private void initView() {
        context = SiempoSettingsActivity.this;
        icon_launcher = findViewById(R.id.icon_launcher);
        icon_version = findViewById(R.id.icon_version);
//        icon_SuppressedNotifications = findViewById(R.id.icon_SuppressedNotifications);
        icon_changeDefaultApp = findViewById(R.id.icon_changeDefaultApp);
        icon_KeyBoardNotification = findViewById(R.id.icon_KeyBoardNotification);
        icon_Feedback = findViewById(R.id.icon_Feedback);
        icon_Faq = findViewById(R.id.icon_Faq);
        icon_AppNotifications = findViewById(R.id.icon_AppNotifications);
        txt_version = findViewById(R.id.txt_version);
        if (BuildConfig.FLAVOR.equalsIgnoreCase(context.getString(R.string.alpha))) {
            txt_version.setText("Version : " + "ALPHA-" + BuildConfig.VERSION_NAME);
        } else if (BuildConfig.FLAVOR.equalsIgnoreCase(context.getString(R.string.beta))) {
            txt_version.setText("Version : " + "BETA-" + BuildConfig.VERSION_NAME);
        }

        boolean isKeyboardDisplay = launcherPrefs.isKeyBoardDisplay().get();
        ln_launcher = findViewById(R.id.ln_launcher);
        ln_version = findViewById(R.id.ln_version);
        ln_version = findViewById(R.id.ln_version);
//        ln_suppressedNotifications = findViewById(R.id.ln_suppressedNotifications);
        ln_Feedback = findViewById(R.id.ln_Feedback);
        ln_AppListNotifications = findViewById(R.id.ln_notifications);
        ln_Faq = findViewById(R.id.ln_Faq);
        icon_hideNotification = findViewById(R.id.icon_hideNotification);
        ln_changeDefaultApp = findViewById(R.id.ln_changeDefaultApp);
        switch_notification = findViewById(R.id.swtch_notification);
        switch_KeyBoardnotification = findViewById(R.id.switch_KeyBoardnotification);
        icon_launcher.setImageDrawable(new IconDrawable(context, "fa-certificate")
                .colorRes(R.color.text_primary)
                .sizeDp(18));
        // In case if suppressed notification bar is to be invoked from this
        //  screen
//        try {
//            icon_SuppressedNotifications.setImageDrawable(new IconDrawable(context, "fa-exclamation").colorRes(R.color.text_primary).sizeDp(18));
//        }catch (Exception e){
//        }
        icon_hideNotification.setImageDrawable(new IconDrawable(context, "fa-flag")
                .colorRes(R.color.text_primary)
                .sizeDp(18));
        icon_changeDefaultApp.setImageDrawable(new IconDrawable(context, "fa-link")
                .colorRes(R.color.text_primary)
                .sizeDp(18));
        icon_version.setImageDrawable(new IconDrawable(context, "fa-info-circle")
                .colorRes(R.color.text_primary)
                .sizeDp(18));
        icon_AppNotifications.setImageDrawable(new IconDrawable(context, "fa-bell").colorRes(R.color.text_primary).sizeDp(18));
        icon_KeyBoardNotification.setImageDrawable(new IconDrawable(context, "fa-keyboard-o")
                .colorRes(R.color.text_primary)
                .sizeDp(18));
        icon_Feedback.setImageDrawable(new IconDrawable(context, "fa-question-circle")
                .colorRes(R.color.text_primary)
                .sizeDp(18));
        icon_Faq.setImageDrawable(new IconDrawable(context, "fa-shield")
                .colorRes(R.color.text_primary)
                .sizeDp(18));

        if (launcherPrefs.isHidenotificationOnLockScreen().get()) {
            switch_notification.setChecked(true);
        } else {
            switch_notification.setChecked(false);
        }
        switch_KeyBoardnotification.setChecked(isKeyboardDisplay);

    }

    private void onClickEvents() {
        ln_launcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ActivityHelper(context).handleDefaultLauncher((CoreActivity) context);
                ((CoreActivity) context).loadDialog();
            }
        });

        ln_changeDefaultApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ActivityHelper(context).openSiempoDefaultAppSettings();
            }
        });
// In case if suppressed notification bar is to be invoked from this screen
// the following click event will be used

//        ln_suppressedNotifications.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                new ActivityHelper(context).openSiempoSuppressNotificationsSettings();
//            }
//        });

        ln_Feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MainListItemLoader(SiempoSettingsActivity.this).listItemClicked(18);
            }
        });

        ln_Faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String url = "http://www.getsiempo.com/apps/android/beta/faq.htm";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                } catch (Exception e) {
                    UIUtils.alert(context, context.getString(R.string.app_not_found));
                }
            }
        });
        ln_AppListNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SiempoSettingsActivity.this, AppListNotificationSetting.class);
                startActivity(i);
            }
        });

        ln_version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                if (activeNetwork != null && appUpdaterUtils == null) {
                    initProgressDialog();
                    appUpdaterUtils = new AppUpdaterUtils(SiempoSettingsActivity.this)
                            .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                            .withListener(new AppUpdaterUtils.UpdateListener() {
                                @Override
                                public void onSuccess(Update update, Boolean isUpdateAvailable) {
                                    if (update.getLatestVersionCode() != null) {
                                        Log.d(TAG, "check version from AppUpdater library");
                                        if (pd != null) {
                                            pd.dismiss();
                                        }
                                        checkVersionFromAppUpdater();
                                        appUpdaterUtils = null;
                                    } else {
                                        Log.d(TAG, "check version from AWS");
                                        if (BuildConfig.FLAVOR.equalsIgnoreCase(context.getString(R.string.alpha))) {
                                            ApiClient_.getInstance_(SiempoSettingsActivity.this).checkAppVersion(CheckVersionEvent.ALPHA);
                                        } else if (BuildConfig.FLAVOR.equalsIgnoreCase(context.getString(R.string.beta))) {
                                            ApiClient_.getInstance_(SiempoSettingsActivity.this).checkAppVersion(CheckVersionEvent.BETA);
                                        }

                                    }
                                }

                                @Override
                                public void onFailed(AppUpdaterError error) {
                                    if (BuildConfig.DEBUG)
                                        Log.d(TAG, " AppUpdater Error ::: " + error.toString());

                                }
                            });

                    appUpdaterUtils.start();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.nointernetconnection), Toast.LENGTH_LONG).show();
                    Log.d(TAG, getString(R.string.nointernetconnection));
                }
            }
        });

        switch_KeyBoardnotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                launcherPrefs.isKeyBoardDisplay().put(isChecked);
            }
        });

//        switch_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                launcherPrefs.isHidenotificationOnLockScreen().put(isChecked);
//            }
//        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
        PackageUtil.checkPermission(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        currentIndex = 0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseHelper.getIntance().logScreenUsageTime(SiempoSettingsActivity.this.getClass().getSimpleName(), startTime);
    }

    /**
     * This function is use to check current app version with play store version
     * and display alert if update is available using Appupdater library.
     */
    private void checkVersionFromAppUpdater() {
        new AppUpdater(this)
                .setDisplay(Display.DIALOG)
                .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                .showEvery(5)
                .setTitleOnUpdateAvailable("Update available")
                .setContentOnUpdateAvailable("New version found! Would you like to update Siempo?")
                .setTitleOnUpdateNotAvailable("Update not available")
                .setContentOnUpdateNotAvailable("Your application is up to date")
                .setButtonUpdate("Update")
                .setButtonDismiss("Maybe later")
                .start();
    }

    @Subscribe
    public void checkVersionEvent(CheckVersionEvent event) {
        Log.d(TAG, "Check Version event...");

        if (event.getVersionName().equalsIgnoreCase(CheckVersionEvent.ALPHA)) {
            if (event.getVersion() > UIUtils.getCurrentVersionCode(this)) {
                if (pd != null) {
                    pd.dismiss();
                    pd = null;
                }
                Tracer.d("Installed version: " + UIUtils.getCurrentVersionCode(this) + " Found: " + event.getVersion());
                appUpdaterUtils = null;
                showUpdateDialog(CheckVersionEvent.ALPHA);
            } else {
                ApiClient_.getInstance_(this).checkAppVersion(CheckVersionEvent.BETA);
            }
        } else {
            if (pd != null) {
                pd.dismiss();
                pd = null;
            }
            if (event.getVersion() > UIUtils.getCurrentVersionCode(this)) {
                Tracer.d("Installed version: " + UIUtils.getCurrentVersionCode(this) + " Found: " + event.getVersion());
                appUpdaterUtils = null;
                showUpdateDialog(CheckVersionEvent.BETA);
            } else {
                appUpdaterUtils = null;
                Toast.makeText(getApplicationContext(), "Your application is up to date", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showUpdateDialog(String str) {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            UIUtils.confirm(this, str.equalsIgnoreCase(CheckVersionEvent.ALPHA) ? "New alpha version found! Would you like to update Siempo?" : "New beta version found! Would you like to update Siempo?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        launcherPrefs.updatePrompt().put(false);
                        new ActivityHelper(SiempoSettingsActivity.this).openBecomeATester();
                    }
                }
            });
        } else {
            Log.d(TAG, getString(R.string.nointernetconnection));
        }
    }

    private void initProgressDialog() {
        try {
            //noinspection deprecation
            if (pd == null) {
                pd = new ProgressDialog(this);
                pd.setMessage("Please wait...");
                pd.setCancelable(false);
                pd.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
                pd.show();
            }
        } catch (Exception e) {
            CoreApplication.getInstance().logException(e);
            //WindowManager$BadTokenException will be caught here
            e.printStackTrace();
        }
    }


}
