package co.siempo.phone.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import co.siempo.phone.event.ConnectivityEvent;
import de.greenrobot.event.EventBus;
import minium.co.core.log.Tracer;

/**
 * Created by Shahab on 5/26/2017.
 */

public class WifiDataReceiver extends BroadcastReceiver implements IDynamicStatus {
    int level = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        handleIntent(context, intent);
    }

    @Override
    public IntentFilter getIntentFilter() {
        return new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
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
        try {
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (networkInfo != null) {
                @SuppressLint("WifiManagerPotentialLeak")
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                int numberOfLevels = 5;
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
                Tracer.d("WifiDataReceiver, label: " + level);
            } else {
                level = 0;
            }
            EventBus.getDefault().post(new ConnectivityEvent(ConnectivityEvent.WIFI, level));
        } catch (Exception e) {
            Tracer.e(e);
        }

    }
}
