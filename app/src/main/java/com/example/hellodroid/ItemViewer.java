package com.example.hellodroid;

import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

public class ItemViewer extends AppCompatActivity {
    private RssItem item;
    private boolean inWebView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_viewer);

        item = (RssItem) getIntent().getSerializableExtra(ChannelFeed.ITEM_MESSAGE);

        createTextView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (item.hasUrl()) {
            getMenuInflater().inflate(R.menu.item_viewer_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.open_web_action) {
            openWebView();
        }

        return super.onOptionsItemSelected(item);
    }

    private void openWebView() {
        inWebView = true; //Mark that we're in the web view not the text view
        setContentView(R.layout.item_web_view);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle(item.getTitle());
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        WebView w = (WebView) findViewById(R.id.itemWebView);
        w.loadUrl(item.getUrl());

        //Start zoomed out
        w.getSettings().setUseWideViewPort(true);
        w.getSettings().setLoadWithOverviewMode(true);

        //Turn on zoom ability but turn off visible zoom controls
        w.getSettings().setBuiltInZoomControls(true);
        w.getSettings().setDisplayZoomControls(false);

        //Turn on javascript, as it may be needed by some sites.
        w.getSettings().setJavaScriptEnabled(true);
    }

    @Override
    public void onBackPressed() {
        if (inWebView) {
            //If we're in the web view, we go back to the text view, rather
            //than going back in the application.
            createTextView();
        } else {
            super.onBackPressed();
        }
    }


    public void createTextView() {
        inWebView = false; //Mark that we're in the text view not the web view

        setContentView(R.layout.activity_item_viewer);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle(item.getTitle());
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView text = (TextView) findViewById(R.id.itemViewText);
        text.setTextColor(Color.BLACK);
        ImageGetter imageGetter = new ImageGetter(text);
        text.setText(Html.fromHtml(item.getDescription(), 0, imageGetter, null));
    }

}