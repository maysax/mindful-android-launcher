package minium.co.launcher2.notificationscheduler;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.orm.query.Condition;
import com.orm.query.Select;

import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import minium.co.core.app.DroidPrefs_;
import minium.co.core.log.Tracer;
import minium.co.launcher2.model.MissedCallItem;
import minium.co.launcher2.utils.AudioUtils;

@EReceiver
public class NotificationScheduleReceiver extends BroadcastReceiver {

    @Pref
    DroidPrefs_ prefs;

    @SystemService
    NotificationManager notificationManager;

    public NotificationScheduleReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Tracer.d("NotificationScheduleReceiver onReceive: " + new SimpleDateFormat("hh:mm:ss.SSS a", Locale.US).format(new Date()));

        if (prefs.isFlowRunning().get()) {
            prefs.isNotificationSupressed().put(true);
        } else if (prefs.isNotificationSupressed().get()) {
            showNotifications(context);
            prefs.isNotificationSupressed().put(false);
        } else {
            showNotifications(context);
        }
    }

    void showNotifications(Context context) {
        List<MissedCallItem> missedCalls = Select.from(MissedCallItem.class)
                .where(Condition.prop("has_displayed").eq(0))
                .list();

        Tracer.d("Generating missed call notifications: " + missedCalls.size());

        if (missedCalls.size() > 0) {
            new AudioUtils().playNotificationSound(context);

            for (MissedCallItem item : missedCalls)
                showCallNotifications(context, item.getNumber());

            MissedCallItem.deleteAll(MissedCallItem.class);
        }
    }

    private void showCallNotifications(Context context, String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,  PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification  = new Notification.Builder(context)
                .setContentTitle("Missed Call!")
                .setContentText("Missed call from " + number)
                .setSmallIcon(android.R.drawable.sym_call_missed)
                .setContentIntent(pIntent)
                .setAutoCancel(true).build();
        notificationManager.notify(0, notification);
    }
}
