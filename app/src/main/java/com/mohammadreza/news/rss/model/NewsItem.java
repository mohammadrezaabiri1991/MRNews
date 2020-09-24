package com.mohammadreza.news.rss.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

// تگ های فرعی در xml می باشد

@Root(name = "item", strict = false)
public class NewsItem implements Serializable {

    @Element(name = "title")
    private String title;

    @Element(name = "link")
    private String link;

    @Element(required = false, name = "description")
    private String description;


    @Element(required = false, name = "enclosure")
    private Enclosure enclosure;

    @Element(required = false, name = "pubDate")
    private String publishDate;


    public String getTitle() {
        return title;
    }


    public String getDescription() {
        return description;
    }


    public Enclosure getEnclosure() {
        return enclosure;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public String getLink() {
        return link;
    }
}
