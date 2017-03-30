package co.minium.launcher3.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import co.minium.launcher3.R;

/**
 * Created by Shahab on 3/30/2017.
 */

public class SiempoSlider extends View {

    private int mThumbX;
    private int mThumbY;

    private int mCircleCenterX;
    private int mCircleCenterY;
    private int mCircleRadius;

    private Drawable mThumbImage;
    private int mPadding;
    private int mThumbSize;
    private int mThumbColor;
    private int mBorderColor;
    private int mBorderThickness;
    private double mStartAngle;
    private double mAngle = mStartAngle;
    private boolean mIsThumbSelected = false;
    private int mTitleSize, mSubTitleSize, mCenterTextSize;
    private int mSliderActiveColor, mSliderInactiveColor, mTitleColor, mSubTitleColor, mCenterTextColor;
    private int mInitPosition;
    private boolean isThumbVisible;


    /**
     * {@code true} if the user clicked on the pointer to start the move mode.
     * {@code false} once the user stops touching the screen.
     *
     * @see #onTouchEvent(MotionEvent)
     */
    private boolean mUserIsMovingPointer = false;

    /**
     * Number of pixels the origin of this view is moved in X- and Y-direction.
     * <p>
     * <p>
     * We use the center of this (quadratic) View as origin of our internal
     * coordinate system. Android uses the upper left corner as origin for the
     * View-specific coordinate system. So this is the value we use to translate
     * from one coordinate system to the other.
     * </p>
     * <p>
     * <p>
     * Note: (Re)calculated in {@link #onMeasure(int, int)}.
     * </p>
     *
     * @see #onDraw(Canvas)
     */
    private float mTranslationOffset;

    private Paint mPaint = new Paint();
    private OnSiempoSliderChangeListener mListener;

    public SiempoSlider(Context context) {
        this(context, null);
    }

