package co.siempo.phone.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import co.siempo.phone.event.ConnectivityEvent;
import de.greenrobot.event.EventBus;
import minium.co.core.log.Tracer;

/**
 * Created by Shahab on 5/26/2017.
 */

@SuppressWarnings("ALL")
public class BatteryDataReceiver extends BroadcastReceiver implements IDynamicStatus {

    @Override
    public void onReceive(Context context, Intent intent) {
        Tracer.d("onReceive BatteryDataReceiver");
        handleIntent(context, intent);
    }

    @Override
    public IntentFilter getIntentFilter() {
        return new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

    }

    @Override
    public void register(Context context) {
        handleIntent(context, context.registerReceiver(this, getIntentFilter()));
    }

    @Override
    public void unregister(Context context) {
        context.unregisterReceiver(this);
    }

    @Override
    public void handleIntent(Context context, Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 1);
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);

        int iconLevel = (int) (((float) level / scale) * 6) + 1;

        if (status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL)
            iconLevel += 7;

        Tracer.d("BatteryDataReceiver level: " + iconLevel);
        EventBus.getDefault().post(new ConnectivityEvent(ConnectivityEvent.BATTERY, iconLevel));
    }
}
