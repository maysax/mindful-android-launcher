package co.siempo.phone.settings;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;

import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3App;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.util.PackageUtil;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.CoreApplication;
import minium.co.core.event.AppInstalledEvent;
import minium.co.core.ui.CoreActivity;

/**
 * Created by hardik on 17/8/17.
 */


@EActivity(R.layout.activity_siempo_alpha_settings)
public class SiempoAlphaSettingsActivity extends CoreActivity {

    private long startTime = 0;
    @ViewById
    ImageView icon_UserId;

    @ViewById
    TextView txt_UserId;

    @SystemService
    TelephonyManager telephonyManager;

    @Subscribe
    public void appInstalledEvent(AppInstalledEvent event) {
        if (event.isRunning()) {
            ((Launcher3App) CoreApplication.getInstance()).setAllDefaultMenusApplication();
        }
    }


    @AfterViews
    void afterViews() {
        initView();
    }


    public void initView() {
        Context context = SiempoAlphaSettingsActivity.this;
        icon_UserId.setImageDrawable(new IconDrawable(context, "fa-user-secret")
                .colorRes(R.color.text_primary)
                .sizeDp(18));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                txt_UserId.setText("UserId: " + telephonyManager.getDeviceId());
            }
        }

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
