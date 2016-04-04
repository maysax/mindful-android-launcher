package com.moez.QKSMS.data;

import android.text.TextUtils;

import java.util.ArrayList;

public class ContactList extends ArrayList<Contact> {

    /**
     * Returns a ContactList for the corresponding recipient ids passed in. This method will
     * create the contact if it doesn't exist, and would inject the recipient id into the contact.
     */
    public static ContactList getByIds(String spaceSepIds, boolean canBlock) {
        ContactList list = new ContactList();
        for (RecipientIdCache.Entry entry : RecipientIdCache.getAddresses(spaceSepIds)) {
            if (entry != null && !TextUtils.isEmpty(entry.number)) {
                Contact contact = Contact.get(entry.number, canBlock);
                contact.setRecipientId(entry.id);
                list.add(contact);
            }
        }
        return list;
    }

    public String formatNames(String separator) {
        String[] names = new String[size()];
        int i = 0;
        for (Contact c : this) {
            names[i++] = c.getName();
        }
        return TextUtils.join(separator, names);
    }
}
