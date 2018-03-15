package co.siempo.phone.service;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import co.siempo.phone.R;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.db.DBClient;
import co.siempo.phone.event.AppInstalledEvent;
import co.siempo.phone.event.OnBackPressedEvent;
import co.siempo.phone.models.AppMenu;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.UIUtils;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

import static co.siempo.phone.utils.NotificationUtils.ANDROID_CHANNEL_ID;

/**
 * This background service used for detect torch status and feature used for any other background status.
 */

public class StatusBarService extends Service {

    Context context;
    private MyObserver myObserver;
    private AppInstallUninstall appInstallUninstall;
    private Vibrator vibrator;

    public StatusBarService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        registerObserverForContact();
        registerObserverForAppInstallUninstall();
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder builder = new Notification.Builder(this, ANDROID_CHANNEL_ID)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("")
                    .setAutoCancel(true);
            Notification notification = builder.build();
            startForeground(1, notification);
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
        super.onDestroy();
    }

    /**
     * Remove uninstall app if it contains in blocked list OR HelpfulRobots
     *
     * @param uninstallPackageName
     */
    public void removeAppFromBlockedList(String uninstallPackageName) {
        Set<String> blockedApps = new HashSet<>();
        Set<String> removeApps = new HashSet<>();
        blockedApps = PrefSiempo.getInstance(context).read(PrefSiempo.BLOCKED_APPLIST,
                new HashSet<String>());
        try {


            if (blockedApps.contains(uninstallPackageName)) {
                blockedApps.remove(uninstallPackageName);
            }
            PrefSiempo.getInstance(context).write(PrefSiempo.BLOCKED_APPLIST,
                    blockedApps);


            ArrayList<String> disableApps = new ArrayList<>();
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
        Set<String> blockedApps = new HashSet<>();
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

    class AppInstallUninstall extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent != null && intent.getAction() != null) {
                    if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                        String installPackageName;
                        if (intent.getData().getEncodedSchemeSpecificPart() != null) {
                            installPackageName = intent.getData().getEncodedSchemeSpecificPart();
                            addAppFromBlockedList(installPackageName);
                            Log.d("Testing with device.", "Added" + installPackageName);
                            CoreApplication.getInstance().addOrRemoveApplicationInfo(true, installPackageName);
                            reloadData();
                        }

                    } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
                        String uninstallPackageName;
                        if (intent.getData().getEncodedSchemeSpecificPart() != null) {
                            if (!(intent.getExtras().containsKey(Intent.EXTRA_REPLACING) &&
                                    intent.getExtras().getBoolean(Intent.EXTRA_REPLACING, false))) {
                                uninstallPackageName = intent.getData().getSchemeSpecificPart();
                                Log.d("Testing with device.", "Removed" + uninstallPackageName);
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
                                addAppFromBlockedList(packageName);
                            } else {
                                removeAppFromPreference(context, packageName);
                                removeAppFromBlockedList(packageName);
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

    private void reloadData() {
        new LoadToolPane(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new LoadFavoritePane(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new LoadJunkFoodPane(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}
