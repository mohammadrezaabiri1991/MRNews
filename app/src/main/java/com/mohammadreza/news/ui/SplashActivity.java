package com.mohammadreza.news.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mohammadreza.news.R;
import com.mohammadreza.news.utils.GetPermissions;


public class SplashActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        intentToRegisterActivity();
        try {
            ((TextView) findViewById(R.id.txtAppVersion)).setText(getString(R.string.v) + getAppVersion());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void intentToRegisterActivity() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, RegisterActivity.class));
            overridePendingTransition(R.anim.anim_fade_in_activity, R.anim.anim_fade_out_activity);
            finish();

        }, 2000);

    }


    private String getAppVersion() throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        return pInfo.versionName;
    }
}
