package co.minium.launcher3.msg;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;

import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.Date;

import co.minium.launcher3.app.Launcher3App;
import co.minium.launcher3.app.Launcher3Prefs_;
import co.minium.launcher3.call.DaoSession;
import co.minium.launcher3.db.TableNotificationSms;
import co.minium.launcher3.db.TableNotificationSmsDao;
import co.minium.launcher3.notification.NotificationUtility;
import de.greenrobot.event.EventBus;
import minium.co.core.app.CoreApplication;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.log.Tracer;

/**
 * Created by Shahab on 7/29/2016.
 */
@EReceiver
public class SmsReceiver extends BroadcastReceiver {

    private String mAddress;
    private String mBody;
    private Date mDate;

    @Pref
    DroidPrefs_ prefs;

    @Pref
    Launcher3Prefs_ launcherPrefs;

    TableNotificationSmsDao smsDao;

    public static final Uri RECEIVED_MESSAGE_CONTENT_PROVIDER = Uri.parse("content://sms/inbox");

    @Override
    public void onReceive(Context context, Intent intent) {
        Tracer.d("Messages: onReceive in Launcher3");

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
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

                saveMessage(mAddress, mBody, mDate);
                //new ReceivedSMSItem(mAddress, mDate, mBody, 0).save();
                EventBus.getDefault().post(new SmsEvent(SmsEventType.RECEIVED));

                if (launcherPrefs.isPauseActive().get()) {
                    abortBroadcast();
                }

                if (prefs.isFlowRunning().get() || (prefs.isNotificationSchedulerEnabled().get() && prefs.notificationSchedulerSupressSMS().get())) {
                    // Suppress notification
                } else {
                    //DisplayAlertActivity_.intent(context).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
                }
                /*addMessageToInbox(context, mAddress, mBody, mDate.getTime());

                if (!prefs.isFlowRunning().get() && !prefs.isNotificationSchedulerEnabled().get()) {
                    context.sendBroadcast(new Intent(context, NotificationScheduleReceiver_.class));
                }*/
            }
        }
    }

    private void saveMessage(String address, String body, Date date) {
        DaoSession daoSession = ((Launcher3App) CoreApplication.getInstance()).getDaoSession();
        smsDao = daoSession.getTableNotificationSmsDao();

        TableNotificationSms sms = new TableNotificationSms();
        sms.set_contact_title(address);
        sms.set_message(body);
        sms.set_date(date);
        sms.setNotification_type(NotificationUtility.NOTIFICATION_TYPE_SMS);
        smsDao.insert(sms);
    }

    /**
     * Add incoming SMS to inbox
     *
     * @param context
     * @param address Address of sender
     * @param body    Body of incoming SMS message
     * @param time    Time that incoming SMS message was sent at
     */
    public static Uri addMessageToInbox(Context context, String address, String body, long time) {

        ContentResolver contentResolver = context.getContentResolver();
        ContentValues cv = new ContentValues();

        cv.put("address", address);
        cv.put("body", body);
        cv.put("date_sent", time);

        return contentResolver.insert(RECEIVED_MESSAGE_CONTENT_PROVIDER, cv);
    }
}
