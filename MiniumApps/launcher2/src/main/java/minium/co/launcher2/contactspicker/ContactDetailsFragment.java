/*
* Copyright (C) 2011 - 2015 by Ngewi Fet <ngewif@gmail.com>
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
*/

package minium.co.launcher2.contactspicker;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import minium.co.launcher2.R;

@EFragment(R.layout.fragment_contact_detail)
public class ContactDetailsFragment extends ListFragment {

    @ViewById
    TextView txtName;

    private OnContactSelectedListener mContactsListener;
    private Cursor mCursor;

    @FragmentArg
    long selectedContactId = 0;

    @FragmentArg
    String contactName;

    @AfterViews
    void afterViews() {
        Uri phonesUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{
                Phone._ID, Phone.DISPLAY_NAME,
                Phone.TYPE, Phone.NUMBER, Phone.LABEL};
        String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
        String[] selectionArgs = new String[]{Long.toString(selectedContactId)};

        mCursor = getActivity().getContentResolver().query(phonesUri,
                projection, selection, selectionArgs, null);

        if (mCursor.moveToFirst()) {
//            txtName.setText(mCursor.getString(mCursor.getColumnIndex(Phone.DISPLAY_NAME)));
        }

        ListAdapter adapter = new PhoneNumbersAdapter(this.getActivity(),
                R.layout.list_item_phone_number, mCursor,
                new String[]{Phone.TYPE, Phone.NUMBER},
                new int[]{R.id.label, R.id.phoneNumber});
        setListAdapter(adapter);
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

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        TextView tv = (TextView) v.findViewById(R.id.phoneNumber);
        String number = tv.getText().toString();
        //String name	= txtName.getText().toString();

        mContactsListener.onContactNumberSelected(selectedContactId, contactName, number);
    }

    class PhoneNumbersAdapter extends SimpleCursorAdapter {

        public PhoneNumbersAdapter(Context context, int layout, Cursor c,
                                   String[] from, int[] to) {
            super(context, layout, c, from, to, 0);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            super.bindView(view, context, cursor);

            TextView tx = (TextView) view.findViewById(R.id.label);
            int type = cursor.getInt(cursor.getColumnIndex(Phone.TYPE));
            String label = cursor.getString(cursor.getColumnIndex(Phone.LABEL));
            label = Phone.getTypeLabel(getResources(), type, label).toString();

            tx.setText(label);
        }
    }
}
