package com.moez.QKSMS.common.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SqliteWrapper;
import android.net.Uri;
import android.provider.Telephony;
import android.text.TextUtils;

import com.google.android.mms.pdu_alt.EncodedStringValue;
import com.google.android.mms.pdu_alt.PduHeaders;
import com.google.android.mms.pdu_alt.PduPersister;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import minium.co.messages.R;
import minium.co.messages.app.MessagesApp_;

public class AddressUtils {
    private static PhoneNumberUtil mPhoneNumberUtil;

    private AddressUtils() {
        // Forbidden being instantiated.
    }

    public static String getFrom(Context context, Uri uri) {
        String msgId = uri.getLastPathSegment();
        Uri.Builder builder = Telephony.Mms.CONTENT_URI.buildUpon();

        builder.appendPath(msgId).appendPath("addr");

        Cursor cursor = SqliteWrapper.query(context, context.getContentResolver(),
                builder.build(), new String[]{Telephony.Mms.Addr.ADDRESS, Telephony.Mms.Addr.CHARSET},
                Telephony.Mms.Addr.TYPE + "=" + PduHeaders.FROM, null, null);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    String from = cursor.getString(0);

                    if (!TextUtils.isEmpty(from)) {
                        byte[] bytes = PduPersister.getBytes(from);
                        int charset = cursor.getInt(1);
                        return new EncodedStringValue(charset, bytes).getString();
                    }
                }
            } finally {
                cursor.close();
            }
        }
        return context.getString(R.string.hidden_sender_address);
    }

    /**
     * isPossiblePhoneNumberCanDoFileAccess does a more accurate test if the input is a
     * phone number, but it can do file access to load country prefixes and other info, so
     * it's not safe to call from the UI thread.
     *
     * @param query the phone number to test
     * @return true if query looks like a valid phone number
     */
    public static boolean isPossiblePhoneNumberCanDoFileAccess(String query) {
        String currentCountry = MessagesApp_.getInstance().getCurrentCountryIso().toUpperCase();
        if (mPhoneNumberUtil == null) {
            mPhoneNumberUtil = PhoneNumberUtil.getInstance();
        }
        return mPhoneNumberUtil.isPossibleNumber(query, currentCountry);
    }
}
