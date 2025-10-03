package com.example.gorail;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gorail.adapters.ShimmerTrainAdapter;
import com.example.gorail.model.DateUtils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gorail.APIs.RetrofitClient;
import com.example.gorail.APIs.TrainApiService;
import com.example.gorail.adapters.DateAdapter;
import com.example.gorail.adapters.TrainAdapter;
import com.example.gorail.model.TrainData;
import com.example.gorail.model.TrainRequest;
import com.example.gorail.model.TrainResponse;
import com.example.gorail.model.TrainWrapper;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindTrainsActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    private Button RetryBtn;
    private ImageView backBtn;
    private RecyclerView recyclerView, recyclerViewDate;
    private TrainAdapter trainAdapter;

    private ShimmerFrameLayout shimmerLayout;
    private List<TrainData> trainList = new ArrayList<>();
    private Map<String, String> seatStatusMap = new HashMap<>();


    DateAdapter dateAdapter;


    private List<String> getAvailableClasses() {
        return Arrays.asList("1A", "2A", "3A", "SL", "3E", "2S", "CC", "EC"); // ✅ Yeh classes dynamically fetch bhi ho sakti hain
    }
    private String convertDateForApi(String inputDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            Date date = inputFormat.parse(inputDate); // Convert string to Date
            return outputFormat.format(date);         // Convert back to required format
        } catch (ParseException e) {
            e.printStackTrace();
            return ""; // Or handle error appropriately
        }
    }

    private String convertDateToLocalFormat(String apiDate) {
        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        SimpleDateFormat localFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        try {
            Date date = apiFormat.parse(apiDate);
            return localFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return apiDate; // fallback
        }
    }



    private String formatDateForAPI(String inputDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH); // ⚠ Current Date Format
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH); // ✅ Required API Format

            Date date = inputFormat.parse(inputDate);
            return outputFormat.format(date);  // ✅ Return formatted date
        } catch (ParseException e) {
            e.printStackTrace();
            return inputDate; // ⚠ If parsing fails, return original input
        }
    }
    private String getQuotaCode(String quota) {
        switch (quota) {
            case "General":
                return "GN";
            case "Tatkal":
                return "TQ";
            case "Premium Tatkal":
                return "PT";
            case "Ladies":
                return "LD";
            case "Lower Berth":
                return "LB";
            default:
                return "GN"; // Default to General Quota
        }
    }



    private static final String TAG = "FindTrainsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_trains);
        recyclerViewDate = findViewById(R.id.rvDates);
        recyclerView = findViewById(R.id.recyclerViewTrains);
        progressBar = findViewById(R.id.progressbarSearch);
        backBtn = findViewById(R.id.back_btn4);
        RetryBtn = findViewById(R.id.RetryButton);




        // Intent Data
        String childPassengers = getIntent().getStringExtra("childNumber");
        String adultPassengers = getIntent().getStringExtra("adultNumber");
        String fromStation = getIntent().getStringExtra("fromStation");
        String toStation = getIntent().getStringExtra("toStation");
        String date = getIntent().getStringExtra("date");
        String quota = getIntent().getStringExtra("quota");

        Log.d(TAG, "Intent Data -> From: " + fromStation + ", To: " + toStation + ", Date: " + date + ", Quota: " + quota + ", Child: " + childPassengers + ", Adult: " + adultPassengers);

        List<DateUtils> dateList = DateUtils.generateUpcomingDates(date);
        Log.d("FindTrainsActivity", "📅 Final Date List Sent to Adapter: " + dateList.toString());

        for (DateUtils dateItem : dateList) {
            Log.d("FindTrainsActivity", "   📆 " + dateItem.getFullDate() + " (" + dateItem.getDay() + ")");
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        trainAdapter = new TrainAdapter(trainList,this,adultPassengers,childPassengers,date);
        recyclerView.setAdapter(trainAdapter);


        recyclerViewDate.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        dateAdapter = new DateAdapter(dateList, date, this, new DateAdapter.OnDateClickListener() {
            @Override
            public void onDateSelected(String date) {
                fetchTrains(fromStation, toStation, date, quota);
            }
        });
        recyclerViewDate.setAdapter(dateAdapter);

        if (fromStation == null || toStation == null || date == null) {
            Toast.makeText(this, "Invalid input data. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        backBtn.setOnClickListener(v -> startActivity(new Intent(FindTrainsActivity.this, MainActivity.class)));

        RetryBtn.setOnClickListener(v->{
            fetchTrains(fromStation,toStation,date,quota);
        });

        fetchTrains(fromStation, toStation, date, quota);
        fetchDateAvailability(fromStation, toStation, date);
    }

    private void fetchTrains(String fromStation, String toStation, String date, String quota) {
        progressBar.setVisibility(View.VISIBLE);

        TrainApiService apiService = RetrofitClient.getClient().create(TrainApiService.class);
        Call<TrainResponse> call = apiService.getTrainData(fromStation, toStation, date);

        call.enqueue(new Callback<TrainResponse>() {
            @Override
            public void onResponse(Call<TrainResponse> call, Response<TrainResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<TrainWrapper> trainWrappers = response.body().data;
                    if (trainWrappers == null || trainWrappers.isEmpty()) {
                        Toast.makeText(FindTrainsActivity.this, "No trains available for this route.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    trainList.clear();
                    for (TrainWrapper wrapper : trainWrappers) {
                        TrainData train = wrapper.train_base;
                        trainList.add(train);
                        fetchSeatAvailability(train, quota, date);
                    }
                    trainAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(FindTrainsActivity.this, "Failed to load trains.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TrainResponse> call, Throwable t) {
                shimmerLayout.stopShimmer();
                shimmerLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                RetryBtn.setVisibility(View.VISIBLE);
                Log.e(TAG, "Network Error: " + t.getMessage());
                Toast.makeText(FindTrainsActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 1️⃣ API Call and Parse Data
    private void fetchDateAvailability(String from, String to, String date) {
        String formattedDate = convertDateForApi(date); // Example: "04042025"

        // Construct API URL
        String url = "https://train-seat-scraper12.onrender.com/get_availability"
                + "?origin=" + from
                + "&destination=" + to
                + "&date=" + formattedDate;

        Log.d("FETCH_SEAT_API", "API Request URL: " + url); // 🔍 Log API URL

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d("FETCH_SEAT_API", "API Response received");
                        Log.d("FETCH_SEAT_API", "Full JSON Response: " + response.toString());

                        // ✅ Get the "data" object first
                        JSONObject dataObject = response.getJSONObject("data");
                        JSONArray availabilityArray = dataObject.getJSONArray("availability");
                        Log.d("FETCH_SEAT_API", "Availability count: " + availabilityArray.length());

                        for (int i = 0; i < availabilityArray.length(); i++) {
                            JSONObject obj = availabilityArray.getJSONObject(i);
                            String responseDate = obj.getString("date");  // yyyy/MM/dd
                            String rawState = obj.getString("state");
                            String state = rawState.replaceAll("([a-z])([A-Z])", "$1 $2");

                            // Convert to dd-MM-yyyy to match your date bar
                            String formattedResponseDate = convertDateToLocalFormat(responseDate);

                            Log.d("FETCH_SEAT_API", "Date: " + formattedResponseDate + ", State: " + state);

                            seatStatusMap.put(formattedResponseDate, state);
                        }

                        // Update Adapter
                        dateAdapter.setSeatStatusMap(seatStatusMap);
                        dateAdapter.notifyDataSetChanged();
                        Log.d("FETCH_SEAT_API", "Adapter notified with seatStatusMap");

                    } catch (JSONException e) {
                        Log.e("FETCH_SEAT_API", "JSON Parsing Error: " + e.getMessage());
                        e.printStackTrace();
                    }
                },
                error -> Log.e("FETCH_SEAT_API", "Volley Error: " + error.toString())
        );

        queue.add(request);
    }






    private void fetchSeatAvailability(TrainData train, String quota, String date) {
        TrainApiService apiService = RetrofitClient.getSeatClient().create(TrainApiService.class);
        List<String> availableClasses = getAvailableClasses(); // ✅ Saari classes yahan milengi

        for (String cls : availableClasses) { // ✅ Har class ke liye loop chalayenge
            String formattedQuota = getQuotaCode(quota);
            String formattedDate = formatDateForAPI(date);

            // ✅ lstSearch Map
            Map<String, String> lstSearch = new HashMap<>();
            lstSearch.put("SelectQuta", formattedQuota);
            lstSearch.put("arrivalTime", train.to_time);
            lstSearch.put("departureTime", train.from_time);
            lstSearch.put("distance", train.getDistance_from_to());
            lstSearch.put("trainName", train.getTrain_name());
            lstSearch.put("trainNumber", train.getTrain_no());

            // ✅ API Request Object
            TrainRequest request = new TrainRequest(
                    cls,  // ✅ Yeh dynamically change hoga (SL, 3E, 3A, 2A, 1A,2S,CC,EC)
                    train.getTrain_no(),  // ✅ Train Number
                    formattedQuota,  // ✅ Quota (GN)
                    train.getFrom_stn_code(),  // ✅ From Station Code
                    train.getTo_stn_code(),  // ✅ To Station Code
                    formattedDate + "|" + train.getFrom_stn_code() + "|" + train.getTo_stn_code() + "|" + "3A",  // ✅ 'e' parameter
                    lstSearch,
                    "1",
                    train.getFrom_stn_name()
            );

            // ✅ Debugging ke liye JSON Print karo
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Log.d(TAG, "📤 Sending Seat API Request: " + gson.toJson(request));

            // ✅ Retrofit Call
            Call<TrainResponse> seatCall = apiService.getTrainAvailability(request);
            seatCall.enqueue(new Callback<TrainResponse>() {
                @Override
                public void onResponse(Call<TrainResponse> call, Response<TrainResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        TrainResponse seatResponse = response.body();

                        if (seatResponse.getErrorMsg() != null &&
                                "IRCTC Down".equalsIgnoreCase(seatResponse.getErrorMsg().getErrorMessage())) {

                            Log.w(TAG, "⚠️ IRCTC Services Down (as per API response)");

                            // ⬇️ Update TrainData to show IRCTC Down in adapter
                            train.apiErrorMessage = "IRCTC Down";
                            train.isSeatLoading = false;
                            train.setClassAvailability(null);  // Clear normal availability
                            trainAdapter.notifyDataSetChanged();
                            return; // Exit further processing
                        }

                        // ✅ Debugging: Print API Response
                        Log.d(TAG, "📜 Full API Response (JSON): " + gson.toJson(seatResponse));

                        List<TrainData.TrainClassAvailability> classAvailability = new ArrayList<>();

                        if (seatResponse.getAvlDayList() == null || seatResponse.getAvlDayList().isEmpty()) {
                            Log.w(TAG, "⚠️ avlDayList is NULL or EMPTY, ignoring...");
                            return;
                        }

                        // ✅ Convert Selected Date to API Format ("d-M-yyyy")
                        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()); // Selected date format
                        SimpleDateFormat apiFormat = new SimpleDateFormat("d-M-yyyy", Locale.getDefault()); // API format (without leading zeros)

                        String selectedDate = date; // Ensure this is in "dd-MM-yyyy"
                        String formattedSelectedDate = "";

                        try {
                            formattedSelectedDate = apiFormat.format(inputFormat.parse(selectedDate)); // ✅ Convert to "d-M-yyyy"
                        } catch (Exception e) {
                            Log.e(TAG, "❌ Date Parsing Error: " + e.getMessage());
                        }

                        // ✅ Find Exact or Nearest Availability
                        String availabilityStatus = "No Data Available"; // Default if no exact match
                        boolean found = false;

                        for (TrainResponse.AvlDay avl : seatResponse.getAvlDayList()) {
                            Log.d(TAG, "🔍 Checking API Date: " + avl.getAvailablityDate() + " vs Selected: " + formattedSelectedDate);

                            if (avl.getAvailablityDate().equals(formattedSelectedDate)) {
                                availabilityStatus = avl.getAvailablityStatus();
                                found = true;
                                break;
                            }
                        }

                        // ✅ If no exact match, pick nearest available
                        if (!found && !seatResponse.getAvlDayList().isEmpty()) {
                            availabilityStatus = seatResponse.getAvlDayList().get(0).getAvailablityStatus();
                            Log.w(TAG, "⚠️ No exact match, using closest available: " + availabilityStatus);
                        }

                        // ✅ Logging for Debugging
                        Log.d(TAG, "📅 Selected Date: " + selectedDate + " -> Converted: " + formattedSelectedDate + " -> Availability: " + availabilityStatus);
                        Log.d(TAG, "🚆 Final Available Classes & Status: " + getAvailableClasses());

                        // ✅ Add Correct Availability Data
                        classAvailability.add(new TrainData.TrainClassAvailability(
                                seatResponse.getEnqClassType().getCode(),
                                availabilityStatus,
                                seatResponse.getTotalFare()
                        ));

                        // ✅ Update Train Data with Availability
                        train.setClassAvailability(classAvailability);
                        train.isSeatLoading = false;
                        trainAdapter.notifyDataSetChanged();
                        Log.d(TAG, "✅ Seat Availability Updated for Class: " + cls);
                    } else {
                        RetryBtn.setVisibility(View.VISIBLE);
                        Log.e(TAG, "❌ Seat API Response Failed: " + response.code() + " for Class: " + cls);
                        try {
                            Log.e(TAG, "❌ Response Body: " + response.errorBody().string());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }



                @Override
                public void onFailure(Call<TrainResponse> call, Throwable t) {
                    Log.e(TAG, "❌ Seat API Error for " + cls + ": " + t.getMessage());
                }
            });
        }
    }




}
