package co.siempo.phone.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;

import co.siempo.phone.log.Tracer;
import co.siempo.phone.utils.PackageUtil;
import co.siempo.phone.utils.PrefSiempo;

/**
 * Created by rajeshjadi on 8/1/18.
 */

public class AlarmBroadcast extends BroadcastReceiver {
    public static final String ACTION_ALARM = "co.siempo.phone.ACTION_ALARM";

    @Override
    public void onReceive(Context context, Intent intent) {
        Tracer.d("Tracking Time", "" + Calendar.getInstance().getTime());
        int tempoType = PrefSiempo.getInstance(context).read(PrefSiempo
                .TEMPO_TYPE, 0);
        if (tempoType == 1) {
            PackageUtil.enableAlarm(context, PackageUtil.batchMode(context));
        } else {

        }
        Intent intent1 = new Intent(context, AlarmService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent1);
        } else {
            context.startService(intent1);
        }
    }
}
