package co.siempo.phone.contact;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

import co.siempo.phone.model.ContactListItem;

/**
 * Created by Shahab on 6/27/2016.
 */
public class ContactsLoader {


    private static final String DISPLAY_NAME_COMPAT = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY;


    private static final String[] CONTACTS_SUMMARY_PROJECTION = new String[]{
            ContactsContract.Data.CONTACT_ID,
            DISPLAY_NAME_COMPAT,
            ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER,
            ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
            ContactsContract.CommonDataKinds.Phone.TYPE,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.LABEL,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI
    };


    public List<ContactListItem> loadContacts(Context context) {
        Uri contactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String selection = "((" + DISPLAY_NAME_COMPAT + " NOTNULL) AND ("
                + ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + "=1) AND ("
                + DISPLAY_NAME_COMPAT + " != '' ))";

        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
        Cursor contactCursor = null;
        if (contactUri != null) {
            contactCursor = context.getContentResolver().query(contactUri, CONTACTS_SUMMARY_PROJECTION, selection, null, sortOrder);
        }
        ContactListItem currItem = null;
        List<ContactListItem> items = new ArrayList<>();

        if (contactCursor != null) {
            while (contactCursor.moveToNext()) {
                long id = contactCursor.getLong(contactCursor.getColumnIndexOrThrow(ContactsContract.Data.CONTACT_ID));
                String name = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                String image_uri = contactCursor
                        .getString(contactCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

                String label = ContactsContract.CommonDataKinds.Phone.getTypeLabel(context.getResources(),
                        contactCursor.getInt(contactCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.TYPE)),
                        contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL))).toString();
                String number = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                if (currItem == null || currItem.getContactId() != id) {
                    currItem = new ContactListItem(id, name);
                    items.add(currItem);
                }
                if (image_uri != null) {
                    currItem.setImageUri(image_uri);
                } else {
                    currItem.setImageUri("");
                }
                currItem.addNumbers(label, number);

            }
            contactCursor.close();
        }
        return items;
    }
}
