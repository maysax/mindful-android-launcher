package co.minium.launcher3.pause;

import android.content.ContextWrapper;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.TimeUtils;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jesusm.holocircleseekbar.lib.HoloCircleSeekBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.KeyDown;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import co.minium.launcher3.R;
import co.minium.launcher3.app.Launcher3Prefs_;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.ui.CoreFragment;

import static co.minium.launcher3.R.id.seekbar;
import static co.minium.launcher3.R.id.start;

@EFragment(R.layout.fragment_pause)
public class PauseFragment extends CoreFragment {


    public PauseFragment() {
        // Required empty public constructor
    }

    @ViewById
    Toolbar toolbar;

    @ViewById
    HoloCircleSeekBar seekbar;

    @ViewById
    ImageView imgBackground;

    @ViewById
    TextView titleActionBar;

    @ViewById
    TextView txtRemainingTime;

    @ViewById
    ViewGroup endingLayout;

    @ViewById
    TextView txtEndingTime;

    @Pref
    Launcher3Prefs_ launcherPrefs;

    @SystemService
    Vibrator vibrator;

    Handler handler;

    private int atMillis = 0;
    private int maxMillis = 0;

    @AfterViews
    void afterViews() {
        ((CoreActivity)getActivity()).setSupportActionBar(toolbar);
        seekbar.setOnSeekBarChangeListener(seekbarListener);
        handler = new Handler();
    }

    @Click
    void crossActionBar() {
        stopPause();
        getActivity().finish();
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
            handler.removeCallbacks(startPauseRunnable);
            handler.postDelayed(startPauseRunnable, 2000);
        }
    };

    private Runnable startPauseRunnable = new Runnable() {
        @Override
        public void run() {
            startPause();
            handler.removeCallbacks(startPauseRunnable);
        }
    };

    private Runnable pauseActiveRunnable = new Runnable() {
        @Override
        public void run() {
            atMillis += 1000;

            if (atMillis >= maxMillis) {
                stopPause();
            } else {
                Tracer.d("Now : " + atMillis + " seekbar value: " + atMillis / (1000 * 60.0f));
                seekbar.setValue(atMillis / (1000 * 60.0f));
                txtRemainingTime.setText(String.format(Locale.US, "%d minute", TimeUnit.MILLISECONDS.toMinutes(maxMillis - atMillis)));
            }

            handler.postDelayed(this, 1000);
        }
    };


    public void volumeUpPresses() {
        if (!launcherPrefs.isPauseActive().get()) {
            Tracer.i("Volume up pressed in PauseFragment");

            int currVal = seekbar.getValue();

            if (currVal < 15) currVal = 15;
            else if (currVal < 30) currVal = 30;
            else if (currVal < 45) currVal = 45;
            else if (currVal <= 60) currVal = 60;

            seekbar.setValue(currVal);
        }
    }

    private void startPause() {
        atMillis = 0;
        maxMillis = seekbar.getValue() * 60 * 1000;
        seekbar.setMax(seekbar.getValue());
        seekbar.setValue(0);
        seekbar.setActive(false);
        handler.postDelayed(pauseActiveRunnable, 1000);
        imgBackground.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));
        imgBackground.setVisibility(View.VISIBLE);
        endingLayout.setVisibility(View.VISIBLE);
        seekbar.setTitleColor(ContextCompat.getColor(getActivity(), R.color.white));
        seekbar.setSubtitleColor(ContextCompat.getColor(getActivity(), R.color.white));
        seekbar.setActiveWheelColor(ContextCompat.getColor(getActivity(), R.color.white));
        titleActionBar.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        txtRemainingTime.setVisibility(View.VISIBLE);

        Calendar cal = Calendar.getInstance(Locale.US);
        cal.add(Calendar.MILLISECOND, maxMillis);
        txtEndingTime.setText(new SimpleDateFormat("hh:mm a", Locale.US).format(cal.getTime()));
    }

    private void stopPause() {
        seekbar.setValue(0);
        seekbar.setShowTitle(false);
        handler.removeCallbacks(pauseActiveRunnable);
        launcherPrefs.isPauseActive().put(false);
        endingLayout.setVisibility(View.INVISIBLE);
    }
}
