package co.minium.launcher3.main;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;

import co.minium.launcher3.R;
import co.minium.launcher3.contact.ContactsLoader;
import co.minium.launcher3.model.ContactListItem;
import co.minium.launcher3.model.MainListItem;
import co.minium.launcher3.model.MainListItemType;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.ui.CoreFragment;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_main)
public class MainFragment extends CoreFragment {

    @ViewById
    ListView listView;

    @Pref
    DroidPrefs_ prefs;

    List<MainListItem> items;

    private MainListAdapter adapter;


    public MainFragment() {
        // Required empty public constructor
    }

    @AfterViews
    void afterViews() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        loadData();
    }

    @Background
    void loadData() {
        items = new ArrayList<>();
        loadActions();
        loadContacts();
        loadDefaults();
        loadView();
    }

    private void loadActions() {
        items.add(new MainListItem(1, getString(R.string.title_text), "{fa-comment}"));
        items.add(new MainListItem(2, getString(R.string.title_call), "{fa-phone}"));
        items.add(new MainListItem(3, getString(R.string.title_note), "{fa-sticky-note}"));


        /*
        items.add(new MainListItem(new ActionListItem(3, "{fa-users}", getString(R.string.title_messages))));
        items.add(new MainListItem(new ActionListItem(4, "{fa-phone}", getString(R.string.title_callLog))));
        items.add(new MainListItem(new ActionListItem(5, "{fa-user}", getString(R.string.title_contacts))));
        items.add(new MainListItem(new ActionListItem(6, "{fa-ban}", getString(R.string.title_flow))));
        items.add(new MainListItem(new ActionListItem(7, "{fa-microphone}", getString(R.string.title_voicemail))));
        items.add(new MainListItem(new ActionListItem(8, "{fa-sticky-note}", getString(R.string.title_notes))));
        items.add(new MainListItem(new ActionListItem(9, "{fa-clock-o}", getString(R.string.title_clock))));
        items.add(new MainListItem(new ActionListItem(10, "{fa-cogs}", getString(R.string.title_settings))));
        items.add(new MainListItem(new ActionListItem(11, "{fa-tint}", getString(R.string.title_theme))));
        items.add(new MainListItem(new ActionListItem(12, "{fa-bell}", getString(R.string.title_notificationScheduler))));
        items.add(new MainListItem(new ActionListItem(13, "{fa-street-view}", getString(R.string.title_map))));

        if (!Build.MODEL.toLowerCase().contains("siempo"))
            items.add(new MainListItem(new ActionListItem(14, "{fa-home}", getString(R.string.title_defaultLauncher))));

        items.add(new MainListItem(new ActionListItem(15, "{fa-info-circle}", getString(R.string.title_version, BuildConfig.VERSION_NAME))));
        */
    }

    private void loadContacts() {
        List<ContactListItem> contactListItems = new ContactsLoader().loadContacts(getActivity());
        items.addAll(contactListItems);
    }

    private void loadDefaults() {
        items.add(new MainListItem(1, getString(R.string.title_sendAsSMS), "{fa-comment}"));
        items.add(new MainListItem(2, getString(R.string.title_saveNote), "{fa-pencil}"));
        items.add(new MainListItem(3, getString(R.string.title_createContact), "{fa-user-plus}"));
    }

    @UiThread
    void loadView() {
        if (getActivity() != null) {
            adapter = new MainListAdapter(getActivity(), items);
            listView.setAdapter(adapter);
            //adapter.getFilter().filter(manager.getCurrent().getActionText());
        }
    }
}
