package com.mohammadreza.news.model;

import com.mohammadreza.news.viewmodel.NewsViewModel;

import java.io.Serializable;
import java.util.ArrayList;

import io.realm.RealmList;

public class NewsModel implements Serializable {
    private String title = "";
    private String link = "";
    private String desc = "";
    private String date = "";
    private String imageUrl = "";

    private ArrayList<NewsItemsRealmModel> newsViewModels;

    public NewsModel() {
    }


    public NewsModel(ArrayList<NewsViewModel> newsViewModels) {
        for (NewsViewModel viewModel : newsViewModels) {
            this.title = viewModel.getNewsTitle();
            this.link = viewModel.getNewsLink();
            this.desc = viewModel.getNewsDescription();
            this.date = viewModel.getNewsDate();
        }
    }

    public NewsModel(RealmList<NewsItemsRealmModel> newsItemsRealmModel) {
        newsViewModels = new ArrayList<>();
        newsViewModels.addAll(newsItemsRealmModel);
    }

    public NewsModel(NewsViewModel newsViewModel) {
        this.title = newsViewModel.getNewsTitle();
        this.link = newsViewModel.getNewsLink();
        this.desc = newsViewModel.getNewsDescription();
        this.date = newsViewModel.getNewsDate();
        this.imageUrl = newsViewModel.getNewsImageUrl();
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getDate() {
        return date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<NewsItemsRealmModel> getNewsViewModels() {
        return newsViewModels;
    }

    public String getLink() {
        return link;
    }
}
