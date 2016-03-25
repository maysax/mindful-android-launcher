package minium.co.messages.ui.conversationlist;


import android.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import minium.co.core.log.LogConfig;
import minium.co.core.ui.CoreFragment;
import minium.co.messages.R;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_conversation_list)
public class ConversationListFragment extends CoreFragment {

    protected final String TRACE_TAG = LogConfig.TRACE_TAG + "ConversationListFragment";

    @ViewById
    View emptyLayout;

    @ViewById
    RecyclerView listViewConversation;




    public ConversationListFragment() {
        // Required empty public constructor
    }



}
