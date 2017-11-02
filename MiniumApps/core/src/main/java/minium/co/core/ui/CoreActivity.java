package minium.co.core.ui;

import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.File;
import java.io.UnsupportedEncodingException;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import minium.co.core.R;
import minium.co.core.app.CoreApplication;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.app.HomeWatcher;
import minium.co.core.config.Config;
import minium.co.core.event.DownloadApkEvent;
import minium.co.core.event.HomePressEvent;
import minium.co.core.helper.Validate;
import minium.co.core.log.Tracer;
import minium.co.core.util.ActiveActivitiesTracker;
import minium.co.core.util.UIUtils;

/**
 * This activity will be the base activity
 * All activity of all the modules should extend this activity
 * <p>
 * Created by shahab on 3/17/16.
 */

@EActivity
@Fullscreen
public abstract class CoreActivity extends AppCompatActivity implements NFCInterface {


    int onStartCount = 0;
    public int currentIndex = 0;
    public HomeWatcher mHomeWatcher;

    @Pref
    public DroidPrefs_ prefs;

    @SystemService
    protected ActivityManager activityManager;
    public View mTestView = null;
    public WindowManager windowManager = null;
    private boolean isOnStopCalled = false;
    UserPresentBroadcastReceiver userPresentBroadcastReceiver;
    public static File localPath, backupPath;
    // Static method to return File at localPath
    public static File getLocalPath() {
        return localPath;
    }

    // Static method to return File at backupPath
    public static File getBackupPath() {
        return backupPath;
    }




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //onCreateAnimation(savedInstanceState);
        windowManager = (WindowManager) getBaseContext().getSystemService(Context.WINDOW_SERVICE);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        userPresentBroadcastReceiver = new UserPresentBroadcastReceiver();
        registerReceiver(userPresentBroadcastReceiver, intentFilter);

        if (prefs != null && prefs.selectedThemeId().get() != 0) {
            setTheme(prefs.selectedThemeId().get());
        }
        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                UIUtils.hideSoftKeyboard(CoreActivity.this, getWindow().getDecorView().getWindowToken());
//                if (CoreApplication.getInstance().isEditNotOpen()) {
//                    EventBus.getDefault().post(new HomePressEvent(true));
//                } else {
                    EventBus.getDefault().post(new HomePressEvent(true));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                                if (!isOnStopCalled && !UIUtils.isMyLauncherDefault(CoreActivity.this))
                                    loadDialog();
                            } else {
                                if (!isOnStopCalled && !UIUtils.isMyLauncherDefault(CoreActivity.this))
                                    if (Settings.canDrawOverlays(CoreActivity.this)) {
                                        loadDialog();
                                    }
                            }
                        }
                    }, 1000);
