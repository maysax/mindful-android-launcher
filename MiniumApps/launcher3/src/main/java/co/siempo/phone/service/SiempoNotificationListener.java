package co.siempo.phone.service;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.UserHandle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import co.siempo.phone.R;
import co.siempo.phone.app.Constants;
import co.siempo.phone.app.Launcher3App;
import co.siempo.phone.app.Launcher3Prefs_;
import co.siempo.phone.db.DBClient;
import co.siempo.phone.db.DBUtility;
import co.siempo.phone.db.DaoSession;
import co.siempo.phone.db.StatusBarNotificationStorage;
import co.siempo.phone.db.StatusBarNotificationStorageDao;
import co.siempo.phone.db.TableNotificationSms;
import co.siempo.phone.db.TableNotificationSmsDao;
import co.siempo.phone.event.NewNotificationEvent;
import co.siempo.phone.event.NotificationTrayEvent;
import co.siempo.phone.notification.NotificationUtility;
import co.siempo.phone.util.PackageUtil;
import co.siempo.phone.util.VibrationUtils;
import de.greenrobot.event.EventBus;
import minium.co.core.app.CoreApplication;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.log.Tracer;
import minium.co.core.util.UIUtils;

/**
 * Created by Shahab on 5/16/2017.
 */

@EService
public class SiempoNotificationListener extends NotificationListenerService {

    public static final String TAG = SiempoNotificationListener.class.getName();


    @Pref
    Launcher3Prefs_ prefs;

    @Pref
    DroidPrefs_ droidPrefs;

    @SystemService
    AudioManager audioManager;

    @SystemService
    NotificationManager notificationManager;

    @Bean
    VibrationUtils vibrationUtils;

