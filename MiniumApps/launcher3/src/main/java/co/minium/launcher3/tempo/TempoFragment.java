package co.minium.launcher3.tempo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
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
import co.minium.launcher3.tempo.TempoPreferenceFragment_;
import de.greenrobot.event.EventBus;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.ui.CoreFragment;
import minium.co.core.util.DateUtils;

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

    @ViewById
    Button btnOff;

    @ViewById
    Button btnOn;

    @SystemService
    AlarmManager alarmMgr;

    PendingIntent alarmIntent;

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

        alarmIntent = PendingIntent.getBroadcast(getActivity(),
                23,
                new Intent(getActivity(),
                        TempoReceiver_.class).putExtra(TempoReceiver.KEY_IS_TEMPO, true)
                , 0);

    }

    @Click
    void imgLeft() {
        getActivity().finish();
    }


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
        if (alarmMgr != null) alarmMgr.cancel(alarmIntent);
    }
    @Click
    void btnOn(){
        btnOn.setTextColor(Color.parseColor("#332d6d"));
        btnOff.setTextColor(Color.parseColor("#4d332d6d"));
        btnOff.setActivated(false);
        btnOn.setActivated(true);
        setAlarm();
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

    private void setAlarm() {
        if (alarmMgr != null) alarmMgr.cancel(alarmIntent);

        long nextIntervalMillis = DateUtils.nextIntervalMillis(seekbar.getValue() * 60 * 1000);

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                nextIntervalMillis,
                seekbar.getValue() * 60 * 1000, alarmIntent);

        Tracer.d("NotificationScheduleAlarm set at: " + DateUtils.log() + " || Next fire: " + DateUtils.log(nextIntervalMillis));

        launcherPrefs.tempoNextNotificationMillis().put(nextIntervalMillis);

//        else {
//            prefs.isNotificationSupressed().put(true);
//            getActivity().sendBroadcast(new Intent(getActivity(), TempoReceiver_.class));
//            Tracer.d("NotificationScheduleAlarm cancelled");
//        }
    }

}
