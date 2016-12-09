package com.itconquest.tracking.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.itconquest.tracking.util.TrackingLogger;

import org.androidannotations.annotations.EReceiver;

import java.util.Objects;

@EReceiver
public class PowerOnOffReceiver extends BroadcastReceiver {
    public PowerOnOffReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            TrackingLogger.log("Phone turned on", null);
        } else if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {
            TrackingLogger.log("Phone turned off", null);
        }
    }
}
