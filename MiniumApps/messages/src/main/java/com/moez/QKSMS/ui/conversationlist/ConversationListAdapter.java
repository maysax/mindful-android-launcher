package com.moez.QKSMS.ui.conversationlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import minium.co.core.log.LogConfig;
import minium.co.core.ui.CoreActivity;
import minium.co.messages.R;
import com.moez.QKSMS.common.utils.DateFormatter;
import com.moez.QKSMS.data.Contact;
import com.moez.QKSMS.data.Conversation;
import com.moez.QKSMS.ui.base.RecyclerCursorAdapter;

/**
 * Created by shahab on 3/25/16.
 */
public class ConversationListAdapter  extends RecyclerCursorAdapter<ConversationListViewHolder, Conversation> {

    protected final String TRACE_TAG = LogConfig.TRACE_TAG + "ConversationListAdapter";

    public ConversationListAdapter(CoreActivity context) {
        super(context);
    }

    protected Conversation getItem(int position) {
        mCursor.moveToPosition(position);
        return Conversation.from(mContext, mCursor);
    }

    @Override
    public ConversationListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.list_item_conversation, null);

        ConversationListViewHolder holder = new ConversationListViewHolder(mContext, view);
        holder.badgeMuted.setImageResource(R.drawable.ic_notifications_muted);
        holder.badgeUnread.setImageResource(R.drawable.ic_unread_indicator);
        holder.badgeError.setImageResource(R.drawable.ic_error);

        return holder;

    }

    @Override
    public void onBindViewHolder(ConversationListViewHolder holder, int position) {
        final Conversation conversation = getItem(position);

        holder.mData = conversation;
        holder.mContext = mContext;
        holder.mClickListener = mItemClickListener;
        holder.root.setOnClickListener(holder);

        //holder.badgeMuted.setVisibility(View.VISIBLE);

        //holder.badgeError.setVisibility(View.VISIBLE);

        final boolean hasUnreadMessages = conversation.hasUnreadMessages();
        if (hasUnreadMessages) {
            holder.badgeUnread.setVisibility(View.VISIBLE);
            holder.txtMsg.setMaxLines(5);
        } else {
            holder.badgeUnread.setVisibility(View.GONE);
            holder.txtMsg.setMaxLines(1);
        }

        // Date
        holder.txtDate.setText(DateFormatter.getConversationTimestamp(mContext, conversation.getDate()));

        // Subject
        holder.txtMsg.setText(conversation.getSnippet());

        Contact.addListener(holder);

        // Update the avatar and name
        holder.onUpdate(conversation.getRecipients().size() == 1 ? conversation.getRecipients().get(0) : null);
    }
}
