package co.siempo.phone;

import android.Manifest;
import android.app.Fragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;
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
import co.siempo.phone.main.MainSlidePagerAdapter;
import co.siempo.phone.msg.SmsObserver;
import co.siempo.phone.notification.NotificationFragment;
import co.siempo.phone.notification.NotificationRetreat_;
import co.siempo.phone.notification.StatusBarHandler;
import co.siempo.phone.pause.PauseActivity_;
import co.siempo.phone.service.ApiClient_;
import co.siempo.phone.service.SiempoNotificationListener_;
import co.siempo.phone.token.TokenManager;
import co.siempo.phone.ui.TopFragment_;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import minium.co.core.event.CheckActivityEvent;
import minium.co.core.event.CheckVersionEvent;
import minium.co.core.event.HomePressEvent;
import minium.co.core.event.NFCEvent;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.ServiceUtils;
import minium.co.core.util.UIUtils;
import com.github.javiersantos.appupdater.enums.Display;
import static minium.co.core.log.LogConfig.TRACE_TAG;

@Fullscreen
@EActivity(R.layout.activity_main)
public class MainActivity extends CoreActivity implements SmsObserver.OnSmsSentListener {


    private static final String TAG = "MainActivity";

    public static int currentItem = 0;
    @ViewById
    ViewPager pager;

    MainSlidePagerAdapter sliderAdapter;

    public StatusBarHandler statusBarHandler;

    @Bean
    TokenManager manager;

    @SystemService
    ConnectivityManager connectivityManager;

    @SystemService
    NotificationManager notificationManager;

    @Pref
    Launcher3Prefs_ launcherPrefs;

    public static String isTextLenghGreater = "";

    private ActivityState state;

    private enum ActivityState {
        NORMAL,
        ONHOMEPRESS
    }

