package com.mohammadreza.news.rss.api;

import androidx.annotation.Keep;

import com.mohammadreza.news.rss.model.Rss;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface RssApi {
    @GET
    Call<Rss> getChannel(@Url String url);
}