package co.siempo.phone.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import co.siempo.phone.event.ConnectivityEvent;
import de.greenrobot.event.EventBus;
import james.signalstrengthslib.SignalStrengths;
import minium.co.core.log.Tracer;

/**
 * Created by Shahab on 5/26/2017.
 */

public class NetworkDataReceiver extends PhoneStateListener implements IDynamicStatus {

    private Context context;
    private TelephonyManager telephonyManager;
    private boolean isRegistered;
    private ConnectivityManager connectivityManager;

    public NetworkDataReceiver(Context context) {
        this.context = context;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        if (isRegistered) {
            int round = (int) Math.round(SignalStrengths.getFirstValid(signalStrength));
            Tracer.d("NetworkDataReceiver " + round);
            if(telephonyManager.getNetworkOperator().equalsIgnoreCase("")){
                EventBus.getDefault().post(new ConnectivityEvent(ConnectivityEvent.NETWORK, -1, ""));
            }else{
                NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (networkInfo != null && networkInfo.isAvailable()) {
                    EventBus.getDefault().post(new ConnectivityEvent(ConnectivityEvent.NETWORK, round, getNetworkClass(context)));
                } else {
                    EventBus.getDefault().post(new ConnectivityEvent(ConnectivityEvent.NETWORK, round, ""));
                }
            }
        }
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

    public String getNetworkClass(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = mTelephonyManager.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "4G";
            default:
                return "";
        }
    }

    @Override
    public void unregister(Context context) {
        isRegistered = false;
    }

    @Override
    public void handleIntent(Context context, Intent intent) {

    }
}
