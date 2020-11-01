package com.example.hellodroid;

import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

public class ItemViewer extends AppCompatActivity {
    private RssItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_viewer);

        item = (RssItem) getIntent().getSerializableExtra(ChannelFeed.ITEM_MESSAGE);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle(item.getTitle());
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView text = (TextView) findViewById(R.id.itemViewText);
        text.setTextColor(Color.BLACK);
        text.setText(Html.fromHtml(item.getDescription(), 0, null, null));
    }
}