package com.mohammadreza.news.model;

public class NewsDetailModel {

    String id = "";
    String detail = "";

    public NewsDetailModel(NewsDetailRealmModel newsDetailRealmModel) {
        this.id = newsDetailRealmModel.getId();
        this.detail = newsDetailRealmModel.getDetail();
    }

    public NewsDetailModel() {
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
