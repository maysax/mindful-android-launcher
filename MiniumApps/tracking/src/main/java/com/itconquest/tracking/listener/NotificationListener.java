package com.itconquest.tracking.listener;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import org.androidannotations.annotations.EService;

import minium.co.core.log.Tracer;

/**
 * Created by Shahab on 12/6/2016.
 */
@EService
public class NotificationListener extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap) {
        super.onNotificationPosted(sbn, rankingMap);
        Tracer.d("onNotificationPosted " + sbn.toString());
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap) {
        super.onNotificationRemoved(sbn, rankingMap);
        Tracer.d("onNotificationRemoved " + sbn.toString());
    }
}
