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
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import co.siempo.phone.R;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.db.DBUtility;
import co.siempo.phone.db.TableNotificationSms;
import co.siempo.phone.db.TableNotificationSmsDao;
import co.siempo.phone.log.Tracer;
import co.siempo.phone.models.AppMenu;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.models.MainListItemType;
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

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;
        if (Build.VERSION.SDK_INT >= 26) {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth() / 2, drawable.getIntrinsicHeight() / 2, Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        } else {
            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                if (bitmapDrawable.getBitmap() != null) {
                    return bitmapDrawable.getBitmap();
                }
            }

            if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth() / 2, drawable.getIntrinsicHeight() / 2, Bitmap.Config.ARGB_8888);
            }
        }
        return bitmap;
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
                    Tracer.d("Tracking notification greater");
                    NotificationChannel notificationChannel = createChannel(context,
                            applicationNameFromPackageName);
                    if (notificationManager != null) {
                        notificationManager.createNotificationChannel(notificationChannel);
                        notificationChannel.enableLights(true);
                        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                        Tracer.d("Tracking createNotificationChannel");
                    }
                    if (notificationManager != null) {
                        notificationManager.createNotificationChannelGroup(new NotificationChannelGroup(applicationNameFromPackageName, applicationNameFromPackageName));
                        Tracer.d("Tracking createNotificationChannelGroup");
                    }
                }
                NotificationCompat.Builder groupBuilder = createGroupNotification(context, notification, applicationNameFromPackageName);
                groupBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                NotificationCompat.Builder builder = getNotification(context, notification);
                builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

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

        Tracer.d("Tracking getNotification1");
        String applicationNameFromPackageName = CoreApplication.getInstance().getApplicationNameFromPackageName(notification.getPackageName());
        Tracer.d("Tracking getNotification2");
        int priority = !PrefSiempo.getInstance(context).read(PrefSiempo.ALLOW_PEAKING, true) ? Notification.PRIORITY_DEFAULT : Notification.PRIORITY_HIGH;
        Tracer.d("Tracking getNotification3");
        NotificationCompat.Builder b
                = new NotificationCompat.Builder(context, applicationNameFromPackageName);
        b.setDefaults(Notification.DEFAULT_LIGHTS);
        PendingIntent contentIntent = getPendingIntent(context, notification);
        Tracer.d("Tracking getNotification4");
        Bitmap bitmap = notification.getUser_icon() != null ? UIUtils.convertBytetoBitmap(notification.getUser_icon()) : null;
        Tracer.d("Tracking getNotification5");
        DateFormat sdf = new SimpleDateFormat(getTimeFormat(context), Locale.getDefault());
        String time = sdf.format(notification.get_date());
        Tracer.d("Tracking getNotification6");
        String title = getNotificationTitle(notification.get_contact_title(), notification.getPackageName(), context);

        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.custom_notification_card);
        try {
            Drawable drawable = context.getPackageManager().getApplicationIcon(notification.getPackageName());
            contentView.setImageViewBitmap(R.id.imgAppIcon, drawableToBitmap(drawable));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Tracer.d("Tracking getNotification load exception");
        }

        contentView.setImageViewBitmap(R.id.imgUserImage, bitmap);
        contentView.setTextViewText(R.id.txtUserName, title);
        contentView.setTextViewText(R.id.txtMessage, notification.get_message());
        contentView.setTextViewText(R.id.txtTime, time);
        contentView.setTextViewText(R.id.txtAppName, applicationNameFromPackageName);
        b.setAutoCancel(true)
                .setGroup(applicationNameFromPackageName)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.siempo_notification_icon)
                .setPriority(priority)
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

        Tracer.d("Tracking createGroupNotification1");
        Bitmap bitmap = notification.getUser_icon() != null ? UIUtils.convertBytetoBitmap(notification.getUser_icon()) : null;
        Tracer.d("Tracking createGroupNotification2");
        List<TableNotificationSms> notificationSms = DBUtility.getNotificationDao().queryBuilder()
                .where(TableNotificationSmsDao.Properties.PackageName.eq(notification.getPackageName())).list();
        Tracer.d("Tracking createGroupNotification3");
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
                        .setDefaults(Notification.DEFAULT_LIGHTS)
                        .setGroup(strChannelName)
                        .setOnlyAlertOnce(true)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Tracer.d("Tracking createGroupNotification4");
            groupBuilder.setStyle(new NotificationCompat.BigTextStyle());
        } else {
            groupBuilder.setStyle(inboxStyle);
            Tracer.d("Tracking createGroupNotification5");
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
    private static NotificationChannel createChannel(Context context, String channelName) {
        int priority = !PrefSiempo.getInstance(context).read(PrefSiempo.ALLOW_PEAKING, true) ? NotificationManager.IMPORTANCE_DEFAULT : NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel chan = new NotificationChannel(channelName,
                channelName, priority);
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

    public static void appSettings(Context context, String packageName) {
        try {
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + packageName));
            context.startActivity(intent);
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
            CoreApplication.getInstance().logException(e);
        }
    }


    public static ArrayList<MainListItem> getToolsMenuData(Context context, ArrayList<MainListItem> items) {


        //create an empty array to hold the list of sorted Customers
        ArrayList<MainListItem> sortedTools = new ArrayList<>();

        //get the JSON array of the ordered of sorted customers
        String jsonListOfSortedToolsId = PrefSiempo.getInstance(context).read(PrefSiempo.SORTED_MENU, "");


        //check for null
        if (!jsonListOfSortedToolsId.isEmpty()) {

            //convert onNoteListChangedJSON array into a List<Long>
            Gson gson = new GsonBuilder()
                    .setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
            List<Long> listOfSortedCustomersId = gson.fromJson(jsonListOfSortedToolsId, new TypeToken<List<Long>>() {
            }.getType());

            //build sorted list
            if (listOfSortedCustomersId != null && listOfSortedCustomersId.size() > 0) {
                for (Long id : listOfSortedCustomersId) {
                    for (MainListItem tools : items) {
                        if (tools.getId() == id) {
                            sortedTools.add(tools);
                            items.remove(tools);
                            break;
                        }
                    }
                }
            }

            //if there are still tools that were not in the sorted list
            //maybe they were added after the last drag and drop
            //add them to the sorted list
            if (items.size() > 0) {
                sortedTools.addAll(items);
            }

            return sortedTools;
        } else {
            return items;
        }
    }


    public static ArrayList<MainListItem> getFavoriteList(Context context) {


        ArrayList<MainListItem> appList = getAppList(context);

        ArrayList<MainListItem> sortedFavoriteList;

        if (appList.size() > 0) {

            String jsonListOfSortedFavorites = PrefSiempo.getInstance(context).read(PrefSiempo.FAVORITE_SORTED_MENU, "");
            List<String> listOfSortFavoritesApps;
            if (!TextUtils.isEmpty(jsonListOfSortedFavorites)) {


                listOfSortFavoritesApps = syncFavoriteList(jsonListOfSortedFavorites, context);
                sortedFavoriteList = sortFavoriteAppsByPosition(listOfSortFavoritesApps, appList, context);
            } else {
                sortedFavoriteList = addDefaultFavoriteApps(context, appList);
            }
        } else {
            sortedFavoriteList = addDefaultFavoriteApps(context, appList);
        }

        return sortedFavoriteList;
    }


    private static ArrayList<MainListItem> getAppList(Context context) {

        ArrayList<MainListItem> appList = new ArrayList<>();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> installedPackageList = context.getPackageManager().queryIntentActivities(mainIntent, 0);

        for (ResolveInfo resolveInfo : installedPackageList) {
            if (!resolveInfo.activityInfo.packageName.equalsIgnoreCase(context.getPackageName())) {
                if (!TextUtils.isEmpty(resolveInfo.activityInfo.packageName) && !TextUtils.isEmpty(resolveInfo.loadLabel(context.getPackageManager()))) {

                    appList.add(new MainListItem(-1, "" + resolveInfo.loadLabel(context.getPackageManager()), resolveInfo.activityInfo.packageName));
                }
            }

        }
        return appList;
    }


    private static List<String> syncFavoriteList(String jsonListOfSortedFavorites, Context context) {
        Set<String> favorite_List_App = PrefSiempo.getInstance(context).read(PrefSiempo.FAVORITE_APPS, new HashSet<String>());
        List<String> listOfSortFavoritesApps = new ArrayList<>();
        //Below logic is use to sync FAVORITE_SORTED_MENU Preference AND FAVORITE_APPS LIST
        if (!jsonListOfSortedFavorites.isEmpty()) {

            //convert onNoteListChangedJSON array into a List<Long>
            Gson gson1 = new Gson();
            listOfSortFavoritesApps = gson1.fromJson(jsonListOfSortedFavorites, new TypeToken<List<String>>() {
            }.getType());


            for (String packageName : favorite_List_App) {
                if (!listOfSortFavoritesApps.contains(packageName)) {
                    boolean isEnable = UIUtils.isAppInstalledAndEnabled(context, packageName);
                    if (isEnable) {
                        for (int j = 0; j < listOfSortFavoritesApps.size(); j++) {
                            if (TextUtils.isEmpty(listOfSortFavoritesApps.get(j).trim())) {
                                listOfSortFavoritesApps.set(j, packageName);
                                break;
                            }
                        }
                    }
                }
            }
            Gson gson2 = new Gson();
            String jsonListOfFavoriteApps = gson2.toJson(listOfSortFavoritesApps);
            PrefSiempo.getInstance(context).write(PrefSiempo.FAVORITE_SORTED_MENU, jsonListOfFavoriteApps);
        }
        return listOfSortFavoritesApps;

    }


    private static ArrayList<MainListItem> sortFavoriteAppsByPosition(List<String> listOfSortFavoritesApps, List<MainListItem> appList, Context context) {

        ArrayList<MainListItem> sortedFavoriteList = new ArrayList<>();
        //build sorted list
        if (listOfSortFavoritesApps != null && listOfSortFavoritesApps.size() > 0) {
            for (String packageName : listOfSortFavoritesApps) {
                if (TextUtils.isEmpty(packageName)) {
                    MainListItem m = new MainListItem(-10, "", "");
                    sortedFavoriteList.add(m);
                } else {
                    for (MainListItem items : appList) {
                        if (!TextUtils.isEmpty(items.getPackageName()) && items.getPackageName().toLowerCase().trim().equalsIgnoreCase(packageName.toLowerCase().trim())) {
                            sortedFavoriteList.add(items);
                            break;
                        }
                    }
                }
            }
            int remainingFavoriteList = 12 - sortedFavoriteList.size();
            for (int i = 0; i < remainingFavoriteList; i++) {
                MainListItem m = new MainListItem(-10, "", "");
                sortedFavoriteList.add(m);
            }
        }
        return sortedFavoriteList;
    }

    public static ArrayList<MainListItem> getListOfBlankAndFavoriteApps(Context context, List<MainListItem> appList) {
        ArrayList<MainListItem> sortedFavoriteList = new ArrayList<>();

        Set<String> favorite_List_App = PrefSiempo.getInstance(context).read(PrefSiempo.FAVORITE_APPS, new HashSet<String>());

        for (String packageName : favorite_List_App) {
            for (MainListItem items : appList) {
                if (!TextUtils.isEmpty(items.getPackageName()) && items.getPackageName().toLowerCase().trim().equalsIgnoreCase(packageName.trim())) {
                    sortedFavoriteList.add(items);
                }
            }
        }

        int remainingFavoriteList = 12 - sortedFavoriteList.size();
        for (int i = 0; i < remainingFavoriteList; i++) {
            MainListItem m = new MainListItem(-10, "", "");
            sortedFavoriteList.add(m);
        }
        return sortedFavoriteList;
    }

    private static ArrayList<MainListItem> addDefaultFavoriteApps(Context context, List<MainListItem> appList) {

        LauncherApps launcherApps = (LauncherApps) context.getSystemService(Context.LAUNCHER_APPS_SERVICE);
        Set<String> list;
        list = PrefSiempo.getInstance(context).read(PrefSiempo.FAVORITE_APPS, new HashSet<String>());

        ArrayList<MainListItem> items = new ArrayList<>();
        String CHROME_PACKAGE = "com.android.chrome", SYSTEM_SETTING = "com.android.settings";


        for (int i = 0; i < appList.size(); i++) {
            if (!TextUtils.isEmpty(appList.get(i).getPackageName())) {
                if (appList.get(i).getPackageName().equalsIgnoreCase(CHROME_PACKAGE) || appList.get(i).getPackageName().equalsIgnoreCase(SYSTEM_SETTING)) {
                    boolean isEnable = UIUtils.isAppInstalledAndEnabled(context, appList.get(i).getPackageName());
                    if (isEnable) {
                        items.add(appList.get(i));
                    }
                }
            }
        }


        int remainingFavoriteList = 12 - items.size();
        for (int i = 0; i < remainingFavoriteList; i++) {
            MainListItem m = new MainListItem(-10, "", "");
            items.add(m);
        }


        //get the JSON array of the ordered of sorted customers
        String jsonListOfSortedFavorites = PrefSiempo.getInstance(context).read(PrefSiempo.FAVORITE_SORTED_MENU, "");
        //convert onNoteListChangedJSON array into a List<Long>
        Gson gson1 = new Gson();
        List<String> listOfSortFavoritesApps = gson1.fromJson(jsonListOfSortedFavorites, new TypeToken<List<String>>() {
        }.getType());

        if (listOfSortFavoritesApps != null) {
            if (!listOfSortFavoritesApps.contains(CHROME_PACKAGE)) {
                for (int i = 0; i < listOfSortFavoritesApps.size(); i++) {
                    if (TextUtils.isEmpty(listOfSortFavoritesApps.get(i).trim())) {
                        boolean isEnable = UIUtils.isAppInstalledAndEnabled(context, CHROME_PACKAGE);
                        if (isEnable) {
                            listOfSortFavoritesApps.set(i, CHROME_PACKAGE);
                            if (list != null && !list.contains(CHROME_PACKAGE)) {
                                list.add(CHROME_PACKAGE);
                            }
                        }
                        break;
                    }
                }
            }

            if (!listOfSortFavoritesApps.contains(SYSTEM_SETTING)) {
                for (int i = 0; i < listOfSortFavoritesApps.size(); i++) {
                    if (TextUtils.isEmpty(listOfSortFavoritesApps.get(i).trim())) {
                        boolean isEnable = UIUtils.isAppInstalledAndEnabled(context, SYSTEM_SETTING);
                        if (isEnable) {
                            listOfSortFavoritesApps.set(i, SYSTEM_SETTING);
                            if (list != null && !list.contains(SYSTEM_SETTING)) {
                                list.add(SYSTEM_SETTING);
                            }
                        }
                        break;
                    }
                }
            }
        } else {
            listOfSortFavoritesApps = new ArrayList<>();
            boolean isChromeEnable = UIUtils.isAppInstalledAndEnabled(context, CHROME_PACKAGE);
            if (isChromeEnable) {
                listOfSortFavoritesApps.add(CHROME_PACKAGE);
            }
            boolean isSystemSettingEnable = UIUtils.isAppInstalledAndEnabled(context, SYSTEM_SETTING);
            if (isSystemSettingEnable) {
                listOfSortFavoritesApps.add(SYSTEM_SETTING);
            }
            int remainingCount = 12 - listOfSortFavoritesApps.size();
            for (int j = 0; j < remainingCount; j++) {
                listOfSortFavoritesApps.add("");
            }

            if (list != null) {
                if (isChromeEnable) {
                    list.add(CHROME_PACKAGE);
                }
                if (isSystemSettingEnable) {
                    list.add(SYSTEM_SETTING);
                }
            }
        }


        Gson gson2 = new Gson();
        String jsonListOfFavoriteApps = gson2.toJson(listOfSortFavoritesApps);
        PrefSiempo.getInstance(context).write(PrefSiempo.FAVORITE_SORTED_MENU, jsonListOfFavoriteApps);
        PrefSiempo.getInstance(context).write(PrefSiempo.FAVORITE_APPS, list);

        return items;
    }

    public static Drawable getDrawableImage(Context context, ApplicationInfo appInfo) {
        Drawable drawable;
        try {
            Resources resourcesForApplication = context.getPackageManager().getResourcesForApplication(appInfo);
            Configuration config = resourcesForApplication.getConfiguration();
            Configuration originalConfig = new Configuration(config);

            DisplayMetrics displayMetrics = resourcesForApplication.getDisplayMetrics();
            DisplayMetrics originalDisplayMetrics = resourcesForApplication.getDisplayMetrics();
            displayMetrics.densityDpi = DisplayMetrics.DENSITY_HIGH;


            resourcesForApplication.updateConfiguration(config, displayMetrics);
            if (appInfo.icon != 0) {
                drawable = resourcesForApplication.getDrawable(appInfo.icon, null);
            } else {
                drawable = appInfo.loadIcon(context.getPackageManager());
            }
            resourcesForApplication.updateConfiguration(originalConfig, originalDisplayMetrics);
        } catch (Exception e) {
            CoreApplication.getInstance().logException(e);
            drawable = appInfo.loadIcon(context.getPackageManager());
        }
        return drawable;
    }


    public static void addRecentItemList(MainListItem item, Context context) {

        if (item != null) {
            // Load RecentItem List from Storage
            Type baseType = new TypeToken<List<MainListItem>>() {
            }.getType();
            List<MainListItem> recentItemList = new ArrayList<>();
            recentItemList = loadRecentItemsFromStore(context);

            // Validate if stored RecentItem List having this item or not.
            boolean isItemAvailable = false;
            MainListItem removeItem = null;
            for (int j = 0; j < recentItemList.size(); j++) {
                String title = recentItemList.get(j).getTitle();
                String packageName = recentItemList.get(j).getPackageName();

                if (TextUtils.isEmpty(item.getPackageName())) {
                    if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(item.getTitle()) && title.toString().toLowerCase().trim().equalsIgnoreCase(item.getTitle().toLowerCase().trim())) {
                        isItemAvailable = true;
                        removeItem = recentItemList.get(j);
                    }
                } else {
                    if (!TextUtils.isEmpty(packageName) && packageName.trim().equalsIgnoreCase(item.getPackageName().trim())) {
                        isItemAvailable = true;
                        removeItem = recentItemList.get(j);
                    }
                }
            }

            /**
             *  1.) If RecentItem List do not contain this item, then add and store into the list.
             *  2.) If RecentItem lsit contain this item, then update it to first position
             */
            if (!isItemAvailable) {
                recentItemList.add(0, item);
            } else {
                if (removeItem != null) {
                    recentItemList.remove(removeItem);
                }
                recentItemList.add(0, item);
            }

            Gson gson = new Gson();
            String val_recentItemList = gson.toJson(recentItemList);
            PrefSiempo.getInstance(context).write(PrefSiempo.RECENT_ITEM_LIST, val_recentItemList);
        }
    }

    public static List<MainListItem> getListWithMostRecentData(List<MainListItem> allItems, Context context) {

        List<MainListItem> recentItemList = new ArrayList<>();
        recentItemList = loadRecentItemsFromStore(context);

        List<MainListItem> removeList = new ArrayList<>();
        List<MainListItem> listWithMostRecentdata = new ArrayList<>();


        for (int j = 0; j < recentItemList.size(); j++) {
            String recentItemTitle = recentItemList.get(j).getTitle();
            String recentItemPackageName = recentItemList.get(j).getPackageName();

            for (int i = 0; i < allItems.size(); i++) {
                MainListItem item = allItems.get(i);
                String title = allItems.get(i).getTitle();
                String packageName = allItems.get(i).getPackageName();

                if (TextUtils.isEmpty(packageName) && TextUtils.isEmpty(recentItemPackageName) && !TextUtils.isEmpty(title) && !TextUtils.isEmpty(recentItemTitle) && title.toLowerCase().trim().equalsIgnoreCase(recentItemTitle.toLowerCase().trim())) {
                    removeList.add(item);
                } else if (!TextUtils.isEmpty(packageName) && !TextUtils.isEmpty(recentItemPackageName) && packageName.trim().equalsIgnoreCase(recentItemPackageName.trim())) {
                    removeList.add(item);
                }
            }

        }

        allItems.removeAll(removeList);

        listWithMostRecentdata.addAll(removeList);
        listWithMostRecentdata.addAll(allItems);

        List<MainListItem> junkListItems = getJunkListItems(listWithMostRecentdata, context);
        listWithMostRecentdata.removeAll(junkListItems);

        return listWithMostRecentdata;
    }

    public static List<MainListItem> loadRecentItemsFromStore(Context context) {
        Type baseType = new TypeToken<List<MainListItem>>() {
        }.getType();
        List<MainListItem> recentItemList = new ArrayList<>();
        String val_recentItems = PrefSiempo.getInstance(context).read(PrefSiempo.RECENT_ITEM_LIST, "");
        if (!TextUtils.isEmpty(val_recentItems)) {
            Gson gson = new GsonBuilder()
                    .setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
            recentItemList = gson.fromJson(val_recentItems, baseType);
        }
        return recentItemList;
    }


    private static List<MainListItem> getJunkListItems(List<MainListItem> allItems, Context context) {
        HashMap<Integer, AppMenu> toolSetting = CoreApplication.getInstance()
                .getToolsSettings();
        ArrayList<String> junkFoodAppList = new ArrayList<>();
        Set<String> junkFoodList = PrefSiempo
                .getInstance(context).read
                        (PrefSiempo.JUNKFOOD_APPS, new HashSet<String>());
        junkFoodAppList = new ArrayList<>(junkFoodList);
        List<MainListItem> junkListItems = new ArrayList<>();

        // Check if tools contain junkfood apps then remove assign from corresponding tool
        Iterator it = toolSetting.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            for (String junkApp : junkFoodAppList) {
                if (((AppMenu) (pair.getValue())).getApplicationName()
                        .equalsIgnoreCase(junkApp) || ((AppMenu) (pair.getValue())).getApplicationName()
                        .equalsIgnoreCase("")) {
                    ((AppMenu) (pair.getValue())).setApplicationName("");
                }

            }
        }

        // Create junkListItems array which contain junkfood apps & tools
        for (MainListItem item : allItems) {
            if (!TextUtils.isEmpty(item.getPackageName())) {
                for (String junkApp : junkFoodAppList) {
                    if (item.getPackageName().equalsIgnoreCase(junkApp)) {
                        junkListItems.add(item);
                    }
                }
            } else {
                AppMenu appMenu = toolSetting
                        .get(item.getId());
                if (null != appMenu && TextUtils
                        .isEmpty(appMenu.getApplicationName()) && item
                        .getItemType() != MainListItemType.DEFAULT) {
                    junkListItems.add(item);
                }
            }
        }

        return junkListItems;
    }
}
