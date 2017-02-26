package co.minium.launcher3.ui;

import android.os.Vibrator;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;

import com.jesusm.holocircleseekbar.lib.HoloCircleSeekBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.KeyDown;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import co.minium.launcher3.R;
import co.minium.launcher3.app.Launcher3Prefs_;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.ui.CoreFragment;

import static co.minium.launcher3.R.id.seekbar;

@EFragment(R.layout.fragment_pause)
public class PauseFragment extends CoreFragment {


    public PauseFragment() {
        // Required empty public constructor
    }

    @ViewById
    Toolbar toolbar;

    @ViewById
    HoloCircleSeekBar seekbar;

    @Pref
    Launcher3Prefs_ launcherPrefs;

    @SystemService
    Vibrator vibrator;

    @AfterViews
    void afterViews() {
        ((CoreActivity)getActivity()).setSupportActionBar(toolbar);
        seekbar.setOnSeekBarChangeListener(seekbarListener);
    }

    @Click
    void crossActionBar() {
        ((CoreActivity)getActivity()).finish();
    }

    @Click
    void settingsActionBar() {

        ((CoreActivity)getActivity()).loadChildFragment(PausePreferenceFragment_.builder().build(),R.id.mainView);
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


    public void volumeUpPresses() {
        Tracer.i("Volume up pressed in PauseFragment");

        int currVal = seekbar.getValue();

        if (currVal < 15) currVal = 15;
        else if (currVal < 30) currVal = 30;
        else if (currVal < 45) currVal = 45;
        else if (currVal <= 60) currVal = 60;

        seekbar.setValue(currVal);
    }
}
