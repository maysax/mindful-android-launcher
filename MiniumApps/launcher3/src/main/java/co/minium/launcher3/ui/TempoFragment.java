package co.minium.launcher3.ui;

import android.graphics.Color;
import android.os.Vibrator;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
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
import minium.co.core.ui.CoreActivity;
import minium.co.core.ui.CoreFragment;

@EFragment(R.layout.fragment_tempo)
public class TempoFragment extends CoreFragment {


    public TempoFragment() {
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

        button_off.setActivated(true);
        button_on.setActivated(false);
    }

    @Click
    void crossActionBar() {
        ((CoreActivity)getActivity()).finish();
    }
    @ViewById
    Button button_off;
    @ViewById
    Button button_on;

    @ViewById
    TextView text_status;
    @Click
    void button_off(){
        button_on.setTextColor(Color.parseColor("#4d332d6d"));
        button_off.setTextColor(Color.parseColor("#332d6d"));
        button_off.setActivated(true);
        button_on.setActivated(false);
        text_status.setText("Turn on Tempo to batch notifications at set intervals");

    }
    @Click
    void button_on(){
        button_on.setTextColor(Color.parseColor("#332d6d"));
        button_off.setTextColor(Color.parseColor("#4d332d6d"));
        button_off.setActivated(false);
        button_on.setActivated(true);

            text_status.setText("Notifications now come batched every  "+seekbar.getValue() +"  minutes, starting at the top of the hour");

    }
    @Click
    void settingsActionBar() {

        ((CoreActivity)getActivity()).loadChildFragment(TempoPreferenceFragment_.builder().build(),R.id.mainView);
    }

    private HoloCircleSeekBar.OnCircleSeekBarChangeListener seekbarListener = new HoloCircleSeekBar.OnCircleSeekBarChangeListener() {

        @Override
        public void onProgressChanged(HoloCircleSeekBar seekBar, int progress, boolean fromUser) {
            if (button_on.isActivated()){
                text_status.setText("Notifications now come batched every  "+progress +"  minutes, starting at the top of the hour");
            }
        }

        @Override
        public void onStartTrackingTouch(HoloCircleSeekBar seekBar) {


        }

        @Override
        public void onStopTrackingTouch(HoloCircleSeekBar seekBar) {


        }
    };

}
