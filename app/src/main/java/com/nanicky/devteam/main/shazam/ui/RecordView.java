package com.nanicky.devteam.main.shazam.ui;


import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.media.MediaRecorder;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.nanicky.devteam.R;
import com.nanicky.devteam.main.common.permission.PermissionUtil;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class RecordView extends View {

    private Activity activity;
    private OnRecordingStoppedListener onRecordingStoppedListener;

    private MediaRecorder recorder;
    private final float INDETERMINANT_MIN_SWEEP = 15f;
    private final Object sync = new Object();
    private Timer progressTimer = null;
    private final Object progressTimerSync = new Object();
    public final int DEFAULT = 0, RECORDING = 1, LOADING = 2;

    private int size,
            radius,
            padding,
            innerSize,
            prevDuration = 0,
            progressSize,
            progressPadding;

    private final Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint backgroundRecord = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint loadingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final TextPaint durationPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private StaticLayout staticLayout, durationLayout;
    public int status = DEFAULT, countAudio = 0, currentDuration = 0;
    private ObjectAnimator animator;
    private File currentFile;
    private boolean breathAnimationWork = false;
    private RectF rectF;
    private ValueAnimator startAngleRotate;
    private ValueAnimator progressAnimator;
    private AnimatorSet indeterminateAnimator;
    private float indeterminateSweep, indeterminateRotateOffset, startAngle = -90;
    private int animDuration = 4000, animSteps = 3;
    private Runnable animationRunnable;

    public RecordView(Activity activity, OnRecordingStoppedListener onRecordingStoppedListener) {
        super(activity);
        this.onRecordingStoppedListener = onRecordingStoppedListener;
        this.activity = activity;
        size = Screen.getWidth(activity, getResources().getConfiguration()) / 2;
        progressSize = Screen.dp(activity, 6);
        progressPadding = Screen.dp(activity, 26) + progressSize;
        radius = size / 2;
        innerSize = radius / 2;
        padding = radius / 6;
        rectF = new RectF(progressPadding, progressPadding, ((float) size) - progressPadding, ((float) size) - progressPadding);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        loadingPaint.setStyle(Paint.Style.STROKE);
        loadingPaint.setColor(Color.WHITE);
        loadingPaint.setStrokeWidth(progressSize);
        loadingPaint.setStrokeCap(Paint.Cap.ROUND);
        backgroundPaint.setShader(new LinearGradient(0, 0, size, size, getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.selectedNavColor), Shader.TileMode.MIRROR));
        backgroundRecord.setColor(Color.WHITE);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(Screen.dp(getContext(), 18));
        textPaint.setTypeface(Screen.getTypeface(getContext(), "fonts/roboto_regular.ttf"));
        durationPaint.setColor(Color.BLACK);
        durationPaint.setTextSize(Screen.dp(getContext(), 16));
        durationPaint.setTypeface(Screen.getTypeface(getContext(), "fonts/roboto_regular.ttf"));
        setText(R.string.start_record);
        setTextDuration("0:00");
        startBreathAnimation();
    }

    private void startBreathAnimation() {
        animationRunnable = () -> {
            breathAnimationWork = true;
            breath();
        };
        post(animationRunnable);
    }

    private void breath() {
        if (!breathAnimationWork) {
            return;
        }
        if (animator != null) {
            animator.cancel();
        }
        animator = ObjectAnimator.ofFloat(this, "scale", getScale() == 1f ? 1.2f : 1f).setDuration(2000);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                breath();
            }
        });
        animator.start();
    }

    @Override
    public void onDraw(Canvas canvas) {
        int count;
        canvas.drawCircle(radius, radius, radius, backgroundPaint);
        switch (status) {
            case DEFAULT:
                count = canvas.save();
                canvas.translate(padding, (size / 2) - (staticLayout.getHeight() / 2));
                staticLayout.draw(canvas);
                canvas.restoreToCount(count);
                break;
            case RECORDING:
                canvas.drawCircle(radius, radius, innerSize, backgroundRecord);
                count = canvas.save();
                canvas.translate(padding, (size / 2) - (durationLayout.getHeight() / 2));
                durationLayout.draw(canvas);
                canvas.restoreToCount(count);
                break;
            case LOADING:
                canvas.drawArc(rectF, startAngle + indeterminateRotateOffset, indeterminateSweep, false, loadingPaint);
                break;
        }
    }

    public void startRecord() throws IOException {
        if (status != DEFAULT) {
            return;
        }
        initRecorder();
        recorder.start();
        startProgressTimer();
        status = RECORDING;
        invalidate();
        animation();
    }

    public void stopRecord() {
        if (status != RECORDING) {
            return;
        }
        release();
        stopProgressTimer();
        setLoading();
        onRecordingStoppedListener.onRecordingStopped(currentFile);
    }

    public void setLoading() {
        status = LOADING;
        breathAnimationWork = false;
        if (animator != null) {
            animator.cancel();
        }
        setText(R.string.upload_record);
        resetAnimation();
        invalidate();
    }

    public void setDefault() {
        stopAnimation();
        status = DEFAULT;
        setText(R.string.start_record);
        setTextDuration("0:00");
        invalidate();
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
        animator = ObjectAnimator.ofFloat(this, "scale", 1f).setDuration(100);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startBreathAnimation();
            }
        });
        animator.start();
    }

    public void setScale(float scale) {
        super.setScaleX(scale);
        super.setScaleY(scale);
    }

    private void initRecorder() throws IOException {
        countAudio++;
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
        recorder.setAudioEncodingBitRate(48000);
        recorder.setAudioSamplingRate(16000);
        currentFile = File.createTempFile("audio", String.valueOf(countAudio));
        recorder.setOutputFile(currentFile.getAbsolutePath());
        recorder.prepare();
    }

    public void release() {
        if (recorder != null) {
            try {
                recorder.stop();
                recorder.release();
            } catch (Throwable ignored) {
            }
        }
    }

    public float getScale() {
        return super.getScaleX();
    }

    private void animation() {
        if (status == DEFAULT) {
            return;
        }
        breathAnimationWork = false;
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
        animator = ObjectAnimator.ofFloat(this, "scale", getScale() == 1.3f ? 0.9f : 1.3f).setDuration(800);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animator = null;
                animation();
            }
        });
        animator.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean hasMicrophonPermission = PermissionUtil.Companion.hasPermissions(getContext(), Manifest.permission.RECORD_AUDIO);
        if (!hasMicrophonPermission) {
            PermissionUtil.Companion.requestRequiredPermissions(activity, Manifest.permission.RECORD_AUDIO);
            return false;
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                return true;
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                try {
                    startRecord();
                } catch (IOException e) {
                    Log.d("RecordView", e.getMessage());
                }
                break;
//            case MotionEvent.ACTION_POINTER_UP:
//            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                stopRecord();
                break;
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY));
    }

    private void setText(int resId) {
        setText(getResources().getString(resId));
    }

    private void setText(String text) {
        staticLayout = new StaticLayout(text, 0, text.length(), textPaint, size - (padding * 2), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
    }

    public int getSize() {
        return size;
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    private void stopProgressTimer() {
        currentDuration = 0;
        synchronized (progressTimerSync) {
            if (progressTimer != null) {
                try {
                    progressTimer.cancel();
                    progressTimer = null;
                } catch (Exception e) {
                }
            }
        }
    }

    private void startProgressTimer() {
        synchronized (progressTimerSync) {
            if (progressTimer != null) {
                try {
                    progressTimer.cancel();
                    progressTimer = null;
                } catch (Exception e) {
                }
            }
            progressTimer = new Timer();
            progressTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    synchronized (sync) {
                        currentDuration += 500;
                        if (status == RECORDING) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    int duration = currentDuration / 1000;
                                    if (prevDuration == duration) {
                                        return;
                                    }
                                    prevDuration = duration;
                                    setTextDuration(String.format("%d:%02d", prevDuration / 60, prevDuration % 60));
                                }
                            });
                        }
                    }
                }
            }, 0, 500);
        }
    }

    private void setTextDuration(String textDuration) {
        durationLayout = new StaticLayout(textDuration, 0, textDuration.length(), durationPaint, size - (padding * 2), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        invalidate();
    }

    public void resetAnimation() {
        if (startAngleRotate != null && startAngleRotate.isRunning()) {
            startAngleRotate.cancel();
        }
        if (progressAnimator != null && progressAnimator.isRunning()) {
            progressAnimator.cancel();
        }
        if (indeterminateAnimator != null && indeterminateAnimator.isRunning()) {
            indeterminateAnimator.cancel();
        }
        indeterminateSweep = INDETERMINANT_MIN_SWEEP;
        indeterminateAnimator = new AnimatorSet();
        AnimatorSet prevSet = null, nextSet;
        for (int k = 0; k < animSteps; k++) {
            nextSet = createIndeterminateAnimator(k);
            AnimatorSet.Builder builder = indeterminateAnimator.play(nextSet);
            if (prevSet != null) {
                builder.after(prevSet);
            }
            prevSet = nextSet;
        }
        indeterminateAnimator.addListener(new AnimatorListenerAdapter() {
            boolean wasCancelled = false;

            @Override
            public void onAnimationCancel(Animator animation) {
                wasCancelled = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!wasCancelled) {
                    resetAnimation();
                }
            }
        });
        indeterminateAnimator.start();
    }

    public void stopAnimation() {
        if (startAngleRotate != null) {
            startAngleRotate.removeAllListeners();
            startAngleRotate.removeAllUpdateListeners();
            startAngleRotate.cancel();
            startAngleRotate = null;
        }
        if (progressAnimator != null) {
            progressAnimator.removeAllListeners();
            progressAnimator.removeAllUpdateListeners();
            progressAnimator.cancel();
            progressAnimator = null;
        }
        if (indeterminateAnimator != null) {
            indeterminateAnimator.removeAllListeners();
            indeterminateAnimator.cancel();
            indeterminateAnimator = null;
        }
    }

    private AnimatorSet createIndeterminateAnimator(float step) {
        final float maxSweep = 360f * (animSteps - 1) / animSteps + INDETERMINANT_MIN_SWEEP;
        final float start = -90f + step * (maxSweep - INDETERMINANT_MIN_SWEEP);
        ValueAnimator frontEndExtend = ValueAnimator.ofFloat(INDETERMINANT_MIN_SWEEP, maxSweep);
        frontEndExtend.setDuration(animDuration / animSteps / 2);
        frontEndExtend.setInterpolator(new DecelerateInterpolator(1));
        frontEndExtend.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                indeterminateSweep = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });
        ValueAnimator rotateAnimator1 = ValueAnimator.ofFloat(step * 720f / animSteps, (step + .5f) * 720f / animSteps);
        rotateAnimator1.setDuration(animDuration / animSteps / 2);
        rotateAnimator1.setInterpolator(new LinearInterpolator());
        rotateAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                indeterminateRotateOffset = (Float) animation.getAnimatedValue();
            }
        });
        ValueAnimator backEndRetract = ValueAnimator.ofFloat(start, start + maxSweep - INDETERMINANT_MIN_SWEEP);
        backEndRetract.setDuration(animDuration / animSteps / 2);
        backEndRetract.setInterpolator(new DecelerateInterpolator(1));
        backEndRetract.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                startAngle = (Float) animation.getAnimatedValue();
                indeterminateSweep = maxSweep - startAngle + start;
                invalidate();
            }
        });
        ValueAnimator rotateAnimator2 = ValueAnimator.ofFloat((step + .5f) * 720f / animSteps, (step + 1) * 720f / animSteps);
        rotateAnimator2.setDuration(animDuration / animSteps / 2);
        rotateAnimator2.setInterpolator(new LinearInterpolator());
        rotateAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                indeterminateRotateOffset = (Float) animation.getAnimatedValue();
            }
        });
        AnimatorSet set = new AnimatorSet();
        set.play(frontEndExtend).with(rotateAnimator1);
        set.play(backEndRetract).with(rotateAnimator2).after(rotateAnimator1);
        return set;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        animator.removeAllListeners();
        animator.removeAllUpdateListeners();
        animator.cancel();
        animator = null;
        status = -1;
        stopAnimation();
        onRecordingStoppedListener = null;
        activity = null;
        if (animationRunnable != null) {
            removeCallbacks(animationRunnable);
        }
        if (progressTimer != null) {
            progressTimer.cancel();
            progressTimer = null;
        }
    }
}
