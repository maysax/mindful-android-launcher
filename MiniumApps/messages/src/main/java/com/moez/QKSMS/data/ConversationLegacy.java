package com.moez.QKSMS.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import minium.co.messages.R;
import com.moez.QKSMS.common.google.DraftCache;
import com.moez.QKSMS.transaction.SmsHelper;
import com.moez.QKSMS.ui.dialog.DefaultSmsHelper;

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

    public long getThreadId() {
        return threadId;
    }

    public Uri getUri() {
        return Uri.parse("content://mms-sms/conversations/" + getThreadId());
    }

    private long[] getUnreadIds() {
        long[] ids = new long[0];

        try {
            cursor = context.getContentResolver().query(getUri(), new String[]{SmsHelper.COLUMN_ID}, SmsHelper.UNREAD_SELECTION, null, null);
            ids = new long[cursor.getCount()];
            cursor.moveToFirst();

            for (int i = 0; i < ids.length; i++) {
                ids[i] = cursor.getLong(cursor.getColumnIndexOrThrow(SmsHelper.COLUMN_ID));
                cursor.moveToNext();
                Log.d(TAG, "Unread ID: " + ids[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return ids;
    }

    public void markRead() {

        new Thread() {
            public void run() {

                long[] ids = getUnreadIds();
                if (ids.length > 0) {
                    new DefaultSmsHelper(context, R.string.not_default_mark_read).showIfNotDefault(null);

                    ContentValues cv = new ContentValues();
                    cv.put("read", true);
                    cv.put("seen", true);

                    for (long id : ids) {
                        context.getContentResolver().update(getUri(), cv, SmsHelper.COLUMN_ID + "=" + id, null);
                    }

                    /* SKIP NotificationManager.update(context);

                    UnreadBadgeService.update(context); */
                }
            }
        }.start();
    }
}
