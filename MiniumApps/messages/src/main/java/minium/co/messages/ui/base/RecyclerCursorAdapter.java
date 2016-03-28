package minium.co.messages.ui.base;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.HashMap;

import minium.co.core.ui.CoreActivity;
import minium.co.messages.common.util.CursorUtils;

/**
 * Created by shahab on 3/25/16.
 */
public abstract class RecyclerCursorAdapter<VH extends RecyclerView.ViewHolder, DataType>
        extends RecyclerView.Adapter<VH> {

    public interface ItemClickListener<DataType> {
        void onItemClick(DataType object, View view);

        void onItemLongClick(DataType object, View view);
    }

    protected CoreActivity mContext;
    protected Cursor mCursor;

    public RecyclerCursorAdapter(CoreActivity context) {
        this.mContext = context;
    }

    protected HashMap<Long, DataType> mSelectedItems = new HashMap<>();

    protected ItemClickListener<DataType> mItemClickListener;

    public Cursor getCursor() {
        return mCursor;
    }

    public int getCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    @Override
    public int getItemCount() {
        return CursorUtils.isValid(mCursor) ? mCursor.getCount() : 0;
    }
}
