package com.mohammadreza.news.model;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class NewsRealmModel extends RealmObject {

    @PrimaryKey
    private String url;
    private RealmList<NewsItemsRealmModel> realmModels;


    public NewsRealmModel() {
    }

    public NewsRealmModel(String url, ArrayList<NewsItemsRealmModel> newsItemsRealmModels) {
        realmModels = new RealmList<>();
        this.url = url;
        realmModels.addAll(newsItemsRealmModels);
    }

    public RealmList<NewsItemsRealmModel> getNewsItemsRealmModel() {
        return realmModels;
    }

    public void setRealmModels(RealmList<NewsItemsRealmModel> realmModels) {
        this.realmModels = realmModels;
    }
}
