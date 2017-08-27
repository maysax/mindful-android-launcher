package co.siempo.phone.settings;

import android.content.Context;
import android.util.Log;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.UiThread;

import co.siempo.phone.R;
import co.siempo.phone.notification.NotificationRetreat_;
import co.siempo.phone.notification.StatusBarHandler;
import co.siempo.phone.ui.TopFragment_;
import minium.co.core.ui.CoreActivity;

/**
 * Created by hardik on 17/8/17.
 */


@SuppressWarnings("ALL")
@Fullscreen
@EActivity(R.layout.activity_siempo_alpha_settings)
public class SiempoAlphaSettingsActivity extends CoreActivity {

    private Context context;
    private StatusBarHandler statusBarHandler;

    @AfterViews
    void afterViews() {
        initView();
        loadTopBar();
        loadStatusBar();
    }

    @UiThread(delay = 1000)
    void loadStatusBar() {
        statusBarHandler = new StatusBarHandler(SiempoAlphaSettingsActivity.this);
        if(statusBarHandler!=null && !statusBarHandler.isActive()) {
            statusBarHandler.requestStatusBarCustomization();
        }
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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        NotificationRetreat_.getInstance_(this.getApplicationContext()).retreat();
        try {
            if (statusBarHandler != null)
                statusBarHandler.restoreStatusBarExpansion();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadStatusBar();
    }
}
