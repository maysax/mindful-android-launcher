package com.moez.QKSMS.ui.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import minium.co.core.ui.CoreActivity;

/**
 * Created by shahab on 3/25/16.
 */
public abstract class ClickyViewHolder<DataType> extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    public RecyclerCursorAdapter.ItemClickListener<DataType> mClickListener;
    public DataType mData;
    public CoreActivity mContext;

    public ClickyViewHolder(CoreActivity context, View itemView) {
        super(itemView);
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {
        if (mClickListener != null) {
            mClickListener.onItemClick(mData, v);
        }
    }
}
