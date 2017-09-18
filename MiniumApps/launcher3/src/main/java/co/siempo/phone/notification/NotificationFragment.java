package co.siempo.phone.notification;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Camera;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;

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
import co.siempo.phone.event.NewNotificationEvent;
import co.siempo.phone.event.NotificationTrayEvent;
import co.siempo.phone.event.TopBarUpdateEvent;
import co.siempo.phone.main.SimpleItemTouchHelperCallback;
import co.siempo.phone.network.NetworkUtil;
import co.siempo.phone.notification.remove_notification_strategy.DeleteIteam;
import co.siempo.phone.notification.remove_notification_strategy.MultipleIteamDelete;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.CoreApplication;
import minium.co.core.config.Config;
import minium.co.core.event.HomePressEvent;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreFragment;
import minium.co.core.util.UIUtils;


@EFragment(R.layout.notification_main)
public class NotificationFragment extends CoreFragment implements View.OnTouchListener {

    private static final String TAG = "NotificationFragment";

    @ViewById
    RecyclerView recyclerView;

    @ViewById
    TextView emptyView;

    RecyclerListAdapter adapter;
    private List<Notification> notificationList;

    @ViewById
    RelativeLayout layout_notification, relWifi, relBle, relDND, relAirPlane, relFlash;

    @ViewById
    ImageView linSecond, imgWifi, imgBle, imgDnd, imgAirplane, imgFlash;

    @SystemService
    WifiManager wifiManager;

    WifiSingal wifiSingal;
    BleSingal bleSingal;
    int currentModeDeviceMode;

//    @SystemService
//    BluetoothAdapter bluetoothadapter;

    @SystemService
    AudioManager audioManager;


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

