package co.siempo.phone.notification;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.james.status.data.IconStyleData;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3Prefs_;
import co.siempo.phone.db.CallStorageDao;
import co.siempo.phone.db.DBUtility;
import co.siempo.phone.db.NotificationSwipeEvent;
import co.siempo.phone.db.TableNotificationSms;
import co.siempo.phone.db.TableNotificationSmsDao;
import co.siempo.phone.event.ConnectivityEvent;
import co.siempo.phone.event.NewNotificationEvent;
import co.siempo.phone.event.NotificationTrayEvent;
import co.siempo.phone.event.TorchOnOff;
import co.siempo.phone.main.SimpleItemTouchHelperCallback;
import co.siempo.phone.network.NetworkUtil;
import co.siempo.phone.notification.remove_notification_strategy.DeleteItem;
import co.siempo.phone.notification.remove_notification_strategy.MultipleIteamDelete;
import co.siempo.phone.receiver.IDynamicStatus;
import co.siempo.phone.receiver.WifiDataReceiver;
import co.siempo.phone.service.StatusBarService;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import minium.co.core.config.Config;
import minium.co.core.event.HomePressEvent;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreFragment;
import minium.co.core.util.UIUtils;


@EFragment(R.layout.notification_main)
public class NotificationFragment extends CoreFragment implements View.OnTouchListener {

    private static final String TAG = "NotificationFragment";

    @Pref
    Launcher3Prefs_ launcherPrefs;

    @ViewById
    RecyclerView recyclerView;

    @ViewById
    TextView emptyView, textView_notification_title;

    RecyclerListAdapter adapter;
    private List<Notification> notificationList;

    @ViewById
    SeekBar seekbarBrightness;
    //Variable to store brightness value
    private int brightness;

    @ViewById
    RelativeLayout layout_notification, relWifi, relMobileData, relBle, relDND, relAirPlane, relFlash, relBrightness;

    @ViewById
    ImageView linSecond, imgWifi, imgData, imgBle, imgDnd, imgAirplane, imgFlash, imgBrightness;

    @SystemService
    WifiManager wifiManager;

    @SystemService
    ConnectivityManager connectivityManager;

    TelephonyManager telephonyManager;

    IDynamicStatus wifiDataReceiver;

    AudioChangeReceiver audioChangeReceiver;
    BleSingal bleSingal;
    int currentModeDeviceMode;

    @SystemService
    AudioManager audioManager;
    private StatusBarHandler statusBarHandler;

    @Subscribe
    public void homePressEvent(HomePressEvent event) {
        try {
            animateOut();
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }
    }

    @SuppressLint("LogConditional")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "" + event.getAction());
        return false;
    }

    private enum mSwipeDirection {
        UP, DOWN, NONE
    }

    TableNotificationSmsDao smsDao;
    CallStorageDao callStorageDao;
    int count = 1;
    boolean isWiFiOn = false;

    @AfterViews
    void afterViews() {
        statusBarHandler = new StatusBarHandler(getActivity());
        telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        notificationList = new ArrayList<>();
        recyclerView.setNestedScrollingEnabled(false);

        adapter = new RecyclerListAdapter(getActivity(), notificationList);

        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);


        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter, getActivity());
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateOut();
            }
        });
        linSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linSecond.setClickable(false);
                animateOut();
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
                    Log.d(TAG, "" + count);
                }

            }
        });


        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {

            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                if (notificationList.get(position).getNotificationType() == NotificationUtility.NOTIFICATION_TYPE_SMS) {
                    removeStatusbar();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", notificationList.get(position).getNumber(), null)));
                } else if (notificationList.get(position).getNotificationType() == NotificationUtility.NOTIFICATION_TYPE_CALL) {
                    removeStatusbar();
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + notificationList.get(position).getNumber()));
                    startActivity(intent);
                }
                //++Tarun , Following code will delete all notification of same user and same types.
                DeleteItem deleteItem = new DeleteItem(new MultipleIteamDelete());
                deleteItem.executeDelete(notificationList.get(position));
                loadData();
            }


        });
        // This feature included in feature sprint.
