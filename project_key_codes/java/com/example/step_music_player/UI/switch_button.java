package com.example.step_music_player.UI;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.core.view.MotionEventCompat;

import com.example.step_music_player.R;

public class switch_button extends View {
    private int widthSize = 280;
    private int heightSize = 140;

    private int mInnerRadius;
    private int mOutRadius;

    private int themeColor;
    private boolean isOpen;

    private static final int PADDING = 6;

    private Paint mPaint;
    private int mAlpha;

    private RectF mOutRect = new RectF();

    private int changingValue;
    private int maxValue;
    private int minValue;
    private int mStartValue = PADDING;

    private int eventStartX;
    private boolean isRunning = false;

    private SlideListener listener;

    public switch_button(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public switch_button(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedValue tv=new TypedValue();
        context.getTheme().resolveAttribute(R.attr.theme_color,tv,true);
        listener = null;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.slideswitch);
        if (typedArray != null) {
            themeColor = typedArray.getColor(R.styleable.preferenceset_theme_color, tv.data+16^6+16^7);
            isOpen = typedArray.getBoolean(R.styleable.slideswitch_isOpen, false);
            typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int withModel = MeasureSpec.getMode(widthMeasureSpec);
        int heightModel = MeasureSpec.getMode(heightMeasureSpec);

        if (withModel == MeasureSpec.EXACTLY) {
            widthSize = width;
        }
        if (heightModel == MeasureSpec.EXACTLY) {
            heightSize = height;
        }
        setMeasuredDimension(widthSize, heightSize);
        initValue();
    }

    public void initValue() {
        mInnerRadius = (heightSize - 2 * PADDING) / 2;
        mOutRadius = heightSize / 2;

        minValue = PADDING;
        maxValue = widthSize - 2 * mInnerRadius - PADDING;
        if (isOpen) {
            changingValue = maxValue;
            mAlpha = 255;
        } else {
            changingValue = PADDING;
            mAlpha = 0;
        }
        mStartValue = changingValue;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(Color.GRAY);
        mOutRect.set(0, 0, widthSize, heightSize);
        canvas.drawRoundRect(mOutRect, mOutRadius, mOutRadius, mPaint);
        mPaint.setColor(themeColor);
        mPaint.setAlpha(mAlpha);
        canvas.drawRoundRect(mOutRect, mOutRadius, mOutRadius, mPaint);
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(changingValue + mInnerRadius, PADDING + mInnerRadius, mInnerRadius, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isRunning) {
            super.onTouchEvent(event);
        }
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                eventStartX = (int) event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                int eventLastX = (int) event.getRawX();
                int distance = eventLastX - eventStartX;
                int movePoint = distance + mStartValue;
                movePoint = (movePoint > maxValue ? maxValue : movePoint);
                movePoint = (movePoint < minValue ? minValue : movePoint);
                if (movePoint >= minValue && movePoint <= maxValue) {
                    changingValue = movePoint;
                    mAlpha = (int) (255 * (float) movePoint / (float) maxValue);
                    invalidateView();
                }
                break;
            case MotionEvent.ACTION_UP:
                int wholeX = (int) (event.getRawX() - eventStartX);
                mStartValue = changingValue;
                boolean toRight;
                toRight = (mStartValue > maxValue / 2);
                if (Math.abs(wholeX) < 3) {
                    toRight = !toRight;
                }
                startAnimator(toRight);
                break;
            default:
                break;
        }
        return true;
    }

    public void startAnimator(final boolean toRight) {
        ValueAnimator toDestAnim = ValueAnimator.ofInt(changingValue, toRight ? maxValue : minValue);
        toDestAnim.setDuration(300);
        toDestAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        toDestAnim.start();
        toDestAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                changingValue = (Integer) animation.getAnimatedValue();
                mAlpha = (int) (255 * (float) changingValue / (float) maxValue);
                invalidateView();
            }
        });

        toDestAnim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                isRunning = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isRunning = false;
                if (toRight) {
                    isOpen = true;
                    if (listener != null) {
                        listener.openState(true, switch_button.this);
                    }
                    mStartValue = maxValue;
                } else {
                    isOpen = false;
                    if (listener != null) {
                        listener.openState(false, switch_button.this);
                    }
                    mStartValue = minValue;
                }
            }
        });
    }


    public void changeOpenState(boolean isOpen) {
        this.isOpen = isOpen;
        initValue();
        invalidateView();
        if (listener != null) {
            if (isOpen) {
                listener.openState(true, switch_button.this);
            } else {
                listener.openState(false, switch_button.this);
            }
        }
    }

    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    public void setSlideListener(SlideListener listener) {
        this.listener = listener;
    }

    public interface SlideListener {
        void openState(boolean isOpen, View view);
    }

    public boolean getOpenState(){ return isOpen; }
}