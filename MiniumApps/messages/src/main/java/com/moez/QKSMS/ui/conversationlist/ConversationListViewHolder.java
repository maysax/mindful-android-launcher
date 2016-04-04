package com.moez.QKSMS.ui.conversationlist;

import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moez.QKSMS.data.Contact;
import com.moez.QKSMS.data.Conversation;
import com.moez.QKSMS.data.ConversationLegacy;
import com.moez.QKSMS.ui.base.ClickyViewHolder;

import minium.co.core.ui.CoreActivity;
import minium.co.messages.R;
import minium.co.messages.ui.MainActivity;

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

        root = itemView.findViewById(R.id.rowLayout);
        selected = (ImageView) itemView.findViewById(R.id.selected);
        txtName = (TextView) itemView.findViewById(R.id.txtName);
        txtDate = (TextView) itemView.findViewById(R.id.txtDate);
        txtMsg = (TextView) itemView.findViewById(R.id.txtMsg);
        badgeMuted = (ImageView) itemView.findViewById(R.id.badgeMuted);
        badgeError = (ImageView) itemView.findViewById(R.id.badgeError);
        badgeUnread = (ImageView) itemView.findViewById(R.id.badgeUnread);
    }

    @Override
    public void onUpdate(final Contact updated) {
        boolean shouldUpdate = true;
        final Drawable drawable;
        final String name;

        if (mData.getRecipients().size() == 1) {
            Contact contact = mData.getRecipients().get(0);
            if (contact.getNumber().equals(updated.getNumber())) {
                // SKIP drawable = contact.getAvatar(mContext, null);
                name = contact.getName();

/* SKIP                if (contact.existsInDatabase()) {
                    mAvatarView.assignContactUri(contact.getUri());
                } else {
                    mAvatarView.assignContactFromPhone(contact.getNumber(), true);
                }*/
            } else {
                // onUpdate was called because *some* contact was loaded, but it wasn't the contact for this
                // conversation, and thus we shouldn't update the UI because we won't be able to set the correct data
                drawable = null;
                name = "";
                shouldUpdate = false;
            }
        } else if (mData.getRecipients().size() > 1) {
            drawable = null;
            name = "" + mData.getRecipients().size();
            // SKIP mAvatarView.assignContactUri(null);
        } else {
            drawable = null;
            name = "#";
            // SKIP mAvatarView.assignContactUri(null);
        }

        final ConversationLegacy conversationLegacy = new ConversationLegacy(mContext, mData.getThreadId());

        if (shouldUpdate) {
            ((MainActivity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // SKIP mAvatarView.setImageDrawable(drawable);
                    // SKIP mAvatarView.setContactName(name);
                    txtName.setText(formatMessage(mData, conversationLegacy));
                }
            });
        }
    }

    private CharSequence formatMessage(Conversation conversation, ConversationLegacy conversationLegacy) {
        String from = conversation.getRecipients().formatNames(", ");

        SpannableStringBuilder buf = new SpannableStringBuilder(from);

        if (conversation.getMessageCount() > 1 /* SKIP && mPrefs.getBoolean(SettingsFragment.MESSAGE_COUNT, false) */ ) {
            int before = buf.length();
            buf.append(mContext.getResources().getString(R.string.format_msg_count, conversation.getMessageCount()));
            buf.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.material_core_grey)), before, buf.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        if (conversationLegacy.hasDraft()) {
            buf.append(mContext.getResources().getString(R.string.draft_separator));
            int before = buf.length();
            buf.append(mContext.getResources().getString(R.string.has_draft));
            buf.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.colorPrimary)), before, buf.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        return buf;
    }
}
