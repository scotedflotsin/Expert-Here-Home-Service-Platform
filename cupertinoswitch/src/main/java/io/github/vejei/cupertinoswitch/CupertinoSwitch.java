package io.github.vejei.cupertinoswitch;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;


/**
 * An iOS-style switch.
 * Used to toggle the on/off state of a single setting.
 */
public class CupertinoSwitch extends View {
    /** The default width of switch, in dp. */
    private static final @Dimension(unit = Dimension.DP) int DEFAULT_SWITCH_WIDTH = 48;

    /** The default switch animation duration, in milliseconds. */
    private static final int DEFAULT_SWITCH_DURATION = 250;

    /** The default slider shadow radius, in dp */
    private static final @Dimension(unit = Dimension.DP) int DEFAULT_SLIDER_SHADOW_RADIUS = 4;

    /**
     * The offset between the slider and the track, used to calculate the radius of the slider,
     * if not specified.
     */
    private static final @Dimension(unit = Dimension.DP) int SLIDER_OFFSET = 2;

    /** The minimum radius of the slider. */
    private static final @Dimension(unit = Dimension.DP) int MIN_SLIDER_RADIUS = 4;

    private static final String PROPERTY_NAME_TRACK_COLOR = "track_color";

    private static final int TOUCH_MODE_IDLE = 0;
    private static final int TOUCH_MODE_DOWN = 1;
    private static final int TOUCH_MODE_DRAGGING = 2;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint sliderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int touchMode;
    private float touchX;

    /** Top bound for drawing the switch track and slider. */
    private int switchTop;

    /** Left bound for drawing the switch track and slider. */
    private int switchLeft;

    /** Right bound for drawing the switch track and slider. */
    private int switchRight;

    /** Bottom bound for drawing the switch track and slider. */
    private int switchBottom;

    /** Width required to draw the switch track and slider. */
    private int switchWidth;

    /** Height required to draw the switch track and slider. */
    private int switchHeight;

    /** The switch animation duration, in milliseconds. */
    private int switchDuration;

    /**
     * The color of track when the switch is on, the default is {@link android.graphics.Color#GRAY},
     * if not specified.
     */
    private int trackOnColor;

    /**
     * The color of track when the switch is off, the default is {@link android.graphics.Color#GRAY},
     * if not specified.
     */
    private int trackOffColor;

    /**
     * The color of the slider, the default is {@link android.graphics.Color#WHITE}, if not specified.
     */
    private int sliderOnColor;
    private int sliderOffColor;
    private int sliderRadius;
    private boolean sliderShadowEnabled;
    private int sliderShadowColor;
    private int sliderShadowRadius;

    private boolean checked;

    private int trackColor;

    /** The round corner radius of track, the default size is half of the {@link #switchHeight}. */
    private int trackCornerRadius;

    private float sliderCenterX;
    private float sliderCenterY;
    private float sliderCenterStartX;
    private float sliderCenterEndX;
    private int sliderMoveRange;

    private final ValueAnimator sliderAnimator = new ValueAnimator();
    private final ValueAnimator trackAnimator = new ValueAnimator();
    private final ArgbEvaluator trackColorEvaluator = new ArgbEvaluator();

    /** {@link OnStateChangeListener} */
    private OnStateChangeListener onStateChangeListener;

    public CupertinoSwitch(Context context) {
        this(context, null);
    }

    public CupertinoSwitch(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.cupertinoSwitchStyle);
    }

    public CupertinoSwitch(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CupertinoSwitch,
                defStyleAttr, 0);

        int defaultSwitchWidthPixels = (int) ViewUtils.dpToPx(context, DEFAULT_SWITCH_WIDTH);
        int sliderOffset = (int) ViewUtils.dpToPx(context, SLIDER_OFFSET);
        int defaultSliderShadowRadiusPixels = (int) ViewUtils.dpToPx(context,
                DEFAULT_SLIDER_SHADOW_RADIUS);

        switchWidth = typedArray.getDimensionPixelSize(R.styleable.CupertinoSwitch_switchWidth,
                defaultSwitchWidthPixels);

        switchHeight = typedArray.getDimensionPixelSize(R.styleable.CupertinoSwitch_switchHeight,
                switchWidth / 2);
        switchHeight = (int) constrain(switchHeight, 0, switchWidth);
        trackCornerRadius = switchHeight / 2;

        switchDuration = typedArray.getInt(R.styleable.CupertinoSwitch_switchDuration,
                DEFAULT_SWITCH_DURATION);


//        sliderColor = typedArray.getColor(R.styleable.CupertinoSwitch_sliderColor, Color.WHITE);

