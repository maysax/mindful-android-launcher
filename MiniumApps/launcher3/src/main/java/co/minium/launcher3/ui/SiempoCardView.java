package co.minium.launcher3.ui;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

import minium.co.core.util.UIUtils;

/**
 * Created by Shahab on 2/17/2017.
 */

public class SiempoCardView extends CardView {
    public SiempoCardView(Context context) {
        super(context);
    }

    public SiempoCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SiempoCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(UIUtils.dpToPx(getContext(), 240), MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
