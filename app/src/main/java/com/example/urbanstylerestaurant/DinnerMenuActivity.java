package com.example.urbanstylerestaurant;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class DinnerMenuActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MenuAdapter adapter;
    private List<MenuItem> itemList;
    private DatabaseReference menuRef;
    private TextView noItemsText;
    private Button checkoutButton;
    private ImageButton cartImageButton, backButton;
    private TextView navBarText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dinner_menu);

        recyclerView = findViewById(R.id.dinnerRecyclerView);
        noItemsText = findViewById(R.id.noItemsText);
        checkoutButton = findViewById(R.id.checkoutButton);
        cartImageButton = findViewById(R.id.cartImageButton);
        backButton = findViewById(R.id.backButton);
        navBarText = findViewById(R.id.navBarText);
        progressBar = findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemList = new ArrayList<>();
        adapter = new MenuAdapter(this, itemList);
        recyclerView.setAdapter(adapter);

        menuRef = FirebaseDatabase.getInstance().getReference("menu");

        loadMenuItems();

        // Set up button listeners
        checkoutButton.setOnClickListener(v -> {
            startActivity(new Intent(this, CartActivity.class));
        });

        cartImageButton.setOnClickListener(v -> {
            startActivity(new Intent(this, CartActivity.class));
        });

        backButton.setOnClickListener(v -> finish());
    }

    private void loadMenuItems() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        noItemsText.setVisibility(View.GONE);

        menuRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("DinnerMenuActivity", "DataSnapshot exists: " + snapshot.exists());
                itemList.clear();

                if (!snapshot.exists()) {
                    updateUI();
                    return;
                }

                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    String itemId = itemSnapshot.getKey();
                    String category = itemSnapshot.child("category").getValue(String.class);
                    if (!"Dinner".equals(category)) {
                        continue; // Skip non-Dinner items
                    }

                    String name = itemSnapshot.child("name").getValue(String.class);
                    Double price = itemSnapshot.child("price").getValue(Double.class);
                    String imageRes = itemSnapshot.child("imageRes").getValue(String.class);
                    Boolean availability = itemSnapshot.child("available").getValue(Boolean.class);

                    Log.d("DinnerMenuActivity", "Item: " + name + ", Price: " + price + ", Image: " + imageRes);

                    if (name != null && price != null) {
                        MenuItem item = new MenuItem(
                                itemId,
                                name,
                                price,
                                imageRes != null ? imageRes : "breakfast1", // Fallback to default if imageRes is missing
                                "", // No description in the structure
                                availability != null ? availability : true
                        );
                        itemList.add(item);
                    }
                }

                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DinnerMenuActivity", "Error: " + error.getMessage());
                Toast.makeText(DinnerMenuActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                noItemsText.setVisibility(View.VISIBLE);
                noItemsText.setText("Error loading menu: " + error.getMessage());
            }
        });
    }

    private void updateUI() {
        progressBar.setVisibility(View.GONE);
        if (itemList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            noItemsText.setVisibility(View.VISIBLE);
            noItemsText.setText("No menu items available");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noItemsText.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
    }
}