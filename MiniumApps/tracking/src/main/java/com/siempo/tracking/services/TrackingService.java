package com.siempo.tracking.services;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;

import com.siempo.tracking.model.LogEvent;
import com.siempo.tracking.util.TrackingLogger;
import com.siempo.tracking.util.TrackingPref_;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import minium.co.core.log.Tracer;
import minium.co.core.util.DateUtils;

/**
 * Created by Shahab on 1/13/2017.
 */

@EBean
public class TrackingService {

    @Pref
    TrackingPref_ trackingPrefs;

    @SystemService
    UsageStatsManager usageStatsManager;

    @App
    com.siempo.tracking.App application;

    @Background
    public void startServices() {
        if (trackingPrefs.isTrackingRunning().get()) {
            Tracer.d("Starting tracking services");
            GlobalTouchService_.intent(application).start();
            ScreenOnOffService_.intent(application).start();
            HomePressService_.intent(application).start();

        } else {
            Tracer.d("Stopping tracking services");
            getAppUsage();
            GlobalTouchService_.intent(application).stop();
            ScreenOnOffService_.intent(application).stop();
            HomePressService_.intent(application).stop();
            uploadFileToAWS();
        }
    }

    private void getAppUsage() {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        List<UsageStats> queryUsageStats = usageStatsManager
                .queryUsageStats(UsageStatsManager.INTERVAL_DAILY, cal.getTimeInMillis(),
                        System.currentTimeMillis());

        for (UsageStats stat : queryUsageStats) {

            if (stat.getTotalTimeInForeground() != 0) {

                String info = "Usage event\t" + stat.getPackageName()
                        + "\tDuration: " + DateUtils.interval(stat.getTotalTimeInForeground())
                        + "\tFrom: " + SimpleDateFormat.getDateTimeInstance().format(new Date(stat.getFirstTimeStamp()))
                        + "\tTo: " + SimpleDateFormat.getDateTimeInstance().format(new Date(stat.getLastTimeStamp()))
                        + "\tLast: " + SimpleDateFormat.getDateTimeInstance().format(new Date(stat.getLastTimeUsed()));


                TrackingLogger.log(new LogEvent(LogEvent.EventType.APP_USAGE)
                    .setDuration(DateUtils.interval(stat.getTotalTimeInForeground()))
                    .setFrom(SimpleDateFormat.getDateTimeInstance().format(new Date(stat.getFirstTimeStamp())))
                    .setTo(SimpleDateFormat.getDateTimeInstance().format(new Date(stat.getLastTimeStamp())))
                    .setLastUsed(SimpleDateFormat.getDateTimeInstance().format(new Date(stat.getLastTimeUsed()))));

                //TrackingLogger.log(info, null);
                Tracer.d(info);
            }
        }
    }

    void uploadFileToFTP() {
        ApiClient_.getInstance_(application).uploadFileToFTP();
    }

    void uploadFileToAWS() {
        ApiClient_.getInstance_(application).uploadFileToAWS();
    }
}
