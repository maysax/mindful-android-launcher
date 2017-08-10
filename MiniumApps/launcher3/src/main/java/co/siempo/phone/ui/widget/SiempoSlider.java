package co.siempo.phone.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import co.siempo.phone.R;

/**
 * Created by Shahab on 3/30/2017.
 */

public class SiempoSlider extends View {

    private static final String STATE_PARENT = "parent";
    private static final String STATE_ANGLE = "angle";

    private OnSliderChangeListener listener;

    /**
     * {@code Paint} instance used to draw the color wheel.
     */
    private Paint mColorWheelPaint;

    /**
     * {@code Paint} instance used to draw the pointer's "halo".
     */
    private Paint mPointerHaloPaint;

    /**
     * {@code Paint} instance used to draw the pointer (the selected color).
     */
    private Paint mPointerColor;

    /**
     * The stroke width used to paint the color wheel (in pixels).
     */
    private int mColorWheelStrokeWidth;

    /**
     * The radius of the pointer (in pixels).
     */
    private float mPointerRadius;

    /**
     * The rectangle enclosing the color wheel.
     */
    private RectF mColorWheelRectangle = new RectF();

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

    /**
     * Radius of the color wheel in pixels.
     * <p>
     * <p>
     * Note: (Re)calculated in {@link #onMeasure(int, int)}.
     * </p>
     */
    private float mColorWheelRadius;

    /**
     * The pointer's position expressed as angle (in rad).
     */
    private float mAngle;
    private Paint titlePaint;
    private String titleText;
    private Paint subTitlePaint;
    private String subTitleText = "minutes";
    private Paint textPaint;
    private String text = "Done";
    private int max = 100;
    private SweepGradient s;
    private Paint mArcColor;
    private int wheel_color, unactive_wheel_color, pointer_color, pointer_halo_color, title_size, title_color, subTitle_size, subTitle_color, text_size, text_color;
    private int init_position = -1;
    private boolean block_end = false;
    private float lastX;
    private int last_radians = 0;
    private boolean block_start = false;

    private int arc_finish_radians = 360;
    private int start_arc = 270;

    private float[] pointerPosition;
    private RectF mColorCenterHaloRectangle = new RectF();
    private int end_wheel;

    private boolean showTitle = true;
    private Rect titleBounds = new Rect();
    private Rect subTitleBounds = new Rect();
    private Rect textBounds = new Rect();

    private Drawable mThumbImage;
    private int mThumbSize;
    private boolean showThumb;

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
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.siempo_slider, defStyleAttr, defStyleRes);

    }

    public interface OnSliderChangeListener {
        void onSliderChanged(SiempoSlider slider, int progress, boolean fromUser);

        void onStartSliderTouch(SiempoSlider slider);

        void onStopSliderTouch(SiempoSlider slider);
    }
}
