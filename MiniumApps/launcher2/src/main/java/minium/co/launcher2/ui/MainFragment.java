package minium.co.launcher2.ui;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import de.greenrobot.event.EventBus;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreFragment;
import minium.co.core.util.UIUtils;
import minium.co.launcher2.R;
import minium.co.launcher2.adapters.MainAdapter;
import minium.co.launcher2.events.MainItemClickedEvent;
import minium.co.launcher2.helper.ActivityHelper;
import minium.co.launcher2.model.MainListItem;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_main)
public class MainFragment extends CoreFragment {

    @ViewById
    ListView listView;

    MainAdapter adapter;

    public MainFragment() {
        // Required empty public constructor
    }

    @AfterViews
    void afterViews() {
        adapter = new MainAdapter(getActivity(), getListItems());
        listView.setAdapter(adapter);
    }

    @ItemClick(R.id.listView)
    public void listItemClicked(int position) {
        switch (position) {
            case 0:
                EventBus.getDefault().post(new MainItemClickedEvent("Text", 0));
                break;
            case 1:
                EventBus.getDefault().post(new MainItemClickedEvent("Call", 1));
                break;
            case 2:
                EventBus.getDefault().post(new MainItemClickedEvent("Note", 2));
                break;
            case 3:
                if (!new ActivityHelper(getActivity()).openMessagingApp())
                    UIUtils.alert(getActivity(), getString(R.string.msg_not_yet_implemented));
                break;
            case 4:
                if (!new ActivityHelper(getActivity()).openContactsApp())
                    UIUtils.alert(getActivity(), getString(R.string.msg_not_yet_implemented));
                break;
            case 5:
                Tracer.d("Clicked on flow");
                break;
            case 6:
                UIUtils.alert(getActivity(), getString(R.string.msg_not_yet_implemented));
                break;
            case 7:
                UIUtils.alert(getActivity(), getString(R.string.msg_not_yet_implemented));
                break;
            case 8:
                UIUtils.alert(getActivity(), getString(R.string.msg_not_yet_implemented));
                break;
            case 9:
                if (!new ActivityHelper(getActivity()).openSettingsApp())
                    UIUtils.alert(getActivity(), getString(R.string.msg_not_yet_implemented));
                break;
        }
    }

    private MainListItem[] getListItems() {
        return new MainListItem[] {
                new MainListItem("{fa-comment-o}", "Text"),
                new MainListItem("{fa-phone}", "Call"),
                new MainListItem("{fa-sticky-note-o}", "Add Note"),
                new MainListItem("{fa-users}", "Conversations"),
                new MainListItem("{fa-user}", "Address Book"),
                new MainListItem("{fa-ban}", "Flow"),
                new MainListItem("{fa-phone}", "Voicemail"),
                new MainListItem("{fa-sticky-note-o}", "Notes"),
                new MainListItem("{fa-clock-o}", "Clock"),
                new MainListItem("{fa-cogs}", "Settings")
        };
    }

}
