package minium.co.messages.ui.conversationlist;

import android.view.View;

import minium.co.messages.data.Contact;
import minium.co.messages.data.Conversation;
import minium.co.messages.ui.base.ClickyViewHolder;

/**
 * Created by shahab on 3/25/16.
 */
public class ConversationListViewHolder extends ClickyViewHolder<Conversation> implements Contact.UpdateListener {

    public ConversationListViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void onUpdate(Contact updated) {

    }
}
