package minium.co.messages.ui.conversationlist;

import android.view.ViewGroup;

import minium.co.core.log.LogConfig;
import minium.co.core.ui.CoreActivity;
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
        return null;
    }

    @Override
    public void onBindViewHolder(ConversationListViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
