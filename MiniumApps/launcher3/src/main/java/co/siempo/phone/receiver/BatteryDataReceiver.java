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

        int batterystatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = batterystatus == BatteryManager.BATTERY_STATUS_CHARGING ||
                batterystatus == BatteryManager.BATTERY_STATUS_FULL;
        int batterylevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int batteryscale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = batterylevel / (float)batteryscale;
        int batteryStatus=(int)((batteryPct)*100);
        String chargingStatus="OFF";
        if(isCharging){
            chargingStatus="ON";
        }
        else{
            chargingStatus="OFF";
        }
        Tracer.d("BatteryDataReceiver level: " + batteryStatus);
        EventBus.getDefault().post(new ConnectivityEvent(ConnectivityEvent.BATTERY, batteryStatus,chargingStatus));
    }
}
