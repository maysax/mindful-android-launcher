package minium.co.messages.ui;

import android.app.Fragment;
import android.app.FragmentManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.messages.R;
import minium.co.messages.common.util.KeyboardUtils;
import minium.co.messages.ui.conversationlist.ConversationListFragment_;
import minium.co.messages.ui.messagelist.MessageListFragment;


@EActivity(R.layout.activity_main)
public class MainActivity extends CoreActivity {

    public static long sThreadShowing;

    private ContentFragment mContent;

    private boolean mIsDestroyed = false;

    @AfterViews
    void afterViews() {
        loadFragment(ConversationListFragment_.builder().build());
    }

    public void setConversation(long threadId, long rowId, String pattern, boolean animate) {

        MessageListFragment fragment = MessageListFragment.getInstance(threadId, rowId, pattern, !animate);

        // Save the thread ID here and switch the content
        sThreadShowing = threadId;
        switchContent(fragment, animate);

    }

    public void switchContent(ContentFragment fragment, boolean animate) {
        // Make sure that the activity isn't destroyed before making fragment transactions.
        if (fragment != null && !mIsDestroyed) {
            KeyboardUtils.hide(this);

            mContent = fragment;
            FragmentManager m = getFragmentManager();

            // Only do a replace if it is a different fragment.
            if (fragment != m.findFragmentById(R.id.mainView)) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainView, (Fragment) fragment)
                        .commitAllowingStateLoss();
            }

            /* SKIP mSlidingMenu.showContent(animate); */
            invalidateOptionsMenu();

        } else {
            Tracer.w("Null fragment, can't switch content");
        }
    }

}
