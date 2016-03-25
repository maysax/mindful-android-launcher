package minium.co.messages.ui.base;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import minium.co.core.ui.CoreActivity;

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
}
