package com.example.hellodroid;

import androidx.annotation.NonNull;

public class RssItemViewModel {

    private String simpleText;
    private RssItem item;

    /**
     * A viewmodel to hold and modify the data passed into it
     *
     * @param simpleText
     *         The initial text
     * @param feedItem
     */
    public RssItemViewModel(@NonNull final String simpleText, RssItem feedItem) {
        //setSimpleText(simpleText);
        item = feedItem;
    }

    /**
     * Gets the text that has been set
     *
     * @return A String that represents the text that has been set
     */
    @NonNull
    public String getSimpleText() {
        return simpleText;
    }

    /**
     * While this is a basic project now, we could use this method to modify the
     * text after it was initially set
     *
     * @param simpleText
     *         The text that will be displayed in the itemview
     */
    public void setSimpleText(@NonNull final String simpleText) {
        this.simpleText = simpleText;
    }

    @NonNull
    public RssItem getItem() {
        return item;
    }

    public String getTitleText() {
        return item.getTitle();
    }

    public String getShortDescription() {
        String description = item.getDescription();

        if (description != null) {
            //Remove all images from the description as it won't render properly in the small view
            description = description.replaceAll("(<(/)img>)|(<img.+?>)", "");

            //Short description should split on newline
            int newLineLoc = description.indexOf("\r");
            if (newLineLoc < 0) {
                newLineLoc = description.indexOf("\n");
            }
            if (newLineLoc > 0) {
                description = description.substring(0);
            }

            //Limit it to a max of 57 characters.
            if (description.length() > 57) {
                description = description.substring(0, 54).concat("...");
            }

            return description;
        } else {
            return "";
        }
    }

}
