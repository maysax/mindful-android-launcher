package co.siempo.phone.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.text.SimpleDateFormat;
import java.util.Date;

import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3App;
import co.siempo.phone.app.Launcher3Prefs_;
import co.siempo.phone.db.DBUtility;
import co.siempo.phone.db.DaoSession;
import co.siempo.phone.db.StatusBarNotificationStorage;
import co.siempo.phone.db.StatusBarNotificationStorageDao;
import co.siempo.phone.db.TableNotificationSms;
import co.siempo.phone.notification.NotificationUtility;
import co.siempo.phone.util.PackageUtil;
import minium.co.core.app.CoreApplication;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.UIUtils;

import static android.media.AudioManager.RINGER_MODE_SILENT;

/**
 * Created by Shahab on 3/17/2017.
 */
@EService
public class NotificationBlockerService extends NotificationListenerService {

    private static final int NOTIFICATION_ID = 4432;
    private int currentFilter = INTERRUPTION_FILTER_ALL;
    private int currentRingerMode = AudioManager.RINGER_MODE_NORMAL;

    @Pref
    Launcher3Prefs_ prefs;

    @SystemService
    AudioManager audioManager;

    @SystemService
    NotificationManager notificationManager;

    private Notification.Builder builder;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        builder = new Notification.Builder(this);
    }

    //In the Service I use this to enable and disable silent mode(or priority...)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean start = intent != null && intent.getBooleanExtra("start", false);

        if (start) {
            if (!prefs.isNotificationBlockerRunning().get()) {
                currentFilter = getCurrentInterruptionFilter();
                currentRingerMode = audioManager.getRingerMode();
                Tracer.i("NotificationBlockerService stating ...");

                //Check if at least Lollipop, otherwise use old method
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    requestInterruptionFilter(INTERRUPTION_FILTER_NONE);
                    audioManager.setRingerMode(RINGER_MODE_SILENT);
                } else {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                }
            } else {
                Tracer.i("NotificationBlockerService already running ...");
            }
            showNotification();
        } else {
            Tracer.i("NotificationBlockerService stopping ...");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                requestInterruptionFilter(currentFilter);
                audioManager.setRingerMode(currentRingerMode);
            }
            else {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            }

            stopForeground(true);
        }

        prefs.isNotificationBlockerRunning().put(start);

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


    private void showNotification() {
        Intent i = new Intent(this, CoreActivity.class);

        Notification notification = builder.setContentIntent(null)
                .setContentTitle(getString(R.string.msg_siempo_active_title))
                .setContentText(getString(R.string.msg_siempo_active_text))
                .setSmallIcon(R.drawable.ic_siempo_notification)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setWhen(System.currentTimeMillis())
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

}
