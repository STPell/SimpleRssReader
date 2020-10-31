package com.example.hellodroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChannelFeed extends AppCompatActivity {
    RssChannel channel;
    List<RssItemViewModel> objectList = new ArrayList<RssItemViewModel>();
    RssItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_feed);

        channel = (RssChannel) getIntent().getSerializableExtra(MainActivity.CHANNEL_MESSAGE);

        RecyclerItemViewClickListener listener = (view, position, model) -> {
            Log.w("CLICKED", "Clicked entry " + position + " (" + model.getItem().getTitle() + ")");
        };

        adapter = new RssItemAdapter(objectList, listener);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle(channel.getTitle());
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        RecyclerView information = (RecyclerView) findViewById(R.id.channelTestRecycler);
        RecyclerView.LayoutManager layout_manager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(information.getContext(),
                DividerItemDecoration.VERTICAL);
        information.addItemDecoration(dividerItemDecoration);
        information.setLayoutManager(layout_manager);
        information.setHasFixedSize(true);
        information.setAdapter(adapter);

        List<RssItem> items = channel.getChannelItems();

        for (RssItem r: items) {
            RssItemViewModel e = new RssItemViewModel(r.toString(), r);
            objectList.add(e);
            adapter.addItem(e);
        }
    }
}