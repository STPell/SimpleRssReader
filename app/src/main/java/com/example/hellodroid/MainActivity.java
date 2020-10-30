package com.example.hellodroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView message;
    private int counter = 0;
    public static final String CHANNEL_MESSAGE = "com.example.hellodroid.CHANNEL";

    RSSFeedParser parserThread; // = new RSSFeedParser("https://sssscomic.com/ssss-feed.xml");
    List<RssChannelViewModel> objectList;
    RssChannelAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        objectList = generateSimpleList();
        RecyclerChannelViewClickListener listener = (view, position, model) -> {
            Intent intent = new Intent(MainActivity.this, ChannelFeed.class);
            intent.putExtra(CHANNEL_MESSAGE, model.getChannel());
            startActivity(intent);
        };

        adapter = new RssChannelAdapter(objectList, listener);

        List<String> urlList = new ArrayList<String>();
        urlList.add("https://sssscomic.com/ssss-feed.xml");
        urlList.add("https://www.questionablecontent.net/QCRSS.xml");
        urlList.add("https://www.dumbingofage.com/feed/");

        parserThread = new RSSFeedParser(urlList, getCacheDir(), objectList, adapter);

        RecyclerView information = (RecyclerView) findViewById(R.id.testRecycler);
        RecyclerView.LayoutManager layout_manager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(information.getContext(),
                DividerItemDecoration.VERTICAL);
        information.addItemDecoration(dividerItemDecoration);
        information.setLayoutManager(layout_manager);
        information.setHasFixedSize(true);
        information.setAdapter(adapter);

    }

    private void tapDroid(){
        Log.w("SAM_INFO", "Starting new thread!");
        try {
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
        if (id == R.id.action_favorite) {
            objectList.clear();
            adapter.notifyDataSetChanged();

            tapDroid();
        }

        return super.onOptionsItemSelected(item);
    }
}