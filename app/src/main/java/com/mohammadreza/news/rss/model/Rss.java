package com.mohammadreza.news.rss.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;


// تک اصلی در xml می باشد

@Root(name = "rss", strict = false)
public class Rss implements Serializable {

    @Element(name = "channel")
    private Channel channel;

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
