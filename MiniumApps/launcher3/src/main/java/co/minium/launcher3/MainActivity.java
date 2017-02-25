package co.minium.launcher3;

import android.Manifest;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;


import co.minium.launcher3.main.MainFragment_;
import co.minium.launcher3.main.MainSlidePagerAdapter;
import co.minium.launcher3.ui.PauseActivity_;
import co.minium.launcher3.ui.TempoActivity_;
import co.minium.launcher3.ui.TopFragment_;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.UIUtils;

import static minium.co.core.log.LogConfig.TRACE_TAG;

@Fullscreen
@EActivity(R.layout.activity_main)
public class MainActivity extends CoreActivity {

    @ViewById
    ViewPager pager;

    MainSlidePagerAdapter sliderAdapter;

    @Trace(tag = TRACE_TAG)
    @AfterViews
    void afterViews() {
        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission, app can not provide you the seamless integration.\n\nPlease consider turn on permissions at Setting > Permission")
                .setPermissions(Manifest.permission.READ_CONTACTS,
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();

        sliderAdapter = new MainSlidePagerAdapter(getFragmentManager());
        pager.setAdapter(sliderAdapter);

       // PauseActivity_.intent(this).start();
        TempoActivity_.intent(this).start();
    }

    private void loadViews() {
        loadTopBar();
        //loadMainView();
    }

    private void loadMainView() {
        loadFragment(MainFragment_.builder().build(), R.id.mainView, "main");
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
}
