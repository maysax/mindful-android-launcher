package co.siempo.phone.settings;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;

import co.siempo.phone.MainActivity;
import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3App;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.ui.SiempoPermissionActivity_;
import co.siempo.phone.util.PackageUtil;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.CoreApplication;
import minium.co.core.event.AppInstalledEvent;
import minium.co.core.ui.CoreActivity;

import static co.siempo.phone.MainActivity.IS_FROM_HOME;

/**
 * Created by hardik on 17/8/17.
 */


@EActivity(R.layout.activity_siempo_alpha_settings)
public class SiempoAlphaSettingsActivity extends CoreActivity {

    private Context context;
    private long startTime = 0;
    @ViewById
    ImageView icon_UserId;

    @ViewById
    TextView txt_UserId;

    @SystemService
    TelephonyManager telephonyManager;
    private LinearLayout ln_notifications,ln_suppressedNotifications;
    private ImageView icon_AppNotifications,icon_SuppressedNotifications;
    private LinearLayout ln_permissions;
    private ImageView icon_permissions;

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


    public void initView() {
        context = SiempoAlphaSettingsActivity.this;
        ln_notifications = findViewById(R.id.ln_notifications);
        ln_suppressedNotifications = findViewById(R.id.ln_suppressedNotifications);
        ln_permissions = findViewById(R.id.ln_permissions);
        icon_SuppressedNotifications = findViewById(R.id.icon_SuppressedNotifications);
        icon_AppNotifications = findViewById(R.id.icon_AppNotifications);
        icon_permissions = findViewById(R.id.icon_permissions);
        icon_AppNotifications.setImageDrawable(new IconDrawable(context,"fa-bell").colorRes(R.color.text_primary).sizeDp(18));
        icon_permissions.setImageDrawable(new IconDrawable(context,"fa-bell").colorRes(R.color.text_primary).sizeDp(18));
        try {
            icon_SuppressedNotifications.setImageDrawable(new IconDrawable(context, "fa-exclamation").colorRes(R.color.text_primary).sizeDp(18));
        }catch (Exception e){
            //Todo log exception to fabric
            e.printStackTrace();
//            Crashlytics.logException(e);
        }
        icon_UserId.setImageDrawable(new IconDrawable(context, "fa-user-secret")
                .colorRes(R.color.text_primary)
                .sizeDp(18));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                txt_UserId.setText(String.format("UserId: %s", telephonyManager.getDeviceId()));
            }
        }
        else{
            txt_UserId.setText(String.format("UserId: %s", telephonyManager.getDeviceId()));
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

        ln_permissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SiempoAlphaSettingsActivity.this, SiempoPermissionActivity_.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra(IS_FROM_HOME,false);
                startActivity(intent);
            }
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
        FirebaseHelper.getIntance().logScreenUsageTime(SiempoAlphaSettingsActivity.this.getClass().getSimpleName(), startTime);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
        PackageUtil.checkPermission(this);
    }


}
