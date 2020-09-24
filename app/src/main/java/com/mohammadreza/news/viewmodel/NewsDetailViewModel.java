package com.mohammadreza.news.viewmodel;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.mohammadreza.news.BR;
import com.mohammadreza.news.R;
import com.mohammadreza.news.constant.NewsConstant;
import com.mohammadreza.news.databinding.ActivityDetail;
import com.mohammadreza.news.model.NewsDetailModel;
import com.mohammadreza.news.model.NewsDetailRealmModel;
import com.mohammadreza.news.model.NewsModel;
import com.mohammadreza.news.receiver.NetworkStateReceiver;
import com.mohammadreza.news.repository.NewsRealmDatabase;
import com.mohammadreza.news.utils.Utils;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.Calendar;

import pl.droidsonroids.gif.GifImageView;

import static com.mohammadreza.news.application.App.TYPE_FACE;
import static com.mohammadreza.news.constant.NewsConstant.CONTAIN_VIDEO;
import static com.mohammadreza.news.constant.NewsConstant.STR_PATH_FOLDER;
import static com.mohammadreza.news.utils.Utils.getImageFile;
import static com.mohammadreza.news.utils.Utils.setPersianNumbers;

public class NewsDetailViewModel extends BaseObservable implements NetworkStateReceiver.NetworkStateReceiverListener {
    private ImageView mainImageView;
    private GridLayout mGridLayoutDetail;
    private Snackbar snackbar;
    private AppCompatActivity appCompatActivity;

    private String newsCategory = "";

    private String newsTitle = "";
    private String newsLink = "";
    private String newsDetail = "";
    private String newsDesc = "";
    private String newsDate = "";
    private String newsImageUrl = "";
    public NetworkStateReceiver networkStateReceivers;
    private boolean isOfflineMode;

    private boolean isProgressLoadDetailVisible;
    private boolean isLinkVisible;
    private boolean isDetailLoad;
    private String imageBasePath = "";
    private boolean isRefreshDetail;
    private String finalLink = "";
    private StringBuilder stringBuilder;


    public NewsDetailViewModel(AppCompatActivity appCompatActivity, ActivityDetail activityDetail) {
        this.appCompatActivity = appCompatActivity;

        snackbar = createSnakeBar(activityDetail.coordinatorParentDetail);
        activityDetail.txtTitleNewsDetail.setTypeface(TYPE_FACE);
        activityDetail.txtDescNewsDetail.setTypeface(TYPE_FACE);
        activityDetail.txtNewsDetail.setTypeface(TYPE_FACE);
        activityDetail.txtDateNewsDetail.setTypeface(TYPE_FACE);
        activityDetail.txtNewsDetailHeader.setTypeface(TYPE_FACE);

        mGridLayoutDetail = activityDetail.mGridLayoutDetail;
        mainImageView = activityDetail.imgNewsDetail;


        setSizeToImageViewNewsDetail(activityDetail.appBarrDetail);
        getInfoFromIntent();
        registerBroadCast();
        hideDetailWhenExpandingToolbar(activityDetail);

        if (!Utils.isOnline(appCompatActivity)) {
            isOfflineMode = true;
            setProgressLoadDetailVisible(false);
        }
    }

