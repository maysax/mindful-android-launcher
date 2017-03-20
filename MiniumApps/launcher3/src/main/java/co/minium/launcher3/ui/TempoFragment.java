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
import co.minium.launcher3.event.TempoEvent;
import de.greenrobot.event.EventBus;
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

    @ViewById
    TextView titleActionBar;

    @Pref
    Launcher3Prefs_ launcherPrefs;

    @SystemService
    Vibrator vibrator;

    @AfterViews
    void afterViews() {
        ((CoreActivity)getActivity()).setSupportActionBar(toolbar);
        seekbar.setOnSeekBarChangeListener(seekbarListener);
        titleActionBar.setText(R.string.title_tempo);
        btnOff.setActivated(true);
        btnOn.setActivated(false);
    }

    @Click
    void imgLeft() {
        getActivity().finish();
    }
    @ViewById
    Button btnOff;
    @ViewById 
    Button btnOn;

    @ViewById
    TextView text_status;
    @Click
    void btnOff(){
        btnOn.setTextColor(Color.parseColor("#4d332d6d"));
        btnOff.setTextColor(Color.parseColor("#332d6d"));
        btnOff.setActivated(true);
        btnOn.setActivated(false);
        text_status.setText("Turn on Tempo to batch notifications at set intervals");
        EventBus.getDefault().post(new TempoEvent(false));

    }
    @Click
    void btnOn(){
        btnOn.setTextColor(Color.parseColor("#332d6d"));
        btnOff.setTextColor(Color.parseColor("#4d332d6d"));
        btnOff.setActivated(false);
        btnOn.setActivated(true);

            text_status.setText("Notifications now come batched every  "+seekbar.getValue() +"  minutes, starting at the top of the hour");
        EventBus.getDefault().post(new TempoEvent(true));
    }
    @Click
    void imgRight() {

        ((CoreActivity)getActivity()).loadChildFragment(TempoPreferenceFragment_.builder().build(),R.id.mainView);
    }

    private HoloCircleSeekBar.OnCircleSeekBarChangeListener seekbarListener = new HoloCircleSeekBar.OnCircleSeekBarChangeListener() {

        @Override
        public void onProgressChanged(HoloCircleSeekBar seekBar, int progress, boolean fromUser) {
            if (btnOn.isActivated()){
                text_status.setText("Notifications now come batched every  "+progress +"  minutes, starting at the top of the hour");
            }
        }

        @Override
        public void onStartTrackingTouch(HoloCircleSeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(HoloCircleSeekBar seekBar) {
            int currVal = seekbar.getValue();

            if (currVal <= 22) currVal = 15;
            else if (currVal <= 45) currVal = 30;
            else currVal = 60;

            seekbar.setValue(currVal);
        }
    };

}
