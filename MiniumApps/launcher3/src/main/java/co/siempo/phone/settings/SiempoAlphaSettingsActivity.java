package co.siempo.phone.settings;

import android.content.Context;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;

import co.siempo.phone.R;
import co.siempo.phone.notification.NotificationRetreat_;
import co.siempo.phone.notification.StatusBarHandler;
import co.siempo.phone.ui.TopFragment_;
import minium.co.core.ui.CoreActivity;

/**
 * Created by hardik on 17/8/17.
 */


@Fullscreen
@EActivity(R.layout.activity_siempo_alpha_settings)
public class SiempoAlphaSettingsActivity extends CoreActivity {

    private Context context;
    private StatusBarHandler statusBarHandler;

    @AfterViews
    void afterViews() {
        initView();
        loadTopBar();
    }

    public void initView() {
        context = SiempoAlphaSettingsActivity.this;
    }

    private void loadTopBar() {
        loadFragment(TopFragment_.builder().build(), R.id.statusView, "status");
    }

    @Override
    protected void onResume() {
        super.onResume();
        statusBarHandler = new StatusBarHandler(SiempoAlphaSettingsActivity.this);
        statusBarHandler.requestStatusBarCustomization();
    }

    @Override
    protected void onPause() {
        super.onPause();
        NotificationRetreat_.getInstance_(this.getApplicationContext()).retreat();
        try {
            if (statusBarHandler != null)
                statusBarHandler.restoreStatusBarExpansion();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
