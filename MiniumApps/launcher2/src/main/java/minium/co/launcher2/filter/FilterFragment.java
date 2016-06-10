package minium.co.launcher2.filter;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import minium.co.core.ui.CoreFragment;
import minium.co.launcher2.R;
import minium.co.launcher2.contactspicker.ContactsPickerFragment_;
import minium.co.launcher2.ui.MainFragment_;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_filter)
public class FilterFragment extends CoreFragment {

    @ViewById
    FrameLayout actionFragment;

    @ViewById
    FrameLayout contactsFragment;

    @ViewById
    FrameLayout optionsFragment;


    public FilterFragment() {
        // Required empty public constructor
    }

    @AfterViews
    void afterViews() {
        loadFragment(R.id.actionFragment, MainFragment_.builder().build());
        loadFragment(R.id.contactsFragment, ContactsPickerFragment_.builder().build());
    }

    void loadFragment(int id, android.app.Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(id, fragment).commit();
    }

}
