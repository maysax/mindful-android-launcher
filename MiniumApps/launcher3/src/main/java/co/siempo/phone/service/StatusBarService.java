package co.siempo.phone.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rvalerio.fgchecker.AppChecker;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import co.siempo.phone.R;
import co.siempo.phone.activities.AppAssignmentActivity;
import co.siempo.phone.activities.DashboardActivity;
import co.siempo.phone.activities.SettingsActivity_;
import co.siempo.phone.app.Constants;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.db.DBClient;
import co.siempo.phone.event.AppInstalledEvent;
import co.siempo.phone.event.LocationUpdateEvent;
import co.siempo.phone.event.NotifySearchRefresh;
import co.siempo.phone.event.OnBackPressedEvent;
import co.siempo.phone.event.ReduceOverUsageEvent;
import co.siempo.phone.event.StartLocationEvent;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.main.MainListItemLoader;
import co.siempo.phone.models.AppMenu;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.utils.PackageUtil;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.UIUtils;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

import static co.siempo.phone.utils.NotificationUtils.ANDROID_CHANNEL_ID;

/**
 * This background service used for detect torch status and feature used for any other background status.
 */

public class StatusBarService extends Service {

    private static int whichPhaseRunning = 0;// 0 for nothing,1 for Grace,2 for cover,3 for break;
    private static boolean deterUsageRunning = false;
    private Context context;
    private MyObserver myObserver;
    private AppInstallUninstall appInstallUninstall;
    private CountDownTimer countDownTimer;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback mLocationCallback;
    private UserPresentBroadcastReceiver userPresentBroadcastReceiver;
    private CountDownTimer countDownTimerGrace, countDownTimerCover, countDownTimerBreak;
    private WindowManager wm;
    private View bottomView;
    private int heightWindow;
    private int maxHeightWindow;
    private int variableMaxHeightPortrait;
    private int variableMaxHeightLandscape;

    private int heightWindowLandscape;
    private int maxHeightWindowLandscape;
    private View topView;
    private Runnable runnableViewBottom;
    private int minusculeHeight;
    private int minusculeHeightLandscape;
    private WindowManager.LayoutParams paramsTop;
    private AppChecker appChecker;
    private TextView txtTime, txtCount, txtSettings, txtWellness;
    private TextView txtTimeTop, txtCountTop, txtSettingsTop, txtWellnessTop;
    private LinearLayout linButtons, linProgress;
    private LinearLayout linButtonsTop, linProgressTop;
    private ProgressBar progressBar;
    private boolean isFullScreenView = false;
    private WindowManager.LayoutParams paramsBottom;
    private int screenHeightExclusive;
    private Display display;
    private Point size;
    private int maxHeightCoverWindow;


    public StatusBarService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        EventBus.getDefault().register(this);
        registerObserverForContact();
        registerObserverForAppInstallUninstall();
        registerReceiverScreenLock();
        appChecker = new AppChecker();
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
        size = new Point();
        display.getSize(size);

        screenHeightExclusive = (size.y - (getNavigationBarHeight()
                + getStatusBarHeight()));
        heightWindow = (size.y - (getNavigationBarHeight()
                + getStatusBarHeight())) * 6 / 9;

        heightWindowLandscape = (size.x) * 6 / 9;
        maxHeightWindowLandscape = heightWindowLandscape;
        paramsTop = new WindowManager
                .LayoutParams();

        paramsBottom = new WindowManager
                .LayoutParams();

