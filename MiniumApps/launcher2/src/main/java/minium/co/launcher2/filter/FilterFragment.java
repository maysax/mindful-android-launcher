package minium.co.launcher2.filter;


import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.widget.FrameLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreFragment;
import minium.co.launcher2.R;
import minium.co.launcher2.contactspicker.ContactsPickerFragment_;
import minium.co.launcher2.model.ContactItem;
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
        loadFragment(R.id.optionsFragment, OptionsFragment_.builder().build());
        loadContacts();
    }

    void loadFragment(int id, android.app.Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(id, fragment).commit();
    }

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

    @Background
    void loadContacts() {
        Uri contactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String selection = "((" + DISPLAY_NAME_COMPAT + " NOTNULL) AND ("
                + ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + "=1) AND ("
                + DISPLAY_NAME_COMPAT + " != '' ))";

        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

        Cursor contactCursor = getActivity().getContentResolver().query(contactUri, CONTACTS_SUMMARY_PROJECTION, selection, null, sortOrder);

        List<ContactItem> items = new ArrayList<>();
        ContactItem currItem = null;

        if (contactCursor != null) {
            while(contactCursor.moveToNext()) {
                long id = contactCursor.getLong(contactCursor.getColumnIndexOrThrow(ContactsContract.Data.CONTACT_ID));
                String name = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String label =  ContactsContract.CommonDataKinds.Phone.getTypeLabel(getResources(),
                        contactCursor.getInt(contactCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.TYPE)),
                        contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL))).toString();
                String number = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                if (currItem == null || currItem.getContactId() != id) {
                    currItem = new ContactItem(id, name);
                    items.add(currItem);
                }

                currItem.addNumbers(label, number);

//                Tracer.d("Id: " + contactCursor.getLong(contactCursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)) +
//                        " Contact name: " + contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
//                    + " Number: " + contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
//                        + " Label: " + ContactsContract.CommonDataKinds.Phone.getTypeLabel(getResources(),
//                        contactCursor.getInt(contactCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.TYPE)),
//                        contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL))).toString()
//                );
            }
            contactCursor.close();

            for (ContactItem item : items) {
                Tracer.d(item.toString());
            }
        }

        /*Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        if (phones != null) {
            while (phones.moveToNext())
            {
                String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Tracer.d("Name: " + name + " number: " + phoneNumber);
            }
            phones.close();
        }*/
    }

}
