package com.itconquest.tracking.listener;

import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.itconquest.tracking.util.TrackingLogger;

import org.androidannotations.annotations.EService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import minium.co.core.log.Tracer;

/**
 * Created by Shahab on 12/6/2016.
 */
@EService
public class NotificationListener extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String info = "Notification event\t" + sbn.getPackageName()
                + "\tPosted: " + SimpleDateFormat.getDateTimeInstance().format(new Date(sbn.getPostTime()))
                + "\tText: " + sbn.getNotification().tickerText;
        Tracer.d("onNotificationPosted " + sbn.toString());

        TrackingLogger.log(info, null);

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);

    }
}
