package minium.co.messages.ui.messagelist;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import minium.co.messages.R;
import minium.co.messages.ui.base.QKContentFragment;

/**
 * Created by Shahab on 3/31/2016.
 */
public class MessageListFragment extends QKContentFragment {

    public static final String ARG_THREAD_ID = "threadId";
    public static final String ARG_ROW_ID = "rowId";
    public static final String ARG_HIGHLIGHT = "highlight";
    public static final String ARG_SHOW_IMMEDIATE = "showImmediate";

    private long mThreadId;
    private long mRowId;
    private String mHighlight;
    private boolean mShowImmediate;

    public static MessageListFragment getInstance(long threadId, long rowId, String highlight, boolean showImmediate) {

        Bundle args = new Bundle();
        args.putLong(ARG_THREAD_ID, threadId);
        args.putLong(ARG_ROW_ID, rowId);
        args.putString(ARG_HIGHLIGHT, highlight);
        args.putBoolean(ARG_SHOW_IMMEDIATE, showImmediate);

        MessageListFragment fragment = new MessageListFragment();
        fragment.updateArguments(args);

        return fragment;
    }

    public MessageListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mThreadId = savedInstanceState.getLong(ARG_THREAD_ID, -1);
            mRowId = savedInstanceState.getLong(ARG_ROW_ID, -1);
            mHighlight = savedInstanceState.getString(ARG_HIGHLIGHT, null);
            mShowImmediate = savedInstanceState.getBoolean(ARG_SHOW_IMMEDIATE, false);
        }

        setHasOptionsMenu(true);


    }

    // This is called by BaseContentFragment when updateArguments is called.
    @Override
    public void onNewArguments() {
        loadFromArguments();
    }

    public void loadFromArguments() {
        // Save the fields from the arguments
        Bundle args = getArguments();
        mThreadId = args.getLong(ARG_THREAD_ID, -1);
        mRowId = args.getLong(ARG_ROW_ID, -1);
        mHighlight = args.getString(ARG_HIGHLIGHT, null);
        mShowImmediate = args.getBoolean(ARG_SHOW_IMMEDIATE, false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);

        return view;
    }

    @Override
    public void onMenuChanging(float percentOpen) {

    }

    @Override
    public void inflateToolbar(Menu menu, MenuInflater inflater, Context context) {

    }
}
