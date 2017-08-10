package co.siempo.phone.ui;


import android.app.Fragment;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.james.status.data.IconStyleData;
import com.joanzapata.iconify.Icon;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3Prefs_;
import co.siempo.phone.battery.BatteryChangeEvent;
import co.siempo.phone.db.DBUtility;
import co.siempo.phone.event.ConnectivityEvent;
import co.siempo.phone.event.NotificationSchedulerEvent;
import co.siempo.phone.event.TempoEvent;
import co.siempo.phone.event.TopBarUpdateEvent;
import co.siempo.phone.network.NetworkUtil;
import co.siempo.phone.receiver.AirplaneModeDataReceiver;
import co.siempo.phone.receiver.BatteryDataReceiver;
import co.siempo.phone.receiver.IDynamicStatus;
import co.siempo.phone.receiver.NetworkDataReceiver;
import co.siempo.phone.receiver.WifiDataReceiver;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreFragment;

;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_top)
public class TopFragment extends CoreFragment {

    @Pref
    DroidPrefs_ prefs;

    @Pref
    Launcher3Prefs_ launcherPrefs;

    @ViewById
    ImageView imgTempo;

    @ViewById
    ImageView imgNotification;

    @ViewById
    ImageView imgBattery;

    @ViewById
    ImageView imgSignal;

    @ViewById
    ImageView imgWifi;

    @ViewById
    ImageView imgAirplane;

    @SystemService
    WifiManager wifiManager;

    FontAwesomeIcons[] batteryIcons = {
            FontAwesomeIcons.fa_battery_0,
            FontAwesomeIcons.fa_battery_1,
            FontAwesomeIcons.fa_battery_2,
            FontAwesomeIcons.fa_battery_3,
            FontAwesomeIcons.fa_battery_4
    };

    TelephonyManager telephonyManager;
    SignalStrengthListener listener;
    private int currentBatteryLevel;

    IDynamicStatus airplaneModeDataReceiver;
    IDynamicStatus batteryDataReceiver;
    IDynamicStatus networkDataReceiver;
    IDynamicStatus wifiDataReceiver;

    public TopFragment() {
        // Required empty public constructor
    }

    @AfterViews
    void afterViews() {
        // Default text
        //updateBatteryText(50);
        updateUI();
    }

