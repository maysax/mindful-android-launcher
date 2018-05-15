package co.siempo.phone.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.concurrent.RejectedExecutionException;

import co.siempo.phone.BuildConfig;
import co.siempo.phone.R;
import co.siempo.phone.adapters.DashboardPagerAdapter;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.event.CheckVersionEvent;
import co.siempo.phone.event.HomePress;
import co.siempo.phone.event.NotifyBackgroundChange;
import co.siempo.phone.event.OnBackPressedEvent;
import co.siempo.phone.fragments.FavoritePaneFragment;
import co.siempo.phone.fragments.IntentionFragment;
import co.siempo.phone.fragments.JunkFoodPaneFragment;
import co.siempo.phone.fragments.PaneFragment;
import co.siempo.phone.fragments.ToolsPaneFragment;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.log.Tracer;
import co.siempo.phone.models.UserModel;
import co.siempo.phone.service.ApiClient_;
import co.siempo.phone.service.LoadFavoritePane;
import co.siempo.phone.service.LoadJunkFoodPane;
import co.siempo.phone.service.LoadToolPane;
import co.siempo.phone.service.MailChimpOperation;
import co.siempo.phone.service.SiempoNotificationListener_;
import co.siempo.phone.ui.SiempoViewPager;
import co.siempo.phone.utils.PackageUtil;
import co.siempo.phone.utils.PermissionUtil;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.UIUtils;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

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
    int swipeCount;
    private Window mWindow;
    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private SiempoViewPager mPager;
    private String TAG = "DashboardActivity";
    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private DashboardPagerAdapter mPagerAdapter;
    private AlertDialog notificationDialog;
    private Dialog overlayDialog;
    private RelativeLayout linMain;
    private ImageView imgBackground;

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


        if (!TextUtils.isEmpty(PrefSiempo.getInstance(this).read(PrefSiempo
                .USER_EMAILID, ""))) {
            boolean isUserSeenEmail = PrefSiempo.getInstance(this).read(PrefSiempo
                    .USER_SEEN_EMAIL_REQUEST, false);
            if (!isUserSeenEmail) {
                try {
                    String strEmail = PrefSiempo.getInstance(this).read(PrefSiempo
                            .USER_EMAILID, "");
                    connectivityManager = (ConnectivityManager) getSystemService(Context
                            .CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = null;
                    if (connectivityManager != null) {
                        activeNetwork = connectivityManager.getActiveNetworkInfo();
                    }
                    if (activeNetwork != null) {
                        new MailChimpOperation().execute(strEmail);
                        storeDataToFirebase(CoreApplication.getInstance().getDeviceId(), strEmail);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                PrefSiempo.getInstance(this).write(PrefSiempo
                        .USER_SEEN_EMAIL_REQUEST, true);
            }
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !permissionUtil.hasGiven
                    (PermissionUtil.WRITE_EXTERNAL_STORAGE_PERMISSION))) {
                Intent intent = new Intent(this, EmailRequestActivity.class);
                startActivity(intent);
            }
        } else {
            if (!PrefSiempo.getInstance(this).read(PrefSiempo
                    .USER_SEEN_EMAIL_REQUEST, false) || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && !permissionUtil.hasGiven
                    (PermissionUtil.WRITE_EXTERNAL_STORAGE_PERMISSION))) {
                Intent intent = new Intent(this, EmailRequestActivity.class);
                startActivity(intent);
            }
        }
    }

    private void storeDataToFirebase(String userId, String emailId) {
        try {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
            UserModel user = new UserModel(userId, emailId);
            mDatabase.child(userId).setValue(user);
            mDatabase.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w("Firebase RealTime", "Failed to read value.", error.toException());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean read = PrefSiempo.getInstance(this).read(PrefSiempo.IS_DARK_THEME, false);
        setTheme(read ? R.style.SiempoAppThemeDark : R.style.SiempoAppTheme);
        setContentView(R.layout.activity_dashboard);
        linMain = findViewById(R.id.linMain);
        imgBackground = findViewById(R.id.imgBackground);
        changeLayoutBackground();

        swipeCount = PrefSiempo.getInstance(DashboardActivity.this).read(PrefSiempo.TOGGLE_LEFTMENU, 0);
        loadViews();
        Log.d("Test", "P1");
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }
        mWindow = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        if (null != mWindow) {
            mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            defaultStatusBarColor = mWindow.getStatusBarColor();
        }
        Log.d("Test", "P2");
        permissionUtil = new PermissionUtil(this);
        overlayDialog = new Dialog(this, 0);
        showOverlayOfDefaultLauncher();
    }

    private void changeLayoutBackground() {
        try {
            String filePath = PrefSiempo.getInstance(this).read(PrefSiempo
                    .DEFAULT_BAG, "");
            boolean isEnable = PrefSiempo.getInstance(this).read(PrefSiempo
                    .DEFAULT_BAG_ENABLE, false);

            if (!TextUtils.isEmpty(filePath) && isEnable) {
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
                //Code for Applying background
                if (null != imgBackground) {
                    imgBackground.setBackground(ob);
                }
            } else {
                imgBackground.setBackground(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showOverlayOfDefaultLauncher() {
        if (!PackageUtil.isSiempoLauncher(this) && !overlayDialog.isShowing()) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showOverLay();
                }
            }, 1000);
        }

        //If already shown there is an overlay dialog and user sets siempo as
        // default launcher from settings or home button then this overlay
        // needs to be dismissed
        if (PackageUtil.isSiempoLauncher(this) && null != overlayDialog &&
                overlayDialog
                        .isShowing()) {
            overlayDialog.dismiss();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        currentIndexDashboard = 1;
        currentIndexPaneFragment = 2;
        mPager.setCurrentItem(currentIndexDashboard, false);
        EventBus.getDefault().postSticky(new HomePress(1, 2));
        loadPane();
        //In case of home press, when app is launched again we need to show
        // this overlay of default launcher if siempo is not set as default
        // launcher
        showOverlayOfDefaultLauncher();
    }

    public void loadViews() {

        mPager = findViewById(R.id.pager);
        mPagerAdapter = new DashboardPagerAdapter(getFragmentManager());
        loadPane();
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
                    if (swipeCount >= 0 && swipeCount < 3) {
                        swipeCount = PrefSiempo.getInstance(DashboardActivity.this).read(PrefSiempo.TOGGLE_LEFTMENU, 0);
                        swipeCount = swipeCount + 1;
                        PrefSiempo.getInstance(DashboardActivity.this).write(PrefSiempo.TOGGLE_LEFTMENU, swipeCount);
                    }
                    FirebaseHelper.getInstance().logScreenUsageTime(IntentionFragment.class.getSimpleName(), startTime);
                    if (DashboardActivity.currentIndexPaneFragment == 0) {
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

        if (PrefSiempo.getInstance(this).read(PrefSiempo
                .INSTALLED_APP_VERSION_CODE, 0) == 0 || (PrefSiempo.getInstance(this).read(PrefSiempo
                .INSTALLED_APP_VERSION_CODE, 0) < UIUtils
                .getCurrentVersionCode(this))) {
            PrefSiempo.getInstance(this).write(PrefSiempo
                    .INSTALLED_APP_VERSION_CODE, UIUtils.getCurrentVersionCode(this));
            checkUpgradeVersion();
        }


    }

    private void loadPane() {
        try {
            new LoadFavoritePane(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            new LoadToolPane(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            new LoadJunkFoodPane(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (RejectedExecutionException e) {
            e.printStackTrace();
        }

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
        NetworkInfo activeNetwork = null;
        if (connectivityManager != null) {
            activeNetwork = connectivityManager.getActiveNetworkInfo();
        }
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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                            && !notificationManager.isNotificationPolicyAccessGranted()) {
                        Intent intent = new Intent(
                                android.provider.Settings
                                        .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                        startActivityForResult(intent, 103);
                    }
                }

            } else {
                notificatoinAccessDialog();
            }
        }
        if (requestCode == 102) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                        && !notificationManager.isNotificationPolicyAccessGranted()) {
                    Intent intent = new Intent(
                            android.provider.Settings
                                    .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    startActivityForResult(intent, 103);
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

    /**
     * Method to show overlay for default launcher setting
     */
    private void showOverLay() {
        try {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            overlayDialog = new Dialog(this, 0);
            Objects.requireNonNull(overlayDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            overlayDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            overlayDialog.setContentView(R.layout.layout_default_launcher);
            Window window = overlayDialog.getWindow();

            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams params = window.getAttributes();
            window.setAttributes(params);
            overlayDialog.getWindow().setLayout(WindowManager.LayoutParams
                    .MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            //overlayDialog.setCancelable(false);
            overlayDialog.setCanceledOnTouchOutside(false);
            if (null != mPager && mPager.getCurrentItem() == 1) {
                overlayDialog.show();
            }

            Button btnEnable = overlayDialog.findViewById(R.id.btnEnable);
            Button btnLater = overlayDialog.findViewById(R.id.btnLater);
            btnEnable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    overlayDialog.dismiss();
                    Intent intent = new Intent(Settings.ACTION_HOME_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }
            });

            btnLater.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    overlayDialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MainThread)
    public void onEvent(NotifyBackgroundChange notifyBackgroundChange) {
        if (notifyBackgroundChange != null && notifyBackgroundChange.isNotify()) {
            changeLayoutBackground();
            EventBus.getDefault().removeStickyEvent(notifyBackgroundChange);
        }

    }


}
