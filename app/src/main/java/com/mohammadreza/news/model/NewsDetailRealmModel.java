package com.mohammadreza.news.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class NewsDetailRealmModel extends RealmObject {
    @PrimaryKey
    String id = "";

    String detail = "";

    public NewsDetailRealmModel(NewsDetailModel newsDetailModel) {
        this.id = newsDetailModel.getId();
        this.detail = newsDetailModel.getDetail();
    }

    public NewsDetailRealmModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
