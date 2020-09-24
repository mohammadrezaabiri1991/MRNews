package com.mohammadreza.news.rss.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import java.io.Serializable;

@Root(name = "enclosure", strict = false)
public class Enclosure implements Serializable {
    @Attribute(name = "url")
    private String url;

    public String getUrl() {
        return url;
    }
}