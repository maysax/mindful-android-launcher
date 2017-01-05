package minium.co.launcher2.flow;

import android.annotation.TargetApi;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.provider.ContactsContract;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.itconquest.tracking.util.TrackingLogger;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.text.SimpleDateFormat;
import java.util.Date;

import minium.co.core.app.DroidPrefs_;
import minium.co.core.log.Tracer;

import static de.greenrobot.event.EventBus.TAG;

/**
 * Created by Shahab on 5/16/2016.
 */
@EService
public class SiempoNotificationService extends NotificationListenerService {

    private int currentFilter = INTERRUPTION_FILTER_ALL;

    @Pref
    DroidPrefs_ prefs;

    //In the Service I use this to enable and disable silent mode(or priority...)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean start = intent != null && intent.getBooleanExtra("start", false);

        if (start && !prefs.isSiempoNotificationServiceRunning().get()) {
            currentFilter = getCurrentInterruptionFilter();
            Tracer.d("Starting service");

            //Check if at least Lollipop, otherwise use old method
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                requestInterruptionFilter(INTERRUPTION_FILTER_NONE);
            else {
                AudioManager am = (AudioManager) getBaseContext().getSystemService(AUDIO_SERVICE);
                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }
        } else {
            Tracer.d("Stopping service");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                requestInterruptionFilter(currentFilter);
            else {
                AudioManager am = (AudioManager) getBaseContext().getSystemService(AUDIO_SERVICE);
                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            }
        }

        prefs.isSiempoNotificationServiceRunning().put(start);

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
        Tracer.d("notification posted");
        if (Build.VERSION.SDK_INT >= 21) {
            cancelNotification(notification.getKey());
            String info = "Notification package: " + notification.getPackageName()
                    + " Post time: " + SimpleDateFormat.getDateTimeInstance().format(new Date(notification.getPostTime()))
                    + " Details: " + notification.getNotification().toString();

            Tracer.d(info);
            //TrackingLogger.log(info, null);
        }
    }

    public void onNotificationRemoved(StatusBarNotification notification) {
        Tracer.d("notification removed " + notification.getPackageName());
    }
}
