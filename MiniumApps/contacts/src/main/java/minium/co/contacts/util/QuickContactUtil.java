package minium.co.contacts.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.ContactsContract;

/**
 * Created by Shahab on 4/21/2016.
 */
public class QuickContactUtil {

    public static Intent composeQuickContactsIntent(Context context, Rect target,
                                                    Uri lookupUri, int mode, String[] excludeMimes) {
        // When launching from an Activiy, we don't want to start a new task, but otherwise
        // we *must* start a new task.  (Otherwise startActivity() would crash.)
        Context actualContext = context;
        while ((actualContext instanceof ContextWrapper)
                && !(actualContext instanceof Activity)) {
            actualContext = ((ContextWrapper) actualContext).getBaseContext();
        }
        final int intentFlags = ((actualContext instanceof Activity)
                ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                // Workaround for b/16898764. Declaring singleTop in manifest doesn't work.
                | Intent.FLAG_ACTIVITY_SINGLE_TOP;

        // Launch pivot dialog through intent for now
        final Intent intent = new Intent(ContactsContract.QuickContact.ACTION_QUICK_CONTACT).addFlags(intentFlags);

        // NOTE: This logic and rebuildManagedQuickContactsIntent() must be in sync.
        intent.setData(lookupUri);
        intent.setSourceBounds(target);
        intent.putExtra(ContactsContract.QuickContact.EXTRA_MODE, mode);
        intent.putExtra(ContactsContract.QuickContact.EXTRA_EXCLUDE_MIMES, excludeMimes);
        return intent;
    }
}
