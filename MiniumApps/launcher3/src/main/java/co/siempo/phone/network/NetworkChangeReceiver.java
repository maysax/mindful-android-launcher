package co.siempo.phone.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.androidannotations.annotations.EReceiver;

import co.siempo.phone.event.ConnectivityEvent;
import de.greenrobot.event.EventBus;
import minium.co.core.log.Tracer;

import static co.siempo.phone.network.NetworkUtil.isAirplaneModeOn;

/**
 * Created by Shahab on 5/2/2017.
 */

@EReceiver
public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE") ||
                intent.getAction().equals("android.net.wifi.WIFI_STATE_CHANGED")) {
            Tracer.i(NetworkUtil.getConnectivityStatusString(context));
            EventBus.getDefault().post(new ConnectivityEvent(ConnectivityEvent.WIFI));
        } else if (intent.getAction().equals("android.intent.action.AIRPLANE_MODE")) {
            Tracer.i("Airplane mode: " + isAirplaneModeOn(context));
            EventBus.getDefault().post(new ConnectivityEvent(ConnectivityEvent.AIRPLANE));
        }
    }
}
