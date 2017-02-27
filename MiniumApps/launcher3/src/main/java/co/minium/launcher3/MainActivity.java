package co.minium.launcher3;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.KeyDown;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import co.minium.launcher3.main.MainSlidePagerAdapter;
import co.minium.launcher3.notification.NotificationActivity;
import co.minium.launcher3.ui.PauseActivity_;
import co.minium.launcher3.ui.TopFragment_;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.UIUtils;

import static minium.co.core.log.LogConfig.TRACE_TAG;

@Fullscreen
@EActivity(R.layout.activity_main)
public class MainActivity extends CoreActivity {

    public static boolean isNotificationTrayVisible = false;

    protected static customViewGroup blockingView = null;

    private static final String TAG = "MainActivity";

    private int status_bar_height = 0;

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
        requestStatusBarCustomization();
        loadTopBar();
        sliderAdapter = new MainSlidePagerAdapter(getFragmentManager());
        pager.setAdapter(sliderAdapter);
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



    @Override
    protected void onResume() {
        super.onResume();
    }


    private void requestStatusBarCustomization(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "User can access system settings without this permission!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                preventStatusBarExpansion(this);
            }
        }
    }

    // preventStatusBarExpansion

    private void preventStatusBarExpansion(Context context) {
        WindowManager manager = ((WindowManager) context.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE));

        Activity activity = (Activity)context;
        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|

                // this is to enable the notification to recieve touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        int resId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int result = 0;
        if (resId > 0) {
            result = activity.getResources().getDimensionPixelSize(resId);
        }

        status_bar_height = result;

        localLayoutParams.height = result;

        localLayoutParams.format = PixelFormat.TRANSPARENT;

        blockingView = new customViewGroup(context);

        manager.addView(blockingView, localLayoutParams);
    }

    private class customViewGroup extends ViewGroup {


        private boolean mIsScrolling;


        public customViewGroup(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
        }


        @Override
        public boolean onTouchEvent(MotionEvent event) {

            if(event.getY() > status_bar_height){
                if(!isNotificationTrayVisible)
                {
                    System.out.println("y position on Touch on notification tray "+ event.getY() + "status_bar_height " + status_bar_height);
                    Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
                    startActivity(intent);

                    isNotificationTrayVisible = true;
                }
            }


            return super.onTouchEvent(event);
        }

    }

    /*
    Added so that when not in launcher it allow status bar to default state
     */

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (blockingView!=null) {
            WindowManager manager = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
            manager.removeView(blockingView);
        }
    }




}