        maxHeightWindow = heightWindow;
        variableMaxHeightPortrait = heightWindow;
        variableMaxHeightLandscape = heightWindowLandscape;
        minusculeHeight = screenHeightExclusive / 9;
        minusculeHeightLandscape = heightWindowLandscape / 9;
        paramsBottom.width = ViewGroup.LayoutParams.MATCH_PARENT;
        paramsBottom.height = minusculeHeight;
        paramsBottom.gravity = Gravity.BOTTOM;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            paramsBottom.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            paramsBottom.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }

        paramsBottom.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        paramsBottom.format = PixelFormat.TRANSLUCENT;
        paramsTop.width = ViewGroup.LayoutParams.MATCH_PARENT;
        paramsTop.height = 0;
        paramsTop.gravity = Gravity.TOP;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            paramsTop.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            paramsTop.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }

        paramsTop.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        paramsTop.format = PixelFormat.TRANSLUCENT;

        display.getSize(size);
        maxHeightCoverWindow = screenHeightExclusive * 6 / 9;
        paramsBottom = new WindowManager.LayoutParams();
        paramsBottom.width = ViewGroup.LayoutParams.MATCH_PARENT;
        paramsBottom.gravity = Gravity.BOTTOM;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            paramsBottom.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        } else {
            paramsBottom.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        paramsBottom.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        paramsBottom.format = PixelFormat.TRANSLUCENT;
        AppChecker.Listener deterUse = new AppChecker.Listener() {
            @Override
            public void onForeground(String process) {
                if (PackageUtil.isSiempoLauncher(context)) {
                    Set<String> set = PrefSiempo.getInstance(context).read(PrefSiempo.JUNKFOOD_APPS, new HashSet<String>());
                    int deterTime = PrefSiempo.getInstance(context).read(PrefSiempo.DETER_AFTER, -1);
                    if (deterTime != -1) {
                        if (set.contains(process)) {
//                            Log.d("DeterUse", "PackageName: " + process);
                            startOverUser();
                        } else {
                            if (!set.contains(process) && deterUsageRunning) {
                                removeView();
                                if (whichPhaseRunning != 0) {
                                    if (whichPhaseRunning == 1) {
                                        if (countDownTimerGrace != null) {
                                            countDownTimerGrace.cancel();
                                            countDownTimerGrace = null;
                                            startTimerForBreakPeriod();
                                        }
                                    } else if (whichPhaseRunning == 2) {
                                        if (countDownTimerCover != null) {
                                            countDownTimerCover.cancel();
                                            countDownTimerCover = null;
                                        }
                                        startTimerForBreakPeriod();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        };
        appChecker.whenAny(deterUse);
        appChecker.timeout(1000);
        appChecker.start(context);
    }

    /**
     * Register the reciver for the screen lock.
     */
    private void registerReceiverScreenLock() {
        userPresentBroadcastReceiver = new UserPresentBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(userPresentBroadcastReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder builder = new Notification.Builder(this, ANDROID_CHANNEL_ID)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("")
                    .setPriority(Notification.PRIORITY_LOW)
                    .setAutoCancel(true);
            Notification notification = builder.build();
            startForeground(Constants.STATUSBAR_SERVICE_ID, notification);
        }

        return START_STICKY;
    }

    /**
     * Observer for when installing new app or uninstalling the app.
     */
    private void registerObserverForAppInstallUninstall() {
        appInstallUninstall = new AppInstallUninstall();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addDataScheme("package");
        registerReceiver(appInstallUninstall, intentFilter);
    }

    /**
     * Observer for when new contact adding or updating any exiting contact.
     */
    private void registerObserverForContact() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {
                myObserver = new MyObserver(new Handler());
                getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true,
                        myObserver);
            }
        } else {
            myObserver = new MyObserver(new Handler());
            getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true,
                    myObserver);
        }
    }

    @Subscribe
    public void firebaseEvent(OnBackPressedEvent onBackPressed) {
//        FirebaseHelper.getInstance().logScreenUsageTime(onBackPressed.getScreenName(), onBackPressed.getStrStartTime());
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (myObserver != null)
            getContentResolver().unregisterContentObserver(myObserver);
        if (appInstallUninstall != null)
            unregisterReceiver(appInstallUninstall);
        if (appChecker != null) appChecker.stop();
        super.onDestroy();
    }

    /**
     * Remove uninstall app if it contains in blocked list OR HelpfulRobots
     *
     * @param uninstallPackageName
     */
    public void removeAppFromBlockedList(String uninstallPackageName) {
        Set<String> blockedApps;
        Set<String> removeApps = new HashSet<>();
        blockedApps = PrefSiempo.getInstance(context).read(PrefSiempo.BLOCKED_APPLIST,
                new HashSet<String>());
        try {


            if (blockedApps.contains(uninstallPackageName)) {
                blockedApps.remove(uninstallPackageName);
            }
            PrefSiempo.getInstance(context).write(PrefSiempo.BLOCKED_APPLIST,
                    blockedApps);


            ArrayList<String> disableApps;
            String disable_AppList = PrefSiempo.getInstance(context).read
                    (PrefSiempo.HELPFUL_ROBOTS, "");
            if (!TextUtils.isEmpty(disable_AppList)) {
                Type type = new TypeToken<ArrayList<String>>() {
                }.getType();
                disableApps = new Gson().fromJson(disable_AppList, type);
                ArrayList<String> removedisableApps = new ArrayList<>();
                for (String disableAppName : disableApps) {
                    if (disableAppName.equalsIgnoreCase(uninstallPackageName.trim())) {
                        removedisableApps.add(disableAppName);
                    }
                }

                disableApps.removeAll(removedisableApps);
                String disableList = new Gson().toJson(disableApps);
                PrefSiempo.getInstance(context).write(PrefSiempo.HELPFUL_ROBOTS, disableList);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Add install app in blocked list
     *
     * @param installPackageName
     */
    public void addAppFromBlockedList(String installPackageName) {
        Set<String> blockedApps;
        blockedApps = PrefSiempo.getInstance(context).read(PrefSiempo.BLOCKED_APPLIST, new HashSet<String>());
        try {
            boolean isAppExist = false;
            for (String blockedAppName : blockedApps) {
                if (blockedAppName.equalsIgnoreCase(installPackageName.trim())) {
                    isAppExist = true;
                }
            }
            if (!isAppExist) {
                blockedApps.add(installPackageName.trim());
            }
            PrefSiempo.getInstance(context).write(PrefSiempo.BLOCKED_APPLIST,
                    blockedApps);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Remove application from Shared Preference when user disable application.
     *
     * @param context
     * @param packageName
     */
    private void removeAppFromPreference(Context context, String packageName) {


        Set<String> favoriteList = PrefSiempo.getInstance(context)
                .read
                        (PrefSiempo.FAVORITE_APPS, new HashSet<String>());
        Set<String> junkFoodList = PrefSiempo
                .getInstance(context).read
                        (PrefSiempo.JUNKFOOD_APPS, new HashSet<String>());

        if (favoriteList.contains(packageName)) {
            favoriteList.remove(packageName);
            PrefSiempo.getInstance(context)
                    .write
                            (PrefSiempo.FAVORITE_APPS, favoriteList);
        }
        if (junkFoodList.contains(packageName)) {
            junkFoodList.remove(packageName);
            PrefSiempo
                    .getInstance(context).write
                    (PrefSiempo.JUNKFOOD_APPS, junkFoodList);
        }

        HashMap<Integer, AppMenu> hashMap = CoreApplication.getInstance().getToolsSettings();
        for (Map.Entry<Integer, AppMenu> has : hashMap.entrySet()) {
            if (has.getValue().getApplicationName().equalsIgnoreCase(packageName)) {
                Log.d("Remove Application", packageName);
                has.getValue().setApplicationName("");
            }
        }
        PrefSiempo
                .getInstance(context).write
                (PrefSiempo.TOOLS_SETTING, new Gson().toJson(hashMap));

        updateFavoriteSort(context, packageName);


    }

    /**
     * Update the favorite pane preference.
     *
     * @param context
     * @param packageName
     */
    public void updateFavoriteSort(Context context, String packageName) {
        //get the JSON array of the ordered of sorted customers
        String jsonListOfSortedFavorites = PrefSiempo.getInstance(context).read(PrefSiempo.FAVORITE_SORTED_MENU, "");
        //convert onNoteListChangedJSON array into a List<Long>
        Gson gson1 = new Gson();
        List<String> listOfSortFavoritesApps = gson1.fromJson(jsonListOfSortedFavorites, new TypeToken<List<String>>() {
        }.getType());
        for (ListIterator<String> it =
             listOfSortFavoritesApps.listIterator(); it.hasNext
                (); ) {
            String removePackageName = it.next();
            if (!TextUtils.isEmpty(removePackageName) && removePackageName.trim().equalsIgnoreCase(packageName)) {
                it.set("");
            }

        }
        Gson gson2 = new Gson();
        String jsonListOfFavoriteApps = gson2.toJson(listOfSortFavoritesApps);
        PrefSiempo.getInstance(context).write(PrefSiempo.FAVORITE_SORTED_MENU, jsonListOfFavoriteApps);
    }

    /**
     * Notify all 3 panes fragment.
     */
    private void reloadData() {
        new LoadFavoritePane(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new LoadToolPane(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new LoadJunkFoodPane(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        EventBus.getDefault().postSticky(new NotifySearchRefresh(true));
    }

    /**
     * This timer is start then user locked the screen and this is used to navigate user to Intention screen after 15 minute.
     */
    public void startLockScreenTimer() {
        countDownTimer = new CountDownTimer(15 * 60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                PrefSiempo.getInstance(context).write(PrefSiempo.LOCK_COUNTER_STATUS, true);
                countDownTimer.cancel();
                countDownTimer = null;
            }
        }.start();

    }

    /**
     * This observer is used to determine the contact add/delete/update.
     */
    public void stopTimer() {
        countDownTimer.cancel();

        PrefSiempo.getInstance(this).write(PrefSiempo
                .LOCK_COUNTER_STATUS, false);
    }

    @SuppressLint("MissingPermission")
    public void getLocation() {
        int timer_time = PrefSiempo.getInstance(context).read(PrefSiempo.LOCATION_TIMER_TIME, 1);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(timer_time * 60000);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        if (mLocationCallback == null) {
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        //return;
                        Location mlocation = locationResult.getLastLocation();
                        EventBus.getDefault().postSticky(new LocationUpdateEvent(mlocation));
                    }
                    List<Location> locations = locationResult.getLocations();
                    for (Location location : locations) {
                        // Update UI with location data
                        // ...
                        if (location != null) {
                            Log.e("location details", "long: " + location.getLongitude() + "lat: " + location
                                    .getLatitude());
                            EventBus.getDefault().postSticky(new LocationUpdateEvent(location));
                        }

                    }
                }
            };
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest,
                mLocationCallback,
                null);
    }

    public void stopLocationUpdates() {
        if (mLocationCallback != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Subscribe
    public void StartLocationEvent(StartLocationEvent event) {
        boolean isLocationOn = event.getIsLocationOn();
        if (!isLocationOn) {
            stopLocationUpdates();
        } else {
            getLocation();
        }
    }

    @Subscribe
    public void reduceOverUsageEvent(ReduceOverUsageEvent reduceOverUsageEvent) {
        if (reduceOverUsageEvent.isStartEvent()) {
            resetAllTimer();
        } else {
//            removeView();
        }
    }

    /**
     * This method is used to start timer and resume the timer and check on every second.
     */
    void startOverUser() {
        int deterTime = PrefSiempo.getInstance(context).read(PrefSiempo.DETER_AFTER, -1);

        long deterTimeLong = deterTime * 60000;

        long grace_time_completed = PrefSiempo.getInstance(context).read(PrefSiempo.GRACE_TIME, 0L);
        long cover_time_completed = PrefSiempo.getInstance(context).read(PrefSiempo.COVER_TIME, 0L);
        long break_time_completed = PrefSiempo.getInstance(context).read(PrefSiempo.BREAK_TIME, 0L);

        if (!deterUsageRunning) {
            if (deterTimeLong == 0L) {
                deterUsageRunning = true;
                long remainingTimeCover = 5 * 60000 - cover_time_completed;
                startTimerForCoverPeriod(remainingTimeCover, cover_time_completed);
            } else {
                deterUsageRunning = true;
                int minutes = (int) (deterTimeLong / (1000 * 60));
                int seconds = (int) ((deterTimeLong / 1000) % 60);
                Log.d("DeterUse:GraceRemaining", "" + minutes + ":" + seconds);
                startTimerForGracePeriod(deterTimeLong);
            }
        } else {
            if (grace_time_completed != 0L && countDownTimerBreak != null) {
                long remainingTimeGrace = deterTimeLong - grace_time_completed;
                countDownTimerBreak.cancel();
                countDownTimerBreak = null;
                PrefSiempo.getInstance(context).write(PrefSiempo.BREAK_TIME, 0L);
                int minutes = (int) (remainingTimeGrace / (1000 * 60));
                int seconds = (int) ((remainingTimeGrace / 1000) % 60);
                Log.d("DeterUse:GraceRemaining", "" + minutes + ":" + seconds);
                startTimerForGracePeriod(remainingTimeGrace);
            } else if (cover_time_completed != 0L && countDownTimerBreak != null) {
                long remainingTimeCover = 5 * 60000 - cover_time_completed;
                countDownTimerBreak.cancel();
                countDownTimerBreak = null;
                PrefSiempo.getInstance(context).write(PrefSiempo.BREAK_TIME, 0L);
                int minutes = (int) (remainingTimeCover / (1000 * 60));
                int seconds = (int) ((remainingTimeCover / 1000) % 60);
                Log.d("DeterUse:CoverRemaining", "" + minutes + ":" + seconds);
                addOverlayWindow((int) (cover_time_completed / (1000 * 60)));
                startTimerForCoverPeriod(remainingTimeCover, cover_time_completed);
            } else if (grace_time_completed == 0L && cover_time_completed == 0L
                    && isFullScreenView && countDownTimerBreak != null) {
                addOverlayWindow(6);
            }

        }

    }

    /**
     * This method used to start timer for Grace-Period.
     *
     * @param deterTime how long the timer runs.
     */
    private void startTimerForGracePeriod(final long deterTime) {
        whichPhaseRunning = 1;
        countDownTimerGrace = new CountDownTimer(deterTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long completedTime = deterTime - millisUntilFinished;
                int minutes = (int) (completedTime / (1000 * 60));
                int seconds = (int) ((completedTime / 1000) % 60);
                Log.d("DeterUse : Grace", "" + minutes + ":" + seconds);
                PrefSiempo.getInstance(context).write(PrefSiempo.GRACE_TIME, completedTime);
            }

            @Override
            public void onFinish() {
                PrefSiempo.getInstance(context).write(PrefSiempo.GRACE_TIME, 0L);
                if (countDownTimerGrace != null) countDownTimerGrace.cancel();
                countDownTimerGrace = null;
                long remainingTimeCover = 5 * 60000;
                startTimerForCoverPeriod(remainingTimeCover, 0L);
            }
        }.start();
    }

    /**
     * This method used to start timer for Cover-Period.
     *
     * @param remainingTimeCover how long the timer runs.
     */
    private void startTimerForCoverPeriod(final long remainingTimeCover, final long cover_time_completed) {
        whichPhaseRunning = 2;
        Log.d("DeterUse : Cover", "remainingTimeCover" + remainingTimeCover + " cover_time_completed" + cover_time_completed);
        countDownTimerCover = new CountDownTimer(remainingTimeCover, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                long completedTime = (remainingTimeCover - millisUntilFinished) + cover_time_completed;
                int minutes = (int) (completedTime / (1000 * 60));
                int seconds = (int) ((completedTime / 1000) % 60);

                Log.d("DeterUse : Cover", "" + minutes + ":" + seconds);
                PrefSiempo.getInstance(context).write(PrefSiempo.COVER_TIME, completedTime);

                if (seconds == 0 && minutes == 0) {
                    addOverlayWindow(0);
                    int deterTime = PrefSiempo.getInstance(context).read(PrefSiempo.DETER_AFTER, -1);
                    String strTime = String.format("%02d", (minutes + deterTime)) + ":" + String.format("%02d", seconds);
                    if (wm != null && txtTime != null) txtTime.setText(strTime);
                    if (wm != null && txtTimeTop != null) txtTimeTop.setText
                            (strTime);
                } else if (seconds == 0 && minutes != 0) {
                    addOverlayWindow(minutes);
                    int deterTime = PrefSiempo.getInstance(context).read(PrefSiempo.DETER_AFTER, -1);
                    String strTime = String.format("%02d", (minutes + deterTime)) + ":" + String.format("%02d", seconds);
                    if (wm != null && txtTime != null) txtTime.setText(strTime);
                    if (wm != null && txtTimeTop != null) txtTimeTop.setText
                            (strTime);
                } else {
                    int deterTime = PrefSiempo.getInstance(context).read(PrefSiempo.DETER_AFTER, -1);
                    String strTime = String.format("%02d", (minutes + deterTime)) + ":" + String.format("%02d", seconds);
                    if (wm != null && txtTime != null) txtTime.setText(strTime);
                    if (wm != null && txtTimeTop != null) txtTimeTop.setText
                            (strTime);
                }
            }

            @Override
            public void onFinish() {
                if (countDownTimerCover != null) {
                    countDownTimerCover.cancel();
                    countDownTimerCover = null;
                }
                addOverlayWindow(5);
                PrefSiempo.getInstance(context).write(PrefSiempo.COVER_TIME, 0L);
                int deterTime = PrefSiempo.getInstance(context).read(PrefSiempo.DETER_AFTER, -1);
                String strTime = String.format("%02d", (5 + deterTime)) + ":" + "00";
                Log.d("DeterUse : Cover", "strTime:" + strTime);
                if (wm != null && txtTime != null) txtTime.setText(strTime);
                if (wm != null && txtTimeTop != null) txtTimeTop.setText
                        (strTime);

            }
        }.start();
    }

    /**
     * This method used to start timer for Break-Period.
     */
    private void startTimerForBreakPeriod() {
        whichPhaseRunning = 3;
        final int breakPeriod = PrefSiempo.getInstance(context).read(PrefSiempo.BREAK_PERIOD, 1);
        final long breakPeriod1 = breakPeriod * 60000;
        countDownTimerBreak = new CountDownTimer(breakPeriod1, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long completedTime = breakPeriod1 - millisUntilFinished;
                int seconds = (int) ((completedTime / 1000) % 60) + 1;
                Log.d("DeterUse : Break", "" + seconds);
                if (wm != null && linProgress != null && txtCount != null) {
                    txtCount.setText("" + seconds);
                    progressBar.setProgress(seconds);
                }
            }

            @Override
            public void onFinish() {
                if (countDownTimerBreak != null) {
                    countDownTimerBreak.cancel();
                    countDownTimerBreak = null;
                }
                deterUsageRunning = false;
                isFullScreenView = false;
                whichPhaseRunning = 0;
                resetAllTimer();
                removeView();
            }
        }.start();
    }

    /**
     * Reset all grace,cover,break timer and set value in preference 0 for all.
     */
    private void resetAllTimer() {
        removeView();
        deterUsageRunning = false;
        if (countDownTimerGrace != null) {
            countDownTimerGrace.cancel();
            countDownTimerGrace = null;
        }
        if (countDownTimerCover != null) {
            countDownTimerCover.cancel();
            countDownTimerCover = null;
        }
        if (countDownTimerBreak != null) {
            countDownTimerBreak.cancel();
            countDownTimerBreak = null;
        }
        PrefSiempo.getInstance(context).write(PrefSiempo.GRACE_TIME, 0L);
        PrefSiempo.getInstance(context).write(PrefSiempo.COVER_TIME, 0L);
        PrefSiempo.getInstance(context).write(PrefSiempo.BREAK_TIME, 0L);
    }

    /**
     * Add/Update overlay window when cover period & break period is running.
     *
     * @param coverTime value which manage the height of overlay based in minutes completed.
     */
    private void addOverlayWindow(final int coverTime) {
        try {
            boolean isSettingPressed = PrefSiempo.getInstance(context).read(PrefSiempo.IS_SETTINGS_PRESSED, false);


            switch (coverTime) {


                case 0:
                    paramsBottom.height = screenHeightExclusive / 9;
                    Log.d("DeterUse : Screen 0/9 :", "" + paramsBottom.height);
                    break;
                case 1:
                    if (topView != null && topView.getHeight() != 0 &&
                            (bottomView == null || bottomView.getWindowToken
                                    () == null || bottomView.getHeight() == 0)) {
                        paramsTop.height = screenHeightExclusive * 2 / 9;
                    } else if (topView != null && topView.getHeight() != 0 &&
                            bottomView != null && bottomView.getHeight() != 0) {
                        paramsTop.height = (screenHeightExclusive * 2 / 9) / 2;
                        paramsBottom.height = (screenHeightExclusive * 2 / 9)
                                / 2;
                    } else if (bottomView != null && bottomView.getHeight() != 0 &&
                            (topView == null || topView.getWindowToken() ==
                                    null)) {
                        paramsBottom.height = screenHeightExclusive * 2 / 9;
                    }

                    Log.d("DeterUse : Screen 2/9 :", "" + paramsBottom.height);
                    break;
                case 2:
                    if (topView != null && topView.getHeight() != 0 &&
                            (bottomView == null || bottomView.getWindowToken
                                    () == null || bottomView.getHeight() == 0)) {
                        paramsTop.height = screenHeightExclusive * 3 / 9;
                    } else if (topView != null && topView.getHeight() != 0 &&
                            bottomView != null && bottomView.getHeight() != 0) {
                        paramsTop.height = (screenHeightExclusive * 3 / 9) / 2;
                        paramsBottom.height = (screenHeightExclusive * 3 / 9) / 2;
                    } else if (bottomView != null && bottomView.getHeight() != 0 &&
                            (topView == null || topView.getWindowToken() ==
                                    null)) {
                        paramsBottom.height = screenHeightExclusive * 3 / 9;
                    }
                    break;
                case 3:
                    if (topView != null && topView.getHeight() != 0 &&
                            (bottomView == null || bottomView.getWindowToken
                                    () == null || bottomView
                                    .getHeight() ==
                                    0)) {
                        paramsTop.height = screenHeightExclusive * 4 / 9;
                    } else if (topView != null && topView.getHeight() != 0 &&
                            bottomView != null && bottomView.getHeight() != 0) {
                        paramsTop.height = (screenHeightExclusive * 4 / 9) / 2;
                        paramsBottom.height = (screenHeightExclusive * 4 / 9) / 2;
                    } else if (bottomView != null && bottomView.getHeight() != 0 &&
                            (topView == null || topView.getWindowToken() == null
                                    || topView
                                    .getHeight() == 0)) {
                        paramsBottom.height = screenHeightExclusive * 4 / 9;
                    }
                    break;
                case 4:
                    if (topView != null && topView.getHeight() != 0 &&
                            (bottomView == null || bottomView.getWindowToken
                                    () == null || bottomView
                                    .getHeight
                                            () ==
                                    0)) {
                        paramsTop.height = screenHeightExclusive * 5 / 9;
                    } else if (topView != null && topView.getHeight() != 0 &&
                            bottomView != null && bottomView.getHeight() != 0) {
                        paramsTop.height = (screenHeightExclusive * 5 / 9) / 2;
                        paramsBottom.height = (screenHeightExclusive * 5 / 9) / 2;
                    } else if (bottomView != null && bottomView.getHeight() != 0 &&
                            (topView == null || topView.getWindowToken() == null
                                    || topView
                                    .getHeight() == 0)) {
                        paramsBottom.height = screenHeightExclusive * 5 / 9;
                    }
                    break;
                case 5:
                    if (topView != null && topView.getHeight() != 0 &&
                            (bottomView == null || bottomView.getWindowToken
                                    () == null ||
                                    bottomView.getHeight
                                            () == 0)) {
                        paramsTop.height = screenHeightExclusive * 6 / 9;
                    } else if (topView != null && topView.getHeight() != 0 &&
                            bottomView != null && bottomView.getHeight() != 0) {
                        paramsTop.height = (screenHeightExclusive * 6 / 9) / 2;
                        paramsBottom.height = (screenHeightExclusive * 6 / 9) / 2;
                    } else if (bottomView != null && bottomView.getHeight() != 0 &&
                            (topView == null || topView.getWindowToken()
                                    == null || topView.getHeight()
                                    ==
                                    0)) {
                        paramsBottom.height = screenHeightExclusive * 6 / 9;
                    }
                    break;
                case 6:
                    paramsBottom.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    break;

            }


            if (topView == null) {
                topView = ((LayoutInflater) getSystemService(Context
                        .LAYOUT_INFLATER_SERVICE)).inflate(R.layout
                        .gray_scale_layout_reverse, null);
                txtTimeTop = topView.findViewById(R.id.txtTime);
                linButtonsTop = topView.findViewById(R.id.linButtons);
                linProgressTop = topView.findViewById(R.id.linProgress);
                progressBar = topView.findViewById(R.id.progress);
                progressBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(context, R.color.appland_blue_bright), PorterDuff.Mode.SRC_IN);
                int value = (int) TimeUnit.MINUTES.toSeconds(PrefSiempo.getInstance(context).read(PrefSiempo.BREAK_PERIOD, 1));
                progressBar.setMax(value);
                txtCountTop = topView.findViewById(R.id.txtCount);
                txtWellnessTop = topView.findViewById(R.id.txtWellness);
                txtSettingsTop = topView.findViewById(R.id.txtSettings);

//                if (linButtonsTop != null) {
//                    linButtonsTop.setVisibility(View.VISIBLE);
//                }

                txtTimeTop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isFullScreenView = true;
                        if (countDownTimerCover != null) {
                            countDownTimerCover.cancel();
                            countDownTimerCover = null;
                        }
                        PrefSiempo.getInstance(context).write(PrefSiempo.COVER_TIME, 0L);
                        paramsTop.height = ViewGroup.LayoutParams.MATCH_PARENT;
                        bottomView.setLayoutParams(new ViewGroup.LayoutParams(paramsBottom));
                        if (wm != null)
                            wm.updateViewLayout(topView, paramsTop);
                        if (linButtons != null && linProgress != null) {
                            linProgress.setVisibility(View.VISIBLE);
                            linButtons.setVisibility(View.GONE);
                        }
                        startTimerForBreakPeriod();
                    }
                });
                if (txtSettingsTop != null) {
                    if (!isSettingPressed) {
                        txtSettingsTop.setText(getResources().getString(R.string.settings));
                        txtSettingsTop.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.settings_overlay), null, null, null);
                    } else {
                        txtSettingsTop.setText(getResources().getString(R.string.notes));
                        txtSettingsTop.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.notes_overlay), null, null, null);
                    }
                }
                txtSettingsTop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (txtSettingsTop.getText().toString().equalsIgnoreCase(getResources().getString(R.string.settings))) {
                            PrefSiempo.getInstance(context).write(PrefSiempo.IS_SETTINGS_PRESSED, true);
                            Intent intent = new Intent(context, SettingsActivity_.class);
                            intent.putExtra("FlagApp", true);
                            startActivity(intent);
                        } else {
                            new ActivityHelper(context).openNotesApp(false);
                        }
                    }
                });
                txtWellnessTop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<Integer, AppMenu> map = CoreApplication.getInstance().getToolsSettings();
                        if (map.get(MainListItemLoader.TOOLS_WELLNESS).getApplicationName().equalsIgnoreCase("")) {
                            MainListItem item = new MainListItem(MainListItemLoader.TOOLS_WELLNESS, context.getResources()
                                    .getString(R.string.title_wellness), R.drawable
                                    .ic_vector_wellness);
                            Intent intent = new Intent(context, AppAssignmentActivity.class);
                            intent.putExtra(Constants.INTENT_MAINLISTITEM, item);
                            intent.putExtra("class_name", DashboardActivity.class.getSimpleName
                                    ().toString());
                            context.startActivity(intent);
                        } else {
                            new ActivityHelper(context).openAppWithPackageName(map.get(10).getApplicationName());
                        }
                    }
                });

                if (null != wm) {
                    wm.addView(topView, paramsTop);
                    topView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (paramsBottom.height != 0 && topView
                                    .getWindowToken() != null) {
                                paramsTop.height = topView.getHeight() +
                                        bottomView.getHeight();
                                topView.setLayoutParams(new ViewGroup.LayoutParams
                                        (paramsTop));
                                wm.updateViewLayout(topView, paramsTop);
                                paramsBottom.height = 0;
                                if (bottomView.getWindowToken() != null) {
                                    wm.removeView(bottomView);
                                }
                                variableMaxHeightPortrait = heightWindow;
                                variableMaxHeightLandscape = heightWindowLandscape;
                                linButtonsTop.setVisibility(View.VISIBLE);
                            } else if (topView
                                    .getWindowToken() != null) {
                                if (paramsTop.height != minusculeHeight) {
                                    paramsTop.height = topView.getHeight() / 2;
                                    topView.setLayoutParams(new ViewGroup.LayoutParams
                                            (paramsTop));
                                    wm.updateViewLayout(topView, paramsTop);
                                    paramsBottom.height = paramsTop.height;
                                    bottomView.setLayoutParams(new ViewGroup.LayoutParams
                                            (paramsBottom));
                                    if (bottomView.getWindowToken() != null) {
                                        wm.updateViewLayout(bottomView, paramsBottom);
                                    } else {
                                        wm.addView(bottomView, paramsBottom);
                                    }
                                    variableMaxHeightPortrait = heightWindow / 2;
                                    variableMaxHeightLandscape = heightWindowLandscape / 2;
                                    linButtonsTop.setVisibility(View.GONE);
                                } else if (paramsTop.height == minusculeHeight) {
                                    //code for shifting bottom view to top
                                    paramsTop.height = 0;
                                    topView.setLayoutParams(new ViewGroup.LayoutParams
                                            (paramsTop));
                                    wm.updateViewLayout(topView, paramsTop);
                                    paramsBottom.height = minusculeHeight;
                                    bottomView.setLayoutParams(new ViewGroup.LayoutParams
                                            (paramsBottom));
                                    if (bottomView.getWindowToken() != null) {
                                        wm.updateViewLayout(bottomView, paramsBottom);
                                    } else {
                                        wm.addView(bottomView, paramsBottom);
                                    }
                                    linButtonsTop.setVisibility(View.GONE);

                                }
                            }

                        }
                    });


                }


            } else {
                try {
                    if (isFullScreenView) {
                        if (linButtonsTop != null && linProgressTop != null) {
                            linProgressTop.setVisibility(View.VISIBLE);
                            linButtonsTop.setVisibility(View.GONE);
                        }
                        if (countDownTimerCover != null) {
                            countDownTimerCover.cancel();
                            countDownTimerCover = null;
                            PrefSiempo.getInstance(context).write(PrefSiempo.COVER_TIME, 0L);
                        }
                    } else {
                        if (paramsTop.height <= maxHeightCoverWindow) {
                            if (topView != null && topView.getWindowToken() != null) {
                                topView.setLayoutParams(new ViewGroup.LayoutParams
                                        (paramsTop));
                                wm.updateViewLayout(topView, paramsTop);
                            }

                        }
                    }
                    if (txtSettingsTop != null) {
                        if (!isSettingPressed) {
                            txtSettingsTop.setText(getResources().getString(R
                                    .string.settings));
                            txtSettingsTop
                                    .setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.settings_overlay), null, null, null);
                        } else {
                            txtSettingsTop.setText(getResources().getString(R
                                    .string.notes));
                            txtSettingsTop
                                    .setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.notes_overlay), null, null, null);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }


            if (bottomView == null) {
                bottomView = ((LayoutInflater) getSystemService(Context
                        .LAYOUT_INFLATER_SERVICE)).inflate(R.layout
                        .gray_scale_layout, null);
                txtTime = bottomView.findViewById(R.id.txtTime);
                linButtons = bottomView.findViewById(R.id.linButtons);
                linProgress = bottomView.findViewById(R.id.linProgress);
                progressBar = bottomView.findViewById(R.id.progress);
                progressBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(context, R.color.appland_blue_bright), PorterDuff.Mode.SRC_IN);
                int value = (int) TimeUnit.MINUTES.toSeconds(PrefSiempo.getInstance(context).read(PrefSiempo.BREAK_PERIOD, 1));
                progressBar.setMax(value);
                txtCount = bottomView.findViewById(R.id.txtCount);
                txtWellness = bottomView.findViewById(R.id.txtWellness);
                txtSettings = bottomView.findViewById(R.id.txtSettings);
                if (isFullScreenView) {
                    if (paramsBottom.height != ViewGroup.LayoutParams.MATCH_PARENT) {
                        paramsBottom.height = ViewGroup.LayoutParams.MATCH_PARENT;
                        bottomView.setLayoutParams(new ViewGroup.LayoutParams(paramsBottom));
                        if (wm != null)
                            wm.updateViewLayout(bottomView, paramsBottom);
                        if (linButtons != null && linProgress != null) {
                            linProgress.setVisibility(View.VISIBLE);
                            linButtons.setVisibility(View.GONE);
                        }
                        if (countDownTimerCover != null) {
                            countDownTimerCover.cancel();
                            countDownTimerCover = null;
                            PrefSiempo.getInstance(context).write(PrefSiempo.COVER_TIME, 0L);
                        }
                    }
                    if (linButtons != null) {
                        linButtons.setVisibility(View.GONE);
                    }
                    if (linProgress != null) {
                        linProgress.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (linButtons != null)
                        linButtons.setVisibility(View.VISIBLE);
                }
                txtTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isFullScreenView = true;
                        if (countDownTimerCover != null) {
                            countDownTimerCover.cancel();
                            countDownTimerCover = null;
                        }
                        PrefSiempo.getInstance(context).write(PrefSiempo.COVER_TIME, 0L);
                        paramsBottom.height = ViewGroup.LayoutParams.MATCH_PARENT;
                        bottomView.setLayoutParams(new ViewGroup.LayoutParams(paramsBottom));
                        if (wm != null)
                            wm.updateViewLayout(bottomView, paramsBottom);
                        if (linButtons != null && linProgress != null) {
                            linProgress.setVisibility(View.VISIBLE);
                            linButtons.setVisibility(View.GONE);
                        }
                        startTimerForBreakPeriod();
                    }
                });
                if (txtSettings != null) {
                    if (!isSettingPressed) {
                        txtSettings.setText(getResources().getString(R.string.settings));
                        txtSettings.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.settings_overlay), null, null, null);
                    } else {
                        txtSettings.setText(getResources().getString(R.string.notes));
                        txtSettings.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.notes_overlay), null, null, null);
                    }
                }
                txtSettings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (txtSettings.getText().toString().equalsIgnoreCase(getResources().getString(R.string.settings))) {
                            PrefSiempo.getInstance(context).write(PrefSiempo.IS_SETTINGS_PRESSED, true);
                            Intent intent = new Intent(context, SettingsActivity_.class);
                            intent.putExtra("FlagApp", true);
                            startActivity(intent);
                        } else {
                            new ActivityHelper(context).openNotesApp(false);
                        }
                    }
                });
                txtWellness.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<Integer, AppMenu> map = CoreApplication.getInstance().getToolsSettings();
                        if (map.get(MainListItemLoader.TOOLS_WELLNESS).getApplicationName().equalsIgnoreCase("")) {
                            MainListItem item = new MainListItem(MainListItemLoader.TOOLS_WELLNESS, context.getResources()
                                    .getString(R.string.title_wellness), R.drawable
                                    .ic_vector_wellness);
                            Intent intent = new Intent(context, AppAssignmentActivity.class);
                            intent.putExtra(Constants.INTENT_MAINLISTITEM, item);
                            intent.putExtra("class_name", DashboardActivity.class.getSimpleName
                                    ().toString());
                            context.startActivity(intent);
                        } else {
                            new ActivityHelper(context).openAppWithPackageName(map.get(10).getApplicationName());
                        }
                    }
                });

                if (null != wm) {
                    wm.addView(bottomView, paramsBottom);
//                    wm.addView(topView, paramsTop);

                    bottomView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (topView != null && topView.getWindowToken() !=
                                    null
                                    &&
                                    topView.getHeight() !=
                                            0) {

                                paramsBottom.height = topView.getHeight() +
                                        bottomView.getHeight();
                                bottomView.setLayoutParams(new ViewGroup.LayoutParams
                                        (paramsBottom));
                                wm.updateViewLayout(bottomView, paramsBottom);
                                paramsTop.height = 0;
                                if (topView.getWindowToken() != null) {
                                    wm.removeView(topView);
                                }
                                variableMaxHeightPortrait = heightWindow;
                                variableMaxHeightLandscape = heightWindowLandscape;

                            } else {

                                if (paramsBottom.height != minusculeHeight) {
                                    paramsBottom.height = bottomView.getHeight() / 2;
                                    bottomView.setLayoutParams(new ViewGroup.LayoutParams
                                            (paramsBottom));
                                    wm.updateViewLayout(bottomView, paramsBottom);
                                    paramsTop.height = paramsBottom.height;
                                    if (null != topView) {
                                        topView.setLayoutParams(new ViewGroup.LayoutParams
                                                (paramsTop));
                                        if (topView.getWindowToken() != null) {
                                            wm.updateViewLayout(topView, paramsTop);
                                        } else {
                                            wm.addView(topView, paramsTop);
                                        }
                                    }
                                    variableMaxHeightPortrait = heightWindow / 2;
                                    variableMaxHeightLandscape = heightWindowLandscape / 2;
                                }
                            }
                        }
                    });
                }


            } else {
                try {
                    if (isFullScreenView) {
                        if (linButtons != null && linProgress != null) {
                            linProgress.setVisibility(View.VISIBLE);
                            linButtons.setVisibility(View.GONE);
                        }
                        if (countDownTimerCover != null) {
                            countDownTimerCover.cancel();
                            countDownTimerCover = null;
                            PrefSiempo.getInstance(context).write(PrefSiempo.COVER_TIME, 0L);
                        }
                    } else {
                        if (paramsBottom.height <= maxHeightCoverWindow) {
                            //Increase height of overlay
                            //Commneted below line as height of bottom view
                            // was increasing way to much
//                            paramsBottom.height = paramsBottom.height + (size.y / 9);
                            bottomView.setLayoutParams(new ViewGroup.LayoutParams(paramsBottom));
                            if (wm != null && bottomView != null && bottomView
                                    .getWindowToken() != null) {
                                wm.updateViewLayout(bottomView, paramsBottom);
                            }
                            if (topView != null && topView.getWindowToken() != null) {
                                topView.setLayoutParams(new ViewGroup.LayoutParams
                                        (paramsTop));
                                wm.updateViewLayout(topView, paramsTop);
                            }

                        }
                    }
                    if (txtSettings != null) {
                        if (!isSettingPressed) {
                            txtSettings.setText(getResources().getString(R.string.settings));
                            txtSettings.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.settings_overlay), null, null, null);
                        } else {
                            txtSettings.setText(getResources().getString(R.string.notes));
                            txtSettings.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.notes_overlay), null, null, null);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove overlay window when break period completes.
     */
    private void removeView() {
        try {
            if (bottomView != null && wm != null && bottomView.getWindowToken
                    () != null) {
                wm.removeView(bottomView);
                bottomView = null;
                paramsBottom.height=0;
            }

            if (topView != null && wm != null && topView.getWindowToken()
                    != null) {
                wm.removeView(topView);
                topView = null;
                paramsTop.height=0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getNavigationBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private class MyObserver extends ContentObserver {
        MyObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            PrefSiempo.getInstance(context).write(PrefSiempo.IS_CONTACT_UPDATE, true);
//            PackageUtil.contactsUpdateInSearchList(context);
        }
    }

    /**
     * This broadcast is used to determine the application installed/uninstalled and update.
     */
    class AppInstallUninstall extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent != null && intent.getAction() != null) {
                    if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                        String installPackageName;
                        if (intent.getData().getEncodedSchemeSpecificPart() != null) {
                            if (!(intent.getExtras().containsKey(Intent.EXTRA_REPLACING) &&
                                    intent.getExtras().getBoolean(Intent.EXTRA_REPLACING, false))) {
                                installPackageName = intent.getData().getEncodedSchemeSpecificPart();
                                addAppFromBlockedList(installPackageName);
                                CoreApplication.getInstance().addOrRemoveApplicationInfo(true, installPackageName);
                                reloadData();
                            }
                        }
                    } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
                        String uninstallPackageName;
                        if (intent.getData().getEncodedSchemeSpecificPart() != null) {
                            if (!(intent.getExtras().containsKey(Intent.EXTRA_REPLACING) &&
                                    intent.getExtras().getBoolean(Intent.EXTRA_REPLACING, false))) {
                                uninstallPackageName = intent.getData().getSchemeSpecificPart();
                                if (!TextUtils.isEmpty(uninstallPackageName)) {
                                    new DBClient().deleteMsgByPackageName(uninstallPackageName);
                                    removeAppFromPreference(context, uninstallPackageName);
                                    removeAppFromBlockedList(uninstallPackageName);
                                    CoreApplication.getInstance().addOrRemoveApplicationInfo(false, uninstallPackageName);
                                    reloadData();
                                }
                            }
                        }
                    } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_CHANGED)) {
                        String packageName;
                        if (intent.getData().getEncodedSchemeSpecificPart() != null) {
                            packageName = intent.getData().getSchemeSpecificPart();
                            boolean isEnable = UIUtils.isAppInstalledAndEnabled(context, packageName);
                            if (isEnable) {
                                if (!CoreApplication.getInstance().getPackagesList().contains(packageName)) {
                                    addAppFromBlockedList(packageName);
                                    CoreApplication.getInstance().addOrRemoveApplicationInfo(true, packageName);
                                }
                            } else {
                                removeAppFromPreference(context, packageName);
                                removeAppFromBlockedList(packageName);
                                CoreApplication.getInstance().addOrRemoveApplicationInfo(false, packageName);
                            }
                            reloadData();
                        }
                    }
                    PrefSiempo.getInstance(context).write
                            (PrefSiempo.IS_APP_UPDATED, true);
                    EventBus.getDefault().post(new AppInstalledEvent(true));
                }
            } catch (Exception e) {
                e.printStackTrace();
                CoreApplication.getInstance().logException(e);
            }

        }
    }

    /**
     * This broadcast is used to determine the screen on/off flag.
     */

    public class UserPresentBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            if (intent != null && intent.getAction() != null && null != arg0) {
                if (PackageUtil.isSiempoLauncher(arg0) && (intent.getAction()
                        .equals
                                (Intent.ACTION_USER_PRESENT) ||
                        intent.getAction().equals(Intent.ACTION_SCREEN_ON))) {

                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                        PrefSiempo.getInstance(context).write(PrefSiempo
                                .LOCK_COUNTER_STATUS, false);
                    }
                    if (countDownTimerBreak != null) {
                        countDownTimerBreak.cancel();
                        countDownTimerBreak = null;
                        deterUsageRunning = false;
                        isFullScreenView = false;
                        whichPhaseRunning = 0;
                        resetAllTimer();
                        removeView();
                        PrefSiempo.getInstance(context).write(PrefSiempo.BREAK_TIME, 0L);
                    }
                } else if (intent.getAction().equals(Intent
                        .ACTION_SCREEN_OFF)) {
                    startLockScreenTimer();
                }
            }
        }
    }
}

