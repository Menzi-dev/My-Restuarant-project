package com.example.urbanstylerestaurant;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChefDashboardActivity extends AppCompatActivity {

    private TextView greetingText, emptyText;
    private LinearLayout ordersContainer;
    private DatabaseReference db;
    private String chefName = "Chef";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_dashboard);

        db = FirebaseDatabase.getInstance().getReference();

        greetingText = findViewById(R.id.greetingText);
        emptyText = findViewById(R.id.emptyText);
        ordersContainer = findViewById(R.id.orderContainer);
        AppCompatImageView backArrow = findViewById(R.id.backArrow);

        backArrow.setOnClickListener(v -> finish());

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        if (email != null && email.contains("@")) {
            chefName = email.split("@")[0];
        }
        greetingText.setText("Hey Chef: " + chefName);

        loadOrders();
    }

    private void loadOrders() {
        db.child("orders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ordersContainer.removeAllViews();
                boolean hasOrders = false;

                for (DataSnapshot orderSnap : snapshot.getChildren()) {
                    String status = orderSnap.child("status").getValue(String.class);
                    if ("Order Accepted".equals(status) || "Being Prepared".equals(status) || "Order Ready".equals(status)) {
                        hasOrders = true;
                        addOrderCard(orderSnap);
                    }
                }

                emptyText.setVisibility(hasOrders ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ChefDashboardActivity.this, "Failed to load orders", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addOrderCard(DataSnapshot orderSnap) {
        View card = getLayoutInflater().inflate(R.layout.order_card_chef, null);
        TextView orderInfo = card.findViewById(R.id.chefOrderInfo);
        Button actionBtn = card.findViewById(R.id.chefActionButton);

        String orderId = orderSnap.getKey();

        // Safely fetch values from snapshot with null checks
        String items = orderSnap.child("items").getValue() != null
                ? orderSnap.child("items").getValue().toString() : "N/A";
        String total = orderSnap.child("total").getValue() != null
                ? orderSnap.child("total").getValue().toString() : "0.00";
        String currentStatus = orderSnap.child("status").getValue() != null
                ? orderSnap.child("status").getValue().toString() : "Unknown";
        String userId = orderSnap.child("user_id").getValue() != null
                ? orderSnap.child("user_id").getValue(String.class) : "N/A";
        Object tableNumberObj = orderSnap.child("tableNumber").getValue();
        String tableNumber = tableNumberObj != null ? String.valueOf(tableNumberObj) : "N/A";
        String customerName = orderSnap.child("customerName").getValue() != null
                ? orderSnap.child("customerName").getValue(String.class) : "Unknown";

        orderInfo.setText("Customer: " + customerName +
                "\nUser ID: " + userId +
                "\nTable: " + tableNumber +
                "\nItems:\n" + items +
                "\nTotal: R" + total +
                "\nStatus: " + currentStatus);

        // Button logic based on order status
        if ("Order Accepted".equals(currentStatus)) {
            actionBtn.setText("Start Preparing");
            actionBtn.setOnClickListener(v -> {
                db.child("orders").child(orderId).child("status").setValue("Being Prepared")
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Marked as Being Prepared", Toast.LENGTH_SHORT).show();
                            loadOrders(); // Refresh orders
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show();
                        });
            });
        } else if ("Being Prepared".equals(currentStatus)) {
            actionBtn.setText("Mark as Ready");
            actionBtn.setOnClickListener(v -> {
                db.child("orders").child(orderId).child("status").setValue("Order Ready")
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Order Ready! ✅", Toast.LENGTH_SHORT).show();
                            loadOrders(); // Refresh orders
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show();
                        });
            });
        } else if ("Order Ready".equals(currentStatus)) {
            actionBtn.setVisibility(View.GONE); // Hide the button when the order is ready
        }

        ordersContainer.addView(card);
    }
}