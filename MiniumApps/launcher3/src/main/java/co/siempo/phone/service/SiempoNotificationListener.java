package co.siempo.phone.service;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.UserHandle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.lang.reflect.Type;
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
    DroidPrefs_ droidPrefs;

    @Pref
    Launcher3Prefs_ launcherPrefs;

    @SystemService
    AudioManager audioManager;

    @SystemService
    NotificationManager notificationManager;

    @Bean
    VibrationUtils vibrationUtils;

    Context context;

    ArrayList<String> disableNotificationApps = new ArrayList<>();

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Tracer.d("Notification connected");
    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        Tracer.d("Notification Disconnected");

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
        context = this;
        printLog(notification);
        if (PackageUtil.isSiempoLauncher(context)) {
            Log.d(TAG, "Suppress Notification Section" + notification.getPackageName());

            KeyguardManager myKM = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            if (PackageUtil.isSiempoLauncher(this) && myKM.inKeyguardRestrictedInputMode() && launcherPrefs.isHidenotificationOnLockScreen().get()) {
                SiempoNotificationListener.this.cancelAllNotifications();
            }
            if (PackageUtil.isSiempoLauncher(this) && notification.getNotification().getSortKey() != null && notification.getNotification().getSortKey().equalsIgnoreCase(getResources().getString(R.string.lock_screen_label)) && launcherPrefs.isHidenotificationOnLockScreen().get()) {
                SiempoNotificationListener.this.cancelAllNotifications();
            }

            SharedPreferences prefs = getSharedPreferences("Launcher3Prefs", 0);
            String disable_AppList = prefs.getString(Constants.DISABLE_APPLIST, "");
            if (!TextUtils.isEmpty(disable_AppList)) {
                Type type = new TypeToken<ArrayList<String>>() {
                }.getType();
                disableNotificationApps = new ArrayList<>();
                disableNotificationApps = new Gson().fromJson(disable_AppList, type);
                if (!TextUtils.isEmpty(notification.getPackageName()) && disableNotificationApps.contains(notification.getPackageName())) {
                    SiempoNotificationListener.this.cancelNotification(notification.getKey());
                    filterByCategory(notification);
                    return;
                }
            }
        }
        if (launcherPrefs.isAppDefaultOrFront().get()) {

            if (launcherPrefs.getCurrentProfile().get() == 0) {
                Log.d("Profile Check:::", "NotificationListener : getCurrentProfile Normal 0");
                if (CoreApplication.getInstance().getSilentList().contains(notification.getPackageName())) {
                    CoreApplication.getInstance().changeProfileToSilentMode();
                } else if (CoreApplication.getInstance().getVibrateList().contains(notification.getPackageName())) {
                    Log.d("Profile Check:::", "NotificationListener : getCurrentProfile Normal 0 - Vibrate");
                    CoreApplication.getInstance().changeProfileToSilentMode();
                    if (!disableNotificationApps.contains(notification.getPackageName())) {
                        vibrationUtils.vibrate(500);
                    }
                }
            } else if (launcherPrefs.getCurrentProfile().get() == 1) {
                Log.d("Profile Check:::", "NotificationListener : getCurrentProfile Vibrate 1");
                CoreApplication.getInstance().changeProfileToVibrateMode();
            } else if (launcherPrefs.getCurrentProfile().get() == 2) {
                Log.d("Profile Check:::", "NotificationListener : getCurrentProfile Silent 2 ");
                CoreApplication.getInstance().changeProfileToSilentMode();
            }
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
        StringBuilder finalString = new StringBuilder();
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
                finalString.append("\n").append(strKey).append(" :").append(strValue);

            }
        }
        Tracer.d("NotificationPosted : " + " Package: " + notification.getPackageName()
                + "\n" + " Id: " + notification.getId()
                + "\n" + " Post time: " + SimpleDateFormat.getDateTimeInstance().format(new Date(notification.getPostTime()))
                + "\n" + " Details: " + notification.getNotification().toString()
                + "\n" + " Category: " + notification.getNotification().category
                + "\n" + " Ticker: " + notification.getNotification().tickerText
                + "\n" + " Bundle Data:" + finalString);
    }


    private synchronized void filterByCategory(StatusBarNotification statusBarNotification) {
        String strPackageName;//getPackageName
        String strTitle = null;//android.title
        String strConversationTitle = null;//android.title
        String strText = null;//android.text
        Date date;
        StringBuilder data = new StringBuilder();
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
                data.append(string).append("\n");
            }

        }

        if (statusBarNotification.getNotification().extras.getString(Notification.EXTRA_TITLE) != null
                && !statusBarNotification.getNotification().extras.getString(Notification.EXTRA_TITLE).equalsIgnoreCase("")) {
            strTitle = statusBarNotification.getNotification().extras.getString(Notification.EXTRA_TITLE);
        }

        if (statusBarNotification.getNotification().extras.getString(Notification.EXTRA_CONVERSATION_TITLE) != null
                && !statusBarNotification.getNotification().extras.getString(Notification.EXTRA_CONVERSATION_TITLE).equalsIgnoreCase("")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            }
        }
        try {
            if (statusBarNotification.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT) != null) {
                CharSequence charText = statusBarNotification.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT).toString();
                strText = charText.toString();
            }
        } catch (Exception e) {
            CoreApplication.getInstance().logException(e);
        }


        if (statusBarNotification.getNotification().extras.getCharSequence(Notification.EXTRA_BIG_TEXT) != null
                && !statusBarNotification.getNotification().extras.getCharSequence(Notification.EXTRA_BIG_TEXT).toString().equalsIgnoreCase("")) {
            CharSequence charBigText = statusBarNotification.getNotification().extras.getCharSequence(Notification.EXTRA_BIG_TEXT);
            strBigText = charBigText.toString();
        }
        try {
            if (statusBarNotification.getNotification().extras.getInt(Notification.EXTRA_SMALL_ICON) != 0) {
                icon = statusBarNotification.getNotification().extras.getInt(Notification.EXTRA_SMALL_ICON);
            }
        } catch (Exception e) {
            CoreApplication.getInstance().logException(e);
            Tracer.d(e.getMessage());
        }

        try {
            if (statusBarNotification.getNotification().extras.getParcelable(Notification.EXTRA_LARGE_ICON) != null) {
                Bitmap iconUser = statusBarNotification.getNotification().extras.getParcelable(Notification.EXTRA_LARGE_ICON);
                largeIcon = UIUtils.convertBitmapToByte(iconUser);
            }
        } catch (Exception e) {
            CoreApplication.getInstance().logException(e);
            Tracer.d(e.getMessage());
        }


        //whatsapp used in Future Sprint.
        if (statusBarNotification.getPackageName().equalsIgnoreCase(Constants.WHATSAPP_PACKAGE)) {
            DaoSession daoSession = ((Launcher3App) CoreApplication.getInstance()).getDaoSession();
            TableNotificationSmsDao smsDao = daoSession.getTableNotificationSmsDao();
            try {
                if (statusBarNotification.getNotification().extras != null) {
                    if (Constants.WHATSAPP_PACKAGE.equals(statusBarNotification.getPackageName())) {
                        Bundle extras = statusBarNotification.getNotification().extras;
                        String title = "";
                        String text = "";
                        Bitmap bitmap = null;
                        if (extras != null) {
                            title = extras.getString(NotificationCompat.EXTRA_TITLE) == null ? "" : extras.getString(NotificationCompat.EXTRA_TITLE);
                            if (extras.getCharSequence(NotificationCompat.EXTRA_TEXT) == null) {
                                text = "";
                            } else {
                                text = extras.getCharSequence(NotificationCompat.EXTRA_TEXT).toString();
                            }
                            text = Build.VERSION.SDK_INT >= 21 ? getNotificationTextLegacy(statusBarNotification.getNotification(), text) : getNotificationTextLegacy(statusBarNotification.getNotification(), text);
                            if (title == null || title.isEmpty() || Constants.WHATSAPP.equals(title.trim())) {
                                title = getTitleLegacy(text);
                                text = fixTextLegacy(text);
                            }
                            if (!Constants.WHATSAPP.equals(title.trim())) {
                                String[] text_comp = text.split(" ");
                                if (text_comp != null && text_comp.length > 0) {
                                    String text_p1 = text_comp[0] != null ? text_comp[0] : "";
                                    if (isInteger(text_p1, 10)) {
                                        return;
                                    }
                                }
                                if (title.contains("@")) {
                                    String title_p1 = title.substring(0, title.indexOf("@")).trim();
                                    title = title.substring(title.indexOf("@") + 1).trim();
                                    text = title_p1 + ": " + text;
                                }
                                if (Build.VERSION.SDK_INT > 23) {
                                    title = removeXnewMessageFromSender(title);
                                    if (!"CODE_IGNORE_ME".equals(title)) {
                                        title = removeColFromTitle(title);
                                        if (title.contains(":")) {
                                            return;
                                        }
                                    } else {
                                        return;
                                    }
                                }
                            } else {
                                return;
                            }
                        }

                        if (statusBarNotification.getNotification().category == null
                                || !statusBarNotification.getNotification().category.equalsIgnoreCase(Notification.CATEGORY_CALL)) {

                            TableNotificationSms notificationSms = DBUtility.getNotificationDao().queryBuilder()
                                    .where(TableNotificationSmsDao.Properties.PackageName.eq(statusBarNotification.getPackageName()),
                                            TableNotificationSmsDao.Properties._contact_title.eq(title),
                                            TableNotificationSmsDao.Properties.Notification_type.eq(NotificationUtility.NOTIFICATION_TYPE_EVENT))
                                    .unique();
                            if (notificationSms == null) {
                                if (!title.contains("WhatsApp") && !title.equalsIgnoreCase("Checking for new messages")) {
                                    notificationSms = new TableNotificationSms();
                                    notificationSms.set_contact_title(title);
                                    notificationSms.set_message(text);
                                    notificationSms.set_date(date);
                                    notificationSms.setNotification_date(statusBarNotification.getPostTime());
                                    notificationSms.setNotification_type(NotificationUtility.NOTIFICATION_TYPE_EVENT);
                                    notificationSms.setPackageName(strPackageName);
                                    notificationSms.setApp_icon(icon);
                                    notificationSms.setUser_icon(largeIcon);

                                    long id = smsDao.insertOrReplace(notificationSms);
                                    notificationSms.setId(id);
                                }
                            } else {
                                if (!title.contains("WhatsApp") && !title.equalsIgnoreCase("Checking for new messages")) {
                                    notificationSms.set_date(date);
                                    notificationSms.setNotification_date(statusBarNotification.getPostTime());
                                    notificationSms.set_message(text /*+ "\n" + notificationSms.get_message()*/);

                                    smsDao.updateInTx(notificationSms);
                                }
                            }
                        }
                        if (statusBarNotification.getNotification().category == null
                                || statusBarNotification.getNotification().category.equalsIgnoreCase(Notification.CATEGORY_CALL)) {

                            if (title.toLowerCase().trim().contains("miss")
                                    || title.toLowerCase().trim().contains("missed")) {
                                if (title.matches(".*\\d.*")) {
                                    // contains a number
                                    title = title.replaceAll("^([0-9]+)", "");
                                }
                                TableNotificationSms notificationSms = DBUtility.getNotificationDao().queryBuilder()
                                        .where(TableNotificationSmsDao.Properties.PackageName.eq(statusBarNotification.getPackageName()),
                                                TableNotificationSmsDao.Properties._contact_title.eq("Missed call"),
                                                TableNotificationSmsDao.Properties.Notification_type.eq(NotificationUtility.NOTIFICATION_TYPE_EVENT))
                                        .unique();
                                if (notificationSms == null) {
                                    if (!title.contains("WhatsApp")) {
                                        notificationSms = new TableNotificationSms();
                                        notificationSms.set_contact_title("Missed call");
                                        notificationSms.set_message(text);
                                        notificationSms.set_date(date);
                                        notificationSms.setNotification_date(statusBarNotification.getPostTime());
                                        notificationSms.setNotification_type(NotificationUtility.NOTIFICATION_TYPE_EVENT);
                                        notificationSms.setPackageName(strPackageName);
                                        notificationSms.setApp_icon(icon);
                                        notificationSms.setUser_icon(largeIcon);

                                        long id = smsDao.insertOrReplace(notificationSms);
                                        notificationSms.setId(id);
                                    }
                                } else {
                                    if (!title.contains("WhatsApp")) {
                                        notificationSms.set_date(date);
                                        notificationSms.setUser_icon(null);
                                        notificationSms.set_contact_title("Missed call");
                                        notificationSms.setNotification_date(statusBarNotification.getPostTime());
                                        if (!data.toString().equalsIgnoreCase("")) {
                                            notificationSms.set_message(data.toString());
                                        } else {
                                            notificationSms.set_message(text + "\n" + notificationSms.get_message());
                                        }

                                        smsDao.updateInTx(notificationSms);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                CoreApplication.getInstance().logException(e);
            }

        }
        // Google Calendar
        else if (statusBarNotification.getPackageName().equalsIgnoreCase(Constants.GOOGLE_CALENDAR_PACKAGES)) {
            try {
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
                }

            } catch (Exception e) {
                CoreApplication.getInstance().logException(e);
                e.printStackTrace();
            }
        }
        // Facebook
        else if (droidPrefs.isFacebookAllowed().get() && statusBarNotification.getPackageName().equalsIgnoreCase(Constants.FACEBOOK_PACKAGE)) {
            try {
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
            } catch (Exception e) {
                CoreApplication.getInstance().logException(e);
                e.printStackTrace();
            }
        }
        // Facebook Messenger
        else if (droidPrefs.isFacebooKMessangerAllowed().get() && statusBarNotification.getPackageName().equalsIgnoreCase(Constants.FACEBOOK_MESSENGER_PACKAGE)) {
            try {
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
                    } else {
                        notificationSms.setPackageName(strPackageName);
                        notificationSms.set_date(date);
                        notificationSms.setNotification_date(statusBarNotification.getPostTime());
                        notificationSms.set_message(strText + "\n" + notificationSms.get_message());
                        notificationSms.set_contact_title(strTitle);
                        smsDao.update(notificationSms);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                CoreApplication.getInstance().logException(e);
            }
        }
        // Facebook Lite Messenger
        else if (droidPrefs.isFacebooKMessangerLiteAllowed().get() && statusBarNotification.getPackageName().equalsIgnoreCase(Constants.FACEBOOK_LITE_PACKAGE)) {
            try {
                DaoSession daoSession = ((Launcher3App) CoreApplication.getInstance()).getDaoSession();
                TableNotificationSmsDao smsDao = daoSession.getTableNotificationSmsDao();
                TableNotificationSms notificationSms
                        = DBUtility.getNotificationDao().queryBuilder()
                        .where(TableNotificationSmsDao.Properties.PackageName.eq(strPackageName),
                                TableNotificationSmsDao.Properties._contact_title.eq(strTitle),
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
                } else {
                    notificationSms.setPackageName(strPackageName);
                    notificationSms.set_date(date);
                    notificationSms.setNotification_date(statusBarNotification.getPostTime());
                    if (data != null && !data.toString().equalsIgnoreCase("")) {
                        notificationSms.set_message(data.toString());
                    } else {
                        notificationSms.set_message(strText + "\n" + notificationSms.get_message());
                    }
                    notificationSms.set_contact_title(strTitle);
                    smsDao.update(notificationSms);
                    EventBus.getDefault().post(new NewNotificationEvent(notificationSms));
                }
                if (statusBarNotification.getNotification().category != null && statusBarNotification.getNotification().category.equalsIgnoreCase(Notification.CATEGORY_CALL)) {
                    EventBus.getDefault().post(new NotificationTrayEvent(true));
                }
            } catch (Exception e) {
                e.printStackTrace();
                CoreApplication.getInstance().logException(e);
            }
        }
        // HangOut Messenger used in Future Sprint.
        else if (droidPrefs.isHangOutAllowed().get() && statusBarNotification.getPackageName().equalsIgnoreCase(Constants.GOOGLE_HANGOUTS_PACKAGES)) {
            try {
                DaoSession daoSession = ((Launcher3App) CoreApplication.getInstance()).getDaoSession();
                TableNotificationSmsDao smsDao = daoSession.getTableNotificationSmsDao();
                if (!strTitle.trim().endsWith("new messages")) {
                    String groupname = "";
                    if (strTitle.contains(":")) {
                        String[] separated = strTitle.split(":");
                        strTitle = separated[0];
                        strText = separated[1] + ": " + strText;
                    }
                    if (statusBarNotification.getNotification().category != null
                            && !statusBarNotification.getNotification().category.equalsIgnoreCase(Notification.CATEGORY_CALL)) {
                        TableNotificationSms notificationSms
                                = DBUtility.getNotificationDao().queryBuilder()
                                .where(TableNotificationSmsDao.Properties.PackageName.eq(strPackageName),
                                        TableNotificationSmsDao.Properties._contact_title.eq(strTitle),
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
                        } else {
                            notificationSms.setPackageName(strPackageName);
                            notificationSms.set_date(date);
                            notificationSms.setNotification_date(statusBarNotification.getPostTime());
                            if (!notificationSms.get_message().split("\n")[0].equalsIgnoreCase(strText)) {
                                notificationSms.set_message(strText + "\n" + notificationSms.get_message());
                                notificationSms.set_contact_title(strTitle);
                                smsDao.update(notificationSms);
                                EventBus.getDefault().post(new NewNotificationEvent(notificationSms));
                            }
                        }
                    } else {
                        if (!strText.toString().equalsIgnoreCase("Incoming voice call")
                                && !strText.toString().equalsIgnoreCase("Incoming video call")) {

                            if (statusBarNotification.getNotification().tickerText.toString().equalsIgnoreCase("Missed call")
                                    && strText.toString().equalsIgnoreCase("Missed call")) {
                                strText = strTitle;
                                strTitle = "Missed Call";
                            } else {
                                if (strTitle.contains("missed calls")) {
                                    strTitle = "Missed Call";
                                }
                            }
                            TableNotificationSms notificationSms
                                    = DBUtility.getNotificationDao().queryBuilder()
                                    .where(TableNotificationSmsDao.Properties.PackageName.eq(strPackageName),
                                            TableNotificationSmsDao.Properties._contact_title.eq(strTitle),
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
                            } else {
                                notificationSms.set_date(date);
                                notificationSms.setNotification_date(statusBarNotification.getPostTime());
                                if (!notificationSms.get_message().split("\n")[0].equalsIgnoreCase(strText)) {
                                    notificationSms.set_message(strText);
                                    notificationSms.set_contact_title(strTitle);
                                }
                                if (!statusBarNotification.getNotification().tickerText.toString().equalsIgnoreCase("Missed call")
                                        && !strText.toString().equalsIgnoreCase("Missed call")) {
                                    smsDao.update(notificationSms);
                                    EventBus.getDefault().post(new NewNotificationEvent(notificationSms));
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (statusBarNotification.getNotification().category == null
                    || (!statusBarNotification.getNotification().category.equalsIgnoreCase(Notification.CATEGORY_CALL) &&
                    !statusBarNotification.getNotification().category.equalsIgnoreCase(Notification.CATEGORY_PROGRESS) &&
                    !statusBarNotification.getNotification().category.equalsIgnoreCase(Notification.CATEGORY_TRANSPORT) &&
                    !statusBarNotification.getNotification().category.equalsIgnoreCase(Notification.CATEGORY_SERVICE) &&
                    !statusBarNotification.getPackageName().equalsIgnoreCase("com.google.android.talk")
                    && !statusBarNotification.getPackageName().equalsIgnoreCase("com.google.android.apps.messaging"))) {
                try {
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
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }




    @Override
    public void onNotificationRemoved(StatusBarNotification notification) {
        super.onNotificationRemoved(notification);
        Tracer.d("Notification removed: " + getNotificationToString(notification));

        if (PackageUtil.isSiempoBlocker(notification.getId())) {
            launcherPrefs.isNotificationBlockerRunning().put(false);
        }
        if (!PackageUtil.isSiempoLauncher(this)
                && !launcherPrefs.isAppDefaultOrFront().get()) {
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
            CoreApplication.getInstance().logException(e);
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
            CoreApplication.getInstance().logException(e);
            return title;
        }
    }

}
