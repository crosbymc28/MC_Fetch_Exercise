package com.example.fetch_exercise;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RecyclerView data;
    private ItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        data = findViewById(R.id.data);
        data.setLayoutManager(new LinearLayoutManager(this));

        new FetchDataTask().execute("https://fetch-hiring.s3.amazonaws.com/hiring.json");
    }

    private class FetchDataTask extends AsyncTask<String, Void, Map<Integer, List<Item>>> {
        @Override
        protected Map<Integer, List<Item>> doInBackground(String... urls) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonString = null;

            try {
                // Open a connection to the URL
                URL url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the response from the server
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                jsonString = buffer.toString();
            } catch (IOException e) {
                Log.e("MainActivity", "Error fetching data", e);
                return null;
            } finally {
                // Close the connection and reader
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e("MainActivity", "Error closing stream", e);
                    }
                }
            }

            try {
                // Parse the JSON data
                return parseJson(jsonString);
            } catch (JSONException e) {
                Log.e("MainActivity", "Error parsing JSON", e);
                return null;
            }

        }

        @Override
        protected void onPostExecute(Map<Integer, List<Item>> groupedItems) {
            // Update the RecyclerView with the fetched data
            if (groupedItems != null) {
                List<Item> allItems = new ArrayList<>();

                for (List<Item> itemList : groupedItems.values()) {
                    allItems.addAll(itemList);
                }

                adapter = new ItemAdapter(allItems);
                data.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private Map<Integer, List<Item>> parseJson(String jsonString) throws JSONException {
        JSONArray jsonArray = new JSONArray(jsonString);
        List<Item> itemList = new ArrayList<>();

        // Loop through the JSON array and create Item objects
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            int id = jsonObject.getInt("id");
            int listId = jsonObject.getInt("listId");
            String name = jsonObject.optString("name", null);

            // Filter out items with null or blank names
            if (name != null && !name.trim().isEmpty() && name != "null") {
                itemList.add(new Item(id, listId, name));
            }
        }

        // Sort the items by listId and name
        Collections.sort(itemList, new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                if (o1.getListID() == o2.getListID()) {
                    return o1.getName().compareTo(o2.getName());
                }
                return Integer.compare(o1.getListID(), o2.getListID());
            }
        });

        // Group the items by listId
        Map<Integer, List<Item>> groupedItems = new HashMap<>();
        for (Item item : itemList) {
            if (!groupedItems.containsKey(item.getListID())) {
                groupedItems.put(item.getListID(), new ArrayList<>());
            }
            groupedItems.get(item.getListID()).add(item);
        }

        return groupedItems;
    }
}