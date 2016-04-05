package com.moez.QKSMS.ui.search;

import android.view.View;

import com.moez.QKSMS.R;
import com.moez.QKSMS.ui.base.ClickyViewHolder;
import com.moez.QKSMS.ui.base.QKActivity;
import com.moez.QKSMS.ui.view.QKTextView;

public class SearchViewHolder extends ClickyViewHolder<SearchData> {

    protected View root;
    protected QKTextView name;
    protected QKTextView date;
    protected QKTextView snippet;

    public SearchViewHolder(QKActivity context, View view) {
        super(context, view);

        root = view;
        name = (QKTextView) view.findViewById(R.id.search_name);
        date = (QKTextView) view.findViewById(R.id.search_date);
        snippet = (QKTextView) view.findViewById(R.id.search_snippet);
    }
}