    private void updateUI() {
        imgSignal.setVisibility(NetworkUtil.isAirplaneModeOn(context) ? View.GONE : View.VISIBLE);
        imgWifi.setVisibility(NetworkUtil.isAirplaneModeOn(context) ? View.GONE : View.VISIBLE);
        imgWifi.setVisibility(NetworkUtil.isWifiOn(context) ? View.VISIBLE : View.GONE);
        imgAirplane.setVisibility(NetworkUtil.isAirplaneModeOn(context) ? View.VISIBLE : View.GONE);
        imgWifi.setImageResource(getWifiIcon(WifiManager.calculateSignalLevel(wifiManager.getConnectionInfo().getRssi(), 5)));

        long notifCount = DBUtility.getTableNotificationSmsDao().count() + DBUtility.getCallStorageDao().count();
        imgNotification.setVisibility(notifCount == 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        onTempoEvent(new TempoEvent(launcherPrefs.isTempoActive().get()));
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            listener = new SignalStrengthListener();
            telephonyManager.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("hardikkamothi","onAttach");
        airplaneModeDataReceiver = new AirplaneModeDataReceiver();
        airplaneModeDataReceiver.register(context);

        wifiDataReceiver = new WifiDataReceiver();
        wifiDataReceiver.register(context);
        batteryDataReceiver = new BatteryDataReceiver();
        batteryDataReceiver.register(context);
        networkDataReceiver = new NetworkDataReceiver(context);
        networkDataReceiver.register(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("hardikkamothi","onDetach");
        try{

            airplaneModeDataReceiver.unregister(context);
            batteryDataReceiver.unregister(context);
            networkDataReceiver.unregister(context);
            wifiDataReceiver.unregister(context);
        }
        catch (Exception e){

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        telephonyManager.listen(listener, PhoneStateListener.LISTEN_NONE);
    }

    @Trace(tag = TRACE_TAG)
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onBatteryLevelChange(BatteryChangeEvent event) {
        updateBatteryText(event.getLevel());
    }

    private void updateBatteryText(int level) {

//        if (level > 0)
//            currentBatteryLevel = level;
//
//        iTxt3.setText(getString(R.string.format_battery,
//                prefs.isNotificationSchedulerEnabled().get() ? String.format(Locale.US, "{fa-bell 12dp} %d min",
//                prefs.notificationSchedulerValue().get()) : "", currentBatteryLevel));
//
//        iTxt3.setCompoundDrawablesWithIntrinsicBounds(null, null, new IconDrawable(context, getBatteryIcon(currentBatteryLevel)).colorRes(R.color.white).sizeDp(12), null);
//
    }

    private Icon getBatteryIcon(int level) {
        if (level < 15) return batteryIcons[0];
        else if (level <= 25) return batteryIcons[1];
        else if (level <= 65) return batteryIcons[2];
        else if (level <= 80) return batteryIcons[3];
        else if (level > 80) return batteryIcons[4];
        return batteryIcons[2];
    }

    private class SignalStrengthListener extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            int strengthVal = signalStrength.getGsmSignalStrength();
            strengthVal = (2 * strengthVal) - 113;
            updateSignalText(strengthVal);
        }
    }

    private void updateSignalText(int strength) {
//        Tracer.i("Signal strength: " + strength + " Operator: " + telephonyManager.getNetworkOperatorName());
//        if (isAdded())
//            iTxt1.setText(getString(R.string.format_signal, telephonyManager.getNetworkOperatorName()));
    }

    @Subscribe
    public void onNotificationScheulerEvent(NotificationSchedulerEvent event) {
        updateBatteryText(-1);
    }

    @Subscribe
    public void onTempoEvent(TempoEvent event) {
        if (event.isStarting()) {
            imgTempo.setVisibility(View.VISIBLE);
            launcherPrefs.isTempoActive().put(true);
        } else {
            imgTempo.setVisibility(View.GONE);
            launcherPrefs.isTempoActive().put(false);
        }
    }

    @Subscribe
    public void updateTopBar(TopBarUpdateEvent event) {
        updateUI();
    }

    @Subscribe
    public void onConnectivityEvent(ConnectivityEvent event) {
        if (event.getState() == ConnectivityEvent.AIRPLANE) {
            imgSignal.setVisibility(NetworkUtil.isAirplaneModeOn(context) ? View.GONE : View.VISIBLE);
            imgWifi.setVisibility(NetworkUtil.isAirplaneModeOn(context) ? View.GONE : View.VISIBLE);
            imgAirplane.setVisibility(NetworkUtil.isAirplaneModeOn(context) ? View.VISIBLE : View.GONE);
        } else if (event.getState() == ConnectivityEvent.WIFI) {
            imgWifi.setVisibility(NetworkUtil.isWifiOn(context) ? View.VISIBLE : View.GONE);
            imgWifi.setImageResource(getWifiIcon(event.getValue()));
        } else if (event.getState() == ConnectivityEvent.BATTERY) {
            imgBattery.setImageResource(getBatteryIcon2(event.getValue()));
        } else if (event.getState() == ConnectivityEvent.NETWORK) {
            imgSignal.setImageResource(getNetworkIcon(event.getValue()));
        }
    }

    private int getBatteryIcon2(int level) {
        int icons[] = {
                IconStyleData.TYPE_VECTOR,
                com.james.status.R.drawable.ic_battery_alert,
                com.james.status.R.drawable.ic_battery_20,
                com.james.status.R.drawable.ic_battery_30,
                com.james.status.R.drawable.ic_battery_50,
                com.james.status.R.drawable.ic_battery_60,
                com.james.status.R.drawable.ic_battery_80,
                com.james.status.R.drawable.ic_battery_90,
                com.james.status.R.drawable.ic_battery_full,
                com.james.status.R.drawable.ic_battery_charging_20,
                com.james.status.R.drawable.ic_battery_charging_30,
                com.james.status.R.drawable.ic_battery_charging_50,
                com.james.status.R.drawable.ic_battery_charging_60,
                com.james.status.R.drawable.ic_battery_charging_80,
                com.james.status.R.drawable.ic_battery_charging_90,
                com.james.status.R.drawable.ic_battery_charging_full
        };

        return icons[level + 1];
    }

    private int getWifiIcon(int level) {
        int icons[] = {
                IconStyleData.TYPE_VECTOR,
                com.james.status.R.drawable.ic_wifi_triangle_0,
                com.james.status.R.drawable.ic_wifi_triangle_1,
                com.james.status.R.drawable.ic_wifi_triangle_2,
                com.james.status.R.drawable.ic_wifi_triangle_3,
                com.james.status.R.drawable.ic_wifi_triangle_4
        };

        return icons[level + 1];
    }

    private int getNetworkIcon(int level) {
        int icons[] = {
                IconStyleData.TYPE_VECTOR,
                com.james.status.R.drawable.ic_signal_0,
                com.james.status.R.drawable.ic_signal_1,
                com.james.status.R.drawable.ic_signal_2,
                com.james.status.R.drawable.ic_signal_3,
                com.james.status.R.drawable.ic_signal_4
        };

        return icons[level + 1];
    }
}
