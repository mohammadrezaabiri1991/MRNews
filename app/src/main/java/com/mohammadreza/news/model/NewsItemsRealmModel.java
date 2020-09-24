package com.mohammadreza.news.model;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public class NewsItemsRealmModel extends RealmObject {

    private String title;
    private String link;
    private String desc;
    private String date;

    private RealmList<NewsItemsRealmModel> newsItemsRealmModels;


    public NewsItemsRealmModel() {
    }

    public NewsItemsRealmModel(NewsModel newsModel) {
        this.title = newsModel.getTitle();
        this.link = newsModel.getLink();
        this.desc = newsModel.getDesc();
        this.date = newsModel.getDate();
    }

    public NewsItemsRealmModel(ArrayList<NewsModel> newsModels) {
        newsItemsRealmModels = new RealmList<>();
        for (NewsModel newsModel : newsModels) {
            NewsItemsRealmModel newsItemsRealmModel = new NewsItemsRealmModel();
            newsItemsRealmModel.setTitle(newsModel.getTitle());
            newsItemsRealmModel.setLink(newsModel.getLink());
            newsItemsRealmModel.setDesc(newsModel.getDesc());
            newsItemsRealmModel.setDate(newsModel.getDate());
            newsItemsRealmModels.add(newsItemsRealmModel);
        }
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}