package co.siempo.phone.ui;


import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import co.siempo.phone.db.NotificationSwipeEvent;
import co.siempo.phone.event.ConnectivityEvent;
import co.siempo.phone.event.NewNotificationEvent;
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

    private String TAG = "TopFragment";

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

//    @ViewById
//    TextView txtType; // SSA-206 Quick Setting for feature reference.

//    @ViewById
//    ImageView imgBluetooth; // SSA-206 Quick Setting for feature reference.

//    @ViewById
//    ImageView imgDND; // SSA-206 Quick Setting for feature reference.

    @ViewById
    ImageView imgAirplane; // SSA-206 Quick Setting for feature reference.

    @SystemService
    WifiManager wifiManager;

    @SystemService
    AudioManager audioManager;

    @SystemService
    ConnectivityManager connectivityManager;

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
    }

    @AfterViews
    void afterViews() {
        airplaneModeDataReceiver = new AirplaneModeDataReceiver();
        airplaneModeDataReceiver.register(context);
        wifiDataReceiver = new WifiDataReceiver();
        wifiDataReceiver.register(context);
        batteryDataReceiver = new BatteryDataReceiver();
        batteryDataReceiver.register(context);
        networkDataReceiver = new NetworkDataReceiver(context);
        networkDataReceiver.register(context);
    }

    private void updateUI() {
        if (NetworkUtil.isAirplaneModeOn(context)) {
            imgAirplane.setVisibility(View.VISIBLE);// SSA-206 Quick Setting for feature reference.
            imgSignal.setVisibility(View.GONE);
            //txtType.setVisibility(View.GONE);// SSA-206 Quick Setting for feature reference.
            imgWifi.setVisibility(View.GONE);
            //imgBluetooth.setVisibility(View.GONE);// SSA-206 Quick Setting for feature reference.
            //imgDND.setVisibility(View.GONE);// SSA-206 Quick Setting for feature reference.
        } else {
            imgAirplane.setVisibility(View.GONE);// SSA-206 Quick Setting for feature reference.
            imgSignal.setVisibility(View.VISIBLE);
           // txtType.setVisibility(View.VISIBLE);// SSA-206 Quick Setting for feature reference.
            if (wifiManager.isWifiEnabled()) {
                imgWifi.setVisibility(View.VISIBLE);
                imgWifi.setImageResource(getWifiIcon(WifiManager.calculateSignalLevel(wifiManager.getConnectionInfo().getRssi(), 5)));
            } else {
                imgWifi.setVisibility(View.GONE);
            }
// SSA-206 Quick Setting for feature reference.
//            if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
//                imgBluetooth.setVisibility(View.VISIBLE);
//                imgBluetooth.setImageResource(R.drawable.ic_bluetooth_on);
//            } else {
//                imgBluetooth.setVisibility(View.GONE);
//            }
//            bindDnd();
        }
        long notifCount = DBUtility.getTableNotificationSmsDao().count() + DBUtility.getCallStorageDao().count();
        imgNotification.setVisibility(notifCount == 0 ? View.GONE : View.VISIBLE);


    }
