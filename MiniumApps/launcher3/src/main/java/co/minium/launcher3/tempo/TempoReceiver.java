package co.minium.launcher3.tempo;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import co.minium.launcher3.app.Launcher3Prefs_;
import minium.co.core.util.UIUtils;

/**
 * Created by Shahab on 3/30/2017.
 */
@EReceiver
public class TempoReceiver extends BroadcastReceiver {

    @SystemService
    NotificationManager notificationManager;

    @Pref
    Launcher3Prefs_ launcherPrefs;

    public static final String KEY_IS_TEMPO = "isTempo";

    public TempoReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (launcherPrefs.isPauseActive().get()) {
            // pass
        } else if (launcherPrefs.isTempoActive().get()) {
            // show notifications
            UIUtils.toast(context, "Show notifications");
        } else {
            // pass
        }
    }
}
