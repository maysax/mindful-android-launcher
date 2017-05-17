package co.siempo.phone.tempo;

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

import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3Prefs_;
import co.siempo.phone.db.CallStorageDao;
import co.siempo.phone.db.DBUtility;
import co.siempo.phone.db.TableNotificationSmsDao;
import co.siempo.phone.event.TempoEvent;
import co.siempo.phone.util.AudioUtils;
import de.greenrobot.event.EventBus;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.ui.CoreFragment;
import minium.co.core.util.DateUtils;

import static co.siempo.phone.app.Constants.DEFAULT_TEMPO_MINUTE;
import static co.siempo.phone.app.Launcher3App.DND_START_STOP_ACTION;

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

    TableNotificationSmsDao smsDao;
    CallStorageDao callStorageDao;

    @AfterViews
    void afterViews() {
        ((CoreActivity)getActivity()).setSupportActionBar(toolbar);
        titleActionBar.setText(R.string.title_tempo);

        updateUI(launcherPrefs.isTempoActive().get());

        seekbar.setOnSeekBarChangeListener(seekbarListener);

        alarmIntent = PendingIntent.getBroadcast(getActivity(),
                23,
                new Intent(getActivity(), TempoReceiver_.class).putExtra(TempoReceiver.KEY_IS_TEMPO, true)
                , 0);
    }

    private void updateUI(Boolean isTempoActive) {
        toggleButton(isTempoActive);
        seekbar.setValue(isTempoActive ? launcherPrefs.tempoIntervalMinutes().get() : DEFAULT_TEMPO_MINUTE);
        setStatus();
    }

    @Click
    void imgLeft() {
        getActivity().finish();
    }


    @ViewById
    TextView text_status;

    @Click
    void btnOff(){
        toggleButton(false);
        launcherPrefs.isTempoActive().put(false);
        setStatus();
        EventBus.getDefault().post(new TempoEvent(false));
        getActivity().sendBroadcast(new Intent().setAction(DND_START_STOP_ACTION));
        tempoHandler();
        if (alarmMgr != null) alarmMgr.cancel(alarmIntent);
    }
    @Click
    void btnOn(){
        toggleButton(true);
        setAlarm();
        launcherPrefs.isTempoActive().put(true);
        setStatus();
        getActivity().sendBroadcast(new Intent().setAction(DND_START_STOP_ACTION));
        EventBus.getDefault().post(new TempoEvent(true));
    }

    private void toggleButton(boolean enable) {
        if (enable) {
            btnOn.setTextColor(Color.parseColor("#332d6d"));
            btnOff.setTextColor(Color.parseColor("#4d332d6d"));
            btnOff.setActivated(false);
            btnOn.setActivated(true);
        } else {
            btnOn.setTextColor(Color.parseColor("#4d332d6d"));
            btnOff.setTextColor(Color.parseColor("#332d6d"));
            btnOff.setActivated(true);
            btnOn.setActivated(false);
        }
    }

    @Click
    void imgRight() {
        ((CoreActivity)getActivity()).loadChildFragment(TempoPreferenceFragment_.builder().build(),R.id.mainView);
    }

    private HoloCircleSeekBar.OnCircleSeekBarChangeListener seekbarListener = new HoloCircleSeekBar.OnCircleSeekBarChangeListener() {

        @Override
        public void onProgressChanged(HoloCircleSeekBar seekBar, int progress, boolean fromUser) {
            setStatus();
            setValue();
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
            setStatus();
            setValue();
        }
    };

    private void setValue() {
        launcherPrefs.tempoIntervalMinutes().put(seekbar.getValue());
    }

    private void setStatus() {
        if (btnOn.isActivated()){
            text_status.setText(getString(R.string.msg_tempo_active, seekbar.getValue()));
        } else {
            text_status.setText(R.string.msg_tempo_inactive);
        }
    }

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

    private void tempoHandler() {
        smsDao = DBUtility.getNotificationDao();
        callStorageDao = DBUtility.getCallStorageDao();

        long smsCount = smsDao.queryBuilder().count();
        long callCount = callStorageDao.queryBuilder().count();

        if (smsCount + callCount > 0) {
            AudioUtils.playnotification(context);
        }
    }

}
