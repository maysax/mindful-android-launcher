package co.siempo.phone.settings;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.joanzapata.iconify.IconDrawable;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;

import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3App;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.notification.NotificationFragment;
import co.siempo.phone.notification.NotificationRetreat_;
import co.siempo.phone.ui.TopFragment_;
import co.siempo.phone.util.PackageUtil;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.CoreApplication;
import minium.co.core.event.AppInstalledEvent;
import minium.co.core.event.HomePressEvent;
import minium.co.core.ui.CoreActivity;

/**
 * Created by hardik on 17/8/17.
 */


@EActivity(R.layout.activity_siempo_alpha_settings)
public class SiempoAlphaSettingsActivity extends CoreActivity {

    private Context context;
    private long startTime=0;
    private LinearLayout ln_notifications,ln_suppressedNotifications;
    private ImageView icon_AppNotifications,icon_SuppressedNotifications;

    @Subscribe
    public void appInstalledEvent(AppInstalledEvent event) {
        if (event.isRunning()) {
            ((Launcher3App) CoreApplication.getInstance()).setAllDefaultMenusApplication();
        }
    }

    private final String TAG = "SiempoAlphaSetting";


    @AfterViews
    void afterViews() {
        initView();
        onClickEvents();
    }


    public void initView() {
        context = SiempoAlphaSettingsActivity.this;
        ln_notifications = findViewById(R.id.ln_notifications);
        ln_suppressedNotifications = findViewById(R.id.ln_suppressedNotifications);
        icon_SuppressedNotifications = findViewById(R.id.icon_SuppressedNotifications);
        icon_AppNotifications = findViewById(R.id.icon_AppNotifications);
        icon_AppNotifications.setImageDrawable(new IconDrawable(context,"fa-bell").colorRes(R.color.text_primary).sizeDp(18));
        try {
            icon_SuppressedNotifications.setImageDrawable(new IconDrawable(context, "fa-exclamation").colorRes(R.color.text_primary).sizeDp(18));
        }catch (Exception e){
        }
    }

    public void onClickEvents(){
        ln_notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ActivityHelper(context).openAppListNotifications();
            }
        });

        ln_suppressedNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new ActivityHelper(context).openSiempoSuppressNotificationsSettings();
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        FirebaseHelper.getIntance().logScreenUsageTime(SiempoAlphaSettingsActivity.this.getClass().getSimpleName(),startTime);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
        PackageUtil.checkPermission(this);
    }




}
