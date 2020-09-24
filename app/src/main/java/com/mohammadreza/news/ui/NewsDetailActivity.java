package com.mohammadreza.news.ui;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.mohammadreza.news.R;
import com.mohammadreza.news.constant.NewsConstant;
import com.mohammadreza.news.databinding.ActivityDetail;
import com.mohammadreza.news.model.EventBusModel;
import com.mohammadreza.news.utils.Utils;
import com.mohammadreza.news.viewmodel.NewsDetailViewModel;

import org.greenrobot.eventbus.EventBus;

public class NewsDetailActivity extends AppCompatActivity {

    private NewsDetailViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityDetail registerBinding = DataBindingUtil.setContentView(this, R.layout.news_detail_activity);
        viewModel = new NewsDetailViewModel(this, registerBinding);
        registerBinding.setDetail(viewModel);

    }


    @Override
    protected void onDestroy() {
        if (viewModel.networkStateReceivers != null) {
            try {
                unregisterReceiver(viewModel.networkStateReceivers);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NewsConstant.EX_STORAGE_REQ_CODE && grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Utils.showAlertDialog(this, getString(R.string.alert_save_in_gallery));
        }

    }
}