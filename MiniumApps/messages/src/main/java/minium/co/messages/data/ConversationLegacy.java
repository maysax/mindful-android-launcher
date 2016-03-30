package minium.co.messages.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import minium.co.messages.common.google.DraftCache;

/**
 * Use this class (rather than Conversation) for marking conversations as read, and managing drafts.
 */
public class ConversationLegacy {
    private final String TAG = "ConversationLegacy";

    public static final Uri CONVERSATIONS_CONTENT_PROVIDER = Uri.parse("content://mms-sms/conversations?simple=true");
    public static final Uri ADDRESSES_CONTENT_PROVIDER = Uri.parse("content://mms-sms/canonical-addresses");

    public static final int COLUMN_ADDRESSES_ADDRESS = 1;

    // SKIP private ContactHelper contactHelper;
    private Context context;

    private long threadId;
    private String name;
    private String address;
    private long recipient;
    private String draft;
    private int type;

    private Cursor cursor;

    public ConversationLegacy(Context context, long threadId) {
        this.context = context;
        this.threadId = threadId;
        // SKIP contactHelper = new ContactHelper();
    }

    public boolean hasDraft() {
        return DraftCache.getInstance().hasDraft(threadId);
    }
}
