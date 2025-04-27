package com.example.urbanstylerestaurant;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TableReservationActivity extends AppCompatActivity {

    private LinearLayout tableContainer;
    private TextView noTablesText;
    private EditText guestCountEditText;
    private Button selectDateButton, selectTimeButton;
    private ProgressBar loadingProgressBar;
    private String selectedDate = "", selectedTime = "";
    private final int TOTAL_TABLES = 16; // Updated to 16 tables
    private DatabaseReference reservationsRef, usersRef;
    private FirebaseAuth auth;

    // Array to store the maximum guest capacity for each table
    private final int[] tableGuestCapacities = new int[TOTAL_TABLES + 1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_reservation);

        tableContainer = findViewById(R.id.tableContainer);
        noTablesText = findViewById(R.id.noTablesText);
        guestCountEditText = findViewById(R.id.guestCountEditText);
        selectDateButton = findViewById(R.id.selectDateButton);
        selectTimeButton = findViewById(R.id.selectTimeButton);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        ImageButton backButton = findViewById(R.id.backButton);

        auth = FirebaseAuth.getInstance();
        reservationsRef = FirebaseDatabase.getInstance().getReference("manager_reservations");
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Initialize table guest capacities
        // Tables 1-5: 2 guests
        for (int i = 1; i <= 5; i++) {
            tableGuestCapacities[i] = 2;
        }
        // Tables 6-9: 6 guests
        for (int i = 6; i <= 9; i++) {
            tableGuestCapacities[i] = 6;
        }
        // Tables 10-11: 8 guests
        for (int i = 10; i <= 11; i++) {
            tableGuestCapacities[i] = 8;
        }
        // Tables 12-15: 1 guest (single sitters)
        for (int i = 12; i <= 15; i++) {
            tableGuestCapacities[i] = 1;
        }
        // Table 16: 4 guests
        tableGuestCapacities[16] = 4;

        selectDateButton.setOnClickListener(v -> showDatePicker());
        selectTimeButton.setOnClickListener(v -> showTimePicker());
        backButton.setOnClickListener(v -> finish());

        loadAvailableTables();
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, y, m, d) -> {
            selectedDate = d + "/" + (m + 1) + "/" + y;
            selectDateButton.setText("Date: " + selectedDate);
            selectDateButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FFEB3B")));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(this, (view, hour, minute) -> {
            selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
            selectTimeButton.setText("Time: " + selectedTime);
            selectTimeButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FFEB3B")));
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void loadAvailableTables() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        tableContainer.setVisibility(View.GONE);
        noTablesText.setVisibility(View.GONE);

        reservationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                tableContainer.removeAllViews();
                boolean[] reserved = new boolean[TOTAL_TABLES + 1];

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Long table = snap.child("tableNumber").getValue(Long.class);
                    String status = snap.child("status").getValue(String.class);

                    if (table != null && table <= TOTAL_TABLES &&
                            status != null && (status.equals("Pending") || status.equals("Accepted"))) {
                        reserved[table.intValue()] = true;
                    }
                }

                boolean available = false;
                for (int i = 1; i <= TOTAL_TABLES; i++) {
                    if (!reserved[i]) {
                        addTableCard(i);
                        available = true;
                    }
                }

                loadingProgressBar.setVisibility(View.GONE);
                tableContainer.setVisibility(available ? View.VISIBLE : View.GONE);
                noTablesText.setVisibility(available ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                loadingProgressBar.setVisibility(View.GONE);
                Toast.makeText(TableReservationActivity.this, "Error loading tables: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addTableCard(int tableNumber) {
        View card = getLayoutInflater().inflate(R.layout.table_card, null);
        TextView tableTitle = card.findViewById(R.id.tableTitle);
        TextView tableDetails = card.findViewById(R.id.tableDetails);
        Button reserveButton = card.findViewById(R.id.reserveButton);

        // Determine if the table is upstairs or downstairs
        boolean isUpstairs = tableNumber >= 9; // Tables 9-16 are upstairs
        String location = isUpstairs ? "Upstairs" : "Downstairs";

        // Assign different views based on table number
        String tableView;
        if (tableNumber <= 5) { // Tables 1-5 (2 guests, downstairs)
            switch (tableNumber) {
                case 1:
                    tableView = "Window View";
                    break;
                case 2:
                    tableView = "Corner View";
                    break;
                case 3:
                    tableView = "Garden View";
                    break;
                case 4:
                    tableView = "Fireplace View";
                    break;
                default:
                    tableView = "Main Hall View";
                    break;
            }
        } else if (tableNumber <= 9) { // Tables 6-9 (6 guests, downstairs)
            switch (tableNumber) {
                case 6:
                    tableView = "Window View";
                    break;
                case 7:
                    tableView = "Corner View";
                    break;
                case 8:
                    tableView = "Garden View";
                    break;
                default:
                    tableView = "Main Hall View";
                    break;
            }
        } else if (tableNumber <= 11) { // Tables 10-11 (8 guests, downstairs)
            tableView = tableNumber == 10 ? "Window View" : "Garden View";
        } else if (tableNumber <= 15) { // Tables 12-15 (1 guest, upstairs)
            switch (tableNumber) {
                case 12:
                    tableView = "Balcony View";
                    break;
                case 13:
                    tableView = "Cityscape View";
                    break;
                case 14:
                    tableView = "Skyline View";
                    break;
                default:
                    tableView = "Terrace View";
                    break;
            }
        } else { // Table 16 (4 guests, upstairs)
            tableView = "Balcony View";
        }

        int maxGuests = tableGuestCapacities[tableNumber];

        tableTitle.setText("Table " + tableNumber + " (" + location + ")");
        tableDetails.setText(tableView + " • Seats up to " + maxGuests + " guest" + (maxGuests == 1 ? "" : "s"));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, dpToPx(20), 0, dpToPx(20));
        card.setLayoutParams(params);

        reserveButton.setOnClickListener(v -> {
            if (selectedDate.isEmpty() || selectedTime.isEmpty() || guestCountEditText.getText().toString().isEmpty()) {
                Snackbar.make(findViewById(android.R.id.content), "Please select date, time, and guest count", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(Color.parseColor("#FF5722"))
                        .setTextColor(Color.WHITE)
                        .show();
                if (selectedDate.isEmpty()) {
                    selectDateButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FFCDD2")));
                }
                if (selectedTime.isEmpty()) {
                    selectTimeButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FFCDD2")));
                }
                if (guestCountEditText.getText().toString().isEmpty()) {
                    guestCountEditText.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FF5722")));
                }
                return;
            }

            int guests = Integer.parseInt(guestCountEditText.getText().toString());
            if (guests > maxGuests) {
                Snackbar.make(findViewById(android.R.id.content), "Table seats up to " + maxGuests + " guest" + (maxGuests == 1 ? "" : "s") + " only", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(Color.parseColor("#FF5722"))
                        .setTextColor(Color.WHITE)
                        .show();
                return;
            }

            new AlertDialog.Builder(TableReservationActivity.this)
                    .setTitle("Confirm Reservation")
                    .setMessage("Reserve Table " + tableNumber + " (" + location + ") for " + guests + " guests on " + selectedDate + " at " + selectedTime + "?")
                    .setPositiveButton("Yes", (dialog, which) -> submitReservation(tableNumber, tableView, guests))
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        tableContainer.addView(card);
    }

    private void submitReservation(int tableNumber, String view, int guests) {
        String userId = auth.getCurrentUser().getUid();
        String email = auth.getCurrentUser().getEmail();
        String reservationId = reservationsRef.push().getKey();

        // Fetch the user's name from the users node
        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String userName = snapshot.child("name").getValue(String.class);
                if (userName == null) {
                    userName = "Unknown User";
                }

                Map<String, Object> data = new HashMap<>();
                data.put("reservation_id", reservationId);
                data.put("user_id", userId);
                data.put("user_email", email);
                data.put("userName", userName); // Add userName to match ManagerDashboardActivity
                data.put("reservation_date", selectedDate);
                data.put("reservation_time", selectedTime);
                data.put("tableNumber", tableNumber);
                data.put("view", view);
                data.put("number_of_guests", guests);
                data.put("status", "Pending");
                data.put("date", selectedDate); // Add date field to match ManagerDashboardActivity
                data.put("time", selectedTime); // Add time field to match ManagerDashboardActivity

                assert reservationId != null;
                reservationsRef.child(reservationId).setValue(data)
                        .addOnSuccessListener(unused -> {
                            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Reservation Submitted ✅", Snackbar.LENGTH_LONG);
                            View snackbarView = snackbar.getView();
                            snackbarView.setBackgroundColor(Color.parseColor("#FF5722"));
                            TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                            textView.setTextColor(Color.WHITE);
                            textView.setTextSize(16);
                            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            snackbar.show();

                            new Handler().postDelayed(() -> {
                                Intent intent = new Intent(TableReservationActivity.this, ThankYouActivity.class);
                                startActivity(intent);
                                finish();
                            }, 1500);
                        })
                        .addOnFailureListener(e -> {
                            Snackbar.make(findViewById(android.R.id.content), "Failed: " + e.getMessage(), Snackbar.LENGTH_LONG)
                                    .setBackgroundTint(Color.parseColor("#FF5722"))
                                    .setTextColor(Color.WHITE)
                                    .show();
                        });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Snackbar.make(findViewById(android.R.id.content), "Failed to fetch user data: " + error.getMessage(), Snackbar.LENGTH_LONG)
                        .setBackgroundTint(Color.parseColor("#FF5722"))
                        .setTextColor(Color.WHITE)
                        .show();
            }
        });
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}