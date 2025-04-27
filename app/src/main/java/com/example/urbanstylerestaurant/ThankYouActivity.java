package com.example.urbanstylerestaurant;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ThankYouActivity extends AppCompatActivity {

    private static final String TAG = "ThankYouActivity";
    private TextView thankYouMessage;
    private Button trackButton, feedbackButton, logoutButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);
        Log.d(TAG, "onCreate called");

        thankYouMessage = findViewById(R.id.thankYouMessage);
        trackButton = findViewById(R.id.trackButton);
        feedbackButton = findViewById(R.id.feedbackButton);
        logoutButton = findViewById(R.id.logoutButton);

        auth = FirebaseAuth.getInstance();
        displayThankYouMessage();

        trackButton.setOnClickListener(v -> {
            Log.d(TAG, "Track button clicked, launching CustomerTrackerActivity");
            Intent intent = new Intent(ThankYouActivity.this, CustomerTrackerActivity.class);
            startActivity(intent);
        });

        feedbackButton.setOnClickListener(v -> {
            Log.d(TAG, "Feedback button clicked, launching FeedbackActivity");
            Intent intent = new Intent(ThankYouActivity.this, FeedbackActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            Log.d(TAG, "Logout button clicked");
            auth.signOut();
            finishAffinity();
        });
    }

    private void displayThankYouMessage() {
        FirebaseUser user = auth.getCurrentUser();
        String userName = user != null && user.getDisplayName() != null ? user.getDisplayName() : "Guest";
        String message = "Thank you, " + userName + "! We’re excited to serve you. Your order and table reservation have been confirmed. Enjoy your dining experience! 🎉";
        thankYouMessage.setText(message);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
    }
}