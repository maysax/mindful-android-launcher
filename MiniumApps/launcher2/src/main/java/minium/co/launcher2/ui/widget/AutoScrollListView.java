package minium.co.launcher2.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by Shahab on 6/1/2016.
 */
public class AutoScrollListView extends ListView {

    public AutoScrollListView(Context context) {
        super(context);
    }

    public AutoScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoScrollListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AutoScrollListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setSelection(getCount());
    }
}