    @AfterViews
    void afterViews() {
        statusOfQuickSettings();
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
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", notificationList.get(position).getNumber(), null)));
                } else if (notificationList.get(position).getNotificationType() == NotificationUtility.NOTIFICATION_TYPE_CALL) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + notificationList.get(position).getNumber()));
                    startActivity(intent);
                }
                //++Tarun , Following code will delete all notification of same user and same types.
                DeleteIteam deleteIteam = new DeleteIteam(new MultipleIteamDelete());
                deleteIteam.executeDelete(notificationList.get(position));
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

    }

    private void statusOfQuickSettings() {
        if (!wifiManager.isWifiEnabled()) {
            imgWifi.setBackground(getActivity().getDrawable(R.drawable.ic_signal_wifi_off_black_24dp));
        } else {
            wifiSingal = new WifiSingal();
            getActivity().registerReceiver(wifiSingal, new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
            imgWifi.setBackground(getActivity().getDrawable(R.drawable.ic_wifi_0));
        }


        if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            imgBle.setBackground(getActivity().getDrawable(R.drawable.ic_bluetooth_on));
        } else {
            imgBle.setBackground(getActivity().getDrawable(R.drawable.ic_bluetooth_disabled_black_24dp));
            bleSingal = new BleSingal();
            getActivity().registerReceiver(bleSingal, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        }

        currentModeDeviceMode = audioManager.getMode();
        if (currentModeDeviceMode == AudioManager.RINGER_MODE_NORMAL) {
            imgDnd.setBackground(getActivity().getDrawable(R.drawable.ic_do_not_disturb_off_black_24dp));
        } else if (currentModeDeviceMode == AudioManager.RINGER_MODE_SILENT) {
            imgDnd.setBackground(getActivity().getDrawable(R.drawable.ic_do_not_disturb_on_black_24dp));
        }
//        else if (currentModeDeviceMode == AudioManager.RINGER_MODE_VIBRATE) {
//            imgDnd.setBackground(getActivity().getDrawable(R.drawable.ic_vibration_black_24dp));
//        }

//        if (NetworkUtil.isAirplaneModeOn(getActivity())) {
//            imgAirplane.setBackground(getActivity().getDrawable(R.drawable.ic_airplane));
//        } else {
//            imgAirplane.setBackground(getActivity().getDrawable(R.drawable.ic_airplanemode_inactive_black_24dp));
//
//        }
        checkFlashOn();
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
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    //  btnClearAll.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
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
        } else {
            adapter.notifyDataSetChanged();
            recyclerView.setVisibility(View.VISIBLE);
            //  btnClearAll.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

    }

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
        if (wifiSingal != null) getActivity().unregisterReceiver(wifiSingal);
        if (bleSingal != null) getActivity().unregisterReceiver(bleSingal);
    }

    @Override
    public void onResume() {
        super.onResume();
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

    @Override
    public void onPause() {
        super.onPause();

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
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                //  btnClearAll.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void swipeScreen(mSwipeDirection mSwipe) {
        //if(mSwipe == mSwipeDirection.UP)
        //finish();
    }


    @Click({R.id.relWifi, R.id.relBle, R.id.relDND, R.id.relAirPlane, R.id.relFlash})
    void clickListener(View view) {
        switch (view.getId()) {
            case R.id.relWifi:
                turnOnOffWIFI();
                break;
            case R.id.relBle:
                if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                    BluetoothAdapter.getDefaultAdapter().disable();
                } else {
                    BluetoothAdapter.getDefaultAdapter().enable();
                    bleSingal = new BleSingal();
                    getActivity().registerReceiver(bleSingal, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
                }
                break;
            case R.id.relDND:
                if (currentModeDeviceMode == AudioManager.RINGER_MODE_NORMAL) {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    imgDnd.setBackground(getActivity().getDrawable(R.drawable.ic_do_not_disturb_on_black_24dp));
                } else if (currentModeDeviceMode == AudioManager.RINGER_MODE_SILENT) {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    imgDnd.setBackground(getActivity().getDrawable(R.drawable.ic_do_not_disturb_off_black_24dp));
                }
                currentModeDeviceMode = audioManager.getMode();
                break;
            case R.id.relAirPlane:
                if (NetworkUtil.isAirplaneModeOn(getActivity())) {
                    imgAirplane.setBackground(getActivity().getDrawable(R.drawable.ic_airplanemode_inactive_black_24dp));
                    Settings.Global.putInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0);
                } else {
                    imgAirplane.setBackground(getActivity().getDrawable(R.drawable.ic_airplane));
                    Settings.Global.putInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 1);
                }

                break;
            case R.id.relFlash:
                if (CoreApplication.getInstance().getCamera()!=null &&
                        CoreApplication.getInstance().getParams().getFlashMode().equals("torch")) {
                    turnONOffFlash(false);
                    imgFlash.setBackground(getActivity().getDrawable(R.drawable.ic_flash_off_black_24dp));
                } else {
                    turnONOffFlash(true);
                    imgFlash.setBackground(getActivity().getDrawable(R.drawable.ic_flash_on_black_24dp));
                }
                break;

        }
    }

    private boolean statusDND() {
        boolean status = false;
        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
            status = true;
        }
        return status;
    }

    private void checkFlashOn() {
        CoreApplication.getInstance().getCameraInstance();
        boolean hasFlash = getActivity().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (hasFlash) {
            relFlash.setVisibility(View.VISIBLE);
            if (CoreApplication.getInstance().getCamera()!=null &&
                    CoreApplication.getInstance().getParams().getFlashMode().equals("torch")) {
                imgFlash.setBackground(getActivity().getDrawable(R.drawable.ic_flash_on_black_24dp));
            } else {
                imgFlash.setBackground(getActivity().getDrawable(R.drawable.ic_flash_off_black_24dp));
            }
        } else {
            relFlash.setVisibility(View.GONE);
        }

    }


    /**
     * Turning On/Off flash
     */
    private void turnONOffFlash(boolean isOnOFF) {
        CoreApplication.getInstance().setParams(CoreApplication.getInstance().getCamera().getParameters());
        if (!isOnOFF) {
            CoreApplication.getInstance().getParams().setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        } else {
            CoreApplication.getInstance().getParams().setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }
        CoreApplication.getInstance().getCamera().setParameters(CoreApplication.getInstance().getParams());
        CoreApplication.getInstance().getCamera().stopPreview();
    }


    /**
     * Turning On/Off WIFI
     */
    private void turnOnOffWIFI() {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
            wifiSingal = new WifiSingal();
            getActivity().registerReceiver(wifiSingal, new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
        } else {
            wifiManager.setWifiEnabled(false);
            imgWifi.setBackground(getActivity().getDrawable(R.drawable.ic_signal_wifi_off_black_24dp));
            if (wifiSingal != null) getActivity().unregisterReceiver(wifiSingal);

        }
    }


    private void initializeWiFiListener() {
        if (!wifiManager.isWifiEnabled()) {
            wifiSingal = new WifiSingal();
            getActivity().registerReceiver(wifiSingal, new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
        }
    }

    class WifiSingal extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (wifiManager.isWifiEnabled()) {
                WifiInfo info = wifiManager.getConnectionInfo();
                int level = WifiManager.calculateSignalLevel(info.getRssi(), 5);
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

    class BleSingal extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (imgBle != null) {
                    switch (state) {
                        case BluetoothAdapter.STATE_OFF:
                            imgBle.setBackground(getActivity().getDrawable(R.drawable.ic_bluetooth_disabled_black_24dp));
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            imgBle.setBackground(getActivity().getDrawable(R.drawable.ic_bluetooth_searching_black_24dp));
                            break;
                        case BluetoothAdapter.STATE_ON:
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
