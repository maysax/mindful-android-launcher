package com.siempo.tracking.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.siempo.tracking.model.LogEvent;
import com.siempo.tracking.util.TrackingLogger;

import org.androidannotations.annotations.EReceiver;

import minium.co.core.log.Tracer;

/**
 * Created by Shahab on 1/5/2017.
 */

@EReceiver
public class HomePressReceiver extends BroadcastReceiver {
    final String SYSTEM_DIALOG_REASON_KEY = "reason";
    final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
    final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
            String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
            if (reason != null) {
                Tracer.d("Home pressed: action = " + action + " reason: " + reason);
                TrackingLogger.log(new LogEvent(LogEvent.EventType.HOME).setEffect(reason));
                //TrackingLogger.log("Home event\t" + reason, null);

            }
        }
    }
}