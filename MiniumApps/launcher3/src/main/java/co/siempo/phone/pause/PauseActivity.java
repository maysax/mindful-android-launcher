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


    @UiThread(delay = 1000)
    void loadStatusBar() {
        statusBarHandler = new StatusBarHandler(PauseActivity.this);
        if (statusBarHandler != null && !statusBarHandler.isActive()) {
            statusBarHandler.requestStatusBarCustomization();
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
            super.onBackPressed();
        }

        try {
            if (statusBarHandler != null && statusBarHandler.isNotificationTrayVisible) {
                Fragment f = getFragmentManager().findFragmentById(R.id.mainView);
                if (f instanceof NotificationFragment) ;
                {
                    statusBarHandler.isNotificationTrayVisible = false;
                    ((NotificationFragment) f).animateOut();
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception e");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Tracer.d("onStart PauseActivity");
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

        NotificationRetreat_.getInstance_(this.getApplicationContext()).retreat();
        try {
            if(statusBarHandler!=null)
                statusBarHandler.restoreStatusBarExpansion();
        } catch (Exception e) {
            e.printStackTrace();
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
}
