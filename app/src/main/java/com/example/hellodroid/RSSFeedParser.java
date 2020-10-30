package com.example.hellodroid;

import android.util.Log;
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
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

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

    private RequestQueue requestQueue;

    // Instantiate the cache
    private DiskBasedCache cache;

    // Set up the network to use HttpURLConnection as the HTTP client.
    private BasicNetwork network;

    List<RssChannelViewModel> displayList;
    RssChannelAdapter displayAdapter;

    RSSFeedParser(List<String> url_, File cacheDir, List<RssChannelViewModel> displayList_, RssChannelAdapter adapter) {
        urlList = url_;
        displayList = displayList_;
        displayAdapter = adapter;

        cache = new DiskBasedCache(cacheDir, 1024 * 1024); // 1MB cap
        network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
    }

    @Override
    public void run() {
        for (String url: urlList) {
            try {
                StringRequest newRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            tapDroid(response);
                            addChannelToInfo();
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
                requestQueue.add(newRequest);
            } catch (Exception e) {
                Log.e("EXCEPTION", e.toString());
            }
        }
    }

    public void tapDroid(String response) throws ParserConfigurationException, IOException, SAXException {
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

            if (rssChannel.isValid()) {
                feedChannelsLock.lock();
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
}
