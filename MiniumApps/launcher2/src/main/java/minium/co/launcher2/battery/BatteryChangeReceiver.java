package minium.co.launcher2.battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import org.androidannotations.annotations.EReceiver;

import de.greenrobot.event.EventBus;
import minium.co.core.util.UIUtils;

/**
 * Created by shahab on 3/16/16.
 */
@EReceiver
public class BatteryChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
            String msg = "";

            if (usbCharge)
                msg += "USB charger connected.";
            else if (acCharge)
                msg += "AC charger connected.";
            else
                msg += "Charger connected.";

            UIUtils.toast(context, msg);

        } else if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
            UIUtils.toast(context, "Charger unplugged");

        }

        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level / (float) scale;

        EventBus.getDefault().post(new BatteryChangeEvent(level));
    }
}
