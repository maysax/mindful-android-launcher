package com.moez.QKSMS.ui.conversationlist;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.ViewById;

import minium.co.core.log.LogConfig;
import minium.co.core.ui.CoreFragment;
import minium.co.messages.R;
import com.moez.QKSMS.data.Conversation;
import com.moez.QKSMS.transaction.SmsHelper;
import minium.co.messages.ui.MainActivity;
import com.moez.QKSMS.ui.base.RecyclerCursorAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_conversation_list)
public class ConversationListFragment extends CoreFragment
implements RecyclerCursorAdapter.ItemClickListener<Conversation>, LoaderManager.LoaderCallbacks<Cursor>{

    protected final String TRACE_TAG = LogConfig.TRACE_TAG + "ConversationListFragment";

    @ViewById
    View emptyLayout;

    @ViewById
    RecyclerView listViewConversation;

    private ConversationListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private boolean mViewHasLoaded = false;

    // This does not hold the current position of the list, rather the position the list is pending being set to
    private int mPosition;


    public ConversationListFragment() {
        // Required empty public constructor
    }

    @AfterViews
    void afterViews() {
        setHasOptionsMenu(true);

        mAdapter = new ConversationListAdapter(context);
        mAdapter.setItemClickListener(this);
        mLayoutManager = new LinearLayoutManager(context);

        listViewConversation.setHasFixedSize(true);
        listViewConversation.setLayoutManager(mLayoutManager);
        listViewConversation.setAdapter(mAdapter);

        mViewHasLoaded = true;

        initLoaderManager();
    }

    @Override
    public void onItemClick(Conversation conversation, View view) {
        /* Skip if (mAdapter.isInMultiSelectMode()) {
            mAdapter.toggleSelection(conversation.getThreadId(), conversation);
        } else { */
            ((MainActivity) context).setConversation(conversation.getThreadId(), -1, null, true);
        /* Skip } */
    }

    @Override
    public void onItemLongClick(Conversation object, View view) {

    }

    public void setPosition(int position) {
        mPosition = position;
        if (mLayoutManager != null && mAdapter != null) {
            mLayoutManager.scrollToPosition(Math.min(mPosition, mAdapter.getCount() - 1));
        }
    }

    public int getPosition() {
        return mLayoutManager.findFirstVisibleItemPosition();
    }

    private void initLoaderManager() {
        getLoaderManager().restartLoader(0, null, this);
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(context, SmsHelper.CONVERSATIONS_CONTENT_PROVIDER, Conversation.ALL_THREADS_PROJECTION, null, null, "date DESC");
    }

    @Trace(tag = TRACE_TAG)
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mAdapter != null) {
            // Swap the new cursor in.  (The framework will take care of closing the, old cursor once we return.)
            mAdapter.changeCursor(data);
            if (mPosition != 0) {
                listViewConversation.scrollToPosition(Math.min(mPosition, data.getCount() - 1));
                mPosition = 0;
            }
        }

        emptyLayout.setVisibility(data != null && data.getCount() > 0 ? View.GONE : View.VISIBLE);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        if (mAdapter != null) {
            mAdapter.changeCursor(null);
        }
    }
}
