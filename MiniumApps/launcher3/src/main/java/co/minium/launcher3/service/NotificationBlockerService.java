package co.minium.launcher3.service;

import android.annotation.TargetApi;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.text.SimpleDateFormat;
import java.util.Date;

import co.minium.launcher3.app.Launcher3App;
import co.minium.launcher3.app.Launcher3Prefs_;
import co.minium.launcher3.db.DBUtility;
import co.minium.launcher3.db.DaoSession;
import co.minium.launcher3.db.StatusBarNotificationStorage;
import co.minium.launcher3.db.StatusBarNotificationStorageDao;
import co.minium.launcher3.db.TableNotificationSms;
import co.minium.launcher3.notification.NotificationUtility;
import minium.co.core.app.CoreApplication;
import minium.co.core.log.Tracer;
import minium.co.core.util.UIUtils;

/**
 * Created by Shahab on 3/17/2017.
 */
@EService
public class NotificationBlockerService extends NotificationListenerService {

    private int currentFilter = INTERRUPTION_FILTER_ALL;

    @Pref
    Launcher3Prefs_ prefs;

    //In the Service I use this to enable and disable silent mode(or priority...)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean start = intent != null && intent.getBooleanExtra("start", false);

        if (start && !prefs.isNotificationBlockerServiceRunning().get()) {
            currentFilter = getCurrentInterruptionFilter();
            Tracer.i("Starting service");

            //Check if at least Lollipop, otherwise use old method
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                requestInterruptionFilter(INTERRUPTION_FILTER_NONE);
            else {
                AudioManager am = (AudioManager) getBaseContext().getSystemService(AUDIO_SERVICE);
                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }
        } else {
            Tracer.i("Stopping service");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                requestInterruptionFilter(currentFilter);
            else {
                AudioManager am = (AudioManager) getBaseContext().getSystemService(AUDIO_SERVICE);
                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            }
        }

        prefs.isNotificationBlockerServiceRunning().put(start);

        return super.onStartCommand(intent, flags, startId);

    }

    @Override public void onListenerConnected() {
        Tracer.d("onListenerConnected()");
    }
    @Override public void onListenerHintsChanged(int hints) {
        Tracer.d("onListenerHintsChanged(" + hints + ')');
    }

    @Override
    public void onInterruptionFilterChanged(int interruptionFilter) {
        Tracer.d("onInterruptionFilterChanged(" + interruptionFilter + ')');
    }

    @TargetApi(21)
    public void onNotificationPosted(StatusBarNotification notification) {
        String info = "Notification package: " + notification.getPackageName()
                + " Post time: " + SimpleDateFormat.getDateTimeInstance().format(new Date(notification.getPostTime()))
                + " Details: " + notification.getNotification().toString()
                + " Ticker: " + notification.getNotification().tickerText;

        Tracer.d("Notification posted: " + info);

        if (prefs.isPauseActive().get() || prefs.isTempoActive().get()) {
            cancelNotification(notification.getKey());
        } else {
            String packageName = notification.getPackageName();
            if (packageName.contains("telecom") || packageName.contains("dialer") || packageName.contains("messaging") || packageName.contains("minium")) {
                // should pass
            } else {
                if (UIUtils.isSiempoLauncher(getApplicationContext())) {
                    cancelNotification(notification.getKey());
                    saveNotification(notification.getPackageName(), notification.getPostTime(),
                            notification.getNotification().tickerText);
                }
            }
        }
    }

    public void onNotificationRemoved(StatusBarNotification notification) {
        Tracer.d("notification removed " + notification.getPackageName());
    }

    private void saveNotification(String packageName, long postTime, CharSequence tickerText) {
        StatusBarNotificationStorageDao statusStorageDao = DBUtility.getStatusStorageDao();

        StatusBarNotificationStorage storage = new StatusBarNotificationStorage();
        storage.setContent(tickerText.toString());
        storage.setPackageName(packageName);
        storage.setPostTime(postTime);
        statusStorageDao.insert(storage);
    }
}
