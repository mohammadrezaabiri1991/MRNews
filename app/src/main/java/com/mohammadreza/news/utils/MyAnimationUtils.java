package com.mohammadreza.news.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.circularprogressbar.CircularProgressBar;

import static com.mohammadreza.news.constant.NewsConstant.SCALE_ANIM_DURATION;
import static com.mohammadreza.news.viewmodel.RegisterViewModel.isTxtVisible;


public class MyAnimationUtils {


    public static void animationTransactionToCircle(final Button addButton, final CircularProgressBar progress, AppCompatActivity appCompatActivity) {
        final int from = addButton.getWidth();
        final int to = (int) (from * 0.18f);
        final LinearInterpolator interpolator = new LinearInterpolator();

        ValueAnimator valueAnimator = ValueAnimator.ofInt(from, to);
        valueAnimator.setTarget(addButton);
        valueAnimator.setInterpolator(interpolator);
        valueAnimator.setDuration(SCALE_ANIM_DURATION);

        final ViewGroup.LayoutParams params = addButton.getLayoutParams();
        valueAnimator.addUpdateListener(animation -> {
            params.width = (Integer) animation.getAnimatedValue();
            addButton.requestLayout();
        });
        valueAnimator.start();

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progress.setVisibility(View.VISIBLE);
                addButton.setVisibility(View.GONE);
                Utils.hideKeyboard(appCompatActivity);
            }
        });

    }


    public static void animationTransactionToRound(final Button button, final CircularProgressBar progress, final FrameLayout frameLayout, final String errorMessage, final AppCompatActivity appCompatActivity) {
        new Handler().postDelayed(() -> {
            progress.setVisibility(View.GONE);
            button.setVisibility(View.VISIBLE);


            final int from = progress.getWidth();
            final int to = frameLayout.getWidth();
            final LinearInterpolator interpolator = new LinearInterpolator();

            ValueAnimator valueAnimator = ValueAnimator.ofInt(from, to);
            valueAnimator.setTarget(button);
            valueAnimator.setInterpolator(interpolator);
            valueAnimator.setDuration(SCALE_ANIM_DURATION);

            final ViewGroup.LayoutParams params = button.getLayoutParams();
            valueAnimator.addUpdateListener(animation -> {
                params.width = (Integer) animation.getAnimatedValue();
                button.requestLayout();
            });
            valueAnimator.start();

            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    Utils.showAlertDialog(appCompatActivity, errorMessage);
                    isTxtVisible.set(View.GONE);
                }
            });
        }, 3000);
    }


    public static void setVibrate(AppCompatActivity activity) {
        Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            assert vibrator != null;
            vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            if (vibrator != null) {
                vibrator.vibrate(300);
            }
        }
    }

    public static TranslateAnimation animationShakeError() {
        TranslateAnimation shake = new TranslateAnimation(0, 6, 0, 0);
        shake.setDuration(300);
        shake.setInterpolator(new CycleInterpolator(5));
        return shake;
    }
}
