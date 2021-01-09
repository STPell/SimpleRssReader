package com.example.hellodroid;

import android.content.Context;
import android.util.Log;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;

import static android.content.Context.MODE_APPEND;

public class RssReadItemSet {
    private static RssReadItemSet instance;
    private Context ctx;
    private HashSet<String> set;

    private RssReadItemSet(Context context) {
        ctx = context;
        set = new HashSet<String>();
        loadPreviouslyRead();
    }

    private void loadPreviouslyRead() {
        try (FileInputStream fis = ctx.openFileInput("prevreadids.dat");) {
            InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String line = reader.readLine();
            while (line != null) {
                set.add(line);
                line = reader.readLine();
            }
        } catch (Exception e) {
            // Error occurred when opening raw file for reading.
            Log.e("EXCEPTION", e.toString());
        }
    }

    public static synchronized RssReadItemSet getInstance(Context context) {
        if (instance == null) {
            instance = new RssReadItemSet(context);
        }
        return instance;
    }

    public static synchronized RssReadItemSet getInstance() {
        return instance;
    }

    public boolean contains(String id) {
        return set.contains(id);
    }

    public void add(String id) {
        if (set.add(id)) {
            //If it isn't in the set already it a HashSet returns true when it is added
            try {
                FileOutputStream fis = ctx.openFileOutput("prevreadids.dat", MODE_APPEND);
                OutputStreamWriter file_writer = new OutputStreamWriter(fis);
                BufferedWriter buffered_writer = new BufferedWriter(file_writer);
                buffered_writer.write(id + "\n");
                buffered_writer.close();
            } catch (IOException e) {
                Log.e("RssReadItemSet", "Failed to open the previously read items file");
            }
        }
    }
}
