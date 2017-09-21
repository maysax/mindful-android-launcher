package co.siempo.phone.tempo;


import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.UiThread;

import co.siempo.phone.BuildConfig;
import co.siempo.phone.MainActivity;
import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3App;
import co.siempo.phone.notification.NotificationFragment;
import co.siempo.phone.notification.NotificationRetreat_;
import co.siempo.phone.notification.StatusBarHandler;
import co.siempo.phone.pause.PauseActivity;
import co.siempo.phone.ui.TopFragment_;
import de.greenrobot.event.Subscribe;
import minium.co.core.event.HomePressEvent;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.UIUtils;

@Fullscreen
@EActivity(R.layout.activity_tempo)
public class TempoActivity extends CoreActivity {
    private StatusBarHandler statusBarHandler;
    private String TAG = "TempoActivity";

    private ActivityState state;
    /**
     * Activitystate is use to identify state whether the screen is coming from
     * after homepress event or from normal flow.
     */
    private enum ActivityState {
        NORMAL,
        ONHOMEPRESS
    }

    @Override
    protected void onStart() {
        super.onStart();
        Launcher3App.getInstance().setSiempoBarLaunch(true);
        if(state== ActivityState.ONHOMEPRESS){
            state= ActivityState.NORMAL;
        }
    }

    @AfterViews
    void afterViews() {
        Launcher3App.getInstance().setSiempoBarLaunch(true);
        // To check the notification service is enable or not.
        if (!MainActivity.isEnabled(this)) {
            UIUtils.confirmWithCancel(this, null, getString(R.string.msg_noti_service_dialog), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"), 100);
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        }


        loadFragment(TempoFragment_.builder().build(), R.id.tempoView, "main");
        statusBarHandler = new StatusBarHandler(TempoActivity.this);
        loadTopBar();
        loadStatusBar();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && !MainActivity.isEnabled(this)) {
            UIUtils.confirmWithCancel(this, null, getString(R.string.msg_noti_service_dialog), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"), 100);
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        }
    }
    /**
     *  Below snippet is use to first check if siempo status bar is restricted from another activity,
     *  then it first remove siempo status bar and restrict siempo status bar with reference to this activity
     */
    void loadStatusBar() {
        try {
            statusBarHandler = new StatusBarHandler(TempoActivity.this);
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

    private void loadTopBar() {
        loadFragment(TopFragment_.builder().build(), R.id.statusView, "status");
    }

    @Override
    protected void onResume() {
        super.onResume();

            if(state== ActivityState.ONHOMEPRESS){
                if(statusBarHandler!=null && !statusBarHandler.isActive()) {
                    statusBarHandler.requestStatusBarCustomization();
                }
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
    public void onBackPressed() {
        Launcher3App.getInstance().setSiempoBarLaunch(false);
        if (statusBarHandler!=null && statusBarHandler.isNotificationTrayVisible) {
            Fragment f = getFragmentManager().findFragmentById(R.id.mainView);
            if(f == null){
                Log.d(TAG," Fragment is null");
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

    @Override
    protected void onRestart() {
        super.onRestart();
        Launcher3App.getInstance().setSiempoBarLaunch(true);
        loadStatusBar();
    }



    @SuppressWarnings("ConstantConditions")
    @Subscribe
    public void homePressEvent(HomePressEvent event) {
        Log.d(TAG,"ACTION HOME PRESS");
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
