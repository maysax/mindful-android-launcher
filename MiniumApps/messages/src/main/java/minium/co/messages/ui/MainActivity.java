package minium.co.messages.ui;

import org.androidannotations.annotations.EActivity;

import minium.co.core.ui.CoreActivity;
import minium.co.messages.R;


@EActivity(R.layout.activity_main)
public class MainActivity extends CoreActivity {

    public void setConversation(long threadId, long rowId, String pattern, boolean animate) {
        /* SKIP
        MessageListFragment fragment = MessageListFragment.getInstance(threadId, rowId, pattern, !animate);

        // Save the thread ID here and switch the content
        sThreadShowing = threadId;
        switchContent(fragment, animate);
        */
    }

}
