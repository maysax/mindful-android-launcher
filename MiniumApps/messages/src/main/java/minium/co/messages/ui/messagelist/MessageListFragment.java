package minium.co.messages.ui.messagelist;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import minium.co.messages.R;
import minium.co.messages.data.Conversation;
import minium.co.messages.data.ConversationLegacy;
import minium.co.messages.data.Message;
import minium.co.messages.transaction.SmsHelper;
import minium.co.messages.ui.base.QKContentFragment;
import minium.co.messages.ui.base.RecyclerCursorAdapter;

/**
 * Created by Shahab on 3/31/2016.
 */
@EFragment(R.layout.fragment_conversation)
public class MessageListFragment extends QKContentFragment implements  LoaderManager.LoaderCallbacks<Cursor>, RecyclerCursorAdapter.ItemClickListener<MessageItem> {

    private Cursor mCursor;
    private MessageListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    @ViewById
    RecyclerView mRecyclerView;

    private Conversation mConversation;
    private ConversationLegacy mConversationLegacy;

    private boolean mOpened = false;

    @FragmentArg
    long threadId = -1;

    @FragmentArg
    long rowId = -1;

    @FragmentArg
    String highlight = null;

    @FragmentArg
    boolean showImmediate = false;

    public MessageListFragment() {

    }

    @AfterViews
    void afterViews() {
        mAdapter = new MessageListAdapter(context);
        mAdapter.setItemClickListener(this);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                int position;

                if (rowId != -1 && mCursor != null) {
                    // Scroll to the position in the conversation for that message.
                    position = SmsHelper.getPositionForMessageId(mCursor, "sms", rowId, mAdapter.getColumnsMap());

                    // Be sure to reset the row ID here---we only want to scroll to the message
                    // the first time the cursor is loaded after the row ID is set.
                    rowId = -1;

                } else {
                    position = mAdapter.getItemCount() - 1;
                }

                if (position != -1) {
                    manager.smoothScrollToPosition(mRecyclerView, null, position);
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onResume() {
        super.onResume();
        onOpenConversation();
    }

    @Override
    public void onMenuChanging(float percentOpen) {

    }

    @Override
    public void inflateToolbar(Menu menu, MenuInflater inflater, Context context) {

    }

    @Override
    public void onItemClick(MessageItem object, View view) {

    }

    @Override
    public void onItemLongClick(MessageItem object, View view) {

    }

    /**
     * To be called when the user opens a conversation. Initializes the Conversation objects, sets
     * up the draft, and marks the conversation as read.
     * <p>
     * Note: This will have no effect if the context has not been initialized yet.
     */
    private void onOpenConversation() {
        loadConversationTask();
    }

    private void setTitle() {
        context.setTitle(mConversation.getRecipients().formatNames(", "));
    }

    private void initLoaderManager() {
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onContentOpening() {
        super.onContentOpening();
        mOpened = false; // We're animating the fragment in, this flag warns us not to do anything heavy
    }

    @Override
    public void onContentOpened() {
        super.onContentOpened();
        mOpened = true; // The fragment has finished animating in

/* SKIP        if (mContext.getBoolean(QKPreference.PROXIMITY_SENSOR)) {
            mSensorManager.registerListener(this, mProxSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        ThemeManager.setActiveColor(mConversationPrefs.getColor());*/
    }
    @Background
    void loadConversationTask() {
        mConversation = Conversation.get(context, threadId, true);
        mConversationLegacy = new ConversationLegacy(context, threadId);

        mConversationLegacy.markRead();
        /* SKIP mConversation.blockMarkAsRead(true);
        mConversation.markAsRead(); */

        // Delay the thread until the fragment has finished opening. If it waits longer than
        // 10 seconds, then something is wrong, so cancel it. This happens when the fragment is closed before
        // it opens, or the screen is rotated, and then "mOpened" never gets changed to true,
        // leaving this thread running forever. This issue is actually what caused the great
        // QKSMS battery drain of 2015
/*        long time = System.currentTimeMillis();
        while (!mOpened) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (System.currentTimeMillis() - time > 10000) {
                Tracer.w("Task running for over 10 seconds, something is wrong");
                return;
            }
        }*/
        handleConversationTask();
    }

    @UiThread
    void handleConversationTask() {
        /* SKIP mComposeView.onOpenConversation(mConversation, mConversationLegacy); */
        setTitle();

        mAdapter.setIsGroupConversation(mConversation.getRecipients().size() > 1);

        if (isAdded()) {
            initLoaderManager();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(context,
                Uri.withAppendedPath(Message.MMS_SMS_CONTENT_PROVIDER, String.valueOf(threadId)),
                MessageColumns.PROJECTION, null, null, "normalized_date ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mAdapter != null) {
            // Swap the new cursor in.  (The framework will take care of closing the, old cursor once we return.)
            mAdapter.changeCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mAdapter != null) {
            mAdapter.changeCursor(null);
        }
    }
}
