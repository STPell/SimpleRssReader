package com.example.hellodroid;

import android.view.View;

public interface RecyclerChannelViewLongClickListener {
    void onLongClick(View view, int position, RssChannelViewModel adapter);
}
