package com.mohammadreza.news.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.mohammadreza.news.R;
import com.mohammadreza.news.constant.NewsConstant;
import com.mohammadreza.news.model.NewsModel;
import com.mohammadreza.news.model.UserModel;
import com.mohammadreza.news.ui.NewsCategoryActivity;
import com.mohammadreza.news.ui.RegisterActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import ir.huri.jcal.JalaliCalendar;
import pl.droidsonroids.gif.GifImageView;

import static com.mohammadreza.news.constant.NewsConstant.JPG_FORMAT;
import static com.mohammadreza.news.constant.NewsConstant.STR_PATH_FOLDER;
import static com.mohammadreza.news.viewmodel.RegisterViewModel.isTxtVisible;


public class Utils {


    public static void callIntent(AppCompatActivity appCompatActivity, Class nextClass) {
        Intent intent = new Intent(appCompatActivity, nextClass);
        appCompatActivity.startActivity(intent);
        appCompatActivity.overridePendingTransition(R.anim.anim_fade_in_activity, R.anim.anim_fade_out_activity);

    }


    public static void callIntent(AppCompatActivity appCompatActivity, Class nextClass, UserModel model) {
        Intent intent = new Intent(appCompatActivity, nextClass);
        intent.putExtra(NewsConstant.USER_MODEL_KEY, model);
        appCompatActivity.startActivity(intent);
        appCompatActivity.overridePendingTransition(R.anim.anim_fade_in_activity, R.anim.anim_fade_out_activity);
        appCompatActivity.finish();
    }

    public static void callIntent(AppCompatActivity appCompatActivity, Class nextClass, NewsModel newsModel, String newsCategory) {
        Intent intent = new Intent(appCompatActivity, nextClass);
        intent.putExtra(NewsConstant.NEWS_MODEL_KEY, newsModel);
        intent.putExtra(NewsConstant.NEWS_CATEGORY_KEY, newsCategory);
        appCompatActivity.startActivity(intent);
        appCompatActivity.overridePendingTransition(R.anim.anim_fade_in_activity, R.anim.anim_fade_out_activity);
    }


    public static void callIntent(AppCompatActivity appCompatActivity, Class nextClass, String key, String value, String secondKey, String secondValue) {
        Intent intent = new Intent(appCompatActivity, nextClass);
        intent.putExtra(key, value);
        intent.putExtra(secondKey, secondValue);
        appCompatActivity.startActivity(intent);
        appCompatActivity.overridePendingTransition(R.anim.anim_fade_in_activity, R.anim.anim_fade_out_activity);

    }


    public static void callIntentRegister(final AppCompatActivity appCompatActivity, final UserModel userModel) {
        new Handler().postDelayed(() -> {
            callIntent(appCompatActivity, NewsCategoryActivity.class, userModel);
            isTxtVisible.set(View.GONE);
        }, 2500);
    }


    public static Bitmap getIntentExtras(AppCompatActivity activity) {
        Intent intent = activity.getIntent();
        String filePath = intent.getStringExtra(NewsConstant.FILE_PATH);
        return BitmapFactory.decodeFile(filePath);
    }


    public static boolean checkPassValidation(String entryPass, String firePass) {
        if (entryPass.contentEquals(firePass)) {
            return true;
        } else {
            return false;
        }
    }

    public static String setPersianNumbers(String str) {
        return str
                .replace("0", "۰")
                .replace("1", "۱")
                .replace("2", "۲")
                .replace("3", "۳")
                .replace("4", "۴")
                .replace("5", "۵")
                .replace("6", "۶")
                .replace("7", "۷")
                .replace("8", "۸")
                .replace("9", "۹");
    }

