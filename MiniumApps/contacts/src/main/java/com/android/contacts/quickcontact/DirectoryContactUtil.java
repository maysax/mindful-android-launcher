package com.android.contacts.quickcontact;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract.Directory;
import android.widget.Toast;

import com.android.contacts.ContactSaveService;
import com.android.contacts.common.model.Contact;
import com.android.contacts.common.model.account.AccountWithDataSet;

import java.util.ArrayList;

import minium.co.contacts.R;

/**
 * Utility class to support adding directory contacts.
 * <p>
 * This class is coupled with {@link QuickContactActivity}, but is left out of
 * QuickContactActivity.java to avoid ballooning the size of the file.
 */
public class DirectoryContactUtil {

    public static boolean isDirectoryContact(Contact contactData) {
        // Not a directory contact? Nothing to fix here
        if (contactData == null || !contactData.isDirectoryEntry()) return false;

        // No export support? Too bad
        return contactData.getDirectoryExportSupport() != Directory.EXPORT_SUPPORT_NONE;
    }

    public static void createCopy(
            ArrayList<ContentValues> values, AccountWithDataSet account,
            Context context) {
        Toast.makeText(context, R.string.toast_making_personal_copy,
                Toast.LENGTH_LONG).show();
        Intent serviceIntent = ContactSaveService.createNewRawContactIntent(
                context, values, account,
                QuickContactActivity.class, Intent.ACTION_VIEW);
        context.startService(serviceIntent);
    }
}
