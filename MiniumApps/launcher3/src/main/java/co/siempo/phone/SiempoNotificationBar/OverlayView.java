package co.siempo.phone.SiempoNotificationBar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextClock;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.james.status.data.IconStyleData;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;

import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3App;
import co.siempo.phone.app.Constants;
import co.siempo.phone.db.CallStorageDao;
import co.siempo.phone.db.DBUtility;
import co.siempo.phone.db.NotificationSwipeEvent;
import co.siempo.phone.db.TableNotificationSms;
import co.siempo.phone.db.TableNotificationSmsDao;
import co.siempo.phone.event.ConnectivityEvent;
import co.siempo.phone.event.NewNotificationEvent;
import co.siempo.phone.event.NotificationTrayEvent;
import co.siempo.phone.event.OnGoingCallEvent;
import co.siempo.phone.event.TempoEvent;
import co.siempo.phone.event.TopBarUpdateEvent;
import co.siempo.phone.event.TorchOnOff;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.main.SimpleItemTouchHelperCallback;
import co.siempo.phone.network.NetworkUtil;
import co.siempo.phone.notification.ItemClickSupport;
import co.siempo.phone.notification.Notification;
import co.siempo.phone.notification.NotificationContactModel;
import co.siempo.phone.notification.NotificationUtility;
import co.siempo.phone.notification.RecyclerListAdapter;
import co.siempo.phone.notification.remove_notification_strategy.DeleteItem;
import co.siempo.phone.notification.remove_notification_strategy.MultipleIteamDelete;
import co.siempo.phone.notification.remove_notification_strategy.SingleIteamDelete;
import co.siempo.phone.receiver.AirplaneModeDataReceiver;
import co.siempo.phone.receiver.BatteryDataReceiver;
import co.siempo.phone.receiver.IDynamicStatus;
import co.siempo.phone.receiver.NetworkDataReceiver;
import co.siempo.phone.receiver.WifiDataReceiver;
import co.siempo.phone.service.StatusBarService;
import co.siempo.phone.util.PackageUtil;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import minium.co.core.app.CoreApplication;
import minium.co.core.app.HomeWatcher;
import minium.co.core.log.Tracer;
import minium.co.core.util.UIUtils;
import minium.co.core.util.UIUtils;

import static android.graphics.PixelFormat.TRANSLUCENT;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
import static android.view.WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
import static android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;

/**
 * The layout that will be used to display the custom status bar and notification bar
 */
class OverlayView extends FrameLayout implements View.OnClickListener {
    private static final String TRACE_TAG = "";
    private View inflateLayout;
    private Context context;
    private FrameLayout mParentView;
    private WindowManager mWinManager;
    private boolean siempoNotificationBar = false;
    private String TAG = "OverlayView";
    private static final int SAFETY_MARGIN = 20;
    private RelativeLayout topbar;

    /**
     * Status Bar Variables
     */

    private SharedPreferences launcherPrefs;
    private ImageView imgNotification, imgTempo, imgBattery, imgSignal, imgWifi, imgAirplane;
    private TextClock iTxt2;
    private WifiManager wifiManager;
    FontAwesomeIcons[] batteryIcons = {
            FontAwesomeIcons.fa_battery_0,
            FontAwesomeIcons.fa_battery_1,
            FontAwesomeIcons.fa_battery_2,
            FontAwesomeIcons.fa_battery_3,
            FontAwesomeIcons.fa_battery_4
    };

    IDynamicStatus airplaneModeDataReceiver;
    IDynamicStatus batteryDataReceiver;
    IDynamicStatus networkDataReceiver;
    IDynamicStatus wifiDataReceiver;

    /**
     * Notification Bar Variables
     */

    private RecyclerView recyclerView;
    private TextView emptyView, txtClearAll, txtHide, textView_notification_title;
    private SeekBar seekbarBrightness;
    LinearLayout linearClearAll;
    private int brightness;
    private RecyclerListAdapter adapter;
    private List<Notification> notificationList;
    private RelativeLayout layout_notification, relWifi, relMobileData, relBle, relDND, relAirPlane, relFlash, relBrightness;
    private ImageView img_background, img_notification_Wifi, img_notification_Data, img_notification_Ble, img_notification_Dnd, img_notification_Airplane, img_notification_Flash, img_notification_Brightness, imgOnGoingCall;
    private int wifilevel;
    private NotificationManager notificationManager;


    private enum mSwipeDirection {UP, DOWN, NONE}

    private ConnectivityManager connectivityManager;
    private TableNotificationSmsDao smsDao;
    private CallStorageDao callStorageDao;
    private int count = 1;
    private TelephonyManager telephonyManager;
    private BleSignal bleSignal;
    private int currentModeDeviceMode;
    private AudioManager audioManager;
    private boolean isWiFiOn = false;
    AudioChangeReceiver audioChangeReceiver;
    private LinearLayout ln_ongoingCall, container_hangup;
    private TextView txtUserName, txtMessage;
    private Chronometer chronometer;
    private ImageView imgUserOngoingCallImage, img_dot;

    public OverlayView(final Context context) {
        super(context);
        this.context = context;
        siempoNotificationBar = false;
        inflateLayout = inflate(context, R.layout.notification_statusbar, this);
        mWinManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Register Event Bus to get updated status of Battery, WiFi, AirPlane Mode
        EventBus.getDefault().register(this);


        //StatusBar Methods to init components and update UI
        context.startService(new Intent(context, StatusBarService.class));
        initStatusBarComponents();


        //Hide Notification if it is visible by press homepress key or Recent Button Key
        HomeWatcher mHomeWatcher = new HomeWatcher(context);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {

                hide();
                if (PackageUtil.isSiempoLauncher(context)) {

                    try {

                        Dialog dialog = ((Launcher3App) CoreApplication.getInstance()).dialog;
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }

                        if (UIUtils.alertDialog != null && UIUtils.alertDialog.isShowing()) {
                            UIUtils.alertDialog.dismiss();
                        }

                        if (CoreApplication.getInstance().isIfScreen == false) {
                            Intent i = new Intent();
                            String pkg = context.getApplicationContext().getPackageName();
                            String cls = "co.siempo.phone.MainActivity_";
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            i.setComponent(new ComponentName(pkg, cls));

                            context.startActivity(i);
                        }
                    } catch (Exception e) {

                        Tracer.d("Activity Not Found.");
                    }
                }
            }

