package minium.co.messages.ui;

import android.text.TextUtils;

import com.moez.QKSMS.ui.conversationlist.ConversationListFragment_;
import com.moez.QKSMS.ui.messagelist.MessageListFragment_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.sharedpreferences.Pref;

import minium.co.core.ui.CoreActivity;
import minium.co.messages.R;
import minium.co.messages.app.MessagesPref_;


@EActivity(R.layout.activity_main)
public class MainActivity extends CoreActivity {

    // This thread (messages) is being displayed currently.
    public static long sThreadShowing;

    @InstanceState
    Boolean isMmsSetupDismissed = false;

    @Pref
    MessagesPref_ prefs;

    @AfterViews
    void afterViews() {
        loadFragment(ConversationListFragment_.builder().build());
        showDialogIfNeeded();
    }

    private void showDialogIfNeeded() {
        if (!isMmsSetupDismissed) {
            beginMmsSetup();
        }
    }

    private void beginMmsSetup() {
        if (!prefs.isMmsSetupDoNotAskAgain().get()
                && TextUtils.isEmpty(prefs.mmsURL().get())
                && TextUtils.isEmpty(prefs.mmsProxy().get())
                && TextUtils.isEmpty(prefs.mmsPort().get())) {

        }
    }

    public void setConversation(long threadId, long rowId, String pattern, boolean animate) {
        // Save the thread ID here and switch the content
        sThreadShowing = threadId;
        loadChildFragment(MessageListFragment_.builder()
                .threadId(threadId)
                .rowId(rowId)
                .highlight(pattern)
                .showImmediate(animate).build());
    }
}