    @Pref
    Launcher3Prefs_ launcherPrefs;

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Tracer.d("Notification connected");
    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        Tracer.d("Notification Disconnected");
        toggleNotificationListenerService();

    }

    private void toggleNotificationListenerService() {
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(this, SiempoNotificationListener.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(new ComponentName(this, SiempoNotificationListener.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

    }

    @Override
    public void onNotificationChannelModified(String pkg, UserHandle user, NotificationChannel channel, int modificationType) {
        super.onNotificationChannelModified(pkg, user, channel, modificationType);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Tracer.d("Notification onNotificationChannelModified" + "pkg:" + pkg + "user: " + user + " channel: " + channel.toString() + "modificationType: " + modificationType);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification notification) {
        super.onNotificationPosted(notification);
//        Tracer.d("Notification posted: " + getNotificationToString(notification));

        if (PackageUtil.isSiempoLauncher(this)
                || SiempoAccessibilityService.packageName.equalsIgnoreCase(getPackageName())) {

            KeyguardManager myKM = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            if (PackageUtil.isSiempoLauncher(this) && myKM.inKeyguardRestrictedInputMode() && launcherPrefs.isHidenotificationOnLockScreen().get()) {
                SiempoNotificationListener.this.cancelAllNotifications();
            }
            if (PackageUtil.isSiempoLauncher(this) && notification.getNotification().getSortKey() != null && notification.getNotification().getSortKey().equalsIgnoreCase(getResources().getString(R.string.lock_screen_label)) && launcherPrefs.isHidenotificationOnLockScreen().get()) {
                SiempoNotificationListener.this.cancelAllNotifications();
            }

            if (PackageUtil.isSiempoBlocker(notification.getId())) {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                launcherPrefs.getCurrentProfile().put(0);
                prefs.isNotificationBlockerRunning().put(true);
            } else if (prefs.isPauseActive().get() || prefs.isTempoActive().get()) {
                cancelNotification(notification.getKey());
                saveNotification(notification.getPackageName(), notification.getPostTime(),
                        notification.getNotification().tickerText);
                // saving the information in other place
            } else if (launcherPrefs.getCurrentProfile().get() == 0) {
                if (CoreApplication.getInstance().getNormalModeList().contains(notification.getPackageName())) {

                } else {
                    //cancelNotification(notification.getKey());
                    if (CoreApplication.getInstance().getVibrateList().contains(notification.getPackageName())) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                                && !notificationManager.isNotificationPolicyAccessGranted()) {
                        } else {
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        }
                        if (notification.getPackageName().equalsIgnoreCase(Constants.FACEBOOK_PACKAGE)
                                || notification.getPackageName().equalsIgnoreCase(Constants.FACEBOOK_MESSENGER_PACKAGE)
                                || notification.getPackageName().equalsIgnoreCase(Constants.FACEBOOK_LITE_PACKAGE)) {
                            if (droidPrefs.isFacebookAllowed().get()) {
                                vibrationUtils.vibrate(500);
                            }
                        } else {
                            vibrationUtils.vibrate(500);
                        }

                    } else if (CoreApplication.getInstance().getSilentList().contains(notification.getPackageName())) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                                && !notificationManager.isNotificationPolicyAccessGranted()) {
                        } else {
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        }
                    }
                }
            } else if (launcherPrefs.getCurrentProfile().get() == 1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                        && !notificationManager.isNotificationPolicyAccessGranted()) {
                } else {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                }
            } else if (launcherPrefs.getCurrentProfile().get() == 2) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                        && !notificationManager.isNotificationPolicyAccessGranted()) {
                } else {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                }
            }
            printLog(notification);
            filterByCategory(notification);
        }
    }


    private void printLog(StatusBarNotification notification) {
        String strName;
        char[] array;
        long time;
        byte[] userImage;


        Bundle bundle = notification.getNotification().extras;
        String strKey;
        String strValue;
        String finalString = "";
        String strTitle;
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                strKey = " Key :: " + key != null ? key : "";
                if (key.equalsIgnoreCase("android.textLines")) {
                    strValue = " Value ::" + value != null ? "" + value.toString() : "";
                } else {
                    strValue = " Value ::" + value != null ? "" + value : "";
                }
                finalString = finalString + "\n" + strKey + " :" + strValue;

            }
        }

        Log.d("NotificationPosted:",
                "\n" + " Package: " + notification.getPackageName()
                        + "\n" + " Id: " + notification.getId()
                        + "\n" + " Post time: " + SimpleDateFormat.getDateTimeInstance().format(new Date(notification.getPostTime()))
                        + "\n" + " Details: " + notification.getNotification().toString()
                        + "\n" + " Category: " + notification.getNotification().category
                        + "\n" + " Ticker: " + notification.getNotification().tickerText
                        + "\n" + " Bundle Data:" + finalString);
        Tracer.d("NotificationPosted : " + " Package: " + notification.getPackageName()
                + "\n" + " Id: " + notification.getId()
                + "\n" + " Post time: " + SimpleDateFormat.getDateTimeInstance().format(new Date(notification.getPostTime()))
                + "\n" + " Details: " + notification.getNotification().toString()
                + "\n" + " Category: " + notification.getNotification().category
                + "\n" + " Ticker: " + notification.getNotification().tickerText
                + "\n" + " Bundle Data:" + finalString);
    }


    private void saveNotification(String packageName, long postTime, CharSequence tickerText) {
        try {
            StatusBarNotificationStorageDao statusStorageDao = DBUtility.getStatusStorageDao();
            StatusBarNotificationStorage storage = new StatusBarNotificationStorage();
            storage.setContent(tickerText.toString());
            storage.setPackageName(packageName);
            storage.setPostTime(postTime);
            statusStorageDao.insert(storage);
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }
    }

    private synchronized void filterByCategory(StatusBarNotification statusBarNotification) {
        String strPackageName;//getPackageName
        String strTitle = null;//android.title
        String strConversationTitle = null;//android.title
        String strText = null;//android.text
        Date date;
        String data = "";
        String strBigText = null;//android.subText
        int icon = 0;//android.icon
        byte[] largeIcon = new byte[0];// android.largeIcon
        strPackageName = statusBarNotification.getPackageName();

        date = new Date(statusBarNotification.getPostTime());

        if (statusBarNotification.getNotification().extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES) != null) {
            CharSequence[] test = statusBarNotification.getNotification().extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);
            ArrayList<String> list = new ArrayList<>();
            for (CharSequence charSequence : test) {
                list.add("" + charSequence);
            }
            Collections.reverse(list);
            for (String string : list) {
                data = data + string + "\n";
            }

        }

        if (statusBarNotification.getNotification().extras.getString(Notification.EXTRA_TITLE) != null
                && !statusBarNotification.getNotification().extras.getString(Notification.EXTRA_TITLE).equalsIgnoreCase("")) {
            strTitle = statusBarNotification.getNotification().extras.getString(Notification.EXTRA_TITLE);
        }

        if (statusBarNotification.getNotification().extras.getString(Notification.EXTRA_CONVERSATION_TITLE) != null
                && !statusBarNotification.getNotification().extras.getString(Notification.EXTRA_CONVERSATION_TITLE).equalsIgnoreCase("")) {
            strConversationTitle = statusBarNotification.getNotification().extras.getString(Notification.EXTRA_CONVERSATION_TITLE);
        }
        try {
            if (statusBarNotification.getNotification().extras.get(Notification.EXTRA_TEXT) != null) {
                strText = statusBarNotification.getNotification().extras.get(Notification.EXTRA_TEXT).toString();
            }
        } catch (Exception e) {

        }


        if (statusBarNotification.getNotification().extras.getString(Notification.EXTRA_BIG_TEXT) != null
                && !statusBarNotification.getNotification().extras.getString(Notification.EXTRA_BIG_TEXT).equalsIgnoreCase("")) {
            strBigText = statusBarNotification.getNotification().extras.getString(Notification.EXTRA_BIG_TEXT);
        }

        if (statusBarNotification.getNotification().extras.getInt(Notification.EXTRA_SMALL_ICON) != 0) {
            icon = statusBarNotification.getNotification().extras.getInt(Notification.EXTRA_SMALL_ICON);
        }

        if (statusBarNotification.getNotification().extras.getParcelable(Notification.EXTRA_LARGE_ICON) != null) {
            Bitmap iconUser = statusBarNotification.getNotification().extras.getParcelable(Notification.EXTRA_LARGE_ICON);
            largeIcon = UIUtils.convertBitmapToByte(iconUser);
        }

        if (statusBarNotification.getNotification().extras.getParcelable(Notification.EXTRA_LARGE_ICON) != null) {
            Bitmap iconUser = statusBarNotification.getNotification().extras.getParcelable(Notification.EXTRA_LARGE_ICON);
            largeIcon = UIUtils.convertBitmapToByte(iconUser);
        }

        if (statusBarNotification.getNotification().extras.getParcelable(Notification.EXTRA_LARGE_ICON) != null) {
            Bitmap iconUser = statusBarNotification.getNotification().extras.getParcelable(Notification.EXTRA_LARGE_ICON);
            largeIcon = UIUtils.convertBitmapToByte(iconUser);
        }

        //whatsapp used in Future Sprint.
