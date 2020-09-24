package com.mohammadreza.news.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.mohammadreza.news.R;
import com.mohammadreza.news.cropper.imageUtils.MyImageCrop;
import com.mohammadreza.news.databinding.NewsCategoryActivityBinding;
import com.mohammadreza.news.model.EventBusModel;
import com.mohammadreza.news.utils.MyAlertDialog;
import com.mohammadreza.news.viewmodel.NewsCategoryViewModel;

import org.greenrobot.eventbus.EventBus;

import static com.mohammadreza.news.constant.NewsConstant.CAMERA_REQUEST;
import static com.mohammadreza.news.constant.NewsConstant.EX_STORAGE_REQ_CODE;
import static com.mohammadreza.news.cropper.imageUtils.MyImageCrop.openCamera;
import static com.mohammadreza.news.cropper.imageUtils.MyImageCrop.pickFromGallery;


public class NewsCategoryActivity extends AppCompatActivity {
    private NewsCategoryViewModel categoryViewModel;
    private int numberOfClick;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NewsCategoryActivityBinding categoryBinding = DataBindingUtil.setContentView(this, R.layout.news_category_activity);
        categoryViewModel = new NewsCategoryViewModel(categoryBinding);
        categoryBinding.setNewsCategory(categoryViewModel);

        setSupportActionBar(categoryBinding.toolbarCategory);
        categoryBinding.toolbarCategory.inflateMenu(R.menu.news_menu);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.news_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            EventBusModel busModel = new EventBusModel();
            busModel.setClick(true);
            EventBus.getDefault().post(busModel);
            return true;
        }
        else if (item.getItemId() == R.id.action_info) {
            MyAlertDialog.createInfoDialog(this);
        }
        else if (item.getItemId() == R.id.action_exit_app) {
            this.finishAffinity();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAndRemoveTask();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (imageReturnedIntent == null) {
//            progressLoading.setVisibility(View.INVISIBLE);
            return;
        }
        final Uri selectedUri = imageReturnedIntent.getData();
        switch (requestCode) {
            case EX_STORAGE_REQ_CODE:
            case CAMERA_REQUEST:
                if (resultCode == RESULT_OK) {
                    if (selectedUri != null) {
                        MyImageCrop imageCrop = new MyImageCrop(this);
                        imageCrop.selectImageFromDeviceAction(imageReturnedIntent);
                    }
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case EX_STORAGE_REQ_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFromGallery(this);
                }
                break;

            case CAMERA_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera(this);
                }
                break;


        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(categoryViewModel)) {
            EventBus.getDefault().register(categoryViewModel);
        }
    }

    @Override
    protected void onDestroy() {

        if (EventBus.getDefault().isRegistered(categoryViewModel)) {
            EventBus.getDefault().unregister(categoryViewModel);
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        numberOfClick += 1;
        if (numberOfClick == 1) {
            Toast.makeText(this, R.string.exit_app_message, Toast.LENGTH_SHORT).show();
        } else if ((numberOfClick > 1)) {
            super.onBackPressed();
        }
        new Handler().postDelayed(() -> numberOfClick = 0, 2000);
    }
}