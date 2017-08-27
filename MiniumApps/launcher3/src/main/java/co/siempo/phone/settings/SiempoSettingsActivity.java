package co.siempo.phone.settings;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;

import co.siempo.phone.BuildConfig;
import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3Prefs_;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.notification.NotificationRetreat_;
import co.siempo.phone.notification.StatusBarHandler;
import co.siempo.phone.pause.PauseActivity;
import co.siempo.phone.service.ApiClient_;
import co.siempo.phone.ui.TopFragment_;
import minium.co.core.ui.CoreActivity;

/**
 * Created by hardik on 17/8/17.
 */

@SuppressWarnings("ALL")
@Fullscreen
@EActivity(R.layout.activity_siempo_settings)
public class SiempoSettingsActivity extends CoreActivity {
    private StatusBarHandler statusBarHandler;
    private Context context;
    private ImageView icon_launcher, icon_version;
    private TextView txt_version;
    private LinearLayout ln_launcher,ln_version;
    private CheckBox chk_keyboard;

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
                ApiClient_.getInstance_(context).checkAppVersion();
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
}
