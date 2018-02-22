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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import co.siempo.phone.R;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.db.DBUtility;
import co.siempo.phone.db.TableNotificationSms;
import co.siempo.phone.db.TableNotificationSmsDao;
import co.siempo.phone.log.Tracer;
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
                    NotificationChannel notificationChannel = createChannel(context,
                            applicationNameFromPackageName);
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
    private static NotificationChannel createChannel(Context context, String channelName) {
        int priority = PrefSiempo.getInstance(context).read(PrefSiempo.ALLOW_PEAKING, true) ? NotificationManager.IMPORTANCE_DEFAULT : NotificationManager.IMPORTANCE_HIGH;
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
            String appName = "";
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
            if (applicationInfo != null && !TextUtils.isEmpty(applicationInfo.name)) {
                appName = applicationInfo.loadLabel(context.getPackageManager()).toString();
            }
            boolean isPackageAvailable = false;
            Type baseType = new TypeToken<List<MainListItem>>() {
            }.getType();
            List<MainListItem> searchItems = new ArrayList<MainListItem>();
            String searchList = PrefSiempo.getInstance(context).read(PrefSiempo.SEARCH_LIST, "");
            if (!TextUtils.isEmpty(searchList)) {
                Gson gson = new GsonBuilder()
                        .setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
                searchItems = gson.fromJson(searchList, baseType);
            }
            int i = 0;
            for (int j = 0; j < searchItems.size(); j++) {
                MainListItem item = searchItems.get(j);
                if (!TextUtils.isEmpty(item.getPackageName()) && item
                        .getPackageName()
                        .equalsIgnoreCase(packageName)) {
                    isPackageAvailable = true;
                }
                if (item.getId() == -1 && item.getTitle().startsWith("" + appName.charAt(0))) {
                    i = j;
                }
            }
            if (!isPackageAvailable) {
                String pckageName = applicationInfo != null ? applicationInfo.packageName : "";
                searchItems.add(i - 1, new MainListItem(-1, appName, pckageName));
            }
            searchItems = Sorting.sortAppList(context, searchItems);
            searchItems = Sorting.sortList(searchItems);
            storeSearchList(searchItems, context);
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
            CoreApplication.getInstance().logException(e);
        }
    }

    public static void contactsUpdateInSearchList(Context context) {
        Type baseType = new TypeToken<List<MainListItem>>() {
        }.getType();
        List<MainListItem> searchItems = new ArrayList<MainListItem>();
        String searchList = PrefSiempo.getInstance(context).read(PrefSiempo.SEARCH_LIST, "");
        if (!TextUtils.isEmpty(searchList)) {
            Gson gson = new GsonBuilder()
                    .setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
            searchItems = gson.fromJson(searchList, baseType);
            List<MainListItem> removeItems = new ArrayList<>();
            for (MainListItem item : searchItems) {
                if (!TextUtils.isEmpty(item.getContactName())) {
                    removeItems.add(item);
                }
            }
            searchItems.removeAll(removeItems);


            for (MainListItem item : searchItems) {
                if (item.getItemType() == MainListItemType.DEFAULT) {
                    removeItems.add(item);
                }
            }
            searchItems.removeAll(removeItems);

            List<MainListItem> contactItems = new ContactsLoader().loadContacts(context);
            searchItems.addAll(contactItems);

            searchItems.add(new MainListItem(4, context.getString(R.string.title_call), R.drawable.icon_call, MainListItemType.NUMBERS));
            searchItems.add(new MainListItem(1, context.getString(R.string.title_sendAsSMS), R.drawable.ic_messages_tool, MainListItemType.DEFAULT));
            searchItems.add(new MainListItem(2, context.getString(R.string.title_saveNote), R.drawable.ic_notes_tool, MainListItemType.DEFAULT));
            searchItems.add(new MainListItem(3, context.getString(R.string.title_swipe), R.drawable.ic_default_swipe, MainListItemType.DEFAULT));

            Sorting.sortList(searchItems);
            storeSearchList(searchItems, context);
        }

    }

    public static void storeSearchList(List<MainListItem> items, Context context) {
        Type baseType = new TypeToken<List<MainListItem>>() {
        }.getType();
        Gson gson = new GsonBuilder()
                .setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
        String searchValues = gson.toJson(items, baseType);
        PrefSiempo.getInstance(context).write(PrefSiempo.SEARCH_LIST, searchValues);
    }

    public static void addAppInSearchList(String packageName, Context context) {
        try {
            String appName = "";
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
            if (applicationInfo != null && !TextUtils.isEmpty(applicationInfo.name)) {
                appName = applicationInfo.loadLabel(context.getPackageManager()).toString();
            }
            boolean isPackageAvailable = false;
            Type baseType = new TypeToken<List<MainListItem>>() {
            }.getType();
            List<MainListItem> searchItems = new ArrayList<MainListItem>();
            String searchList = PrefSiempo.getInstance(context).read(PrefSiempo.SEARCH_LIST, "");
            if (!TextUtils.isEmpty(searchList)) {
                Gson gson = new GsonBuilder()
                        .setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
                searchItems = gson.fromJson(searchList, baseType);
            }
            int i = 0;
            for (int j = 0; j < searchItems.size(); j++) {
                MainListItem item = searchItems.get(j);
                if (!TextUtils.isEmpty(item.getPackageName()) && item.getPackageName().equalsIgnoreCase(packageName)) {
                    isPackageAvailable = true;
                }
                if (item.getId() == -1 && item.getTitle().startsWith("" + appName.charAt(0))) {
                    i = j;
                }
            }
            if (!isPackageAvailable) {
                searchItems.add(i - 1, new MainListItem(-1, appName,
                        applicationInfo.packageName));
            }
            searchItems = Sorting.sortAppList(context, searchItems);
            searchItems = Sorting.sortList(searchItems);
            storeSearchList(searchItems, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeAppFromSearchList(String packageName, Context context) {
        Type baseType = new TypeToken<List<MainListItem>>() {
        }.getType();
        List<MainListItem> searchItems = new ArrayList<MainListItem>();
        String searchList = PrefSiempo.getInstance(context).read(PrefSiempo.SEARCH_LIST, "");
        if (!TextUtils.isEmpty(searchList)) {
            Gson gson = new GsonBuilder()
                    .setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
            searchItems = gson.fromJson(searchList, baseType);
        }

        List<MainListItem> removeApps = new ArrayList<>();


        for (MainListItem item : searchItems) {
            if (!TextUtils.isEmpty(item.getPackageName()) && item.getPackageName().equalsIgnoreCase(packageName)) {
                removeApps.add(item);
            }
        }
        searchItems.removeAll(removeApps);
        searchItems = Sorting.sortList(searchItems);
        storeSearchList(searchItems, context);
    }

    public static List<MainListItem> getSearchList(Context context) {
        Type baseType = new TypeToken<List<MainListItem>>() {
        }.getType();
        List<MainListItem> searchItems = new ArrayList<MainListItem>();
        String searchList = PrefSiempo.getInstance(context).read(PrefSiempo.SEARCH_LIST, "");
        if (!TextUtils.isEmpty(searchList)) {
            Gson gson = new GsonBuilder()
                    .setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
            searchItems = gson.fromJson(searchList, baseType);
        }
        searchItems = Sorting.sortList(searchItems);
        return searchItems;
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


        ArrayList<MainListItem> appList=getAppList(context);

        ArrayList<MainListItem> sortedFavoriteList;

        if (appList.size()>0) {

            String jsonListOfSortedFavorites = PrefSiempo.getInstance(context).read(PrefSiempo.FAVORITE_SORTED_MENU, "");

            List<String> listOfSortFavoritesApps;
            if(!TextUtils.isEmpty(jsonListOfSortedFavorites)){

                listOfSortFavoritesApps=syncFavoriteList(jsonListOfSortedFavorites,context);

                sortedFavoriteList = sortFavoriteAppsByPosition(listOfSortFavoritesApps,appList,context);

            }
            else{
                sortedFavoriteList=addDefaultFavoriteApps(context,appList);
            }
        }
        else{
            sortedFavoriteList=addDefaultFavoriteApps(context,appList);
        }

        return sortedFavoriteList;
        }



        public static ArrayList<MainListItem>  getAppList(Context context){

            ArrayList<MainListItem> appList= new ArrayList<>();
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> installedPackageList = context.getPackageManager().queryIntentActivities(mainIntent, 0);

            for (ResolveInfo resolveInfo : installedPackageList) {
                if(!TextUtils.isEmpty(resolveInfo.activityInfo.packageName) && !TextUtils.isEmpty(resolveInfo.loadLabel(context.getPackageManager()))){

                    appList.add(new MainListItem(-1,""+resolveInfo.loadLabel(context.getPackageManager()), resolveInfo.activityInfo.packageName));
                }
            }
            return appList;
        }


        public static List<String> syncFavoriteList(String jsonListOfSortedFavorites,Context context) {
            Set<String> favorite_List_App = PrefSiempo.getInstance(context).read(PrefSiempo.FAVORITE_APPS, new HashSet<String>());
            List<String> listOfSortFavoritesApps= new ArrayList<>();
            //Below logic is use to sync FAVORITE_SORTED_MENU Preference AND FAVORITE_APPS LIST
            if (!jsonListOfSortedFavorites.isEmpty()) {

                //convert onNoteListChangedJSON array into a List<Long>
                Gson gson1 = new Gson();
                listOfSortFavoritesApps = gson1.fromJson(jsonListOfSortedFavorites, new TypeToken<List<String>>() {
                }.getType());


                for (Iterator<String> it = favorite_List_App.iterator(); it.hasNext(); ) {
                    String packageName = it.next();
                    if (!listOfSortFavoritesApps.contains(packageName)) {
                        for (int j = 0; j < listOfSortFavoritesApps.size(); j++) {
                            if (TextUtils.isEmpty(listOfSortFavoritesApps.get(j).toString().trim())) {
                                listOfSortFavoritesApps.set(j, packageName);
                                break;
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


        public static ArrayList<MainListItem> sortFavoriteAppsByPosition(List<String> listOfSortFavoritesApps,List<MainListItem> appList,Context context) {

            ArrayList<MainListItem> sortedFavoriteList= new ArrayList<>();
            //build sorted list
            if (listOfSortFavoritesApps != null && listOfSortFavoritesApps.size() > 0) {
                for (String packageName : listOfSortFavoritesApps) {
                    if(TextUtils.isEmpty(packageName)){
                        MainListItem m = new MainListItem(-10,"","");
                        sortedFavoriteList.add(m);
                    }
                    else{
                        for (MainListItem items : appList) {
                            if (!TextUtils.isEmpty(items.getPackageName()) && items.getPackageName().toLowerCase().trim().equalsIgnoreCase(packageName.toLowerCase().trim())) {
                                sortedFavoriteList.add(items);
                                break;
                            }
                        }
                    }
                }
                int remainingFavoriteList=12-sortedFavoriteList.size();
                for(int i=0;i<remainingFavoriteList;i++){
                    MainListItem m = new MainListItem(-10,"","");
                    sortedFavoriteList.add(m);
                }
            }
            return sortedFavoriteList;
        }

        public static ArrayList<MainListItem> getListOfBlankAndFavoriteApps(Context context,List<MainListItem> appList){
            ArrayList<MainListItem> sortedFavoriteList= new ArrayList<>();

            Set<String> favorite_List_App = PrefSiempo.getInstance(context).read(PrefSiempo.FAVORITE_APPS, new HashSet<String>());

            for (Iterator<String> it = favorite_List_App.iterator(); it.hasNext(); ) {
                String  packageName = it.next();

                for (MainListItem items : appList) {
                    if (!TextUtils.isEmpty(items.getPackageName()) && items.getPackageName().toLowerCase().trim().equalsIgnoreCase(packageName.toString().trim())) {
                        sortedFavoriteList.add(items);
                    }
                }
            }

            int remainingFavoriteList=12-sortedFavoriteList.size();
            for(int i=0;i<remainingFavoriteList;i++){
                MainListItem m = new MainListItem(-10,"","");
                sortedFavoriteList.add(m);
            }
            return sortedFavoriteList;
        }

        public static ArrayList<MainListItem> addDefaultFavoriteApps(Context context,List<MainListItem> appList){

            LauncherApps launcherApps = (LauncherApps) context.getSystemService(Context.LAUNCHER_APPS_SERVICE);
            Set<String> list = new HashSet<>();
            list = PrefSiempo.getInstance(context).read(PrefSiempo.FAVORITE_APPS, new HashSet<String>());

            ArrayList<MainListItem> items= new ArrayList<>();
            String CHROME_PACKAGE="com.android.chrome",SYSTEM_SETTING="com.android.settings";

            for(int i=0;i<appList.size();i++){
                if(!TextUtils.isEmpty(appList.get(i).getPackageName())){
                    if(appList.get(i).getPackageName().equalsIgnoreCase(CHROME_PACKAGE) || appList.get(i).getPackageName().equalsIgnoreCase(SYSTEM_SETTING)){
                        items.add(appList.get(i));
                    }
                }
            }


            int remainingFavoriteList=12-items.size();
            for(int i=0;i<remainingFavoriteList;i++){
                MainListItem m = new MainListItem(-10,"","");
                items.add(m);
            }


            //get the JSON array of the ordered of sorted customers
            String jsonListOfSortedFavorites = PrefSiempo.getInstance(context).read(PrefSiempo.FAVORITE_SORTED_MENU, "");
            //convert onNoteListChangedJSON array into a List<Long>
            Gson gson1 = new Gson();
            List<String> listOfSortFavoritesApps = gson1.fromJson(jsonListOfSortedFavorites, new TypeToken<List<String>>() {
            }.getType());

            if(listOfSortFavoritesApps!=null){
                if(!listOfSortFavoritesApps.contains(CHROME_PACKAGE)){
                    for(int i=0;i<listOfSortFavoritesApps.size();i++){
                        if(TextUtils.isEmpty(listOfSortFavoritesApps.get(i).trim())){
                            listOfSortFavoritesApps.set(i,CHROME_PACKAGE);
                            if(list!=null && !list.contains(CHROME_PACKAGE)) {
                                list.add(CHROME_PACKAGE);
                            }
                            break;
                        }
                    }
                }

                if(!listOfSortFavoritesApps.contains(SYSTEM_SETTING)){
                    for(int i=0;i<listOfSortFavoritesApps.size();i++){
                        if(TextUtils.isEmpty(listOfSortFavoritesApps.get(i).trim())){
                            listOfSortFavoritesApps.set(i,SYSTEM_SETTING);
                            if(list!=null && !list.contains(SYSTEM_SETTING)) {
                                list.add(SYSTEM_SETTING);
                            }
                            break;
                        }
                    }
                }
            }
            else{
                listOfSortFavoritesApps=new ArrayList<>();
                listOfSortFavoritesApps.add(CHROME_PACKAGE);
                listOfSortFavoritesApps.add(SYSTEM_SETTING);
                int remainingCount=12-listOfSortFavoritesApps.size();
                for(int j=0;j<remainingCount;j++){
                    listOfSortFavoritesApps.add("");
                }

                if(list!=null){
                    list.add(CHROME_PACKAGE);
                    list.add(SYSTEM_SETTING);
                }
            }


            Gson gson2 = new Gson();
            String jsonListOfFavoriteApps = gson2.toJson(listOfSortFavoritesApps);
            PrefSiempo.getInstance(context).write(PrefSiempo.FAVORITE_SORTED_MENU, jsonListOfFavoriteApps);
            PrefSiempo.getInstance(context).write(PrefSiempo.FAVORITE_APPS, list);

            return items;
        }



}
