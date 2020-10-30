package com.example.hellodroid;

import androidx.annotation.NonNull;

public class RssChannelViewModel {

    private String simpleText;
    private RssChannel channel;

    /**
     * A viewmodel to hold and modify the data passed into it
     *
     * @param simpleText
     *         The initial text
     * @param feedChannel
     */
    public RssChannelViewModel(@NonNull final String simpleText, RssChannel feedChannel) {
        setSimpleText(simpleText);
        channel = feedChannel;
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
    public RssChannel getChannel() {
        return channel;
    }
}
