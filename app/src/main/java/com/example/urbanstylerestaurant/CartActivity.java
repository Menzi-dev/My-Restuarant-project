package com.example.urbanstylerestaurant;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.graphics.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private TextView totalPriceTextView, cartEmptyText;
    private Button clearCartButton, checkoutButton;
    private ImageButton backArrow;
    private DatabaseReference orderRef;
    private FirebaseAuth auth;
    private ArrayList<String> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        cartEmptyText = findViewById(R.id.cartEmptyText);
        clearCartButton = findViewById(R.id.clearCartButton);
        checkoutButton = findViewById(R.id.checkoutButtonCart);
        backArrow = findViewById(R.id.backArrow);

        auth = FirebaseAuth.getInstance();
        orderRef = FirebaseDatabase.getInstance().getReference("orders");

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartItems = CartManager.getItems();
        cartAdapter = new CartAdapter(this, cartItems);
        cartRecyclerView.setAdapter(cartAdapter);

        updateUI();

        clearCartButton.setOnClickListener(v -> {
            CartManager.clearCart();
            cartAdapter.notifyDataSetChanged();
            updateUI();
        });

        checkoutButton.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Snackbar.make(findViewById(android.R.id.content), "Cart is empty!", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(Color.parseColor("#FF5722"))
                        .setTextColor(Color.WHITE)
                        .show();
            } else {
                submitOrderToWaiter();
            }
        });

        backArrow.setOnClickListener(v -> finish());
    }

    private void updateUI() {
        totalPriceTextView.setText("Total: R" + CartManager.getTotalPrice());
        if (cartItems.isEmpty()) {
            cartRecyclerView.setVisibility(View.GONE);
            cartEmptyText.setVisibility(View.VISIBLE);
            clearCartButton.setVisibility(View.GONE);
            checkoutButton.setEnabled(false);
            checkoutButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#B0BEC5")));
        } else {
            cartRecyclerView.setVisibility(View.VISIBLE);
            cartEmptyText.setVisibility(View.GONE);
            clearCartButton.setVisibility(View.VISIBLE);
            checkoutButton.setEnabled(true);
            checkoutButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FFEB3B")));
            cartAdapter.notifyDataSetChanged();
        }
    }

    private void submitOrderToWaiter() {
        String userId = auth.getCurrentUser().getUid();
        String orderId = orderRef.push().getKey();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        StringBuilder items = new StringBuilder();
        for (String item : cartItems) items.append(item).append("\n");

        Map<String, Object> order = new HashMap<>();
        order.put("orderId", orderId);
        order.put("userId", userId);
        order.put("items", items.toString());
        order.put("total", CartManager.getTotalPrice());
        order.put("status", "Order Submitted");
        order.put("timestamp", time);

        assert orderId != null;
        orderRef.child(userId).setValue(order)
                .addOnSuccessListener(unused -> {
                    View rootView = findViewById(android.R.id.content);
                    Snackbar snackbar = Snackbar.make(rootView, "Order Submitted Successfully 🎉", Snackbar.LENGTH_LONG);
                    View snackbarView = snackbar.getView();
                    TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    snackbarView.setBackgroundColor(Color.parseColor("#FF5722"));
                    snackbar.show();

                    new Handler().postDelayed(() -> {
                        Intent intent = new Intent(CartActivity.this, TableReservationActivity.class);
                        startActivity(intent);
                        finish();
                    }, 2000);

                    CartManager.clearCart();
                    cartAdapter.notifyDataSetChanged();
                    updateUI();
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(findViewById(android.R.id.content), "Failed to submit: " + e.getMessage(), Snackbar.LENGTH_LONG)
                            .setBackgroundTint(Color.parseColor("#FF5722"))
                            .setTextColor(Color.WHITE)
                            .show();
                });
    }
}