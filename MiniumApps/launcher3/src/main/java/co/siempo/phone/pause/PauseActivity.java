package co.siempo.phone.pause;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.KeyDown;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.IOException;

import co.siempo.phone.MainActivity;
import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3Prefs_;
import co.siempo.phone.event.PauseStartEvent;
import co.siempo.phone.notification.NotificationFragment;
import co.siempo.phone.notification.NotificationRetreat_;
import co.siempo.phone.notification.StatusBarHandler;
import co.siempo.phone.ui.TopFragment_;
import de.greenrobot.event.Subscribe;
import minium.co.core.event.HomePressEvent;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.UIUtils;

@Fullscreen
@EActivity(R.layout.activity_pause)
public class PauseActivity extends CoreActivity {

    private PauseFragment pauseFragment;
    private PauseActivatedFragment pauseActivatedFragment;
    private StatusBarHandler statusBarHandler;
    private String TAG="PauseActivity";

    @Pref
    public Launcher3Prefs_ launcherPrefs;

    @Extra
    Tag tag;

    private Handler nfcCheckHandler;
    private Runnable nfcRunnable;

    private ActivityState state;
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

        Tracer.d("afterviews PauseActivity");
        init();
        loadTopBar();
        loadStatusBar();
    }

    /**
     *  Below snippet is use to first check if siempo status bar is restricted from another activity,
     *  then it first remove siempo status bar and restrict siempo status bar with reference to this activity
     */
    synchronized void loadStatusBar() {
        try {
            statusBarHandler = new StatusBarHandler(PauseActivity.this);
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
    private void init() {
        if (tag != null) {
            pauseStartEvent(new PauseStartEvent(-1));
        } else {
            pauseFragment = PauseFragment_.builder().build();
            loadFragment(pauseFragment, R.id.pauseView, "main");
        }
    }

    @KeyDown(KeyEvent.KEYCODE_VOLUME_UP)
    void volumeUpPressed() {
        pauseFragment.volumeUpPressed();
    }

    @Override
    public void onBackPressed() {
        if (launcherPrefs.isPauseActive().get()) {
            onStopPause();
        } else {
            if (statusBarHandler!=null && statusBarHandler.isNotificationTrayVisible) {
                /**
                 *  Below snippet is use to remove notification fragment (Siempo Notification Screen) if visible on screen
                 */
                Fragment f = getFragmentManager().findFragmentById(R.id.mainView);
                if(f == null){
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        Tracer.d("onStart PauseActivity");
        if(state== ActivityState.ONHOMEPRESS){
            state= ActivityState.NORMAL;
        }
        if (tag != null) {
            if (nfcCheckHandler == null ) nfcCheckHandler = new Handler();
            nfcCheckHandler.postDelayed(buildNfcRunnable(tag), 5000);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Tracer.d("onStop PauseActivity");

        if (nfcCheckHandler != null) {
            nfcCheckHandler.removeCallbacks(nfcRunnable);
            Ndef  ndef = Ndef.get(tag);
            if (ndef != null) {
                try {
                    ndef.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Click
    void pauseContainer() {
        if (launcherPrefs.isPauseActive().get()) {
            onStopPause();
        }
    }

    private void onStopPause() {
        UIUtils.ask(this, "Do you want to go back online?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stopPause();
            }
        });
    }

    private void stopPause() {

        if (pauseActivatedFragment != null) {
            pauseActivatedFragment.stopPause(true);
        }
    }

    @Subscribe
    public void pauseStartEvent(PauseStartEvent event) {
        pauseActivatedFragment = PauseActivatedFragment_.builder().maxMillis(event.getMaxMillis()).build();
        loadFragment(pauseActivatedFragment, R.id.pauseView, "main");
    }

    private Runnable buildNfcRunnable(final Tag tag) {
        if (nfcRunnable != null) return nfcRunnable;
        return nfcRunnable = new Runnable() {
            @Override
            public void run() {
                Ndef  ndef = Ndef.get(tag);
                Tracer.d("Ndef: " + ndef);
                try {
                    ndef.connect();
                    Tracer.d("Connection heart-beat for nfc tag " + tag);
                    nfcCheckHandler.postDelayed(this, 1000);
                } catch (Exception e) {
                    // if the tag is gone we might want to end the thread:
                    stopPause();
                    Tracer.e(e, e.getMessage());
                    Tracer.d("Disconnected from nfc tag" + tag);
                    nfcCheckHandler.removeCallbacks(this);
                } finally {
                    try {
                        ndef.close();
                    } catch (IOException e) {
                        Tracer.e(e, e.getMessage());
                    }
                }
            }
        };
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

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadStatusBar();
    }


    @Override
    protected void onResume() {
        super.onResume();
        /**
         * Below snippet is use to load siempo status bar when launch from background.
         */
        if(state==ActivityState.ONHOMEPRESS){
            if(statusBarHandler!=null && !statusBarHandler.isActive()) {
                statusBarHandler.requestStatusBarCustomization();
            }
        }
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
