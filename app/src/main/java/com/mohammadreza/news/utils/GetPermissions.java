package com.mohammadreza.news.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mohammadreza.news.R;
import com.mohammadreza.news.cropper.imageUtils.MyImageCrop;


public class GetPermissions {

    public static void checkStoragePermission(Activity activity, int code, String comFrom) {
        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        int grantWrite = ContextCompat.checkSelfPermission(activity, permission[0]);
        int grantRead = ContextCompat.checkSelfPermission(activity, permission[1]);

        if (grantWrite != PackageManager.PERMISSION_GRANTED && grantRead != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list;
            permission_list = permission;
            ActivityCompat.requestPermissions(activity, permission_list, code);

        } else {
            if (comFrom.contentEquals(activity.getResources().getString(R.string.com_from_profile))) {
                MyImageCrop.pickFromGallery(activity);
            }

        }
    }


}
