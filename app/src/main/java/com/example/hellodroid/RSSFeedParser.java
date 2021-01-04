package com.example.hellodroid;

import android.content.Context;
import android.util.Log;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

import static com.example.hellodroid.RssDocumentContext.values;

public class RSSFeedParser extends Thread {
    private List<String> urlList;

    private String[] rssLanguage_ = {"none", "channel", "title", "item", "link", "guid", "description", "language", "copyright",
                                    "managingEditor", "webMaster", "pubDate", "lastBuildDate", "category", "generator",
                                    "docs", "cloud", "ttl", "image", "rating", "textInput", "skipHours", "skipDays",
                                    "author", "comment", "enclosure", "source"};
    private List<String> rssLanguage = Arrays.asList(rssLanguage_);

    private List<RssChannel> feedChannels = new ArrayList<RssChannel>();
    private ReentrantLock feedChannelsLock = new ReentrantLock();

    List<RssChannelViewModel> displayList;
    RssChannelAdapter displayAdapter;
    private boolean local;
    private Context ctx;

    //Refresh complete counter and object
    private int counter;
    private ReentrantLock counterLock = new ReentrantLock();
    private SwipeRefreshLayout refresher;

    RSSFeedParser(List<String> url_, List<RssChannelViewModel> displayList_, RssChannelAdapter adapter, Context context) {
        urlList = url_;
        displayList = displayList_;
        displayAdapter = adapter;
        ctx = context;
    }

    @Override
    public void run() {
        if (local) {
            parseFromLocalResources();
        } else {
            parseFromRemoteResources();
        }
    }

    private void parseFromLocalResources() {
        String[] files = ctx.fileList();
        if (ctx.fileList().length > 0) {
            for (String fileName : files) {
                if (fileName.contains("RSS-")) {
                    //If it is a valid file RSS file, open it and parse it
                    try (FileInputStream fis = ctx.openFileInput(fileName);) {
                        InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
                        StringBuilder stringBuilder = new StringBuilder();
                        BufferedReader reader = new BufferedReader(inputStreamReader);

                        //First line of file is the URL
                        String url = reader.readLine();

                        String line = reader.readLine();
                        while (line != null) {
                            stringBuilder.append(line).append('\n');
                            line = reader.readLine();
                        }

                        String contents = stringBuilder.toString();
                        parseFeed(url, contents);
                        addChannelToInfo();
                    } catch (Exception e) {
                        // Error occurred when opening raw file for reading.
                        Log.e("EXCEPTION", e.toString());
                    }
                }
            }
        }
    }

    private void parseFromRemoteResources() {
        counter = urlList.size();

        for (String url: urlList) {
            try {
                StringRequest newRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            parseFeed(url, response);
                            addChannelToInfo();
                            saveResponseToFile(url, response);

                            //If we're the last thing to be parsed we should stop
                            //the refreshing icon.
                            counterLock.lock();
                            counter--;
                            if (counter == 0 && refresher != null) {
                                refresher.setRefreshing(false);
                            }
                            counterLock.unlock();
                        } catch (Exception e) {
                            Log.e("EXCEPTION", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("EXCEPTION", error.toString());
                    }
                });
                RssHttpRequestQueue.getInstance().addToRequestQueue(newRequest);
            } catch (Exception e) {
                Log.e("EXCEPTION", e.toString());
            }
        }
    }

    public void parseSingleFeed(String url) {
        try {
            StringRequest newRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        parseFeed(url, response);
                        addChannelToInfo();
                        saveResponseToFile(url, response);
                    } catch (Exception e) {
                        Log.e("EXCEPTION", e.toString());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("EXCEPTION", error.toString());
                }
            });
            RssHttpRequestQueue.getInstance().addToRequestQueue(newRequest);
        } catch (Exception e) {
            Log.e("EXCEPTION", e.toString());
        }
    }

    private void saveResponseToFile(String url, String response) {
        String fileName;
        feedChannelsLock.lock();
        fileName = "RSS-" + feedChannels.get(feedChannels.size() - 1).getTitle() + ".xml";
        feedChannelsLock.unlock();

        fileName = fileName.replaceAll("[:*?\"<>|&/ ]", "_");

        try (FileOutputStream fos = ctx.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            fos.write((url + "\n").getBytes());
            fos.write(response.getBytes());
        } catch (Exception e) {
            Log.e("EXCEPTION", e.toString());
        }
    }

    public void parseFeed(String url, String response) throws ParserConfigurationException, IOException, SAXException {
        StringReader reader = new StringReader(response);
        InputSource input = new InputSource(reader);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(input);
        doc.getDocumentElement().normalize();

        NodeList channels = doc.getElementsByTagName("channel");
        for (int i = 0; i < channels.getLength(); i++) {
            RssChannel rssChannel = new RssChannel();

            Node channel = channels.item(i);
            NodeList channel_contents  = channel.getChildNodes();

            for (int j = 0; j < channel_contents.getLength(); j++) {
                Node child = channel_contents.item(j);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    Element current_element = (Element) child;

                    int typeIndex = rssLanguage.indexOf(current_element.getTagName());

                    if (typeIndex > 0) {
                        RssDocumentContext field = values()[typeIndex];

                        switch (field) {
                            case ITEM:
                                rssChannel.addItem(processItemNode(child.getChildNodes()));
                                break;

                            case IMAGE:
                                rssChannel.addImage(processChannelImage(child.getChildNodes()));
                                break;

                            default:
                                rssChannel.addChannelInfo(field, current_element);
                        }
                    }
                }
            }

            rssChannel.setUrl(url);

            if (rssChannel.isValid()) {
                feedChannelsLock.lock();

                //Remove any prior channels
                Predicate<RssChannel> dupChanCheck = c -> c.getTitle().equals(rssChannel.getTitle()) &&
                                                          c.getUrl().equals(rssChannel.getUrl());
                feedChannels.removeIf(dupChanCheck);

                //Add the newly parsed channel
                feedChannels.add(rssChannel);
                feedChannelsLock.unlock();
            }
        }

    }

    private RssItem processItemNode(NodeList item_node) {
        RssItem item = new RssItem();

        for (int i = 0; i < item_node.getLength(); i++) {
            Node child = item_node.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) child;

                int typeIndex = rssLanguage.indexOf(e.getTagName());

                if (typeIndex > 0) {
                    RssDocumentContext field = values()[typeIndex];

                    item.addItemField(field, e);
                }
            }
        }

        return item;
    }

    private RssChannelImage processChannelImage(NodeList child) {
        return new RssChannelImage();
    }

    public int getNumChannels() {
        return feedChannels.size();
    }

    public void addChannelToInfo() {
        feedChannelsLock.lock();
        RssChannelViewModel e = new RssChannelViewModel(feedChannels.get(feedChannels.size() - 1).toString(), feedChannels.get(feedChannels.size() - 1));
        feedChannelsLock.unlock();
        displayList.add(e);

        displayAdapter.addItem(e);
    }

    public void setLocal(boolean b) {
        local = b;
    }

    public void setRefresher(SwipeRefreshLayout swipeRefreshLayout) {
        refresher = swipeRefreshLayout;
    }
}

