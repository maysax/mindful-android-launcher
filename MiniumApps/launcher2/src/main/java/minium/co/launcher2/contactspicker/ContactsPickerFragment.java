package minium.co.launcher2.contactspicker;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.widget.SimpleCursorAdapter;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;

import java.lang.ref.WeakReference;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import minium.co.core.log.Tracer;
import minium.co.launcher2.R;
import minium.co.launcher2.events.ActionItemUpdateEvent;
import minium.co.launcher2.events.ImeActionDoneEvent;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_contacts_picker)
public class ContactsPickerFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private OnContactSelectedListener mContactsListener;
    private SimpleCursorAdapter mAdapter;
    private String mSearchString = null;

    @SuppressLint("InlinedApi")
    private static String DISPLAY_NAME_COMPAT = Build.VERSION.SDK_INT
            >= Build.VERSION_CODES.HONEYCOMB ?
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
            ContactsContract.Contacts.DISPLAY_NAME;


    private static final String[] CONTACTS_SUMMARY_PROJECTION = new String[]{
            ContactsContract.Contacts._ID,
            DISPLAY_NAME_COMPAT,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
            ContactsContract.Contacts.LOOKUP_KEY
    };

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        getLoaderManager().initLoader(0, null, this);

        mAdapter = new IndexedListAdapter(
                this.getActivity(),
                R.layout.list_item_contacts,
                null,
                new String[]{ContactsContract.Contacts.DISPLAY_NAME},
                new int[]{R.id.display_name});

        setListAdapter(mAdapter);
        getListView().setFastScrollEnabled(true);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ViewHolder viewHolder = (ViewHolder) v.getTag();
        String phoneNumber = viewHolder.phoneNumber.getText().toString();
        String phoneLabel = viewHolder.phoneLabel.getText().toString();
        String name = viewHolder.contactName.getText().toString();

        if (phoneLabel.equals(getString(R.string.label_multiple_numbers))) {
            mContactsListener.onContactNameSelected(id, name);
        } else {
            mContactsListener.onContactNumberSelected(phoneNumber, name);
        }
    }

    @Subscribe
    public void onImeActionDone(ImeActionDoneEvent event) {
        Tracer.d("onImeActionDone " + mSearchString);
        mContactsListener.onContactNumberSelected(mSearchString, mSearchString);
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
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        Uri baseUri;

        if (mSearchString != null) {
            baseUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI,
                    Uri.encode(mSearchString));
        } else {
            baseUri = ContactsContract.Contacts.CONTENT_URI;
        }

        String selection = "((" + DISPLAY_NAME_COMPAT + " NOTNULL) AND ("
                + ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1) AND ("
                + DISPLAY_NAME_COMPAT + " != '' ))";

        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

        return new CursorLoader(getActivity(), baseUri, CONTACTS_SUMMARY_PROJECTION, selection, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        onSpaceAction();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @UiThread(delay = 200)
    public void onSpaceAction() {
        if (mSearchString != null && mSearchString.endsWith(" ") && PhoneNumberUtils.isGlobalPhoneNumber(mSearchString.trim())) {
            if (mAdapter.getCount() == 1) {
                ListView lView = getListView();
                lView.performItemClick(lView.getChildAt(0), 0, lView.getAdapter().getItemId(0));
            } else {
                mContactsListener.onContactNumberSelected(mSearchString.trim(), mSearchString.trim());
            }
        }
    }

    @Subscribe
    public void onQueryTextChange(ActionItemUpdateEvent event) {
        String newText = event.getText();
        String newFilter = !TextUtils.isEmpty(newText) ? newText : null;

        if (mSearchString == null && newFilter == null) {
            return;
        }
        if (mSearchString != null && mSearchString.equals(newFilter)) {
            return;
        }
        mSearchString = newFilter;
        getLoaderManager().restartLoader(0, null, this);
    }

    static class ViewHolder {
        TextView contactName;
        TextView phoneLabel;
        TextView phoneNumber;
        View separator;
        PhoneNumberLookupTask phoneNumberLookupTask;
    }

    class IndexedListAdapter extends SimpleCursorAdapter implements SectionIndexer {

        AlphabetIndexer alphaIndexer;

        public IndexedListAdapter(Context context, int layout, Cursor c,
                                  String[] from, int[] to) {
            super(context, layout, c, from, to, 0);
        }

        @Override
        public Cursor swapCursor(Cursor c) {
            if (c != null) {
                alphaIndexer = new AlphabetIndexer(c,
                        c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME),
                        " ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            }

            return super.swapCursor(c);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater().cloneInContext(getActivity());
                convertView = inflater.inflate(R.layout.list_item_contacts, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.contactName = (TextView) convertView.findViewById(R.id.display_name);
                viewHolder.phoneLabel = (TextView) convertView.findViewById(R.id.phone_label);
                viewHolder.phoneNumber = (TextView) convertView.findViewById(R.id.phone_number);
                viewHolder.separator = convertView.findViewById(R.id.label_separator);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                viewHolder.phoneNumberLookupTask.cancel(true);
            }

            return super.getView(position, convertView, parent);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            super.bindView(view, context, cursor);

            long contactId = cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            viewHolder.phoneNumberLookupTask = new PhoneNumberLookupTask(view);
            viewHolder.phoneNumberLookupTask.execute(contactId);
        }

        @Override
        public int getPositionForSection(int section) {
            return alphaIndexer.getPositionForSection(section);
        }

        @Override
        public int getSectionForPosition(int position) {
            return alphaIndexer.getSectionForPosition(position);
        }

        @Override
        public Object[] getSections() {
            return alphaIndexer == null ? null : alphaIndexer.getSections();
        }
    }

    /**
     * Task for looking up the phone number and displaying it next to the contact.
     * This task holds a weak reference to the view so that if it is recycled while task is running,
     * then the task does nothing.
     */
    private class PhoneNumberLookupTask extends AsyncTask<Long, Void, Void> {
        final WeakReference<View> mViewReference;

        String mPhoneNumber;
        String mPhoneLabel;

        public PhoneNumberLookupTask(View view) {
            mViewReference = new WeakReference<>(view);
        }

        @Override
        protected Void doInBackground(Long... ids) {
            String[] projection = new String[]{
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.TYPE,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.LABEL
            };
            long contactId = ids[0];

            final Cursor phoneCursor = getActivity().getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    projection,
                    ContactsContract.Data.CONTACT_ID + "=?",
                    new String[]{String.valueOf(contactId)},
                    null);

            if (phoneCursor != null && phoneCursor.moveToFirst() && phoneCursor.getCount() == 1) {
                final int contactNumberColumnIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                mPhoneNumber = phoneCursor.getString(contactNumberColumnIndex);
                int type = phoneCursor.getInt(phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.TYPE));
                mPhoneLabel = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));
                mPhoneLabel = ContactsContract.CommonDataKinds.Phone.getTypeLabel(getResources(), type, mPhoneLabel).toString();
                phoneCursor.close();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            View view = mViewReference.get();
            if (view != null) {
                ViewHolder viewHolder = (ViewHolder) view.getTag();
                if (mPhoneNumber != null) {
                    viewHolder.phoneNumber.setText(mPhoneNumber);
                    viewHolder.phoneLabel.setText(mPhoneLabel);
                    viewHolder.phoneLabel.setVisibility(View.VISIBLE);
                    viewHolder.separator.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.phoneLabel.setText(getString(R.string.label_multiple_numbers));
                    viewHolder.phoneNumber.setVisibility(View.INVISIBLE);
                    viewHolder.separator.setVisibility(View.INVISIBLE);
                }

            }
        }
    }
}