            @Override
            public void onHomeLongPressed() {
                hide();
            }
        });
        mHomeWatcher.startWatch();

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                1,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        UserPresentBroadcastReceiver userPresentBroadcastReceiver = new UserPresentBroadcastReceiver();
        context.registerReceiver(userPresentBroadcastReceiver, intentFilter);
    }

    /**
     * This BroadcastReceiver is included for the when user press home button and lock the screen.
     * when it comes back we have to show launcher dialog,toottip window.
     */
    public class UserPresentBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                boolean locked = myKM.inKeyguardRestrictedInputMode();
                boolean isHideNotificationOnLockScreen = launcherPrefs.getBoolean("isHidenotificationOnLockScreen", true);
                if (locked && PackageUtil.isSiempoLauncher(context) && isHideNotificationOnLockScreen) {
                    int icon = R.drawable.ic_launch;
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    int notifyID = 96;
                    NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context)
                            .setContentTitle("SiempoApp")
                            .setSortKey("SiempoLockScreeen")
                            .setDefaults(android.app.Notification.DEFAULT_ALL)
                            .setAutoCancel(true)
                            .setSmallIcon(icon);

                    android.app.Notification notification = mNotifyBuilder.build();
                    mNotificationManager.notify(notifyID, notification);

                }

                hide();
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                hide();
                if (CoreApplication.getInstance().getMediaPlayer() != null) {
                    CoreApplication.getInstance().getMediaPlayer().stop();
                    CoreApplication.getInstance().getMediaPlayer().reset();
                    CoreApplication.getInstance().setmMediaPlayer(null);
                    CoreApplication.getInstance().getVibrator().cancel();
                    CoreApplication.getInstance().declinePhone();
                }
                if (CoreApplication.getInstance().isCallisRunning()) {
                    CoreApplication.getInstance().declinePhone();
                }
            }
        }

    }


    /**
     * Status Bar init components
     */
    private void initStatusBarComponents() {

        //Initialization of components
        launcherPrefs = context.getSharedPreferences("Launcher3Prefs", 0);
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        imgNotification = inflateLayout.findViewById(R.id.imgNotification);
        imgTempo = inflateLayout.findViewById(R.id.imgTempo);
        imgBattery = inflateLayout.findViewById(R.id.imgBattery);
        imgSignal = inflateLayout.findViewById(R.id.imgSignal);
        imgWifi = inflateLayout.findViewById(R.id.imgWifi);
        imgAirplane = inflateLayout.findViewById(R.id.imgAirplane);
        iTxt2 = inflateLayout.findViewById(R.id.iTxt2);
        topbar = inflateLayout.findViewById(R.id.topbar);
        layout_notification = inflateLayout.findViewById(R.id.layout_notification);


        imgOnGoingCall = (ImageView) inflateLayout.findViewById(R.id.imgOnGoingCall);
        ln_ongoingCall = (LinearLayout) inflateLayout.findViewById(R.id.ln_ongoingCall);
        chronometer = (Chronometer) inflateLayout.findViewById(R.id.chronometer);
        txtUserName = (TextView) inflateLayout.findViewById(R.id.txtUserName);
        txtMessage = (TextView) inflateLayout.findViewById(R.id.txtMessage);
        imgUserOngoingCallImage = (ImageView) inflateLayout.findViewById(R.id.imgUserOngoingCallImage);
        container_hangup = (LinearLayout) inflateLayout.findViewById(R.id.container_hangup);
        img_dot = (ImageView) inflateLayout.findViewById(R.id.img_dot);
        //Register Airplane Mode, Wifi Receiver, Battery Receiver, Network Receiver
        airplaneModeDataReceiver = new AirplaneModeDataReceiver();
        airplaneModeDataReceiver.register(context);
        wifiDataReceiver = new WifiDataReceiver();
        wifiDataReceiver.register(context);
        batteryDataReceiver = new BatteryDataReceiver();
        batteryDataReceiver.register(context);
        networkDataReceiver = new NetworkDataReceiver(context);
        networkDataReceiver.register(context);
        audioChangeReceiver = new AudioChangeReceiver();
        context.registerReceiver(audioChangeReceiver, new IntentFilter(
                AudioManager.RINGER_MODE_CHANGED_ACTION));

        updateStatusBarUI();

        //Initialization components of Siempo NotificationBar//
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        seekbarBrightness = inflateLayout.findViewById(R.id.seekbarBrightness);
        recyclerView = inflateLayout.findViewById(R.id.recyclerView);
        txtClearAll = inflateLayout.findViewById(R.id.txtClearAll);
        txtHide = inflateLayout.findViewById(R.id.txtHide);
        emptyView = inflateLayout.findViewById(R.id.emptyView);
        linearClearAll = inflateLayout.findViewById(R.id.linearClearAll);
        img_background = inflateLayout.findViewById(R.id.img_background);
        textView_notification_title = inflateLayout.findViewById(R.id.textView_notification_title);
        relWifi = inflateLayout.findViewById(R.id.relNotificationWifi);
        relBle = inflateLayout.findViewById(R.id.relNotificationBle);
        relDND = inflateLayout.findViewById(R.id.relNotificationDND);
        relAirPlane = inflateLayout.findViewById(R.id.relNotificationAirPlane);
        relBrightness = inflateLayout.findViewById(R.id.relNotificationBrightness);
        relFlash = inflateLayout.findViewById(R.id.relNotificationFlash);
        relMobileData = inflateLayout.findViewById(R.id.relNotificationMobileData);
        img_notification_Wifi = inflateLayout.findViewById(R.id.imgNotificationWifi);
        img_notification_Data = inflateLayout.findViewById(R.id.imgNotificationData);
        img_notification_Ble = inflateLayout.findViewById(R.id.imgNotificationBle);
        img_notification_Dnd = inflateLayout.findViewById(R.id.imgNotificationDnd);
        img_notification_Airplane = inflateLayout.findViewById(R.id.imgNotificationAirplane);
        img_notification_Flash = inflateLayout.findViewById(R.id.imgNotificationFlash);
        img_notification_Brightness = inflateLayout.findViewById(R.id.imgNotificationBrightness);
        relMobileData.setOnClickListener(this);
        relDND.setOnClickListener(this);
        relAirPlane.setOnClickListener(this);
        relFlash.setOnClickListener(this);
        relBrightness.setOnClickListener(this);
        layout_notification.setFocusable(true);
        layout_notification.setClickable(true);
        layout_notification.setVisibility(GONE);

        layout_notification.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    hide();
                }
                if (event.getKeyCode() == KeyEvent.KEYCODE_APP_SWITCH) {
                    hide();
                }
                return false;
            }
        });

        topbar.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    addSiempoNotificationBar(motionEvent);
                }
                return false;
            }
        });

        notificationList = new ArrayList<>();
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new RecyclerListAdapter(context, notificationList);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter, context);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        smsDao = DBUtility.getNotificationDao();
        callStorageDao = DBUtility.getCallStorageDao();
        loadData();
        bindBrightnessControl();
        bleSignal = new BleSignal();
        context.registerReceiver(bleSignal, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        if (UIUtils.isDeviceHasSimCard(context)) {
            imgSignal.setVisibility(View.VISIBLE);
        } else {
            imgSignal.setImageResource(R.drawable.ic_no_sim_black_24dp);
        }
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void ReloadNotificationsEvent(OnGoingCallEvent event) {
        smsDao = DBUtility.getNotificationDao();
        callStorageDao = DBUtility.getCallStorageDao();
        loadData();
    }


    private class AudioChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            bindDND();
        }
    }

    @Subscribe
    public void object(Object object) {

    }

    @Subscribe
    public void updateTopBar(TopBarUpdateEvent event) {
        updateStatusBarUI();
    }


    /**
     * Below function is use to show and hide ongoing Call notification
     */
    @Subscribe
    public void OnGoingCallEvent(OnGoingCallEvent event) {
        long notificationCount = DBUtility.getTableNotificationSmsDao().count() + DBUtility.getCallStorageDao().count();
        OnGoingCallData callData = event.getCallData();
        if (imgOnGoingCall != null && callData.get_isCallRunning()) {
            launcherPrefs.edit().putBoolean("onGoingCall", true).commit();
            imgOnGoingCall.setVisibility(View.VISIBLE);
            if (callData.getId() != 0) {
                img_dot.setVisibility(View.VISIBLE);
                chronometer.setVisibility(View.VISIBLE);
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
            } else {
                img_dot.setVisibility(View.GONE);
                chronometer.setVisibility(View.GONE);
            }
            txtMessage.setText(callData.get_message());
            if (!TextUtils.isEmpty(callData.get_contact_title())) {
                NotificationContactModel contactDetails = gettingNameAndImageFromPhoneNumber(callData.get_contact_title());
                txtUserName.setText(contactDetails.getName());
                if (contactDetails.getImage() != null && !contactDetails.getImage().equals("")) {
                    Glide.with(context)
                            .load(Uri.parse(contactDetails.getImage()))
                            .placeholder(R.drawable.ic_person_black_24dp)
                            .into(imgUserOngoingCallImage);
                }
            }

            ln_ongoingCall.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

            ln_ongoingCall.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent();
                    myIntent.setAction(Intent.ACTION_CALL_BUTTON);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(myIntent);
                    hide();
                }
            });
            container_hangup.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        declinePhone(context);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        if (imgOnGoingCall != null && !callData.get_isCallRunning()) {
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.stop();
            launcherPrefs.edit().putBoolean("onGoingCall", false).commit();
            ln_ongoingCall.setVisibility(View.GONE);
            imgOnGoingCall.setVisibility(View.GONE);
            if (notificationCount == 0) {
                emptyView.setVisibility(View.VISIBLE);
            }
        }
        if (imgNotification != null) {
            imgNotification.setVisibility(notificationCount == 0 ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Status Bar update UI
     */
    private void updateStatusBarUI() {
        if (NetworkUtil.isAirplaneModeOn(context)) {
            imgSignal.setVisibility(View.GONE);
            imgWifi.setVisibility(View.GONE);
            imgAirplane.setVisibility(View.VISIBLE);
        } else {
            imgAirplane.setVisibility(View.GONE);
            imgSignal.setVisibility(View.VISIBLE);
            if (wifiManager.isWifiEnabled()) {
                imgWifi.setVisibility(View.VISIBLE);
            } else {
                imgWifi.setVisibility(View.GONE);
            }
        }
        long notificationCount = DBUtility.getTableNotificationSmsDao().count() + DBUtility.getCallStorageDao().count();
        imgNotification.setVisibility(notificationCount == 0 ? View.GONE : View.VISIBLE);
        onTempoEvent(new TempoEvent(launcherPrefs.getBoolean("isTempoActive", false)));
    }


    @Subscribe
    public void onTempoEvent(TempoEvent event) {
        if (event.isStarting()) {
            imgTempo.setVisibility(View.VISIBLE);
            launcherPrefs.edit().putBoolean("isTempoActive", true).apply();
        } else {
            imgTempo.setVisibility(View.GONE);
            launcherPrefs.edit().putBoolean("isTempoActive", false).apply();
        }
    }


    @Subscribe
    public void notificationHideEvent(NotificationTrayEvent event) {
        if (event.isVisible())
            hide();
    }

    /**
     * StatusBar : Connectivity Change Listener
     */
    @Subscribe
    public void onConnectivityEvent(ConnectivityEvent event) {
        if (event.getState() == ConnectivityEvent.AIRPLANE) {
            if (imgSignal != null)
                imgSignal.setVisibility(NetworkUtil.isAirplaneModeOn(context) ? View.GONE : View.VISIBLE);
            if (imgAirplane != null)
                imgAirplane.setVisibility(NetworkUtil.isAirplaneModeOn(context) ? View.VISIBLE : View.GONE);
        } else if (event.getState() == ConnectivityEvent.WIFI) {
            if (event.getValue() == 0) {
                if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING
                        || wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
                    if (imgWifi != null) imgWifi.setVisibility(View.GONE);
                    if (img_notification_Wifi != null)
                        img_notification_Wifi.setBackground(context.getDrawable(R.drawable.ic_signal_wifi_off_black_24dp));
                }
            } else if (event.getValue() == -1) {
                if (imgWifi != null) imgWifi.setVisibility(View.GONE);
                if (img_notification_Wifi != null)
                    img_notification_Wifi.setBackground(context.getDrawable(R.drawable.ic_signal_wifi_off_black_24dp));
            } else {
                int level = event.getValue();
                wifilevel = level;
                bindWiFiImage(level);
            }
        } else if (event.getState() == ConnectivityEvent.BATTERY) {
            displayBatteryIcon(event.getValue(), event.getType());
        } else if (event.getState() == ConnectivityEvent.NETWORK) {
            //Update status bar network icon
            if (imgSignal != null) imgSignal.setImageResource(getNetworkIcon(event.getValue()));
            if (!NetworkUtil.isAirplaneModeOn(context)) {
                if (relMobileData != null) {
                    relMobileData.setEnabled(true);
                    checkMobileData();
                }
            }
            if (event.getValue() == -1) {
                imgSignal.setImageResource(R.drawable.ic_no_sim_black_24dp);
            }
        }
    }

    private void bindWiFiImage(int level) {
        if (img_notification_Wifi != null)
            img_notification_Wifi.setVisibility(View.VISIBLE);
        if (imgWifi != null) imgWifi.setVisibility(View.VISIBLE);
        if (level == 0) {
            if (img_notification_Wifi != null) {
                img_notification_Wifi.setBackground(context.getDrawable(R.drawable.ic_wifi_0));
            }
            if (imgWifi != null) {
                imgWifi.setImageResource(R.drawable.ic_wifi_0);
            }
        } else if (level == 1) {
            if (img_notification_Wifi != null) {
                img_notification_Wifi.setBackground(context.getDrawable(R.drawable.ic_wifi_1));
            }
            if (imgWifi != null) {
                imgWifi.setImageResource(R.drawable.ic_wifi_1);
            }
        } else if (level == 2) {
            if (img_notification_Wifi != null) {
                img_notification_Wifi.setBackground(context.getDrawable(R.drawable.ic_wifi_2));
            }
            if (imgWifi != null) {
                imgWifi.setImageResource(R.drawable.ic_wifi_2);
            }
        } else if (level == 3) {
            if (img_notification_Wifi != null) {
                img_notification_Wifi.setBackground(context.getDrawable(R.drawable.ic_wifi_3));
            }
            if (imgWifi != null) {
                imgWifi.setImageResource(R.drawable.ic_wifi_3);
            }
        } else if (level == 4) {
            if (img_notification_Wifi != null) {
                img_notification_Wifi.setBackground(context.getDrawable(R.drawable.ic_wifi_4));
            }
            if (imgWifi != null) {
                imgWifi.setImageResource(R.drawable.ic_wifi_4);
            }
        }
    }

    /**
     * This method is called from adapter when there is no notification reamining in list.
     *
     * @param event
     */
    @Subscribe
    public void notificationSwipeEvent(NotificationSwipeEvent event) {
        try {
            if (event.isNotificationListNull()) {
                notificationList.clear();
                if (recyclerView != null) recyclerView.setVisibility(View.GONE);
                if (!launcherPrefs.getBoolean("onGoingCall", false)) {
                    if (emptyView != null) emptyView.setVisibility(View.VISIBLE);
                }
                if (linearClearAll != null) linearClearAll.setVisibility(View.GONE);
                if (textView_notification_title != null)
                    textView_notification_title.setVisibility(View.GONE);
                if (imgNotification != null) imgNotification.setVisibility(View.GONE);
            } else {
                if (recyclerView != null) recyclerView.setVisibility(View.VISIBLE);
                if (emptyView != null) emptyView.setVisibility(View.GONE);
                if (linearClearAll != null) linearClearAll.setVisibility(View.VISIBLE);
                if (textView_notification_title != null)
                    textView_notification_title.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                com.james.status.R.drawable.ic_signal_0,
                com.james.status.R.drawable.ic_signal_1,
                com.james.status.R.drawable.ic_signal_2,
                com.james.status.R.drawable.ic_signal_3,
                com.james.status.R.drawable.ic_signal_4
        };

        return icons[level + 1];
    }

    static WindowManager.LayoutParams createLayoutParams(int height) {
        final WindowManager.LayoutParams params =
                new WindowManager.LayoutParams(MATCH_PARENT, WRAP_CONTENT, TYPE_SYSTEM_ERROR, FLAG_NOT_FOCUSABLE
                        | FLAG_LAYOUT_IN_SCREEN
                        | FLAG_LAYOUT_NO_LIMITS
                        | FLAG_NOT_TOUCH_MODAL
                        | FLAG_LAYOUT_INSET_DECOR
                        , TRANSLUCENT);
        params.gravity = Gravity.TOP;
        return params;
    }

    /**
     * Below logic will use in further development
     *
     * @return
     */
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        addSiempoNotificationBar(event);
//        return super.onTouchEvent(event);
//    }
    public int retrieveStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
    }

    /**
     * Hides view
     */
    public synchronized void hide() {
        if (siempoNotificationBar) {
            siempoNotificationBar = false;
            layout_notification.setVisibility(View.GONE);
            if (mWinManager != null && mParentView != null && mParentView.getParent() != null) {
                mWinManager.removeView(mParentView);
            }
            WindowManager.LayoutParams params =
                    new WindowManager.LayoutParams(MATCH_PARENT, WRAP_CONTENT, TYPE_SYSTEM_ERROR, FLAG_NOT_FOCUSABLE
                            | FLAG_LAYOUT_IN_SCREEN
                            | FLAG_LAYOUT_NO_LIMITS
                            | FLAG_NOT_TOUCH_MODAL
                            | FLAG_WATCH_OUTSIDE_TOUCH
                            , TRANSLUCENT);
            params.gravity = Gravity.TOP;
            mWinManager.updateViewLayout(this, params);
        }
    }


    /**
     * Load and Display Siempo Notification bar when swipe down
     */
    public synchronized void addSiempoNotificationBar(MotionEvent event) {
        if (!siempoNotificationBar) {
            int navigationBarHeight = 0;

            WindowManager.LayoutParams params = new WindowManager.LayoutParams(MATCH_PARENT, MATCH_PARENT, TYPE_SYSTEM_ERROR, FLAG_NOT_FOCUSABLE
                    | FLAG_LAYOUT_IN_SCREEN
                    | FLAG_LAYOUT_NO_LIMITS
                    | FLAG_NOT_TOUCH_MODAL
                    | FLAG_WATCH_OUTSIDE_TOUCH
                    , TRANSLUCENT);
            params.gravity = Gravity.TOP;
            mWinManager.updateViewLayout(this, params);

            siempoNotificationBar = true;
            img_notification_Brightness.setBackground(context.getDrawable(R.drawable.ic_brightness_off_black_24dp));
            seekbarBrightness.setVisibility(View.GONE);
            WindowManager.LayoutParams params1 = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    1,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);
            mParentView = new FrameLayout(context) {
                @Override
                public boolean dispatchKeyEvent(KeyEvent event) {
                    Log.d("getKeyCode", "" + event.getKeyCode());
                    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                        hide();
                    }
                    if (event.getKeyCode() == KeyEvent.KEYCODE_APP_SWITCH) {
                        hide();
                    }
                    if (event.getKeyCode() == KeyEvent.KEYCODE_POWER) {
                        hide();
                    }
                    return super.dispatchKeyEvent(event);
                }
            };

            mWinManager.addView(mParentView, params1);
            final Animation in = AnimationUtils.loadAnimation(context, R.anim.slide_down);
            layout_notification.setVisibility(VISIBLE);
            layout_notification.startAnimation(in);
            layout_notification.startAnimation(in);
            layout_notification.setFocusable(true);
            layout_notification.setClickable(true);
            updateNotificationComponents();

        }
    }


    public void updateNotificationComponents() {

        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
        txtClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteItem deleteItem = new DeleteItem(new MultipleIteamDelete());
                deleteItem.deleteAll();
                notificationList.clear();
                adapter.notifyDataSetChanged();
                EventBus.getDefault().post(new NotificationSwipeEvent(true));
            }
        });
        txtHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
        img_background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_background.setClickable(false);
                hide();
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (isLastItemDisplaying(recyclerView)) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            count++;
                        }
                    }, 500);
                }

            }
        });

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {

            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                if (notificationList.get(position).getNotificationType() == NotificationUtility.NOTIFICATION_TYPE_SMS) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", notificationList.get(position).getNumber(), null));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                    hide();
                    // Following code will delete all notification of same user and same types.
                    DeleteItem deleteItem = new DeleteItem(new MultipleIteamDelete());
                    deleteItem.executeDelete(notificationList.get(position));
                    loadData();
                } else if (notificationList.get(position).getNotificationType() == NotificationUtility.NOTIFICATION_TYPE_CALL) {
                    if (
                            ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED
                                    && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {

                    } else {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + notificationList.get(position).getNumber()));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        hide();
                    }
                    // Following code will delete all notification of same user and same types.
                    DeleteItem deleteItem = new DeleteItem(new MultipleIteamDelete());
                    deleteItem.executeDelete(notificationList.get(position));
                    loadData();
                } else {
                    String strPackageName = notificationList.get(position).getPackageName();
                    String strTitle = notificationList.get(position).getStrTitle();
                    List<TableNotificationSms> tableNotificationSms = DBUtility.getNotificationDao().queryBuilder()
                            .where(TableNotificationSmsDao.Properties.PackageName.eq(notificationList.get(position).getPackageName())).list();
                    DBUtility.getNotificationDao().deleteInTx(tableNotificationSms);
                    adapter.notifyItemRemoved(position);
                    notificationList.remove(position);
                    hide();
                    if (DBUtility.getTableNotificationSmsDao().count() >= 1) {
                        imgNotification.setVisibility(View.VISIBLE);
                    } else {
                        imgNotification.setVisibility(View.GONE);
                    }
                    if (strPackageName.equalsIgnoreCase(Constants.WHATSAPP_PACKAGE)) {
                        if (getPhoneNumber(strTitle, context).equalsIgnoreCase("")) {
                            new ActivityHelper(context).openAppWithPackageName(strPackageName);
                        } else {
                            Uri uri = Uri.parse("smsto:" + strTitle);
                            Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                            i.putExtra("sms_body", "");
                            i.setPackage("com.whatsapp");
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);
                        }
                    } else {
                        new ActivityHelper(context).openAppWithPackageName(strPackageName);
                    }
                }
            }


        });

        smsDao = DBUtility.getNotificationDao();
        callStorageDao = DBUtility.getCallStorageDao();
        loadData();
        bindBrightnessControl();
        bleSignal = new BleSignal();
        context.registerReceiver(bleSignal, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        statusOfQuickSettings();
        relWifi.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gdWifi.onTouchEvent(motionEvent);
                return true;
            }
        });
        relBle.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gdBle.onTouchEvent(motionEvent);
                return true;
            }
        });
    }

    public String getPhoneNumber(String name, Context context) {
        String ret = null;
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like'%" + name + "%'";
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, selection, null, null);
        if (c.moveToFirst()) {
            ret = c.getString(0);
        }
        c.close();
        if (ret == null)
            ret = "";
        return ret;
    }

    /**
     * Touch listener for the wifi to detect double tap and single tap
     */
    final GestureDetector gdWifi = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            hide();
            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            seekbarBrightness.setVisibility(View.GONE);
            img_notification_Brightness.setBackground(context.getDrawable(R.drawable.ic_brightness_off_black_24dp));
            turnOnOffWIFI();
            return super.onSingleTapConfirmed(e);
        }

    });

    /**
     * Touch listener for the BLE to detect double tap and single tap
     */
    final GestureDetector gdBle = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            hide();
            Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        }


        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            seekbarBrightness.setVisibility(View.GONE);
            img_notification_Brightness.setBackground(context.getDrawable(R.drawable.ic_brightness_off_black_24dp));
            if (BluetoothAdapter.getDefaultAdapter() != null && BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                BluetoothAdapter.getDefaultAdapter().disable();
                EventBus.getDefault().post(new ConnectivityEvent(ConnectivityEvent.BLE, 0));
            } else {
                if (BluetoothAdapter.getDefaultAdapter() != null) {
                    BluetoothAdapter.getDefaultAdapter().enable();
                }
                relBle.setEnabled(false);
                img_notification_Ble.setBackground(context.getDrawable(R.drawable.ic_bluetooth_searching_black_24dp));
                EventBus.getDefault().post(new ConnectivityEvent(ConnectivityEvent.BLE, 1));
            }
            return super.onSingleTapConfirmed(e);
        }

    });

    private void bindBrightnessControl() {
        img_notification_Brightness.setBackground(context.getDrawable(R.drawable.ic_brightness_off_black_24dp));
        try {
            //Get the current system brightness
            brightness = Settings.System.getInt(
                    context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS,
                    0);
        } catch (Exception e) {
            //Throw an error case it couldn't be retrieved
            e.printStackTrace();
        }

        seekbarBrightness.setMax(255);
        //Set the progress of the seek bar based on the system's brightness
        seekbarBrightness.setProgress(brightness);

        //Register OnSeekBarChangeListener, so it can actually change values
        seekbarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                try {
                    if (i >= 0 && i <= 255) {
                        screenBrightness(i, context);
                    }
                    //Get the current system brightness
                    brightness = Settings.System.getInt(
                            context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
                } catch (Exception e) {
                    //Throw an error case it couldn't be retrieved
                    e.printStackTrace();
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    boolean screenBrightness(int level, Context context) {

        try {
            android.provider.Settings.System.putInt(
                    context.getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS, level);
            android.provider.Settings.System.putInt(context.getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE,
                    android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            android.provider.Settings.System.putInt(
                    context.getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS,
                    level);
            return true;
        } catch (Exception e) {
            Log.e("Screen Brightness", "error changing screen brightness");
            return false;
        }
    }


    /**
     * This method used for load the current status of Wifi/BF/DND/Airplane/FlashLight.
     */
    private void statusOfQuickSettings() {
        if (NetworkUtil.isAirplaneModeOn(context)) {
            relMobileData.setEnabled(false);
            img_notification_Airplane.setBackground(context.getDrawable(R.drawable.ic_airplane));
            imgAirplane.setVisibility(View.VISIBLE);
            wifiManager.setWifiEnabled(false);
            img_notification_Wifi.setBackground(context.getDrawable(R.drawable.ic_signal_wifi_off_black_24dp));
            if (BluetoothAdapter.getDefaultAdapter() != null) {
                BluetoothAdapter.getDefaultAdapter().disable();
            }
            img_notification_Ble.setBackground(context.getDrawable(R.drawable.ic_bluetooth_disabled_black_24dp));
            img_notification_Data.setBackground(context.getDrawable(R.drawable.ic_data_on_black_24dp));
        } else {
            relMobileData.setEnabled(true);
            checkMobileData();
            img_notification_Airplane.setBackground(context.getDrawable(R.drawable.ic_airplanemode_inactive_black_24dp));
            imgAirplane.setVisibility(View.GONE);
            if (isWiFiOn) {
                wifiManager.setWifiEnabled(true);
            } else {
                isWiFiOn = false;
            }
            if (!wifiManager.isWifiEnabled() || NetworkUtil.isAirplaneModeOn(context)) {
                img_notification_Wifi.setBackground(context.getDrawable(R.drawable.ic_signal_wifi_off_black_24dp));
            } else {
                img_notification_Wifi.setBackground(context.getDrawable(R.drawable.ic_wifi_0));
                bindWiFiImage(wifilevel);
            }
            if (BluetoothAdapter.getDefaultAdapter() != null && BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                img_notification_Ble.setBackground(context.getDrawable(R.drawable.ic_bluetooth_on));
            } else {
                img_notification_Ble.setBackground(context.getDrawable(R.drawable.ic_bluetooth_disabled_black_24dp));
            }
        }

        bindDND();
        bindFlash();
        if (telephonyManager.getNetworkOperator().equalsIgnoreCase("")) {
            img_notification_Data.setBackground(context.getDrawable(R.drawable.ic_data_on_black_24dp));
            relMobileData.setEnabled(false);
        }

    }

    private void bindDND() {
        currentModeDeviceMode = launcherPrefs.getInt("getCurrentProfile", 0);
        if (img_notification_Dnd != null) {
            if (currentModeDeviceMode == 0) {
                img_notification_Dnd.setBackground(context.getDrawable(R.drawable.ic_do_not_disturb_off_black_24dp));
            } else if (currentModeDeviceMode == 1) {
                img_notification_Dnd.setBackground(context.getDrawable(R.drawable.ic_vibration_black_24dp));
            } else if (currentModeDeviceMode == 2) {
                img_notification_Dnd.setBackground(context.getDrawable(R.drawable.ic_do_not_disturb_on_black_24dp));
            }
        }
    }

    private void bindFlash() {
        if (context.getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            relFlash.setVisibility(View.VISIBLE);

            if (StatusBarService.isFlashOn) {
                img_notification_Flash.setBackground(context.getDrawable(R.drawable.ic_flash_on_black_24dp));
            } else {
                img_notification_Flash.setBackground(context.getDrawable(R.drawable.ic_flash_off_black_24dp));
            }

        } else {
            relFlash.setVisibility(View.GONE);
        }
    }


    private void loadData() {
        List<TableNotificationSms> SMSItems = smsDao.queryBuilder().orderDesc(TableNotificationSmsDao.Properties._date).build().list();
        setUpNotifications(SMSItems);
        EventBus.getDefault().post(new TopBarUpdateEvent());
    }


    /**
     * Event bus notifier when new message or call comes.
     *
     * @param tableNotificationSms
     */
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void newNotificationEvent(NewNotificationEvent tableNotificationSms) {
        System.out.println("NotificationFragment.newNotificationEvent" + tableNotificationSms.toString());
        if (tableNotificationSms != null) {
            if (imgNotification != null) imgNotification.setVisibility(View.VISIBLE);
            if (!checkNotificationExistsOrNot(tableNotificationSms.getTopTableNotificationSmsDao().getId())) {
                @SuppressLint("SimpleDateFormat")
                DateFormat sdf = new SimpleDateFormat("hh:mm a");
                String time = sdf.format(tableNotificationSms.getTopTableNotificationSmsDao().get_date());
                if (tableNotificationSms.getTopTableNotificationSmsDao().getNotification_type() == NotificationUtility.NOTIFICATION_TYPE_EVENT) {
                    Notification notification = new Notification();
                    notification.setId(tableNotificationSms.getTopTableNotificationSmsDao().getId());
                    notification.setNotitification_date(tableNotificationSms.getTopTableNotificationSmsDao().getNotification_date());
                    notification.setNotificationType(tableNotificationSms.getTopTableNotificationSmsDao().getNotification_type());
                    notification.setApp_icon(tableNotificationSms.getTopTableNotificationSmsDao().getApp_icon());
                    notification.setUser_icon(tableNotificationSms.getTopTableNotificationSmsDao().getUser_icon());
                    notification.setPackageName(tableNotificationSms.getTopTableNotificationSmsDao().getPackageName());
                    notification.set_time(time);
                    notification.setStrTitle(tableNotificationSms.getTopTableNotificationSmsDao().get_contact_title());
                    notification.set_text(tableNotificationSms.getTopTableNotificationSmsDao().get_message());
                    notificationList.add(0, notification);
                } else {
                    Notification n = new Notification(gettingNameAndImageFromPhoneNumber(tableNotificationSms.getTopTableNotificationSmsDao().get_contact_title()),
                            tableNotificationSms.getTopTableNotificationSmsDao().getId(), tableNotificationSms.getTopTableNotificationSmsDao().get_contact_title(),
                            tableNotificationSms.getTopTableNotificationSmsDao().get_message(), time, false, tableNotificationSms.getTopTableNotificationSmsDao().getNotification_type());
                    notificationList.add(0, n);
                }
                sortDate(notificationList);
                adapter.notifyDataSetChanged();
                if (notificationList.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    if (!launcherPrefs.getBoolean("onGoingCall", false)) {
                        emptyView.setVisibility(View.VISIBLE);
                    }
                    if (linearClearAll != null) linearClearAll.setVisibility(View.GONE);
                    textView_notification_title.setVisibility(View.GONE);
                    imgNotification.setVisibility(View.GONE);
                } else {
                    adapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                    if (linearClearAll != null) linearClearAll.setVisibility(View.VISIBLE);
                    textView_notification_title.setVisibility(View.VISIBLE);
                    imgNotification.setVisibility(View.VISIBLE);
                }
            } else {
                updateNotification(tableNotificationSms);
            }
        }
    }


    private void updateNotification(NewNotificationEvent notificationEvent) {
        if (notificationList != null) {
            int pos = -1;
            for (int i = 0; i < notificationList.size(); i++) {
                if (notificationList.get(i).getId() == notificationEvent.getTopTableNotificationSmsDao().getId()) {
                    pos = i;
                    break;
                }

            }
            if (pos != -1) {
                DateFormat sdf = new SimpleDateFormat("hh:mm a");
                String time = sdf.format(notificationEvent.getTopTableNotificationSmsDao().get_date());
                Notification notification = notificationList.get(pos);
                notification.setNotitification_date(notificationEvent.getTopTableNotificationSmsDao().getNotification_date());
                notification.setId(notificationEvent.getTopTableNotificationSmsDao().getId());
                notification.setNotificationType(notificationEvent.getTopTableNotificationSmsDao().getNotification_type());
                notification.setApp_icon(notificationEvent.getTopTableNotificationSmsDao().getApp_icon());
                notification.setUser_icon(notificationEvent.getTopTableNotificationSmsDao().getUser_icon());
                notification.set_time(time);
                notification.setPackageName(notificationEvent.getTopTableNotificationSmsDao().getPackageName());
                notification.setStrTitle(notificationEvent.getTopTableNotificationSmsDao().get_contact_title());
                notification.set_text(notificationEvent.getTopTableNotificationSmsDao().get_message());
                notificationList.set(pos, notification);
                sortDate(notificationList);
                adapter.notifyDataSetChanged();
            }
        }


    }

    private void sortDate(List<Notification> list) {
        Collections.sort(list, new Comparator<Notification>() {
            public int compare(Notification o1, Notification o2) {
                return new Date(o2.getNotitification_date()).compareTo(new Date(o1.getNotitification_date()));
            }
        });
    }

    /**
     * Check to see whether the new notification data already exist in list or not.
     *
     * @param id
     * @return
     */
    private boolean checkNotificationExistsOrNot(Long id) {
        if (notificationList != null) {
            for (Notification notification : notificationList) {
                if (notification.getId() == id) {
                    return true;
                }
            }
        }
        return false;
    }


    private void setUpNotifications(List<TableNotificationSms> items) {
        notificationList.clear();
        for (int i = 0; i < items.size(); i++) {
            @SuppressLint("SimpleDateFormat") DateFormat sdf = new SimpleDateFormat("hh:mm a");
            String time = sdf.format(items.get(i).get_date());
            if (items.get(i).getNotification_type() == NotificationUtility.NOTIFICATION_TYPE_EVENT) {
                Notification notification = new Notification();
                notification.setId(items.get(i).getId());
                notification.setNotitification_date(items.get(i).getNotification_date());
                notification.setNotificationType(items.get(i).getNotification_type());
                notification.setApp_icon(items.get(i).getApp_icon());
                notification.setUser_icon(items.get(i).getUser_icon());
                notification.setPackageName(items.get(i).getPackageName());
                notification.set_time(time);
                notification.setStrTitle(items.get(i).get_contact_title());
                notification.set_text(items.get(i).get_message());
                notificationList.add(notification);
            } else {
                Notification n = new Notification(gettingNameAndImageFromPhoneNumber(items.get(i).get_contact_title()), items.get(i).getId(), items.get(i).get_contact_title(), items.get(i).get_message(), time, false, items.get(i).getNotification_type());
                notificationList.add(n);
            }
        }

        if (items.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            if (!launcherPrefs.getBoolean("onGoingCall", false)) {
                emptyView.setVisibility(View.VISIBLE);
            }
            textView_notification_title.setVisibility(View.GONE);
            if (linearClearAll != null) linearClearAll.setVisibility(View.GONE);
            imgNotification.setVisibility(View.GONE);
        } else {
            adapter.notifyDataSetChanged();
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            if (linearClearAll != null) linearClearAll.setVisibility(View.VISIBLE);
            textView_notification_title.setVisibility(View.VISIBLE);
            imgNotification.setVisibility(View.VISIBLE);
        }


    }

    private NotificationContactModel gettingNameAndImageFromPhoneNumber(String number) {

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor cursor = context.getContentResolver().query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI}, null, null, null);

        String contactName, imageUrl = "";
        try {
            if (cursor != null && cursor.moveToFirst()) {
                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                imageUrl = cursor
                        .getString(cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                cursor.close();

            } else {
                contactName = number;
            }
        } catch (Exception e) {
            contactName = "";
            imageUrl = "";
            e.printStackTrace();
        }


        NotificationContactModel notificationContactModel = new NotificationContactModel();
        notificationContactModel.setName(contactName);
        notificationContactModel.setImage(imageUrl);

        return notificationContactModel;
    }

    final GestureDetector gesture = new GestureDetector(context,
            new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                       float velocityY) {
                    final int SWIPE_MIN_DISTANCE = 30;
                    final int SWIPE_MAX_OFF_PATH = 250;
                    final int SWIPE_THRESHOLD_VELOCITY = 200;

                    try {
                        if (e1 != null && e2 != null) {
                            if (Math.abs(e1.getX() - e2.getX()) > SWIPE_MAX_OFF_PATH)
                                return false;
                            if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
                                    && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                                hide();
                            }
                        }
                    } catch (Exception e) {
                        // nothing
                        e.printStackTrace();
                    }
                    return super.onFling(e1, e2, velocityX, velocityY);
                }
            });

    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView != null && recyclerView.getAdapter() != null) {
            if (recyclerView.getAdapter().getItemCount() != 0) {
                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                    return true;
            }
            count = 1;
        }
        return false;
    }


    private void checkMobileData() {
        if (NetworkUtil.getConnectivityStatus(context) == NetworkUtil.TYPE_MOBILE) {
            img_notification_Data.setBackground(context.getDrawable(R.drawable.ic_data_off_black_24dp));
        } else if (NetworkUtil.getConnectivityStatus(context) == NetworkUtil.TYPE_WIFI) {
            img_notification_Data.setBackground(context.getDrawable(R.drawable.ic_data_on_black_24dp));
        } else if (NetworkUtil.getConnectivityStatus(context) == NetworkUtil.TYPE_NOT_CONNECTED) {
            img_notification_Data.setBackground(context.getDrawable(R.drawable.ic_data_on_black_24dp));
        }
    }


    /**
     * Broadcast Receiver for the Bluetooth single.
     */
    class BleSignal extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (img_notification_Ble != null) {
                    switch (state) {
                        case BluetoothAdapter.STATE_OFF:
                            relBle.setEnabled(true);
                            EventBus.getDefault().post(new ConnectivityEvent(ConnectivityEvent.BLE, 0));
                            img_notification_Ble.setBackground(context.getDrawable(R.drawable.ic_bluetooth_disabled_black_24dp));
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            img_notification_Ble.setBackground(context.getDrawable(R.drawable.ic_bluetooth_searching_black_24dp));
                            break;
                        case BluetoothAdapter.STATE_ON:
                            relBle.setEnabled(true);
                            EventBus.getDefault().post(new ConnectivityEvent(ConnectivityEvent.BLE, 1));
                            img_notification_Ble.setBackground(context.getDrawable(R.drawable.ic_bluetooth_on));
                            break;
                        case BluetoothAdapter.STATE_TURNING_ON:
                            img_notification_Ble.setBackground(context.getDrawable(R.drawable.ic_bluetooth_searching_black_24dp));
                            break;
                    }
                }

            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.relNotificationMobileData:
                try {
                    seekbarBrightness.setVisibility(View.GONE);
                    img_notification_Brightness.setBackground(context.getDrawable(R.drawable.ic_brightness_off_black_24dp));
                    hide();
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity"));
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.e(TAG, "Setting screen not found due to: " + e.fillInStackTrace());
                }
                break;
            case R.id.relNotificationDND:
                seekbarBrightness.setVisibility(View.GONE);
                img_notification_Brightness.setBackground(context.getDrawable(R.drawable.ic_brightness_off_black_24dp));
                if (currentModeDeviceMode == 0) {
                    launcherPrefs.edit().putInt("getCurrentProfile", 1).apply();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                            && !notificationManager.isNotificationPolicyAccessGranted()) {
                    } else {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    }
                    img_notification_Dnd.setBackground(context.getDrawable(R.drawable.ic_vibration_black_24dp));
                } else if (currentModeDeviceMode == 1) {
                    launcherPrefs.edit().putInt("getCurrentProfile", 2).apply();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                            && !notificationManager.isNotificationPolicyAccessGranted()) {
                    } else {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    }
                    img_notification_Dnd.setBackground(context.getDrawable(R.drawable.ic_do_not_disturb_on_black_24dp));
                } else if (currentModeDeviceMode == 2) {
                    launcherPrefs.edit().putInt("getCurrentProfile", 0).apply();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                            && !notificationManager.isNotificationPolicyAccessGranted()) {
                    } else {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    }
                    img_notification_Dnd.setBackground(context.getDrawable(R.drawable.ic_do_not_disturb_off_black_24dp));
                }
                currentModeDeviceMode = launcherPrefs.getInt("getCurrentProfile", 0);
                EventBus.getDefault().post(new ConnectivityEvent(ConnectivityEvent.DND, 0));
                break;
            case R.id.relNotificationAirPlane:
                try {
                    // No root permission, just show the Airplane / Flight mode setting screen.
                    seekbarBrightness.setVisibility(View.GONE);
                    img_notification_Brightness.setBackground(context.getDrawable(R.drawable.ic_brightness_off_black_24dp));
                    if (wifiManager.isWifiEnabled()) {
                        isWiFiOn = true;
                    }
                    hide();
                    Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.e(TAG, "Setting screen not found due to: " + e.fillInStackTrace());
                }
                break;
            case R.id.relNotificationFlash:
                seekbarBrightness.setVisibility(View.GONE);
                img_notification_Brightness.setBackground(context.getDrawable(R.drawable.ic_brightness_off_black_24dp));
                if (StatusBarService.isFlashOn) {
                    EventBus.getDefault().post(new TorchOnOff(false));
                    img_notification_Flash.setBackground(context.getDrawable(R.drawable.ic_flash_off_black_24dp));
                } else {
                    EventBus.getDefault().post(new TorchOnOff(true));
                    img_notification_Flash.setBackground(context.getDrawable(R.drawable.ic_flash_on_black_24dp));
                }
                break;
            case R.id.relNotificationBrightness:
                if (checkSystemWritePermission()) {
                    if (seekbarBrightness.getVisibility() == View.VISIBLE) {
                        seekbarBrightness.setVisibility(View.GONE);
                        img_notification_Brightness.setBackground(context.getDrawable(R.drawable.ic_brightness_off_black_24dp));
                    } else {
                        bindBrightnessControl();
                        seekbarBrightness.setVisibility(View.VISIBLE);
                        img_notification_Brightness.setBackground(context.getDrawable(R.drawable.ic_brightness_on_black_24dp));
                    }
                } else {
                    Intent intent;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        hide();
                        intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.parse("package:" + context.getPackageName()));
                        context.startActivity(intent);
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * Turning On/Off WIFI
     */
    private void turnOnOffWIFI() {
        try {
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
                img_notification_Wifi.setBackground(context.getDrawable(R.drawable.ic_wifi_0));
            } else {
                wifiManager.setWifiEnabled(false);
                if (imgWifi != null) imgWifi.setVisibility(View.GONE);
                if (img_notification_Wifi != null)
                    img_notification_Wifi.setBackground(context.getDrawable(R.drawable.ic_signal_wifi_off_black_24dp));
                //EventBus.getDefault().post(new ConnectivityEvent(ConnectivityEvent.WIFI, -1));
            }
            if (!NetworkUtil.isAirplaneModeOn(context)) {
                relMobileData.setEnabled(true);
                checkMobileData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Used for check the Write permission
     *
     * @return
     */
    private boolean checkSystemWritePermission() {
        boolean retVal = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            retVal = Settings.System.canWrite(context);
        }
        return retVal;
    }

    public boolean isMobileDataEnable() {
        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean) method.invoke(cm);
        } catch (Exception e) {
            // Some problem accessible private API and do whatever error handling you want here
        }
        return mobileDataEnabled;
    }

    public boolean toggleMobileDataConnection(boolean ON) {
        try {
            //create instance of connectivity manager and get system connectivity service
            final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            //create instance of class and get name of connectivity manager system service class
            final Class conmanClass = Class.forName(conman.getClass().getName());
            //create instance of field and get mService Declared field
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            //Attempt to set the value of the accessible flag to true
            iConnectivityManagerField.setAccessible(true);
            //create instance of object and get the value of field conman
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            //create instance of class and get the name of iConnectivityManager field
            final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
            //create instance of method and get declared method and type
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            //Attempt to set the value of the accessible flag to true
            setMobileDataEnabledMethod.setAccessible(true);
            //dynamically invoke the iConnectivityManager object according to your need (true/false)
            setMobileDataEnabledMethod.invoke(iConnectivityManager, ON);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public void displayBatteryIcon(int batteryStatus, String isCharging) {
        if (imgBattery != null) {
            if (!TextUtils.isEmpty(isCharging) && isCharging.equalsIgnoreCase("ON")) {
                if ((batteryStatus >= 0 && batteryStatus < 5) || (batteryStatus < 0)) {
                    imgBattery.setImageResource(R.drawable.battery_c_05);
                } else if (batteryStatus >= 5 && batteryStatus < 10) {
                    imgBattery.setImageResource(R.drawable.battery_c_05);
                } else if (batteryStatus >= 10 && batteryStatus < 15) {
                    imgBattery.setImageResource(R.drawable.battery_c_10);
                } else if (batteryStatus >= 15 && batteryStatus < 20) {
                    imgBattery.setImageResource(R.drawable.battery_c_15);
                } else if (batteryStatus >= 20 && batteryStatus < 25) {
                    imgBattery.setImageResource(R.drawable.battery_c_20);
                } else if (batteryStatus >= 25 && batteryStatus < 30) {
                    imgBattery.setImageResource(R.drawable.battery_c_25);
                } else if (batteryStatus >= 30 && batteryStatus < 35) {
                    imgBattery.setImageResource(R.drawable.battery_c_30);
                } else if (batteryStatus >= 35 && batteryStatus < 40) {
                    imgBattery.setImageResource(R.drawable.battery_c_35);
                } else if (batteryStatus >= 40 && batteryStatus < 45) {
                    imgBattery.setImageResource(R.drawable.battery_c_40);
                } else if (batteryStatus >= 45 && batteryStatus < 50) {
                    imgBattery.setImageResource(R.drawable.battery_c_45);
                } else if (batteryStatus >= 50 && batteryStatus < 55) {
                    imgBattery.setImageResource(R.drawable.battery_c_50);
                } else if (batteryStatus >= 55 && batteryStatus < 60) {
                    imgBattery.setImageResource(R.drawable.battery_c_55);
                } else if (batteryStatus >= 60 && batteryStatus < 65) {
                    imgBattery.setImageResource(R.drawable.battery_c_60);
                } else if (batteryStatus >= 65 && batteryStatus < 70) {
                    imgBattery.setImageResource(R.drawable.battery_c_65);
                } else if (batteryStatus >= 70 && batteryStatus < 75) {
                    imgBattery.setImageResource(R.drawable.battery_c_70);
                } else if (batteryStatus >= 75 && batteryStatus < 80) {
                    imgBattery.setImageResource(R.drawable.battery_c_75);
                } else if (batteryStatus >= 80 && batteryStatus < 85) {
                    imgBattery.setImageResource(R.drawable.battery_c_80);
                } else if (batteryStatus >= 85 && batteryStatus < 90) {
                    imgBattery.setImageResource(R.drawable.battery_c_85);
                } else if (batteryStatus >= 90 && batteryStatus < 95) {
                    imgBattery.setImageResource(R.drawable.battery_c_90);
                } else if (batteryStatus >= 95 && batteryStatus < 100) {
                    imgBattery.setImageResource(R.drawable.battery_c_95);
                } else if (batteryStatus >= 100) {
                    imgBattery.setImageResource(R.drawable.battery_c_100);
                } else {
                    imgBattery.setImageResource(R.drawable.battery_c_50);
                }
            } else if (!TextUtils.isEmpty(isCharging) && isCharging.equalsIgnoreCase("OFF")) {
                if ((batteryStatus >= 0 && batteryStatus < 5) || (batteryStatus < 0)) {
                    imgBattery.setImageResource(R.drawable.battery_alert);
                } else if (batteryStatus >= 5 && batteryStatus < 10) {
                    imgBattery.setImageResource(R.drawable.battery_n_05);
                } else if (batteryStatus >= 10 && batteryStatus < 15) {
                    imgBattery.setImageResource(R.drawable.battery_n_10);
                } else if (batteryStatus >= 15 && batteryStatus < 20) {
                    imgBattery.setImageResource(R.drawable.battery_n_15);
                } else if (batteryStatus >= 20 && batteryStatus < 25) {
                    imgBattery.setImageResource(R.drawable.battery_n_20);
                } else if (batteryStatus >= 25 && batteryStatus < 30) {
                    imgBattery.setImageResource(R.drawable.battery_n_25);
                } else if (batteryStatus >= 30 && batteryStatus < 35) {
                    imgBattery.setImageResource(R.drawable.battery_n_30);
                } else if (batteryStatus >= 35 && batteryStatus < 40) {
                    imgBattery.setImageResource(R.drawable.battery_n_35);
                } else if (batteryStatus >= 40 && batteryStatus < 45) {
                    imgBattery.setImageResource(R.drawable.battery_n_40);
                } else if (batteryStatus >= 45 && batteryStatus < 50) {
                    imgBattery.setImageResource(R.drawable.battery_n_45);
                } else if (batteryStatus >= 50 && batteryStatus < 55) {
                    imgBattery.setImageResource(R.drawable.battery_n_50);
                } else if (batteryStatus >= 55 && batteryStatus < 60) {
                    imgBattery.setImageResource(R.drawable.battery_n_55);
                } else if (batteryStatus >= 60 && batteryStatus < 65) {
                    imgBattery.setImageResource(R.drawable.battery_n_60);
                } else if (batteryStatus >= 65 && batteryStatus < 70) {
                    imgBattery.setImageResource(R.drawable.battery_n_65);
                } else if (batteryStatus >= 70 && batteryStatus < 75) {
                    imgBattery.setImageResource(R.drawable.battery_n_70);
                } else if (batteryStatus >= 75 && batteryStatus < 80) {
                    imgBattery.setImageResource(R.drawable.battery_n_75);
                } else if (batteryStatus >= 80 && batteryStatus < 85) {
                    imgBattery.setImageResource(R.drawable.battery_n_80);
                } else if (batteryStatus >= 85 && batteryStatus < 90) {
                    imgBattery.setImageResource(R.drawable.battery_n_85);
                } else if (batteryStatus >= 90 && batteryStatus < 95) {
                    imgBattery.setImageResource(R.drawable.battery_n_90);
                } else if (batteryStatus >= 95 && batteryStatus < 100) {
                    imgBattery.setImageResource(R.drawable.battery_n_95);
                } else if (batteryStatus >= 100) {
                    imgBattery.setImageResource(R.drawable.battery_n_100);
                } else {
                    imgBattery.setImageResource(R.drawable.battery_n_50);
                }
            } else {
                Log.d(TAG, "Charging Status not identify");
            }
        }
    }

    private void declinePhone(Context context) throws Exception {

        try {
            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";
            Class<?> telephonyClass;
            Class<?> telephonyStubClass;
            Class<?> serviceManagerClass;
            Class<?> serviceManagerNativeClass;
            Method telephonyEndCall;
            Object telephonyObject;
            Object serviceManagerObject;
            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
            Method getService = // getDefaults[29];
                    serviceManagerClass.getMethod("getService", String.class);
            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");
            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
            telephonyObject = serviceMethod.invoke(null, retbinder);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            telephonyEndCall.invoke(telephonyObject);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
