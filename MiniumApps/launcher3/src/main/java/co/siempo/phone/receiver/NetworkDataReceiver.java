package co.siempo.phone.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;

/**
 * Created by Shahab on 5/26/2017.
 */

public class NetworkDataReceiver extends PhoneStateListener {

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
    }
}
