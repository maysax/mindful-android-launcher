package co.siempo.phone.service;

import android.app.IntentService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.support.annotation.Nullable;

import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.app.Constants;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.db.DBUtility;
import co.siempo.phone.db.TableNotificationSms;
import co.siempo.phone.db.TableNotificationSmsDao;
import co.siempo.phone.log.Tracer;
import co.siempo.phone.utils.PackageUtil;

import static co.siempo.phone.utils.NotificationUtils.ANDROID_CHANNEL_ID;

/**
 * Created by rajeshjadi on 8/1/18.
 */

public class AlarmService extends IntentService {


    Context context;
    private AudioManager audioManager;

    public AlarmService() {
        super("MyServerOrWhatever");
    }

    public AlarmService(String name) {
        super(name);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                Notification.Builder builder = new Notification.Builder(this, ANDROID_CHANNEL_ID)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText("")
                        .setPriority(Notification.PRIORITY_LOW)
                        .setAutoCancel(true);
                Notification notification = builder.build();
                startForeground(Constants.ALARM_SERVICE_ID, notification);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        context = this;
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        Tracer.d("AlarmService: onHandleIntent");
        if (PackageUtil.isSiempoLauncher(context)) {
            List<TableNotificationSms> notificationList = DBUtility.getNotificationDao().queryBuilder().orderDesc(TableNotificationSmsDao.Properties.Notification_date).build().list();
            Tracer.d("AlarmService: notificationList.size" + notificationList.size());
            createNotification(notificationList, context);
        }
    }

    public void createNotification(List<TableNotificationSms> notificationList, Context context) {
        try {
            Tracer.d("AlarmService: createNotification");
            if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                int sound = audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM) / 2;
                audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, sound, 0);
                Tracer.d("AlarmService: audioManager");
            }
            Tracer.d("AlarmService: notificationList.size" + notificationList.size());
            for (int i = 0; i < notificationList.size(); i++) {
                TableNotificationSms notification = notificationList.get(i);
                if (notification.getPackageName() != null && !notification.getPackageName().equalsIgnoreCase("android")) {
                    Tracer.d("AlarmService: notification.getPackageName()" + notification.getPackageName());
                    PackageUtil.recreateNotification(notification, context, notification.getApp_icon());
                }
            }
            if (notificationList.size() >= 1) {
                Tracer.d("AlarmService: deleteAll");
                DBUtility.getNotificationDao().deleteAll();
            }

        } catch (Exception e) {
            Tracer.d("AlarmService: createNotification" + e.getMessage());
            e.printStackTrace();
            CoreApplication.getInstance().logException(e);
        }
    }

}