//                }
            }

            @Override
            public void onHomeLongPressed() {
            }
        });
        mHomeWatcher.startWatch();

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mHomeWatcher != null) mHomeWatcher.startWatch();
        isOnStopCalled = false;
        CoreApplication.getInstance().restoreDefaultApplication();
    }

    /**
     * This BroadcastReceiver is included for the when user press home button and lock the screen.
     * when it comes back we have to show launcher dialog,toottip window.
     */
    public class UserPresentBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                if (mTestView != null && mTestView.getVisibility() == View.INVISIBLE) {
                    //if (Build.MANUFACTURER.equalsIgnoreCase("Samsung")) {
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                    //  }
                    mTestView.setVisibility(View.VISIBLE);
                } else {
                    if (mTestView != null)
                        mTestView.setVisibility(View.VISIBLE);
                }
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                if (mTestView != null) mTestView.setVisibility(View.INVISIBLE);
            }
        }

    }

    public void loadDialog() {
        if (mTestView == null) {
            WindowManager.LayoutParams layoutParams;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            } else {
                //noinspection deprecation
                layoutParams = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
            }
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.format = PixelFormat.RGBA_8888;
            layoutParams.gravity = Gravity.TOP | Gravity.START;
            layoutParams.flags =
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                            | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
            mTestView = View.inflate(CoreActivity.this, R.layout.tooltip_launcher, null);
            if (currentIndex == 0) {
                mTestView.findViewById(R.id.linSiempoApp).setVisibility(View.VISIBLE);
                mTestView.findViewById(R.id.linDefaultApp).setVisibility(View.GONE);
                mTestView.findViewById(R.id.txtTitle).setVisibility(View.VISIBLE);
            } else {
                mTestView.findViewById(R.id.linSiempoApp).setVisibility(View.GONE);
                mTestView.findViewById(R.id.linDefaultApp).setVisibility(View.VISIBLE);
                mTestView.findViewById(R.id.txtTitle).setVisibility(View.GONE);
            }
            //Must wire up back button, otherwise it's not sent to our activity
            mTestView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (mTestView != null)
                            windowManager.removeView(mTestView);
                        mTestView = null;
                        onBackPressed();
                    }
                    return true;
                }
            });
            mTestView.findViewById(R.id.linSecond).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTestView != null)
                        windowManager.removeView(mTestView);
                    mTestView = null;
                }
            });

            mTestView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTestView != null)
                        windowManager.removeView(mTestView);
                    mTestView = null;
                }
            });
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                if (Settings.canDrawOverlays(this)) {
                    windowManager.addView(mTestView, layoutParams);
                }
            } else {
                windowManager.addView(mTestView, layoutParams);
            }

        }

    }

    private void onCreateAnimation(Bundle savedInstanceState) {
        onStartCount = 1;
        if (savedInstanceState == null) {
            this.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
        } else {
            onStartCount = 2;
        }
    }

    private void onStartAnimation() {
        if (onStartCount > 1) {
            this.overridePendingTransition(R.anim.anim_slide_in_right,
                    R.anim.anim_slide_out_right);
        } else if (onStartCount == 1) {
            onStartCount++;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        ActiveActivitiesTracker.activityStarted();
        //onStartAnimation();
    }

    @Override
    protected void onStop() {
        ActiveActivitiesTracker.activityStopped();
        EventBus.getDefault().unregister(this);
        try {
            if (Config.isNotificationAlive) {
                //EventBus.getDefault().post(new NotificationTrayEvent(false));
                //this.getFragmentManager().beginTransaction().remove(NotificationFragment.this).commit();
                getFragmentManager().beginTransaction().
                        remove(getFragmentManager().findFragmentById(R.id.mainView)).commit();
                Config.isNotificationAlive = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        isOnStopCalled = true;
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mHomeWatcher != null) mHomeWatcher.stopWatch();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userPresentBroadcastReceiver != null) {
            unregisterReceiver(userPresentBroadcastReceiver);
        }
    }

    /**
     * Load fragment by replacing all previous fragments
     *
     * @param fragment
     */
    public void loadFragment(Fragment fragment, int containerViewId, String tag) {
        try {
            FragmentManager fragmentManager = getFragmentManager();
            // clear back stack
            for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
                fragmentManager.popBackStack();
            }
            FragmentTransaction t = fragmentManager.beginTransaction();
            t.replace(containerViewId, fragment, tag);
            fragmentManager.popBackStack();
            t.commitAllowingStateLoss();
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }
    }

    /**
     * Load Fragment on top of other fragments
     *
     * @param fragment
     */
    public void loadChildFragment(Fragment fragment, int containerViewId) {
        Validate.notNull(fragment);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(containerViewId, fragment, "main")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                handleBackPress();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void genericEvent(Object event) {
        // DO NOT code here, it is a generic catch event method
    }

    @Override
    public void onBackPressed() {
        handleBackPress();
    }

    private void handleBackPress() {
        try {
            if (getFragmentManager().getBackStackEntryCount() == 0) {
                this.finish();
            } else {
                getFragmentManager().popBackStack();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void downloadApkEvent(DownloadApkEvent event) {
        try {
            Intent installIntent = new Intent(Intent.ACTION_VIEW);
            installIntent.setDataAndType(Uri.fromFile(new File(event.getPath())),
                    "application/vnd.android.package-archive");
            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(installIntent);
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
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