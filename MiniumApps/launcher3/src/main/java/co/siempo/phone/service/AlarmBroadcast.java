package co.siempo.phone.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;

import co.siempo.phone.util.PackageUtil;
import minium.co.core.log.Tracer;

/**
 * Created by rajeshjadi on 8/1/18.
 */

public class AlarmBroadcast extends BroadcastReceiver {
    public static final String ACTION_ALARM = "co.siempo.phone.ACTION_ALARM";

    @Override
    public void onReceive(Context context, Intent intent) {
        Tracer.d("Time", "" + Calendar.getInstance().getTime());
        PackageUtil.enableAlarm(context);
        Intent intent1 = new Intent(context, AlarmService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent1);
        }
        else{
            context.startService(intent1);
        }
    }
}
