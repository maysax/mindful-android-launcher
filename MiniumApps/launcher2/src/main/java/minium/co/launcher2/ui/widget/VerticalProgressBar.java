package minium.co.launcher2.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import java.lang.ref.WeakReference;

import cn.dreamtobe.percentsmoothhandler.ISmoothTarget;
import cn.dreamtobe.percentsmoothhandler.SmoothHandler;
import minium.co.launcher2.R;

/**
 * Created by Shahab on 5/12/2016.
 */
public class VerticalProgressBar extends View implements ISmoothTarget {

    // ColorInt
    private int fillColor;

    // ColorInt
    private int backgroundColor;

    private Paint fillPaint;
    private Paint backgroundPaint;

    private float percent;
    private boolean isFlat;

    private SmoothHandler smoothHandler;

    public VerticalProgressBar(Context context) {
        super(context);
        init(context, null);
    }

    public VerticalProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public VerticalProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VerticalProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(final Context context, AttributeSet attrs) {
        if (context == null || attrs == null) {
            return;
        }


        TypedArray typedArray = null;
        try {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.VerticalProgressBar);
            percent = typedArray.getFloat(R.styleable.VerticalProgressBar_vpb_percent, 0);
            fillColor = typedArray.getColor(R.styleable.VerticalProgressBar_vpb_fill_color, 0);
            backgroundColor = typedArray.getColor(R.styleable.VerticalProgressBar_vpb_background_color, 0);
            isFlat = typedArray.getBoolean(R.styleable.VerticalProgressBar_vpb_flat, false);
        } finally {
            if (typedArray != null) {
                typedArray.recycle();
            }
        }

        fillPaint = new Paint();
        fillPaint.setColor(fillColor);
        fillPaint.setAntiAlias(true);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setAntiAlias(true);

    }

    /**
     * @param fillColor ColorInt
     */
    public void setFillColor(final int fillColor) {
        if (this.fillColor != fillColor) {
            this.fillColor = fillColor;
            this.fillPaint.setColor(fillColor);
            invalidate();
        }

    }

    /**
     * @param backgroundColor ColorInt
     */
    public void setBackgroundColor(final int backgroundColor) {
        if (this.backgroundColor != backgroundColor) {
            this.backgroundColor = backgroundColor;
            this.backgroundPaint.setColor(backgroundColor);
            invalidate();
        }
    }

    public int getFillColor() {
        return this.fillColor;
    }

    public int getBackgroundColor() {
        return this.backgroundColor;
    }

    public float getPercent() {
        return this.percent;
    }

    /**
     * @param percent FloatRange(from = 0.0, to = 1.0)
     */
    public void setPercent(float percent) {
        percent = Math.min(1, percent);
        percent = Math.max(0, percent);

        if (smoothHandler != null) {
            smoothHandler.commitPercent(percent);
        }

        if (this.percent != percent) {
            this.percent = percent;
            invalidate();
        }

    }

    @Override
    public void setSmoothPercent(float percent) {
        getSmoothHandler().loopSmooth(percent);
    }

    @Override
    public void setSmoothPercent(float percent, long durationMillis) {
        getSmoothHandler().loopSmooth(percent, durationMillis);
    }

    private SmoothHandler getSmoothHandler() {
        if (smoothHandler == null) {
            smoothHandler = new SmoothHandler(new WeakReference<ISmoothTarget>(this));
        }
        return smoothHandler;
    }

    /**
     * @param flat Whether the right side of progress is round or flat
     */
    public void setFlat(final boolean flat) {
        if (this.isFlat != flat) {
            this.isFlat = flat;

            invalidate();
        }
    }

    private final RectF rectF = new RectF();
//    private final Path regionPath = new Path();
//    private final Path fillPath = new Path();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float drawPercent = percent;
        canvas.save();

        final int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        final int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();

        float fillHeight = drawPercent * height;

        rectF.left = 0;
        rectF.top = 0;
        rectF.right = width;
        rectF.bottom = height;

        if (backgroundColor != 0) canvas.drawRect(rectF, backgroundPaint);

        // draw fill
        try {
            if (fillColor != 0 && fillHeight > 0) {
                if (fillHeight == height) {
                    rectF.top = height - fillHeight;
                    canvas.drawRect(rectF, fillPaint);
                    return;
                }

                canvas.save();
                rectF.top = height - fillHeight;
                canvas.clipRect(rectF);
                canvas.drawRect(rectF, fillPaint);
            }
        } finally {
            canvas.restore();
        }
    }
}
