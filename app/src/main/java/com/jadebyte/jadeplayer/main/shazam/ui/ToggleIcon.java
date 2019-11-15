package com.jadebyte.jadeplayer.main.shazam.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;

import com.jadebyte.jadeplayer.R;

public class ToggleIcon extends View {

    private final int colorGray = Color.BLACK;
    private final int colorPrimaryDark = Color.parseColor("#2196F3");
    private HummingSupplier hummingSupplier;

    private final TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private StaticLayout staticLayout;
    private int size, iconSize, iconCenter;
    private Drawable icon;


    public ToggleIcon(Context context, HummingSupplier hummingSupplier) {
        super(context);
        size = Screen.dp(context, 36) * 2;
        iconSize = size / 3;
        iconCenter = (size / 2) - (iconSize / 2);
        this.hummingSupplier = hummingSupplier;
        textPaint.setTextSize(Screen.dp(context, 12));
        updateIcon();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec((int) (size * 1.5), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY));
    }

    public void setIcon(int resIcon) {
        icon = getDrawable(resIcon);
        icon.setBounds(iconCenter, 0, iconSize + iconCenter, iconSize);
        invalidate();
    }

    private Drawable getDrawable(int resId) {
        return getContext().getResources().getDrawable(resId);
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.translate(- iconSize, 0);
        icon.draw(canvas);
        int count = canvas.save();
        canvas.translate(1.7F * iconSize, iconSize / 4);
        staticLayout.draw(canvas);
        canvas.restoreToCount(count);
    }

    public void toggle() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "scaleX", 0f).setDuration(100);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                updateIcon();
                ObjectAnimator.ofFloat(ToggleIcon.this, "scaleX", 1f).setDuration(100).start();
            }
        });
        animator.start();
    }

    private void updateIcon() {
        setIcon(isHumming() ? R.drawable.ic_voice : R.drawable.ic_microphone);
        setText(isHumming() ? getResources().getString(R.string.humming) : getResources().getString(R.string.listening));
    }

    private void setText(String text) {
        textPaint.setColor(isHumming() ? colorPrimaryDark : colorGray);
        staticLayout = new StaticLayout(text, 0, text.length(), textPaint, size, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
    }

    public int getSize() {
        return size;
    }

    public boolean isHumming() {
        return hummingSupplier.get();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        hummingSupplier = null;
    }
}

