package com.moez.QKSMS.ui.messagelist;

import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Handler;
import android.provider.Telephony;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.mms.pdu_alt.PduHeaders;
import com.moez.QKSMS.common.utils.CursorUtils;
import com.moez.QKSMS.common.utils.LinkifyUtils;
import com.moez.QKSMS.common.utils.MessageUtils;
import com.moez.QKSMS.data.Contact;
import com.moez.QKSMS.ui.base.RecyclerCursorAdapter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import minium.co.core.log.LogConfig;
import minium.co.core.ui.CoreActivity;
import minium.co.messages.R;

public class MessageListAdapter extends RecyclerCursorAdapter<MessageListViewHolder, MessageItem>  {

    private final String TRACE_TAG = LogConfig.TRACE_TAG + "MessageListAdapter";

    public static final int INCOMING_ITEM = 0;
    public static final int OUTGOING_ITEM = 1;

    private static final Pattern urlPattern = Pattern.compile(
            "\\b(https?:\\/\\/\\S+(?:png|jpe?g|gif)\\S*)\\b",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    private MessageItemCache mMessageItemCache;
    private MessageColumns.ColumnsMap mColumnsMap;

    // Configuration options.
    private long mThreadId = -1;
    private long mRowId = -1;
    private Pattern mSearchHighlighter = null;
    private boolean mIsGroupConversation = false;
    private Handler mMessageListItemHandler = null; // TODO this isn't quite the same as the others
    private String mSelection = null;

    public MessageListAdapter(CoreActivity context) {
        super(context);
    }

    protected MessageItem getItem(int position) {
        mCursor.moveToPosition(position);

        String type = mCursor.getString(mColumnsMap.mColumnMsgType);
        long msgId = mCursor.getLong(mColumnsMap.mColumnMsgId);

        return mMessageItemCache.get(type, msgId, mCursor);
    }

    @Override
    public int getItemViewType(int position) {
        // This method shouldn't be called if our cursor is null, since the framework should know
        // that there aren't any items to look at in that case
        MessageItem item = getItem(position);
        int boxId = item.getBoxId();

        if (item.isSms()) {
            if (boxId == Telephony.TextBasedSmsColumns.MESSAGE_TYPE_INBOX || boxId == Telephony.TextBasedSmsColumns.MESSAGE_TYPE_ALL) {
                return INCOMING_ITEM;
            } else {
                return OUTGOING_ITEM;
            }
        } else {
            if (boxId == Telephony.Mms.MESSAGE_BOX_ALL || boxId == Telephony.Mms.MESSAGE_BOX_INBOX) {
                return INCOMING_ITEM;
            } else {
                return OUTGOING_ITEM;
            }
        }
    }

    @Override
    public MessageListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        int resource;
        boolean sent;

        if (viewType == INCOMING_ITEM) {
            resource = R.layout.list_item_message_in;
            sent = false;
        } else {
            resource = R.layout.list_item_message_out;
            sent = true;
        }

        View view = inflater.inflate(resource, parent, false);
        return new MessageListViewHolder(mContext, view);
    }

    @Override
    public void onBindViewHolder(MessageListViewHolder holder, int position) {
        MessageItem item = getItem(position);

        bindTimestamp(holder, item);
        bindBody(holder, item);
        bindContact(holder, item);
    }

    private void bindContact(MessageListViewHolder holder, MessageItem item) {
        if (!item.isMe())
            holder.txtName.setText(Contact.get(item.mAddress, true).getName());
    }

    public MessageColumns.ColumnsMap getColumnsMap() {
        return mColumnsMap;
    }

    public void setIsGroupConversation(boolean b) {
        mIsGroupConversation = b;
    }

    private void bindTimestamp(MessageListViewHolder holder, MessageItem messageItem) {
        String timestamp;


        if (messageItem.isSending()) {
            timestamp = mContext.getString(R.string.status_sending);
        } else if (messageItem.mTimestamp != null && !messageItem.mTimestamp.equals("")) {
            timestamp = messageItem.mTimestamp;
        } else if (messageItem.isOutgoingMessage() && messageItem.isFailedMessage()) {
            timestamp = mContext.getResources().getString(R.string.status_failed);
        } else if (messageItem.isMms()) {
            timestamp = mContext.getString(R.string.loading);
        } else {
            timestamp = "";
        }

        if (!mIsGroupConversation || messageItem.isMe() || TextUtils.isEmpty(messageItem.mContact)) {
            holder.txtDate.setText(timestamp);
        } else {
            holder.txtDate.setText(mContext.getString(R.string.message_timestamp_format, timestamp, messageItem.mContact));
        }

    }

    private void bindBody(MessageListViewHolder holder, MessageItem messageItem) {
        holder.txtMsg.setAutoLinkMask(0);
        SpannableStringBuilder buf = new SpannableStringBuilder();

        String body = messageItem.mBody;

        if (messageItem.mMessageType == PduHeaders.MESSAGE_TYPE_NOTIFICATION_IND) {
            String msgSizeText = mContext.getString(R.string.message_size_label)
                    + String.valueOf((messageItem.mMessageSize + 1023) / 1024)
                    + mContext.getString(R.string.kilobyte);

            body = msgSizeText;
        }

        // Cleanse the subject
        String subject = MessageUtils.cleanseMmsSubject(mContext, messageItem.mSubject, body);
        boolean hasSubject = !TextUtils.isEmpty(subject);
        if (hasSubject) {
            buf.append(mContext.getResources().getString(R.string.inline_subject, subject));
        }

        if (!TextUtils.isEmpty(body)) {
/* SKIP            if (mPrefs.getBoolean(SettingsFragment.AUTO_EMOJI, false)) {
                body = EmojiRegistry.parseEmojis(body);
            }*/

            buf.append(body);
        }

        if (messageItem.mHighlight != null) {
            Matcher m = messageItem.mHighlight.matcher(buf.toString());
            while (m.find()) {
                buf.setSpan(new StyleSpan(Typeface.BOLD), m.start(), m.end(), 0);
            }
        }

        if (!TextUtils.isEmpty(buf)) {
            holder.txtMsg.setText(buf);
            Matcher matcher = urlPattern.matcher(holder.txtMsg.getText());
            if (matcher.find()) { //only find the image to the first link
                int matchStart = matcher.start(1);
                int matchEnd = matcher.end();
                String imageUrl = buf.subSequence(matchStart, matchEnd).toString();
                /* SKIP Ion.with(mContext).load(imageUrl).withBitmap().asBitmap().setCallback((e, result) -> {
                    try {
                        holder.setImage("url_img" + holder.getItemId(), result);
                        holder.mImageView.setOnClickListener(v -> {
                            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(imageUrl));
                            mContext.startActivity(i);
                        });
                    } catch (NullPointerException imageException) {
                        imageException.printStackTrace();
                    }
                });*/
            }
            LinkifyUtils.addLinks(holder.txtMsg);
        }
        holder.txtMsg.setVisibility(TextUtils.isEmpty(buf) ? View.GONE : View.VISIBLE);
    }

    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);

        if (CursorUtils.isValid(cursor)) {
            mColumnsMap = new MessageColumns.ColumnsMap(cursor);
            mMessageItemCache = new MessageItemCache(mContext, mColumnsMap, mSearchHighlighter, MessageColumns.CACHE_SIZE);
        }
    }
}
