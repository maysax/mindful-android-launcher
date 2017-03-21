package co.minium.launcher3;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Handler;
import android.support.v4.view.ViewPager;
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
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import co.minium.launcher3.event.NFCEvent;
import co.minium.launcher3.main.MainSlidePagerAdapter;

import co.minium.launcher3.notification.StatusBarHandler;
import co.minium.launcher3.pause.PauseActivity_;
import co.minium.launcher3.service.ApiClient_;
import co.minium.launcher3.sms.SmsObserver;
import co.minium.launcher3.token.TokenItemType;
import co.minium.launcher3.token.TokenManager;
import co.minium.launcher3.ui.TopFragment_;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import minium.co.core.event.CheckActivityEvent;
import minium.co.core.event.CheckVersionEvent;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
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



    private Handler nfcCheckHandler;

    @Trace(tag = TRACE_TAG)
    @AfterViews
    void afterViews() {
        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission, app can not provide you the seamless integration.\n\nPlease consider turn on permissions at Setting > Permission")
                .setPermissions(Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS,
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.SYSTEM_ALERT_WINDOW)
                .check();
        nfcCheckHandler = new Handler();
    }

    @Override
    protected void onResume() {
        super.onResume();
      //  if (nfcManager != null) nfcManager.onResume();



    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void loadViews() {
        statusBarHandler = new StatusBarHandler(this);
        statusBarHandler.requestStatusBarCustomization();
        loadTopBar();
        sliderAdapter = new MainSlidePagerAdapter(getFragmentManager());
        pager.setAdapter(sliderAdapter);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                try {
                    if(position == 1)
                        UIUtils.hideSoftKeyboard(MainActivity.this,getCurrentFocus().getWindowToken());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    private void loadTopBar() {
        loadFragment(TopFragment_.builder().build(), R.id.statusView, "status");
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            loadViews();
            checkVersion();
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
        ApiClient_.getInstance_(this).checkAppVersion();
    }

    @Subscribe
    public void checkVersionEvent(CheckVersionEvent event) {
        Tracer.d("Installed version: " + BuildConfig.VERSION_CODE + " Found: " + event.getVersion());
        if (event.getVersion() > BuildConfig.VERSION_CODE) {
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
                UIUtils.toast(this, "New version found! Downloading apk...");
                ApiClient_.getInstance_(this).downloadApk();
            } else {
                UIUtils.toast(this, "New version found! Skipping for now because of metered connection");
            }
        }
    }


    @Subscribe
    public void onCheckActivityEvent(CheckActivityEvent event){
        try {
            if(event.isResume())
                statusBarHandler.requestStatusBarCustomization();
            else
                statusBarHandler.restoreStatusBarExpansion();
        }catch (Exception e){
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
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.disableForegroundDispatch(this);
        try {
            statusBarHandler.restoreStatusBarExpansion();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {

            PauseActivity_.intent(this).start();

            final Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            nfcCheckHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Ndef ndef = Ndef.get(tag);
                    try {
                        ndef.connect();
                        Tracer.d("Connection heart-beat for nfc tag " + tag);
                        nfcCheckHandler.postDelayed(this, 1000);
                    } catch (IOException e) {
                        // if the tag is gone we might want to end the thread:
                        EventBus.getDefault().post(new NFCEvent(false));
                        Tracer.e(e, e.getMessage());
                        Tracer.d("Disconnected from nfc tag" + tag);
                        nfcCheckHandler.removeCallbacks(this);
                    } finally {
                        try {
                            ndef.close();
                        } catch (IOException e) {
                            Tracer.e(e, e.getMessage());
                        }
                    }
                }
            }, 1000);

        }
    }

    @Override
    public String nfcRead(Tag t) {
        return null;
    }

    @Override
    public String readText(NdefRecord record) throws UnsupportedEncodingException {
        return null;
    }

    @Override
    public void nfcReader(Tag tag) {

    }
}