// In the constructor, initialize the new attributes
        sliderOnColor = typedArray.getColor(R.styleable.CupertinoSwitch_sliderOnColor, Color.WHITE);
        sliderOffColor = typedArray.getColor(R.styleable.CupertinoSwitch_sliderOffColor, Color.WHITE);



        sliderRadius = typedArray.getDimensionPixelSize(R.styleable.CupertinoSwitch_sliderRadius,
                trackCornerRadius - sliderOffset);
        // Constrain the slider radius to [MIN_SLIDER_RADIUS, trackCornerRadius].
        sliderRadius = (int) constrain(sliderRadius, ViewUtils.dpToPx(context, MIN_SLIDER_RADIUS),
                trackCornerRadius);

        sliderShadowEnabled = typedArray.getBoolean(R.styleable.CupertinoSwitch_sliderShadowEnabled,
                true);
        sliderShadowColor = typedArray.getColor(R.styleable.CupertinoSwitch_sliderShadowColor,
                Color.parseColor("#dddddd"));
        sliderShadowRadius = typedArray.getDimensionPixelSize(
                R.styleable.CupertinoSwitch_sliderShadowRadius, defaultSliderShadowRadiusPixels);

        trackOnColor = typedArray.getColor(R.styleable.CupertinoSwitch_trackOnColor, Color.GRAY);
        trackOffColor = typedArray.getColor(R.styleable.CupertinoSwitch_trackOffColor, Color.GRAY);

        checked = typedArray.getBoolean(R.styleable.CupertinoSwitch_android_checked, false);
        trackColor = !checked ? trackOffColor : trackOnColor;

        setEnabled(typedArray.getBoolean(R.styleable.CupertinoSwitch_android_enabled, isEnabled()));

        typedArray.recycle();

        paint.setStyle(Paint.Style.FILL);
        sliderPaint.setStyle(Paint.Style.FILL);
