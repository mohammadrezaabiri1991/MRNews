package com.mohammadreza.news.cropper.imageUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;

import com.mohammadreza.news.R;
import com.mohammadreza.news.constant.NewsConstant;
import com.mohammadreza.news.cropper.ui.CropActivity;
import com.mohammadreza.news.utils.Utils;

import java.io.File;
import java.io.IOException;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import top.zibin.luban.Luban;

import static com.mohammadreza.news.constant.NewsConstant.CAMERA_REQUEST;
import static com.mohammadreza.news.constant.NewsConstant.EX_STORAGE_REQ_CODE;


public class MyImageCrop {
    private AppCompatActivity activity;
    public static ProgressDialog progressCompressImageProfile;

    public MyImageCrop(AppCompatActivity activity) {
        this.activity = activity;
//        this.prgLoadingCompress = prgLoadingCompress;
    }

    public static void pickFromGallery(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                .setType("image/*")
                .addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            String[] mimeTypes = {"image/jpeg", "image/png"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        }
        activity.startActivityForResult(Intent.createChooser(intent, activity.getString(R.string.label_select_picture)), EX_STORAGE_REQ_CODE);
    }

    public static void openCamera(AppCompatActivity appCompatActivity) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        appCompatActivity.startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    public void selectImageFromDeviceAction(final Intent imageReturnedIntent) {
        progressCompressImageProfile = Utils.showProgressDialog(activity);
        new Thread(() -> {
            try {
                compressImageLuban(FileUtil.from(activity, imageReturnedIntent.getData()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();


    }

    private void compressImageLuban(File file) {
        if (file != null) {
            Luban.get(activity)
                    .load(file)
                    .putGear(Luban.THIRD_GEAR)
                    .asObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError(Throwable::printStackTrace)
                    .onErrorResumeNext(throwable -> Observable.empty())
                    .subscribe(file1 -> {
                        // TODO called when compression finishes successfully, provides compressed image
                        activity.runOnUiThread(() -> sendDataToCropActivity(file1));
                    });
        }
    }

    private void sendDataToCropActivity(File file) {
        Intent intent = new Intent(activity, CropActivity.class);
        intent.putExtra(NewsConstant.FILE_PATH, file.getPath());
        activity.startActivity(intent);
    }
}
