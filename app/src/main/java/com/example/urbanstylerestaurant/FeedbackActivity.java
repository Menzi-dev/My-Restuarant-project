package com.example.urbanstylerestaurant;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FeedbackActivity extends AppCompatActivity {

    private EditText feedbackInput;
    private CheckBox anonymousCheckBox;
    private ImageView[] starViews;
    private Button submitButton;
    private ImageButton backButton;
    private DatabaseReference feedbackRef;
    private FirebaseAuth auth;
    private int rating = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        feedbackInput = findViewById(R.id.feedbackInput);
        anonymousCheckBox = findViewById(R.id.anonymousCheckBox);
        submitButton = findViewById(R.id.submitButton);
        backButton = findViewById(R.id.backButton);

        // Initialize star views
        starViews = new ImageView[]{
                findViewById(R.id.star1),
                findViewById(R.id.star2),
                findViewById(R.id.star3),
                findViewById(R.id.star4),
                findViewById(R.id.star5)
        };

        auth = FirebaseAuth.getInstance();
        feedbackRef = FirebaseDatabase.getInstance().getReference("manager_feedback");

        // Set up star rating click listeners
        for (int i = 0; i < starViews.length; i++) {
            final int starIndex = i + 1;
            starViews[i].setOnClickListener(v -> setRating(starIndex));
        }

        submitButton.setOnClickListener(v -> submitFeedback());
        backButton.setOnClickListener(v -> finish());
    }

    private void setRating(int selectedRating) {
        rating = selectedRating;
        for (int i = 0; i < starViews.length; i++) {
            if (i < rating) {
                starViews[i].setImageResource(android.R.drawable.star_big_on);
                starViews[i].setColorFilter(Color.parseColor("#FFD700")); // Gold color for selected stars
            } else {
                starViews[i].setImageResource(android.R.drawable.star_big_off);
                starViews[i].setColorFilter(Color.parseColor("#B0BEC5")); // Gray color for unselected stars
            }
        }
    }

    private void submitFeedback() {
        String feedbackText = feedbackInput.getText().toString().trim();
        if (feedbackText.isEmpty() || rating == 0) {
            Snackbar.make(findViewById(android.R.id.content), "Please provide feedback and a rating", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(Color.parseColor("#FF5722"))
                    .setTextColor(Color.WHITE)
                    .show();
            return;
        }

        FirebaseUser user = auth.getCurrentUser();
        String userId = anonymousCheckBox.isChecked() ? "Anonymous" : (user != null ? user.getUid() : "Unknown");
        String userName = anonymousCheckBox.isChecked() ? "Anonymous" : (user != null && user.getDisplayName() != null ? user.getDisplayName() : "Guest");
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String feedbackId = feedbackRef.push().getKey();

        Map<String, Object> feedbackData = new HashMap<>();
        feedbackData.put("feedback_id", feedbackId);
        feedbackData.put("user_id", userId);
        feedbackData.put("user_name", userName);
        feedbackData.put("rating", rating);
        feedbackData.put("feedback_text", feedbackText);
        feedbackData.put("timestamp", timestamp);

        assert feedbackId != null;
        feedbackRef.child(feedbackId).setValue(feedbackData)
                .addOnSuccessListener(unused -> {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Feedback Submitted Successfully ✅", Snackbar.LENGTH_LONG);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(Color.parseColor("#FF5722"));
                    TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    textView.setTextSize(16);
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    snackbar.show();

                    new Handler().postDelayed(() -> {
                        Intent intent = new Intent(FeedbackActivity.this, ThankYouActivity.class);
                        startActivity(intent);
                        finish();
                    }, 1500);
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(findViewById(android.R.id.content), "Failed to submit feedback: " + e.getMessage(), Snackbar.LENGTH_LONG)
                            .setBackgroundTint(Color.parseColor("#FF5722"))
                            .setTextColor(Color.WHITE)
                            .show();
                });
    }
}