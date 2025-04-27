package com.example.urbanstylerestaurant;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WaiterDashboardActivity extends AppCompatActivity {

    private LinearLayout orderContainer, readyOrdersContainer;
    private DatabaseReference ordersRef, usersRef, reservationsRef;
    private TextView greetingText;
    private static final String TAG = "WaiterDashboard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiter_dashboard);

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Waiter Dashboard");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        orderContainer = findViewById(R.id.ordersContainer);
        readyOrdersContainer = findViewById(R.id.readyOrdersContainer);
        ImageView backArrow = findViewById(R.id.backArrow);
        greetingText = findViewById(R.id.greetingText);

        ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        reservationsRef = FirebaseDatabase.getInstance().getReference("manager_reservations");

        // Set up personalized greeting
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String waiterName = user != null && user.getDisplayName() != null ? user.getDisplayName() : "Waiter";
        greetingText.setText("Hey Waiter " + waiterName + "!");

        // Set up back button listener (for both Toolbar and ImageView)
        backArrow.setOnClickListener(v -> finish());
        toolbar.setNavigationOnClickListener(v -> finish());

        loadOrders();
    }

    private void loadOrders() {
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                orderContainer.removeAllViews();
                readyOrdersContainer.removeAllViews();

                for (DataSnapshot orderSnap : snapshot.getChildren()) {
                    String status = orderSnap.child("status").getValue(String.class);
                    String orderId = orderSnap.getKey();
                    String userId = orderSnap.child("user_id").getValue(String.class);
                    String items = orderSnap.child("items").getValue(String.class);
                    Object totalObj = orderSnap.child("total").getValue();
                    String total = totalObj != null ? String.valueOf(totalObj) : "N/A";

                    String finalUserId = userId != null ? userId : "N/A";
                    String finalItems = items != null ? items : "N/A";

                    // Fetch customer name from users node
                    if (finalUserId.equals("N/A")) {
                        // If no userId, display order with default values
                        displayOrder(status, orderId, "Unknown", finalUserId, "N/A", finalItems, total);
                    } else {
                        usersRef.child(finalUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot userSnapshot) {
                                String customerName = userSnapshot.child("name").getValue(String.class);
                                if (customerName == null) {
                                    customerName = "Unknown";
                                    Log.w(TAG, "Customer name not found for user ID: " + finalUserId);
                                }

                                // Fetch table number from manager_reservations node
                                String finalCustomerName = customerName;
                                reservationsRef.orderByChild("user_id").equalTo(finalUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot reservationSnapshot) {
                                        String tableNumber = "N/A";
                                        for (DataSnapshot resSnap : reservationSnapshot.getChildren()) {
                                            String resStatus = resSnap.child("status").getValue(String.class);
                                            if ("Accepted".equals(resStatus)) {
                                                Object tableNumberObj = resSnap.child("tableNumber").getValue();
                                                if (tableNumberObj != null) {
                                                    tableNumber = String.valueOf(tableNumberObj);
                                                }
                                                break;
                                            }
                                        }

                                        // Display the order with the fetched customer name and table number
                                        displayOrder(status, orderId, finalCustomerName, finalUserId, tableNumber, finalItems, total);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        Log.e(TAG, "Failed to fetch reservation for user " + finalUserId + ": " + error.getMessage());
                                        // Fallback: Display the order even if table number fetch fails
                                        displayOrder(status, orderId, finalCustomerName, finalUserId, "N/A", finalItems, total);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                Log.e(TAG, "Failed to fetch customer name for user " + finalUserId + ": " + error.getMessage());
                                // Fallback: Display the order even if customer name fetch fails
                                displayOrder(status, orderId, "Unknown", finalUserId, "N/A", finalItems, total);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "loadOrders: Failed to load orders: " + error.getMessage());
                Toast.makeText(WaiterDashboardActivity.this, "Failed to load orders: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayOrder(String status, String orderId, String customerName, String userId, String tableNumber, String items, String total) {
        // Display "Order Submitted" orders in the "Orders to Process" section
        if (status != null && status.equals("Order Submitted")) {
            View card = getLayoutInflater().inflate(R.layout.waiter_order_card, null);
            TextView orderInfoText = card.findViewById(R.id.orderInfo);
            Button acceptButton = card.findViewById(R.id.acceptOrder);
            Button declineButton = card.findViewById(R.id.declineOrder);

            String orderDetails = "Customer: " + customerName +
                    "\nUser ID: " + userId +
                    "\nTable: " + tableNumber +
                    "\nItems:\n" + items +
                    "\nTotal: R" + total;
            orderInfoText.setText(orderDetails);

            acceptButton.setOnClickListener(v -> updateOrderStatus(orderId, "Order Accepted"));
            declineButton.setOnClickListener(v -> updateOrderStatus(orderId, "Order Declined"));

            // Apply fade-in animation
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setDuration(500);
            card.startAnimation(fadeIn);

            orderContainer.addView(card);
        }

        // Display "Order Ready" orders in the "Ready Orders to Deliver" section
        if (status != null && status.equals("Order Ready")) {
            View card = getLayoutInflater().inflate(R.layout.waiter_ready_card, null);
            TextView readyInfoText = card.findViewById(R.id.readyInfo);
            Button deliverButton = card.findViewById(R.id.deliverButton);

            String readyDetails = "Customer: " + customerName +
                    "\nUser ID: " + userId +
                    "\nTable: " + tableNumber +
                    "\nItems:\n" + items +
                    "\nTotal: R" + total;
            readyInfoText.setText(readyDetails);

            deliverButton.setOnClickListener(v -> updateOrderStatus(orderId, "Order Delivered"));

            // Apply fade-in animation
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setDuration(500);
            card.startAnimation(fadeIn);

            readyOrdersContainer.addView(card);
        }
    }

    private void updateOrderStatus(String orderId, String status) {
        ordersRef.child(orderId).child("status").setValue(status)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Order " + status, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}