// SSA-206 Quick Setting for feature reference.
//    private void bindDnd() {
//        int currentModeDeviceMode = audioManager.getRingerMode();
//        if (currentModeDeviceMode == AudioManager.RINGER_MODE_NORMAL) {
//            imgDND.setVisibility(View.GONE);
//        } else if (currentModeDeviceMode == AudioManager.RINGER_MODE_SILENT) {
//            imgDND.setVisibility(View.VISIBLE);
//            imgDND.setImageResource(R.drawable.ic_do_not_disturb_on_black_24dp);
//        } else if (currentModeDeviceMode == AudioManager.RINGER_MODE_VIBRATE) {
//            imgDND.setVisibility(View.VISIBLE);
//            imgDND.setImageResource(R.drawable.ic_vibration_black_24dp);
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
        onTempoEvent(new TempoEvent(launcherPrefs.isTempoActive().get()));
    }


    /**
     * Event bus notifier when new message or call comes.
     *
     * @param tableNotificationSms
     */
    @Subscribe
    public void newNotificationEvent(NewNotificationEvent tableNotificationSms) {
        if (tableNotificationSms != null) {
            imgNotification.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Event bus notifier when notification becomes null.Hide the blue dot
     *
     * @param event
     */
    @Subscribe
    public void notificationSwipeEvent(NotificationSwipeEvent event) {
        try {
            if (event.isNotificationListNull()) {
                imgNotification.setVisibility(View.GONE);
            } else {
                imgNotification.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            airplaneModeDataReceiver.unregister(context);
            batteryDataReceiver.unregister(context);
            networkDataReceiver.unregister(context);
            wifiDataReceiver.unregister(context);
        } catch (Exception e) {
            Log.d(TAG, "onDetach Call");
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
            imgAirplane.setVisibility(NetworkUtil.isAirplaneModeOn(context) ? View.VISIBLE : View.GONE);// SSA-206 Quick Setting for feature reference.
        } else if (event.getState() == ConnectivityEvent.WIFI) {

            if (wifiManager.isWifiEnabled()) {
                //txtType.setText("");// SSA-206 Quick Setting for feature reference.
                //txtType.setVisibility(View.GONE);// SSA-206 Quick Setting for feature reference.
                imgWifi.setVisibility(View.VISIBLE);
                imgWifi.setImageResource(getWifiIcon(event.getValue()));
            } else {
                imgWifi.setVisibility(View.GONE);
            }
            if (event.getValue() == -1) {
                imgWifi.setVisibility(View.GONE);
            }
        } else if (event.getState() == ConnectivityEvent.BATTERY) {
            imgBattery.setImageResource(getBatteryIcon2(event.getValue()));
        } else if (event.getState() == ConnectivityEvent.NETWORK) {
            if (event.getValue() == -1) {
                imgSignal.setImageResource(R.drawable.ic_signal_0);
                //txtType.setText("");// SSA-206 Quick Setting for feature reference.
                //txtType.setVisibility(View.GONE);// SSA-206 Quick Setting for feature reference.
            } else {
                imgSignal.setImageResource(getNetworkIcon(event.getValue()));
                NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
// SSA-206 Quick Setting for feature reference.
//                if (!wifiManager.isWifiEnabled() && networkInfo.isConnected()) {
//                    txtType.setText(event.getType());
//                    txtType.setVisibility(View.VISIBLE);
//                } else {
//                    txtType.setText("");
//                    txtType.setVisibility(View.GONE);
//                }
            }
        } else if (event.getState() == ConnectivityEvent.DND) {
           // bindDnd();// SSA-206 Quick Setting for feature reference.
        } else if (event.getState() == ConnectivityEvent.BLE) {
//            imgBluetooth.setVisibility(event.getValue() == 0 ? View.GONE : View.VISIBLE);// SSA-206 Quick Setting for feature reference.
        }
    }

    private int getBatteryIcon2(int level) {
        int icons[] = {
                IconStyleData.TYPE_VECTOR,
                R.drawable.ic_battery_alert,
                R.drawable.ic_battery_20,
                R.drawable.ic_battery_30,
                R.drawable.ic_battery_50,
                R.drawable.ic_battery_60,
                R.drawable.ic_battery_80,
                R.drawable.ic_battery_90,
                R.drawable.ic_battery_full,
                R.drawable.ic_battery_charging_20,
                R.drawable.ic_battery_charging_30,
                R.drawable.ic_battery_charging_50,
                R.drawable.ic_battery_charging_60,
                R.drawable.ic_battery_charging_80,
                R.drawable.ic_battery_charging_90,
                R.drawable.ic_battery_charging_full
        };

        return icons[level + 1];
    }

    private int getWifiIcon(int level) {
        int icons[] = {
                IconStyleData.TYPE_VECTOR,
                R.drawable.ic_wifi_0,
                R.drawable.ic_wifi_1,
                R.drawable.ic_wifi_2,
                R.drawable.ic_wifi_3,
                R.drawable.ic_wifi_4
        };

        return icons[level + 1];
    }

    private int getNetworkIcon(int level) {
        int icons[] = {
                IconStyleData.TYPE_VECTOR,
                R.drawable.ic_signal_0,
                R.drawable.ic_signal_1,
                R.drawable.ic_signal_2,
                R.drawable.ic_signal_3,
                R.drawable.ic_signal_4
        };

        return icons[level + 1];
    }
}
