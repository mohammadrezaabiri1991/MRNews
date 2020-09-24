package com.mohammadreza.news.application;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

import androidx.multidex.MultiDex;

import java.io.File;

public class App extends Application {
    public static Typeface TYPE_FACE;
//    public static Context mainContext;

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        mainContext= getApplicationContext();
        TYPE_FACE = Typeface.createFromAsset(getAssets(), "shabnam.ttf");

        try {
            File dir = this.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }


    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}
