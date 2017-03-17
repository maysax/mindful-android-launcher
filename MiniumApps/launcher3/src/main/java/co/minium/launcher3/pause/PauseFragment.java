package co.minium.launcher3.pause;

import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jesusm.holocircleseekbar.lib.HoloCircleSeekBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import co.minium.launcher3.R;
import co.minium.launcher3.app.Launcher3Prefs_;
import co.minium.launcher3.event.PauseStartEvent;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.ui.CoreFragment;



@EFragment(R.layout.fragment_pause)
public class PauseFragment extends CoreFragment {


    public PauseFragment() {
        // Required empty public constructor
    }

    @ViewById
    Toolbar toolbar;
    @ViewById
    ImageButton pause_button;
    @ViewById
    HoloCircleSeekBar seekbar;

    @ViewById
    ImageView imgBackground;

    @ViewById
    TextView titleActionBar;

    @Pref
    Launcher3Prefs_ launcherPrefs;

    @SystemService
    Vibrator vibrator;

    Handler handler;


    @AfterViews
    void afterViews() {
        ((CoreActivity)getActivity()).setSupportActionBar(toolbar);
        seekbar.setOnSeekBarChangeListener(seekbarListener);
        titleActionBar.setText(R.string.title_pause);
        handler = new Handler();
    }
    @Click
    void pause_button(){
        startPause();

    }
    @Click
    void imgLeft() {
        getActivity().onBackPressed();
    }

    @Click
    void imgRight() {
        if (launcherPrefs.isPauseActive().get()) {
            getActivity().onBackPressed();
        } else {
            ((CoreActivity)getActivity()).loadChildFragment(PausePreferenceFragment_.builder().build(),R.id.mainView);
        }
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

    private Runnable startPauseRunnable = new Runnable() {
        @Override
        public void run() {
            startPause();
            handler.removeCallbacks(startPauseRunnable);
        }
    };

    public void volumeUpPresses() {
        if (!launcherPrefs.isPauseActive().get()) {
            Tracer.i("Volume up pressed in MMTimePickerFragment");

            int currVal = seekbar.getValue();

            if (currVal < 15) currVal = 15;
            else if (currVal < 30) currVal = 30;
            else if (currVal < 45) currVal = 45;
            else if (currVal <= 60) currVal = 60;

            seekbar.setValue(currVal);
        }
    }

    private void startPause() {
        EventBus.getDefault().post(new PauseStartEvent(seekbar.getValue() * 60 * 1000));
    }
}
