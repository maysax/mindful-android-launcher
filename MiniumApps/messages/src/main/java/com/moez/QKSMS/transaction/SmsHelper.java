package com.moez.QKSMS.transaction;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Patterns;

import com.google.android.mms.pdu_alt.CharacterSets;
import com.google.android.mms.pdu_alt.EncodedStringValue;
import com.google.android.mms.pdu_alt.PduPersister;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.moez.QKSMS.MmsConfig;
import com.moez.QKSMS.data.Message;
import com.moez.QKSMS.ui.messagelist.MessageColumns;

/**
 * Created by shahab on 3/25/16.
 */
public class SmsHelper {

    public static final Uri SMS_CONTENT_PROVIDER = Uri.parse("content://sms/");
    public static final Uri MMS_CONTENT_PROVIDER = Uri.parse("content://mms/");
    public static final Uri SENT_MESSAGE_CONTENT_PROVIDER = Uri.parse("content://sms/sent");
    public static final Uri DRAFTS_CONTENT_PROVIDER = Uri.parse("content://sms/draft");
    public static final Uri PENDING_MESSAGE_CONTENT_PROVIDER = Uri.parse("content://sms/outbox");
    public static final Uri RECEIVED_MESSAGE_CONTENT_PROVIDER = Uri.parse("content://sms/inbox");
    public static final Uri CONVERSATIONS_CONTENT_PROVIDER = Uri.parse("content://mms-sms/conversations?simple=true");
    public static final Uri ADDRESSES_CONTENT_PROVIDER = Uri.parse("content://mms-sms/canonical-addresses");

    public static final byte UNREAD = 0;
    public static final byte READ = 1;

    // Attachment types
    public static final int TEXT = 0;
    public static final int IMAGE = 1;
    public static final int VIDEO = 2;
    public static final int AUDIO = 3;
    public static final int SLIDESHOW = 4;

    // Columns for SMS content providers
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_THREAD_ID = "thread_id";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_RECIPIENT = "recipient_ids";
    public static final String COLUMN_PERSON = "person";
    public static final String COLUMN_SNIPPET = "snippet";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_DATE_NORMALIZED = "normalized_date";
    public static final String COLUMN_DATE_SENT = "date_sent";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_ERROR = "error";
    public static final String COLUMN_READ = "read";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_MMS = "ct_t";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_SUB = "sub";
    public static final String COLUMN_MSG_BOX = "msg_box";
    public static final String COLUMN_SUBJECT = "subject";
    public static final String COLUMN_BODY = "body";
    public static final String COLUMN_SEEN = "seen";

    public static final String UNREAD_SELECTION = COLUMN_READ + " = " + UNREAD;
    public static final String UNSEEN_SELECTION = COLUMN_SEEN + " = " + UNREAD;
    public static final String FAILED_SELECTION = COLUMN_TYPE + " = " + Message.FAILED;

    /**
     * Message type: all messages.
     */
    public static final int MESSAGE_TYPE_ALL = 0;

    /**
     * Message type: inbox.
     */
    public static final int MESSAGE_TYPE_INBOX = 1;

    /**
     * Message type: sent messages.
     */
    public static final int MESSAGE_TYPE_SENT = 2;

    /**
     * Message type: drafts.
     */
    public static final int MESSAGE_TYPE_DRAFT = 3;

    /**
     * Message type: outbox.
     */
    public static final int MESSAGE_TYPE_OUTBOX = 4;

    /**
     * Message type: failed outgoing message.
     */
    public static final int MESSAGE_TYPE_FAILED = 5;

    /**
     * Message type: queued to send later.
     */
    public static final int MESSAGE_TYPE_QUEUED = 6;

    private static String[] sNoSubjectStrings;

    /**
     * cleanseMmsSubject will take a subject that's says, "<Subject: no subject>", and return
     * a null string. Otherwise it will return the original subject string.
     *
     * @param context a regular context so the function can grab string resources
     * @param subject the raw subject
     * @return
     */
    public static String cleanseMmsSubject(Context context, String subject) {
        /*SKIP
        if (TextUtils.isEmpty(subject)) {
            return subject;
        }
        if (sNoSubjectStrings == null) {
            sNoSubjectStrings = context.getResources().getStringArray(R.array.empty_subject_strings);
        }

        final int len = sNoSubjectStrings.length;
        for (int i = 0; i < len; i++) {
            if (subject.equalsIgnoreCase(sNoSubjectStrings[i])) {
                return null;
            }
        }*/
        return subject;
    }

    public static String extractEncStrFromCursor(Cursor cursor,
                                                 int columnRawBytes, int columnCharset) {
        String rawBytes = cursor.getString(columnRawBytes);
        int charset = cursor.getInt(columnCharset);

        if (TextUtils.isEmpty(rawBytes)) {
            return "";
        } else if (charset == CharacterSets.ANY_CHARSET) {
            return rawBytes;
        } else {
            return new EncodedStringValue(charset, PduPersister.getBytes(rawBytes)).getString();
        }
    }

    /**
     * Is the specified address an email address?
     *
     * @param address the input address to test
     * @return true if address is an email address; false otherwise.
     * @hide
     */
    public static boolean isEmailAddress(String address) {
        if (TextUtils.isEmpty(address)) {
            return false;
        }

        String s = extractAddrSpec(address);
        Matcher match = Patterns.EMAIL_ADDRESS.matcher(s);
        return match.matches();
    }

