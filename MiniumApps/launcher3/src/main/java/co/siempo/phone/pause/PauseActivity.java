package co.siempo.phone.pause;

import android.content.DialogInterface;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Handler;
import android.view.KeyEvent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.KeyDown;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.IOException;

import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3Prefs_;
import co.siempo.phone.event.PauseStartEvent;
import de.greenrobot.event.Subscribe;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.UIUtils;

@Fullscreen
@EActivity(R.layout.activity_pause)
public class PauseActivity extends CoreActivity {

    private PauseFragment pauseFragment;
    private PauseActivatedFragment pauseActivatedFragment;

    @Pref
    Launcher3Prefs_ launcherPrefs;

    @Extra
    Tag tag;

    private Handler nfcCheckHandler;
    private Runnable nfcRunnable;

    @AfterViews
    void afterViews() {
        Tracer.d("afterviews PauseActivity");
        init();
    }

    private void init() {
        if (tag != null) {
            pauseStartEvent(new PauseStartEvent(-1));
        } else {
            pauseFragment = PauseFragment_.builder().build();
            loadFragment(pauseFragment, R.id.mainView, "main");
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        Tracer.d("onStart PauseActivity");
        if (tag != null) {
            if (nfcCheckHandler == null) nfcCheckHandler = new Handler();
            nfcCheckHandler.postDelayed(buildNfcRunnable(tag), 5000);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Tracer.d("onStop PauseActivity");
        if (nfcCheckHandler != null) {
            nfcCheckHandler.removeCallbacks(nfcRunnable);
            Ndef ndef = Ndef.get(tag);
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
            pauseActivatedFragment.stopPause();
        }
    }

    @Subscribe
    public void pauseStartEvent(PauseStartEvent event) {
        pauseActivatedFragment = PauseActivatedFragment_.builder().maxMillis(event.getMaxMillis()).build();
        loadFragment(pauseActivatedFragment, R.id.mainView, "main");
    }

    private Runnable buildNfcRunnable(final Tag tag) {
        if (nfcRunnable != null) return nfcRunnable;
        return nfcRunnable = new Runnable() {
            @Override
            public void run() {
                Ndef ndef = Ndef.get(tag);
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
}
