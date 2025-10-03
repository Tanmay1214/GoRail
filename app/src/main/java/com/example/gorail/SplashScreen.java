package com.example.gorail;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SplashScreen extends AppCompatActivity {

    private Animation topAnim, bottomAnim;
    private ImageView image;
    private TextView textView;
    private Handler handler;
    private Runnable runnable;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // Disable Night Mode

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        image = findViewById(R.id.logo);
        textView = findViewById(R.id.tagline);

        image.setAnimation(topAnim);
        textView.setAnimation(bottomAnim);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
            checkAndUpdateEmailVerification();
        }




        handler = new Handler(Looper.getMainLooper());
        runnable = () -> {
            Intent iHome = new Intent(SplashScreen.this, StartUp.class);
            startActivity(iHome);
            finish();
        };
        handler.postDelayed(runnable, 2000);
    }

    private void checkAndUpdateEmailVerification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) return; // Exit if user is null

        user.reload().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser updatedUser = FirebaseAuth.getInstance().getCurrentUser(); // Get the latest user state

                if (updatedUser != null) {
                    boolean isVerified = updatedUser.isEmailVerified(); // Check latest verification status

                    // Now, update the database with the correct status
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(updatedUser.getUid());
                    userRef.child("emailVerified").setValue(isVerified);
                }
            } else {
                Log.e("FirebaseAuth", "Failed to reload user", task.getException());
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }




}



