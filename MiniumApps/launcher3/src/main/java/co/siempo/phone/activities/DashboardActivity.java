package co.siempo.phone.activities;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.javiersantos.appupdater.AppUpdaterUtils;

import co.siempo.phone.BuildConfig;
import co.siempo.phone.R;
import co.siempo.phone.adapters.DashboardPagerAdapter;
import co.siempo.phone.event.CheckVersionEvent;
import co.siempo.phone.event.HomePress;
import co.siempo.phone.event.OnBackPressedEvent;
import co.siempo.phone.fragments.FavoritePaneFragment;
import co.siempo.phone.fragments.IntentionFragment;
import co.siempo.phone.fragments.JunkFoodPaneFragment;
import co.siempo.phone.fragments.PaneFragment;
import co.siempo.phone.fragments.ToolsPaneFragment;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.log.Tracer;
import co.siempo.phone.service.ApiClient_;
import co.siempo.phone.service.LoadFavoritePane;
import co.siempo.phone.service.LoadJunkFoodPane;
import co.siempo.phone.service.LoadToolPane;
import co.siempo.phone.service.SiempoNotificationListener_;
import co.siempo.phone.utils.PermissionUtil;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.UIUtils;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class DashboardActivity extends CoreActivity {

    public static final String IS_FROM_HOME = "isFromHome";
    public static String isTextLenghGreater = "";
    public static boolean isJunkFoodOpen = false;
    public static int currentIndexDashboard = 1;
    public static int currentIndexPaneFragment = -1;
    public static long startTime = 0;
    public static int defaultStatusBarColor;
    PermissionUtil permissionUtil;
    ConnectivityManager connectivityManager;
    AppUpdaterUtils appUpdaterUtils;
    boolean isApplicationLaunch = false;
    NotificationManager notificationManager;
    private Window mWindow;
    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;
    private String TAG = "DashboardActivity";
    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private DashboardPagerAdapter mPagerAdapter;
    private AlertDialog notificationDialog;

    /**
     * @return True if {@link android.service.notification.NotificationListenerService} is enabled.
     */
    public static boolean isEnabled(Context mContext) {

        ComponentName cn = new ComponentName(mContext, SiempoNotificationListener_.class);
        String flat = Settings.Secure.getString(mContext.getContentResolver(), "enabled_notification_listeners");
        return flat != null && flat.contains(cn.flattenToString());

        //return ServiceUtils.isNotificationListenerServiceRunning(mContext, SiempoNotificationListener_.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        permissionUtil = new PermissionUtil(this);
        if (!permissionUtil.hasGiven(PermissionUtil.CONTACT_PERMISSION)
                || !permissionUtil.hasGiven(PermissionUtil.CALL_PHONE_PERMISSION) || !permissionUtil.hasGiven(PermissionUtil.SEND_SMS_PERMISSION)
                || !permissionUtil.hasGiven(PermissionUtil.WRITE_EXTERNAL_STORAGE_PERMISSION)
                || !permissionUtil.hasGiven(PermissionUtil.NOTIFICATION_ACCESS) || !permissionUtil.hasGiven(PermissionUtil.DRAWING_OVER_OTHER_APPS)
                ) {
            Intent intent = new Intent(DashboardActivity.this, SiempoPermissionActivity_
                    .class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra(IS_FROM_HOME, true);
            startActivity(intent);
        } else {

        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        loadViews();
        Log.d("Test", "P1");
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }
        mWindow = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        defaultStatusBarColor = mWindow.getStatusBarColor();
        Log.d("Test", "P2");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        currentIndexDashboard = 1;
        currentIndexPaneFragment = 2;
        mPager.setCurrentItem(currentIndexDashboard, true);
        EventBus.getDefault().post(new HomePress(1, 2));
        loadPane();
    }

    public void loadViews() {
        mPager = findViewById(R.id.pager);
        mPagerAdapter = new DashboardPagerAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(currentIndexDashboard);
        mPager.setOffscreenPageLimit(2);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                if (currentIndexDashboard == 1 && i == 0) {
                    Log.d("Firebase", "Intention End");
                    FirebaseHelper.getInstance().logScreenUsageTime(IntentionFragment.class.getSimpleName(), startTime);
                    if (currentIndexDashboard == 1 && PrefSiempo.getInstance(DashboardActivity.this).read(PrefSiempo.IS_APP_INSTALLED_FIRSTTIME, true)) {
                        PrefSiempo.getInstance(DashboardActivity.this).write(PrefSiempo.IS_APP_INSTALLED_FIRSTTIME, false);
                        Intent intent = new Intent(DashboardActivity.this, JunkfoodFlaggingActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R
                                .anim.fade_in_junk, R.anim.fade_out_junk);
                    } else if (DashboardActivity.currentIndexPaneFragment == 0) {
                        Log.d("Firebase", "Junkfood Start");
                        startTime = System.currentTimeMillis();
                    } else if (DashboardActivity.currentIndexPaneFragment == 1) {
                        Log.d("Firebase", "Favorite Start");
                        startTime = System.currentTimeMillis();
                    } else if (DashboardActivity.currentIndexPaneFragment == 2) {
                        Log.d("Firebase", "Tools Start");
                        startTime = System.currentTimeMillis();
                    }

                } else if (currentIndexDashboard == 0 && i == 1) {
                    if (DashboardActivity.currentIndexPaneFragment == 0) {
                        Log.d("Firebase", "Junkfood End");
                        FirebaseHelper.getInstance().logScreenUsageTime(JunkFoodPaneFragment.class.getSimpleName(), startTime);
                    } else if (DashboardActivity.currentIndexPaneFragment == 1) {
                        if (PaneFragment.isSearchVisable) {
                            Log.d("Firebase", "Search End");
                            FirebaseHelper.getInstance().logScreenUsageTime("SearchPaneFragment", startTime);
                        } else {
                            Log.d("Firebase", "Favorite End");
                            FirebaseHelper.getInstance().logScreenUsageTime(FavoritePaneFragment.class.getSimpleName(), startTime);
                        }
                    } else if (DashboardActivity.currentIndexPaneFragment == 2) {
                        if (PaneFragment.isSearchVisable) {
                            Log.d("Firebase", "Search End");
                            FirebaseHelper.getInstance().logScreenUsageTime("SearchPaneFragment", startTime);
                        } else {
                            Log.d("Firebase", "Tools End");
                            FirebaseHelper.getInstance().logScreenUsageTime(ToolsPaneFragment.class.getSimpleName(), startTime);
                        }
                    }
                    Log.d("Firebase", "Intention Start");
                    startTime = System.currentTimeMillis();
                }
                currentIndexDashboard = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        loadPane();
        if (PrefSiempo.getInstance(this).read(PrefSiempo
                .IS_APP_INSTALLED_FIRSTTIME, true)) {
            Log.d(TAG, "Display upgrade dialog.");
            checkUpgradeVersion();
        }
    }

    private void loadPane() {
        new LoadToolPane(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new LoadFavoritePane(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new LoadJunkFoodPane(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (currentIndexDashboard == 1) {
            Log.d("Firebase", "Intention End");
            FirebaseHelper.getInstance().logScreenUsageTime(IntentionFragment.class.getSimpleName(), startTime);
        } else if (currentIndexDashboard == 0) {
            if (DashboardActivity.currentIndexPaneFragment == 0) {
                Log.d("Firebase", "Junkfood End");
                FirebaseHelper.getInstance().logScreenUsageTime(JunkFoodPaneFragment.class.getSimpleName(), startTime);
            } else if (DashboardActivity.currentIndexPaneFragment == 1) {
                Log.d("Firebase", "Favorite End");
                FirebaseHelper.getInstance().logScreenUsageTime(FavoritePaneFragment.class.getSimpleName(), startTime);
            } else if (DashboardActivity.currentIndexPaneFragment == 2) {
                Log.d("Firebase", "Tools End");
                FirebaseHelper.getInstance().logScreenUsageTime(ToolsPaneFragment.class.getSimpleName(), startTime);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mPager != null && mPager.getCurrentItem() == 0) {
            if (DashboardActivity.currentIndexPaneFragment == 2 || DashboardActivity.currentIndexPaneFragment == 1) {
                if (mPagerAdapter.getItem(0) instanceof PaneFragment) {
                    EventBus.getDefault().post(new OnBackPressedEvent(true));
                } else {
                    mPager.setCurrentItem(1);
                }
            } else {
                mPager.setCurrentItem(1);
            }
        } else {
            if (mPager != null && mPager.getCurrentItem() == 0) {
                mPager.setCurrentItem(0);
            }
        }
    }

    public void checkUpgradeVersion() {
        Log.d(TAG, "Active network..");
        connectivityManager = (ConnectivityManager) getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (BuildConfig.FLAVOR.equalsIgnoreCase(getString(R.string.alpha))) {
                ApiClient_.getInstance_(DashboardActivity.this)
                        .checkAppVersion(CheckVersionEvent.ALPHA);
            } else if (BuildConfig.FLAVOR.equalsIgnoreCase(getString(R.string.beta))) {
                ApiClient_.getInstance_(DashboardActivity.this)
                        .checkAppVersion(CheckVersionEvent.BETA);
            }
        } else {
            Log.d(TAG, getString(R.string.nointernetconnection));
        }
    }


    @Override
    protected void onStop() {
        if (notificationDialog != null && notificationDialog.isShowing()) {
            notificationDialog.dismiss();
        }
        super.onStop();
    }

    public void notificatoinAccessDialog() {
        notificationDialog = new AlertDialog.Builder(DashboardActivity.this)
                .setTitle(null)
                .setMessage(getString(R.string.msg_noti_service_dialog))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivityForResult(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"), 100);
                    }
                })
                .show();
    }

    @Subscribe
    public void checkVersionEvent(CheckVersionEvent event) {
        Log.d(TAG, "Check Version event...");
        if (event.getVersionName() != null && event.getVersionName().equalsIgnoreCase(CheckVersionEvent.ALPHA)) {
            if (event.getVersion() > UIUtils.getCurrentVersionCode(this)) {
                Tracer.i("Installed version: " + UIUtils.getCurrentVersionCode(this) + " Found: " + event.getVersion());
                showUpdateDialog(CheckVersionEvent.ALPHA);
                appUpdaterUtils = null;
            } else {
                ApiClient_.getInstance_(this).checkAppVersion(CheckVersionEvent.BETA);
            }
        } else {
            if (event.getVersion() > UIUtils.getCurrentVersionCode(this)) {
                Tracer.i("Installed version: " + UIUtils.getCurrentVersionCode(this) + " Found: " + event.getVersion());
                showUpdateDialog(CheckVersionEvent.BETA);
                appUpdaterUtils = null;
            } else {
                Tracer.i("Installed version: " + "Up to date.");
            }
        }
    }

    private void showUpdateDialog(String str) {
        PrefSiempo.getInstance(DashboardActivity.this).write(PrefSiempo
                .IS_APP_INSTALLED_FIRSTTIME, false);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            UIUtils.confirmWithCancel(this, "", str.equalsIgnoreCase(CheckVersionEvent.ALPHA) ? "New alpha version found! Would you like to update Siempo?" : "New beta version found! Would you like to update Siempo?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        PrefSiempo.getInstance(DashboardActivity.this).write
                                (PrefSiempo
                                        .UPDATE_PROMPT, false);
                        new ActivityHelper(DashboardActivity.this).openBecomeATester();
                    }
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    isApplicationLaunch = false;
                }
            });
        } else {
            Log.d(TAG, getString(R.string.nointernetconnection));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            if (isEnabled(DashboardActivity.this)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(DashboardActivity.this)) {
                        Toast.makeText(this, R.string.msg_overlay_settings, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 102);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                                && !notificationManager.isNotificationPolicyAccessGranted()) {
                            Intent intent = new Intent(
                                    android.provider.Settings
                                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                            startActivityForResult(intent, 103);
                        }
                    }
                }

            } else {
                notificatoinAccessDialog();
            }
        }
        if (requestCode == 102) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(DashboardActivity.this)) {
                    Toast.makeText(this, R.string.msg_overlay_settings, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 102);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                            && !notificationManager.isNotificationPolicyAccessGranted()) {
                        Intent intent = new Intent(
                                android.provider.Settings
                                        .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                        startActivityForResult(intent, 103);
                    }
                }
            }
        }

        if (requestCode == 103) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                    && !notificationManager.isNotificationPolicyAccessGranted()) {
                Intent intent = new Intent(
                        android.provider.Settings
                                .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivityForResult(intent, 103);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DashboardActivity.isTextLenghGreater = "";
        currentIndexDashboard = 1;
        currentIndexPaneFragment = 1;
    }


}
