package minium.co.launcher2.notificationscheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import de.greenrobot.event.EventBus;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.log.Tracer;
import minium.co.core.util.DateUtils;
import minium.co.launcher2.events.NotificationSchedulerEvent;
import minium.co.launcher2.flow.SiempoNotificationService_;

@EReceiver
public class TempoSettingsReceiver extends BroadcastReceiver {

    @SystemService
    AlarmManager alarmMgr;

    PendingIntent alarmIntent;

    @Pref
    DroidPrefs_ prefs;

    public TempoSettingsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        alarmIntent = PendingIntent.getBroadcast(context, 23, new Intent(context, NotificationScheduleReceiver_.class).putExtra(NotificationScheduleReceiver.KEY_IS_NOTIFICATION_SCHEDULER, true), 0);

        boolean isSuppressCalls = intent.getBooleanExtra("EXTRA_TEMPO_SUPPRESS_CALLS", false);
        boolean isSuppressSMS = intent.getBooleanExtra("EXTRA_TEMPO_SUPPRESS_SMS", false);
        int pickerData = intent.getIntExtra("EXTRA_TEMPO_INTERVAL", 0);

        setAlarm(context, pickerData);
        makeEnabled(context, pickerData);
        prefs.notificationScheduleIndex().put(pickerData);
        prefs.notificationSchedulerValue().put(pickerData);
        prefs.notificationSchedulerSupressCalls().put(isSuppressCalls);
        prefs.notificationSchedulerSupressSMS().put(isSuppressSMS);
        EventBus.getDefault().post(new NotificationSchedulerEvent(pickerData != 0));

        Tracer.d("Tempo settings received: " + intent.getExtras().toString());
    }

    private void setAlarm(Context context, int newVal) {
        if (alarmMgr != null) alarmMgr.cancel(alarmIntent);

        if (newVal != 0) {
            long nextIntervalMillis = DateUtils.nextIntervalMillis(newVal * 60 * 1000);

            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                    nextIntervalMillis,
                    newVal * 60 * 1000, alarmIntent);

            Tracer.d("NotificationScheduleAlarm set at: " + DateUtils.log() + " || Next fire: " + DateUtils.log(nextIntervalMillis));

            prefs.notificationScheulerNextMillis().put(nextIntervalMillis);

        } else {
            prefs.isNotificationSupressed().put(true);
            context.sendBroadcast(new Intent(context, NotificationScheduleReceiver_.class));
            Tracer.d("NotificationScheduleAlarm cancelled");
        }
    }

    private void makeEnabled(Context context, int newVal) {
        if (newVal == 0) {
            prefs.isNotificationSchedulerEnabled().put(false);
            SiempoNotificationService_.intent(context).extra("start", false).start();
        } else {
            prefs.isNotificationSchedulerEnabled().put(true);
            SiempoNotificationService_.intent(context).extra("start", true).start();
        }
    }
}
