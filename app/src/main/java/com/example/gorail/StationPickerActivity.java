package com.example.gorail;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gorail.adapters.RecentSearchesAdapter;
import com.example.gorail.adapters.StationAdapter;
import com.example.gorail.model.Station;
import com.example.gorail.model.StationResponse;
import com.google.gson.Gson;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StationPickerActivity extends AppCompatActivity {
    private EditText searchInput;
    private RecyclerView stationRecyclerView, nearbyRecentRecyclerView;
    private ProgressBar progressBar;
    private ImageView backBtn;
    private LinearLayout RecentLayout, NearByLayout, StationNameBox;
    private TextView stationpickerHeader;
    private StationAdapter stationAdapter;
    private RecentSearchesAdapter recentSearchAdapter;
    private List<Station> stationList = new ArrayList<>();
    private List<Station> searchResultsList = new ArrayList<>();


    private static final String API_KEY = "1aca12ae0b61019a6e9a5c36eeb3bf7e";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_picker);

        searchInput = findViewById(R.id.searchInput);
        stationpickerHeader = findViewById(R.id.stationpicker_heading1);
        nearbyRecentRecyclerView = findViewById(R.id.recentlySearchedRecyclerView);
        stationRecyclerView = findViewById(R.id.stationRecyclerView);
        RecentLayout = findViewById(R.id.recentlySearchedLayout);
        NearByLayout = findViewById(R.id.nearbyStationLayout);
        progressBar = findViewById(R.id.progressBar);
        StationNameBox = findViewById(R.id.station);
        backBtn = findViewById(R.id.back_btn3);

        backBtn.setOnClickListener(v -> {
            startActivity(new Intent(StationPickerActivity.this, MainActivity.class));
        });

        // Set LayoutManagers for RecyclerViews
        stationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        nearbyRecentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // ✅ Initialize recentSearchList to prevent null pointer issues
        searchResultsList = new ArrayList<>();

        // ✅ Initialize the Adapter
        recentSearchAdapter = new RecentSearchesAdapter(searchResultsList, station -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selected_station", station.getStationName() + " (" + station.getStationCode() + ")");
            setResult(RESULT_OK, resultIntent);
            saveRecentSearch(station.getStationName(), station.getStationCode());
            finish();
        });

        // ✅ Set the adapter to the RecyclerView
        nearbyRecentRecyclerView.setAdapter(recentSearchAdapter);

        // ✅ Now load recent searches (only after adapter is set)
        loadRecentSearches();

        // Initialize stationAdapter
        stationAdapter = new StationAdapter(new ArrayList<>(), station -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selected_station", station.getStationName() + " (" + station.getStationCode() + ")");
            setResult(RESULT_OK, resultIntent);
            saveRecentSearch(station.getStationName(), station.getStationCode());
            finish();
        });



        stationRecyclerView.setAdapter(stationAdapter);
        stationRecyclerView.setVisibility(View.VISIBLE);

        // ✅ Your TextWatcher is still here 👇
        searchInput.addTextChangedListener(new TextWatcher() {
            private Handler handler = new Handler();
            private Runnable workRunnable;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String formattedText = s.toString().toUpperCase().replace(" ", "");

                if (!formattedText.equals(s.toString())) {
                    searchInput.setText(formattedText);
                    searchInput.setSelection(formattedText.length());
                }

                if (formattedText.isEmpty()) {
                    StationNameBox.setVisibility(View.VISIBLE);
                    RecentLayout.setVisibility(View.VISIBLE);
                    stationpickerHeader.setText("Nearby Stations");
                    NearByLayout.setVisibility(View.VISIBLE);
                    stationRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    StationNameBox.setVisibility(View.GONE);
                    RecentLayout.setVisibility(View.GONE);
                    stationpickerHeader.setText("Search Results");
                    NearByLayout.setVisibility(View.VISIBLE);
                    stationRecyclerView.setVisibility(View.VISIBLE);
                }

                if (workRunnable != null) {
                    handler.removeCallbacks(workRunnable);
                }

                workRunnable = () -> {
                    if (formattedText.length() > 2) {
                        fetchStations(formattedText);
                    }
                };
                handler.postDelayed(workRunnable, 1000);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });




    }

    private List<Station> loadStationsFromAssets() {
        List<Station> stationList = new ArrayList<>();
        try {
            InputStream is = getAssets().open("indian-railway-stations.json"); // assets/ folder me file
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String code = obj.getString("value");   // JSON me "value" = code
                String name = obj.getString("label");  // JSON me "label" = name
                stationList.add(new Station(name, code));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stationList;
    }

  /*  private OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .protocols(java.util.Arrays.asList(okhttp3.Protocol.HTTP_2, okhttp3.Protocol.HTTP_1_1)) // Enable HTTP/2
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS) // Increase timeout
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .retryOnConnectionFailure(true) // Retry failed requests
                .build();
    }

   */


    private void loadRecentSearches() {
        SharedPreferences prefs = getSharedPreferences("GoRailPrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("recent_stations", "[]");
        Type type = new TypeToken<List<Station>>() {}.getType();
        List<Station> recentStations = gson.fromJson(json, type);

        // 🔍 Debugging: Log the retrieved list
        Log.d("StationPicker", "Loaded recent searches: " + json);

        if (recentStations == null) {
            recentStations = new ArrayList<>();
        }

        searchResultsList.clear();
        searchResultsList.addAll(recentStations);

        // 🔍 Debugging: Check if objects have proper values
        for (Station station : searchResultsList) {
            Log.d("StationPicker", "Station: " + station.getStationName() + " (" + station.getStationCode() + ")");
        }

        recentSearchAdapter.notifyDataSetChanged();
    }




    private void saveRecentSearch(String stationName, String stationCode) {
        if (stationName == null || stationCode == null) {
            Log.e("SaveRecentSearch", "Error: Station name or code is null");
            return; // 🚨 Prevent crash by exiting early
        }

        SharedPreferences prefs = getSharedPreferences("GoRailPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = prefs.getString("recent_stations", "[]");
        Type type = new TypeToken<List<Station>>() {}.getType();
        List<Station> recentStations = gson.fromJson(json, type);

        // 🚀 Prevent duplicates while avoiding NullPointerException
        for (Station s : recentStations) {
            if (stationCode.equals(s.getStationCode())) { // 🔥 Null check safe now
                return; // Already exists, so no need to add
            }
        }

        // 📝 Add new station
        recentStations.add(0, new Station(stationName,stationCode));

        // 🗑️ Limit recent searches to 5
        if (recentStations.size() > 10) {
            recentStations.remove(recentStations.size() - 1);
        }

        // 🛠️ Save updated list
        editor.putString("recent_stations", gson.toJson(recentStations));
        editor.apply();
    }


    private void fetchStations(String query) {
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            List<Station> allStations = loadStationsFromAssets();
            List<Station> filteredStations = new ArrayList<>();

            for (Station s : allStations) {
                if (s.getStationName().toUpperCase().contains(query) ||
                        s.getStationCode().toUpperCase().contains(query)) {
                    filteredStations.add(s);
                }
            }

            runOnUiThread(() -> {
                stationList.clear();
                stationList.addAll(filteredStations);
                stationAdapter.updateList(stationList);
                progressBar.setVisibility(View.GONE);
            });
        }).start();
    }



  /*  private void fetchStations(String query) {
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            OkHttpClient client = getOkHttpClient();
            String url = "https://indianrailapi.com/api/v2/AutoCompleteStation/apikey/" + API_KEY + "/StationCodeOrName/" + query + "/";
            Request request = new Request.Builder().url(url).build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    Log.d("StationPicker", "Response: " + jsonResponse);

                    Type responseType = new TypeToken<StationResponse>() {}.getType();
                    StationResponse stationResponse = new Gson().fromJson(jsonResponse, responseType);

                    runOnUiThread(() -> {
                        if (stationResponse != null && stationResponse.getStations() != null) {
                            stationList.clear();
                            stationList.addAll(stationResponse.getStations());

                            if (!stationList.isEmpty()) {
                                stationRecyclerView.setVisibility(View.VISIBLE);
                            } else {
                                stationRecyclerView.setVisibility(View.VISIBLE);
                            }

                            stationAdapter.updateList(stationList);  // 🚀 Update adapter with API data
                        }

                        progressBar.setVisibility(View.GONE);
                    });

                }
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> progressBar.setVisibility(View.GONE));
            }
        }).start();
    }

   */
}
