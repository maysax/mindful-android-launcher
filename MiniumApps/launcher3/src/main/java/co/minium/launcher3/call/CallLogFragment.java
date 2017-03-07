package co.minium.launcher3.call;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.androidannotations.annotations.EFragment;

import co.minium.launcher3.R;

/**
 * Created by Shahab on 5/26/2016.
 */
@EFragment(R.layout.fragment_call_log)
public class CallLogFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private OnCallLogSelectedListener mCallLogListener;
    private SimpleCursorAdapter mAdapter;
    private String mSearchString = null;

//    @Override
//    public void onStart() {
//        super.onStart();
//        EventBus.getDefault().register(this);
//    }
//
//    @Override
//    public void onStop() {
//        EventBus.getDefault().unregister(this);
//        super.onStop();
//    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        getLoaderManager().initLoader(0, null, this);

        mAdapter = new IndexedListAdapter(this.getActivity(),
                R.layout.item_call_log, null, new String[]{CallLog.Calls.CACHED_NAME}, new int[]{R.id.txtTitle});

        setListAdapter(mAdapter);
        getListView().setFastScrollEnabled(true);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Cursor cursor = mAdapter.getCursor();
        String contactNumber = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contactNumber)));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = CallLog.Calls.DATE + " DESC";
        return new CursorLoader(getActivity(), CallLog.Calls.CONTENT_URI, null, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    static class ViewHolder {
        TextView txtTitle;
        TextView txtCallType;
        TextView txtCallDate;
        TextView txtCallDuration;
    }

    class IndexedListAdapter extends SimpleCursorAdapter {

        AlphabetIndexer alphabetIndexer;

        public IndexedListAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
            super(context, layout, c, from, to, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater().cloneInContext(getActivity());
                convertView = inflater.inflate(R.layout.item_call_log, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
                viewHolder.txtCallType = (TextView) convertView.findViewById(R.id.txtCallType);
                viewHolder.txtCallDate = (TextView) convertView.findViewById(R.id.txtCallDate);
                viewHolder.txtCallDuration = (TextView) convertView.findViewById(R.id.txtCallDuration);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            return super.getView(position, convertView, parent);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            super.bindView(view, context, cursor);

            ViewHolder viewHolder = (ViewHolder) view.getTag();

            String title = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
            if (TextUtils.isEmpty(title)) {
                title = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            }

            viewHolder.txtTitle.setText(title);

            String callTypeStr = cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE));
            int callType = Integer.parseInt(callTypeStr);

            switch (callType) {
                case CallLog.Calls.OUTGOING_TYPE:
                    viewHolder.txtCallType.setText(R.string.icon_outgoingCall);
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    viewHolder.txtCallType.setText(R.string.icon_incomingCall);
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    viewHolder.txtCallType.setText(R.string.icon_missedCall);
                    break;
            }

            viewHolder.txtCallDate.setText(DateUtils.getRelativeTimeSpanString(Long.parseLong(
                    cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE)))));

            viewHolder.txtCallDuration.setText(getString(R.string.format_duration,
                    DateUtils.formatElapsedTime(Long.parseLong(
                            cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION))))));


        }
    }
}
