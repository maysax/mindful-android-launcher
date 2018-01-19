package co.siempo.phone.msg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

import co.siempo.phone.app.Constants;
import co.siempo.phone.app.Launcher3App;
import co.siempo.phone.app.Launcher3Prefs_;
import co.siempo.phone.db.DBUtility;
import co.siempo.phone.db.DaoSession;
import co.siempo.phone.db.TableNotificationSms;
import co.siempo.phone.db.TableNotificationSmsDao;
import co.siempo.phone.event.NewNotificationEvent;
import co.siempo.phone.event.TopBarUpdateEvent;
import co.siempo.phone.notification.NotificationUtility;
import co.siempo.phone.util.PackageUtil;
import de.greenrobot.event.EventBus;
import minium.co.core.app.CoreApplication;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.log.Tracer;

/**
 * Created by Shahab on 7/29/2016.
 */
@EReceiver
public class SmsReceiver extends BroadcastReceiver {

    public static final Uri RECEIVED_MESSAGE_CONTENT_PROVIDER = Uri.parse("content://sms/inbox");
    @Pref
    DroidPrefs_ prefs;
    @Pref
    Launcher3Prefs_ launcherPrefs;
    TableNotificationSmsDao smsDao;
    ArrayList<String> disableNotificationApps = new ArrayList<>();
    ArrayList<String> blockedApps = new ArrayList<>();
    private String mAddress;
    private String mBody;
    private Date mDate;

    @Override
    public void onReceive(Context context, Intent intent) {

        Tracer.d("Messages: onReceive in Launcher3");
        if (intent != null && intent.getAction() != null && intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Tracer.d("Notification posted: " + bundle.toString());
                Object messages[] = (Object[]) bundle.get("pdus");
                SmsMessage smsMessage[] = new SmsMessage[messages.length];

                for (int n = 0; n < messages.length; n++) {
                    smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
                }

                SmsMessage sms = smsMessage[0];
                if (smsMessage.length == 1 || sms.isReplace()) {
                    mBody = sms.getDisplayMessageBody();
                } else {
                    StringBuilder bodyText = new StringBuilder();
                    for (SmsMessage message : smsMessage) {
                        bodyText.append(message.getMessageBody());
                    }
                    mBody = bodyText.toString();
                }

                mAddress = sms.getDisplayOriginatingAddress(); // sms..getOriginatingAddress();
                mDate = new Date(sms.getTimestampMillis());

                if (launcherPrefs.isAppDefaultOrFront().get()) {
                    SharedPreferences prefs = context.getSharedPreferences("Launcher3Prefs", 0);
                    String disable_AppList = prefs.getString(Constants.DISABLE_APPLIST, "");
                    if (!TextUtils.isEmpty(disable_AppList)) {
                        Type type = new TypeToken<ArrayList<String>>() {
                        }.getType();
                        disableNotificationApps = new ArrayList<>();
                        disableNotificationApps = new Gson().fromJson(disable_AppList, type);
                        SharedPreferences sharedPreferences = context.getSharedPreferences("Launcher3Prefs", 0);

                        String block_AppList = sharedPreferences.getString(Constants.BLOCKED_APPLIST, "");
                        if (!TextUtils.isEmpty(block_AppList)) {
                            Type blockType = new TypeToken<ArrayList<String>>() {
                            }.getType();
                            blockedApps = new Gson().fromJson(block_AppList, blockType);
                        }
                        boolean isShowNotification = true;
                        String messagingAppPackage = "com.google.android.apps.messaging";
                        if (null != blockedApps && blockedApps.size() > 0) {
                            for (String blockedApp : blockedApps) {
                                if (blockedApp.equalsIgnoreCase(messagingAppPackage)) {
                                    isShowNotification = false;
                                }
                            }

                        }

                        if (disableNotificationApps.contains(messagingAppPackage) && isShowNotification) {
                            saveMessage(mAddress, mBody, mDate, context);
                        }
                    }
                }


                //new ReceivedSMSItem(mAddress, mDate, mBody, 0).save();
                EventBus.getDefault().post(new TopBarUpdateEvent());

                if (launcherPrefs.isPauseActive().get() || launcherPrefs.isTempoActive().get()) {
                    abortBroadcast();
                }
            }
        }
    }

    private void saveMessage(String address, String body, Date date, Context context) {
//        DaoSession daoSession = ((Launcher3App) CoreApplication.getInstance()).getDaoSession();
//        smsDao = daoSession.getTableNotificationSmsDao();
//
//        TableNotificationSms sms = new TableNotificationSms();
//        sms.set_contact_title(address);
//        sms.set_message(body);
//        sms.setPackageName("com.google.android.apps.messaging");
//        sms.setNotification_date(System.currentTimeMillis());
//        sms.set_date(date);
//        sms.setNotification_type(NotificationUtility.NOTIFICATION_TYPE_SMS);
//        long id = smsDao.insert(sms);
//        sms.setId(id);
//        EventBus.getDefault().post(new NewNotificationEvent(sms));


        try {
            DaoSession daoSession = ((Launcher3App) CoreApplication.getInstance()).getDaoSession();
            TableNotificationSmsDao smsDao = daoSession.getTableNotificationSmsDao();
            TableNotificationSms notificationSms = DBUtility.getNotificationDao().queryBuilder()
                    .where(TableNotificationSmsDao.Properties._contact_title.eq(address),
                            TableNotificationSmsDao.Properties.Notification_type.eq(NotificationUtility.NOTIFICATION_TYPE_EVENT))
                    .unique();
            if (notificationSms == null) {
                notificationSms = new TableNotificationSms();
                notificationSms.set_contact_title(address);
                notificationSms.set_message(body);
                notificationSms.set_date(date);
                notificationSms.setNotification_date(System.currentTimeMillis());
                notificationSms.setNotification_type(NotificationUtility.NOTIFICATION_TYPE_SMS);
                notificationSms.setPackageName(Telephony.Sms.getDefaultSmsPackage(context));
//                notificationSms.setApp_icon(icon);
//                notificationSms.setUser_icon(largeIcon);
                // notificationSms.setNotification_id(statusBarNotification.getId());
                long id = smsDao.insert(notificationSms);
                notificationSms.setId(id);
                PackageUtil.recreateNotification(notificationSms, context, prefs.tempoType().get(), prefs.tempoSoundProfile().get(), launcherPrefs.isAllowNotificationOnLockScreen().get(), 1234);
                EventBus.getDefault().post(new NewNotificationEvent(notificationSms));
            } else {
                notificationSms.set_date(date);
                notificationSms.setNotification_date(System.currentTimeMillis());
                notificationSms.set_contact_title(address);
                notificationSms.set_message(notificationSms.get_message() + "\n" + body);
                smsDao.update(notificationSms);
                PackageUtil.recreateNotification(notificationSms, context, prefs.tempoType().get(), prefs.tempoSoundProfile().get(), launcherPrefs.isAllowNotificationOnLockScreen().get(), 1234);
                EventBus.getDefault().post(new NewNotificationEvent(notificationSms));
            }
        } catch (Exception e) {
            e.printStackTrace();
            CoreApplication.getInstance().logException(e);
        }
    }


}
