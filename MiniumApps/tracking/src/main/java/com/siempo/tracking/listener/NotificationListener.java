package com.siempo.tracking.listener;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.siempo.tracking.model.LogEvent;
import com.siempo.tracking.util.TrackingLogger;

import org.androidannotations.annotations.EService;

import java.text.SimpleDateFormat;
import java.util.Date;

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

        TrackingLogger.log(new LogEvent(LogEvent.EventType.NOTIFICATION)
                .setPkg(sbn.getPackageName())
                .setPostedOn(SimpleDateFormat.getDateTimeInstance().format(new Date(sbn.getPostTime())))
                .setText(sbn.getNotification() == null ? "..." : sbn.getNotification().tickerText));

        //TrackingLogger.log(info, null);

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }
}
