package co.siempo.phone.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.db.DBUtility;
import co.siempo.phone.db.TableNotificationSms;
import co.siempo.phone.db.TableNotificationSmsDao;
import co.siempo.phone.log.Tracer;
import co.siempo.phone.utils.PackageUtil;
import co.siempo.phone.utils.PrefSiempo;

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
        startForeground(1, new Notification());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        context = this;
        Tracer.d("-1");
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        everyHourList.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24));
        everyTwoHourList.addAll(Arrays.asList(1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23));
        everyFourHoursList.addAll(Arrays.asList(1, 4, 8, 12, 16, 20, 24));
        Tracer.d("0");
        run();
    }


    public void createNotification(List<TableNotificationSms> notificationList, Context context) {
        try {
            Tracer.d("Tracking createNotification");
            if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_VIBRATE
                    || audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
                int sound = audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
                audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, sound, 0);
            }

            for (int i = 0; i < notificationList.size(); i++) {
                TableNotificationSms notification = notificationList.get(i);
                if (notification.getPackageName() != null && !notification.getPackageName().equalsIgnoreCase("android")) {
                    Tracer.d("Tracking notification.getPackageName()");
                    PackageUtil.recreateNotification(notification, context, notification.getApp_icon());
                }
            }
            if (notificationList.size() >= 1) {
                DBUtility.getNotificationDao().deleteAll();
            }

        } catch (Exception e) {
            e.printStackTrace();
            CoreApplication.getInstance().logException(e);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private int getNumberOfNotifications(String name, NotificationManager notificationManager) {
        // [BEGIN get_active_notifications]
        // Query the currently displayed notifications.
        final StatusBarNotification[] activeNotifications = notificationManager
                .getActiveNotifications();
        // [END get_active_notifications]
        int count = 0;
        // Since the notifications might include a summary notification remove it from the count if
        // it is present.
        for (StatusBarNotification notification : activeNotifications) {
            if (name.equals(notification.getPackageName())) {
                count++;
            }
        }
        return count;
    }


    public void run() {
        Tracer.d("1");
        if (PackageUtil.isSiempoLauncher(context)) {
            Tracer.d("2");
            Calendar calendar = Calendar.getInstance();
            int systemHours = calendar.get(Calendar.HOUR_OF_DAY);
            int systemMinutes = calendar.get(Calendar.MINUTE);
            int tempoType = PrefSiempo.getInstance(context).read(PrefSiempo
                    .TEMPO_TYPE, 0);
            Tracer.d("3");
            if (tempoType == 1) {
                Tracer.d("4");
                int batchTime = PrefSiempo.getInstance(context).read(PrefSiempo
                        .BATCH_TIME, 15);
                if (batchTime == 15) {
                    if (systemMinutes == 0 || systemMinutes == 15 || systemMinutes == 30 || systemMinutes == 45) {
                        Tracer.d("Tracking Batch");
                        Tracer.d("Batch::" + "15 minute interval");
                        List<TableNotificationSms> notificationList = DBUtility.getNotificationDao().queryBuilder().orderDesc(TableNotificationSmsDao.Properties.Notification_date).build().list();
                        Tracer.d("Tracking notificationList.size" + notificationList.size());
                        createNotification(notificationList, context);
                    }
                } else if (batchTime == 30) {
                    if (systemMinutes == 0 || systemMinutes == 30) {
                        Tracer.d("Batch::" + "30 minute interval");
                        List<TableNotificationSms> notificationList = DBUtility.getNotificationDao().queryBuilder().orderDesc(TableNotificationSmsDao.Properties.Notification_date).build().list();
                        createNotification(notificationList, context);
                    }
                } else if (batchTime == 1) {
                    if (everyHourList.contains(systemHours) && systemMinutes == 0) {
                        Tracer.d("Batch::" + "Every Hour interval");
                        List<TableNotificationSms> notificationList = DBUtility.getNotificationDao().queryBuilder().orderDesc(TableNotificationSmsDao.Properties.Notification_date).build().list();
                        createNotification(notificationList, context);
                    }
                } else if (batchTime == 2) {
                    if (systemHours % 2 == 0 && systemMinutes == 0) {
                        Tracer.d("Batch::" + "Every 2 Hour interval");
                        List<TableNotificationSms> notificationList = DBUtility.getNotificationDao().queryBuilder().orderDesc(TableNotificationSmsDao.Properties.Notification_date).build().list();
                        createNotification(notificationList, context);
                    }
                } else if (batchTime == 4) {
                    if (systemHours % 4 == 0 && systemMinutes == 0) {
                        Tracer.d("Batch::" + "Every 4 Hour interval");
                        List<TableNotificationSms> notificationList = DBUtility.getNotificationDao().queryBuilder().orderDesc(TableNotificationSmsDao.Properties.Notification_date).build().list();
                        createNotification(notificationList, context);
                    }
                }

            } else if (tempoType == 2) {
                Tracer.d("5");
                String strTimeData = PrefSiempo.getInstance(context).read(PrefSiempo
                        .ONLY_AT, "12:01");
                if (!strTimeData.equalsIgnoreCase("")) {
                    Tracer.d("6");
                    String strTime[] = strTimeData.split(",");
                    Tracer.d("7" + strTime.length);
                    for (String str : strTime) {
                        int hours = Integer.parseInt(str.split(":")[0]);
                        int minutes = Integer.parseInt(str.split(":")[1]);

                        Tracer.d("Time " + "User" + hours + ":" + minutes + "System:" + systemHours + ":" + systemMinutes);
                        if (hours == systemHours && minutes == systemMinutes) {
                            Tracer.d("Only at::" + str);
                            List<TableNotificationSms> notificationList = DBUtility.getNotificationDao().queryBuilder().orderDesc(TableNotificationSmsDao.Properties.Notification_date).build().list();
                            createNotification(notificationList, context);
                        }
                    }
                }
            }

        }
    }
}
