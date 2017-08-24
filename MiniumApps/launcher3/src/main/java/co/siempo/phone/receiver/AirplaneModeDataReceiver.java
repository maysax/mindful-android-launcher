package co.siempo.phone.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;

import minium.co.core.log.Tracer;

/**
 * Created by Shahab on 5/26/2017.
 */

@SuppressWarnings("ALL")
public class AirplaneModeDataReceiver extends BroadcastReceiver implements IDynamicStatus {

    @Override
    public void onReceive(Context context, Intent intent) {
        Tracer.d("onReceive AirplaneModeDataReceiver");
        handleIntent(context, intent);
    }

    public void handleIntent(Context context, Intent intent) {
        if (intent == null) return;

        if (intent.getBooleanExtra(TelephonyManager.EXTRA_STATE, false)) {
            Tracer.d("AirplaneModeDataReceiver On");
        } else {
            Tracer.d("AirplaneModeDataReceiver Off");
        }
    }

    @Override
    public IntentFilter getIntentFilter() {
        return new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
    }

    @Override
    public void register(Context context) {
        handleIntent(context, context.registerReceiver(this, getIntentFilter()));
    }

    @Override
    public void unregister(Context context) {
        context.unregisterReceiver(this);
    }
}
