package com.mohammadreza.news.viewmodel;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mohammadreza.news.BR;
import com.mohammadreza.news.R;
import com.mohammadreza.news.constant.NewsConstant;
import com.mohammadreza.news.databinding.NewsActivityBinding;
import com.mohammadreza.news.model.NewsItemsRealmModel;
import com.mohammadreza.news.model.NewsModel;
import com.mohammadreza.news.model.NewsRealmModel;
import com.mohammadreza.news.repository.NewsRealmDatabase;
import com.mohammadreza.news.rss.adapter.RssItemListAdapter;
import com.mohammadreza.news.rss.api.RssApi;
import com.mohammadreza.news.rss.model.NewsItem;
import com.mohammadreza.news.rss.model.Rss;
import com.mohammadreza.news.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class NewsViewModel extends BaseObservable {


    public static RssItemListAdapter adapter;
    private CoordinatorLayout coordinatorRecyclerNews;
    private boolean isDataLoad;
    private AppCompatActivity appCompatActivity;


    private String newsTitle = "";
    private String newsLink = "";
    private String newsDescription = "";
    private String newsDate = "";
    private String newsImageUrl = "";

    private String newsCategory = "";
    private String urlCategory = "1";
    private String searchText = "";

    private boolean isLinearSearchVisible;
    private boolean isAppBarVisible;

    private ObservableBoolean isRefreshLoading = new ObservableBoolean();
    private ObservableInt isLinearNoAccessToNetVisible = new ObservableInt();
    private ObservableInt isLinearProgressLoadDataVisible = new ObservableInt();


    private MutableLiveData<List<NewsViewModel>> mutableLiveData = new MutableLiveData<>();
    private ArrayList<NewsViewModel> newsViewModels = new ArrayList<>();

    public NewsViewModel() {
    }

    public NewsViewModel(NewsActivityBinding newsActivityBinding, AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
        getNewsIntentData(appCompatActivity.getIntent());
        coordinatorRecyclerNews = newsActivityBinding.coordinatorRecyclerNews;
        setFabBehavior(newsActivityBinding.recyclerView, newsActivityBinding.fabGoToFirst);


    }

    private NewsViewModel(NewsItem newsItem) {
        this.newsTitle = newsItem.getTitle();
        this.newsLink = newsItem.getLink();
        this.newsDescription = newsItem.getDescription();
        this.newsDate = newsItem.getPublishDate();
        if (newsItem.getEnclosure() != null)
            this.newsImageUrl = newsItem.getEnclosure().getUrl();
    }


    private void getNewsIntentData(Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            setUrlCategory(intent.getExtras().getString(NewsConstant.NEWS_LINK));
            setNewsCategory(intent.getStringExtra(NewsConstant.NEWS_CATEGORY));
//            setNewsCategory(appCompatActivity.getString(R.string.news) + " " + intent.getStringExtra(NewsConstant.NEWS_CATEGORY));
            if (Utils.isOnline(appCompatActivity)) {
                getNewsDataWithRetrofit();
                isLinearNoAccessToNetVisible.set(View.GONE);
                isLinearProgressLoadDataVisible.set(View.VISIBLE);
            } else {
                if (getNewsFromDatabase()) {
                    isLinearNoAccessToNetVisible.set(View.VISIBLE);
                    isLinearProgressLoadDataVisible.set(View.GONE);
                }
            }
        }

    }

    @BindingAdapter({"app:recycler_news", "app:url_category"})
    public static void setRecyclerviewData(final RecyclerView recyclerView, final MutableLiveData<List<NewsViewModel>> liveData, String urlCategory) {

        int dataLoad = 0;

        liveData.observe((LifecycleOwner) recyclerView.getContext(), newsViewModels -> {
            adapter = new RssItemListAdapter(liveData.getValue(), urlCategory, (AppCompatActivity) recyclerView.getContext());
            LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
            recyclerView.setNestedScrollingEnabled(false);

        });

    }


    public void onRefresh() {
        if (isLinearProgressLoadDataVisible.get() != View.VISIBLE) {
            isRefreshLoading.set(true);
            getNewsDataWithRetrofit();
        }

    }


    public void onClickTryAgain(View v) {
        if (Utils.isOnline(v.getContext())) {
            getNewsDataWithRetrofit();
            isLinearProgressLoadDataVisible.set(View.VISIBLE);
            isLinearNoAccessToNetVisible.set(View.GONE);
        }

    }

    public void onClickBack() {
        appCompatActivity.finish();
    }

    public void onClickArrowUp(View v, RecyclerView recyclerView) {
        if (recyclerView.getChildCount() > 0) {
            recyclerView.smoothScrollToPosition(0);
        }

    }


    private void getNewsDataWithRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NewsConstant.MIZAN_RSS_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        RssApi channelApi = retrofit.create(RssApi.class);
        Call<Rss> apiCall = channelApi.getChannel(urlCategory);

        apiCall.enqueue(new Callback<Rss>() {
            @Override
            public void onResponse(Call<Rss> call, Response<Rss> response) {
                newsViewModels.clear();
                assert response.body() != null;
                List<NewsItem> items = response.body().getChannel().getItems();
//                if (items.size() > 0) {
//                    Utils.getTimeSpam(items.get(0).getPublishDate(), appCompatActivity);
//                }

                ArrayList<NewsItemsRealmModel> newsItemsRealmModels = new ArrayList<>();
                for (NewsItem item : items) {
                    NewsViewModel newsViewModel = new NewsViewModel(item);
                    newsViewModels.add(newsViewModel);

                    NewsItemsRealmModel newsItemsRealmModel = new NewsItemsRealmModel();
                    newsItemsRealmModel.setTitle(item.getTitle());
                    newsItemsRealmModel.setLink(item.getLink());
                    newsItemsRealmModel.setDesc(item.getDescription());
                    newsItemsRealmModel.setDate(item.getPublishDate());
                    newsItemsRealmModels.add(newsItemsRealmModel);
                }
                mutableLiveData.setValue(newsViewModels);
                isLinearNoAccessToNetVisible.set(View.GONE);
                isLinearProgressLoadDataVisible.set(View.GONE);
                isRefreshLoading.set(false);
                storeNewsInDatabase(newsItemsRealmModels);

                if (!isDataLoad) {
                    createSnakeBar(coordinatorRecyclerNews);
                }
                isDataLoad = true;

            }

            @Override
            public void onFailure(Call<Rss> call, Throwable t) {
                if (isRefreshLoading.get()) {
                    isRefreshLoading.set(false);
                    Utils.showAlertDialog(appCompatActivity, appCompatActivity.getResources().getString(R.string.no_access_to_net));
                } else {
                    isLinearNoAccessToNetVisible.set(View.VISIBLE);
                }
            }
        });
    }

    private void setFabBehavior(RecyclerView recyclerView, FloatingActionButton fabGoToFirst) {
        fabGoToFirst.hide();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy < 0 && !fabGoToFirst.isShown())
                    fabGoToFirst.show();
                else if (dy > 0 && fabGoToFirst.isShown())
                    fabGoToFirst.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (recyclerView != null) {
                    LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (manager != null) {
                        if (manager.findFirstVisibleItemPosition() == 0) {
                            fabGoToFirst.hide();
                        }
                    }
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    private void storeNewsInDatabase(ArrayList<NewsItemsRealmModel> newsItemsRealmModel) {
        NewsRealmModel newsRealmModel = new NewsRealmModel(urlCategory, newsItemsRealmModel);
        NewsRealmDatabase.storeNewsList(newsRealmModel, urlCategory, appCompatActivity);
    }

    private boolean getNewsFromDatabase() {
        NewsModel newsModel = NewsRealmDatabase.getNews(urlCategory, appCompatActivity);
        if (newsModel != null) {
            for (int i = 0; i < newsModel.getNewsViewModels().size(); i++) {
                NewsViewModel viewModel = new NewsViewModel();
                viewModel.setNewsTitle(newsModel.getNewsViewModels().get(i).getTitle());
                viewModel.setNewsLink(newsModel.getNewsViewModels().get(i).getLink());
                viewModel.setNewsDescription(newsModel.getNewsViewModels().get(i).getDesc());
                viewModel.setNewsDate(newsModel.getNewsViewModels().get(i).getDate());
                newsViewModels.add(viewModel);

                isLinearNoAccessToNetVisible.set(View.GONE);
                isLinearProgressLoadDataVisible.set(View.GONE);

            }
            mutableLiveData.setValue(newsViewModels);

            return false;
        } else {
            return true;
        }
    }

    public void createSnakeBar(CoordinatorLayout newsActivityBinding) {
        Snackbar.make(newsActivityBinding, appCompatActivity.getString(R.string.drag_to_down), Snackbar.LENGTH_LONG)
                .setBackgroundTint(appCompatActivity.getResources().getColor(R.color.colorPrimary))
                .setTextColor(appCompatActivity.getResources().getColor(R.color.colorWhite))
                .show();
    }

    @Bindable
    public ObservableBoolean getIsRefreshLoading() {
        return isRefreshLoading;
    }

    @Bindable
    public ObservableInt getIsLinearNoAccessToNetVisible() {
        return isLinearNoAccessToNetVisible;
    }

    @Bindable
    public ObservableInt getIsLinearProgressLoadDataVisible() {
        return isLinearProgressLoadDataVisible;
    }

    public void setIsRefreshLoading(ObservableBoolean isRefreshLoading) {
        this.isRefreshLoading = isRefreshLoading;
        notifyPropertyChanged(BR.isRefreshLoading);
    }

    public void setIsLinearNoAccessToNetVisible(ObservableInt isLinearNoAccessToNetVisible) {
        this.isLinearNoAccessToNetVisible = isLinearNoAccessToNetVisible;
        notifyPropertyChanged(BR.isLinearNoAccessToNetVisible);
    }

    public void setIsLinearProgressLoadDataVisible(ObservableInt isLinearProgressLoadDataVisible) {
        this.isLinearProgressLoadDataVisible = isLinearProgressLoadDataVisible;
        notifyPropertyChanged(BR.isLinearProgressLoadDataVisible);

    }

    public MutableLiveData<List<NewsViewModel>> getMutableLiveData() {
        return mutableLiveData;
    }


    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public void setNewsDescription(String newsDescription) {
        this.newsDescription = newsDescription;
    }

    public void setNewsDate(String newsDate) {
        this.newsDate = Utils.setPersianNumbers(newsDate);
    }

    @Bindable
    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
        notifyPropertyChanged(BR.searchText);

    }

    @Bindable
    public String getNewsTitle() {
        return newsTitle;
    }

    @Bindable
    public String getNewsDate() {
        return Utils.setPersianNumbers(newsDate);
    }

    @Bindable
    public String getNewsImageUrl() {
        return newsImageUrl;
    }


    @Bindable
    public String getNewsDescription() {
        return newsDescription;
    }


    @Bindable
    public String getNewsCategory() {
        return newsCategory;
    }

    @Bindable
    public boolean isLinearSearchVisible() {
        return isLinearSearchVisible;
    }

    @Bindable
    public boolean isAppBarVisible() {
        return isAppBarVisible;
    }

    public void setLinearSearchVisible(boolean linearSearchVisible) {
        isLinearSearchVisible = linearSearchVisible;
        notifyPropertyChanged(BR.linearSearchVisible);
    }


    public void setAppBarVisible(boolean appBarVisible) {
        isAppBarVisible = appBarVisible;
        notifyPropertyChanged(BR.appBarVisible);
    }

    public String getNewsLink() {
        return newsLink;
    }

    public void setNewsLink(String newsLink) {
        this.newsLink = newsLink;
    }

    public void setNewsCategory(String newsCategory) {
        this.newsCategory = newsCategory;
        notifyPropertyChanged(BR.newsCategory);
    }

    public void setUrlCategory(String urlCategory) {
        this.urlCategory = urlCategory;
    }

    public String getUrlCategory() {
        return urlCategory;
    }
}
