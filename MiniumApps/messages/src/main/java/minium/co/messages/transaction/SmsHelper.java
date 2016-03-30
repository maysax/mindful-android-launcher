package minium.co.messages.transaction;

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

import minium.co.messages.MmsConfig;

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
}
