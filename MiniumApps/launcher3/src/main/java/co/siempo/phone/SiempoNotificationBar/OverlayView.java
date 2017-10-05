/*
 * Simiasque
 * Copyright (C) 2015 Orange
 * Authors: arnaud.ruffin@orange.com
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.siempo.phone.SiempoNotificationBar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextClock;
import android.widget.TextView;
import com.blankj.utilcode.util.NetworkUtils;
import com.james.status.data.IconStyleData;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import co.siempo.phone.R;
import co.siempo.phone.db.CallStorageDao;
import co.siempo.phone.db.DBUtility;
import co.siempo.phone.db.NotificationSwipeEvent;
import co.siempo.phone.db.TableNotificationSms;
import co.siempo.phone.db.TableNotificationSmsDao;
import co.siempo.phone.event.ConnectivityEvent;
import co.siempo.phone.event.NewNotificationEvent;
import co.siempo.phone.event.TempoEvent;
import co.siempo.phone.event.TopBarUpdateEvent;
import co.siempo.phone.event.TorchOnOff;
import co.siempo.phone.main.SimpleItemTouchHelperCallback;
import co.siempo.phone.network.NetworkUtil;
import co.siempo.phone.notification.ItemClickSupport;
import co.siempo.phone.notification.Notification;
import co.siempo.phone.notification.NotificationContactModel;
import co.siempo.phone.notification.NotificationUtility;
import co.siempo.phone.notification.RecyclerListAdapter;
import co.siempo.phone.notification.remove_notification_strategy.DeleteIteam;
import co.siempo.phone.notification.remove_notification_strategy.MultipleIteamDelete;
import co.siempo.phone.receiver.AirplaneModeDataReceiver;
import co.siempo.phone.receiver.BatteryDataReceiver;
import co.siempo.phone.receiver.IDynamicStatus;
import co.siempo.phone.receiver.NetworkDataReceiver;
import co.siempo.phone.receiver.WifiDataReceiver;
import co.siempo.phone.service.StatusBarService;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.HomeWatcher;
import static android.graphics.PixelFormat.TRANSLUCENT;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;

/**
 * The layout that will be used to display the custom status bar and notification bar
 *
 */
class OverlayView extends FrameLayout implements View.OnClickListener{
    private static final String TRACE_TAG = "";
    private View inflateLayout;
    private Context context;
    private RelativeLayout mNotificationLayout;
    private FrameLayout mParentView;
    private WindowManager mWinManager;
    private boolean siempoNotificationBar=false;
    private String TAG="OverlayView";


    /**
     *  Status Bar Variables
     */

    private SharedPreferences launcherPrefs;
    private ImageView imgNotification,imgTempo,imgBattery,imgSignal,imgWifi,imgAirplane;
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
    private TextView emptyView,textView_notification_title;
    private SeekBar seekbarBrightness;
    private int brightness;
    private RecyclerListAdapter adapter;
    private List<Notification> notificationList;
    private RelativeLayout layout_notification,relWifi, relMobileData, relBle, relDND, relAirPlane, relFlash, relBrightness;
    private ImageView img_background,img_notification_Wifi, img_notification_Data, img_notification_Ble, img_notification_Dnd, img_notification_Airplane, img_notification_Flash, img_notification_Brightness;
    private enum mSwipeDirection {UP, DOWN, NONE}
    private ConnectivityManager connectivityManager;
    private TableNotificationSmsDao smsDao;
    private CallStorageDao callStorageDao;
    private int count = 1;
    private TelephonyManager telephonyManager;
    private BleSingal bleSingal;
    private int currentModeDeviceMode;
    private AudioManager audioManager;
    private boolean isWiFiOn = false;



    public OverlayView(Context context) {
        super(context);
        this.context=context;
        siempoNotificationBar = false;
        inflateLayout = inflate(context, R.layout.notification_statusbar, this);

        /**
         * StatusBar Methods to init components and update UI
         */
        context.startService(new Intent(context, StatusBarService.class));
        initStatusBarComponents();
        updateStatusBarUI();

        /**
         * Hide Notification if it is visible by press homepress key or Recent Button Key
         */
        HomeWatcher mHomeWatcher = new HomeWatcher(context);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                hide();
            }