//        if (statusBarNotification.getPackageName().equalsIgnoreCase(Constants.WHATSAPP_PACKAGE)) {
//            DaoSession daoSession = ((Launcher3App) CoreApplication.getInstance()).getDaoSession();
//            TableNotificationSmsDao smsDao = daoSession.getTableNotificationSmsDao();
//
//            try {
//                if (statusBarNotification.getNotification().extras != null) {
//                    if (Constants.WHATSAPP_PACKAGE.equals(statusBarNotification.getPackageName())) {
//                        Bundle extras = statusBarNotification.getNotification().extras;
//                        String title = "";
//                        String text = "";
//                        Bitmap bitmap = null;
//                        if (extras != null) {
//                            title = extras.getString(NotificationCompat.EXTRA_TITLE) == null ? "" : extras.getString(NotificationCompat.EXTRA_TITLE);
//                            if (extras.getCharSequence(NotificationCompat.EXTRA_TEXT) == null) {
//                                text = "";
//                            } else {
//                                text = extras.getCharSequence(NotificationCompat.EXTRA_TEXT).toString();
//                            }
//                            text = Build.VERSION.SDK_INT >= 21 ? getNotificationTextLegacy(statusBarNotification.getNotification(), text) : getNotificationTextLegacy(statusBarNotification.getNotification(), text);
//                            if (title == null || title.isEmpty() || Constants.WHATSAPP.equals(title.trim())) {
//                                title = getTitleLegacy(text);
//                                text = fixTextLegacy(text);
//                            }
//                            if (!Constants.WHATSAPP.equals(title.trim())) {
//                                String[] text_comp = text.split(" ");
//                                if (text_comp != null && text_comp.length > 0) {
//                                    String text_p1 = text_comp[0] != null ? text_comp[0] : "";
//                                    if (isInteger(text_p1, 10) || Constants.RISALATAN.equals(text_p1)) {
//                                        return;
//                                    }
//                                }
//                                if (title.contains("@")) {
//                                    String title_p1 = title.substring(0, title.indexOf("@")).trim();
//                                    title = title.substring(title.indexOf("@") + 1).trim();
//                                    text = title_p1 + ": " + text;
//                                }
//                                if (Build.VERSION.SDK_INT > 23) {
//                                    title = removeXnewMessageFromSender(title);
//                                    if (!"CODE_IGNORE_ME".equals(title)) {
//                                        title = removeColFromTitle(title);
//                                        if (title.contains(":")) {
//                                            return;
//                                        }
//                                    } else {
//                                        return;
//                                    }
//
//                                    // return;
//                                }
//                                if (extras.getParcelable(NotificationCompat.EXTRA_LARGE_ICON) != null) {
//                                    bitmap = extras.getParcelable(NotificationCompat.EXTRA_LARGE_ICON);
//                                    largeIcon = UIUtils.convertBitmapToByte(bitmap);
//                                }
//                                //BitmapUtils.saveBitmapToInternalStorage(title, (Bitmap) extras.getParcelable(NotificationCompat.EXTRA_LARGE_ICON), this);
//                            } else {
//                                return;
//                            }
//                        }
//
//
//                        if (statusBarNotification.getNotification().category == null
//                                || !statusBarNotification.getNotification().category.equalsIgnoreCase(Notification.CATEGORY_CALL)) {
//                            TableNotificationSms notificationSms = DBUtility.getNotificationDao().queryBuilder()
//                                    .where(TableNotificationSmsDao.Properties.PackageName.eq(statusBarNotification.getPackageName()),
//                                            TableNotificationSmsDao.Properties._contact_title.eq(title),
//                                            TableNotificationSmsDao.Properties.Notification_type.eq(NotificationUtility.NOTIFICATION_TYPE_EVENT))
//                                    .unique();
//                            if (notificationSms == null) {
//                                if (!title.contains("WhatsApp") && !title.equalsIgnoreCase("Checking for new messages")) {
//                                    notificationSms = new TableNotificationSms();
//                                    notificationSms.set_contact_title(title);
//                                    notificationSms.set_message(text);
//                                    notificationSms.set_date(date);
//                                    notificationSms.setNotification_date(statusBarNotification.getPostTime());
//                                    notificationSms.setNotification_type(NotificationUtility.NOTIFICATION_TYPE_EVENT);
//                                    notificationSms.setPackageName(strPackageName);
//                                    notificationSms.setApp_icon(icon);
//                                    notificationSms.setUser_icon(largeIcon);
//                                    long id = smsDao.insertOrReplace(notificationSms);
//                                    notificationSms.setId(id);
//                                    EventBus.getDefault().post(new NewNotificationEvent(notificationSms));
//                                }
//                            } else {
////                                if (!text.equalsIgnoreCase("Incoming voice call") && !text.equalsIgnoreCase("Incoming video call")) {
//                                if (!title.contains("WhatsApp") && !title.equalsIgnoreCase("Checking for new messages")) {
//                                    notificationSms.set_date(date);
//                                    notificationSms.setNotification_date(statusBarNotification.getPostTime());
//                                    notificationSms.set_message(text + "\n" + notificationSms.get_message());
//                                    smsDao.updateInTx(notificationSms);
//                                    EventBus.getDefault().post(new NewNotificationEvent(notificationSms));
//                                }
//                            }
//                        }
//                        if (statusBarNotification.getNotification().category == null
//                                || statusBarNotification.getNotification().category.equalsIgnoreCase(Notification.CATEGORY_CALL)) {
//
//                            if (title.toLowerCase().trim().contains("miss")
//                                    || title.toLowerCase().trim().contains("missed")) {
//                                if (title.matches(".*\\d.*")) {
//                                    // contains a number
//                                    title = title.replaceAll("^([0-9]+)", "");
//                                }
//                                TableNotificationSms notificationSms = DBUtility.getNotificationDao().queryBuilder()
//                                        .where(TableNotificationSmsDao.Properties.PackageName.eq(statusBarNotification.getPackageName()),
//                                                TableNotificationSmsDao.Properties._contact_title.eq("Missed call"),
//                                                TableNotificationSmsDao.Properties.Notification_type.eq(NotificationUtility.NOTIFICATION_TYPE_EVENT))
//                                        .unique();
//                                if (notificationSms == null) {
//                                    if (!title.contains("WhatsApp")) {
//                                        notificationSms = new TableNotificationSms();
//                                        notificationSms.set_contact_title("Missed call");
//                                        notificationSms.set_message(text);
//                                        notificationSms.set_date(date);
//                                        notificationSms.setNotification_date(statusBarNotification.getPostTime());
//                                        notificationSms.setNotification_type(NotificationUtility.NOTIFICATION_TYPE_EVENT);
//                                        notificationSms.setPackageName(strPackageName);
//                                        notificationSms.setApp_icon(icon);
//                                        notificationSms.setUser_icon(largeIcon);
//                                        long id = smsDao.insertOrReplace(notificationSms);
//                                        notificationSms.setId(id);
//                                        EventBus.getDefault().post(new NewNotificationEvent(notificationSms));
//                                    }
//                                } else {
//                                    if (!title.contains("WhatsApp")) {
//                                        notificationSms.set_date(date);
//                                        notificationSms.setUser_icon(null);
//                                        notificationSms.set_contact_title("Missed call");
//                                        notificationSms.setNotification_date(statusBarNotification.getPostTime());
//                                        notificationSms.set_message(text);
//                                        smsDao.updateInTx(notificationSms);
//                                        EventBus.getDefault().post(new NewNotificationEvent(notificationSms));
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            } catch (Exception e) {
//            }
//
//        }

        // event Calender
        try {
            if (statusBarNotification.getPackageName().equalsIgnoreCase(Constants.GOOGLE_CALENDAR_PACKAGES)) {
                DaoSession daoSession = ((Launcher3App) CoreApplication.getInstance()).getDaoSession();
                TableNotificationSmsDao smsDao = daoSession.getTableNotificationSmsDao();
                TableNotificationSms notificationSms = DBUtility.getNotificationDao().queryBuilder()
                        .where(TableNotificationSmsDao.Properties._contact_title.eq(strTitle),
                                TableNotificationSmsDao.Properties.Notification_type.eq(NotificationUtility.NOTIFICATION_TYPE_EVENT))
                        .unique();
                if (notificationSms == null) {
                    notificationSms = new TableNotificationSms();
                    notificationSms.set_contact_title(strTitle);
                    notificationSms.set_message(strText);
                    notificationSms.set_date(date);
                    notificationSms.setNotification_date(statusBarNotification.getPostTime());
                    notificationSms.setNotification_type(NotificationUtility.NOTIFICATION_TYPE_EVENT);
                    notificationSms.setPackageName(strPackageName);
                    notificationSms.setApp_icon(icon);
                    notificationSms.setUser_icon(largeIcon);
                    notificationSms.setNotification_id(statusBarNotification.getId());
                    long id = smsDao.insert(notificationSms);
                    notificationSms.setId(id);
                    EventBus.getDefault().post(new NewNotificationEvent(notificationSms));

                    //cancelNotification(statusBarNotification.getKey());
                } else {
                    notificationSms.set_date(date);
                    notificationSms.setNotification_date(statusBarNotification.getPostTime());
                    notificationSms.set_contact_title(strTitle);
                    if (strBigText == null) {
                        notificationSms.set_message(strText);
                    } else {
                        notificationSms.set_message(strBigText);
                    }
                    smsDao.update(notificationSms);
                    EventBus.getDefault().post(new NewNotificationEvent(notificationSms));
                    // cancelNotification(statusBarNotification.getKey());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Facebook
        try {
            if (droidPrefs.isFacebookAllowed().get() && statusBarNotification.getPackageName().equalsIgnoreCase(Constants.FACEBOOK_PACKAGE)) {
                DaoSession daoSession = ((Launcher3App) CoreApplication.getInstance()).getDaoSession();
                TableNotificationSmsDao smsDao = daoSession.getTableNotificationSmsDao();
                TableNotificationSms notificationSms = new TableNotificationSms();
                notificationSms.set_contact_title(strTitle);
                notificationSms.set_message(strText);
                notificationSms.set_date(date);
                notificationSms.setNotification_date(statusBarNotification.getPostTime());
                notificationSms.setNotification_type(NotificationUtility.NOTIFICATION_TYPE_EVENT);
                notificationSms.setPackageName(strPackageName);
                notificationSms.setApp_icon(icon);
                notificationSms.setUser_icon(largeIcon);
                notificationSms.setNotification_id(statusBarNotification.getId());
                long id = smsDao.insert(notificationSms);
                notificationSms.setId(id);
                EventBus.getDefault().post(new NewNotificationEvent(notificationSms));
                //cancelNotification(statusBarNotification.getKey());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Facebook Messenger
        try {
            if (droidPrefs.isFacebookAllowed().get() && statusBarNotification.getPackageName().equalsIgnoreCase(Constants.FACEBOOK_MESSENGER_PACKAGE)) {
                DaoSession daoSession = ((Launcher3App) CoreApplication.getInstance()).getDaoSession();
                TableNotificationSmsDao smsDao = daoSession.getTableNotificationSmsDao();
                TableNotificationSms notificationSms
                        = DBUtility.getNotificationDao().queryBuilder()
                        .where(TableNotificationSmsDao.Properties.PackageName.eq(strPackageName),
                                TableNotificationSmsDao.Properties._contact_title.eq(strTitle),
                                TableNotificationSmsDao.Properties.Notification_type.eq(NotificationUtility.NOTIFICATION_TYPE_EVENT))
                        .unique();
                if (strTitle != null & !strTitle.equalsIgnoreCase("Chat heads active")) {
                    if (notificationSms == null) {
                        notificationSms = new TableNotificationSms();
                        notificationSms.set_contact_title(strTitle);
                        notificationSms.set_message(strText);
                        notificationSms.set_date(date);
                        notificationSms.setNotification_date(statusBarNotification.getPostTime());
                        notificationSms.setNotification_type(NotificationUtility.NOTIFICATION_TYPE_EVENT);
                        notificationSms.setPackageName(strPackageName);
                        notificationSms.setApp_icon(icon);
                        notificationSms.setUser_icon(largeIcon);
                        notificationSms.setNotification_id(statusBarNotification.getId());
                        long id = smsDao.insert(notificationSms);
                        notificationSms.setId(id);
                        EventBus.getDefault().post(new NewNotificationEvent(notificationSms));
                       // cancelNotification(statusBarNotification.getKey());
                    } else {
                        notificationSms.setPackageName(strPackageName);
                        notificationSms.set_date(date);
                        notificationSms.setNotification_date(statusBarNotification.getPostTime());
                        notificationSms.set_message(strText + "\n" + notificationSms.get_message());
                        notificationSms.set_contact_title(strTitle);
                        smsDao.update(notificationSms);
                        EventBus.getDefault().post(new NewNotificationEvent(notificationSms));
                       // cancelNotification(statusBarNotification.getKey());
                    }
                }
                if (statusBarNotification.getNotification().category != null && statusBarNotification.getNotification().category.equalsIgnoreCase(Notification.CATEGORY_CALL)) {
                    EventBus.getDefault().post(new NotificationTrayEvent(true));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Facebook Lite Messenger
        try {
            if (droidPrefs.isFacebookAllowed().get() && statusBarNotification.getPackageName().equalsIgnoreCase(Constants.FACEBOOK_LITE_PACKAGE)) {
                DaoSession daoSession = ((Launcher3App) CoreApplication.getInstance()).getDaoSession();
                TableNotificationSmsDao smsDao = daoSession.getTableNotificationSmsDao();
                TableNotificationSms notificationSms
                        = DBUtility.getNotificationDao().queryBuilder()
                        .where(TableNotificationSmsDao.Properties.PackageName.eq(strPackageName),
                                TableNotificationSmsDao.Properties._contact_title.eq(strTitle),
                                TableNotificationSmsDao.Properties.Notification_type.eq(NotificationUtility.NOTIFICATION_TYPE_EVENT))
                        .unique();
                // if (strTitle != null & !strTitle.equalsIgnoreCase("Chat heads active")) {

                if (notificationSms == null) {
                    notificationSms = new TableNotificationSms();
                    notificationSms.set_contact_title(strTitle);
                    notificationSms.set_message(strText);
                    notificationSms.set_date(date);
                    notificationSms.setNotification_date(statusBarNotification.getPostTime());
                    notificationSms.setNotification_type(NotificationUtility.NOTIFICATION_TYPE_EVENT);
                    notificationSms.setPackageName(strPackageName);
                    notificationSms.setApp_icon(icon);
                    notificationSms.setUser_icon(largeIcon);
                    notificationSms.setNotification_id(statusBarNotification.getId());
                    long id = smsDao.insert(notificationSms);
                    notificationSms.setId(id);
                    EventBus.getDefault().post(new NewNotificationEvent(notificationSms));
                    //cancelNotification(statusBarNotification.getKey());
                } else {
                    notificationSms.setPackageName(strPackageName);
                    notificationSms.set_date(date);
                    notificationSms.setNotification_date(statusBarNotification.getPostTime());
                    notificationSms.set_message(strText + "\n" + notificationSms.get_message());
                    notificationSms.set_contact_title(strTitle);
                    smsDao.update(notificationSms);
                    EventBus.getDefault().post(new NewNotificationEvent(notificationSms));
                   // cancelNotification(statusBarNotification.getKey());
                }
                if (statusBarNotification.getNotification().category != null && statusBarNotification.getNotification().category.equalsIgnoreCase(Notification.CATEGORY_CALL)) {
                    EventBus.getDefault().post(new NotificationTrayEvent(true));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // HangOut Messenger used in Future Sprint.
//        if (statusBarNotification.getPackageName().equalsIgnoreCase(Constants.GOOGLE_HANGOUTS_PACKAGES)) {
//            DaoSession daoSession = ((Launcher3App) CoreApplication.getInstance()).getDaoSession();
//            TableNotificationSmsDao smsDao = daoSession.getTableNotificationSmsDao();
//
//
//            if (statusBarNotification.getNotification().category != null
//                    && !statusBarNotification.getNotification().category.equalsIgnoreCase(Notification.CATEGORY_CALL)) {
//                TableNotificationSms notificationSms
//                        = DBUtility.getNotificationDao().queryBuilder()
//                        .where(TableNotificationSmsDao.Properties.PackageName.eq(strPackageName),
//                                TableNotificationSmsDao.Properties._contact_title.eq(strConversationTitle),
//                                TableNotificationSmsDao.Properties.Notification_type.eq(NotificationUtility.NOTIFICATION_TYPE_EVENT))
//                        .unique();
//                if (notificationSms == null) {
//                    notificationSms = new TableNotificationSms();
//                    notificationSms.set_contact_title(strConversationTitle);
//                    notificationSms.set_message(strText);
//                    notificationSms.set_date(date);
//                    notificationSms.setNotification_date(statusBarNotification.getPostTime());
//                    notificationSms.setNotification_type(NotificationUtility.NOTIFICATION_TYPE_EVENT);
//                    notificationSms.setPackageName(strPackageName);
//                    notificationSms.setApp_icon(icon);
//                    notificationSms.setUser_icon(largeIcon);
//                    notificationSms.setNotification_id(statusBarNotification.getId());
//                    long id = smsDao.insert(notificationSms);
//                    notificationSms.setId(id);
//                    EventBus.getDefault().post(new NewNotificationEvent(notificationSms));
////                    cancelNotification(statusBarNotification.getKey());
//                } else {
//                    notificationSms.setPackageName(strPackageName);
//                    notificationSms.set_date(date);
//                    notificationSms.setNotification_date(statusBarNotification.getPostTime());
//                    notificationSms.set_message(strText + "\n" + notificationSms.get_message());
//                    notificationSms.set_contact_title(strConversationTitle);
//                    smsDao.update(notificationSms);
//                    EventBus.getDefault().post(new NewNotificationEvent(notificationSms));
////                    cancelNotification(statusBarNotification.getKey());
//                }
//            } else {
//                if (statusBarNotification.getNotification().tickerText.toString().equalsIgnoreCase("Missed call")) {
//                    TableNotificationSms notificationSms
//                            = DBUtility.getNotificationDao().queryBuilder()
//                            .where(TableNotificationSmsDao.Properties.PackageName.eq(strPackageName),
//                                    TableNotificationSmsDao.Properties._contact_title.eq(strTitle),
//                                    TableNotificationSmsDao.Properties.Notification_type.eq(NotificationUtility.NOTIFICATION_TYPE_EVENT))
//                            .unique();
//                    if (notificationSms == null) {
//                        notificationSms = new TableNotificationSms();
//                        notificationSms.set_contact_title(strTitle);
//                        notificationSms.set_message(strText);
//                        notificationSms.set_date(date);
//                        notificationSms.setNotification_date(statusBarNotification.getPostTime());
//                        notificationSms.setNotification_type(NotificationUtility.NOTIFICATION_TYPE_EVENT);
//                        notificationSms.setPackageName(strPackageName);
//                        notificationSms.setApp_icon(icon);
//                        notificationSms.setUser_icon(largeIcon);
//                        notificationSms.setNotification_id(statusBarNotification.getId());
//                        long id = smsDao.insert(notificationSms);
//                        notificationSms.setId(id);
//                        EventBus.getDefault().post(new NewNotificationEvent(notificationSms));
////                        cancelNotification(statusBarNotification.getKey());
//                    } else {
//                        notificationSms.setPackageName(strPackageName);
//                        notificationSms.set_date(date);
//                        notificationSms.setNotification_date(statusBarNotification.getPostTime());
//                        notificationSms.set_message(strText + "\n" + notificationSms.get_message());
//                        notificationSms.set_contact_title(strTitle);
//                        smsDao.update(notificationSms);
//                        EventBus.getDefault().post(new NewNotificationEvent(notificationSms));
////                        cancelNotification(statusBarNotification.getKey());
//                    }
//                }
//            }
//        }

    }


    @Override
    public void onNotificationRemoved(StatusBarNotification notification) {
        super.onNotificationRemoved(notification);
        Tracer.d("Notification removed: " + getNotificationToString(notification));

        if (PackageUtil.isSiempoBlocker(notification.getId())) {
            prefs.isNotificationBlockerRunning().put(false);
        }
        if (!PackageUtil.isSiempoLauncher(this)
                && !SiempoAccessibilityService.packageName.equalsIgnoreCase(getPackageName())) {
            if (PackageUtil.isMsgPackage(notification.getPackageName())) {
                new DBClient().deleteMsgByType(NotificationUtility.NOTIFICATION_TYPE_SMS);
            } else if (PackageUtil.isCallPackage(notification.getPackageName())) {
                new DBClient().deleteMsgByType(NotificationUtility.NOTIFICATION_TYPE_CALL);
            } else {
                new DBClient().deleteMsgByPackageName(notification.getPackageName());
            }
        }
    }

    private String getNotificationToString(StatusBarNotification notification) {
        return "package: " + notification.getPackageName()
                + "Id: " + notification.getId()
                + " Post time: " + SimpleDateFormat.getDateTimeInstance().format(new Date(notification.getPostTime()))
                + " Details: " + notification.getNotification().toString()
                + " Ticker: " + notification.getNotification().tickerText;
    }

    @Override
    public void onNotificationRankingUpdate(RankingMap rankingMap) {
        super.onNotificationRankingUpdate(rankingMap);
    }

    private static boolean isInteger(String s, int radix) {
        try {
            if (s.isEmpty()) {
                return false;
            }
            int i = 0;
            while (i < s.length()) {
                if (i == 0 && s.charAt(i) == '-') {
                    if (s.length() == 1) {
                        return false;
                    }
                } else if (Character.digit(s.charAt(i), radix) < 0) {
                    return false;
                }
                i++;
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    private String getNotificationTextLegacy(Notification notification, String defaultText) {
        String notificationText = "";
        if (!(notification == null || notification.extras == null)) {
            CharSequence[] lines = notification.extras.getCharSequenceArray(NotificationCompat.EXTRA_TEXT_LINES);
            if (lines != null) {
                for (CharSequence msg : lines) {
                    if (msg != null) {
                        notificationText = msg.toString();
                    }
                }
            }
        }
        if (notificationText == null || notificationText.isEmpty()) {
            return defaultText;
        }
        return notificationText;
    }

    private String getTitleLegacy(String text) {
        if (text == null || text.indexOf(":") <= 0) {
            return text;
        }
        return text.substring(0, text.indexOf(":"));
    }

    private String fixTextLegacy(String text) {
        if (text == null || text.indexOf(":") <= 0 || text.indexOf(":") == text.length() - 1) {
            return text;
        }
        return text.substring(text.indexOf(":") + 1);
    }

    private String removeXnewMessageFromSender(String title) {
        String finalTitle = title;
        if (finalTitle == null || !finalTitle.contains("(") || !finalTitle.contains(")")) {
            return finalTitle;
        }
        int lastParenthesiIndex = finalTitle.lastIndexOf("(");
        int lastEndParenthesisIndex = finalTitle.lastIndexOf(")");
        if (true) {
            return "CODE_IGNORE_ME";
        }
        return finalTitle;
    }

    private String removeColFromTitle(String title) {
        String finalTitle = title == null ? null : title.trim();
        try {
            return (finalTitle.length() <= 3 || finalTitle.charAt(finalTitle.length() - 3) != ':') ? finalTitle : finalTitle.substring(0, finalTitle.length() - 3);
        } catch (Exception e) {
            return title;
        }
    }
}
