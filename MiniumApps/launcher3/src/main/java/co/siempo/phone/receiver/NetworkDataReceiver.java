package co.siempo.phone.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import com.james.status.data.icon.NetworkIconData;

import james.signalstrengthslib.SignalStrengths;
import minium.co.core.log.Tracer;

/**
 * Created by Shahab on 5/26/2017.
 */

public class NetworkDataReceiver extends PhoneStateListener implements IDynamicStatus {

    private Context context;
    private TelephonyManager telephonyManager;
    private boolean isRegistered;

    public NetworkDataReceiver(Context context) {
        this.context = context;
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        if (isRegistered)
            Tracer.d("NetworkDataReceiver " + (int) Math.round(SignalStrengths.getFirstValid(signalStrength)));
    }

    @Override
    public IntentFilter getIntentFilter() {
        return null;
    }

    @Override
    public void register(Context context) {
        telephonyManager.listen(this, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        isRegistered = true;
    }

    @Override
    public void unregister(Context context) {
        isRegistered = false;
    }

    @Override
    public void handleIntent(Context context, Intent intent) {

    }
}
