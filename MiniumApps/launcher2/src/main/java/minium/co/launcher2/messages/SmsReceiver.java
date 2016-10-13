package minium.co.launcher2.messages;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsMessage;

import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.Date;

import minium.co.core.app.DroidPrefs_;
import minium.co.core.log.Tracer;
import minium.co.launcher2.model.ReceivedSMSItem;
import minium.co.launcher2.notification.DisplayAlertActivity_;
import minium.co.launcher2.notificationscheduler.NotificationScheduleReceiver_;

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

    public static final Uri RECEIVED_MESSAGE_CONTENT_PROVIDER = Uri.parse("content://sms/inbox");

    @Override
    public void onReceive(Context context, Intent intent) {
        Tracer.d("Messages: onReceive in Launcher2");
        abortBroadcast();

        if (intent.getExtras() != null) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            SmsMessage[] messages = new SmsMessage[pdus.length];
            for (int i = 0; i < messages.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }

            SmsMessage sms = messages[0];
            if (messages.length == 1 || sms.isReplace()) {
                mBody = sms.getDisplayMessageBody();
            } else {
                StringBuilder bodyText = new StringBuilder();
                for (SmsMessage message : messages) {
                    bodyText.append(message.getMessageBody());
                }
                mBody = bodyText.toString();
            }

            mAddress = sms.getDisplayOriginatingAddress();
            mDate = new Date(sms.getTimestampMillis());


            new ReceivedSMSItem(mAddress, mDate, mBody, 0).save();

            if (prefs.isFlowRunning().get() || (prefs.isNotificationSchedulerEnabled().get() && prefs.notificationSchedulerSupressSMS().get())) {
                // Suppress notification
            } else {
                DisplayAlertActivity_.intent(context).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
            }
            /*addMessageToInbox(context, mAddress, mBody, mDate.getTime());

            if (!prefs.isFlowRunning().get() && !prefs.isNotificationSchedulerEnabled().get()) {
                context.sendBroadcast(new Intent(context, NotificationScheduleReceiver_.class));
            }*/
        }
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
