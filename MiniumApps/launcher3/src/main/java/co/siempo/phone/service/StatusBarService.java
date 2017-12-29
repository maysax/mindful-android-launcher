package co.siempo.phone.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import co.siempo.phone.R;
import co.siempo.phone.db.DBClient;
import co.siempo.phone.db.DBUtility;
import co.siempo.phone.db.TableNotificationSms;
import co.siempo.phone.db.TableNotificationSmsDao;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.util.PackageUtil;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.CoreApplication;
import minium.co.core.event.AppInstalledEvent;
import minium.co.core.event.FirebaseEvent;
import minium.co.core.log.Tracer;

import static co.siempo.phone.SiempoNotificationBar.NotificationUtils.ANDROID_CHANNEL_ID;

/**
 * This background service used for detect torch status and feature used for any other background status.
 */

public class StatusBarService extends Service {

    SharedPreferences sharedPreferences;
    Timer timer;
    MyTimerTask timerTask;
    Context context;
    ArrayList<Integer> everyHourList = new ArrayList<>();
    ArrayList<Integer> everyTwoHourList = new ArrayList<>();
    ArrayList<Integer> everyFourHoursList = new ArrayList<>();
    private MyObserver myObserver;
    private AppInstallUninstall appInstallUninstall;
    private AudioManager audioManager;
    private Vibrator vibrator;

