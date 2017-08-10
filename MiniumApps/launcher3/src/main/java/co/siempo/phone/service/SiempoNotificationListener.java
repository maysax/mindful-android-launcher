package co.siempo.phone.service;

import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.text.SimpleDateFormat;
import java.util.Date;

import co.siempo.phone.app.Launcher3Prefs_;
import co.siempo.phone.db.DBClient;
import co.siempo.phone.db.DBUtility;
import co.siempo.phone.db.StatusBarNotificationStorage;
import co.siempo.phone.db.StatusBarNotificationStorageDao;
import co.siempo.phone.notification.NotificationUtility;
import co.siempo.phone.util.PackageUtil;
import minium.co.core.log.Tracer;

/**
 * Created by Shahab on 5/16/2017.
 */
@EService
public class SiempoNotificationListener extends NotificationListenerService {

    @Pref
    Launcher3Prefs_ prefs;

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification notification) {
        super.onNotificationPosted(notification);
        Tracer.d("Notification posted: " + getNotificationToString(notification));

        if (PackageUtil.isSiempoBlocker(notification.getId())) {
            requestInterruptionFilter(INTERRUPTION_FILTER_NONE);
            prefs.isNotificationBlockerRunning().put(true);
        } else if (prefs.isPauseActive().get() || prefs.isTempoActive().get()) {
            cancelNotification(notification.getKey());
            // saving the information in other place
        } else {
            if (PackageUtil.isCallPackage(notification.getPackageName()) || PackageUtil.isMsgPackage(notification.getPackageName())) {
                // should pass
            } else {
                if (PackageUtil.isSiempoLauncher(getApplicationContext())) {
                    cancelNotification(notification.getKey());
                    saveNotification(notification.getPackageName(), notification.getPostTime(),
                            notification.getNotification().tickerText);
                }
            }
        }
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
        } else if (PackageUtil.isMsgPackage(notification.getPackageName())) {
            new DBClient().deleteMsgByType(NotificationUtility.NOTIFICATION_TYPE_SMS);
        }
    }

    private String getNotificationToString(StatusBarNotification notification) {
        return "package: " + notification.getPackageName()
                + " Post time: " + SimpleDateFormat.getDateTimeInstance().format(new Date(notification.getPostTime()))
                + " Details: " + notification.getNotification().toString()
                + " Ticker: " + notification.getNotification().tickerText;
    }
}
