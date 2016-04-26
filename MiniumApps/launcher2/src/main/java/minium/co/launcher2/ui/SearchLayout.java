package minium.co.launcher2.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import org.androidannotations.annotations.EViewGroup;

/**
 * Created by Shahab on 4/26/2016.
 */
@EViewGroup()
public class SearchLayout extends LinearLayout {

    public SearchLayout(Context context) {
        super(context);
    }

    public SearchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SearchLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();


    }
}
