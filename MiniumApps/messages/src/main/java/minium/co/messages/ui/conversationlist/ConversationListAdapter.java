package minium.co.messages.ui.conversationlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import minium.co.core.log.LogConfig;
import minium.co.core.ui.CoreActivity;
import minium.co.messages.R;
import minium.co.messages.data.Conversation;
import minium.co.messages.ui.base.RecyclerCursorAdapter;

/**
 * Created by shahab on 3/25/16.
 */
public class ConversationListAdapter  extends RecyclerCursorAdapter<ConversationListViewHolder, Conversation> {

    protected final String TRACE_TAG = LogConfig.TRACE_TAG + "ConversationListAdapter";

    public ConversationListAdapter(CoreActivity context) {
        super(context);
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

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
