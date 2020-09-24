package com.mohammadreza.news.cropper.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.mohammadreza.news.R;
import com.mohammadreza.news.constant.NewsConstant;
import com.mohammadreza.news.model.EventBusModel;
import com.mohammadreza.news.cropper.imageUtils.CropImageView;

import org.greenrobot.eventbus.EventBus;

import static com.mohammadreza.news.constant.NewsConstant.ASPECT_RATIO_X;
import static com.mohammadreza.news.constant.NewsConstant.ASPECT_RATIO_Y;
import static com.mohammadreza.news.constant.NewsConstant.DEFAULT_ASPECT_RATIO_VALUES;
import static com.mohammadreza.news.utils.Utils.getIntentExtras;
import static com.mohammadreza.news.cropper.imageUtils.MyImageCrop.progressCompressImageProfile;


public class CropActivity extends AppCompatActivity {
    // The 3 states (events) which the user is trying to perform
    static final int NONE = 0;
    private static final String TAG = "Touch";
    @SuppressWarnings("unused")
    private static final float MIN_ZOOM = 1f, MAX_ZOOM = 1f;
    private int mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES;
    private int mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES;


    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(ASPECT_RATIO_X, mAspectRatioX);
        bundle.putInt(ASPECT_RATIO_Y, mAspectRatioY);
    }

    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        mAspectRatioX = bundle.getInt(ASPECT_RATIO_X);
        mAspectRatioY = bundle.getInt(ASPECT_RATIO_Y);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_crop);


        Bitmap bitmap = getIntentExtras(this);

        final CropImageView cropImageView = findViewById(R.id.cropImageView);
        cropImageView.setGuidelines(NewsConstant.OFF);

        if (bitmap != null)
            cropImageView.setImageBitmap(bitmap);
        cropImageView.setFixedAspectRatio(true);
        cropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES, DEFAULT_ASPECT_RATIO_VALUES);


        findViewById(R.id.Button_crop).setOnClickListener(v -> {
            if (cropImageView.getCroppedCircleImage() != null) {
                BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), cropImageView.getCroppedCircleImage());
                EventBusModel busModel = new EventBusModel();
                busModel.setBitmapDrawable(bitmapDrawable);
                EventBus.getDefault().post(busModel);
            }
            finish();
        });

    }

    @Override
    public void onBackPressed() {
        if(progressCompressImageProfile!=null){
            progressCompressImageProfile.dismiss();
        }
        super.onBackPressed();
    }
}
