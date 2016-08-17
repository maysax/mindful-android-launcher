package minium.co.launcher2.notificationscheduler;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.Locale;

import de.greenrobot.event.EventBus;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreFragment;
import minium.co.core.util.DateUtils;
import minium.co.launcher2.R;
import minium.co.launcher2.events.NotificationSchedulerEvent;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_notification_scheduler)
public class NotificationSchedulerFragment extends CoreFragment {

    @ViewById
    NumberPicker valPicker;

    @ViewById
    TextView txtMsg;

    @Pref
    DroidPrefs_ prefs;

    @SystemService
    AlarmManager alarmMgr;

    PendingIntent alarmIntent;

    private String[] pickerData = new String[] { "0", "1", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60" };


    public NotificationSchedulerFragment() {
        // Required empty public constructor
    }

    @AfterViews
    void afterViews() {
        valPicker.setMinValue(0);
        valPicker.setMaxValue(pickerData.length - 1);
        valPicker.setDisplayedValues(pickerData);
        valPicker.setWrapSelectorWheel(true);
        valPicker.setValue(prefs.notificationScheduleIndex().get());
        updateUI(prefs.notificationScheduleIndex().get());

        alarmIntent = PendingIntent.getBroadcast(getActivity(), 23, new Intent(getActivity(), NotificationScheduleReceiver_.class).putExtra(NotificationScheduleReceiver.KEY_IS_NOTIFICATION_SCHEDULER, true), 0);

        valPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                updateUI(newVal);
                setAlarm(newVal);
                makeEnabled(newVal);
                prefs.notificationScheduleIndex().put(newVal);
                prefs.notificationSchedulerValue().put(Integer.parseInt(pickerData [newVal]));
                EventBus.getDefault().post(new NotificationSchedulerEvent(newVal != 0));
            }
        });
    }

    private void makeEnabled(int newVal) {
        if (newVal == 0) {
            prefs.isNotificationSchedulerEnabled().put(false);
        } else {
            prefs.isNotificationSchedulerEnabled().put(true);
        }
    }

    private void updateUI(int newVal) {
        if (newVal == 0) {
            txtMsg.setText("Notification is enabled always");
        } else if (newVal < pickerData.length){
            txtMsg.setText(String.format(Locale.US, "Notification will be enabled for every %s minutes", pickerData [newVal]));
        }

    }

    private void setAlarm(int newVal) {
        if (alarmMgr != null) alarmMgr.cancel(alarmIntent);

        if (newVal != 0) {
            long nextIntervalMillis = DateUtils.nextIntervalMillis(Integer.parseInt(pickerData [newVal]) * 60 * 1000);
            long systemElapsedRealTime = SystemClock.elapsedRealtime();
            long elapsedRealTime = systemElapsedRealTime + (nextIntervalMillis - systemElapsedRealTime);

            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                    nextIntervalMillis,
                    Integer.parseInt(pickerData [newVal]) * 60 * 1000, alarmIntent);

            Tracer.d("NotificationScheduleAlarm set at: " + DateUtils.log() + " || Next fire: " + DateUtils.log(nextIntervalMillis));

            prefs.notificationScheulerNextMillis().put(nextIntervalMillis);

        } else {
            prefs.isNotificationSupressed().put(true);
            getActivity().sendBroadcast(new Intent(getActivity(), NotificationScheduleReceiver_.class));
            Tracer.d("NotificationScheduleAlarm cancelled");
        }
    }

    @CheckedChange
    void switchSuppressCalls(CompoundButton btn, boolean isChecked) {
        Tracer.d("switchSuppressCalls " + isChecked);
        prefs.notificationSchedulerSupressCalls().put(isChecked);
    }
}
