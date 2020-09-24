package com.mohammadreza.news.rss.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

// تک فرعی در xml می باشد

@Root(name = "channel", strict = false)
public class Channel {

    @Element(name = "title")
    private String title;

    @Element(required = false, name = "lastBuildDate")
    private String lastBuildDate;

    @ElementList(inline = true, name = "item")
    private List<NewsItem> items;

    public Channel() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLastBuildDate() {
        return lastBuildDate;
    }

    public void setLastBuildDate(String lastBuildDate) {
        this.lastBuildDate = lastBuildDate;
    }

    public List<NewsItem> getItems() {
        return items;
    }

    public void setItems(List<NewsItem> items) {
        this.items = items;
    }
}
