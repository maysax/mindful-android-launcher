package com.itconquest.tracking.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.itconquest.tracking.services.TrackingService_;
import com.itconquest.tracking.util.TrackingLogger;

import org.androidannotations.annotations.EReceiver;

import java.util.Objects;

import minium.co.core.log.Tracer;

@EReceiver
public class PowerOnOffReceiver extends BroadcastReceiver {
    public PowerOnOffReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Tracer.d("Boot completed");
            TrackingService_.getInstance_(context).startServices();
            TrackingLogger.log("Power event\tOn", null);
        } else if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {
            TrackingLogger.log("Power event\tOff", null);
        }
    }
}
