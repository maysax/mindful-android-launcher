package com.moez.QKSMS.ui.messagelist;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.text.TextUtils;

import com.google.android.mms.MmsException;
import com.google.android.mms.pdu_alt.EncodedStringValue;
import com.google.android.mms.pdu_alt.PduHeaders;
import com.google.android.mms.pdu_alt.PduPersister;

import java.util.regex.Pattern;

import minium.co.messages.R;
import com.moez.QKSMS.common.formatter.FormatterFactory;
import com.moez.QKSMS.common.utils.AddressUtils;
import com.moez.QKSMS.common.utils.DateFormatter;
import com.moez.QKSMS.data.Contact;
import com.moez.QKSMS.transaction.SmsHelper;

/**
 * Mostly immutable model for an SMS/MMS message.
 *
 * <p>The only mutable field is the cached formatted message member,
 * the formatting of which is done outside this model in MessageListItem.
 *
 * Copied from QKSMS
 * Created by Shahab on 3/31/2016.
 */
public class MessageItem {

    public enum DeliveryStatus  { NONE, INFO, FAILED, PENDING, RECEIVED }

    public static int ATTACHMENT_TYPE_NOT_LOADED = -1;

    final Context mContext;
    public final String mType;
    public final long mMsgId;
    public final int mBoxId;

    public String mDeliveryStatusString;
    public DeliveryStatus mDeliveryStatus;
    public String mReadReportString;
    public boolean mReadReport;
    public boolean mLocked;            // locked to prevent auto-deletion

    public long mDate;
    public String mTimestamp;
    public String mAddress;
    public String mContact;
    public String mBody; // Body of SMS, first text of MMS.
    public String mTextContentType; // ContentType of text of MMS.
    public Pattern mHighlight; // portion of message to highlight (from search)

    // The only non-immutable field.  Not synchronized, as access will
    // only be from the main GUI thread.  Worst case if accessed from
    // another thread is it'll return null and be set again from that
    // thread.
    public CharSequence mCachedFormattedMessage;

    // The last message is cached above in mCachedFormattedMessage. In the latest design, we
    // show "Sending..." in place of the timestamp when a message is being sent. mLastSendingState
    // is used to keep track of the last sending state so that if the current sending state is
    // different, we can clear the message cache so it will get rebuilt and recached.
    public boolean mLastSendingState;

    // Fields for MMS only.
    public Uri mMessageUri;
    public int mMessageType;
    public int mAttachmentType;
    public String mSubject;
    public int mMessageSize;
    public int mErrorType;
    public int mErrorCode;
    public int mMmsStatus;
    public MessageColumns.ColumnsMap mColumnsMap;

