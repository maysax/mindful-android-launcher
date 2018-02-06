package co.siempo.phone.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
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

/**
 * Created by rajeshjadi on 8/1/18.
 */

public class AlarmService extends IntentService {


    Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferencesLauncher3;
    private AudioManager audioManager;
    private Vibrator vibrator;
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
    protected void onHandleIntent(@Nullable Intent intent) {
        context = this;
        Tracer.d("-1");
        sharedPreferences = getSharedPreferences("DroidPrefs", 0);
        sharedPreferencesLauncher3 = getSharedPreferences("Launcher3Prefs", 0);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        everyHourList.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24));
        everyTwoHourList.addAll(Arrays.asList(1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23));
        everyFourHoursList.addAll(Arrays.asList(1, 4, 8, 12, 16, 20, 24));
        Tracer.d("0");
        run();
    }


    public void createNotification(List<TableNotificationSms> notificationList, Context context) {
        try {
            int sound = audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
            audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, sound, 0);

                for (int i = 0; i < notificationList.size(); i++) {
                    TableNotificationSms notification = notificationList.get(i);
                    if (notification.getPackageName() != null && !notification.getPackageName().equalsIgnoreCase("android")) {
                        PackageUtil.recreateNotification(notification, context, notification.getApp_icon());
                    }
                }
                if (notificationList.size() >= 1) {
                    PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                    PowerManager.WakeLock wl = pm != null ? pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG") : null;
                    if (wl != null) {
                        wl.acquire(2000);
                    }
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
            int tempoType = sharedPreferences.getInt("tempoType", 0);
            Tracer.d("3");
              if (tempoType == 1) {
                Tracer.d("4");
                int batchTime = sharedPreferences.getInt("batchTime", 15);
                if (batchTime == 15) {
                    if (systemMinutes == 0 || systemMinutes == 15 || systemMinutes == 30 || systemMinutes == 45) {
                        Tracer.d("Batch::" + "15 minute interval");
                        List<TableNotificationSms> notificationList = DBUtility.getNotificationDao().queryBuilder().orderDesc(TableNotificationSmsDao.Properties.Notification_date).build().list();
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
                String strTimeData = sharedPreferences.getString("onlyAt", "");
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