    /**
     * Regex pattern for names and email addresses.
     * <ul>
     * <li><em>mailbox</em> = {@code name-addr}</li>
     * <li><em>name-addr</em> = {@code [display-name] angle-addr}</li>
     * <li><em>angle-addr</em> = {@code [CFWS] "<" addr-spec ">" [CFWS]}</li>
     * </ul>
     *
     * @hide
     */
    public static final Pattern NAME_ADDR_EMAIL_PATTERN =
            Pattern.compile("\\s*(\"[^\"]*\"|[^<>\"]+)\\s*<([^<>]+)>\\s*");


    /**
     * Helper method to extract email address from address string.
     *
     * @hide
     */
    public static String extractAddrSpec(String address) {
        Matcher match = NAME_ADDR_EMAIL_PATTERN.matcher(address);

        if (match.matches()) {
            return match.group(2);
        }
        return address;
    }

    // An alias (or commonly called "nickname") is:
    // Nickname must begin with a letter.
    // Only letters a-z, numbers 0-9, or . are allowed in Nickname field.
    public static boolean isAlias(String string) {
        if (!MmsConfig.isAliasEnabled()) {
            return false;
        }

        int len = string == null ? 0 : string.length();

        if (len < MmsConfig.getAliasMinChars() || len > MmsConfig.getAliasMaxChars()) {
            return false;
        }

        if (!Character.isLetter(string.charAt(0))) {    // Nickname begins with a letter
            return false;
        }
        for (int i = 1; i < len; i++) {
            char c = string.charAt(i);
            if (!(Character.isLetterOrDigit(c) || c == '.')) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the position of the message in the cursor.
     *
     * @param cursor
     * @param messageType
     * @param messageId
     * @param map
     * @return
     */
    public static int getPositionForMessageId(Cursor cursor, String messageType, long messageId, MessageColumns.ColumnsMap map) {

        // Modified binary search on the cursor to find the position of the message in the cursor.
        // It's modified because, although the SMS and MMS are generally ordered in terms of their
        // ID, they have different IDs. So, we might have a list of IDs like:
        //
        // [ 4444, 4447, 4449, 4448, 312, 315, 4451 ]
        //
        // where the 44xx IDs are for SMS messages, and the 31x IDs are for MMS messages. The
        // solution is to do a linear scan if we reach a point in the list where the ID doesn't
        // match what we're looking for.

        // Lower and upper bounds for doing the search
        int min = 0;
        int max = cursor.getCount() - 1;

        while (min <= max) {
            int mid = min / 2 + max / 2 + (min & max & 1);

            cursor.moveToPosition(mid);
            long candidateId = cursor.getLong(map.mColumnMsgId);
            String candidateType = cursor.getString(map.mColumnMsgType);

            if (messageType.equals(candidateType)) {
                if (messageId < candidateId) {
                    max = mid - 1;
                } else if (messageId > candidateId) {
                    min = mid + 1;
                } else {
                    return mid;
                }

            } else {
                // This message is the wrong type, so we have to do a linear search until we find a
                // message that is the right type so we can orient ourselves.

                // First, look forward. Stop when we move past max, or reach the end of the cursor.
                boolean success = false;
                while (cursor.getPosition() <= max && cursor.moveToNext()) {
                    candidateType = cursor.getString(map.mColumnMsgType);
                    if (candidateType.equals(messageType)) {
                        success = true;
                        break;
                    }
                }

                if (!success) {
                    // We didn't find any messages of the right type by looking forward, so try
                    // looking backwards.
                    cursor.moveToPosition(mid);
                    while (cursor.getPosition() >= min && cursor.moveToPrevious()) {
                        candidateType = cursor.getString(map.mColumnMsgType);
                        if (candidateType.equals(messageType)) {
                            success = true;
                            break;
                        }
                    }
                }

                if (!success) {
                    // There is no message with that ID of the correct type!
                    return -1;
                }

                // In this case, we've found a message of the correct type! Now to do the binary
                // search stuff.
                candidateId = cursor.getLong(map.mColumnMsgId);
                int pos = cursor.getPosition();
                if (messageId < candidateId) {
                    // The new upper bound is the minimum of where we started and where we ended
                    // up, subtract one.
                    max = (mid < pos ? mid : pos) - 1;
                } else if (messageId > candidateId) {
                    // Same as above but in reverse.
                    min = (mid > pos ? mid : pos) + 1;
                } else {
                    return pos;
                }
            }
        }

        // This is the case where we've minimized our bounds until they're the same, and we haven't
        // found anything yet---this means that the item doesn't exist, so return -1.
        return -1;
    }

    /**
     * Returns true iff the folder (message type) identifies an
     * outgoing message.
     *
     * @hide
     */
    public static boolean isOutgoingFolder(int messageType) {
        return (messageType == MESSAGE_TYPE_FAILED)
                || (messageType == MESSAGE_TYPE_OUTBOX)
                || (messageType == MESSAGE_TYPE_SENT)
                || (messageType == MESSAGE_TYPE_QUEUED);
    }

}
