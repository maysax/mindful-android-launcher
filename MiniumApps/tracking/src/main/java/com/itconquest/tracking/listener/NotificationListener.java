package com.itconquest.tracking.listener;

import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.itconquest.tracking.util.TrackingLogger;

import org.androidannotations.annotations.EService;

import minium.co.core.log.Tracer;

/**
 * Created by Shahab on 12/6/2016.
 */
@EService
public class NotificationListener extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Tracer.d("onNotificationPosted " + sbn.toString());
        TrackingLogger.log(sbn.toString(), null);

    }
}
