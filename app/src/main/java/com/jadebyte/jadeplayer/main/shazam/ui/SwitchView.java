package com.jadebyte.jadeplayer.main.shazam.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.ArrayList;

public class SwitchView extends View {

    private final int colorGray = Color.parseColor("#616161");
    private int colorPrimary = Color.parseColor("#64B5F6");
    private final HummingSupplier singingSupplier;

    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint thumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int radius, trackHeight, thumbSize, width, shadow;
    private int thumbPosition = thumbSize;
    private AnimatorSet currentAnimation;
    private OnSwitchListener listener;

    public SwitchView(HummingSupplier singingSupplier, Context context) {
        super(context);
        this.singingSupplier = singingSupplier;
        radius = Screen.dp(context, 16);
        trackHeight = Screen.dp(context,18);
        thumbSize = Screen.dp(context, 30) / 2;
        width = Screen.dp(context, 52);
        shadow = Screen.dp(context, 2);
        thumbPaint.setShadowLayer(3.0f, 0.0f, 0.0f, setAlphaComponent(colorGray, 150));
        setViewColor(colorGray);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setOnClickListener(v -> toggle());
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.translate(shadow, shadow);
        canvas.drawRoundRect(thumbSize, (getViewHeight() / 2) - (trackHeight / 2), getViewWidth() - thumbSize, (getViewHeight() / 2) + (trackHeight / 2), radius, radius, trackPaint);
        canvas.drawCircle(thumbPosition, thumbSize, thumbSize, thumbPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec((width + thumbSize) + (shadow * 2), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec((thumbSize * 2) + (shadow * 2), MeasureSpec.EXACTLY));
    }


    public void initActive(boolean isActive) {
        if (isActive) {
            setViewColor(colorPrimary);
            setThumbPosition(getViewWidth() - thumbSize);
        } else {
            setViewColor(colorGray);
            setThumbPosition(thumbSize);
        }
    }

    private void toggle() {
        if (currentAnimation != null) {
            currentAnimation.cancel();
        }
        ArrayList<Animator> animators = new ArrayList<>();
        if (singingSupplier.get()) {
            animators.add(ObjectAnimator.ofObject(this, "viewColor", new ColorEvaluator(), colorPrimary, colorGray));
            animators.add(ObjectAnimator.ofInt(this, "thumbPosition", (getViewWidth() - thumbSize), thumbSize));
        } else {
            animators.add(ObjectAnimator.ofObject(this, "viewColor", new ColorEvaluator(), colorGray, colorPrimary));
            animators.add(ObjectAnimator.ofInt(this, "thumbPosition", thumbSize, (getViewWidth() - thumbSize)));
        }
        currentAnimation = new AnimatorSet();
        currentAnimation.playTogether(animators);
        currentAnimation.setDuration(120);
        currentAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                currentAnimation = null;
                if (listener != null) {
                    listener.onSwitch();
                }
            }
        });
        currentAnimation.start();
    }

    public float getThumbPosition() {
        return thumbPosition;
    }

    public void setThumbPosition(int thumbPosition) {
        this.thumbPosition = thumbPosition;
        invalidate();
    }

    public void setViewColor(int color) {
        trackPaint.setColor(setAlphaComponent(color, 155));
        thumbPaint.setColor(color);
    }

    public int getViewWidth() {
        return (width + thumbSize);
    }

    public int getViewHeight() {
        return (thumbSize * 2);
    }

    public void setOnSwitchListener(OnSwitchListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    public static int setAlphaComponent(int color, int alpha) {
        return (color & 0x00ffffff) | (alpha << 24);
    }
}
