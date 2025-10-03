package com.example.gorail;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.gorail.adapters.CustomViewPage;
import com.example.gorail.adapters.IntroViewPageAdpater;
import com.example.gorail.adapters.ScreenItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class StartUp extends AppCompatActivity {

    private CustomViewPage screenPager;

    IntroViewPageAdpater introViewPageAdpater;
    TabLayout tabIndicator;
    Button btnNext, btnGetStarted;

    TextView btnAlreadyAccount;
    Animation bottomAnim;



    int position = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // Disable Night Mode
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tabIndicator = findViewById(R.id.tabLayout);
        btnNext = findViewById(R.id.btn_next);
        btnGetStarted = findViewById(R.id.btn_get_started);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        btnAlreadyAccount = findViewById(R.id.btn_already_account);


        List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem(" Find Your Route Instantly", "Easily search and discover the best train routes to your destination with real-time maps and directions.", R.drawable.screenimg2));
        mList.add(new ScreenItem(" Seamless City\nConnections", "Travel effortlessly between cities with our wide rail network, ensuring smooth and scenic journeys.", R.drawable.screenimg3));
        mList.add(new ScreenItem("Book Tickets with a\nTap", "Enjoy a simple, user-friendly booking process. Reserve your seat quickly and securely anytime, anywhere.", R.drawable.screenimg1));
        //setupviewpager
        screenPager = findViewById(R.id.viewPager);
        introViewPageAdpater = new IntroViewPageAdpater(mList, this);
        screenPager.setAdapter(introViewPageAdpater);
        tabIndicator.setupWithViewPager(screenPager);

        //btngraadientcolor
        // 1️⃣ Create gradient shader for text fill
        String text = btnAlreadyAccount.getText().toString();
        float width = btnAlreadyAccount.getPaint().measureText(text);
        Shader textShader = new LinearGradient(0, 0, width, btnAlreadyAccount.getTextSize(),
                new int[]{Color.parseColor("#FFB547"), Color.parseColor("#F58B23")},
                null, Shader.TileMode.CLAMP);

        // 2️⃣ Create a Spannable to handle stroke + gradient
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new CharacterStyle() {
            @Override
            public void updateDrawState(TextPaint tp) {
                tp.setShader(textShader);               // Gradient fill
                tp.setStyle(Paint.Style.FILL_AND_STROKE);
                tp.setStrokeWidth(2);                  // Thickness of stroke
                tp.setStrokeJoin(Paint.Join.ROUND);
                tp.setColor(Color.WHITE);              // Stroke color (outline)
                tp.setAntiAlias(true);
            }
        }, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        btnAlreadyAccount.setText(spannable);          // Apply styled text
        btnAlreadyAccount.invalidate();               // Refresh view


        // locking slide on last
        screenPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == introViewPageAdpater.getCount() - 1) {
                    loadLastScreen();
                    screenPager.setSwipeEnabled(false);  // 🔒 Disable swipe on the last page
                } else {
                    screenPager.setSwipeEnabled(true);   // 🔓 Enable swipe for other pages
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        btnNext.setOnClickListener(v -> {
            position = screenPager.getCurrentItem();
            if (position < mList.size() - 1) {
                position++;
                screenPager.setCurrentItem(position);
            }
            if (position == mList.size() - 1) {
                loadLastScreen();
            }
        });

        btnGetStarted.setOnClickListener(v -> {
            Intent intent = new Intent(StartUp.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });



        btnAlreadyAccount.setOnClickListener(v -> {

            Intent intent= new Intent(StartUp.this,LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });
    }

    private void loadLastScreen() {
        btnGetStarted.setVisibility(View.VISIBLE);
        btnAlreadyAccount.setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.INVISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        btnGetStarted.setAnimation(bottomAnim);
        btnAlreadyAccount.setAnimation(bottomAnim);
    }

    private void navigateToMain() {
        startActivity(new Intent(StartUp.this, MainActivity.class));
        finish(); // Prevent user from going back to register page
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            navigateToMain();
        }
    }



}