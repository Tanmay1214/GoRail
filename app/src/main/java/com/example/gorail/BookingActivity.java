package com.example.gorail;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gorail.APIs.RetrofitClient;
import com.example.gorail.APIs.TrainApiService;
import com.example.gorail.adapters.AddPassengerBottomSheet;
import com.example.gorail.adapters.PassengerAdapter;
import com.example.gorail.model.Passenger;
import com.example.gorail.model.TrainRouteModel;
import com.example.gorail.model.TrainRouteResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingActivity extends AppCompatActivity {
    private List<Passenger> passengerList = new ArrayList<>();
    Spinner boardingSpinner;

    ProgressBar passengerLoadingSpinner;

    PassengerAdapter adapter;



    public String convertDateToReadableFormat(String inputDate) {
        try {
            // Step 1: Parse the input date
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date date = inputFormat.parse(inputDate);

            // Step 2: Format the output
            SimpleDateFormat outputFormat = new SimpleDateFormat("d MMMM" , Locale.getDefault());
            return outputFormat.format(date);

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String formatTime(String timeString) {
        try {
            // Step 1: Split hours and minutes
            String[] parts = timeString.split("\\.");

            int hours = Integer.parseInt(parts[0]);
            int minutes = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;

            // Step 2: Format to hh:mm
            return String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);

        } catch (Exception e) {
            e.printStackTrace();
            return timeString;
        }
    }

    public String formatDurationFromFloat(String input) {
        try {
            String[] parts = input.split("\\.");
            int hours = Integer.parseInt(parts[0]);
            int minutes = (parts.length > 1) ? Integer.parseInt(parts[1]) : 0;

            // Extra fix: if someone writes 8.5 (assuming 50 not 5)
            if (parts.length > 1 && parts[1].length() == 1) {
                minutes = Integer.parseInt(parts[1] + "0"); // 5 becomes 50
            }

            return hours + "h " + minutes + "m";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    public String getArrivalDate(String departureDate, String departureTime, String travelDuration) {
        try {
            // Step 1: Combine departure date and time.
            // Assume departureDate format: "dd-MM-yyyy" (e.g., "04-04-2025")
            // and departureTime format: "HH.mm" (e.g., "12.05")
            SimpleDateFormat combinedFormat = new SimpleDateFormat("dd-MM-yyyy HH.mm", Locale.getDefault());
            Date departureDateTime = combinedFormat.parse(departureDate + " " + departureTime);

            // Step 2: Parse travelDuration.
            // For example, "8.35" means 8 hours and 35 minutes.
            String[] parts = travelDuration.split("\\.");
            int hours = Integer.parseInt(parts[0]);
            int minutes = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;

            // Step 3: Add travel duration to the departure dateTime.
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(departureDateTime);
            calendar.add(Calendar.HOUR_OF_DAY, hours);
            calendar.add(Calendar.MINUTE, minutes);

            // Step 4: Format the resulting arrival date.
            // This will output e.g., "4 April, Friday" (automatically shows next day if applicable)
            SimpleDateFormat outputFormat = new SimpleDateFormat("d MMMM", Locale.getDefault());
            return outputFormat.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }



Button addPassengerButton;

    RecyclerView recyclerView;
    TextView noWarning,fareTextView,fromToTextView,trainName,trainNumber,status,lastUpdated,departureTime,departureDate,journeyDuration,arrivalTime,arrivalDate,departureStation,arrivalStation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fromToTextView = findViewById(R.id.fromToTextview);
        trainName = findViewById(R.id.trainName);
        trainNumber = findViewById(R.id.trainNumber);
        status = findViewById(R.id.status);
        lastUpdated = findViewById(R.id.lastUpdated);
        departureTime = findViewById(R.id.departureTime);
        departureDate = findViewById(R.id.departureDate);
        journeyDuration = findViewById(R.id.journeyDuration);
        arrivalTime = findViewById(R.id.arrivalTime);
        passengerLoadingSpinner = findViewById(R.id.passengerLoadingSpinner);
        arrivalDate = findViewById(R.id.arrivalDate);
        departureStation = findViewById(R.id.departureStation);
        arrivalStation = findViewById(R.id.arrivalStation);
        boardingSpinner = findViewById(R.id.boardingSpinner);
        addPassengerButton = findViewById(R.id.btnAddPassenger);
        recyclerView = findViewById(R.id.recyclerViewPassengers);
        noWarning= findViewById(R.id.noPassengersWarning);
      //  fareTextView = findViewById(R.id.fareTextView);
        passengerList = new ArrayList<>();
        adapter = new PassengerAdapter(this,passengerList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        // Get intent data
        Intent intent = getIntent();
        String classType = intent.getStringExtra("classType");
        String price = intent.getStringExtra("price");
        String date = intent.getStringExtra("date");
        String status = intent.getStringExtra("status");
        String fromStation = intent.getStringExtra("fromStation");
        String departureTime = intent.getStringExtra("departureTime");
        String arrivalTime = intent.getStringExtra("arrivalTime");
        String travelTime = intent.getStringExtra("travelTime");
        String fromStationName = intent.getStringExtra("fromStationName");
        String toStationName = intent.getStringExtra("toStationName");
        String toStation = intent.getStringExtra("toStation");
        String adultPassengers = intent.getStringExtra("adultNumber");
        String childPassengers = intent.getStringExtra("childNumber");
        String trainNumber = intent.getStringExtra("trainNumber");
        String trainName = intent.getStringExtra("trainName");

        Log.d("BookingData", "Price: " + price + ", Status:" + status + ", Class Type: " + classType
                + ", From: " + fromStation + ", To: " + toStation + ", Adult:" + adultPassengers + ", Child:" + childPassengers + "Date:"+ date);

        String fareUrl = "https://erail.in/train-fare/" + trainNumber +
                "?from=" + fromStation +
                "&to=" + toStation +
                "&adult=" + adultPassengers +
                "&child=" + childPassengers;

        Log.d("BookingData", "Fetching URL: " + fareUrl);

        this.trainName.setText(trainName);
        this.trainNumber.setText(trainNumber + " | " + classType + " | " + "GN");
        this.status.setText(status);
        this.lastUpdated.setText("Updated few mins ago");
        String formattedDuration = formatDurationFromFloat(travelTime);
        this.journeyDuration.setText("────  " + formattedDuration + "  ────");
        this.departureStation.setText(fromStationName + " ( " + fromStation + " )");
        this.arrivalStation.setText(toStationName + " ( " + toStation + " )");
        String timeStringDeparture = formatTime(departureTime);
        this.departureTime.setText(timeStringDeparture + ",");
        String timeStringArrival = formatTime(arrivalTime);
        this.arrivalTime.setText(","+timeStringArrival);
        String formattedDeparture = convertDateToReadableFormat(date);
        String formattedArrival = getArrivalDate(date, departureTime,travelTime);
        this.arrivalDate.setText(formattedArrival);
        this.departureDate.setText(formattedDeparture);
        fromToTextView.setText(fromStation + " To " + toStation + " | " + formattedDeparture);
        addPassengerButton.setOnClickListener(v -> {
            AddPassengerBottomSheet bottomSheet = new AddPassengerBottomSheet();
            bottomSheet.setOnPassengerAddedListener((passengerId,name, age, gender, berth) -> {
                // Add the passenger to your RecyclerView or list
                passengerList.add(new Passenger(passengerId,name, age, gender, berth));
                adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
                noWarning.setVisibility(View.GONE);
            });
            bottomSheet.show(getSupportFragmentManager(), "AddPassengerBottomSheet");
        });

        fetchRouteForSpinner(trainNumber,fromStation, toStation);

        passengerLoadingSpinner.setVisibility(View.VISIBLE);
        noWarning.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference passengerRef;

        if (currentUser != null) {
            passengerRef = FirebaseDatabase.getInstance().getReference("Users")
                    .child(currentUser.getUid())
                    .child("PassengersList");

            passengerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    passengerList.clear();

                    for (DataSnapshot child : snapshot.getChildren()) {
                        Passenger passenger = child.getValue(Passenger.class);
                        if (passenger != null) {
                            passengerList.add(passenger);
                        }
                    }

                    if (passengerList.isEmpty()) {
                        noWarning.setVisibility(View.VISIBLE);
                        passengerLoadingSpinner.setVisibility(View.GONE);
                    } else {
                        noWarning.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        passengerLoadingSpinner.setVisibility(View.GONE);
                    }

                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(BookingActivity.this, "Failed to load passengers", Toast.LENGTH_SHORT).show();
                    passengerLoadingSpinner.setVisibility(View.GONE);
                    noWarning.setVisibility(View.VISIBLE); // fallback
                }
            });

        }
        adapter.setOnPassengerEditListener(passenger -> {
            AddPassengerBottomSheet bottomSheet = new AddPassengerBottomSheet();

            // 👇 Set mode to EDIT and pass passenger data
            Bundle bundle = new Bundle();
            bundle.putString("mode", "edit");
            bundle.putString("id", passenger.getId());
            bundle.putString("name", passenger.getName());
            bundle.putInt("age", passenger.getAge());
            bundle.putString("gender", passenger.getGender());
            bundle.putString("berth", passenger.getBerth());

            bottomSheet.setArguments(bundle);

            bottomSheet.setOnPassengerAddedListener((id, name, age, gender, berth) -> {
                // ✅ Firebase update logic
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users")
                        .child(uid)
                        .child("PassengersList")
                        .child(id);

                Passenger updatedPassenger = new Passenger(id, name, age, gender, berth);
                ref.setValue(updatedPassenger).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // ✅ Update local list
                        for (int i = 0; i < passengerList.size(); i++) {
                            if (passengerList.get(i).getId().equals(id)) {
                                passengerList.set(i, updatedPassenger);
                                adapter.notifyItemChanged(i);
                                break;
                            }
                        }
                        Toast.makeText(this, "Passenger updated", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            bottomSheet.show(getSupportFragmentManager(), "EditPassengerBottomSheet");
        });





        new Thread(() -> {
            try {
                Document doc = Jsoup.connect(fareUrl)
                        .userAgent("Mozilla/5.0")
                        .timeout(10_000)
                        .get();

                Element fareTable = doc.selectFirst("table.tableSingleFare");
                if (fareTable != null) {
                    Elements rows = fareTable.select("tr");

                    for (int i = 1; i < rows.size(); i++) { // Skip header
                        Elements cols = rows.get(i).select("td");
                        String category = cols.get(0).text(); // e.g., General or Tatkal

                        for (int j = 1; j < cols.size(); j++) {
                            String fareValue = cols.get(j).text();
                            String header = rows.get(0).select("th").get(j).text(); // e.g., 1A, 2A, etc.

                            Log.d("FareInfo", category + " - " + header + ": " + fareValue);

                            if (header.equalsIgnoreCase(classType) && category.equalsIgnoreCase("General")) {
                                String finalFare = fareValue;
                                runOnUiThread(() -> {
                                    Toast.makeText(this, classType + " Fare: ₹" + finalFare, Toast.LENGTH_LONG).show();
                                    if (fareTextView != null) {
                                        fareTextView.setText(classType + " Fare: ₹" + finalFare);
                                    }
                                });
                                return;
                            }
                        }
                    }
                } else {
                    Log.e("FareInfo", "Fare table not found!");
                    runOnUiThread(() ->
                            Toast.makeText(this, "Fare table not found on page", Toast.LENGTH_SHORT).show()
                    );
                }

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Failed to fetch fare", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private void fetchRouteForSpinner(String trainNumber, String fromStation, String toStation) {
        TrainApiService apiService = RetrofitClient.getRouteClient().create(TrainApiService.class);
        Call<TrainRouteResponse> call = apiService.getTrainRoute(trainNumber, fromStation, toStation);

        call.enqueue(new Callback<TrainRouteResponse>() {
            @Override
            public void onResponse(Call<TrainRouteResponse> call, Response<TrainRouteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TrainRouteModel> routeList = response.body().getFilteredRoute(); // 👈 filteredRoute use kar

                    List<String> stationDisplayList = new ArrayList<>();
                    for (TrainRouteModel item : routeList) {
                        String stationName = item.getStation_name();
                        String arrival = item.getArrival();
                        int day = item.getDay();

                        String displayText = stationName + " - Arrival: " + arrival + " (Day: " + day + ")";
                        stationDisplayList.add(displayText);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            BookingActivity.this,
                            android.R.layout.simple_spinner_item,
                            stationDisplayList
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    boardingSpinner.setAdapter(adapter);

                } else {
                    Toast.makeText(BookingActivity.this, "Route fetch failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TrainRouteResponse> call, Throwable t) {
                Toast.makeText(BookingActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




}