    public StatusBarService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        sharedPreferences = getSharedPreferences("DroidPrefs", 0);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        registerObserverForContact();
        registerObserverForAppInstallUninstall();
        EventBus.getDefault().register(this);
        everyHourList.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24));
        everyTwoHourList.addAll(Arrays.asList(1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23));
        everyFourHoursList.addAll(Arrays.asList(1, 4, 8, 12, 16, 20, 24));
        timerTask = new MyTimerTask();
        timer = new Timer();
        timer.schedule(timerTask, 0, 60000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder builder = new Notification.Builder(this, ANDROID_CHANNEL_ID)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("")
                    .setAutoCancel(true);
            Notification notification = builder.build();
            startForeground(1, notification);
        }

        return START_STICKY;
    }


    /**
     * Observer for when installing new app or uninstalling the app.
     */
    private void registerObserverForAppInstallUninstall() {
        appInstallUninstall = new AppInstallUninstall();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addDataScheme("package");
        registerReceiver(appInstallUninstall, intentFilter);
    }


    /**
     * Observer for when new contact adding or updating any exiting contact.
     */
    private void registerObserverForContact() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {
                myObserver = new MyObserver(new Handler());
                getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true,
                        myObserver);
            }
        } else {
            myObserver = new MyObserver(new Handler());
            getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true,
                    myObserver);
        }
    }

    @Subscribe
    public void firebaseEvent(FirebaseEvent firebaseEvent) {
        FirebaseHelper.getIntance().logScreenUsageTime(firebaseEvent.getScreenName(), firebaseEvent.getStrStartTime());
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (myObserver != null)
            getContentResolver().unregisterContentObserver(myObserver);
        if (appInstallUninstall != null)
            unregisterReceiver(appInstallUninstall);
        super.onDestroy();
    }

    public void recreateNotification(List<TableNotificationSms> notificationList, Context context) {
        for (int i = 0; i < notificationList.size(); i++) {
            TableNotificationSms notification = notificationList.get(i);
            if (!notification.getPackageName().equalsIgnoreCase("android")) {
                NotificationCompat.Builder b = new NotificationCompat.Builder(context, "" + notification.getId());
                Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage(notification.getPackageName());

                int requestID = (int) System.currentTimeMillis();
                PendingIntent contentIntent = PendingIntent.getActivity(context, requestID, launchIntentForPackage, PendingIntent.FLAG_UPDATE_CURRENT);

                if (launchIntentForPackage != null) {
                    launchIntentForPackage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                }
                b.setAutoCancel(true)
                        .setWhen(notification.getNotification_date())
                        .setSmallIcon(R.drawable.ic_airplane_air_balloon)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentTitle(notification.get_contact_title())
                        .setContentText(notification.get_message())
                        .setContentIntent(contentIntent)
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                        .setContentInfo("Info");

                if (notificationList.size() == 1 || i == (notificationList.size() - 1)) {
                    if (sharedPreferences.getInt("tempoSoundProfile", 0) == 0) {
                        b.setVibrate(new long[0]);
                        b.setSound(null);
                    } else {
                        playNotificationSoundVibrate();
                    }
                    DBUtility.getNotificationDao().deleteAll();
                }
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                String CHANNEL_ID = "Siempo";
                if (Build.VERSION.SDK_INT >= 26) {
                    CharSequence name = "Siempo";// The user-visible name of the channel.
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                    b.setChannelId(CHANNEL_ID);
                    if (notificationManager != null) {
                        notificationManager.createNotificationChannel(mChannel);
                    }

                }
                if (notificationManager != null) {
                    notificationManager.notify(notification.getId().intValue(), b.build());
                }
            }
        }
    }

    public void playNotificationSoundVibrate() {
        try {
            MediaPlayer mMediaPlayer = null;
            if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
                Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setDataSource(this, alert);
                final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                if (audioManager != null && audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                    mMediaPlayer.setVolume(100, 100);
                    mMediaPlayer.setScreenOnWhilePlaying(true);
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                    vibrator.vibrate(1000);
                }
            }
        } catch (Exception e) {
            CoreApplication.getInstance().logException(e);
            e.printStackTrace();
        }
    }

    class MyTimerTask extends TimerTask {
        public void run() {
            if (PackageUtil.isSiempoLauncher(context)) {
                Calendar calendar = Calendar.getInstance();
                int systemHours = calendar.get(Calendar.HOUR_OF_DAY);
                int systemMinutes = calendar.get(Calendar.MINUTE);
                int tempoType = sharedPreferences.getInt("tempoType", 0);
                boolean isTempoNotificationControlsDisabled = sharedPreferences.getBoolean("isTempoNotificationControlsDisabled", false);
                if (!isTempoNotificationControlsDisabled && tempoType == 1) {
                    int batchTime = sharedPreferences.getInt("batchTime", 15);
                    if (batchTime == 15) {
                        if (systemMinutes == 0 || systemMinutes == 15 || systemMinutes == 30 || systemMinutes == 45) {
                            Tracer.d("Batch::" + "15 minute interval");
                            List<TableNotificationSms> notificationList = DBUtility.getNotificationDao().queryBuilder().orderDesc(TableNotificationSmsDao.Properties.Notification_date).build().list();
                            recreateNotification(notificationList, context);
                        }
                    } else if (batchTime == 30) {
                        if (systemMinutes == 0 || systemMinutes == 30) {
                            Tracer.d("Batch::" + "30 minute interval");
                            List<TableNotificationSms> notificationList = DBUtility.getNotificationDao().queryBuilder().orderDesc(TableNotificationSmsDao.Properties.Notification_date).build().list();
                            recreateNotification(notificationList, context);
                        }
                    } else if (batchTime == 1) {
                        if (everyHourList.contains(systemHours) && systemMinutes == 0) {
                            Tracer.d("Batch::" + "Every Hour interval");
                            List<TableNotificationSms> notificationList = DBUtility.getNotificationDao().queryBuilder().orderDesc(TableNotificationSmsDao.Properties.Notification_date).build().list();
                            recreateNotification(notificationList, context);
                        }
                    } else if (batchTime == 2) {
                        if (everyTwoHourList.contains(systemHours) && systemMinutes == 0) {
                            Tracer.d("Batch::" + "Every 2 Hour interval");
                            List<TableNotificationSms> notificationList = DBUtility.getNotificationDao().queryBuilder().orderDesc(TableNotificationSmsDao.Properties.Notification_date).build().list();
                            recreateNotification(notificationList, context);
                        }
                    } else if (batchTime == 4) {
                        if (everyFourHoursList.contains(systemHours) && systemMinutes == 0) {
                            Tracer.d("Batch::" + "Every 4 Hour interval");
                            List<TableNotificationSms> notificationList = DBUtility.getNotificationDao().queryBuilder().orderDesc(TableNotificationSmsDao.Properties.Notification_date).build().list();
                            recreateNotification(notificationList, context);
                        }
                    }

                } else if (!isTempoNotificationControlsDisabled && tempoType == 2) {
                    String strTimeData = sharedPreferences.getString("onlyAt", "");
                    if (!strTimeData.equalsIgnoreCase("")) {
                        String strTime[] = strTimeData.split(",");
                        for (String str : strTime) {
                            int hours = Integer.parseInt(str.split(":")[0]);
                            int minutes = Integer.parseInt(str.split(":")[1]);
                            if (hours == systemHours && minutes == systemMinutes) {
                                Tracer.d("Only at::" + str);
                                List<TableNotificationSms> notificationList = DBUtility.getNotificationDao().queryBuilder().orderDesc(TableNotificationSmsDao.Properties.Notification_date).build().list();
                                recreateNotification(notificationList, context);
                            }
                        }
                    }
                }
            }

        }
    }

    private class MyObserver extends ContentObserver {
        MyObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            // do s.th.
            // depending on the handler you might be on the UI
            // thread, so be cautious!

            sharedPreferences.edit().putBoolean("isContactUpdate", true).apply();
        }
    }

    class AppInstallUninstall extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                CoreApplication.getInstance().getAllApplicationPackageName();
                if (intent != null && intent.getAction() != null) {
                    if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                        String installPackageName;
                        if (intent.getData().getEncodedSchemeSpecificPart() != null) {
                            installPackageName = intent.getData().getEncodedSchemeSpecificPart();
                            Log.d("Testing with device.", "Added" + installPackageName);
                        }

                    } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
                        String uninstallPackageName;
                        if (intent.getData().getEncodedSchemeSpecificPart() != null) {
                            uninstallPackageName = intent.getData().getSchemeSpecificPart();
                            Log.d("Testing with device.", "Removed" + uninstallPackageName);
                            if (!TextUtils.isEmpty(uninstallPackageName)) {
                                new DBClient().deleteMsgByPackageName(uninstallPackageName);
                            }
                        }
                    }
                    sharedPreferences.edit().putBoolean("isAppUpdated", true).apply();
                    EventBus.getDefault().post(new AppInstalledEvent(true));
                }
            } catch (Exception e) {
                e.printStackTrace();
                CoreApplication.getInstance().logException(e);
            }

        }
    }
}
