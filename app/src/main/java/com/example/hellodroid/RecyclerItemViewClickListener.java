package com.example.hellodroid;

import android.view.View;

public interface RecyclerItemViewClickListener {
        void onClick(View view, int position, RssItemViewModel adapter);
}
