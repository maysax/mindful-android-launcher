package co.siempo.phone.service;

import android.app.IntentService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.app.Constants;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.db.DBUtility;
import co.siempo.phone.db.TableNotificationSms;
import co.siempo.phone.db.TableNotificationSmsDao;
import co.siempo.phone.log.Tracer;
import co.siempo.phone.utils.PackageUtil;
import co.siempo.phone.utils.PrefSiempo;

import static co.siempo.phone.utils.NotificationUtils.ANDROID_CHANNEL_ID;

/**
 * Created by rajeshjadi on 8/1/18.
 */

public class AlarmService extends IntentService {


    Context context;
    private AudioManager audioManager;
    private ArrayList<Integer> everyHourList = new ArrayList<>();
    private ArrayList<Integer> everyTwoHourList = new ArrayList<>();
    private ArrayList<Integer> everyFourHoursList = new ArrayList<>();

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
            Notification.Builder builder = new Notification.Builder(this, ANDROID_CHANNEL_ID)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("")
                    .setPriority(Notification.PRIORITY_LOW)
                    .setAutoCancel(true);
            Notification notification = builder.build();
            startForeground(Constants.ALARM_SERVICE_ID, notification);
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        context = this;
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        everyHourList.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24));
        everyTwoHourList.addAll(Arrays.asList(1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23));
        everyFourHoursList.addAll(Arrays.asList(1, 4, 8, 12, 16, 20, 24));
        Tracer.d("AlarmService: onHandleIntent");
        run();
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


    public void run() {
        if (PackageUtil.isSiempoLauncher(context)) {
            Calendar calendar = Calendar.getInstance();
            int systemHours = calendar.get(Calendar.HOUR_OF_DAY);
            int systemMinutes = calendar.get(Calendar.MINUTE);
            int tempoType = PrefSiempo.getInstance(context).read(PrefSiempo
                    .TEMPO_TYPE, 0);
            Tracer.d("AlarmService: TempoType::" + tempoType);
            if (tempoType == 1) {
                Tracer.d("AlarmService: TempoType::Batch");
                int batchTime = PrefSiempo.getInstance(context).read(PrefSiempo
                        .BATCH_TIME, 15);
                Tracer.d("AlarmService: batchTime" + batchTime);
                if (batchTime == 15) {
                    if (systemMinutes == 0 || systemMinutes == 15 || systemMinutes == 30 || systemMinutes == 45) {
                        Tracer.i("AlarmService: batchTime 15 minute interval");
                        List<TableNotificationSms> notificationList = DBUtility.getNotificationDao().queryBuilder().orderDesc(TableNotificationSmsDao.Properties.Notification_date).build().list();
                        Tracer.i("AlarmService: notificationList.size" + notificationList.size());
                        createNotification(notificationList, context);
                    }
                } else if (batchTime == 30) {
                    if (systemMinutes == 0 || systemMinutes == 30) {
                        Tracer.i("AlarmService: batch Time 30 minute interval");
                        List<TableNotificationSms> notificationList = DBUtility.getNotificationDao().queryBuilder().orderDesc(TableNotificationSmsDao.Properties.Notification_date).build().list();
                        Tracer.i("AlarmService: notificationList.size" + notificationList.size());
                        createNotification(notificationList, context);
                    }
                } else if (batchTime == 1) {
                    if (everyHourList.contains(systemHours) && systemMinutes == 0) {
                        Tracer.i("AlarmService: batch Every Hour interval");
                        List<TableNotificationSms> notificationList = DBUtility.getNotificationDao().queryBuilder().orderDesc(TableNotificationSmsDao.Properties.Notification_date).build().list();
                        Tracer.i("AlarmService: notificationList.size" + notificationList.size());
                        createNotification(notificationList, context);
                    }
                } else if (batchTime == 2) {
                    if (systemHours % 2 == 0 && systemMinutes == 0) {
                        Tracer.i("AlarmService: batch Every 2 Hour interval");
                        List<TableNotificationSms> notificationList = DBUtility.getNotificationDao().queryBuilder().orderDesc(TableNotificationSmsDao.Properties.Notification_date).build().list();
                        Tracer.i("AlarmService: notificationList.size" + notificationList.size());
                        createNotification(notificationList, context);
                    }
                } else if (batchTime == 4) {
                    if (systemHours % 4 == 0 && systemMinutes == 0) {
                        Tracer.i("AlarmService: batch Every 4 Hour interval");
                        List<TableNotificationSms> notificationList = DBUtility.getNotificationDao().queryBuilder().orderDesc(TableNotificationSmsDao.Properties.Notification_date).build().list();
                        Tracer.i("AlarmService: notificationList.size" + notificationList.size());
                        createNotification(notificationList, context);
                    }
                }

            } else if (tempoType == 2) {
                Tracer.d("AlarmService: TempoType::OnlyAt");
                String strTimeData = PrefSiempo.getInstance(context).read(PrefSiempo
                        .ONLY_AT, "12:01");
                Tracer.i("AlarmService: onlyAt start");
                if (!strTimeData.equalsIgnoreCase("")) {
                    String strTime[] = strTimeData.split(",");
                    Tracer.i("AlarmService: onlyAt strTime.length" + strTime.length);
                    for (String str : strTime) {
                        int hours = Integer.parseInt(str.split(":")[0]);
                        int minutes = Integer.parseInt(str.split(":")[1]);
                        Tracer.i("AlarmService: Time " + "User" + hours + ":" + minutes + "System:" + systemHours + ":" + systemMinutes);
                        if (hours == systemHours && minutes == systemMinutes) {
                            Tracer.i("AlarmService: onlyAt match condition" + str);
                            List<TableNotificationSms> notificationList = DBUtility.getNotificationDao().queryBuilder().orderDesc(TableNotificationSmsDao.Properties.Notification_date).build().list();
                            Tracer.i("AlarmService: notificationList.size" + notificationList.size());
                            createNotification(notificationList, context);
                        }
                    }
                }
            }

        }
    }
}
