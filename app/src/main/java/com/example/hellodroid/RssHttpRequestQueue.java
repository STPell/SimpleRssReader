package com.example.hellodroid;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

import java.lang.ref.ReferenceQueue;

public class RssHttpRequestQueue {
    private static RssHttpRequestQueue instance;

    private RequestQueue requestQueue;
    private DiskBasedCache cache;
    private BasicNetwork network;
    private Context ctx;

    private RssHttpRequestQueue(Context context) {
        ctx = context;
        cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024); // 1MB cap
        network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
    }

    public static synchronized RssHttpRequestQueue getInstance(Context context) {
        if (instance == null) {
            instance = new RssHttpRequestQueue(context);
        }
        return instance;
    }

    public static synchronized RssHttpRequestQueue getInstance() {
        return instance;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}
