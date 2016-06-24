package minium.co.launcher2.filter;


import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;

import minium.co.core.app.DroidPrefs_;
import minium.co.core.ui.CoreFragment;
import minium.co.launcher2.R;
import minium.co.launcher2.data.ActionItemManager;
import minium.co.launcher2.model.ActionListItem;
import minium.co.launcher2.model.ContactListItem;
import minium.co.launcher2.model.MainListItem;
import minium.co.launcher2.model.OptionsListItem;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_filter_2)
public class FilterFragment2 extends CoreFragment {

    @ViewById
    ListView listView;

    @Pref
    DroidPrefs_ prefs;

    @Bean
    ActionItemManager manager;

    FilterAdapter adapter;

    List<MainListItem> items;

    @SuppressLint("InlinedApi")
    private static String DISPLAY_NAME_COMPAT = Build.VERSION.SDK_INT
            >= Build.VERSION_CODES.HONEYCOMB ?
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY :
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;


    private static final String[] CONTACTS_SUMMARY_PROJECTION = new String[]{
            ContactsContract.Data.CONTACT_ID,
            DISPLAY_NAME_COMPAT,
            ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER,
            ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
            ContactsContract.CommonDataKinds.Phone.TYPE,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.LABEL
    };


    public FilterFragment2() {
        // Required empty public constructor
    }

    @AfterViews
    void afterViews() {

    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Background
    void loadData() {
        items = new ArrayList<>();
        loadActions();
        loadContacts();
        loadOptions();
        loadView();
    }

    @UiThread
    void loadView() {
        adapter = new FilterAdapter(getActivity(), items);
        listView.setAdapter(adapter);
    }

    private void loadOptions() {
        items.add(new MainListItem(new OptionsListItem(0, "{fa-paper-plane}", "Send Text")));
        items.add(new MainListItem(new OptionsListItem(1, "{fa-pencil}", "Save Note")));
        items.add(new MainListItem(new OptionsListItem(2, "{fa-user-plus}", "Create Contact")));
    }

    private void loadActions() {
        items.add(new MainListItem(new ActionListItem(0, "{fa-comment}", "Text")));
        items.add(new MainListItem(new ActionListItem(1, "{fa-phone}", "Call")));
        items.add(new MainListItem(new ActionListItem(2, "{fa-sticky-note}", "Note")));
        items.add(new MainListItem(new ActionListItem(3, "{fa-users}", "Messages")));
        items.add(new MainListItem(new ActionListItem(4, "{fa-phone}", "Call Log")));
        items.add(new MainListItem(new ActionListItem(5, "{fa-user}", "Contacts")));
        items.add(new MainListItem(new ActionListItem(6, "{fa-ban}", "Flow")));
        items.add(new MainListItem(new ActionListItem(7, "{fa-microphone}", "Voicemail")));
        items.add(new MainListItem(new ActionListItem(8, "{fa-sticky-note}", "Notes")));
        items.add(new MainListItem(new ActionListItem(9, "{fa-clock-o}", "Clock")));
        items.add(new MainListItem(new ActionListItem(10, "{fa-cogs}", "Settings")));
        items.add(new MainListItem(new ActionListItem(11, "{fa-tint}", "Theme")));
    }

    private void loadContacts() {
        Uri contactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String selection = "((" + DISPLAY_NAME_COMPAT + " NOTNULL) AND ("
                + ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + "=1) AND ("
                + DISPLAY_NAME_COMPAT + " != '' ))";

        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

        Cursor contactCursor = getActivity().getContentResolver().query(contactUri, CONTACTS_SUMMARY_PROJECTION, selection, null, sortOrder);

        ContactListItem currItem = null;

        if (contactCursor != null) {
            while(contactCursor.moveToNext()) {
                long id = contactCursor.getLong(contactCursor.getColumnIndexOrThrow(ContactsContract.Data.CONTACT_ID));
                String name = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String label =  ContactsContract.CommonDataKinds.Phone.getTypeLabel(getResources(),
                        contactCursor.getInt(contactCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.TYPE)),
                        contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL))).toString();
                String number = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                if (currItem == null || currItem.getContactId() != id) {
                    currItem = new ContactListItem(id, name);
                    items.add(new MainListItem(currItem));
                }

                currItem.addNumbers(label, number);

            }
            contactCursor.close();
        }
    }

    @ItemClick(R.id.listView)
    public void listItemClicked(int position) {

    }
}
