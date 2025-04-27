package com.example.urbanstylerestaurant;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class CustomerTrackerActivity extends AppCompatActivity {

    private static final String TAG = "CustomerTrackerActivity";
    private TextView orderStatusText, reservationStatusText;
    private ProgressBar loadingProgressBar;
    private DatabaseReference database;
    private String userId;
    private boolean orderStatusLoaded = false;
    private boolean reservationStatusLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_tracker);
        Log.d(TAG, "onCreate called");

        orderStatusText = findViewById(R.id.orderStatusText);
        reservationStatusText = findViewById(R.id.reservationStatusText);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        ImageButton backButton = findViewById(R.id.backButton);

        // Check if the user is logged in
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e(TAG, "User is not logged in");
            orderStatusText.setText("Error: User not logged in. Please log in again.");
            reservationStatusText.setText("Error: User not logged in. Please log in again.");
            loadingProgressBar.setVisibility(View.GONE);
            backButton.setOnClickListener(v -> {
                Log.d(TAG, "Back button pressed");
                finish();
            });
            return;
        }

        userId = user.getUid();
        database = FirebaseDatabase.getInstance().getReference();

        backButton.setOnClickListener(v -> {
            Log.d(TAG, "Back button pressed");
            finish();
        });

        loadingProgressBar.setVisibility(View.VISIBLE);

        fetchOrderStatus();
        fetchReservationStatus();
    }

    private void fetchOrderStatus() {
        orderStatusText.setVisibility(View.GONE);

        database.child("orders").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d(TAG, "fetchOrderStatus: onDataChange");
                orderStatusText.setVisibility(View.VISIBLE);
                if (snapshot.exists() && snapshot.hasChild("status")) {
                    String status = snapshot.child("status").getValue(String.class);
                    String items = snapshot.child("items").getValue(String.class);
                    // Safely handle total, which might be a Long or Double
                    Object totalObj = snapshot.child("total").getValue();
                    String total = totalObj != null ? String.valueOf(totalObj) : null;

                    StringBuilder statusDetails = new StringBuilder("Order Status: " + status);
                    if (items != null) {
                        statusDetails.append("\nItems:\n").append(items);
                    }
                    if (total != null) {
                        statusDetails.append("\nTotal: R").append(total);
                    }
                    orderStatusText.setText(statusDetails.toString());
                } else {
                    orderStatusText.setText("No pending order, make an order.");
                }
                orderStatusLoaded = true;
                checkIfLoadingComplete();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "fetchOrderStatus: onCancelled - " + error.getMessage());
                orderStatusText.setVisibility(View.VISIBLE);
                orderStatusText.setText("Error loading order status: " + error.getMessage());
                orderStatusLoaded = true;
                checkIfLoadingComplete();
            }
        });
    }

    private void fetchReservationStatus() {
        reservationStatusText.setVisibility(View.GONE);

        database.child("manager_reservations").orderByChild("user_id").equalTo(userId)
                .limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Log.d(TAG, "fetchReservationStatus: onDataChange");
                        reservationStatusText.setVisibility(View.VISIBLE);
                        if (snapshot.exists()) {
                            for (DataSnapshot resSnap : snapshot.getChildren()) {
                                String status = resSnap.child("status").getValue(String.class);
                                // Safely handle tableNumber, which might be a Long
                                Object tableNumberObj = resSnap.child("tableNumber").getValue();
                                String tableNumber = tableNumberObj != null ? String.valueOf(tableNumberObj) : null;
                                String date = resSnap.child("reservation_date").getValue(String.class);
                                String time = resSnap.child("reservation_time").getValue(String.class);

                                if (status == null) {
                                    Log.w(TAG, "Status field is missing or null for reservation: " + resSnap.getKey());
                                    reservationStatusText.setText("Reservation found, but status is unavailable.");
                                    reservationStatusLoaded = true;
                                    checkIfLoadingComplete();
                                    return;
                                }

                                StringBuilder statusDetails = new StringBuilder("Reservation Status: " + status);
                                if (tableNumber != null) {
                                    statusDetails.append("\nTable Number: ").append(tableNumber);
                                }
                                if (date != null && time != null) {
                                    statusDetails.append("\nDate & Time: ").append(date).append(" at ").append(time);
                                }
                                reservationStatusText.setText(statusDetails.toString());
                                reservationStatusLoaded = true;
                                checkIfLoadingComplete();
                                return;
                            }
                        } else {
                            Log.d(TAG, "No reservations found for userId: " + userId);
                            reservationStatusText.setText("No pending reservation, make a reservation.");
                        }
                        reservationStatusLoaded = true;
                        checkIfLoadingComplete();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e(TAG, "fetchReservationStatus: onCancelled - " + error.getMessage());
                        reservationStatusText.setVisibility(View.VISIBLE);
                        reservationStatusText.setText("Error loading reservation status: " + error.getMessage());
                        reservationStatusLoaded = true;
                        checkIfLoadingComplete();
                    }
                });
    }

    private void checkIfLoadingComplete() {
        if (orderStatusLoaded && reservationStatusLoaded) {
            loadingProgressBar.setVisibility(View.GONE);
            Log.d(TAG, "Loading complete, statuses displayed");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");
    }
}