    public static ProgressDialog showProgressDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }

    public static ProgressDialog showProgressDialog(Context context, View view) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
        view.setClickable(true);
        return progressDialog;
    }


    public static void showAlertDialog(AppCompatActivity appCompatActivity, final String message) {
        if (!appCompatActivity.isFinishing()) {
            new AlertDialog.Builder(appCompatActivity)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }
    }


    //-------------------------------------------------------------------------------------------------------------------------------
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();

    }

    public static void hideProgressDialog(AppCompatActivity appCompatActivity) {
        if (((RegisterActivity) appCompatActivity).progressDialog != null && !appCompatActivity.isFinishing()) {
            ((RegisterActivity) appCompatActivity).progressDialog.dismiss();
        }

    }

    @SuppressLint("SimpleDateFormat")
    public static String convertToPersianDate(AppCompatActivity appCompatActivity, String gregorianDate) {
        if (appCompatActivity.isFinishing()) {
            return gregorianDate;
        }
        try {

            if (gregorianDate == null) {
                return NewsConstant.DEFAULT_TIME;
            }

            SimpleDateFormat format = new SimpleDateFormat(appCompatActivity.getString(R.string.news_date_format));
            Date newDate = new Date();
            try {
                newDate = format.parse(gregorianDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar cal = Calendar.getInstance();
            if (newDate != null) {
                cal.setTimeInMillis(newDate.getTime());
                JalaliCalendar jalaliDate = new JalaliCalendar(new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)));

                String hour = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
                String minute = String.valueOf(cal.get(Calendar.MINUTE));

                if (cal.get(Calendar.HOUR_OF_DAY) < 10) {
                    hour = "0" + hour;
                }
                if (cal.get(Calendar.MINUTE) < 10) {
                    minute = "0" + minute;
                }

                return jalaliDate.getYear() + "-" + jalaliDate.getMonth() + "-" + jalaliDate.getDay() + "  " + hour + ":" + minute;
            } else {
                return gregorianDate;
            }
        } catch (Exception e) {
            return gregorianDate;
        }

    }

    public static void saveImageFile(Bitmap imageToSave, String imageBaseName, String fileNames) {
        File direct = new File(Environment.getExternalStorageDirectory() + STR_PATH_FOLDER);
        if (!direct.exists()) {
            direct.mkdirs();
        }

        File finalFolder = new File(direct + "/" + imageBaseName);
        if (!finalFolder.exists()) {
            finalFolder.mkdirs();
            createNoMediaFile(finalFolder);
        }


        File file = new File(finalFolder, "/" + fileNames + JPG_FORMAT);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createNoMediaFile(File finalFolder) {
        try {
            File file = new File(finalFolder + "/", NewsConstant.NO_MEDIA);
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                child.delete();
            }
        }
        fileOrDirectory.delete();
    }

    public static boolean getImageFile(String imageBasePath, AppCompatActivity appCompatActivity, GridLayout mGridLayoutDetail, int comFrom) {
        if (appCompatActivity.isFinishing()) {
            return false;
        }

        File direct = new File(Environment.getExternalStorageDirectory() + STR_PATH_FOLDER);
        File finalFolder = new File(direct + "/" + imageBasePath);

        File[] files = finalFolder.listFiles();
        if (files != null && files.length > 0) {
            new Thread(() -> {
                for (int i = 0; i < files.length; i++) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(new File("", files[i].getPath())));
                        if (bitmap != null) {
                            ImageView imageView = new AppCompatImageView(appCompatActivity);
                            setSizeToNewsDetailImage(imageView, appCompatActivity);
                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                            appCompatActivity.runOnUiThread(() -> {
                                if (comFrom == 1 || files.length <= 2) {
                                    setGravityToImageView(imageView, appCompatActivity);
                                }
                                imageView.setImageBitmap(bitmap);
                                mGridLayoutDetail.addView(imageView);

                            });
                        }


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
            return true;

        } else {
            return false;
        }
    }

    public static void setGravityToImageView(ImageView imageView, AppCompatActivity appCompatActivity) {
        DisplayMetrics dm = appCompatActivity.getResources().getDisplayMetrics();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dm.widthPixels - 24, dm.widthPixels - 24);
        params.leftMargin = 12;
        params.rightMargin = 12;
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setLayoutParams(params);
    }


    public static void setSizeToNewsDetailImage(ImageView imageView, AppCompatActivity appCompatActivity) {
        if (!appCompatActivity.isFinishing()) {
            DisplayMetrics dm = appCompatActivity.getResources().getDisplayMetrics();
            int width = (dm.widthPixels / 2) - 12;
            setLayoutParams(imageView, width);
        }
    }

    private static void setLayoutParams(View imageView, int densityDpi) {
        GridLayout.LayoutParams para = new GridLayout.LayoutParams();
        para.height = densityDpi;
        para.width = densityDpi;
        para.setGravity(Gravity.END);
        para.topMargin = 8;
        para.leftMargin = 8;
        imageView.setLayoutParams(para);
    }

    public static void saveImageToInternalStorage(String imageName, Bitmap bitmap, AppCompatActivity appCompatActivity) {
        if (!appCompatActivity.isFinishing()) {
            imageName = imageName + JPG_FORMAT;
            ContextWrapper contextWrapper = new ContextWrapper(appCompatActivity);
            // path to /data/data/your_app/app_data/imageDir
            File directory = contextWrapper.getDir(NewsConstant.IMAGE_DIR, Context.MODE_PRIVATE);
            // Create imageDir
            File path = new File(directory, imageName);
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(path);
                // Use the compress method on the BitMap object to write image to the OutputStream
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, fileOutputStream);


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void loadImageFromStorage(String imageName, AppCompatActivity appCompatActivity, GifImageView imageView) {
        if (!appCompatActivity.isFinishing()) {
            imageName = imageName + JPG_FORMAT;
            ContextWrapper contextWrapper = new ContextWrapper(appCompatActivity);
            String path = contextWrapper.getDir(NewsConstant.IMAGE_DIR, Context.MODE_PRIVATE).getAbsolutePath();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(new File(path, imageName)));
                appCompatActivity.runOnUiThread(() -> imageView.setImageBitmap(bitmap));

            } catch (FileNotFoundException e) {
                appCompatActivity.runOnUiThread(() -> imageView.setImageResource(R.drawable.all_news));
                e.printStackTrace();
            }
        }
    }

    public static void loadImageFromStorage(String imageName, AppCompatActivity appCompatActivity, ImageView imageView) {
        if (!appCompatActivity.isFinishing()) {
            imageName = imageName + JPG_FORMAT;
            ContextWrapper contextWrapper = new ContextWrapper(appCompatActivity);
            String path = contextWrapper.getDir(NewsConstant.IMAGE_DIR, Context.MODE_PRIVATE).getAbsolutePath();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(new File(path, imageName)));
                appCompatActivity.runOnUiThread(() -> imageView.setImageBitmap(bitmap));

            } catch (FileNotFoundException e) {
                appCompatActivity.runOnUiThread(() -> imageView.setImageResource(R.drawable.all_news));
                e.printStackTrace();
            }
        }
    }

    public static boolean isEmailValid(String strEmail) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(strEmail).matches();
    }

    public static void hideKeyboard(AppCompatActivity appCompatActivity) {
        View view = appCompatActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) appCompatActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}
