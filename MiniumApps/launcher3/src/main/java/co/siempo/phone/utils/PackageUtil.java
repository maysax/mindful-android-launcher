package co.siempo.phone.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import co.siempo.phone.R;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.db.DBUtility;
import co.siempo.phone.db.TableNotificationSms;
import co.siempo.phone.db.TableNotificationSmsDao;
import co.siempo.phone.service.AlarmBroadcast;

/**
 * Created by Shahab on 5/17/2017.
 */

public class PackageUtil {


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

    /**
     * Create Notification after parsing the notification from notification listener
     *
     * @param notification database object
     * @param context      user context
     * @param icon         application icon integer format
     */
    public synchronized static void recreateNotification(TableNotificationSms notification, Context context, int icon) {
        try {
            if (notification.getPackageName() != null && !notification.getPackageName().equalsIgnoreCase("android")) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                String applicationNameFromPackageName = CoreApplication.getInstance().getApplicationNameFromPackageName(notification.getPackageName());
                if (Build.VERSION.SDK_INT >= 26) {
                    NotificationChannel notificationChannel = createChannel(applicationNameFromPackageName);
                    if (notificationManager != null) {
                        notificationManager.createNotificationChannel(notificationChannel);
                    }
                    if (notificationManager != null) {
                        notificationManager.createNotificationChannelGroup(new NotificationChannelGroup(applicationNameFromPackageName, applicationNameFromPackageName));
                    }
                }
                NotificationCompat.Builder groupBuilder = createGroupNotification(context, notification, applicationNameFromPackageName);

                NotificationCompat.Builder builder = getNotification(context, notification);

                if (notificationManager != null) {
                    notificationManager.notify(icon, groupBuilder.build());
                }
                if (notificationManager != null) {
                    notificationManager.notify(notification.getId().intValue(), builder.build());
                }
            }
        } catch (Exception e) {
            CoreApplication.getInstance().logException(e);
            e.printStackTrace();
        }

    }

    /**
     * Generate Notification object
     *
     * @param context      selected context
     * @param notification database object
     * @return notification builder object
     */
    private static NotificationCompat.Builder getNotification(Context context, TableNotificationSms notification) {
        String applicationNameFromPackageName = CoreApplication.getInstance().getApplicationNameFromPackageName(notification.getPackageName());

        NotificationCompat.Builder b
                = new NotificationCompat.Builder(context, applicationNameFromPackageName);
        PendingIntent contentIntent = getPendingIntent(context, notification);

        Bitmap bitmap = notification.getUser_icon() != null ? UIUtils.convertBytetoBitmap(notification.getUser_icon()) : null;
        DateFormat sdf = new SimpleDateFormat(getTimeFormat(context), Locale.getDefault());
        String time = sdf.format(notification.get_date());

        String title = getNotificationTitle(notification.get_contact_title(), notification.getPackageName(), context);


        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.custom_notification_card);
        contentView.setImageViewBitmap(R.id.imgAppIcon, CoreApplication.getInstance().iconList.get(notification.getPackageName()));
        contentView.setImageViewBitmap(R.id.imgUserImage, bitmap);
        contentView.setTextViewText(R.id.txtUserName, title);
        contentView.setTextViewText(R.id.txtMessage, notification.get_message());
        contentView.setTextViewText(R.id.txtTime, time);
        contentView.setTextViewText(R.id.txtAppName, applicationNameFromPackageName);
        b.setAutoCancel(true)
                .setGroup(applicationNameFromPackageName)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.siempo_notification_icon)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentTitle(title)
                .setContentText(notification.get_message())
                .setContentIntent(contentIntent)
                .setCustomContentView(contentView)
                .setCustomBigContentView(contentView)
                .setLights(Color.MAGENTA, 500, 500)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentInfo("Info");
        return b;
    }

    /**
     * Navigation for user flow.
     *
     * @param context      current context
     * @param notification database object
     * @return
     */
    @Nullable
    private static PendingIntent getPendingIntent(Context context, TableNotificationSms notification) {
        Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage(notification.getPackageName());
        PendingIntent contentIntent = null;
        if (launchIntentForPackage != null) {
            int requestID = (int) System.currentTimeMillis();
            contentIntent = PendingIntent.getActivity(context, requestID, launchIntentForPackage, PendingIntent.FLAG_UPDATE_CURRENT);
            launchIntentForPackage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        return contentIntent;
    }

    /**
     * Generate Notification Group object
     *
     * @param context        current context
     * @param notification   current database object
     * @param strChannelName selected channel name
     * @return notification builder object for group
     */
    private static NotificationCompat.Builder createGroupNotification(Context context, TableNotificationSms notification, String strChannelName) {
        Bitmap bitmap = notification.getUser_icon() != null ? UIUtils.convertBytetoBitmap(notification.getUser_icon()) : null;
        List<TableNotificationSms> notificationSms = DBUtility.getNotificationDao().queryBuilder()
                .where(TableNotificationSmsDao.Properties.PackageName.eq(notification.getPackageName())).list();
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        for (int i = 0; i < notificationSms.size(); i++) {

            String title = getNotificationTitle(notification.get_contact_title(), notification.getPackageName(), context);

            inboxStyle.addLine(title + ": " + notificationSms.get(i).get_message());
        }
        inboxStyle.setSummaryText("You have " + notificationSms.size() + " unread message");
        PendingIntent pendingIntent = getPendingIntent(context, notification);
        NotificationCompat.Builder groupBuilder =
                new NotificationCompat.Builder(context, strChannelName)
                        .setContentTitle(strChannelName)
                        .setContentText(notificationSms.size() + " New message")
                        .setLargeIcon(bitmap)
                        .setSmallIcon(R.drawable.siempo_notification_icon)
                        .setGroupSummary(true)
                        .setGroup(strChannelName)
                        .setOnlyAlertOnce(true)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            groupBuilder.setStyle(new NotificationCompat.BigTextStyle());
        } else {
            groupBuilder.setStyle(inboxStyle);
        }
        return groupBuilder;
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
                if (alarmManager != null) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, alarmIntent);
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Wakes up the device in Idle Mode
                if (alarmManager != null) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, alarmIntent);
                }
            } else {
                if (alarmManager != null) {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, time, alarmIntent);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            CoreApplication.getInstance().logException(e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static NotificationChannel createChannel(String channelName) {
        NotificationChannel chan = new NotificationChannel(channelName,
                channelName, NotificationManager.IMPORTANCE_HIGH);
        chan.setLightColor(Color.BLUE);
        chan.setDescription("");
        chan.enableLights(true);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        chan.setVibrationPattern(new long[]{1000});
        return chan;
    }

    /**
     * Below function is used to get contact name from contact number store in contact list
     */
    private static String nameFromContactNumber(String number, Context context) {

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor cursor = context.getContentResolver().query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI}, null, null, null);
        String contactName;
        try {
            if (cursor != null && cursor.moveToFirst()) {
                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                cursor.close();
            } else {
                contactName = number;
            }
        } catch (Exception e) {
            contactName = "";
            e.printStackTrace();
        }
        return contactName;
    }

    private static String getNotificationTitle(String notificationTitle, String notificationPackageName, Context context) {
        String title = "";
        if (!TextUtils.isEmpty(notificationTitle)) {
            title = notificationTitle;
            String smsPackage = Telephony.Sms.getDefaultSmsPackage(context);
            if (!TextUtils.isEmpty(notificationPackageName) && notificationPackageName.equalsIgnoreCase(smsPackage)) {
                title = nameFromContactNumber(notificationTitle, context);
            }
        }
        return title;
    }

}
