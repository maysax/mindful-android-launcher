package co.siempo.phone.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by Shahab on 5/26/2017.
 */

public class AirplaneModeDataReceiver extends BroadcastReceiver implements IDynamicStatus {

    @Override
    public void onReceive(Context context, Intent intent) {

    }

    @Override
    public IntentFilter getIntentFilter() {
        return new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
    }

    @Override
    public void register(Context context) {
        context.registerReceiver(this, getIntentFilter());
    }

    @Override
    public void unregister(Context context) {
        context.unregisterReceiver(this);
    }
}
