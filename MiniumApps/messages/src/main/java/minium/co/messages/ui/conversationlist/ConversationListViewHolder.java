package minium.co.messages.ui.conversationlist;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import minium.co.core.ui.CoreActivity;
import minium.co.messages.R;
import minium.co.messages.data.Contact;
import minium.co.messages.data.Conversation;
import minium.co.messages.ui.base.ClickyViewHolder;

/**
 * Created by shahab on 3/25/16.
 */

public class ConversationListViewHolder extends ClickyViewHolder<Conversation> implements Contact.UpdateListener {

    protected View root;
    protected ImageView selected;
    protected TextView txtName;
    protected TextView txtDate;
    protected TextView txtMsg;
    protected ImageView badgeMuted;
    protected ImageView badgeError;
    protected ImageView badgeUnread;

    public ConversationListViewHolder(CoreActivity context, View itemView) {
        super(context, itemView);

        selected = (ImageView) itemView.findViewById(R.id.selected);
        txtName = (TextView) itemView.findViewById(R.id.txtName);
        txtDate = (TextView) itemView.findViewById(R.id.txtDate);
        txtMsg = (TextView) itemView.findViewById(R.id.txtMsg);
        badgeMuted = (ImageView) itemView.findViewById(R.id.badgeMuted);
        badgeError = (ImageView) itemView.findViewById(R.id.badgeError);
        badgeUnread = (ImageView) itemView.findViewById(R.id.badgeUnread);
    }

    @Override
    public void onUpdate(Contact updated) {

    }
}