    private void hideDetailWhenExpandingToolbar(ActivityDetail activityDetail) {
        activityDetail.appBarrDetail.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                activityDetail.txtNewsDetailHeader.setVisibility(View.VISIBLE);

            } else if (verticalOffset == 0) {
                activityDetail.txtNewsDetailHeader.setVisibility(View.INVISIBLE);
                activityDetail.linearBackDetail.setBackground(appCompatActivity.getResources().getDrawable(R.drawable.back_detail_expand));
            }
        });


    }

    private void setSizeToImageViewNewsDetail(AppBarLayout appBarLayout) {
        DisplayMetrics dm = appCompatActivity.getResources().getDisplayMetrics();
        int width = (dm.widthPixels - 120);
        appBarLayout.getLayoutParams().height = width - 1;
    }

    private void getInfoFromIntent() {
        Intent intent = appCompatActivity.getIntent();
        if (intent != null) {
            String category = intent.getStringExtra(NewsConstant.NEWS_CATEGORY_KEY);
            NewsModel model = (NewsModel) intent.getSerializableExtra(NewsConstant.NEWS_MODEL_KEY);
            if (model != null) {
                setNewsTitle(model.getTitle());
                setNewsDesc(model.getDesc());
                setNewsDate(Utils.convertToPersianDate(appCompatActivity, model.getDate()));
                setNewsImageUrl(model.getImageUrl());
                checkIfLinkIsGet(model.getLink());

                if (category != null && !category.isEmpty()) {
                    setNewsCategory(category + "/ " + getNewsTitle());
                }
            }
        }
    }


    private void checkIfLinkIsGet(String link) {
        try {
            finalLink = link.substring(0, link.lastIndexOf(NewsConstant.STR_NEWS) + 11);
            String[] separated = finalLink.split(NewsConstant.STR_NEWS);
            imageBasePath = separated[1];

            setNewsLink(finalLink);
            NewsDetailModel newsDetailModel = NewsRealmDatabase.getNewsDetail(finalLink, appCompatActivity);
            if (newsDetailModel != null) {
                setNewsDetail(newsDetailModel.getDetail());
                if (newsDetailModel.getDetail().contains(appCompatActivity.getString(R.string.no_detail)) && !newsDetailModel.getDetail().isEmpty() ||
                        newsDetailModel.getDetail().contains(appCompatActivity.getString(R.string.news_video)) && !newsDetailModel.getDetail().isEmpty()) {
                    setLinkVisible(true);
                }
                setProgressLoadDetailVisible(false);
                isDetailLoad = true;

                getImageFile(imageBasePath, appCompatActivity, mGridLayoutDetail, 1);

            } else {

                if (getImageFile(imageBasePath, appCompatActivity, mGridLayoutDetail, 0)) {
                    isDetailLoad = true;
                } else {
                    setProgressLoadDetailVisible(true);
                    requestNewsDetailWithVolley(finalLink);
                }


            }
        } catch (StringIndexOutOfBoundsException e) {
            setNewsLink(appCompatActivity.getString(R.string.no_detail));
            setLinkVisible(true);
            setProgressLoadDetailVisible(false);
        }
    }

    private void requestNewsDetailWithVolley(String link) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, link,
                response -> {
                    deleteImageFolder(imageBasePath);
                    setProgressLoadDetailVisible(false);
                    setRefreshDetail(false);
                    stringBuilder = new StringBuilder();
//                    new Thread(() ->.start();
                    getResponseFromVolley(response, link);
//                    new Thread(() -> getResponseFromVolley(response, link)).start();

                }, error -> {
            setProgressLoadDetailVisible(false);
            setLinkVisible(false);
//            isDetailLoad = true;
            snackbar.show();
            setRefreshDetail(false);
        });

        Volley.newRequestQueue(appCompatActivity).add(stringRequest);
    }

    private void getResponseFromVolley(String response, String link) {

        Element docElement = Jsoup.parse(response).body();
        Elements bodySearch = docElement.getElementsByClass(NewsConstant.CONTAINER).get(0).getElementsByClass(NewsConstant.BODY_SEARCH);
        if (bodySearch.size() > 0) {
            Element bodySearch2 = bodySearch.get(0);

            Elements paragraph = bodySearch2.select(NewsConstant.PARAGRAPH);

            if (bodySearch2.children().size() > 0) {
                if (bodySearch2.text().contains(CONTAIN_VIDEO)) {
                    showNothingMore(appCompatActivity.getString(R.string.news_video));
                    Log.d("MY_SIZE", "CONTAIN_VIDEO  ");
                } else if (paragraph.size() > 0) {
                    addParagraph(paragraph, bodySearch2);
                    Log.d("MY_SIZE", "paragraph  ");
                } else {
                    getImageSet1(bodySearch2);
                    Log.d("MY_SIZE", "image  ");
                }
            }
        } else {
            showNothingMore(appCompatActivity.getString(R.string.no_detail));
        }

        appCompatActivity.runOnUiThread(() -> {
            setNewsDetail(stringBuilder.toString());
            NewsDetailModel model = new NewsDetailModel();
            model.setId(link);
            model.setDetail(stringBuilder.toString());
            NewsRealmDatabase.saveNewsDetail(new NewsDetailRealmModel(model), appCompatActivity);
        });
    }

    private void getImageSet1(Element bodySearch2) {
        for (Element bodySearch3 : bodySearch2.children()) {
            if (bodySearch3 != null && bodySearch3.text() != null && bodySearch3.children() != null && bodySearch3.children().size() > 0) {
                mGridLayoutDetail.removeAllViews();
                for (Element bodySearch4 : bodySearch3.children()) {
                    if (bodySearch4 != null && bodySearch4.children().size() > 0 && bodySearch4.className().contains(NewsConstant.IMAGE_SET)) {
                        Log.d("MY_SIZE", "IMAGE_SET  ");
                        for (Element childElement : bodySearch4.children()) {
                            createGridLayoutForImagesDetail(childElement);
                        }
                    } else if (bodySearch4 != null && bodySearch4.children().size() > 0) {
                        Log.d("MY_SIZE", "else  if ");
                        Element bodySearch5 = bodySearch4.children().get(0);
                        getImageSet2(bodySearch5, bodySearch2);
                    }
                }
            }
        }
    }

    private void addParagraph(Elements paragraph, Element bodySearch2) {
        stringBuilder.setLength(0);
        for (Element element : paragraph) {
            if (!element.text().contains(NewsConstant.READ_MORE) && !element.text().contains(NewsConstant.END_MESSAGE)
                    && !element.text().contains(NewsConstant.IN_THIS_CASE)) {
                stringBuilder.append(element.text());
                isDetailLoad = true;
                snackbar.dismiss();
            }
        }
        if (stringBuilder.length() <= 0) {
            mGridLayoutDetail.removeAllViews();

            if (bodySearch2.children() != null && bodySearch2.children().size() > 0) {
                Element textElement = bodySearch2.children().get(0);
                stringBuilder.append(textElement.text());
            }
            if (bodySearch2.children() != null && bodySearch2.children().size() > 1) {
                Element imageElement = bodySearch2.children().get(1);
                getImageSet3(imageElement, 0);


            }
        }
    }


    private void getImageSet2(Element bodySearch5, Element bodySearch2) {
        for (Element child : bodySearch5.children()) {
            if (child.className().contains(NewsConstant.IMAGE_SET)) {
                if (child.children().size() > 0) {
                    for (Element lastChild : child.children()) {
                        if (lastChild != null) {
                            createGridLayoutForImagesDetail(lastChild);
                        }
                    }
                }
            } else {
                if (!stringBuilder.toString().contains(bodySearch2.text())) {
                    stringBuilder.append(bodySearch2.text().trim());
                }
                Elements album_lis = bodySearch2.getElementsByClass(NewsConstant.ALBUM_LIST);
                if (album_lis != null && album_lis.size() > 0) {
                    getImageSet3(album_lis.get(0), 1);
                }
            }
        }

    }


    private void getImageSet3(Element imageElement, int comFrom) {
        if (imageElement != null && imageElement.children().size() > 0) {
            Element srcElement = imageElement.children().get(0);
            String srcValue = "";
            if (comFrom == 1) {
                srcValue = srcElement.attr(NewsConstant.HREF);
            } else {
                srcValue = srcElement.attr(NewsConstant.SRC_ATTR);
            }
            if (srcValue != null && !srcValue.isEmpty()) {
                String imageName = imageBasePath + 1;
                String url = NewsConstant.MIZAN_URL + srcValue;
                downloadImagesWithGlide(url, imageName, 1);
            }
        }
    }


    private void showNothingMore(String message) {
        appCompatActivity.runOnUiThread(() -> {
            stringBuilder.setLength(0);
            stringBuilder.append(message);
            setLinkVisible(true);
            isDetailLoad = true;
            snackbar.dismiss();
        });
    }

    //    TODO
    private void createGridLayoutForImagesDetail(Element rowImage) {
        if (rowImage != null) {
            if (rowImage.children().size() > 0) {
                String imageName = imageBasePath + Calendar.getInstance().getTimeInMillis();
                String url = NewsConstant.MIZAN_URL + rowImage.children().get(0).attr(NewsConstant.HREF);
                if (!url.isEmpty()) {
                    downloadImagesWithGlide(url, imageName, 0);
                }
            }
        }


    }

    private void downloadImagesWithGlide(String url, String imageName, int comFrom) {
        if (appCompatActivity.isFinishing()) {
            return;
        }
//        appCompatActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
        pl.droidsonroids.gif.GifImageView imageView = new GifImageView(appCompatActivity);
        Utils.setSizeToNewsDetailImage(imageView, appCompatActivity);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        if (comFrom == 1) {
//                    mGridLayoutDetail.setRowCount(1);
//                    mGridLayoutDetail.setColumnCount(1);
            Utils.setGravityToImageView(imageView, appCompatActivity);

        }

        Glide.with(appCompatActivity)
                .asBitmap()
                .load(url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Bitmap bitmap = Bitmap.createScaledBitmap(resource, 250, 250, false);
                        imageView.setImageBitmap(bitmap);
                        Utils.saveImageFile(bitmap, imageBasePath, imageName);
                        isDetailLoad = true;
                    }

                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        imageView.setImageResource(R.drawable.gif);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
        mGridLayoutDetail.addView(imageView);
    }
