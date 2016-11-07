package minium.co.launcher2.notificationscheduler;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
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
import minium.co.core.util.UIUtils;
import minium.co.launcher2.R;
import minium.co.launcher2.events.NotificationSchedulerEvent;
import minium.co.launcher2.flow.SiempoNotificationService;
import minium.co.launcher2.flow.SiempoNotificationService_;
import minium.co.launcher2.utils.ServiceUtils;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_notification_scheduler)
public class NotificationSchedulerFragment extends CoreFragment {

    @ViewById
    NumberPicker valPicker;

    @ViewById
    TextView txtMsg;

    @ViewById
    CheckBox chkSuppressCalls;

    @ViewById
    CheckBox chkSuppressSMS;

    @Pref
    DroidPrefs_ prefs;

    @SystemService
    AlarmManager alarmMgr;

    PendingIntent alarmIntent;


    private String[] pickerData = new String[] { "0", "1", "5", "10", "15", "30", "60" };


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
        chkSuppressCalls.setChecked(prefs.notificationSchedulerSupressCalls().get());
        chkSuppressSMS.setChecked(prefs.notificationSchedulerSupressSMS().get());
        updateUI(prefs.notificationScheduleIndex().get());

        alarmIntent = PendingIntent.getBroadcast(getActivity(), 23, new Intent(getActivity(), NotificationScheduleReceiver_.class).putExtra(NotificationScheduleReceiver.KEY_IS_NOTIFICATION_SCHEDULER, true), 0);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isEnabled(getActivity())) {
            UIUtils.confirm(getActivity(), "Ebb flow manager service is not enabled. Please allow Ebb flow manager to access notification service", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                }
            });
        }
    }

    /** @return True if {@link SiempoNotificationService} is enabled. */
    public static boolean isEnabled(Context mContext) {
        return ServiceUtils.isNotificationListenerServiceRunning(mContext, SiempoNotificationService_.class);
    }

    private void makeEnabled(int newVal) {
        if (newVal == 0) {
            prefs.isNotificationSchedulerEnabled().put(false);
            SiempoNotificationService_.intent(getActivity()).extra("start", false).start();
        } else {
            prefs.isNotificationSchedulerEnabled().put(true);
            SiempoNotificationService_.intent(getActivity()).extra("start", true).start();
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

    @CheckedChange
    void chkSuppressCalls(boolean isChecked) {
        Tracer.d("chkSuppressCalls " + isChecked);
        prefs.notificationSchedulerSupressCalls().put(isChecked);
    }

    @CheckedChange
    void chkSuppressSMS(boolean isChecked) {
        Tracer.d("chkSuppressSMS " + isChecked);
        prefs.notificationSchedulerSupressSMS().put(isChecked);
    }

    @Click
    void btnSet() {
        int newVal = valPicker.getValue();
        updateUI(newVal);
        setAlarm(newVal);
        makeEnabled(newVal);
        prefs.notificationScheduleIndex().put(newVal);
        prefs.notificationSchedulerValue().put(Integer.parseInt(pickerData [newVal]));
        EventBus.getDefault().post(new NotificationSchedulerEvent(newVal != 0));
    }
}
