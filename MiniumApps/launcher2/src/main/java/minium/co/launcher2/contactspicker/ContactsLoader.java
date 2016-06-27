package minium.co.launcher2.contactspicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

import minium.co.launcher2.model.ContactListItem;

/**
 * Created by Shahab on 6/27/2016.
 */
public class ContactsLoader {

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


    public List<ContactListItem> loadContacts(Context context) {
        Uri contactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String selection = "((" + DISPLAY_NAME_COMPAT + " NOTNULL) AND ("
                + ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + "=1) AND ("
                + DISPLAY_NAME_COMPAT + " != '' ))";

        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

        Cursor contactCursor = context.getContentResolver().query(contactUri, CONTACTS_SUMMARY_PROJECTION, selection, null, sortOrder);

        ContactListItem currItem = null;
        List<ContactListItem> items = new ArrayList<>();

        if (contactCursor != null) {
            while(contactCursor.moveToNext()) {
                long id = contactCursor.getLong(contactCursor.getColumnIndexOrThrow(ContactsContract.Data.CONTACT_ID));
                String name = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String label =  ContactsContract.CommonDataKinds.Phone.getTypeLabel(context.getResources(),
                        contactCursor.getInt(contactCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.TYPE)),
                        contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL))).toString();
                String number = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                if (currItem == null || currItem.getContactId() != id) {
                    currItem = new ContactListItem(id, name);
                    items.add(currItem);
                }

                currItem.addNumbers(label, number);

            }
            contactCursor.close();
        }

        return items;
    }
}
