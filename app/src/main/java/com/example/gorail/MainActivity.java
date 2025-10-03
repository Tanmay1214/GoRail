package com.example.gorail;

import static android.view.View.VISIBLE;

import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.gorail.databinding.ActivityMainBinding;
import com.example.gorail.validators.ValidatorTrain;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private boolean isExpanded = false; // Declare as a global variable


    private ActivityMainBinding binding;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    private boolean isNextDateSelected = false, isDayAfterSelected = false;

    private int adultPassengers=1,childPassengers=0;

    private static final int REQUEST_CODE_FROM = 1;
    private static final int REQUEST_CODE_TO = 2;

    private static final int FROM_STATION_REQUEST = 1;
    private static final int TO_STATION_REQUEST = 2;


    private SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    private Calendar calendar=Calendar.getInstance();
    private Calendar originalCalendar;

    private static final int REQUEST_CODE_PICK_STATION = 100; // Unique request code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ✅ Load Last Search Details (ONLY ONCE)
        SharedPreferences sharedPreferences = getSharedPreferences("LastSearchPrefs", MODE_PRIVATE);

        String fromStation = sharedPreferences.getString("fromStationCode", null);
        String toStation = sharedPreferences.getString("toStationCode", null);
        String journeyDate = sharedPreferences.getString("journeyDate", null);
        String fromStationName = sharedPreferences.getString("fromStationName",null);
        String toStationName = sharedPreferences.getString("toStationName",null);
        LinearLayout recentSearchLayout = findViewById(R.id.recentSearchLayout);
        LinearLayout fillingfastLayout = findViewById(R.id.fillingfastLayout);
        TextView fromTextviewRecent = findViewById(R.id.fromTextviewRecent);
        TextView toTextviewRecent = findViewById(R.id.toTextviewRecent);
        TextView textViewDate = findViewById(R.id.textViewDate);
        TextView toTextView = findViewById(R.id.toTextview);
        TextView fromTextView = findViewById(R.id.fromTextview);
        TextView departuredateText = findViewById(R.id.departuredateText);

        fromTextviewRecent.setText(fromStation);
        toTextviewRecent.setText(toStation);
        textViewDate.setText(journeyDate);

        if (fromStation != null && toStation != null && journeyDate != null) {
            // Display recent search
            fromTextviewRecent.setText(fromStation);
            toTextviewRecent.setText(toStation);
            toTextView.setText(toStationName);
            fromTextView.setText(fromStationName);
            departuredateText.setText(journeyDate);
            textViewDate.setText(journeyDate);
            recentSearchLayout.setVisibility(View.VISIBLE);// Show layout
            fillingfastLayout.setVisibility(View.VISIBLE);// Show layout

        } else {
            recentSearchLayout.setVisibility(View.GONE); // Hide layout if no last search
            fillingfastLayout.setVisibility(View.GONE); // Hide layout if no last search
        }

        recentSearchLayout.requestLayout();

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        originalCalendar = (Calendar) calendar.clone();
        updateDate();

        googleSignInClient = GoogleSignIn.getClient(this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build());

        binding.tomorrowBtn.setOnClickListener(v -> toggleSelection(binding.tomorrowBtn, binding.tomorrowText, binding.tatkalopenText1,1));
        binding.dayafterBtn.setOnClickListener(v -> toggleSelection(binding.dayafterBtn, binding.dayafterText, binding.tatkalopenText,2));
        binding.expandableSection.setVisibility(View.GONE);
        binding.btnExpandCollapse.setImageResource(R.drawable.ic_expand_collapse);
        binding.btnExpandCollapse.setOnClickListener(v -> toggleSection());

        binding.swapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence fromText = fromTextView.getText();
                CharSequence toText = toTextView.getText();

                fromTextView.setText(toText);
                toTextView.setText(fromText);
            }
        });

// Open station picker on spinner click
        binding.toStationLayout.setOnClickListener(v -> openStationPicker(REQUEST_CODE_TO));
        binding.fromStatioonLayout.setOnClickListener(v -> openStationPicker(REQUEST_CODE_FROM));

