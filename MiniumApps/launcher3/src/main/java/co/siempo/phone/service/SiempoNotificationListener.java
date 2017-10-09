package co.siempo.phone.service;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import co.siempo.phone.app.Launcher3Prefs_;
import co.siempo.phone.call.PhonecallReceiver;
import co.siempo.phone.db.DBClient;
import co.siempo.phone.db.DBUtility;
import co.siempo.phone.db.StatusBarNotificationStorage;
import co.siempo.phone.db.StatusBarNotificationStorageDao;
import co.siempo.phone.notification.NotificationUtility;
import co.siempo.phone.receiver.DndStartStopReceiver;
import co.siempo.phone.util.PackageUtil;
import co.siempo.phone.util.VibrationUtils;
import minium.co.core.app.CoreApplication;
import minium.co.core.log.Tracer;

/**
 * Created by Shahab on 5/16/2017.
 */

@EService
public class SiempoNotificationListener extends NotificationListenerService {

    public static final String TAG = SiempoNotificationListener.class.getName();


    @Pref
    Launcher3Prefs_ prefs;

    @SystemService
    AudioManager audioManager;

    @SystemService
    NotificationManager notificationManager;

    @Bean
    VibrationUtils vibrationUtils;

    @Pref
    Launcher3Prefs_ launcherPrefs;


    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification notification) {
        super.onNotificationPosted(notification);
        Tracer.d("Notification posted: " + getNotificationToString(notification));
        if (PackageUtil.isSiempoLauncher(this)
                || SiempoAccessibilityService.packageName.equalsIgnoreCase(getPackageName())) {
            if (PackageUtil.isSiempoBlocker(notification.getId())) {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                launcherPrefs.getCurrentProfile().put(0);
                prefs.isNotificationBlockerRunning().put(true);
            } else if (prefs.isPauseActive().get() || prefs.isTempoActive().get()) {
                cancelNotification(notification.getKey());
                saveNotification(notification.getPackageName(), notification.getPostTime(),
                        notification.getNotification().tickerText);
                // saving the information in other place
            } else if (launcherPrefs.getCurrentProfile().get() == 0) {
                if (CoreApplication.getInstance().getNormalModeList().contains(notification.getPackageName())) {

                } else {
                    cancelNotification(notification.getKey());
                    if (CoreApplication.getInstance().getVibrateList().contains(notification.getPackageName())) {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        vibrationUtils.vibrate(500);
                    } else if (CoreApplication.getInstance().getSilentList().contains(notification.getPackageName())) {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    }
                }
            } else if (launcherPrefs.getCurrentProfile().get() == 1) {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            } else if (launcherPrefs.getCurrentProfile().get() == 2) {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }
        }
    }


    private boolean isAppOnForeground(String appPackageName) {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(getPackageName())) {
                return false;
            }
        }
        return true;
    }

    public String getLauncherPackageName() {
        PackageManager localPackageManager = getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        return localPackageManager.resolveActivity(intent,
                PackageManager.MATCH_DEFAULT_ONLY).activityInfo.packageName;
    }

    private void saveNotification(String packageName, long postTime, CharSequence tickerText) {
        try {
            StatusBarNotificationStorageDao statusStorageDao = DBUtility.getStatusStorageDao();
            StatusBarNotificationStorage storage = new StatusBarNotificationStorage();
            storage.setContent(tickerText.toString());
            storage.setPackageName(packageName);
            storage.setPostTime(postTime);
            statusStorageDao.insert(storage);
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }
    }


    @Override
    public void onNotificationRemoved(StatusBarNotification notification) {
        super.onNotificationRemoved(notification);
        Tracer.d("Notification removed: " + getNotificationToString(notification));

        if (PackageUtil.isSiempoBlocker(notification.getId())) {
            prefs.isNotificationBlockerRunning().put(false);
        }
        if (!PackageUtil.isSiempoLauncher(this)
                && !SiempoAccessibilityService.packageName.equalsIgnoreCase(getPackageName())) {
            if (PackageUtil.isMsgPackage(notification.getPackageName())) {
                new DBClient().deleteMsgByType(NotificationUtility.NOTIFICATION_TYPE_SMS);
            } else if (PackageUtil.isCallPackage(notification.getPackageName())) {
                new DBClient().deleteMsgByType(NotificationUtility.NOTIFICATION_TYPE_CALL);
            }
        }
    }

    private String getNotificationToString(StatusBarNotification notification) {
        return "package: " + notification.getPackageName()
                + "Id: " + notification.getId()
                + " Post time: " + SimpleDateFormat.getDateTimeInstance().format(new Date(notification.getPostTime()))
                + " Details: " + notification.getNotification().toString()
                + " Ticker: " + notification.getNotification().tickerText;
    }


}
