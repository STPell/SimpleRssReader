package com.example.hellodroid;

import android.view.View;

public interface RecyclerChannelViewClickListener {

    void onClick(View view, int position, RssChannelViewModel adapter);
}