// ✅ Fixing SharedPreferences & journeyDate duplicate issue
        // ✅ Declare a final copy of journeyDate before the lambda
        final String finalJourneyDate = binding.departuredateText.getText().toString();

        binding.findTrainsButton.setOnClickListener(v -> {
            if (ValidatorTrain.validateInputs(this, binding.fromTextview, binding.toTextview, binding.ClassSp, binding.quotaSpinner)) {
                String sourceText = binding.fromTextview.getText().toString().trim();
                String destinationText = binding.toTextview.getText().toString().trim();

                // Validate station format (Station Name (CODE))
                if (!sourceText.contains("(") || !sourceText.contains(")")) {
                    binding.fromTextview.setError("Invalid source station");
                    binding.fromTextview.requestFocus();
                    return;
                }
                if (!destinationText.contains("(") || !destinationText.contains(")")) {
                    binding.toTextview.setError("Invalid destination station");
                    binding.toTextview.requestFocus();
                    return;
                }

                // Extract station codes
                String sourceStationCode = sourceText.substring(sourceText.indexOf("(") + 1, sourceText.indexOf(")"));
                String destinationStationCode = destinationText.substring(destinationText.indexOf("(") + 1, destinationText.indexOf(")"));
                String selectedDate = binding.departuredateText.getText().toString();
                String selectedClass = binding.ClassSp.getSelectedItem().toString();
                String quota = binding.quotaSpinner.getSelectedItem().toString();
                String adultPassengers = String.valueOf(this.adultPassengers);
                String childPassengers = String.valueOf(this.childPassengers);

                // ✅ Save last search details in SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("fromStationName", sourceText);
                editor.putString("fromStationCode", sourceStationCode);
                editor.putString("toStationName", destinationText);
                editor.putString("toStationCode", destinationStationCode);
                editor.apply();

                // ✅ Logging all values before starting FindTrainsActivity
                Log.d("MainActivity", "🚀 Navigating to FindTrainsActivity with:");
                Log.d("MainActivity", "📍 From Station: " + sourceStationCode);
                Log.d("MainActivity", "🎯 To Station: " + destinationStationCode);
                Log.d("MainActivity", "📅 Selected Date: " + selectedDate);
                Log.d("MainActivity", "💺 Selected Class: " + selectedClass);
                Log.d("MainActivity", "🎫 Quota: " + quota);
                Log.d("MainActivity", "🧑 Adult Passengers: " + adultPassengers);
                Log.d("MainActivity", "👶 Child Passengers: " + childPassengers);

                // 🔹 Open FindTrainsActivity
                Intent intent = new Intent(MainActivity.this, FindTrainsActivity.class);
                intent.putExtra("fromStation", sourceStationCode);
                intent.putExtra("toStation", destinationStationCode);
                intent.putExtra("date", selectedDate); // ✅ Using final variable
                intent.putExtra("selectedClass", selectedClass);
                intent.putExtra("quota", quota);
                intent.putExtra("adultNumber", adultPassengers);
                intent.putExtra("childNumber", childPassengers);
                startActivity(intent);

            }
        });




        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

            // User Ka Naam Set Karna
            String name = currentUser.getDisplayName(); // Google Sign-In Se Name Fetch
            if (name != null && !name.isEmpty()) {
                binding.userNameTextView.setText(name);
            } else {
                fetchUserNameFromDatabase();
            }

            // Profile Photo Fetch Karna
            if (currentUser.getPhotoUrl() != null) {
                // Google Sign-In User Ke Liye Profile Photo Set
                Picasso.get().load(currentUser.getPhotoUrl()).transform(new CircleTransform()).into(binding.profileImageView);
            } else {
                // Phone Authentication Ke Liye Default Avatar Set
                binding.profileImageView.setImageResource(R.drawable.profileimg);
            }
        } else {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
        }




        initPassengers();
        initClassSeat();
        initQuotaSeat();
        initDatePicker();



    }


    private void updateDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        binding.departuredateText.setText(dateFormat.format(calendar.getTime()));
    }
    private void fetchUserNameFromDatabase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

        userRef.child("name").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                String name = task.getResult().getValue(String.class);
                binding.userNameTextView.setText(name);
            } else {
                binding.userNameTextView.setText("User"); // Default Name
            }
        });
    }


    private void initDatePicker() {
        Calendar calendarToday = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()); // Ensure correct format
        String currentDate = dateFormat.format(calendarToday.getTime());

        binding.departuredateText.setText(currentDate);
        binding.clearDateIcon.setVisibility(View.GONE); // Show cross icon initially

        // Date selection logic
        binding.departuredateText.setOnClickListener(v -> showDatePickerDialog());

        // Clear date when clicking on cross icon
        binding.clearDateIcon.setOnClickListener(v -> {
            binding.departuredateText.setText("Select Date"); // Reset text
            binding.clearDateIcon.setVisibility(View.GONE);  // Hide cross icon
        });
    }


    private void initClassSeat() {
        binding.progressbarClass.setVisibility(View.VISIBLE);
        ArrayList<String> list=new ArrayList<>();
        list.add("SL");
        list.add("3A");
        list.add("2A");
        list.add("1A");
        list.add("CC");
        list.add("EC");

        ArrayAdapter<String> adapter=new ArrayAdapter<>(MainActivity.this,R.layout.sp_item,list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.ClassSp.setAdapter(adapter);
        binding.progressbarClass.setVisibility(View.GONE);
    }

    private void initQuotaSeat(){
        binding.progressbarQuota.setVisibility(VISIBLE);
        ArrayList<String> list=new ArrayList<>();
        list.add("General");
        list.add("Tatkal");

        ArrayAdapter<String> adapter=new ArrayAdapter<>(MainActivity.this,R.layout.sp_item,list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.quotaSpinner.setAdapter(adapter);
        binding.progressbarQuota.setVisibility(View.GONE);
    }

    private void initPassengers() {

        binding.plusAdultBtn.setOnClickListener(v -> {
            Log.d("Passenger", "Plus Button Clicked");
            adultPassengers++;
            binding.AdultText.setText(adultPassengers + " Adult");
        });
        binding.minusAdultBtn.setOnClickListener(v -> {
            if (adultPassengers > 1) {
                adultPassengers--;
                binding.AdultText.setText(adultPassengers + " Adult");
            }
        });


        binding.plusChildBtn.setOnClickListener(v -> {
            childPassengers++;
            binding.ChildText.setText(childPassengers + " Child");
        });

        binding.minusChildBtn.setOnClickListener(v -> {
            if (childPassengers > 0) {
                childPassengers--;
                binding.ChildText.setText(childPassengers + " Child");
            }
        });
    }

    private void showDatePickerDialog() {
        Calendar today = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

                    // ✅ Update the departure date text
                    binding.departuredateText.setText(sdf.format(selectedDate.getTime()));

                    // ✅ Save the selected date in SharedPreferences to persist
                    SharedPreferences sharedPreferences = getSharedPreferences("LastSearchPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("journeyDate", sdf.format(selectedDate.getTime()));
                    editor.apply();

                    binding.clearDateIcon.setVisibility(View.VISIBLE);
                },
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)
        );

        // **Fix:** Ensure minimum date is always today
        datePickerDialog.getDatePicker().setMinDate(today.getTimeInMillis());

        datePickerDialog.show();
    }



    private void resetDate() {
        calendar = (Calendar) originalCalendar.clone();
        updateDate();
    }
    private void toggleSelection(LinearLayout button, TextView text1, TextView text2, int daysToAdd) {
        boolean isSelected = button.isSelected();
        resetSelection();
        if (!isSelected) {
            button.setSelected(true);
            text1.setSelected(true);
            text2.setSelected(true);
            calendar.add(Calendar.DAY_OF_YEAR, daysToAdd);
            updateDate();
            if (daysToAdd == 1) isNextDateSelected = true;
            if (daysToAdd == 2) isDayAfterSelected = true;
        }
    }

    private void resetSelection() {
        isNextDateSelected = false;
        isDayAfterSelected = false;
        binding.tomorrowBtn.setSelected(false);
        binding.dayafterBtn.setSelected(false);
        binding.tomorrowText.setSelected(false);
        binding.dayafterText.setSelected(false);
        binding.tatkalopenText.setSelected(false);
        binding.tatkalopenText1.setSelected(false);
        resetDate();
    }


