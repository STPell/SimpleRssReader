package com.example.hellodroid;

import android.util.Log;
import org.w3c.dom.Element;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

public class RssChannel implements Serializable {

    private String channel_url;
    private String description;
    private String link; //URL of the website associated with the feed
    private String title;
    private String category;
    private String cloud;
    private String copyright;
    private String docs;
    private String generator;
    private RssChannelImage image;
    private String language;
    private ZonedDateTime lastBuildDate;
    private long lastBuildDateUnix;
    private String managingEditor;
    private ZonedDateTime pubDate;
    private long pubDateUnix;
    private String rating;
    private String webmaster; //Email address of feed administrator

    private List<RssItem> items = new ArrayList<RssItem>();

    private static final DateTimeFormatter date_format = DateTimeFormatter.RFC_1123_DATE_TIME;

    RssChannel() {
        Log.d("CHANNEL", "New Channel");
    }

    public boolean addItem(RssItem newItem) {
        if (newItem.isValid()) {
            items.add(newItem);
            return true;
        }
        return false;
    }

    public boolean addImage(RssChannelImage newImage) {
        if (newImage.isValid()) {
            //image = newImage;
            return true;
        }

        return false;
    }

    public void addChannelInfo(RssDocumentContext field, Element e) {
        //Log.w("RSSChannelInfo", field.toString() + " " + e.getTextContent());
        switch (field) {
            case CLOUD:
                cloud = e.getTextContent();
                break;

            case CATEGORY:
                category = e.getTextContent();
                break;

            case DOCS:
                docs = e.getTextContent();
                break;

            case DESCRIPTION:
                description = e.getTextContent();
                break;

            case GENERATOR:
                generator = e.getTextContent();
                break;

            case LANGUAGE:
                language = e.getTextContent();
                break;

            case LASTBUILDDATE:
                if (e.getTextContent() != null) {
                    try {
                        lastBuildDate = ZonedDateTime.parse(e.getTextContent(), date_format);
                        lastBuildDateUnix = pubDate.getLong(ChronoField.INSTANT_SECONDS);
                    } catch (NullPointerException null_pointer) {
                        //Do nothing
                    }
                }
                break;

            case LINK:
                link = e.getTextContent();
                break;

            case MANAGINGEDITOR:
                managingEditor = e.getTextContent();
                break;

            case PUBDATE:
                if (e.getTextContent() != null) {
                    try {
                        pubDate = ZonedDateTime.parse(e.getTextContent(), date_format);
                        pubDateUnix = pubDate.getLong(ChronoField.INSTANT_SECONDS);
                    } catch (NullPointerException null_pointer) {
                        //Do nothing
                    }
                }
                break;

            case RATING:
                rating = e.getTextContent();
                break;

            case TITLE:
                title = e.getTextContent();
                break;

            case WEBMASTER:
                webmaster = e.getTextContent();
                break;
        }
    }

    public List<RssItem> getChannelItems() {
        return items;
    }

    public String toString() {
        return title;
    }

    public boolean isValid() {
        boolean has_title = title != null;
        boolean has_link = link != null;
        boolean has_description = description != null;
        return has_title && has_link && has_description;
    }

    public String getTitle() {
        return title;
    }

    public void setUrl(String url) {
        channel_url = url;
    }

    public String getUrl() {
        return channel_url;
    }
}
