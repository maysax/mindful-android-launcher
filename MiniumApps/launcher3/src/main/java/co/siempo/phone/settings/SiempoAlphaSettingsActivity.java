package co.siempo.phone.settings;

import android.app.Fragment;
import android.content.Context;
import android.util.Log;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.UiThread;

import co.siempo.phone.R;
import co.siempo.phone.notification.NotificationFragment;
import co.siempo.phone.notification.NotificationRetreat_;
import co.siempo.phone.notification.StatusBarHandler;
import co.siempo.phone.ui.TopFragment_;
import de.greenrobot.event.Subscribe;
import minium.co.core.event.HomePressEvent;
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

    private ActivityState state;

    private final String TAG="SiempoAlphaSetting";
    /**
     * Activitystate is use to identify state whether the screen is coming from
     * after homepress event or from normal flow.
     */
    private enum ActivityState {
        NORMAL,
        ONHOMEPRESS
    }

    @AfterViews
    void afterViews() {
        initView();
        loadTopBar();
        loadStatusBar();
    }

    /**
     *  Below snippet is use to first check if siempo status bar is restricted from another activity,
     *  then it first remove siempo status bar and restrict siempo status bar with reference to this activity
     */
    synchronized void loadStatusBar() {
        try {
            statusBarHandler = new StatusBarHandler(SiempoAlphaSettingsActivity.this);
            NotificationRetreat_.getInstance_(this.getApplicationContext()).retreat();
            if (statusBarHandler != null) {
                statusBarHandler.restoreStatusBarExpansion();
            }

            if(statusBarHandler!=null && !statusBarHandler.isActive()) {
                statusBarHandler.requestStatusBarCustomization();
            }

        } catch (Exception e) {
            e.printStackTrace();
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
        /**
         * Below snippet is use to load siempo status bar when launch from background.
         */
        if(state== ActivityState.ONHOMEPRESS){
            if(statusBarHandler!=null && !statusBarHandler.isActive()) {
                statusBarHandler.requestStatusBarCustomization();
            }
        }
        // If status bar view becomes null,reload the statusbar
        if (getSupportFragmentManager().findFragmentById(R.id.statusView) == null) {
            loadTopBar();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadStatusBar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(state== ActivityState.ONHOMEPRESS){
            state= ActivityState.NORMAL;
        }
    }

    @Override
    public void onBackPressed() {
        if (statusBarHandler!=null && statusBarHandler.isNotificationTrayVisible) {
            /**
             *  Below snippet is use to remove notification fragment (Siempo Notification Screen) if visible on screen
             */
            Fragment f = getFragmentManager().findFragmentById(R.id.mainView);
            if(f == null){
                Log.d(TAG,"Fragment is null");
                super.onBackPressed();
            }
            else if (f!=null && f instanceof NotificationFragment && f.isAdded())
            {
                statusBarHandler.isNotificationTrayVisible = false;
                ((NotificationFragment) f).animateOut();
                super.onBackPressed();
            }
            else{
                super.onBackPressed();
            }
        }
        else{
            super.onBackPressed();
        }
    }



    @SuppressWarnings("ConstantConditions")
    @Subscribe
    public void homePressEvent(HomePressEvent event) {
        state= ActivityState.ONHOMEPRESS;
        if (event.isVisible()) {
            /**
             *  Below snippet is use to remove notification fragment (Siempo Notification Screen) if visible on screen
             */
            if (statusBarHandler!=null && statusBarHandler.isNotificationTrayVisible) {

                Fragment f = getFragmentManager().findFragmentById(R.id.mainView);
                if(f == null){
                    Log.d(TAG,"Fragment is null");
                }
                else if (f!=null && f.isAdded() && f instanceof NotificationFragment)
                {
                    StatusBarHandler.isNotificationTrayVisible = false;
                    ((NotificationFragment) f).animateOut();

                }
            }
            /**
             *  Below snippet is use to remove siempo status bar
             */
            if(statusBarHandler!=null){
                NotificationRetreat_.getInstance_(this.getApplicationContext()).retreat();
                try{
                    statusBarHandler.restoreStatusBarExpansion();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
