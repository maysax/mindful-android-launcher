package co.minium.launcher3;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.KeyDown;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import co.minium.launcher3.main.MainSlidePagerAdapter;
import co.minium.launcher3.notification.StatusBarHandler;
import co.minium.launcher3.pause.PauseActivity_;
import co.minium.launcher3.sms.SmsObserver;
import co.minium.launcher3.token.TokenItemType;
import co.minium.launcher3.token.TokenManager;
import co.minium.launcher3.ui.TopFragment_;
import de.greenrobot.event.Subscribe;
import minium.co.core.event.CheckActivityEvent;
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
                if(position == 1)
                    UIUtils.hideSoftKeyboard(MainActivity.this,getCurrentFocus().getWindowToken());
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
            //checkVersion();
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
        Intent defineIntent = new Intent(Intent.ACTION_VIEW);
//        defineIntent.setData(Uri.parse("content://mms-sms/conversations/"+threadId));
        defineIntent.setData(Uri.parse("smsto:" + manager.get(TokenItemType.CONTACT).getExtra2()));
//        defineIntent.setType("vnd.android-dir/mms-sms");
//        defineIntent.addCategory(Intent.CATEGORY_DEFAULT);
        try {
             startActivity(defineIntent);
            manager.clear();
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
            UIUtils.alert(this, "Minium-messages app not found.");
        }
    }



}
