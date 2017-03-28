package co.minium.launcher3.pause;

import android.content.DialogInterface;
import android.view.KeyEvent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.KeyDown;
import org.androidannotations.annotations.sharedpreferences.Pref;

import co.minium.launcher3.R;
import co.minium.launcher3.app.Launcher3Prefs_;
import co.minium.launcher3.event.PauseStartEvent;
import de.greenrobot.event.Subscribe;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.UIUtils;

@Fullscreen
@EActivity(R.layout.activity_pause)
public class PauseActivity extends CoreActivity {

    private PauseFragment pauseFragment;
    private PauseActivatedFragment pauseActivatedFragment;

    @Pref
    Launcher3Prefs_ launcherPrefs;

    @AfterViews
    void afterViews() {
        init();
    }

    private void init() {
        pauseFragment = PauseFragment_.builder().build();
        loadFragment(pauseFragment, R.id.mainView, "main");
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
                if (pauseActivatedFragment != null) {
                    pauseActivatedFragment.stopPause();
                }
            }
        });
    }

    @Subscribe
    public void pauseStartEvent(PauseStartEvent event) {
        pauseActivatedFragment = PauseActivatedFragment_.builder().maxMillis(event.getMaxMillis()).build();
        loadFragment(pauseActivatedFragment, R.id.mainView, "main");
    }
}