    public SiempoSlider(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SiempoSlider(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SiempoSlider(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.siempo_slider, defStyleAttr, defStyleRes);

        mStartAngle = a.getFloat(R.styleable.siempo_slider_start_angle, (float) Math.PI / 2);
        mAngle = a.getFloat(R.styleable.siempo_slider_angle, (float) Math.PI / 2);
        mThumbSize = a.getDimensionPixelSize(R.styleable.siempo_slider_thumb_size, 50);
        mThumbColor = a.getColor(R.styleable.siempo_slider_thumb_color, Color.BLUE);
        mBorderThickness = a.getDimensionPixelSize(R.styleable.siempo_slider_border_thickness, 20);
        mBorderColor = a.getColor(R.styleable.siempo_slider_border_color, Color.GREEN);
        mThumbImage = a.getDrawable(R.styleable.siempo_slider_thumb_image);
        mTitleSize = a.getDimensionPixelSize(R.styleable.siempo_slider_title_size, 25);
        mSubTitleSize = a.getDimensionPixelSize(R.styleable.siempo_slider_subTitle_size, 15);
        mCenterTextSize = a.getDimensionPixelSize(R.styleable.siempo_slider_center_text_size, 20);
        mInitPosition = a.getInteger(R.styleable.siempo_slider_init_position, 0);
        isThumbVisible = a.getBoolean(R.styleable.siempo_slider_thumb_visible, true);
        mSliderActiveColor = a.getColor(R.styleable.siempo_slider_slider_active_color, Color.DKGRAY);
        mSliderInactiveColor = a.getColor(R.styleable.siempo_slider_slider_inactive_color, Color.WHITE);
        mTitleColor = a.getColor(R.styleable.siempo_slider_title_color, Color.CYAN);
        mSubTitleColor = a.getColor(R.styleable.siempo_slider_subTitle_color, Color.MAGENTA);
        mCenterTextColor = a.getColor(R.styleable.siempo_slider_center_text_color, Color.YELLOW);

        int all = getPaddingLeft() + getPaddingRight() + getPaddingBottom() + getPaddingTop() + getPaddingEnd() + getPaddingStart();
        mPadding = all / 6;

        a.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // use smaller dimension for calculations (depends on parent size)
        int smallerDim = w > h ? h : w;

        // find circle's rectangle points
        int largestCenteredSquareLeft = (w - smallerDim) / 2;
        int largestCenteredSquareTop = (h - smallerDim) / 2;
        int largestCenteredSquareRight = largestCenteredSquareLeft + smallerDim;
        int largestCenteredSquareBottom = largestCenteredSquareTop + smallerDim;

        // save circle coordinates and radius in fields
        mCircleCenterX = largestCenteredSquareRight / 2 + (w - largestCenteredSquareRight) / 2;
        mCircleCenterY = largestCenteredSquareBottom / 2 + (h - largestCenteredSquareBottom) / 2;
        mCircleRadius = smallerDim / 2 - mBorderThickness / 2 - mPadding;

        // works well for now, should we call something else here?
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // outer circle (ring)
        mPaint.setColor(mBorderColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mBorderThickness);
        mPaint.setAntiAlias(true);
        canvas.drawCircle(mCircleCenterX, mCircleCenterY, mCircleRadius, mPaint);

        // find thumb position
        mThumbX = (int) (mCircleCenterX + mCircleRadius * Math.cos(mAngle));
        mThumbY = (int) (mCircleCenterY - mCircleRadius * Math.sin(mAngle));

        if (mThumbImage != null) {
            // draw png
            mThumbImage.setBounds(mThumbX - mThumbSize / 2, mThumbY - mThumbSize / 2, mThumbX + mThumbSize / 2, mThumbY + mThumbSize / 2);
            mThumbImage.draw(canvas);
        } else {
            // draw colored circle
            mPaint.setColor(mThumbColor);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(mThumbX, mThumbY, mThumbSize, mPaint);
        }
    }

    /**
     * Invoked when slider starts moving or is currently moving. This method calculates and sets position and angle of the thumb.
     *
     * @param touchX Where is the touch identifier now on X axis
     * @param touchY Where is the touch identifier now on Y axis
     */
    private void updateSliderState(int touchX, int touchY) {
        int distanceX = touchX - mCircleCenterX;
        int distanceY = mCircleCenterY - touchY;
        //noinspection SuspiciousNameCombination
        double c = Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
        mAngle = Math.acos(distanceX / c);
        if (distanceY < 0) {
            mAngle = -mAngle;
        }

        if (mListener != null) {
            // notify slider moved listener of the new position which should be in [0..1] range
            // mListener.onSliderMoved ((mAngle - mStartAngle) / (2 * Math.PI));
        }
    }

    /**
     * Position setter. This method should be used to manually position the slider thumb.<br>
     * Note that counterclockwise {@link #mStartAngle} is used to determine the initial thumb position.
     *
     * @param pos Value between 0 and 1 used to calculate the angle. {@code Angle = StartingAngle + pos * 2 * Pi}<br>
     *            Note that angle will not be updated if the position parameter is not in the valid range [0..1]
     */
    public void setPosition(double pos) {
        if (pos >= 0 && pos <= 1) {
            mAngle = mStartAngle + pos * 2 * Math.PI;
        }
    }

    /**
     * Saves a new slider moved listner. Set {@link OnSiempoSliderChangeListener} to {@code null} to remove it.
     *
     * @param listener Instance of the slider moved listener, or null when removing it
     */
    public void setOnSliderMovedListener(OnSiempoSliderChangeListener listener) {
        mListener = listener;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                // start moving the thumb (this is the first touch)
                int x = (int) ev.getX();
                int y = (int) ev.getY();
                if (x < mThumbX + mThumbSize && x > mThumbX - mThumbSize && y < mThumbY + mThumbSize && y > mThumbY - mThumbSize) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    mIsThumbSelected = true;
                    updateSliderState(x, y);
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                // still moving the thumb (this is not the first touch)
                if (mIsThumbSelected) {
                    int x = (int) ev.getX();
                    int y = (int) ev.getY();
                    updateSliderState(x, y);
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                // finished moving (this is the last touch)
                getParent().requestDisallowInterceptTouchEvent(false);
                mIsThumbSelected = false;
                break;
            }
        }

        // redraw the whole component
        invalidate();
        return true;
    }

    public interface OnSiempoSliderChangeListener {
        void onSliderMoved(SiempoSlider slider, int progress, boolean fromUser);
        void onStartSliding(SiempoSlider slider);
        void onStopSliding(SiempoSlider slider);
    }
}
