package minium.co.launcher2.ui;


import android.support.v4.app.Fragment;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import minium.co.core.ui.CoreFragment;
import minium.co.launcher2.R;
import minium.co.launcher2.data.ActionItemManager;
import minium.co.launcher2.filter.OptionsAdapter;
import minium.co.launcher2.model.ActionItem;
import minium.co.launcher2.model.OptionsListItem;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_main)
public class OptionsFragment2 extends CoreFragment {

    @ViewById
    ListView listView;

    OptionsAdapter adapter;

    @Bean
    ActionItemManager manager;


    public OptionsFragment2() {
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
                manager.add(new ActionItem(ActionItem.ActionItemType.CALL));
                manager.fireEvent();
                break;
            case 1:
                if (manager.getCurrent().getActionText().isEmpty()) {
                    // Message not entered
                    manager.setCurrent(new ActionItem(ActionItem.ActionItemType.TEXT));
                    manager.add(new ActionItem(ActionItem.ActionItemType.DATA));
                    manager.fireEvent();

                } else {
                    // Message entered already
                    manager.getCurrent().setCompleted(true);
                    manager.add(new ActionItem(ActionItem.ActionItemType.TEXT));
                    manager.add(new ActionItem(ActionItem.ActionItemType.END_OP));
                    manager.fireEvent();
                }

                break;
        }
    }

    private OptionsListItem[] getListItems() {
        return new OptionsListItem[]{
                new OptionsListItem(0, "{fa-phone}", "Call"),
                new OptionsListItem(1, "{fa-comment-o}", "Text")

        };
    }
}