package co.siempo.phone.util;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import co.siempo.phone.R;
import co.siempo.phone.db.TableNotificationSms;
import co.siempo.phone.service.AlarmBroadcast;
import co.siempo.phone.service.SiempoDndService;
import minium.co.core.app.CoreApplication;
import minium.co.core.util.UIUtils;

/**
 * Created by Shahab on 5/17/2017.
 */

public class PackageUtil {

    private static final String SYSTEM_PACKAGE_NAME = "android";

    public static boolean isCallPackage(String pkg) {
        return pkg != null && !pkg.equalsIgnoreCase("") && (pkg.contains("telecom") || pkg.contains("dialer"));
    }

    public static boolean isMsgPackage(String pkg) {
        return pkg != null && !pkg.equalsIgnoreCase("") && (pkg.contains("messaging") || pkg.contains("com.android.mms"));
    }

    public static boolean isSiempoLauncher(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo defaultLauncher = context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (defaultLauncher != null && defaultLauncher.activityInfo != null && defaultLauncher.activityInfo.packageName != null) {
            String defaultLauncherStr = defaultLauncher.activityInfo.packageName;
            return defaultLauncherStr.equals(context.getPackageName());
        }
        return false;

    }

    public static boolean isSiempoBlocker(int notifId) {
        return notifId == SiempoDndService.NOTIFICATION_ID;
    }

    public static int getIdByPackage(String pkg) {
        int ret = 0;
        for (int i = 0; i < pkg.length(); i++) {
            ret += ((i + 1) * pkg.charAt(i));
        }
        return ret;
    }

    public static void checkPermission(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Show alert dialog to the user saying a separate permission is needed
            // Launch the settings activity if the user prefers
            if (!Settings.canDrawOverlays(context)) {
                Toast.makeText(context, R.string.msg_overlay_settings, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent);
            }
        }


    }

    /**
     * Match signature of application to identify that if it is signed by system
     * or not.
     *
     * @param packageName package of application. Can not be blank.
     * @return <code>true</code> if application is signed by system certificate,
     * otherwise <code>false</code>
     */
    public static boolean isSystemApp(String packageName, Context context) {
        try {
            PackageManager mPackageManager = context.getPackageManager();
            // Get packageinfo for target application

            ApplicationInfo ai = mPackageManager.getApplicationInfo(packageName, 0);
            return ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0);


        } catch (PackageManager.NameNotFoundException e) {
            CoreApplication.getInstance().logException(e);
            return false;
        }
    }


    public synchronized static void recreateNotification(TableNotificationSms notification, Context context, Integer tempoType, Integer tempoSound, Boolean isAllowNotificationOnLockScreen) {
        if (tempoType == 0) try {
            NotificationCompat.Builder b = new NotificationCompat.Builder(context, "11111");
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
                    .setGroup(applicationNameFromPackageName)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setContentInfo("Info");
            if (tempoSound == 0) {
                b.setDefaults(Notification.DEFAULT_LIGHTS);
            } else {
                if (!CoreApplication.getInstance().isCallisRunning()) {
                    KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                    if (((myKM != null && myKM.inKeyguardRestrictedInputMode()) && !isAllowNotificationOnLockScreen)) {
                        // hide notification on lock screen so mute the notification sound.
                    } else {
                        CoreApplication.getInstance().playNotificationSoundVibrate();
                    }
                }
            }
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= 26) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                if (applicationNameFromPackageName != null) {
                    NotificationChannel mChannel = new NotificationChannel(applicationNameFromPackageName, applicationNameFromPackageName, importance);
                    b.setChannelId(applicationNameFromPackageName);
                    if (notificationManager != null) {
                        notificationManager.createNotificationChannel(mChannel);
                    }
                }
            }
            if (notificationManager != null) {
                notificationManager.notify(notification.getId().intValue(), b.build());
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
                wl.acquire(3000);
            }
        } catch (Exception e) {
            CoreApplication.getInstance().logException(e);
            e.printStackTrace();
        }
    }


    private static String getTimeFormat(Context context) {
        String format;
        boolean is24hourformat = android.text.format.DateFormat.is24HourFormat(context);

        if (is24hourformat) {
            format = "HH:mm";
        } else {
            format = "hh:mm a";
        }
        return format;
    }

    public static void enableAlarm(Context context) {
        try {
            Intent intentToFire = new Intent(context, AlarmBroadcast.class);
            intentToFire.setAction(AlarmBroadcast.ACTION_ALARM);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 1234, intentToFire, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            long delay = 30000;
            long time = System.currentTimeMillis() + delay;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Wakes up the device in Doze Mode
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, alarmIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Wakes up the device in Idle Mode
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, alarmIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, time, alarmIntent);
            }

        } catch (Exception e) {
            e.printStackTrace();
            CoreApplication.getInstance().logException(e);
        }
    }

}