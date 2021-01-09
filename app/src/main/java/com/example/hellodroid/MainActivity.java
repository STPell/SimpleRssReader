package com.example.hellodroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView message;
    private int counter = 0;
    public static final String CHANNEL_MESSAGE = "com.example.hellodroid.CHANNEL";

    RSSFeedParser parserThread;
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
        RecyclerChannelViewClickListener clickListener = (view, position, model) -> {
            Intent intent = new Intent(MainActivity.this, ChannelFeedActivity.class);
            intent.putExtra(CHANNEL_MESSAGE, model.getChannel());
            startActivity(intent);
        };

        Context context = this.getApplicationContext();

        RecyclerChannelViewLongClickListener longClickListener = new RecyclerChannelViewLongClickListener() {
            @Override
            public void onLongClick(View view, int position, RssChannelViewModel adapter) {
                PopupMenu popup = new PopupMenu(context, view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.channel_context_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Log.e("Context Menu Pressed", "Hello");
                        switch (item.getItemId()) {
                            case R.id.delete_channel:
                                String fileName = "RSS-" + adapter.getChannel().getTitle() + ".xml";
                                fileName = fileName.replaceAll("[:*?\"<>|&/ ]", "_");
                                File dir = getFilesDir();
                                File file = new File(dir, fileName);
                                file.delete();

                                parserThread.removeFeed(position, adapter.getChannel().getUrl());

                                return true;

                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        };

        adapter = new RssChannelAdapter(objectList, clickListener, longClickListener);

        urlList = new ArrayList<String>();

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