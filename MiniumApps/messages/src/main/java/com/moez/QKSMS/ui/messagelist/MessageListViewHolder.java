package com.moez.QKSMS.ui.messagelist;

import android.view.View;
import android.widget.TextView;

import com.moez.QKSMS.ui.base.ClickyViewHolder;

import minium.co.core.ui.CoreActivity;
import minium.co.messages.R;

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
