package co.siempo.phone.tempo;


import android.app.Fragment;
import android.util.Log;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.UiThread;

import co.siempo.phone.R;
import co.siempo.phone.notification.NotificationFragment;
import co.siempo.phone.notification.NotificationRetreat_;
import co.siempo.phone.notification.StatusBarHandler;
import co.siempo.phone.settings.SiempoSettingsActivity;
import co.siempo.phone.ui.TopFragment_;
import minium.co.core.ui.CoreActivity;

@Fullscreen
@EActivity(R.layout.activity_tempo)
public class TempoActivity extends CoreActivity {
    private StatusBarHandler statusBarHandler;
    private String TAG="TempoActivity";

    @AfterViews
    void afterViews() {
        loadFragment(TempoFragment_.builder().build(), R.id.tempoView, "main");
        statusBarHandler = new StatusBarHandler(TempoActivity.this);
        loadTopBar();
        loadStatusBar();
    }

    @UiThread(delay = 1000)
    void loadStatusBar() {
        statusBarHandler = new StatusBarHandler(TempoActivity.this);
        if(statusBarHandler!=null && !statusBarHandler.isActive()) {
            statusBarHandler.requestStatusBarCustomization();
        }
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
            if(statusBarHandler!=null)
                statusBarHandler.restoreStatusBarExpansion();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try{
            if (statusBarHandler.isNotificationTrayVisible) {
                Fragment f = getFragmentManager().findFragmentById(R.id.mainView);
                if (f instanceof NotificationFragment) ;
                {
                    statusBarHandler.isNotificationTrayVisible = false;
                    ((NotificationFragment) f).animateOut();
                }
            }
        }
        catch (Exception e){
            Log.d(TAG,"Exception onBackPressed.."+e.toString());
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadStatusBar();
    }
}