@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            String selectedStation = data.getStringExtra("selected_station");

            if (requestCode == REQUEST_CODE_FROM) {
                if (binding.toTextview.getText().toString().equals(selectedStation)) {
                    Toast.makeText(this, "Source and destination cannot be the same", Toast.LENGTH_SHORT).show();
                } else {
                    binding.fromTextview.setText(selectedStation);
                }
            } else if (requestCode == REQUEST_CODE_TO) {
                if (binding.fromTextview.getText().toString().equals(selectedStation)) {
                    Toast.makeText(this, "Source and destination cannot be the same", Toast.LENGTH_SHORT).show();
                } else {
                    binding.toTextview.setText(selectedStation);

                }
            }
        }


    }




    private void openStationPicker(int requestCode) {
        Intent intent = new Intent(MainActivity.this, StationPickerActivity.class);
        startActivityForResult(intent, requestCode); // ✅ This will return selected station data
    }



    private void toggleSection() {
        if (isExpanded) {
            collapseSection(binding.expandableSection);
            binding.btnExpandCollapse.setImageResource(R.drawable.ic_expand_collapse);
        } else {
            expandSection(binding.expandableSection);
            binding.btnExpandCollapse.setImageResource(R.drawable.collapse_btn);
        }
        isExpanded = !isExpanded; // Toggle state
    }

    private void expandSection(View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int targetHeight = view.getMeasuredHeight();

        ValueAnimator animator = ValueAnimator.ofInt(0, targetHeight);
        animator.setDuration(300); // Animation duration (milliseconds)
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            view.getLayoutParams().height = (int) animation.getAnimatedValue();
            view.requestLayout();
        });

        view.setVisibility(View.VISIBLE);
        animator.start();
    }

    private void collapseSection(View view) {
        int initialHeight = view.getHeight();

        ValueAnimator animator = ValueAnimator.ofInt(initialHeight, 0);
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            view.getLayoutParams().height = (int) animation.getAnimatedValue();
            view.requestLayout();
        });

        animator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                view.setVisibility(View.GONE);
            }
        });

        animator.start();

    }

}
