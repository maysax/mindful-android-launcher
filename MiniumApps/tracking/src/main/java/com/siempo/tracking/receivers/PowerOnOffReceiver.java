package com.siempo.tracking.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.siempo.tracking.model.LogEvent;
import com.siempo.tracking.services.TrackingService_;
import com.siempo.tracking.util.TrackingLogger;

import org.androidannotations.annotations.EReceiver;

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
            //TrackingLogger.log("Power event\tOn", null);
            TrackingLogger.log(new LogEvent(LogEvent.EventType.POWER).setEffect("On"));
        } else if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {
            //TrackingLogger.log("Power event\tOff", null);
            TrackingLogger.log(new LogEvent(LogEvent.EventType.POWER).setEffect("Off"));
        }
    }
}