//        sliderPaint.setColor(sliderColor);
        if (sliderShadowEnabled) {
            sliderPaint.setShadowLayer(sliderShadowRadius, 0, 0, sliderShadowColor);
        }

        sliderAnimator.setDuration(switchDuration);
        sliderAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                sliderCenterX = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        sliderAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                checked = !checked;

                if (onStateChangeListener != null) {
                    onStateChangeListener.onChanged(CupertinoSwitch.this, checked);

                    if (checked) {
                        onStateChangeListener.onSwitchOn(CupertinoSwitch.this);
                    } else {
                        onStateChangeListener.onSwitchOff(CupertinoSwitch.this);
                    }
                }
            }
        });

        trackAnimator.setDuration(switchDuration);
        trackAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                trackColor = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = switchWidth;
        int height = switchHeight;

        int desiredWidth;
        int desiredHeight;

        if (sliderShadowEnabled) {
            float radius = sliderShadowRadius + sliderRadius;
            if (radius * 2 > switchHeight) {
                width += sliderShadowRadius * 2;
                height += sliderShadowRadius * 2;
            }
        }

        desiredWidth = Math.max(width, getSuggestedMinimumWidth()) + getPaddingLeft()
                + getPaddingRight();
        desiredHeight = Math.max(height, getSuggestedMinimumHeight()) + getPaddingTop()
                + getPaddingBottom();
        setMeasuredDimension(resolveSize(desiredWidth, widthMeasureSpec),
                resolveSize(desiredHeight, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int offset = 0;
        if (sliderShadowEnabled) {
            float radius = sliderShadowRadius + sliderRadius;
            if (radius * 2 > switchHeight) {
                offset = sliderShadowRadius;
            } else {
                offset = 0;
            }
        }

        if (!ViewUtils.isLayoutRtl(this)) {
            switchRight = getWidth() - getPaddingRight() - offset;
            switchLeft = switchRight - switchWidth;
        } else {
            switchLeft = getPaddingLeft() + offset;
            switchRight = switchLeft + switchWidth;
        }

        switchTop = (getPaddingTop() + getHeight() - getPaddingBottom()) / 2 - switchHeight / 2;
        switchBottom = switchTop + switchHeight;

        if (ViewUtils.isLayoutRtl(this)) {
            sliderCenterStartX = switchLeft + switchWidth - switchHeight / 2f;
            sliderCenterEndX = switchLeft + switchHeight / 2f;
        } else {
            sliderCenterStartX = switchLeft + switchHeight / 2f;
            sliderCenterEndX = switchLeft + switchWidth - switchHeight / 2f;
        }

        sliderCenterX = !checked ? sliderCenterStartX : sliderCenterEndX;
        sliderCenterY = switchTop + switchHeight / 2f;

        sliderMoveRange = switchWidth - sliderRadius * 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(trackColor);

        canvas.drawRoundRect(switchLeft, switchTop, switchRight, switchBottom, trackCornerRadius,
                trackCornerRadius, paint);

        int currentSliderColor = checked ? sliderOnColor : sliderOffColor;
        sliderPaint.setColor(currentSliderColor);

        canvas.drawCircle(sliderCenterX, sliderCenterY, sliderRadius, sliderPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                touchX = event.getX();
                touchMode = TOUCH_MODE_DOWN;
                return containPoint(event.getX(), event.getY());
            case MotionEvent.ACTION_MOVE:
                switch (touchMode) {
                    case TOUCH_MODE_IDLE:
                        break;
                    case TOUCH_MODE_DOWN: // start dragging
                        touchMode = TOUCH_MODE_DRAGGING;
                        touchX = event.getX();
                        break;
                    case TOUCH_MODE_DRAGGING:// dragging
                        float eventX = event.getX();
                        float offset;
                        float colorOffset;
                        boolean toEnd;

                        if (ViewUtils.isLayoutRtl(this)) {
                            eventX = constrain(eventX, sliderCenterEndX, sliderCenterStartX);
                            offset = -(eventX - touchX) / sliderMoveRange;
                            toEnd = eventX < touchX;
                        } else {
                            eventX = constrain(eventX, sliderCenterStartX, sliderCenterEndX);
                            offset = (eventX - touchX) / sliderMoveRange;
                            toEnd = eventX > touchX;
                        }

                        if (toEnd) {
                            colorOffset = offset;
                        } else {
                            colorOffset = 1 + offset;
                        }

                        sliderCenterX = (int) eventX;
                        trackColor = (int) trackColorEvaluator.evaluate(colorOffset, trackOffColor,
                                trackOnColor);
                        invalidate();
                        break;
                }
                return true;
            case MotionEvent.ACTION_UP:
                if ((event.getEventTime() - event.getDownTime()) <= ViewConfiguration.getTapTimeout()) {
                    performClick();
                } else {
                    if (touchMode == TOUCH_MODE_DRAGGING) {
                        float offset = Math.abs((sliderCenterX - sliderCenterStartX) / sliderMoveRange);
                        boolean newState = offset > 0.5f;
                        setChecked(newState);
                    }
                    touchMode = TOUCH_MODE_IDLE;
                }
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    @Override
    public boolean performClick() {
        setChecked(!checked);
        return super.performClick();
    }

    /**
     * Check whether the specified point is inside the drawn content.
     * @param x The x-coordinate of specified point
     * @param y The y-coordinate of specified point
     * @return true if specified point inside the drawn content, false otherwise.
     */
    private boolean containPoint(float x, float y) {
        float semicircleRadius = switchHeight / 2f;
        float leftSemicircleCenterX = switchLeft + semicircleRadius;
        float leftSemicircleCenterY = switchTop + semicircleRadius;
        float rightSemicircleCenterX = switchRight - semicircleRadius;
        RectF centerRect = new RectF(switchLeft + semicircleRadius, switchTop,
                switchRight - semicircleRadius, switchBottom);

        double leftDistance;
        double rightDistance;
        boolean inSemicircle;
        boolean inCenterRect;

        if (x < switchLeft || x > switchRight || y < switchTop || y > switchBottom) {
            return false;
        }

        // The distance from the point to the left semicircle's center
        leftDistance = computePointsDistance(x, y, leftSemicircleCenterX,
                leftSemicircleCenterY);
        // The distance from the point to the right semicircle's center
        rightDistance = computePointsDistance(x, y, rightSemicircleCenterX,
                leftSemicircleCenterY);

        inSemicircle = leftDistance <= semicircleRadius || rightDistance <= semicircleRadius;
        inCenterRect = x >= centerRect.left && x <= centerRect.right && y >= centerRect.top
                && y <= centerRect.bottom;
        return inSemicircle || inCenterRect;
    }

    /**
     * Calculate the distance between two given points.
     * @param x1 The x-coordinate of first point
     * @param y1 The y-coordinate of first point
     * @param x2 The x-coordinate of second point
     * @param y2 The y-coordinate of second point
     * @return the distance between points.
     */
    private double computePointsDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    private float constrain(float amount, float low, float high) {
        return amount < low ? low : (Math.min(amount, high));
    }

    /**
     * @return The switch width in pixels.
     */
    @Dimension
    public int getSwitchWidth() {
        return switchWidth;
    }

    /**
     * Set the switch width to the given value.
     * @param switchWidth width pixel size.
     */
    public void setSwitchWidth(@Dimension int switchWidth) {
        this.switchWidth = switchWidth;
        this.switchHeight = (int) constrain(switchHeight, 0, switchWidth);
        requestLayout();
    }

    /**
     * @return The switch height in pixels.
     */
    @Dimension
    public int getSwitchHeight() {
        return switchHeight;
    }

    /**
     * Set the switch height to the given value.
     * @param switchHeight height pixel size.
     */
    public void setSwitchHeight(@Dimension int switchHeight) {
        this.switchHeight = (int) constrain(switchHeight, 0, switchWidth);

        trackCornerRadius = switchHeight / 2;
        requestLayout();
    }

    /**
     * Get the switch animation duration.
     * @return The switch animation duration in milliseconds.
     */
    public int getSwitchDuration() {
        return switchDuration;
    }

    /**
     * Set the switch animation duration to the given value.
     * @param switchDuration duration in milliseconds.
     */
    public void setSwitchDuration(int switchDuration) {
        this.switchDuration = switchDuration;
        sliderAnimator.setDuration(switchDuration);
        trackAnimator.setDuration(switchDuration);
    }

    /**
     * @return The track color used at switch on.
     */
    @ColorInt
    public int getTrackOnColor() {
        return trackOnColor;
    }

    /**
     * Set the track color which used at switch on.
     * @param trackOnColor A color value in the form 0xAARRGGBB.
     */
    public void setTrackOnColor(@ColorInt int trackOnColor) {
        this.trackOnColor = trackOnColor;
        invalidate();
    }

    /**
     * @return The track color used at switch off.
     */
    @ColorInt
    public int getTrackOffColor() {
        return trackOffColor;
    }

    /**
     * Set the track color which used at switch off.
     * @param trackOffColor A color value in the form 0xAARRGGBB.
     */
    public void setTrackOffColor(@ColorInt int trackOffColor) {
        this.trackOffColor = trackOffColor;
        invalidate();
    }

    /**
     * @return The slider color.
     */

    // Add getter and setter methods for the new attributes
    public int getSliderOnColor() {
        return sliderOnColor;
    }

    public void setSliderOnColor(@ColorInt int sliderOnColor) {
        this.sliderOnColor = sliderOnColor;
        invalidate();
    }

    public int getSliderOffColor() {
        return sliderOffColor;
    }

    public void setSliderOffColor(@ColorInt int sliderOffColor) {
        this.sliderOffColor = sliderOffColor;
        invalidate();
    }

//    @ColorInt
//    public int getSliderColor() {
//        return sliderColor;
//    }
//
//    /**
//     * Set the slider color to the given value.
//     * @param sliderColor A color value in the form 0xAARRGGBB.
//     */
//    public void setSliderColor(@ColorInt int sliderColor) {
//        this.sliderColor = sliderColor;
//        invalidate();
//    }

    /**
     * @return The slider radius pixels.
     */
    @Dimension
    public int getSliderRadius() {
        return sliderRadius;
    }

    /**
     * Set the slider radius to the given pixel size.
     * @param sliderRadius The slider pixel size.
     */
    public void setSliderRadius(int sliderRadius) {
        this.sliderRadius = (int) constrain(sliderRadius,
                ViewUtils.dpToPx(getContext(), MIN_SLIDER_RADIUS),
                trackCornerRadius);
        invalidate();
    }

    /**
     * Get enabled status for slider shadow.
     * @return True if this slider shadow is enabled, false otherwise.
     */
    public boolean isSliderShadowEnabled() {
        return sliderShadowEnabled;
    }

    /**
     * Set enabled status for slider shadow.
     * @param sliderShadowEnabled True if this slider shadow is enabled, false otherwise.
     */
    public void setSliderShadowEnabled(boolean sliderShadowEnabled) {
        this.sliderShadowEnabled = sliderShadowEnabled;
        if (sliderShadowEnabled) {
            sliderPaint.setShadowLayer(sliderShadowRadius, 0, 0, sliderShadowColor);
        } else {
            sliderPaint.clearShadowLayer();
        }
        invalidate();
    }

    /**
     * @return A color value in the form 0xAARRGGBB.
     */
    @ColorInt
    public int getSliderShadowColor() {
        return sliderShadowColor;
    }

    /**
     * Set slider shadow color to the given value.
     * @param sliderShadowColor A color value in the form 0xAARRGGBB.
     */
    public void setSliderShadowColor(@ColorInt int sliderShadowColor) {
        this.sliderShadowColor = sliderShadowColor;
        if (sliderShadowEnabled) {
            sliderPaint.setShadowLayer(sliderShadowRadius, 0, 0, sliderShadowColor);
        } else {
            sliderPaint.clearShadowLayer();
        }
        invalidate();
    }

    /**
     * @return The shadow radius pixel sizes.
     */
    @Dimension
    public int getSliderShadowRadius() {
        return sliderShadowRadius;
    }

    /**
     * @param sliderShadowRadius The slider shadow radius pixel size.
     */
    public void setSliderShadowRadius(@Dimension int sliderShadowRadius) {
        this.sliderShadowRadius = sliderShadowRadius;
        if (sliderShadowEnabled) {
            sliderPaint.setShadowLayer(sliderShadowRadius, 0, 0, sliderShadowColor);
        } else {
            sliderPaint.clearShadowLayer();
        }
        invalidate();
    }

    /**
     * @return True is checked, false otherwise.
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * Switch to the new state based on the given value.
     * @param shouldChecked true to check the button, false to uncheck it.
     */
//    public void setChecked(boolean shouldChecked) {
//        if (getWindowToken() != null && ViewCompat.isLaidOut(this)) {
//            // Move the slider to new position with animation, if not laid out  yet.
//            animateSlider(shouldChecked);
//        } else {
//            // Immediately move the thumb to the new position.
//            setUpSwitch(shouldChecked);
//        }
//    }
//

    public void setChecked(boolean shouldChecked) {
        if (getWindowToken() != null && ViewCompat.isLaidOut(this)) {
            // Move the slider to new position with animation, if not laid out yet.
            animateSlider(shouldChecked);
        } else {
            // Immediately move the thumb to the new position.
            setUpSwitch(shouldChecked);
        }

        // Set the slider colors based on the checked state
        if (shouldChecked) {
            // Set the slider color when it is checked (green)
            setSliderOnColor(sliderOnColor); // Set to green color
            // Set the slider color when it is unchecked (red)
            setSliderOffColor(sliderOffColor); // Set to red color
        } else {
            // Set the slider color when it is checked (red)
            setSliderOnColor(sliderOffColor); // Set to red color
            // Set the slider color when it is unchecked (green)
            setSliderOffColor(sliderOnColor); // Set to green color
        }
    }

    private void animateSlider(boolean shouldChecked) {
        float targetX;
        int targetColor;

        if (sliderAnimator.isRunning()) {
            sliderAnimator.cancel();
        }
        if (trackAnimator.isRunning()) {
            trackAnimator.cancel();
        }

        if (shouldChecked) {
            targetX = sliderCenterEndX;
            targetColor = trackOnColor;
        } else {
            targetX = sliderCenterStartX;
            targetColor = trackOffColor;
        }

        sliderAnimator.setFloatValues(sliderCenterX, targetX);
        PropertyValuesHolder valuesHolder = PropertyValuesHolder.ofInt(PROPERTY_NAME_TRACK_COLOR,
                trackColor, targetColor);
        valuesHolder.setEvaluator(new ArgbEvaluator());
        trackAnimator.setValues(valuesHolder);
        sliderAnimator.start();
        trackAnimator.start();
    }

    private void setUpSwitch(boolean shouldChecked) {
        if (ViewUtils.isLayoutRtl(this)) {
            sliderCenterStartX = switchWidth - sliderRadius;
            sliderCenterEndX = sliderRadius;
        } else {
            sliderCenterStartX = sliderRadius;
            sliderCenterEndX = switchWidth - sliderRadius;
        }
        checked = shouldChecked;
        sliderCenterX = shouldChecked ? sliderCenterEndX : sliderCenterStartX;
        sliderCenterY = switchTop + switchHeight / 2f;
        trackColor = shouldChecked ? trackOnColor : trackOffColor;
        invalidate();
    }

    /**
     * Register a callback to be invoked when the checked state of this button changes.
     * @param onStateChangeListener the callback to call on checked state change
     */
    public void setOnStateChangeListener(@Nullable OnStateChangeListener onStateChangeListener) {
        this.onStateChangeListener = onStateChangeListener;
    }

    /**
     * The callback to invoked when the state of switch changed.
     */
    public interface OnStateChangeListener {
        /**
         * Called when the state of switch has changed.
         * @param view The view whose state has changed.
         * @param checked The new checked state of view.
         */
        void onChanged(CupertinoSwitch view, boolean checked);

        /**
         * Called when the switch is switch on.
         * @param view The view whose state has changed.
         */
        void onSwitchOn(CupertinoSwitch view);

        /**
         * Called when the switch is switch off.
         * @param view The view whose state has changed.
         */
        void onSwitchOff(CupertinoSwitch view);
    }
}
