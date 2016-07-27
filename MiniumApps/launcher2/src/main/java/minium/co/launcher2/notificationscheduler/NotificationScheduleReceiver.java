package minium.co.launcher2.notificationscheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.androidannotations.annotations.EReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import minium.co.core.log.Tracer;

@EReceiver
public class NotificationScheduleReceiver extends BroadcastReceiver {
    public NotificationScheduleReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Tracer.d("NotificationScheduleReceiver onReceive: " + new SimpleDateFormat("hh:mm:ss.SSS a", Locale.US).format(new Date()));
    }
}
