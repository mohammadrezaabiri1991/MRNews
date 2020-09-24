package com.mohammadreza.news.viewmodel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.lifecycle.MutableLiveData;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.mohammadreza.news.BR;
import com.mohammadreza.news.R;
import com.mohammadreza.news.constant.NewsConstant;
import com.mohammadreza.news.databinding.NewsCategoryActivityBinding;
import com.mohammadreza.news.model.EventBusModel;
import com.mohammadreza.news.model.UserModel;
import com.mohammadreza.news.repository.NewsRealmDatabase;
import com.mohammadreza.news.ui.NewsActivity;
import com.mohammadreza.news.ui.NewsCategoryActivity;
import com.mohammadreza.news.ui.RegisterActivity;
import com.mohammadreza.news.utils.GetPermissions;
import com.mohammadreza.news.utils.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.mohammadreza.news.cropper.imageUtils.MyImageCrop.progressCompressImageProfile;


public class NewsCategoryViewModel extends BaseObservable {
    private GridLayout gridLayout;
    private int id;
    private String fieldName;
    private String userName;
    private String imageProfileUri;
    private static NewsCategoryActivity appCompatActivity;
    private int userOnline = 0;
    private CircleImageView circleImageViewNewsCategory;

    //    private String categoryUrl = "";
    private Bitmap bitmap;
    private MutableLiveData<String> uriLiveDataList = new MutableLiveData<>();

    @SuppressLint("ClickableViewAccessibility")
    public NewsCategoryViewModel(NewsCategoryActivityBinding categoryActivityBinding) {
        appCompatActivity = (NewsCategoryActivity) categoryActivityBinding.getRoot().getContext();
        this.gridLayout = categoryActivityBinding.mGridLayoutCategory;
        this.circleImageViewNewsCategory = categoryActivityBinding.circleImageViewNewsCategory;

        registerBroadCastAndEventBus();
        setSizeToNewsCardView();
        getInfoFromIntent();
        GetPermissions.checkStoragePermission(appCompatActivity, NewsConstant.EXT_STORAGE_REQ_CODE, "");


    }

    private void registerBroadCastAndEventBus() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }


    public void getInfoFromIntent() {
        Intent intent = appCompatActivity.getIntent();
        if (intent != null) {
            UserModel model = (UserModel) intent.getSerializableExtra(NewsConstant.USER_MODEL_KEY);
            if (model != null) {
                id = model.getId();

                if (model.getEmailDisplayName() != null) {
                    setUserName(model.getEmailDisplayName());
                    fieldName = NewsConstant.EMAIL_DISPLAYED_NAME_FIELD;

                } else {
                    setUserName(model.getUserName());
                    fieldName = NewsConstant.USER_NAME_FIELD;
                }

                loadImageFromStorage(getUserName() + id, appCompatActivity);
                if (bitmap != null) {
                    circleImageViewNewsCategory.setImageBitmap(bitmap);
                } else {
                    if (model.getImageProfileUri() != null) {
                        setImageProfileUri(model.getImageProfileUri());
                    }
                }

                userOnline = model.getIsOnline();
            }
        }
    }

    public void setSizeToNewsCardView() {
        DisplayMetrics dm = appCompatActivity.getResources().getDisplayMetrics();
        int width = (dm.widthPixels / 3) - 11;
        for (int i = 1; i <= 17; i++) {
            setLayoutParams(gridLayout.getChildAt(i), width);
        }
    }

    public void setLayoutParams(View cardView, int densityDpi) {
        GridLayout.LayoutParams para = new GridLayout.LayoutParams();
        para.height = densityDpi;
        para.width = densityDpi;
        para.topMargin = 8;
        para.leftMargin = 8;
        cardView.setLayoutParams(para);
    }

    public void onCardClick(View view, String category) {
        Utils.callIntent(appCompatActivity, NewsActivity.class, NewsConstant.NEWS_LINK, view.getTag().toString(), NewsConstant.NEWS_CATEGORY, category);
    }

    public void onClickImgProfile(View view) {
        GetPermissions.checkStoragePermission(appCompatActivity, NewsConstant.EX_STORAGE_REQ_CODE, appCompatActivity.getResources().getString(R.string.com_from_profile));
    }

    @BindingAdapter("app:srcProfile")
    public static void setProfileImage(ImageView imageView, String uri) {
        if (uri != null && !uri.isEmpty())
            Picasso.get().load(uri).into(imageView);
    }

    @BindingAdapter("app:srcCardImage")
    public static void setNewsCardImage(final ImageView imageView, final String url) {
//        Utils.loadImageFromStorage(String.valueOf(imageView.getId()), appCompatActivity, imageView);
//        url.observe(appCompatActivity, strings -> {

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(() -> Utils.saveImageToInternalStorage(String.valueOf(imageView.getId()), bitmap, appCompatActivity)).start();
                imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }


            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };

        if (!url.isEmpty()) {
            Picasso.get().load(url).into(target);
        }


    }


//    public String getCategoryUrl() {
//        return categoryUrl;
//    }


    @Bindable
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        notifyPropertyChanged(BR.userName);
    }

    @Bindable
    public String getImageProfileUri() {
        return imageProfileUri;
    }

    public void setImageProfileUri(String imageProfileUri) {
        this.imageProfileUri = imageProfileUri;
        notifyPropertyChanged(BR.imageProfileUri);
    }

    @Subscribe()
    public void onEvent(EventBusModel event) {
        if (event.isClick()) {
            if (userOnline == 1) {
                if (userName != null && !userName.isEmpty() && !fieldName.isEmpty()) {
                    actionLogOut(fieldName, userName, appCompatActivity);
                    Utils.callIntent(appCompatActivity, RegisterActivity.class);
                    appCompatActivity.finish();
                }
            } else {
                Utils.callIntent(appCompatActivity, RegisterActivity.class);
                appCompatActivity.finish();
            }
        } else if (event.getBitmapDrawable() != null) {
            circleImageViewNewsCategory.setImageDrawable(event.getBitmapDrawable());
            Bitmap bitmap = event.getBitmapDrawable().getBitmap();
            Utils.saveImageToInternalStorage(getUserName() + id, bitmap, appCompatActivity);
            if (progressCompressImageProfile != null) {
                progressCompressImageProfile.dismiss();
            }

        }
        EventBus.getDefault().unregister(NewsCategoryViewModel.this);
    }

    public void loadImageFromStorage(String imageName, Context context) {
        imageName = imageName + ".jpg";
        ContextWrapper contextWrapper = new ContextWrapper(context);
        String path = contextWrapper.getDir(NewsConstant.IMAGE_DIR, Context.MODE_PRIVATE).getAbsolutePath();
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(new File(path, imageName)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void actionLogOut(String filedName, String requestValue, Context context) {
        NewsRealmDatabase.changeIsOnline(filedName, requestValue, NewsConstant.LOG_OUT, context);
        if (filedName.equals(NewsConstant.EMAIL_DISPLAYED_NAME_FIELD)) {
            logOut();
        }
    }

    public void logOut() {
        AccessToken.setCurrentAccessToken(null);
//        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
    }

}
