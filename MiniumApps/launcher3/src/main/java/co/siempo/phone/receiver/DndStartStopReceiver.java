package co.siempo.phone.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import co.siempo.phone.app.Launcher3Prefs_;
import co.siempo.phone.service.SiempoDndService_;
import minium.co.core.log.Tracer;

import static android.media.AudioManager.RINGER_MODE_SILENT;

/**
 * Created by Shahab on 5/16/2017.
 */


@EReceiver
public class DndStartStopReceiver extends BroadcastReceiver {
    @Pref
    Launcher3Prefs_ prefs;

    @SystemService
    AudioManager audioManager;

    private boolean shouldStart;

    @SystemService
    NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (prefs.isPauseActive().get()) shouldStart = true;
        else if (prefs.isTempoActive().get()) shouldStart = true;
        else shouldStart = false;


        if (shouldStart) {
            if (!prefs.isNotificationBlockerRunning().get()) startBlocker(context, true);
        } else {
            /**
             * SSA-36 : NotificationBlockerRunning will use in further purpose
             */
//            if (prefs.isNotificationBlockerRunning().get()) {
            startBlocker(context, false);
//            }
        }
    }

    private void startBlocker(Context context, boolean start) {
        if (start) {
            Tracer.d("Starting Dnd mode");
            SiempoDndService_.intent(context).extra(SiempoDndService_.KEY_START, "start").start();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                    && !notificationManager.isNotificationPolicyAccessGranted()) {
            } else {
                audioManager.setRingerMode(RINGER_MODE_SILENT);
            }
            Log.d("Check Silent ", "" + DndStartStopReceiver.class.getClass().getName());
        } else {
            Tracer.d("Stopping Dnd mode");
            SiempoDndService_.intent(context).start();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                    && !notificationManager.isNotificationPolicyAccessGranted()) {
            } else {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            }
        }
    }
}