    @Trace(tag = TRACE_TAG)
    @AfterViews
    void afterViews() {
        Log.d(TAG,"afterViews event called");
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
            UIUtils.confirmWithCancel(this, null, "Siempo Notification service is not enabled. Please allow Siempo to access notification service", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == -2) {
                        checkAppLoadFirstTime();
                    } else {
                        startActivityForResult(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"), 100);
                    }

                }
            });
        }

        FirebaseHelper firebaseHelper = new FirebaseHelper(this);
        firebaseHelper.testEvent1();
        firebaseHelper.testEvent2();

        launcherPrefs.updatePrompt().put(true);
    }

    private void checkAppLoadFirstTime() {
        if (launcherPrefs.isAppInstalledFirstTime().get()) {
            launcherPrefs.isAppInstalledFirstTime().put(false);
            ActivityHelper activityHelper = new ActivityHelper(MainActivity.this);
            if (!UIUtils.isMyLauncherDefault(MainActivity.this)) {
                activityHelper.handleDefaultLauncher(MainActivity.this);
                loadDialog();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            checkAppLoadFirstTime();
        }
    }

    @UiThread(delay = 500)
    void loadViews() {
        sliderAdapter = new MainSlidePagerAdapter(getFragmentManager());
        pager.setAdapter(sliderAdapter);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentItem = position;
              //  currentIndex = currentItem;
                try {
                    if (position == 1)
                        //noinspection ConstantConditions
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
            Log.d(TAG,"Permission granted");
            loadViews();
            loadStatusBar();
            if (!launcherPrefs.isAppInstalledFirstTime().get()) {
                Log.d(TAG,"Display upgrade dialog.");
                checkUpgradeVersion();
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            UIUtils.toast(MainActivity.this, "Permission denied");
        }
    };


    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
    }


    @KeyDown(KeyEvent.KEYCODE_VOLUME_UP)
    void volumeUpPressed() {
        Tracer.i("Volume up pressed in MainActivity");
        PauseActivity_.intent(this).start();
    }

    void checkVersion() {
        Tracer.d("Checking if new version is available ... ");
        ApiClient_.getInstance_(this).checkAppVersion();
    }

    @SuppressWarnings("ConstantConditions")
    @Subscribe
    public void homePressEvent(HomePressEvent event) {
        Log.d(TAG,"ACTION HOME PRESS");
        state=ActivityState.ONHOMEPRESS;
        if (event.isVisible()) {
            if (StatusBarHandler.isNotificationTrayVisible) {

                Fragment f = getFragmentManager().findFragmentById(R.id.mainView);
                if (f instanceof NotificationFragment)
                {
                    StatusBarHandler.isNotificationTrayVisible = false;
                    ((NotificationFragment) f).animateOut();

                }
            }
        }
    }

    @UiThread(delay = 1000)
    void loadStatusBar() {
        statusBarHandler = new StatusBarHandler(MainActivity.this);
        if (statusBarHandler != null && !statusBarHandler.isActive()) {
            Log.d(TAG, "LOAD STATUSBAR ::: ACTION PREVENT");
            statusBarHandler.requestStatusBarCustomization();
        }
    }
    @Subscribe
    public void checkVersionEvent(CheckVersionEvent event) {
        Log.d(TAG,"Check Version event...");
        Tracer.d("Installed version: " + BuildConfig.VERSION_CODE + " Found: " + event.getVersion());
        if (event.getVersion() > BuildConfig.VERSION_CODE) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            if (activeNetwork != null) { // connected to the internet
                    UIUtils.confirm(this, "New version found! Would you like to update Siempo?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                launcherPrefs.updatePrompt().put(false);
                                new ActivityHelper(MainActivity.this).openBecomeATester();
                            }
                        }
                    });
            } else {
                Log.d(TAG, getString(R.string.nointernetconnection));
            }
        }
    }


    @Subscribe
    public void onCheckActivityEvent(CheckActivityEvent event) {
        /**
         *  It will use further to maintain custom siempo flow.
         */

//        try {
//
//            if (event.isResume()) {
//                if (statusBarHandler != null && !statusBarHandler.isActive()) {
//                    statusBarHandler.requestStatusBarCustomization();
//                }
//            }else {
//
//                if (statusBarHandler != null) {
//                    statusBarHandler.restoreStatusBarExpansion();
//                }
//            }
//            } catch (Exception e) {
//            System.out.println(TAG + " exception caught on onCheckActivityEvent  " + e.getMessage());
//        }


    }


    @Override
    public void onSmsSent(int threadId) {
//        try {
// Don't remove this code,this code is needed for feature refrence.
//            UIUtils.hideSoftKeyboard(MainActivity.this,getWindow().getDecorView().getWindowToken());
//            Intent defineIntent = new Intent(Intent.ACTION_VIEW);
//            defineIntent.setData(Uri.parse("content://mms-sms/conversations/"+threadId));
//            defineIntent.setData(Uri.parse("smsto:" + manager.get(TokenItemType.CONTACT).getExtra2()));
//            defineIntent.setType("vnd.android-dir/mms-sms");
//            defineIntent.addCategory(Intent.CATEGORY_DEFAULT);
//            startActivity(defineIntent);
             //manager.clear();
//        } catch (Exception e) {
//            Tracer.e(e, e.getMessage());
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.isTextLenghGreater = "";
        try {
            Log.d(TAG,"DESTROY ::: ACTION RESTORE");
            if(statusBarHandler!=null) {
                statusBarHandler.restoreStatusBarExpansion();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SuppressWarnings("deprecation")
    @Override
    protected void onStart() {
        Log.d(TAG,"onStart Event Call : "+state);
        super.onStart();

        if(state==ActivityState.ONHOMEPRESS){
            checkUpgradeVersion();
            state=ActivityState.NORMAL;
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        //currentIndex = 0;

        NotificationRetreat_.getInstance_(this.getApplicationContext()).retreat();
        if(statusBarHandler!=null){
            try{
                Log.d(TAG,"ONSTOP ::: ACTION RESTORE");
                statusBarHandler.restoreStatusBarExpansion();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        if (pager != null) pager.setCurrentItem(currentItem, true);
      //  currentIndex = currentItem;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"ACTION ONPAUSE");
        enableNfc(false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        currentItem = 0;
        Log.d(TAG,"ACTION onNewIntent");
        if (intent.getAction() != null && intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            Tracer.i("NFC Tag detected");
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            EventBus.getDefault().post(new NFCEvent(true, tag));
        }
        loadStatusBar();
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
        try{
        if (StatusBarHandler.isNotificationTrayVisible) {
            Log.d(TAG,"onBackPressed");
            Fragment f = getFragmentManager().findFragmentById(R.id.mainView);
            if (f!=null && f instanceof NotificationFragment)
            {
                StatusBarHandler.isNotificationTrayVisible = false;
                //noinspection ConstantConditions
                ((NotificationFragment) f).animateOut();

                }
            } else if (pager.getCurrentItem() == 1) {
                pager.setCurrentItem(0);
            }
        }
        catch (Exception e){
            Log.d(TAG,"Exception"+e.toString());
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG,"RESTART ::: ACTION RESTART");
        loadStatusBar();
    }

    public void checkVersionFromAppUpdater(){
        new AppUpdater(this)
                .setDisplay(Display.DIALOG)
                .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                .showEvery(5)
                .setTitleOnUpdateAvailable("Update available")
                .setContentOnUpdateAvailable("New version found! Would you like to update Siempo?")
                .setTitleOnUpdateNotAvailable("Update not available")
                .setContentOnUpdateNotAvailable("No update available. Check for updates again later!")
                .setButtonUpdate("Update")
                .setButtonDismiss("Maybe later")
                .start();
    }

    /**
     * Below function is use to check if latest version is available from play store or not
     * 1) It will check first with Appupdater library if it fails to identify then
     * 2) It will check with AWS logic.
     */
    public  void checkUpgradeVersion(){
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null) {
            Log.d(TAG,"Active network..");
            AppUpdaterUtils appUpdaterUtils = new AppUpdaterUtils(this)
                    .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                    .withListener(new AppUpdaterUtils.UpdateListener() {
                        @Override
                        public void onSuccess(Update update, Boolean isUpdateAvailable) {
                            Log.d(TAG,"on success");
                            if (update.getLatestVersionCode() != null) {
                                Log.d(TAG,"check version from AppUpdater library");
                                checkVersionFromAppUpdater();
                            } else {
                                Log.d(TAG,"check version from AWS");
                                ApiClient_.getInstance_(MainActivity.this).checkAppVersion();
                            }
                        }

                        @Override
                        public void onFailed(AppUpdaterError error) {
                            Log.d(TAG," AppUpdater Error ::: "+error.toString());

                        }
                    });

            appUpdaterUtils.start();
        } else {
            Log.d(TAG, getString(R.string.nointernetconnection));
        }
    }


}