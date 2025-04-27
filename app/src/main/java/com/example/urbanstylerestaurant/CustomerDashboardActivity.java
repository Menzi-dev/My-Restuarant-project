package com.example.urbanstylerestaurant;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CustomerDashboardActivity extends AppCompatActivity {

    private ImageButton breakfastButton, lunchButton, dinnerButton, feedbackButton, drinksButton, trackOrdersButton, cartButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        // Find buttons
        breakfastButton = findViewById(R.id.breakfastButton);
        lunchButton = findViewById(R.id.lunchButton);
        dinnerButton = findViewById(R.id.dinnerButton);
        drinksButton = findViewById(R.id.drinksButton);
        trackOrdersButton = findViewById(R.id.trackOrderButton);
        feedbackButton = findViewById(R.id.feedbackButton);
        cartButton = findViewById(R.id.cartButton);
        backButton = findViewById(R.id.backButton);

        // Animation setup
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        // Appear two by two with delays
        new Handler().postDelayed(() -> {
            breakfastButton.startAnimation(fadeIn);
            lunchButton.startAnimation(fadeIn);
        }, 500);

        new Handler().postDelayed(() -> {
            dinnerButton.startAnimation(fadeIn);
            drinksButton.startAnimation(fadeIn);
        }, 1000);

        new Handler().postDelayed(() -> {
            trackOrdersButton.startAnimation(fadeIn);
            feedbackButton.startAnimation(fadeIn);
        }, 1500);

        // Button click actions
        breakfastButton.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerDashboardActivity.this, BreakfastMenuActivity.class);
            startActivity(intent);
        });

        lunchButton.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerDashboardActivity.this, LunchMenuActivity.class);
            startActivity(intent);
        });

        dinnerButton.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerDashboardActivity.this, DinnerMenuActivity.class);
            startActivity(intent);
        });

        drinksButton.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerDashboardActivity.this, DrinksMenuActivity.class);
            startActivity(intent);
        });

        trackOrdersButton.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerDashboardActivity.this, CustomerTrackerActivity.class);
            startActivity(intent);
        });

        cartButton.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerDashboardActivity.this, CartActivity.class);
            startActivity(intent);
        });

        backButton.setOnClickListener(v -> {
            finish();
        });

         feedbackButton.setOnClickListener(v -> {
          Intent intent = new Intent(CustomerDashboardActivity.this, FeedbackActivity.class);
          startActivity(intent);});
    }
}