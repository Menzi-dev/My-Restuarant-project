package com.example.urbanstylerestaurant;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.*;
import java.util.HashMap;
import java.util.Map;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

public class ManagerDashboardActivity extends AppCompatActivity {

    private TextView totalUsersText, totalIncomeText, totalReservationsText;
    private LinearLayout mainContentContainer;
    private DatabaseReference database;
    private ImageButton backButton;

    private static final String TAG = "ManagerDashboard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_dashboard);

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Manager Dashboard");
        }

        // Dashboard totals
        totalUsersText = findViewById(R.id.totalUsersText);
        totalIncomeText = findViewById(R.id.totalIncomeText);
        totalReservationsText = findViewById(R.id.totalReservationsText);
        backButton = findViewById(R.id.backButton);

        // Back button listener to return to the main dashboard
        backButton.setOnClickListener(v -> finish());

        // Main content container
        mainContentContainer = findViewById(R.id.mainContentContainer);

        // Card views
        MaterialCardView cardManageMenu = findViewById(R.id.cardManageMenu);
        MaterialCardView cardReservations = findViewById(R.id.cardReservations);
        MaterialCardView cardSalesStock = findViewById(R.id.cardSalesStock);

        database = FirebaseDatabase.getInstance().getReference();



        // Set up card click listeners with animations
        cardManageMenu.setOnClickListener(v -> {
            applyScaleAnimation(v);
            mainContentContainer.removeAllViews();
            showMenuItemsSection();
        });

        cardReservations.setOnClickListener(v -> {
            applyScaleAnimation(v);
            mainContentContainer.removeAllViews();
            showReservationsSection();
        });

        cardSalesStock.setOnClickListener(v -> {
            applyScaleAnimation(v);
            mainContentContainer.removeAllViews();
            showStockSection();
        });

        // Load dashboard stats and default section
        calculateDashboardStats();
        showMenuItemsSection(); // Default section

        // Show users section as part of dashboard
        mainContentContainer.addView(new View(this)); // Spacer
        showUsersSection();
    }

    private void calculateDashboardStats() {
        database.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                long userCount = snapshot.getChildrenCount();
                totalUsersText.setText(String.valueOf(userCount));
                Log.d(TAG, "Fetched users: " + userCount);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to fetch users: " + error.getMessage());
                Toast.makeText(ManagerDashboardActivity.this, "Failed to fetch users", Toast.LENGTH_SHORT).show();
            }
        });

        database.child("reservations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                long reservationCount = snapshot.getChildrenCount();
                totalReservationsText.setText(String.valueOf(reservationCount));
                Log.d(TAG, "Fetched reservations: " + reservationCount);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to fetch reservations: " + error.getMessage());
                Toast.makeText(ManagerDashboardActivity.this, "Failed to fetch reservations", Toast.LENGTH_SHORT).show();
            }
        });

        database.child("orders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                double income = 0;
                for (DataSnapshot order : snapshot.getChildren()) {
                    String status = order.child("status").getValue(String.class);
                    if ("Order Delivered".equals(status)) {
                        Object totalObj = order.child("total").getValue();
                        if (totalObj != null) {
                            try {
                                income += Double.parseDouble(String.valueOf(totalObj));
                            } catch (NumberFormatException e) {
                                Log.e(TAG, "Invalid total for order: " + order.getKey());
                            }
                        }
                    }
                }
                totalIncomeText.setText("R" + String.format("%.2f", income));
                Log.d(TAG, "Fetched total income: R" + income);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to fetch income: " + error.getMessage());
                Toast.makeText(ManagerDashboardActivity.this, "Failed to fetch income", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showMenuItemsSection() {
        View sectionView = LayoutInflater.from(this).inflate(R.layout.manager_menu_section, null);
        LinearLayout menuListContainer = sectionView.findViewById(R.id.menuListContainer);
        EditText newItemName = sectionView.findViewById(R.id.newItemName);
        EditText newItemPrice = sectionView.findViewById(R.id.newItemPrice);
        Spinner categorySpinner = sectionView.findViewById(R.id.categorySpinner);
        Spinner imageSpinner = sectionView.findViewById(R.id.imageSpinner);
        Button addItemBtn = sectionView.findViewById(R.id.addItemBtn);
        ProgressBar progressBar = sectionView.findViewById(R.id.progressBar);
        TextView errorMessage = sectionView.findViewById(R.id.errorMessage);

        loadMenuItems(menuListContainer, progressBar, errorMessage);

        addItemBtn.setOnClickListener(v -> {
            String name = newItemName.getText().toString().trim();
            String priceStr = newItemPrice.getText().toString().trim();
            String category = categorySpinner.getSelectedItem().toString();
            String imageRes = imageSpinner.getSelectedItem().toString();

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Enter name and price", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double price = Double.parseDouble(priceStr);
                String itemId = database.child("menuItems").push().getKey();

                Map<String, Object> newItem = new HashMap<>();
                newItem.put("name", name);
                newItem.put("price", price);
                newItem.put("available", true);
                newItem.put("category", category);
                newItem.put("imageRes", imageRes);

                database.child("menu").child(itemId).setValue(newItem)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Menu item added to " + category + "!", Toast.LENGTH_SHORT).show();
                            newItemName.setText("");
                            newItemPrice.setText("");
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Failed to add menu item: " + e.getMessage());
                            Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show();
                        });
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
            }
        });

        applyFadeInAnimation(sectionView);
        mainContentContainer.addView(sectionView);
    }

    private void loadMenuItems(LinearLayout menuListContainer, ProgressBar progressBar, TextView errorMessage) {
        progressBar.setVisibility(View.VISIBLE);
        menuListContainer.setVisibility(View.GONE);
        errorMessage.setVisibility(View.GONE);

        database.child("menu").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                menuListContainer.removeAllViews();
                if (!snapshot.exists()) {
                    progressBar.setVisibility(View.GONE);
                    menuListContainer.setVisibility(View.GONE);
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText("No menu items found");
                    return;
                }
                for (DataSnapshot item : snapshot.getChildren()) {
                    String id = item.getKey();
                    String name = item.child("name").getValue(String.class);
                    Double price = item.child("price").getValue(Double.class);
                    String category = item.child("category").getValue(String.class);
                    String imageRes = item.child("imageRes").getValue(String.class);

                    View view = LayoutInflater.from(ManagerDashboardActivity.this)
                            .inflate(R.layout.manager_menu_item, null);

                    LinearLayout menuItemContainer = view.findViewById(R.id.menuItemContainer);
                    LinearLayout editMenuItemContainer = view.findViewById(R.id.editMenuItemContainer);
                    TextView itemName = view.findViewById(R.id.menuItemName);
                    TextView itemPrice = view.findViewById(R.id.menuItemPrice);
                    TextView itemCategory = view.findViewById(R.id.menuItemCategory);
                    EditText editItemPrice = view.findViewById(R.id.editItemPrice);
                    Button updateBtn = view.findViewById(R.id.updatePriceBtn);
                    Button deleteBtn = view.findViewById(R.id.deleteItemBtn);

                    itemName.setText(name != null ? name : "Unknown Item");
                    itemPrice.setText(price != null ? "R" + String.format("%.2f", price) : "R0.00");
                    itemCategory.setText(category != null ? category : "No Category");
                    editItemPrice.setText(price != null ? String.format("%.2f", price) : "0.00");

                    menuItemContainer.setOnClickListener(v -> {
                        editMenuItemContainer.setVisibility(
                                editMenuItemContainer.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE
                        );
                    });

                    updateBtn.setOnClickListener(v -> {
                        try {
                            double newPrice = Double.parseDouble(editItemPrice.getText().toString());
                            database.child("menu").child(id).child("price").setValue(newPrice)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(ManagerDashboardActivity.this, "Price updated!", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(ManagerDashboardActivity.this, "Failed to update price", Toast.LENGTH_SHORT).show();
                                    });
                        } catch (NumberFormatException e) {
                            Toast.makeText(ManagerDashboardActivity.this, "Invalid price", Toast.LENGTH_SHORT).show();
                        }
                    });

                    deleteBtn.setOnClickListener(v -> {
                        database.child("menu").child(id).removeValue()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(ManagerDashboardActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(ManagerDashboardActivity.this, "Failed to delete item", Toast.LENGTH_SHORT).show();
                                });
                    });

                    menuListContainer.addView(view);
                }
                progressBar.setVisibility(View.GONE);
                menuListContainer.setVisibility(View.VISIBLE);
                errorMessage.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                menuListContainer.setVisibility(View.GONE);
                errorMessage.setVisibility(View.VISIBLE);
                errorMessage.setText("Failed to fetch menu items: " + error.getMessage());
                Toast.makeText(ManagerDashboardActivity.this, "Failed to fetch menu items", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUsersSection() {
        View sectionView = LayoutInflater.from(this).inflate(R.layout.manager_users_section, null);
        LinearLayout usersListContainer = sectionView.findViewById(R.id.usersListContainer);
        ProgressBar progressBar = sectionView.findViewById(R.id.progressBar);
        TextView errorMessage = sectionView.findViewById(R.id.errorMessage);

        progressBar.setVisibility(View.VISIBLE);
        usersListContainer.setVisibility(View.GONE);
        errorMessage.setVisibility(View.GONE);

        database.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                usersListContainer.removeAllViews();
                if (!snapshot.exists()) {
                    progressBar.setVisibility(View.GONE);
                    usersListContainer.setVisibility(View.GONE);
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText("No users found");
                    return;
                }
                for (DataSnapshot user : snapshot.getChildren()) {
                    String name = user.child("name").getValue(String.class);
                    String role = user.child("role").getValue(String.class);

                    View view = LayoutInflater.from(ManagerDashboardActivity.this)
                            .inflate(R.layout.manager_user_item, null);

                    TextView userName = view.findViewById(R.id.userName);
                    TextView userRole = view.findViewById(R.id.userRole);

                    userName.setText(name != null ? name : "Unknown User");
                    userRole.setText(role != null ? role : "No Role");

                    usersListContainer.addView(view);
                }
                progressBar.setVisibility(View.GONE);
                usersListContainer.setVisibility(View.VISIBLE);
                errorMessage.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                usersListContainer.setVisibility(View.GONE);
                errorMessage.setVisibility(View.VISIBLE);
                errorMessage.setText("Failed to fetch users: " + error.getMessage());
                Toast.makeText(ManagerDashboardActivity.this, "Failed to fetch users", Toast.LENGTH_SHORT).show();
            }
        });

        applyFadeInAnimation(sectionView);
        mainContentContainer.addView(sectionView);
    }

    private void showReservationsSection() {
        View sectionView = LayoutInflater.from(this).inflate(R.layout.manager_reservations_section, null);
        LinearLayout reservationsListContainer = sectionView.findViewById(R.id.reservationsListContainer);
        ProgressBar progressBar = sectionView.findViewById(R.id.progressBar);
        TextView errorMessage = sectionView.findViewById(R.id.errorMessage);


        progressBar.setVisibility(View.VISIBLE);
        reservationsListContainer.setVisibility(View.GONE);
        errorMessage.setVisibility(View.GONE);

        database.child("manager_reservations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                reservationsListContainer.removeAllViews();
                if (!snapshot.exists()) {
                    progressBar.setVisibility(View.GONE);
                    reservationsListContainer.setVisibility(View.GONE);
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText("No reservations found");
                    return;
                }
                boolean hasUnaccepted = false;
                for (DataSnapshot reservation : snapshot.getChildren()) {
                    String reservationId = reservation.getKey();
                    String userName = reservation.child("userName").getValue(String.class);
                    String userId = reservation.child("user_id").getValue(String.class);
                    Long tableNumber = reservation.child("tableNumber").getValue(Long.class);
                    String date = reservation.child("date").getValue(String.class);
                    String time = reservation.child("time").getValue(String.class);
                    String status = reservation.child("status").getValue(String.class);

                    if (!"Pending".equals(status)) {
                        continue;
                    }

                    hasUnaccepted = true;

                    View view = LayoutInflater.from(ManagerDashboardActivity.this)
                            .inflate(R.layout.manager_reservation_item, null);

                    TextView reservationUserName = view.findViewById(R.id.reservationUserName);
                    TextView reservationUserId = view.findViewById(R.id.reservationUserId);
                    TextView reservationTableNumber = view.findViewById(R.id.reservationTableNumber);
                    TextView reservationDateTime = view.findViewById(R.id.reservationDateTime);
                    TextView reservationStatus = view.findViewById(R.id.reservationStatus);
                    Button acceptButton = view.findViewById(R.id.acceptButton);
                    Button declineButton = view.findViewById(R.id.declineButton);

                    reservationUserName.setText(userName != null ? userName : "Unknown User");
                    reservationUserId.setText("User ID: " + (userId != null ? userId : "Unknown"));
                    reservationTableNumber.setText("Table Number: " + (tableNumber != null ? tableNumber.toString() : "Unknown"));
                    reservationDateTime.setText((date != null && time != null) ? date + ", " + time : "Unknown Date/Time");
                    reservationStatus.setText(status != null ? status : "Unknown Status");

                    acceptButton.setOnClickListener(v -> {
                        database.child("manager_reservations").child(reservationId).child("status").setValue("Accepted")
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(ManagerDashboardActivity.this, "Reservation accepted", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(ManagerDashboardActivity.this, "Failed to accept reservation", Toast.LENGTH_SHORT).show();
                                });
                    });

                    declineButton.setOnClickListener(v -> {
                        database.child("manager_reservations").child(reservationId).child("status").setValue("Rejected")
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(ManagerDashboardActivity.this, "Reservation declined", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(ManagerDashboardActivity.this, "Failed to decline reservation", Toast.LENGTH_SHORT).show();
                                });
                    });

                    reservationsListContainer.addView(view);
                }
                progressBar.setVisibility(View.GONE);
                if (!hasUnaccepted) {
                    reservationsListContainer.setVisibility(View.GONE);
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText("No unaccepted reservations");
                } else {
                    reservationsListContainer.setVisibility(View.VISIBLE);
                    errorMessage.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                reservationsListContainer.setVisibility(View.GONE);
                errorMessage.setVisibility(View.VISIBLE);
                errorMessage.setText("Failed to fetch reservations: " + error.getMessage());
                Toast.makeText(ManagerDashboardActivity.this, "Failed to fetch reservations", Toast.LENGTH_SHORT).show();
            }
        });

        applyFadeInAnimation(sectionView);
        mainContentContainer.addView(sectionView);
    }

    private void showStockSection() {
        View sectionView = LayoutInflater.from(this).inflate(R.layout.manager_stock_section, null);
        LinearLayout stockListContainer = sectionView.findViewById(R.id.stockListContainer);
        ProgressBar progressBar = sectionView.findViewById(R.id.progressBar);
        TextView errorMessage = sectionView.findViewById(R.id.errorMessage);

        progressBar.setVisibility(View.VISIBLE);
        stockListContainer.setVisibility(View.GONE);
        errorMessage.setVisibility(View.GONE);

        database.child("menu").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                stockListContainer.removeAllViews();
                if (!snapshot.exists()) {
                    progressBar.setVisibility(View.GONE);
                    stockListContainer.setVisibility(View.GONE);
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText("No items found");
                    return;
                }
                for (DataSnapshot item : snapshot.getChildren()) {
                    String name = item.child("name").getValue(String.class);
                    Boolean available = item.child("available").getValue(Boolean.class);

                    View view = LayoutInflater.from(ManagerDashboardActivity.this)
                            .inflate(R.layout.manager_stock_item, null);

                    TextView stockItemName = view.findViewById(R.id.stockItemName);
                    TextView stockAvailability = view.findViewById(R.id.stockAvailability);

                    stockItemName.setText(name != null ? name : "Unknown Item");
                    if (available != null && available) {
                        stockAvailability.setText("Available");
                        stockAvailability.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    } else {
                        stockAvailability.setText("Not Available");
                        stockAvailability.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    }

                    stockListContainer.addView(view);
                }
                progressBar.setVisibility(View.GONE);
                stockListContainer.setVisibility(View.VISIBLE);
                errorMessage.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                stockListContainer.setVisibility(View.GONE);
                errorMessage.setVisibility(View.VISIBLE);
                errorMessage.setText("Failed to fetch stock items: " + error.getMessage());
                Toast.makeText(ManagerDashboardActivity.this, "Failed to fetch stock items", Toast.LENGTH_SHORT).show();
            }
        });

        applyFadeInAnimation(sectionView);
        mainContentContainer.addView(sectionView);
    }

    private void applyFadeInAnimation(View view) {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(500);
        view.startAnimation(fadeIn);
    }

    private void applyScaleAnimation(View view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                1.0f, 0.9f, 1.0f, 0.9f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        scaleAnimation.setDuration(100);
        scaleAnimation.setRepeatCount(1);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        view.startAnimation(scaleAnimation);
    }
}