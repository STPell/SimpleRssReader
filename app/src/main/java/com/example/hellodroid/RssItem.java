package com.example.hellodroid;

import android.util.Log;
import org.w3c.dom.Element;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;

public class RssItem implements Serializable {

    private String author; //Email address of the Author
    private String category;
    private String comments; //URL of the comment section
    private String description;
    private String guid; //Unique ID string
    private ZonedDateTime pubDate;
    private long pubDateUnix;
    private String link; //Web page associated with the feed item
    private String title;
    private String channelUrl;

    private boolean opened = false;
    private boolean flagged = false;

    private final static DateTimeFormatter date_format = DateTimeFormatter.RFC_1123_DATE_TIME;

    RssItem(String channelUrl) {
        channelUrl = channelUrl;
    }

    public Boolean isValid() {
        Log.d("ItemResult", this.asString());

        return (title != null) || (description != null);
    }

    private String asString() {
        if (pubDate != null) {
            return "'" + title + "' by " + author + ". Published on " + pubDate.toString() + "\n\t" + description;
        } else {
            return "'" + title + "' by " + author + ".\n\t" + description;
        }
    }

    public void addItemField(RssDocumentContext field, Element e) {
        switch (field) {
            case AUTHOR:
                author = e.getTextContent();
                break;

            case CATEGORY:
                category = e.getTextContent();
                break;

            case COMMENT:
                comments = e.getTextContent();
                break;

            case DESCRIPTION:
                description = e.getTextContent();
                break;

            case ENCLOSURE:
                //Do nothing
                break;

            case GUID:
                guid = e.getTextContent();
                break;

            case LINK:
                link = e.getTextContent();
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

            case TITLE:
                title = e.getTextContent();
                break;
        }
    }

    public void markAsRead() {
        opened = true;
    }

    public void markFlagged(boolean flag) {
        flagged = flag;
    }

    public boolean viewFlag() {
        return flagged;
    }

    public boolean isRead() {
        return opened;
    }

    public String getTitle() {
        return title;
    }

    public String toString() {
        if (title != null) {
            return title;
        } else {
            return "";
        }
    }

    public String getDescription() {
        if (description != null) {
            return description;
        } else {
            return "";
        }
    }

    public Boolean hasUrl() {
        return link != null;
    }

    public String getUrl() {
        return link;
    }

    public String getGuid() { return guid; }

    public String getUniqueId() {
        //Search for item in the read list
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            try {
                digest = MessageDigest.getInstance("SHA-1");
            } catch (NoSuchAlgorithmException f) {
                Log.e("RssFeedParser", "Cannot get an appropriate hash function to check for read status. Giving up");
                return "";
            }
        }
        String uniqueID = channelUrl + guid;
        digest.update(uniqueID.getBytes(StandardCharsets.UTF_8));

        return bytesToHexString(digest.digest());
    }

    private static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
