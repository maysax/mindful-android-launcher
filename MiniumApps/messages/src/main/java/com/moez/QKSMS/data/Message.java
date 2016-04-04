package com.moez.QKSMS.data;

import android.net.Uri;

/**
 * Created by Shahab on 3/31/2016.
 */
public class Message {

    public static final int RECEIVED = 1;
    public static final int SENT = 2;
    public static final int DRAFT = 3;
    public static final int SENDING = 4;
    public static final int FAILED = 5;

    public static final Uri SMS_CONTENT_PROVIDER = Uri.parse("content://sms/");
    public static final Uri MMS_SMS_CONTENT_PROVIDER = Uri.parse("content://mms-sms/conversations/");
    public static final Uri SENT_MESSAGE_CONTENT_PROVIDER = Uri.parse("content://sms/sent");
}
