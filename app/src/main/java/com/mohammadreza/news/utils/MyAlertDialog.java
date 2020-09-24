package com.mohammadreza.news.utils;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.mohammadreza.news.R;

public class MyAlertDialog {
    public MyAlertDialog() {
    }

    public static void createInfoDialog(AppCompatActivity appCompatActivity) {
        if (!appCompatActivity.isFinishing()) {
            AlertDialog.Builder alert = new AlertDialog.Builder(appCompatActivity, R.style.FullScreenTheme);
            LayoutInflater factory = LayoutInflater.from(appCompatActivity);
            final View view = factory.inflate(R.layout.full_screen_image, null);
            alert.setView(view);
            final AlertDialog dialog = alert.create();
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            if (dialog.getWindow() != null) {
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.gravity = Gravity.CENTER;
                dialog.getWindow().setGravity(Gravity.CENTER);
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            }
            ImageView imgBack = view.findViewById(R.id.imgBackFullScreenImage);

            dialog.getWindow().setAttributes(layoutParams);
            dialog.show();
            imgBack.setOnClickListener(v -> dialog.dismiss());

        }
    }

}
