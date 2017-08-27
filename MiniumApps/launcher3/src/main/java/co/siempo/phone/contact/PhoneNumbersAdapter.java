package co.siempo.phone.contact;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import co.siempo.phone.R;

/**
 * Created by shahab on 2/16/17.
 */

@SuppressWarnings("ALL")
public class PhoneNumbersAdapter extends SimpleCursorAdapter {

    public PhoneNumbersAdapter(Context context, int layout, Cursor c,
                               String[] from, int[] to) {
        super(context, layout, c, from, to, 0);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        TextView tx = (TextView) view.findViewById(R.id.txtTitle);
        int type = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
        String label = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));
        label = ContactsContract.CommonDataKinds.Phone.getTypeLabel(context.getResources(), type, label).toString();

        tx.setText(label);
    }
}
