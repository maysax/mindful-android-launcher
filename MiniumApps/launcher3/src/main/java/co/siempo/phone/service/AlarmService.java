package co.siempo.phone.service;

import android.app.IntentService;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import co.siempo.phone.R;
import co.siempo.phone.db.DBUtility;
import co.siempo.phone.db.TableNotificationSms;
import co.siempo.phone.db.TableNotificationSmsDao;
import co.siempo.phone.util.PackageUtil;
import minium.co.core.app.CoreApplication;
import minium.co.core.log.Tracer;
import minium.co.core.util.UIUtils;

import static co.siempo.phone.service.StatusBarService.getTimeFormat;

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
    private MediaPlayer notificationMediaPlayer;

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


    public void recreateNotification(List<TableNotificationSms> notificationList, Context context, boolean isAllowNotificationOnLockScreen) {
        try {
            for (int i = 0; i < notificationList.size(); i++) {
                TableNotificationSms notification = notificationList.get(i);
                if (!notification.getPackageName().equalsIgnoreCase("android")) {
                    NotificationCompat.Builder b = new NotificationCompat.Builder(context, "" + notification.getId());
                    Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage(notification.getPackageName());
                    PendingIntent contentIntent = null;
                    if (launchIntentForPackage != null) {
                        int requestID = (int) System.currentTimeMillis();
                        contentIntent = PendingIntent.getActivity(context, requestID, launchIntentForPackage, PendingIntent.FLAG_UPDATE_CURRENT);
                        launchIntentForPackage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    }
                    Bitmap bitmap = notification.getUser_icon() != null ? UIUtils.convertBytetoBitmap(notification.getUser_icon()) : null;
                    DateFormat sdf = new SimpleDateFormat(getTimeFormat(context), Locale.getDefault());
                    String time = sdf.format(notification.get_date());
                    RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.custom_notification_card);
                    contentView.setImageViewBitmap(R.id.imgAppIcon, CoreApplication.getInstance().iconList.get(notification.getPackageName()));
                    if (null != bitmap) {
                        contentView.setImageViewBitmap(R.id.imgUserImage, bitmap);
                    } else {
                        contentView.setImageViewBitmap(R.id.imgUserImage, null);
                    }
                    contentView.setTextViewText(R.id.txtUserName, notification.get_contact_title());
                    contentView.setTextViewText(R.id.txtMessage, notification.get_message());
                    contentView.setTextViewText(R.id.txtTime, time);
                    String applicationNameFromPackageName = CoreApplication.getInstance().getApplicationNameFromPackageName(notification.getPackageName());
                    contentView.setTextViewText(R.id.txtAppName, applicationNameFromPackageName);
                    b.setAutoCancel(true)
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.drawable.ic_airplane_air_balloon)
                            .setPriority(Notification.PRIORITY_HIGH)
                            .setContentTitle(notification.get_contact_title())
                            .setContentText(notification.get_message())
                            .setContentIntent(contentIntent)
                            .setCustomContentView(contentView)
                            .setCustomBigContentView(contentView)
                            .setGroup("Siempo")
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setDefaults(Notification.DEFAULT_LIGHTS)
                            .setContentInfo("Info");
//
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= 26) {
                        int importance = NotificationManager.IMPORTANCE_HIGH;
                        if (applicationNameFromPackageName != null) {
                            NotificationChannel mChannel = new NotificationChannel(applicationNameFromPackageName, applicationNameFromPackageName, importance);
                            mChannel.enableLights(true);
                            mChannel.setLightColor(Color.RED);
                            b.setChannelId(applicationNameFromPackageName);
                            if (notificationManager != null) {
                                notificationManager.createNotificationChannel(mChannel);
                            }
                        }
                    }
                    if (notificationManager != null) {
                        notificationManager.notify(notification.getId().intValue(), b.build());
                    }
                }

            }
            if (notificationList.size() >= 1) {
                if (!CoreApplication.getInstance().isCallisRunning() && sharedPreferences.getInt("tempoSoundProfile", 0) != 0) {
                    KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                    if (((myKM != null && myKM.inKeyguardRestrictedInputMode()) && !isAllowNotificationOnLockScreen)) {
                        // hide notification on lock screen so mute the notification sound.
                    } else {
                        Tracer.d("Play Sound Vibrate");
                        playNotificationSoundVibrate();
                    }
                }
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                PowerManager.WakeLock wl = pm != null ? pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG") : null;
                wl.acquire(3000);
                DBUtility.getNotificationDao().deleteAll();
            }
        } catch (Exception e) {
            e.printStackTrace();
            CoreApplication.getInstance().logException(e);
        }
    }

    public void playNotificationSoundVibrate() {
        try {
            if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
                Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                notificationMediaPlayer = new MediaPlayer();
                notificationMediaPlayer.setDataSource(this, alert);
                final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                if (audioManager != null && audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                    notificationMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, max, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                    notificationMediaPlayer.setLooping(false);
                    notificationMediaPlayer.prepare();
                    if (!notificationMediaPlayer.isPlaying()) {
                        notificationMediaPlayer.start();
                    }
                    vibrator.vibrate(500);
                    notificationMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            if (notificationMediaPlayer != null) {
                                notificationMediaPlayer.stop();
                                notificationMediaPlayer.release();
                            }
                            notificationMediaPlayer = null;
                        }
                    });
                }
            }
            // }
        } catch (Exception e) {
            CoreApplication.getInstance().logException(e);
            e.printStackTrace();
        }
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
            boolean isTempoNotificationControlsDisabled = sharedPreferences.getBoolean("isTempoNotificationControlsDisabled", false);
            if (!isTempoNotificationControlsDisabled && tempoType == 1) {
                Tracer.d("4");
                int batchTime = sharedPreferences.getInt("batchTime", 15);
                if (batchTime == 15) {
                    if (systemMinutes == 0 || systemMinutes == 15 || systemMinutes == 30 || systemMinutes == 45) {
                        Tracer.d("Batch::" + "15 minute interval");
                        List<TableNotificationSms> notificationList = DBUtility.getNotificationDao().queryBuilder().orderDesc(TableNotificationSmsDao.Properties.Notification_date).build().list();
                        recreateNotification(notificationList, context, sharedPreferencesLauncher3.getBoolean("isAllowNotificationOnLockScreen", true));
                    }
                } else if (batchTime == 30) {
                    if (systemMinutes == 0 || systemMinutes == 30) {
                        Tracer.d("Batch::" + "30 minute interval");
                        List<TableNotificationSms> notificationList = DBUtility.getNotificationDao().queryBuilder().orderDesc(TableNotificationSmsDao.Properties.Notification_date).build().list();
                        recreateNotification(notificationList, context, sharedPreferencesLauncher3.getBoolean("isAllowNotificationOnLockScreen", true));
                    }
                } else if (batchTime == 1) {
                    if (everyHourList.contains(systemHours) && systemMinutes == 0) {
                        Tracer.d("Batch::" + "Every Hour interval");
                        List<TableNotificationSms> notificationList = DBUtility.getNotificationDao().queryBuilder().orderDesc(TableNotificationSmsDao.Properties.Notification_date).build().list();
                        recreateNotification(notificationList, context, sharedPreferencesLauncher3.getBoolean("isAllowNotificationOnLockScreen", true));
                    }
                } else if (batchTime == 2) {
                    if (everyTwoHourList.contains(systemHours) && systemMinutes == 0) {
                        Tracer.d("Batch::" + "Every 2 Hour interval");
                        List<TableNotificationSms> notificationList = DBUtility.getNotificationDao().queryBuilder().orderDesc(TableNotificationSmsDao.Properties.Notification_date).build().list();
                        recreateNotification(notificationList, context, sharedPreferencesLauncher3.getBoolean("isAllowNotificationOnLockScreen", true));
                    }
                } else if (batchTime == 4) {
                    if (everyFourHoursList.contains(systemHours) && systemMinutes == 0) {
                        Tracer.d("Batch::" + "Every 4 Hour interval");
                        List<TableNotificationSms> notificationList = DBUtility.getNotificationDao().queryBuilder().orderDesc(TableNotificationSmsDao.Properties.Notification_date).build().list();
                        recreateNotification(notificationList, context, sharedPreferencesLauncher3.getBoolean("isAllowNotificationOnLockScreen", true));
                    }
                }

            } else if (!isTempoNotificationControlsDisabled && tempoType == 2) {
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
                            recreateNotification(notificationList, context, sharedPreferencesLauncher3.getBoolean("isAllowNotificationOnLockScreen", true));
                        }
                    }
                }
            }

        }
    }
}