    @SuppressLint("NewApi")
    public MessageItem(Context context, String type, final Cursor cursor,
                       final MessageColumns.ColumnsMap columnsMap, Pattern highlight,
                       boolean canBlock) throws MmsException {
        mContext = context;
        mMsgId = cursor.getLong(columnsMap.mColumnMsgId);
        mHighlight = highlight;
        mType = type;
        mColumnsMap = columnsMap;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        if ("sms".equals(type)) {
            mReadReport = false; // No read reports in sms

            long status = cursor.getLong(columnsMap.mColumnSmsStatus);
            if (status == Telephony.Sms.STATUS_NONE) {
                // No delivery report requested
                mDeliveryStatus = DeliveryStatus.NONE;
            } else if (status >= Telephony.Sms.STATUS_FAILED) {
                // Failure
                mDeliveryStatus = DeliveryStatus.FAILED;
            } else if (status >= Telephony.Sms.STATUS_PENDING) {
                // Pending
                mDeliveryStatus = DeliveryStatus.PENDING;
            } else {
                // Success
                mDeliveryStatus = DeliveryStatus.RECEIVED;
            }

            mMessageUri = ContentUris.withAppendedId(Telephony.Sms.CONTENT_URI, mMsgId);
            // Set contact and message body
            mBoxId = cursor.getInt(columnsMap.mColumnSmsType);
            mAddress = cursor.getString(columnsMap.mColumnSmsAddress);
            if (SmsHelper.isOutgoingFolder(mBoxId)) {
                String meString = context.getString(
                        R.string.messagelist_sender_self);

                mContact = meString;
            } else {
                // For incoming messages, the ADDRESS field contains the sender.
                mContact = Contact.get(mAddress, canBlock).getName();
            }
            mBody = cursor.getString(columnsMap.mColumnSmsBody);
            mBody = FormatterFactory.format(mBody);

            // Unless the message is currently in the progress of being sent, it gets a time stamp.
            if (!isOutgoingMessage()) {
                // Set "received" or "sent" time stamp
                boolean sent = /* SKIP prefs.getBoolean(QKPreference.SENT_TIMESTAMPS.getKey(), false) */ false && !isMe();
                mDate = cursor.getLong(sent ? columnsMap.mColumnSmsDateSent : columnsMap.mColumnSmsDate);
                mTimestamp = DateFormatter.getMessageTimestamp(context, mDate);
            }

            mLocked = cursor.getInt(columnsMap.mColumnSmsLocked) != 0;
            mErrorCode = cursor.getInt(columnsMap.mColumnSmsErrorCode);
        } else if ("mms".equals(type)) {
            mMessageUri = ContentUris.withAppendedId(Telephony.Mms.CONTENT_URI, mMsgId);
            mBoxId = cursor.getInt(columnsMap.mColumnMmsMessageBox);
            // If we can block, get the address immediately from the "addr" table.
            if (canBlock) {
                mAddress = AddressUtils.getFrom(mContext, mMessageUri);
            }
            mMessageType = cursor.getInt(columnsMap.mColumnMmsMessageType);
            mErrorType = cursor.getInt(columnsMap.mColumnMmsErrorType);
            String subject = cursor.getString(columnsMap.mColumnMmsSubject);
            if (!TextUtils.isEmpty(subject)) {
                EncodedStringValue v = new EncodedStringValue(
                        cursor.getInt(columnsMap.mColumnMmsSubjectCharset),
                        PduPersister.getBytes(subject));
                mSubject = SmsHelper.cleanseMmsSubject(context, v.getString());
            }
            mLocked = cursor.getInt(columnsMap.mColumnMmsLocked) != 0;
            // SKIP mSlideshow = null;
            mDeliveryStatusString = cursor.getString(columnsMap.mColumnMmsDeliveryReport);
            mReadReportString = cursor.getString(columnsMap.mColumnMmsReadReport);
            mBody = null;
            mMessageSize = 0;
            mTextContentType = null;
            // Initialize the time stamp to "" instead of null
            mTimestamp = "";
            mMmsStatus = cursor.getInt(columnsMap.mColumnMmsStatus);
            mAttachmentType = cursor.getInt(columnsMap.mColumnMmsTextOnly) != 0 ?
                    SmsHelper.TEXT : ATTACHMENT_TYPE_NOT_LOADED;

            // Start an async load of the pdu. If the pdu is already loaded, the callback
            // will get called immediately
            boolean loadSlideshow = mMessageType != PduHeaders.MESSAGE_TYPE_NOTIFICATION_IND;

            /* SKIP mItemLoadedFuture = QKSMSApp.getApplication().getPduLoaderManager()
                    .getPdu(mMessageUri, loadSlideshow,
                            new PduLoadedMessageItemCallback()); */

        } else {
            throw new MmsException("Unknown type of the message: " + type);
        }
    }

    public int getBoxId() {
        return mBoxId;
    }

    public boolean isMe() {
        // Logic matches MessageListAdapter.getItemViewType which is used to decide which
        // type of MessageListItem to create: a left or right justified item depending on whether
        // the message is incoming or outgoing.
        boolean isIncomingMms = isMms()
                && (mBoxId == Telephony.Mms.MESSAGE_BOX_INBOX
                || mBoxId == Telephony.Mms.MESSAGE_BOX_ALL);
        boolean isIncomingSms = isSms()
                && (mBoxId == Telephony.Sms.MESSAGE_TYPE_INBOX
                || mBoxId == Telephony.Sms.MESSAGE_TYPE_ALL);
        return !(isIncomingMms || isIncomingSms);
    }

    public boolean isMms() {
        return mType.equals("mms");
    }

    public boolean isSms() {
        return mType.equals("sms");
    }

    public boolean isOutgoingMessage() {
        boolean isOutgoingMms = isMms() && (mBoxId == Telephony.Mms.MESSAGE_BOX_OUTBOX);
        boolean isOutgoingSms = isSms()
                && ((mBoxId == Telephony.Sms.MESSAGE_TYPE_FAILED)
                || (mBoxId == Telephony.Sms.MESSAGE_TYPE_OUTBOX)
                || (mBoxId == Telephony.Sms.MESSAGE_TYPE_QUEUED));
        return isOutgoingMms || isOutgoingSms;
    }

    public boolean isSending() {
        return !isFailedMessage() && isOutgoingMessage();
    }

    public boolean isFailedMessage() {
        boolean isFailedMms = isMms()
                && (mErrorType >= Telephony.MmsSms.ERR_TYPE_GENERIC_PERMANENT);
        boolean isFailedSms = isSms()
                && (mBoxId == Telephony.Sms.MESSAGE_TYPE_FAILED);
        return isFailedMms || isFailedSms;
    }
}
