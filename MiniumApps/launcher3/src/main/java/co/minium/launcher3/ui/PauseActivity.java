package co.minium.launcher3.ui;

import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;

import com.jesusm.holocircleseekbar.lib.HoloCircleSeekBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.KeyDown;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import co.minium.launcher3.R;
import co.minium.launcher3.app.Launcher3Prefs_;
import co.minium.launcher3.notification.NotificationActivity;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.UIUtils;

@Fullscreen
@EActivity(R.layout.activity_pause)
public class PauseActivity extends CoreActivity {

    @ViewById
    Toolbar toolbar;

    @ViewById
    HoloCircleSeekBar seekbar;

    @Pref
    Launcher3Prefs_ launcherPrefs;

    @SystemService
    Vibrator vibrator;

    private final int SEEKBAR_MAX = 60;

    @AfterViews
    void afterViews() {
        setSupportActionBar(toolbar);
        seekbar.setOnSeekBarChangeListener(seekbarListener);
    }

    @Click
    void crossActionBar() {
        finish();
    }

    @Click
    void settingsActionBar() {
        startActivity(new Intent(this, NotificationActivity.class));
//        UIUtils.alert(this, getString(R.string.msg_not_yet_implemented));
    }

    private HoloCircleSeekBar.OnCircleSeekBarChangeListener seekbarListener = new HoloCircleSeekBar.OnCircleSeekBarChangeListener() {

        @Override
        public void onProgressChanged(HoloCircleSeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(HoloCircleSeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(HoloCircleSeekBar seekBar) {

        }
    };

    @KeyDown(KeyEvent.KEYCODE_VOLUME_UP)
    void volumeUpPressed() {
        Tracer.i("Volume up pressed in PauseActivity");
        int currVal = seekbar.getValue();

        if (currVal < 15) currVal = 15;
        else if (currVal < 30) currVal = 30;
        else if (currVal < 45) currVal = 45;
        else if (currVal <= 60) currVal = 60;
        
        seekbar.setValue(currVal);
    }

}
