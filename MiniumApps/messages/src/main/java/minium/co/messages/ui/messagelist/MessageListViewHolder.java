package minium.co.messages.ui.messagelist;

import android.view.View;
import android.widget.TextView;

import minium.co.core.ui.CoreActivity;
import minium.co.messages.R;
import minium.co.messages.ui.base.ClickyViewHolder;

/**
 * Created by Shahab on 3/31/2016.
 */
public class MessageListViewHolder extends ClickyViewHolder<MessageItem> {

    protected View root;
    protected TextView txtDate;
    protected TextView txtName;
    protected TextView txtMsg;

    public MessageListViewHolder(CoreActivity context, View itemView) {
        super(context, itemView);

        root = itemView.findViewById(R.id.rowLayout);
        txtDate = (TextView) itemView.findViewById(R.id.txtDate);
        txtName = (TextView) itemView.findViewById(R.id.txtName);
        txtMsg = (TextView) itemView.findViewById(R.id.txtMsg);
    }
}
