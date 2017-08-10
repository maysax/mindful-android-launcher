package co.siempo.phone.tempo;


import android.app.Fragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;

import co.siempo.phone.R;
import co.siempo.phone.notification.NotificationFragment;
import co.siempo.phone.notification.NotificationRetreat_;
import co.siempo.phone.notification.StatusBarHandler;
import co.siempo.phone.ui.TopFragment_;
import minium.co.core.ui.CoreActivity;

@Fullscreen
@EActivity(R.layout.activity_tempo)
public class TempoActivity extends CoreActivity {
    private StatusBarHandler statusBarHandler;

    @AfterViews
    void afterViews() {
        loadFragment(TempoFragment_.builder().build(), R.id.tempoView, "main");
        statusBarHandler = new StatusBarHandler(TempoActivity.this);
        loadTopBar();
    }

    private void loadTopBar() {
        loadFragment(TopFragment_.builder().build(), R.id.statusView, "status");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (statusBarHandler != null && !statusBarHandler.isActive())
            statusBarHandler.requestStatusBarCustomization();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        if (statusBarHandler.isNotificationTrayVisible) {
            Fragment f = getFragmentManager().findFragmentById(R.id.mainView);
            if (f instanceof NotificationFragment) ;
            {
                statusBarHandler.isNotificationTrayVisible = false;
                ((NotificationFragment) f).animateOut();
            }
        }
    }
}
