package minium.co.launcher2.filter;


import android.support.v4.app.Fragment;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import minium.co.core.ui.CoreFragment;
import minium.co.core.util.UIUtils;
import minium.co.launcher2.R;
import minium.co.launcher2.data.ActionItemManager;
import minium.co.launcher2.model.ActionItem;
import minium.co.launcher2.model.OptionsListItem;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_main)
public class OptionsFragment extends CoreFragment {

    @ViewById
    ListView listView;

    OptionsAdapter adapter;

    @Bean
    ActionItemManager manager;


    public OptionsFragment() {
        // Required empty public constructor
    }

    @AfterViews
    void afterViews() {
        adapter = new OptionsAdapter(getActivity(), getListItems());
        listView.setAdapter(adapter);
    }

    @ItemClick(R.id.listView)
    public void listItemClicked(int position) {
        switch (position) {
            case 0:
                String actionText = manager.getCurrent().getActionText();
                manager.setCurrent(new ActionItem(ActionItem.ActionItemType.DATA));
                manager.getCurrent().setActionText(actionText).setCompleted(true);
                manager.add(new ActionItem(ActionItem.ActionItemType.CONTACT));
                manager.fireEvent();
                break;
            case 1:
                UIUtils.alert(getActivity(), getString(R.string.msg_not_yet_implemented));
                break;
        }
    }

    private OptionsListItem[] getListItems() {
        return new OptionsListItem[] {
            new OptionsListItem(0, "{fa-users}", "Select Contacts"),
                new OptionsListItem(1, "{fa-sticky-note-o}", "Add Note")
        };
    }
}
