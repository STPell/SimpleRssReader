package com.example.hellodroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView message;
    private int counter = 0;
    public static final String CHANNEL_MESSAGE = "com.example.hellodroid.CHANNEL";

    RSSFeedParser parserThread; // = new RSSFeedParser("https://sssscomic.com/ssss-feed.xml");
    List<RssChannelViewModel> objectList;
    RssChannelAdapter adapter;
    List<String> urlList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Create a universal request queue
        RssHttpRequestQueue.getInstance(this.getApplicationContext());

        objectList = generateSimpleList();
        RecyclerChannelViewClickListener listener = (view, position, model) -> {
            Intent intent = new Intent(MainActivity.this, ChannelFeed.class);
            intent.putExtra(CHANNEL_MESSAGE, model.getChannel());
            startActivity(intent);
        };

        adapter = new RssChannelAdapter(objectList, listener);

        urlList = new ArrayList<String>();
        urlList.add("https://sssscomic.com/ssss-feed.xml");
        urlList.add("https://www.questionablecontent.net/QCRSS.xml");
        urlList.add("https://www.dumbingofage.com/feed/");
        urlList.add("https://www.thirtythreeforty.net/posts/index.xml");
        urlList.add("https://www.rssboard.org/files/sample-rss-092.xml");
        urlList.add("https://www.rssboard.org/files/sample-rss-091.xml");
        urlList.add("https://www.rssboard.org/files/sample-rss-2.xml");

        parserThread = new RSSFeedParser(urlList, objectList, adapter, getApplicationContext());

        RecyclerView information = (RecyclerView) findViewById(R.id.testRecycler);
        RecyclerView.LayoutManager layout_manager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(information.getContext(),
                DividerItemDecoration.VERTICAL);
        information.addItemDecoration(dividerItemDecoration);
        information.setLayoutManager(layout_manager);
        information.setHasFixedSize(true);
        information.setAdapter(adapter);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                parserThread.setLocal(false);
                parserThread.setRefresher(swipeRefreshLayout);
                parserThread.run();
            }
        });

        parserThread.setLocal(true);
        parserThread.run();
    }

    private void refreshList() {
        Log.w("SAM_INFO", "Starting new thread!");
        try {
            parserThread.setLocal(false);
            parserThread.run();
            Log.w("NUM_CHANNELS", "" + parserThread.getNumChannels());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper method which will generate a basic ArrayList of 100 items
     *
     * @return A List of SimpleViewModels
     */
    private List<RssChannelViewModel> generateSimpleList() {
        List<RssChannelViewModel> simpleViewModelList = new ArrayList<>();

        return simpleViewModelList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            objectList.clear();
            adapter.notifyDataSetChanged();

            refreshList();
        }

        if (id == R.id.action_addfeed) {
            //Add a feed
            Log.w("ADD_FEED", "I should add a feed to the URL list and reload the display!");
            addFeedDialogue();
        }

        return super.onOptionsItemSelected(item);
    }

    private void addFeedDialogue() {
        NewFeedDialog dialog = new NewFeedDialog();
        dialog.setReturnLocation(urlList);
        dialog.setRssFeedParser(parserThread);
        dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }
}