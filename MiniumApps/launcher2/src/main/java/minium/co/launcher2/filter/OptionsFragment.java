package minium.co.launcher2.filter;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import minium.co.core.ui.CoreFragment;
import minium.co.launcher2.R;
import minium.co.launcher2.model.OptionsListItem;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_main)
public class OptionsFragment extends CoreFragment {

    @ViewById
    ListView listView;

    OptionsAdapter adapter;


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
                break;
        }
    }

    private OptionsListItem[] getListItems() {
        return new OptionsListItem[] {
            new OptionsListItem("{fa-users}", "Select Contacts"),
                new OptionsListItem("{fa-sticky-note-o}", "Add Note")
        };
    }
}