            @Override
            public void onHomeLongPressed() {
                hide();
            }
        });
        mHomeWatcher.startWatch();
    }


    /**
     * Status Bar init components
     */
    private void initStatusBarComponents(){

        /**
         * Register Event Bus to get updated status of Battery, WiFi, AirPlane Mode
         */
        EventBus.getDefault().register(this);

        /**
         * Initialization of components
         */
        launcherPrefs=context.getSharedPreferences("Launcher3Prefs", 0);
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        imgNotification=(ImageView)inflateLayout.findViewById(R.id.imgNotification);
        imgTempo=(ImageView)inflateLayout.findViewById(R.id.imgTempo);
        imgBattery=(ImageView)inflateLayout.findViewById(R.id.imgBattery);
        imgSignal=(ImageView)inflateLayout.findViewById(R.id.imgSignal);
        imgWifi=(ImageView)inflateLayout.findViewById(R.id.imgWifi);
        imgAirplane=(ImageView)inflateLayout.findViewById(R.id.imgAirplane);
        iTxt2=(TextClock)inflateLayout.findViewById(R.id.iTxt2);

        /**
         * Register Airplane Mode, Wifi Receiver, Battery Reciever, Network Reciever
         */

        airplaneModeDataReceiver = new AirplaneModeDataReceiver();
        airplaneModeDataReceiver.register(context);
        wifiDataReceiver = new WifiDataReceiver();
        wifiDataReceiver.register(context);
        batteryDataReceiver = new BatteryDataReceiver();
        batteryDataReceiver.register(context);
        networkDataReceiver = new NetworkDataReceiver(context);
        networkDataReceiver.register(context);

    }

    @Subscribe
    public void updateTopBar(TopBarUpdateEvent event) {
        updateStatusBarUI();
    }


    /**
     * Status Bar update UI
     */
    private void updateStatusBarUI() {
        imgSignal.setVisibility(NetworkUtil.isAirplaneModeOn(context) ? View.GONE : View.VISIBLE);
        imgWifi.setVisibility(NetworkUtil.isAirplaneModeOn(context) ? View.GONE : View.VISIBLE);
        imgWifi.setVisibility(NetworkUtil.isWifiOn(context) ? View.VISIBLE : View.GONE);
        imgAirplane.setVisibility(NetworkUtil.isAirplaneModeOn(context) ? View.VISIBLE : View.GONE);
        long notifCount = DBUtility.getTableNotificationSmsDao().count() + DBUtility.getCallStorageDao().count();
        imgNotification.setVisibility(notifCount == 0 ? View.GONE : View.VISIBLE);
        onTempoEvent(new TempoEvent(launcherPrefs.getBoolean("isTempoActive",false)));
    }



    @Subscribe
    public void onTempoEvent(TempoEvent event) {
        if (event.isStarting()) {
            imgTempo.setVisibility(View.VISIBLE);
            launcherPrefs.edit().putBoolean("isTempoActive",true).commit();
        } else {
            imgTempo.setVisibility(View.GONE);
            launcherPrefs.edit().putBoolean("isTempoActive",false).commit();
        }
    }



    /**
     * StatusBar : Connectivity Change Listener
     */
    @Subscribe
    public void onConnectivityEvent(ConnectivityEvent event) {
        if (event.getState() == ConnectivityEvent.AIRPLANE) {
            imgSignal.setVisibility(NetworkUtil.isAirplaneModeOn(context) ? View.GONE : View.VISIBLE);
            img_notification_Wifi.setVisibility(NetworkUtil.isAirplaneModeOn(context) ? View.GONE : View.VISIBLE);
            img_notification_Airplane.setVisibility(NetworkUtil.isAirplaneModeOn(context) ? View.VISIBLE : View.GONE);
            imgAirplane.setVisibility(NetworkUtil.isAirplaneModeOn(context) ? View.VISIBLE : View.GONE);
        } else if (event.getState() == ConnectivityEvent.WIFI) {
            /**
             * Update notificationBar Wifi Icon
             */
            if (wifiManager.isWifiEnabled()) {
                img_notification_Wifi.setVisibility(View.VISIBLE);
                imgWifi.setVisibility(View.VISIBLE);
                imgWifi.setImageResource(getWifiIcon(WifiManager.calculateSignalLevel(wifiManager.getConnectionInfo().getRssi(), 5)));
                int level = event.getValue();
                if (level == 0) {
                    if (img_notification_Wifi!= null) {
                        img_notification_Wifi.setBackground(context.getDrawable(R.drawable.ic_wifi_0));
                    }
                } else if (level == 1) {
                    if (img_notification_Wifi != null) {
                        img_notification_Wifi.setBackground(context.getDrawable(R.drawable.ic_wifi_1));
                    }
                } else if (level == 2) {
                    if (img_notification_Wifi != null) {
                        img_notification_Wifi.setBackground(context.getDrawable(R.drawable.ic_wifi_2));
                    }
                } else if (level == 3) {
                    if (img_notification_Wifi != null) {
                        img_notification_Wifi.setBackground(context.getDrawable(R.drawable.ic_wifi_3));
                    }
                } else if (level == 4) {
                    if (img_notification_Wifi != null) {
                        img_notification_Wifi.setBackground(context.getDrawable(R.drawable.ic_wifi_4));
                    }
                }
            }
            if (event.getValue() == -1 || !wifiManager.isWifiEnabled()) {
                if(img_notification_Wifi!=null) {
                    img_notification_Wifi.setBackground(context.getDrawable(R.drawable.ic_signal_wifi_off_black_24dp));
                }

                if(imgWifi!=null){
                    imgWifi.setVisibility(View.GONE);
                }
            }
        } else if (event.getState() == ConnectivityEvent.BATTERY) {
            imgBattery.setImageResource(getBatteryIcon2(event.getValue()));
        } else if (event.getState() == ConnectivityEvent.NETWORK) {
            /**
             * Update status bar network icon
             */
            imgSignal.setImageResource(getNetworkIcon(event.getValue()));
            /**
             * Update notification bar network icon
             */
            if (!NetworkUtil.isAirplaneModeOn(context)) {
                relMobileData.setEnabled(true);
                checkMobileData();
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
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                textView_notification_title.setVisibility(View.GONE);
                imgNotification.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
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
                new WindowManager.LayoutParams(MATCH_PARENT, height, TYPE_SYSTEM_ERROR, FLAG_NOT_FOCUSABLE
                        | FLAG_LAYOUT_IN_SCREEN
                        | FLAG_LAYOUT_NO_LIMITS
                        | FLAG_NOT_TOUCH_MODAL
                        | FLAG_LAYOUT_INSET_DECOR
                       , TRANSLUCENT);
        params.gravity = Gravity.TOP;
        return params;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        addSiempoNotificationBar(event);
        return super.onTouchEvent(event);
    }

    public int retrieveStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    /**
     * Shows view
     */
    public void show(){
        final Animation in = AnimationUtils.loadAnimation(context, R.anim.slide_down);
        mNotificationLayout.setVisibility(VISIBLE);
        mNotificationLayout.startAnimation(in);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
    }

    /**
     * Hides view
     */
    public synchronized  void hide() {
        if(siempoNotificationBar){
            siempoNotificationBar = false;
            final Animation in = AnimationUtils.loadAnimation(context, R.anim.slide_up);
            final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);

            in.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mNotificationLayout.setVisibility(GONE);
                    mWinManager.updateViewLayout(mParentView,params);
                    mWinManager.removeView(mParentView);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mNotificationLayout.setVisibility(VISIBLE);
            mNotificationLayout.startAnimation(in);
        }


    }


    /**
     * Load and Display Siempo Notification bar when swipe down
     */
    public synchronized  void addSiempoNotificationBar(MotionEvent event){
        if (event.getY() > (retrieveStatusBarHeight()+20) && !siempoNotificationBar) {
            siempoNotificationBar = true;

            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);
            mWinManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            /**
             * Initialization components of Siempo NotificationBar
             */
            audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            mNotificationLayout = (RelativeLayout) inflater.inflate(R.layout.notification_bar, null);
            seekbarBrightness=(SeekBar) mNotificationLayout.findViewById(R.id.seekbarBrightness);
            recyclerView=(RecyclerView) mNotificationLayout.findViewById(R.id.recyclerView);
            emptyView=(TextView)mNotificationLayout.findViewById(R.id.emptyView);
            layout_notification = (RelativeLayout) mNotificationLayout.findViewById(R.id.layout_notification);
            img_background = (ImageView) mNotificationLayout.findViewById(R.id.img_background);
            textView_notification_title = (TextView) mNotificationLayout.findViewById(R.id.textView_notification_title);
            relWifi = (RelativeLayout)mNotificationLayout.findViewById(R.id.relWifi);
            relBle = (RelativeLayout)mNotificationLayout.findViewById(R.id.relBle);
            relDND = (RelativeLayout)mNotificationLayout.findViewById(R.id.relDND);
            relAirPlane =(RelativeLayout)mNotificationLayout.findViewById(R.id.relAirPlane);
            relBrightness = (RelativeLayout)mNotificationLayout.findViewById(R.id.relBrightness);
            relFlash = (RelativeLayout)mNotificationLayout.findViewById(R.id.relFlash);
            relMobileData = (RelativeLayout)mNotificationLayout.findViewById(R.id.relMobileData);
            img_notification_Wifi = (ImageView)mNotificationLayout.findViewById(R.id.imgWifi);
            img_notification_Data = (ImageView)mNotificationLayout.findViewById(R.id.imgData);
            img_notification_Ble = (ImageView)mNotificationLayout.findViewById(R.id.imgBle);
            img_notification_Dnd = (ImageView)mNotificationLayout.findViewById(R.id.imgDnd);
            img_notification_Airplane = (ImageView)mNotificationLayout.findViewById(R.id.imgAirplane);
            img_notification_Flash = (ImageView)mNotificationLayout.findViewById(R.id.imgFlash);
            img_notification_Brightness= (ImageView)mNotificationLayout.findViewById(R.id.imgBrightness);


            relWifi.setOnClickListener(this);
            relBle.setOnClickListener(this);
            relMobileData.setOnClickListener(this);
            relDND.setOnClickListener(this);
            relAirPlane.setOnClickListener(this);
            relFlash.setOnClickListener(this);
            relBrightness.setOnClickListener(this);
            mNotificationLayout.setFocusable(true);
            mNotificationLayout.setVisibility(GONE);

            params.width = ActionBar.LayoutParams.MATCH_PARENT;
            params.height = ActionBar.LayoutParams.MATCH_PARENT;

            mParentView = new FrameLayout(context){
                @Override
                public boolean dispatchKeyEvent(KeyEvent event) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                        hide();
                    }
                    if (event.getKeyCode() == KeyEvent.KEYCODE_APP_SWITCH) {
                        hide();
                    }
                    return super.dispatchKeyEvent(event);
                }
            };

            mWinManager.addView(mParentView, params);
            mParentView.addView(mNotificationLayout);
            mNotificationLayout.setVisibility(GONE);
            show();
            updateNotificationComponents();
        }
    }


    public void updateNotificationComponents(){
        notificationList = new ArrayList<>();
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new RecyclerListAdapter(context, notificationList);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter, context);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        emptyView.setOnClickListener(new View.OnClickListener() {
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
                } else if (notificationList.get(position).getNotificationType() == NotificationUtility.NOTIFICATION_TYPE_CALL) {
                    if (
                            ContextCompat.checkSelfPermission( context, android.Manifest.permission.READ_CALL_LOG ) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission( context, Manifest.permission.WRITE_CALL_LOG ) != PackageManager.PERMISSION_GRANTED ) {

                    }
                    else{
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + notificationList.get(position).getNumber()));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        hide();
                    }
                }
                // Following code will delete all notification of same user and same types.
                DeleteIteam deleteIteam = new DeleteIteam(new MultipleIteamDelete());
                deleteIteam.executeDelete(notificationList.get(position));
                loadData();
            }


        });

        smsDao = DBUtility.getNotificationDao();
        callStorageDao = DBUtility.getCallStorageDao();
        loadData();
        bindBrighnessControl();
        bleSingal = new BleSingal();
        context.registerReceiver(bleSingal, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        statusOfQuickSettings();
    }


    private void bindBrighnessControl() {
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
            BluetoothAdapter.getDefaultAdapter().disable();
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
            }

            if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
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
        currentModeDeviceMode = audioManager.getRingerMode();
        if (currentModeDeviceMode == AudioManager.RINGER_MODE_NORMAL) {
            img_notification_Dnd.setBackground(context.getDrawable(R.drawable.ic_do_not_disturb_off_black_24dp));
        } else if (currentModeDeviceMode == AudioManager.RINGER_MODE_SILENT) {
            img_notification_Dnd.setBackground(context.getDrawable(R.drawable.ic_do_not_disturb_on_black_24dp));
        } else if (currentModeDeviceMode == AudioManager.RINGER_MODE_VIBRATE) {
            img_notification_Dnd.setBackground(context.getDrawable(R.drawable.ic_vibration_black_24dp));
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
     * @param tableNotificationSms
     */
    @Subscribe
    public void newNotificationEvent(NewNotificationEvent tableNotificationSms) {
        System.out.println("NotificationFragment.newNotificationEvent" + tableNotificationSms);
        if (tableNotificationSms != null) {
            imgNotification.setVisibility(View.VISIBLE);
            if (!checkNotificationExistsOrNot(tableNotificationSms.getTopTableNotificationSmsDao().getId())) {
                @SuppressLint("SimpleDateFormat")
                DateFormat sdf = new SimpleDateFormat("hh:mm a");
                String time = sdf.format(tableNotificationSms.getTopTableNotificationSmsDao().get_date());
                Notification n = new Notification(gettingNameAndImageFromPhoneNumber(tableNotificationSms.getTopTableNotificationSmsDao().get_contact_title()),
                        tableNotificationSms.getTopTableNotificationSmsDao().getId(), tableNotificationSms.getTopTableNotificationSmsDao().get_contact_title(),
                        tableNotificationSms.getTopTableNotificationSmsDao().get_message(), time, false, tableNotificationSms.getTopTableNotificationSmsDao().getNotification_type());
                notificationList.add(0,n);
                adapter.notifyDataSetChanged();

                if (notificationList.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * Check to see whether the new notification data already exist in list or not.
     * @param id
     * @return
     */
    private boolean checkNotificationExistsOrNot(Long id) {
        for (Notification notification : notificationList) {
            if (notification.getId() == id) {
                return true;
            }
        }
        return false;
    }

    private void setUpNotifications(List<TableNotificationSms> items) {
        notificationList.clear();
        for (int i = 0; i < items.size(); i++) {
            @SuppressLint("SimpleDateFormat") DateFormat sdf = new SimpleDateFormat("hh:mm a");
            String time = sdf.format(items.get(i).get_date());
            Notification n = new Notification(gettingNameAndImageFromPhoneNumber(items.get(i).get_contact_title()), items.get(i).getId(), items.get(i).get_contact_title(), items.get(i).get_message(), time, false, items.get(i).getNotification_type());
            notificationList.add(n);
        }

        if (items.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            //  btnClearAll.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            adapter.notifyDataSetChanged();
            recyclerView.setVisibility(View.VISIBLE);
            //  btnClearAll.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
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
                            } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
                                    && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
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
        Log.d(TAG, "" + NetworkUtils.getDataEnabled());
        if (NetworkUtil.getConnectivityStatus(context) == NetworkUtil.TYPE_MOBILE) {
            img_notification_Data.setBackground(context.getDrawable(R.drawable.ic_data_off_black_24dp));
        } else if (NetworkUtil.getConnectivityStatus(context) == NetworkUtil.TYPE_WIFI) {
            img_notification_Data.setBackground(context.getDrawable(R.drawable.ic_data_on_black_24dp));
        } else if (NetworkUtil.getConnectivityStatus(context) == NetworkUtil.TYPE_NOT_CONNECTED) {
            img_notification_Data.setBackground(context.getDrawable(R.drawable.ic_data_on_black_24dp));
        }
    }


    /**
     * Broadcast Receiver for the Blutooth single.
     */
    class BleSingal extends BroadcastReceiver {

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
            case R.id.relMobileData:
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
            case R.id.relWifi:
                seekbarBrightness.setVisibility(View.GONE);
                img_notification_Brightness.setBackground(context.getDrawable(R.drawable.ic_brightness_off_black_24dp));
                turnOnOffWIFI();
                break;
            case R.id.relBle:
                seekbarBrightness.setVisibility(View.GONE);
                img_notification_Brightness.setBackground(context.getDrawable(R.drawable.ic_brightness_off_black_24dp));
                if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                    BluetoothAdapter.getDefaultAdapter().disable();
                    EventBus.getDefault().post(new ConnectivityEvent(ConnectivityEvent.BLE, 0));
                } else {
                    BluetoothAdapter.getDefaultAdapter().enable();
                    relBle.setEnabled(false);
                    img_notification_Ble.setBackground(context.getDrawable(R.drawable.ic_bluetooth_searching_black_24dp));
                    EventBus.getDefault().post(new ConnectivityEvent(ConnectivityEvent.BLE, 1));
                }
                break;
            case R.id.relDND:
                seekbarBrightness.setVisibility(View.GONE);
                img_notification_Brightness.setBackground(context.getDrawable(R.drawable.ic_brightness_off_black_24dp));
                if (currentModeDeviceMode == AudioManager.RINGER_MODE_NORMAL) {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    img_notification_Dnd.setBackground(context.getDrawable(R.drawable.ic_vibration_black_24dp));
                } else if (currentModeDeviceMode == AudioManager.RINGER_MODE_VIBRATE) {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    img_notification_Dnd.setBackground(context.getDrawable(R.drawable.ic_do_not_disturb_on_black_24dp));
                } else if (currentModeDeviceMode == AudioManager.RINGER_MODE_SILENT) {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    img_notification_Dnd.setBackground(context.getDrawable(R.drawable.ic_do_not_disturb_off_black_24dp));
                }
                currentModeDeviceMode = audioManager.getRingerMode();
                EventBus.getDefault().post(new ConnectivityEvent(ConnectivityEvent.DND, 0));
                break;
            case R.id.relAirPlane:
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
            case R.id.relFlash:
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
            case R.id.relBrightness:
                if (checkSystemWritePermission()) {
                    if (seekbarBrightness.getVisibility() == View.VISIBLE) {
                        seekbarBrightness.setVisibility(View.GONE);
                        img_notification_Brightness.setBackground(context.getDrawable(R.drawable.ic_brightness_off_black_24dp));
                    } else {
                        seekbarBrightness.setVisibility(View.VISIBLE);
                        img_notification_Brightness.setBackground(context.getDrawable(R.drawable.ic_brightness_on_black_24dp));
                    }
                } else {
                    Intent intent = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
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
                img_notification_Wifi.setBackground(context.getDrawable(R.drawable.ic_signal_wifi_off_black_24dp));
                EventBus.getDefault().post(new ConnectivityEvent(ConnectivityEvent.WIFI, -1));
            }
            if (!NetworkUtil.isAirplaneModeOn(context)) {relMobileData.setEnabled(true);checkMobileData();}
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

}
