package co.siempo.phone;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.KeyDown;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;

import co.siempo.phone.app.Launcher3Prefs_;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.notification.NotificationRetreat_;
import co.siempo.phone.receiver.AirplaneModeDataReceiver;
import co.siempo.phone.receiver.IDynamicStatus;
import co.siempo.phone.service.SiempoNotificationListener;
import co.siempo.phone.service.SiempoNotificationListener_;
import co.siempo.phone.util.PackageUtil;
import de.greenrobot.event.EventBus;
import minium.co.core.event.NFCEvent;
import co.siempo.phone.main.MainSlidePagerAdapter;

import co.siempo.phone.notification.StatusBarHandler;
import co.siempo.phone.pause.PauseActivity_;
import co.siempo.phone.service.ApiClient_;
import co.siempo.phone.msg.SmsObserver;
import co.siempo.phone.token.TokenItemType;
import co.siempo.phone.token.TokenManager;
import co.siempo.phone.ui.TopFragment_;
import de.greenrobot.event.Subscribe;
import minium.co.core.event.CheckActivityEvent;
import minium.co.core.event.CheckVersionEvent;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.ServiceUtils;
import minium.co.core.util.UIUtils;

import static minium.co.core.log.LogConfig.TRACE_TAG;

@Fullscreen
@EActivity(R.layout.activity_main)
public class MainActivity extends CoreActivity implements SmsObserver.OnSmsSentListener {

    private static final String TAG = "MainActivity";


    @ViewById
    ViewPager pager;

    MainSlidePagerAdapter sliderAdapter;


    StatusBarHandler statusBarHandler;

    @Bean
    TokenManager manager;

    @SystemService
    ConnectivityManager connectivityManager;

    @SystemService
    NotificationManager notificationManager;

    @Pref
    Launcher3Prefs_ launcherPrefs;

    IDynamicStatus airplaneModeDataReceiver;

    @Trace(tag = TRACE_TAG)
    @AfterViews
    void afterViews() {
        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission, app can not provide you the seamless integration.\n\nPlease consider turn on permissions at Setting > Permission")
                .setPermissions(Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.RECEIVE_MMS,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.SYSTEM_ALERT_WINDOW)
                .check();

        if (!isEnabled(this)) {
            UIUtils.confirm(this, "Siempo Notification service is not enabled. Please allow Siempo to access notification service", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                }
            });
        }

        // broadcast reciever for taking over volume key

        final BroadcastReceiver vReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //your code here
                System.out.println("Volume key pressed");
            }
        };

        registerReceiver(vReceiver, new IntentFilter("android.media.VOLUME_CHANGED_ACTION"));

        // NotificationBlockerService_.intent(this).extra("start", true).start();

        FirebaseHelper firebaseHelper = new FirebaseHelper(this);
        firebaseHelper.testEvent1();
        firebaseHelper.testEvent2();

        launcherPrefs.updatePrompt().put(true);
    }

    @UiThread(delay = 500)
    void loadViews() {
        statusBarHandler = new StatusBarHandler(this);
        statusBarHandler.requestStatusBarCustomization();

        sliderAdapter = new MainSlidePagerAdapter(getFragmentManager());
        pager.setAdapter(sliderAdapter);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                try {
                    if (position == 1)
                        UIUtils.hideSoftKeyboard(MainActivity.this, getCurrentFocus().getWindowToken());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        loadTopBar();
    }

    private void loadTopBar() {
        loadFragment(TopFragment_.builder().build(), R.id.statusView, "status");
    }

    /**
     * @return True if {@link android.service.notification.NotificationListenerService} is enabled.
     */
    public static boolean isEnabled(Context mContext) {
        return ServiceUtils.isNotificationListenerServiceRunning(mContext, SiempoNotificationListener_.class);
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            loadViews();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            UIUtils.toast(MainActivity.this, "Permission denied");
        }
    };

    @KeyDown(KeyEvent.KEYCODE_VOLUME_UP)
    void volumeUpPressed() {
        Tracer.i("Volume up pressed in MainActivity");
        PauseActivity_.intent(this).start();
    }


    void checkVersion() {
        Tracer.d("Checking if new version is available ... ");
        ApiClient_.getInstance_(this).checkAppVersion();
    }

    @Subscribe
    public void checkVersionEvent(CheckVersionEvent event) {
        Tracer.d("Installed version: " + BuildConfig.VERSION_CODE + " Found: " + event.getVersion());
        if (event.getVersion() > BuildConfig.VERSION_CODE) {
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
                // UIUtils.toast(this, "New version found! Downloading apk...");
                UIUtils.confirm(this, "New version found! Would you like to update Siempo?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            launcherPrefs.updatePrompt().put(false);
                            new ActivityHelper(MainActivity.this).openBecomeATester();
                        }
                    }
                });
                // ApiClient_.getInstance_(this).downloadApk();
            } else {
                UIUtils.toast(this, "New version found! Skipping for now because of metered connection");
            }
        }
    }


    @Subscribe
    public void onCheckActivityEvent(CheckActivityEvent event) {
        try {
            if (event.isResume())
                statusBarHandler.requestStatusBarCustomization();
            else
                statusBarHandler.restoreStatusBarExpansion();
        } catch (Exception e) {
            System.out.println(TAG + " exception caught on onCheckActivityEvent  " + e.getMessage());
        }


    }

    @Override
    public void onSmsSent(int threadId) {
        try {
            Intent defineIntent = new Intent(Intent.ACTION_VIEW);
//          defineIntent.setData(Uri.parse("content://mms-sms/conversations/"+threadId));
            defineIntent.setData(Uri.parse("smsto:" + manager.get(TokenItemType.CONTACT).getExtra2()));
//          defineIntent.setType("vnd.android-dir/mms-sms");
//          defineIntent.addCategory(Intent.CATEGORY_DEFAULT);

            startActivity(defineIntent);
            manager.clear();
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            statusBarHandler.restoreStatusBarExpansion();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (launcherPrefs.updatePrompt().get())
            checkVersion();

        airplaneModeDataReceiver = new AirplaneModeDataReceiver();
        airplaneModeDataReceiver.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        NotificationRetreat_.getInstance_(this.getApplicationContext()).retreat();
        try {
            statusBarHandler.restoreStatusBarExpansion();
        } catch (Exception e) {
            e.printStackTrace();
        }

        airplaneModeDataReceiver.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            enableNfc(true);
        } catch (Exception e) {
            Tracer.e(e);
        }
        // prevent keyboard up on old menu screen when coming back from other launcher
        if (pager != null) pager.setCurrentItem(0, true);
        if (statusBarHandler != null && !statusBarHandler.isActive())
            statusBarHandler.requestStatusBarCustomization();

    }

    @Override
    protected void onPause() {
        super.onPause();
        enableNfc(false);
        Log.i("onPause","MainActivity");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            Tracer.i("NFC Tag detected");
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            EventBus.getDefault().post(new NFCEvent(true, tag));
        }
    }

    private void enableNfc(boolean isEnable) {
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter != null) {
            if (isEnable) {
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                        getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
                IntentFilter filter = new IntentFilter();
                filter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
                filter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
                filter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
                nfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[]{filter}, techList);
            } else {
                nfcAdapter.disableForegroundDispatch(this);
            }
        }
    }

    @Subscribe
    public void nfcEvent(NFCEvent event) {
        if (event.isConnected()) {
            PauseActivity_.intent(this).tag(event.getTag()).start();
        }
    }

    @Override
    public void onBackPressed() {

    }
}
