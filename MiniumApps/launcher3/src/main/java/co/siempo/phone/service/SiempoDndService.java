package co.siempo.phone.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.SystemService;

import co.siempo.phone.R;
import minium.co.core.ui.CoreActivity;

/**
 * Created by Shahab on 5/17/2017.
 */
@EService
public class SiempoDndService extends Service {

    private static final int NOTIFICATION_ID = 4432;
    public static final String KEY_START = "start";

    @SystemService
    NotificationManager manager;

    private Notification.Builder builder;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        builder = new Notification.Builder(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String start = intent.getStringExtra(KEY_START);
        if (start != null) {
            showNotification();
        } else {
            stopForeground(true);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
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