//        });
//
//    }

    private void registerBroadCast() {
        networkStateReceivers = new NetworkStateReceiver();
        networkStateReceivers.addListener(this);
        appCompatActivity.registerReceiver(networkStateReceivers, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public void onRefresh() {
        setRefreshDetail(true);
        if (!getNewsDetail().isEmpty()) {
            setRefreshDetail(false);
            return;
        }

        if (!finalLink.isEmpty()) {
            requestNewsDetailWithVolley(finalLink);
        } else {
            setRefreshDetail(false);
        }

    }

    private void deleteImageFolder(String imageBaseName) {
        try {
            File direct = new File(Environment.getExternalStorageDirectory() + STR_PATH_FOLDER);
            File finalFolder = new File(direct + "/" + imageBaseName);

            if (finalFolder.isDirectory() && finalFolder.exists()) {
                String[] children = finalFolder.list();
                if (children != null)
                    for (int i = 0; i < children.length; i++) {
                        new File(finalFolder, children[i]).delete();
                    }
                finalFolder.mkdirs();
                Utils.createNoMediaFile(finalFolder);
            }
        } catch (Exception ignore) {
        }
    }

    public Snackbar createSnakeBar(CoordinatorLayout coordinatorLayout) {
        return Snackbar.make(coordinatorLayout, appCompatActivity.getString(R.string.no_access_to_net), Snackbar.LENGTH_INDEFINITE)
                .setBackgroundTint(appCompatActivity.getResources().getColor(R.color.colorPrimary))
                .setTextColor(appCompatActivity.getResources().getColor(R.color.colorWhite));
    }

    @BindingAdapter({"app:srcNewsImage", "app:txtNewsTitle"})
    public static void setNewsImage(ImageView imageView, String url, String newsTitle) {
        if (url != null && !url.isEmpty()) {
            Picasso.get().load(url).into(imageView);
        } else {
            Utils.loadImageFromStorage(newsTitle, (AppCompatActivity) imageView.getContext(), imageView);
        }
    }

    public void onClickBack() {
        appCompatActivity.finish();
    }


    @Override
    public void networkUnAvailable() {
        if (!isDetailLoad) {
            snackbar.show();
        } else {
            snackbar.dismiss();
        }
    }

    @Override
    public void networkAvailable() {
        if (isOfflineMode) {
            if (mainImageView.getDrawable() == null) {
                Picasso.get().load(newsImageUrl).into(mainImageView);
            }


            if (getNewsDetail().isEmpty() || !isDetailLoad) {
                setProgressLoadDetailVisible(true);
                snackbar.dismiss();
                requestNewsDetailWithVolley(getNewsLink());
            }
        }
        isOfflineMode = false;


    }


    @Bindable
    public boolean isProgressLoadDetailVisible() {
        return isProgressLoadDetailVisible;
    }


    @Bindable
    public String getNewsTitle() {
        return newsTitle;
    }


    @Bindable
    public String getNewsLink() {
        return newsLink;
    }


    @Bindable
    public String getNewsDesc() {
        return newsDesc;
    }

    @Bindable
    public String getNewsDate() {
        return setPersianNumbers(newsDate);

    }

    @Bindable
    public String getNewsImageUrl() {
        return newsImageUrl;
    }

    @Bindable
    public String getNewsDetail() {
        return newsDetail;
    }

    @Bindable
    public boolean isLinkVisible() {
        return isLinkVisible;
    }


    @Bindable
    public String getNewsCategory() {
        return newsCategory;
    }

    @Bindable
    public boolean isRefreshDetail() {
        return isRefreshDetail;
    }

    public void setRefreshDetail(boolean refreshDetail) {
        isRefreshDetail = refreshDetail;
        notifyPropertyChanged(BR.refreshDetail);

    }

    public void setNewsCategory(String newsCategory) {
        this.newsCategory = newsCategory;
        notifyPropertyChanged(BR.newsCategory);
    }

    public void setLinkVisible(boolean linkVisible) {
        isLinkVisible = linkVisible;
        notifyPropertyChanged(BR.linkVisible);
    }

    public void setNewsDetail(String newsDetail) {
        this.newsDetail = newsDetail;
        notifyPropertyChanged(BR.newsDetail);
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
        notifyPropertyChanged(BR.newsTitle);
    }

    public void setNewsDesc(String newsDesc) {
        this.newsDesc = newsDesc;
        notifyPropertyChanged(BR.newsDesc);
    }

    public void setNewsDate(String newsDate) {
        this.newsDate = newsDate;
        notifyPropertyChanged(BR.newsDate);
    }

    public void setNewsImageUrl(String newsImageUrl) {
        this.newsImageUrl = newsImageUrl;
        notifyPropertyChanged(BR.newsImageUrl);
    }

    public void setNewsLink(String newsLink) {
        this.newsLink = newsLink;
        notifyPropertyChanged(BR.newsLink);
    }

    public void setProgressLoadDetailVisible(boolean progressLoadDetailVisible) {
        isProgressLoadDetailVisible = progressLoadDetailVisible;
        notifyPropertyChanged(BR.progressLoadDetailVisible);
    }


}
