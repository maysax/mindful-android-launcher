package minium.co.launcher2.contactspicker;


import android.app.Activity;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.ListView;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.Subscribe;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.ui.CoreFragment;
import minium.co.launcher2.R;
import minium.co.launcher2.data.ActionItemManager;
import minium.co.launcher2.events.ActionItemUpdateEvent;
import minium.co.launcher2.model.ContactListItem;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_filter)
public class ContactsPickerFragment2 extends CoreFragment {

    @ViewById
    ListView listView;

    @Pref
    DroidPrefs_ prefs;

    @Bean
    ActionItemManager manager;

    ContactsPickerAdapter adapter;

    List<ContactListItem> items;

    private String mSearchString = null;

    private OnContactSelectedListener mContactsListener;

    public ContactsPickerFragment2() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Background
    void loadData() {
        items = new ArrayList<>();
        loadContacts();
        loadView();
    }

    @UiThread
    void loadView() {
        if (getActivity() != null) {
            adapter = new ContactsPickerAdapter(getActivity(), items);
            listView.setAdapter(adapter);
            listView.setFastScrollEnabled(true);
        }
    }

    private void loadContacts() {
        items = new ContactsLoader().loadContacts(getActivity());
    }

    @ItemClick(R.id.listView)
    public void listItemClicked(int position) {
        ContactListItem item = adapter.getItem(position);
        if (item.hasMultipleNumber()) {
            mContactsListener.onContactNameSelected(item.getContactId(), item.getContactName());
        } else {
            mContactsListener.onContactNumberSelected(item.getContactId(), item.getContactName(), item.getNumber().getNumber());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mContactsListener = (OnContactSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnContactSelectedListener");
        }
    }

    @Subscribe
    public void onActionUpdateEvent(ActionItemUpdateEvent event) {
        if (adapter == null) return;
        String newText = event.getText();
        String newFilter = !TextUtils.isEmpty(newText) ? newText : "";

        if (mSearchString != null && mSearchString.equals(newFilter)) {
            return;
        }
        mSearchString = newFilter;
        adapter.getFilter().filter(mSearchString);
    }
}
