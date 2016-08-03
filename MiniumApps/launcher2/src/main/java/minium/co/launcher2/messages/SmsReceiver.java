package minium.co.launcher2.messages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

import org.androidannotations.annotations.EReceiver;

import java.util.Date;

import minium.co.core.log.Tracer;

/**
 * Created by Shahab on 7/29/2016.
 */
@EReceiver
public class SmsReceiver extends BroadcastReceiver {

    private String mAddress;
    private String mBody;
    private Date mDate;

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


        }
    }
}
