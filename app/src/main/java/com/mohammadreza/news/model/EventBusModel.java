package com.mohammadreza.news.model;


import android.graphics.drawable.BitmapDrawable;

public class EventBusModel {

    private boolean isClick;
    private boolean isPermissionGranted;
    private BitmapDrawable bitmapDrawable;


    public BitmapDrawable getBitmapDrawable() {
        return bitmapDrawable;
    }

    public void setBitmapDrawable(BitmapDrawable bitmapDrawable) {
        this.bitmapDrawable = bitmapDrawable;
    }

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }


    public boolean isPermissionGranted() {
        return isPermissionGranted;
    }

    public void setPermissionGranted(boolean permissionGranted) {
        isPermissionGranted = permissionGranted;
    }
}