//        ItemClickSupport.addTo(recyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
//                Toast.makeText(getActivity().getApplicationContext(), "Item long clicked at position " + position, Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });

        bindBrighnessControl();


        wifiDataReceiver = new WifiDataReceiver();
        wifiDataReceiver.register(context);


        audioChangeReceiver = new AudioChangeReceiver();
        getActivity().registerReceiver(audioChangeReceiver, new IntentFilter(
                AudioManager.RINGER_MODE_CHANGED_ACTION));

        bleSingal = new BleSingal();
        getActivity().registerReceiver(bleSingal, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));


    }

    private class AudioChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            bindDND();
        }
    }


    private void bindBrighnessControl() {
        try {
            //Get the current system brightness
            brightness = Settings.System.getInt(
                    getActivity().getContentResolver(),
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
                        screenBrightness(i, getActivity());
                    }
                    //Get the current system brightness
                    brightness = Settings.System.getInt(
                            getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
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
        if (NetworkUtil.isAirplaneModeOn(getActivity())) {
            relMobileData.setEnabled(false);
            imgAirplane.setBackground(getActivity().getDrawable(R.drawable.ic_airplane));
            wifiManager.setWifiEnabled(false);
            imgWifi.setBackground(getActivity().getDrawable(R.drawable.ic_signal_wifi_off_black_24dp));
            BluetoothAdapter.getDefaultAdapter().disable();
            imgBle.setBackground(getActivity().getDrawable(R.drawable.ic_bluetooth_disabled_black_24dp));
            imgData.setBackground(getActivity().getDrawable(R.drawable.ic_data_on_black_24dp));
        } else {
            relMobileData.setEnabled(true);
            checkMobileData();
            imgAirplane.setBackground(getActivity().getDrawable(R.drawable.ic_airplanemode_inactive_black_24dp));
            if (isWiFiOn) {
                wifiManager.setWifiEnabled(true);
            } else {
                isWiFiOn = false;
            }
            if (!wifiManager.isWifiEnabled() || NetworkUtil.isAirplaneModeOn(getActivity())) {
                imgWifi.setBackground(getActivity().getDrawable(R.drawable.ic_signal_wifi_off_black_24dp));
            } else {
                imgWifi.setBackground(getActivity().getDrawable(R.drawable.ic_wifi_0));
            }

            if (BluetoothAdapter.getDefaultAdapter()!=null && BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                imgBle.setBackground(getActivity().getDrawable(R.drawable.ic_bluetooth_on));
            } else {
                imgBle.setBackground(getActivity().getDrawable(R.drawable.ic_bluetooth_disabled_black_24dp));

            }
        }


        bindDND();

        bindFlash();

        if (telephonyManager.getNetworkOperator().equalsIgnoreCase("")) {
            imgData.setBackground(getActivity().getDrawable(R.drawable.ic_data_on_black_24dp));
            relMobileData.setEnabled(false);
        }

    }

    private void bindDND() {
        currentModeDeviceMode = launcherPrefs.getCurrentProfile().get();
        if (launcherPrefs.getCurrentProfile().get() == 0) {
            imgDnd.setBackground(getActivity().getDrawable(R.drawable.ic_do_not_disturb_off_black_24dp));
        } else if (launcherPrefs.getCurrentProfile().get() == 1) {
            imgDnd.setBackground(getActivity().getDrawable(R.drawable.ic_vibration_black_24dp));
        } else if (launcherPrefs.getCurrentProfile().get() == 2) {
            imgDnd.setBackground(getActivity().getDrawable(R.drawable.ic_do_not_disturb_on_black_24dp));
        }
//        currentModeDeviceMode = audioManager.getRingerMode();
//        if (currentModeDeviceMode == AudioManager.RINGER_MODE_NORMAL) {
//            imgDnd.setBackground(getActivity().getDrawable(R.drawable.ic_do_not_disturb_off_black_24dp));
//        } else if (currentModeDeviceMode == AudioManager.RINGER_MODE_SILENT) {
//            imgDnd.setBackground(getActivity().getDrawable(R.drawable.ic_do_not_disturb_on_black_24dp));
//        } else if (currentModeDeviceMode == AudioManager.RINGER_MODE_VIBRATE) {
//            imgDnd.setBackground(getActivity().getDrawable(R.drawable.ic_vibration_black_24dp));
//        }
    }

    private void bindFlash() {
        if (getActivity().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            relFlash.setVisibility(View.VISIBLE);
            getActivity().startService(new Intent(getActivity(), StatusBarService.class));
            if (StatusBarService.isFlashOn) {
                imgFlash.setBackground(getActivity().getDrawable(R.drawable.ic_flash_on_black_24dp));
            } else {
                imgFlash.setBackground(getActivity().getDrawable(R.drawable.ic_flash_off_black_24dp));
            }

        } else {
            relFlash.setVisibility(View.GONE);
        }
    }

    /**
     * Load the notificaiton data from database
     */
    private void loadData() {
        List<TableNotificationSms> SMSItems = smsDao.queryBuilder().orderDesc(TableNotificationSmsDao.Properties._date).build().list();
        setUpNotifications(SMSItems);
        // EventBus.getDefault().post(new TopBarUpdateEvent());
    }

    /**
     * Event bus notifier when new message or call comes.
     *
     * @param tableNotificationSms
     */
    @Subscribe
    public void newNotificationEvent(NewNotificationEvent tableNotificationSms) {
        System.out.println("NotificationFragment.newNotificationEvent" + tableNotificationSms);
        if (tableNotificationSms != null) {
            if (!checkNotificationExistsOrNot(tableNotificationSms.getTopTableNotificationSmsDao().getId())) {
                @SuppressLint("SimpleDateFormat")
                DateFormat sdf = new SimpleDateFormat("hh:mm a");
                String time = sdf.format(tableNotificationSms.getTopTableNotificationSmsDao().get_date());
                Notification n = new Notification(gettingNameAndImageFromPhoneNumber(tableNotificationSms.getTopTableNotificationSmsDao().get_contact_title()),
                        tableNotificationSms.getTopTableNotificationSmsDao().getId(), tableNotificationSms.getTopTableNotificationSmsDao().get_contact_title(),
                        tableNotificationSms.getTopTableNotificationSmsDao().get_message(), time, false, tableNotificationSms.getTopTableNotificationSmsDao().getNotification_type());
                notificationList.add(0, n);
                adapter.notifyDataSetChanged();


                if (notificationList.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    //  btnClearAll.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    textView_notification_title.setVisibility(View.GONE);

                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    //  btnClearAll.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                    textView_notification_title.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * Check to see whether the new notification data already exist in list or not.
     *
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

    /**
     * Filter the notification and hide and show message view based on the list size.
     *
     * @param items
     */
    private void setUpNotifications(List<TableNotificationSms> items) {
        notificationList.clear();
        for (int i = 0; i < items.size(); i++) {
            //DateFormat dateFormat = new SimpleDateFormat("hh:mm a");

            @SuppressLint("SimpleDateFormat") DateFormat sdf = new SimpleDateFormat("hh:mm a");
            String time = sdf.format(items.get(i).get_date());
            Notification n = new Notification(gettingNameAndImageFromPhoneNumber(items.get(i).get_contact_title()), items.get(i).getId(), items.get(i).get_contact_title(), items.get(i).get_message(), time, false, items.get(i).getNotification_type());
            notificationList.add(n);
        }

        if (items.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            //  btnClearAll.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            textView_notification_title.setVisibility(View.GONE);
        } else {
            adapter.notifyDataSetChanged();
            recyclerView.setVisibility(View.VISIBLE);
            //  btnClearAll.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            textView_notification_title.setVisibility(View.VISIBLE);
        }

    }

    /**
     * This method is used for fetch the user image from local content provider of contacts.
     *
     * @param number
     * @return
     */
    private NotificationContactModel gettingNameAndImageFromPhoneNumber(String number) {

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor cursor = getActivity().getContentResolver().query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
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

    final GestureDetector gesture = new GestureDetector(getActivity(),
            new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                       float velocityY) {
                    Log.i(TAG, "onFling has been called!");
                    final int SWIPE_MIN_DISTANCE = 30;
                    final int SWIPE_MAX_OFF_PATH = 250;
                    final int SWIPE_THRESHOLD_VELOCITY = 200;

                    try {
                        if (e1 != null && e2 != null) {
                            if (Math.abs(e1.getX() - e2.getX()) > SWIPE_MAX_OFF_PATH)
                                return false;
                            if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
                                    && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                                Log.i(TAG, "Down to Top");
                                animateOut();
                            } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
                                    && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                                Log.i(TAG, "Top to Down");
                            }
                        }
                    } catch (Exception e) {
                        // nothing
                        e.printStackTrace();
                    }
                    return super.onFling(e1, e2, velocityX, velocityY);
                }
            });

    /**
     * This is used for to decide if user reach the last item position in view.
     *
     * @param recyclerView
     * @return
     */
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

    /**
     * Below snippet is use to remove notification fragment
     */
    public void animateOut() {
        try {
            linSecond.setClickable(true);
            EventBus.getDefault().post(new NotificationTrayEvent(false));
            getActivity().getFragmentManager().beginTransaction().remove(NotificationFragment.this).commit();
            Config.isNotificationAlive = false;
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent("IsNotificationVisible").putExtra("IsNotificationVisible", false));
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        StatusBarHandler.isNotificationTrayVisible = false;
        try {
            wifiDataReceiver.unregister(context);
            if (bleSingal != null) getActivity().unregisterReceiver(bleSingal);
            if (audioChangeReceiver != null) getActivity().unregisterReceiver(audioChangeReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onConnectivityEvent(ConnectivityEvent event) {
        if (event.getState() == ConnectivityEvent.WIFI) {
            if (wifiManager.isWifiEnabled()) {
                imgWifi.setVisibility(View.VISIBLE);
                int level = event.getValue();
                if (level == 0) {
                    if (imgWifi != null) {
                        imgWifi.setBackground(getActivity().getDrawable(R.drawable.ic_wifi_0));
                    }
                } else if (level == 1) {
                    if (imgWifi != null) {
                        imgWifi.setBackground(getActivity().getDrawable(R.drawable.ic_wifi_1));
                    }
                } else if (level == 2) {
                    if (imgWifi != null) {
                        imgWifi.setBackground(getActivity().getDrawable(R.drawable.ic_wifi_2));
                    }
                } else if (level == 3) {
                    if (imgWifi != null) {
                        imgWifi.setBackground(getActivity().getDrawable(R.drawable.ic_wifi_3));
                    }
                } else if (level == 4) {
                    if (imgWifi != null) {
                        imgWifi.setBackground(getActivity().getDrawable(R.drawable.ic_wifi_4));
                    }
                }
            }
            if (event.getValue() == -1) {
                imgWifi.setBackground(getActivity().getDrawable(R.drawable.ic_signal_wifi_off_black_24dp));
            }
            checkMobileData();
        } else if (event.getState() == ConnectivityEvent.NETWORK) {
            if (!NetworkUtil.isAirplaneModeOn(getActivity())) {
                relMobileData.setEnabled(true);
                checkMobileData();
            }
        }
    }

    private void checkMobileData() {
        if (NetworkUtil.getConnectivityStatus(getActivity()) == NetworkUtil.TYPE_MOBILE) {
            imgData.setBackground(getActivity().getDrawable(R.drawable.ic_data_off_black_24dp));
        } else if (NetworkUtil.getConnectivityStatus(getActivity()) == NetworkUtil.TYPE_WIFI) {
            imgData.setBackground(getActivity().getDrawable(R.drawable.ic_data_on_black_24dp));
        } else if (NetworkUtil.getConnectivityStatus(getActivity()) == NetworkUtil.TYPE_NOT_CONNECTED) {
            imgData.setBackground(getActivity().getDrawable(R.drawable.ic_data_on_black_24dp));
        }
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

    @Override
    public void onResume() {
        super.onResume();
        statusOfQuickSettings();
        smsDao = DBUtility.getNotificationDao();
        callStorageDao = DBUtility.getCallStorageDao();
        loadData();
        try {
            if (getActivity() != null)
                UIUtils.hideSoftKeyboard(getActivity(), getActivity().getCurrentFocus().getWindowToken());
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }

    }

    /**
     * This method is called from adapter when there is no notification reamaning in list.
     *
     * @param event
     */
    @Subscribe
    public void notificationSwipeEvent(NotificationSwipeEvent event) {
        try {
            if (event.isNotificationListNull()) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                //    btnClearAll.setVisibility(View.GONE);
                textView_notification_title.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                //  btnClearAll.setVisibility(View.VISIBLE);
                textView_notification_title.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void swipeScreen(mSwipeDirection mSwipe) {
        //if(mSwipe == mSwipeDirection.UP)
        //finish();
    }

    /**
     * This is onclick listener for all the widget.
     *
     * @param view
     */
    @Click({R.id.relWifi, R.id.relBle, R.id.relMobileData, R.id.relDND, R.id.relAirPlane, R.id.relFlash, R.id.relBrightness})
    void clickListener(View view) {
        switch (view.getId()) {
            case R.id.relMobileData:
                try {
                    seekbarBrightness.setVisibility(View.GONE);
                    imgBrightness.setBackground(getActivity().getDrawable(R.drawable.ic_brightness_off_black_24dp));
                    removeStatusbar();
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity"));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.e(TAG, "Setting screen not found due to: " + e.fillInStackTrace());
                }
                break;
            case R.id.relWifi:
                seekbarBrightness.setVisibility(View.GONE);
                imgBrightness.setBackground(getActivity().getDrawable(R.drawable.ic_brightness_off_black_24dp));
                turnOnOffWIFI();
                break;
            case R.id.relBle:
                seekbarBrightness.setVisibility(View.GONE);
                imgBrightness.setBackground(getActivity().getDrawable(R.drawable.ic_brightness_off_black_24dp));
                if (BluetoothAdapter.getDefaultAdapter()!=null && BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                    BluetoothAdapter.getDefaultAdapter().disable();
                    EventBus.getDefault().post(new ConnectivityEvent(ConnectivityEvent.BLE, 0));
                } else {
                    if(BluetoothAdapter.getDefaultAdapter()!=null){
                    BluetoothAdapter.getDefaultAdapter().enable();
                    relBle.setEnabled(false);
                    imgBle.setBackground(getActivity().getDrawable(R.drawable.ic_bluetooth_searching_black_24dp));
                    EventBus.getDefault().post(new ConnectivityEvent(ConnectivityEvent.BLE, 1));}
                }
                break;
            case R.id.relDND:
                seekbarBrightness.setVisibility(View.GONE);
                imgBrightness.setBackground(getActivity().getDrawable(R.drawable.ic_brightness_off_black_24dp));
//                if (currentModeDeviceMode == AudioManager.RINGER_MODE_NORMAL) {
//                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
//                    imgDnd.setBackground(getActivity().getDrawable(R.drawable.ic_vibration_black_24dp));
//                } else if (currentModeDeviceMode == AudioManager.RINGER_MODE_VIBRATE) {
//                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//                    imgDnd.setBackground(getActivity().getDrawable(R.drawable.ic_do_not_disturb_on_black_24dp));
//                } else if (currentModeDeviceMode == AudioManager.RINGER_MODE_SILENT) {
//                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//                    imgDnd.setBackground(getActivity().getDrawable(R.drawable.ic_do_not_disturb_off_black_24dp));
//                }
                if (currentModeDeviceMode == 0) {
                    launcherPrefs.getCurrentProfile().put(1);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    imgDnd.setBackground(getActivity().getDrawable(R.drawable.ic_vibration_black_24dp));
                } else if (currentModeDeviceMode == 1) {
                    launcherPrefs.getCurrentProfile().put(2);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    imgDnd.setBackground(getActivity().getDrawable(R.drawable.ic_do_not_disturb_on_black_24dp));
                } else if (currentModeDeviceMode == 2) {
                    launcherPrefs.getCurrentProfile().put(0);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    imgDnd.setBackground(getActivity().getDrawable(R.drawable.ic_do_not_disturb_off_black_24dp));
                }
                currentModeDeviceMode = launcherPrefs.getCurrentProfile().get();
                EventBus.getDefault().post(new ConnectivityEvent(ConnectivityEvent.DND, 0));
                break;
            case R.id.relAirPlane:
                try {
                    // No root permission, just show the Airplane / Flight mode setting screen.
                    removeStatusbar();
                    seekbarBrightness.setVisibility(View.GONE);
                    imgBrightness.setBackground(getActivity().getDrawable(R.drawable.ic_brightness_off_black_24dp));
                    if (wifiManager.isWifiEnabled()) {
                        isWiFiOn = true;
                    }
                    Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(intent, 90);

                } catch (ActivityNotFoundException e) {
                    Log.e(TAG, "Setting screen not found due to: " + e.fillInStackTrace());
                }
                break;
            case R.id.relFlash:
                seekbarBrightness.setVisibility(View.GONE);
                imgBrightness.setBackground(getActivity().getDrawable(R.drawable.ic_brightness_off_black_24dp));
                if (StatusBarService.isFlashOn) {
                    EventBus.getDefault().post(new TorchOnOff(false));
                    imgFlash.setBackground(getActivity().getDrawable(R.drawable.ic_flash_off_black_24dp));
                } else {
                    EventBus.getDefault().post(new TorchOnOff(true));
                    imgFlash.setBackground(getActivity().getDrawable(R.drawable.ic_flash_on_black_24dp));
                }
                break;
            case R.id.relBrightness:
                if (checkSystemWritePermission()) {
                    if (seekbarBrightness.getVisibility() == View.VISIBLE) {
                        seekbarBrightness.setVisibility(View.GONE);
                        imgBrightness.setBackground(getActivity().getDrawable(R.drawable.ic_brightness_off_black_24dp));
                    } else {
                        seekbarBrightness.setVisibility(View.VISIBLE);
                        imgBrightness.setBackground(getActivity().getDrawable(R.drawable.ic_brightness_on_black_24dp));
                    }
                } else {
                    Intent intent = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        removeStatusbar();
                        intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                        startActivity(intent);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void removeStatusbar() {
        NotificationRetreat_.getInstance_(getActivity()).retreat();
        if (statusBarHandler != null) {
            Log.d(TAG, "LOAD STATUSBAR ::: RESTORE PREVENT");
            statusBarHandler.restoreStatusBarExpansion();
        }
    }

    /**
     * Turning On/Off WIFI
     */
    private void turnOnOffWIFI() {
        try {
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
                imgWifi.setBackground(getActivity().getDrawable(R.drawable.ic_wifi_0));
            } else {
                wifiManager.setWifiEnabled(false);
                imgWifi.setBackground(getActivity().getDrawable(R.drawable.ic_signal_wifi_off_black_24dp));
                EventBus.getDefault().post(new ConnectivityEvent(ConnectivityEvent.WIFI, -1));
            }
            if (!NetworkUtil.isAirplaneModeOn(getActivity())) {
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
            retVal = Settings.System.canWrite(getActivity());
        }
        return retVal;
    }

    public boolean isMobileDataEnable() {
        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
            final ConnectivityManager conman = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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


    /**
     * Broadcast Receiver for the Wifi single.
     */
    class WifiSingal extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (wifiManager.isWifiEnabled()) {
                WifiInfo info = wifiManager.getConnectionInfo();
                int level = WifiManager.calculateSignalLevel(info.getRssi(), 5);
                //  EventBus.getDefault().post(new ConnectivityEvent(ConnectivityEvent.WIFI, level));
                if (level == 0) {
                    if (imgWifi != null) {
                        imgWifi.setBackground(getActivity().getDrawable(R.drawable.ic_wifi_0));
                    }
                } else if (level == 1) {
                    if (imgWifi != null) {
                        imgWifi.setBackground(getActivity().getDrawable(R.drawable.ic_wifi_1));
                    }
                } else if (level == 2) {
                    if (imgWifi != null) {
                        imgWifi.setBackground(getActivity().getDrawable(R.drawable.ic_wifi_2));
                    }
                } else if (level == 3) {
                    if (imgWifi != null) {
                        imgWifi.setBackground(getActivity().getDrawable(R.drawable.ic_wifi_3));
                    }
                } else if (level == 4) {
                    if (imgWifi != null) {
                        imgWifi.setBackground(getActivity().getDrawable(R.drawable.ic_wifi_4));
                    }
                }
            }
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
                if (imgBle != null) {
                    switch (state) {
                        case BluetoothAdapter.STATE_OFF:
                            relBle.setEnabled(true);
                            EventBus.getDefault().post(new ConnectivityEvent(ConnectivityEvent.BLE, 0));
                            imgBle.setBackground(getActivity().getDrawable(R.drawable.ic_bluetooth_disabled_black_24dp));
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            imgBle.setBackground(getActivity().getDrawable(R.drawable.ic_bluetooth_searching_black_24dp));
                            break;
                        case BluetoothAdapter.STATE_ON:
                            relBle.setEnabled(true);
                            EventBus.getDefault().post(new ConnectivityEvent(ConnectivityEvent.BLE, 1));
                            imgBle.setBackground(getActivity().getDrawable(R.drawable.ic_bluetooth_on));
                            break;
                        case BluetoothAdapter.STATE_TURNING_ON:
                            imgBle.setBackground(getActivity().getDrawable(R.drawable.ic_bluetooth_searching_black_24dp));
                            break;
                    }
                }

            }
        }
    }

}