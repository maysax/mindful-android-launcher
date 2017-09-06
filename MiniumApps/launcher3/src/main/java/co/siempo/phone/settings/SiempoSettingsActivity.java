package co.siempo.phone.settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;
import com.joanzapata.iconify.IconDrawable;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;
import co.siempo.phone.BuildConfig;
import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3Prefs_;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.notification.NotificationRetreat_;
import co.siempo.phone.notification.StatusBarHandler;
import co.siempo.phone.service.ApiClient_;
import co.siempo.phone.ui.TopFragment_;
import de.greenrobot.event.Subscribe;
import minium.co.core.event.CheckVersionEvent;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.UIUtils;
import com.github.javiersantos.appupdater.enums.Display;


/**
 * Created by hardik on 17/8/17.
 */

/**
 * This class contain all the siempo settings feature.
 *  1. Switc home app
 *  2. Keyboard hide & show in IF Screen when launch
 *  3. Version of Current App & update
 */
@Fullscreen
@EActivity(R.layout.activity_siempo_settings)
public class SiempoSettingsActivity extends CoreActivity {
    private StatusBarHandler statusBarHandler;
    private Context context;
    private ImageView icon_launcher, icon_version;
    private TextView txt_version;
    private LinearLayout ln_launcher,ln_version;
    private CheckBox chk_keyboard;
    private String TAG = "SiempoSettingsActivity";
    private ProgressDialog pd;

    @SystemService
    ConnectivityManager connectivityManager;

    @Pref
    Launcher3Prefs_ launcherPrefs;


    @AfterViews
    void afterViews() {
        initView();
        onClickEvents();
        loadTopBar();
        loadStatusBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentIndex =0;
    }

    @UiThread(delay = 1000)
    void loadStatusBar() {
        statusBarHandler = new StatusBarHandler(SiempoSettingsActivity.this);
        if(statusBarHandler!=null && !statusBarHandler.isActive()) {
            statusBarHandler.requestStatusBarCustomization();
        }
    }

    public void initView() {
        context = SiempoSettingsActivity.this;
        icon_launcher = (ImageView) findViewById(R.id.icon_launcher);
        icon_version = (ImageView) findViewById(R.id.icon_version);
        txt_version = (TextView) findViewById(R.id.txt_version);
        txt_version.setText("Version : " + BuildConfig.VERSION_NAME);
        chk_keyboard = (CheckBox) findViewById(R.id.chk_keyboard);
        boolean isKeyboardDisplay=launcherPrefs.isKeyBoardDisplay().get();
        chk_keyboard.setChecked(isKeyboardDisplay);
        ln_launcher = (LinearLayout) findViewById(R.id.ln_launcher);
        ln_version = (LinearLayout)findViewById(R.id.ln_version);
        icon_launcher.setImageDrawable(new IconDrawable(context, "fa-certificate")
                .colorRes(R.color.text_primary)
                .sizeDp(18));
        icon_version.setImageDrawable(new IconDrawable(context, "fa-info-circle")
                .colorRes(R.color.text_primary)
                .sizeDp(18));

    }

    private void onClickEvents() {
        ln_launcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ActivityHelper(context).handleDefaultLauncher((CoreActivity) context);
                ((CoreActivity) context).loadDialog();
            }
        });

        ln_version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                if (activeNetwork != null) {
                    AppUpdaterUtils appUpdaterUtils = new AppUpdaterUtils(SiempoSettingsActivity.this)
                            .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                            .withListener(new AppUpdaterUtils.UpdateListener() {
                                @Override
                                public void onSuccess(Update update, Boolean isUpdateAvailable) {

                                    if (update.getLatestVersionCode() != null) {
                                        Log.d(TAG,"check version from AppUpdater library");
                                        checkVersionFromAppUpdater();
                                    } else {
                                        Log.d(TAG,"check version from AWS");
                                        initProgressDialog();
                                        ApiClient_.getInstance_(SiempoSettingsActivity.this).checkAppVersion();
                                    }
                                }

                                @Override
                                public void onFailed(AppUpdaterError error) {
                                    Log.d(TAG," AppUpdater Error ::: "+error.toString());

                                }
                            });

                    appUpdaterUtils.start();
                } else {
                    Log.d(TAG, getString(R.string.nointernetconnection));
                }
            }
        });

        chk_keyboard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                launcherPrefs.isKeyBoardDisplay().put(isChecked);
            }
        });
    }

    private void loadTopBar() {
        loadFragment(TopFragment_.builder().build(), R.id.statusView, "status");
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentIndex =1;
    }

    @Override
    protected void onStop() {

        super.onStop();
        currentIndex =0;
        NotificationRetreat_.getInstance_(this.getApplicationContext()).retreat();
        try {
            if (statusBarHandler != null)
                statusBarHandler.restoreStatusBarExpansion();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadStatusBar();
    }

    /**
     * This function is use to check current app version with play store version
     * and display alert if update is available using Appupdater library.
     */
    public void checkVersionFromAppUpdater(){
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
        if(pd!=null){
            pd.dismiss();
        }
        Tracer.d("Installed version: " + BuildConfig.VERSION_CODE + " Found: " + event.getVersion());
        if (event.getVersion() > BuildConfig.VERSION_CODE) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            if (activeNetwork != null) { // connected to the internet
                UIUtils.confirm(this, "New version found! Would you like to update Siempo?", new DialogInterface.OnClickListener() {
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
        }else{
            Toast.makeText(getApplicationContext(),"Your application is up to date",Toast.LENGTH_LONG).show();
        }
    }

    public void initProgressDialog(){
        pd = new ProgressDialog(SiempoSettingsActivity.this,R.style.ProgressTheme);
        pd.setCancelable(false);
        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pd.show();
    }

}
