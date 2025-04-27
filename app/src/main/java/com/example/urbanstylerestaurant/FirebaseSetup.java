package com.example.urbanstylerestaurant;

import android.util.Log;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class FirebaseSetup {

    private static final String TAG = "FirebaseSetup";

    public static void populateMenuItems() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("menuItems").child("breakfast");

        // Clear existing data to avoid duplicates
        dbRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Cleared existing menu items successfully");
            } else {
                Log.e(TAG, "Failed to clear existing menu items: " + task.getException());
            }

            // List of menu items
            Object[][] menuItems = {
                    {"Pancakes", "Fluffy pancakes served with syrup and butter", 55.00, true, "breakfast1"},
                    {"Omelette", "Three-egg omelette with cheese and veggies", 45.00, true, "breakfast2"},
                    {"Toast & Eggs", "Toasted bread with sunny-side-up eggs", 40.00, true, "breakfast1"},
                    {"Fruit Bowl", "Fresh seasonal fruits in a bowl", 35.00, true, "breakfast2"},
                    {"Yogurt Parfait", "Layered yogurt with granola and berries", 30.00, true, "breakfast1"},
                    {"Waffles", "Crispy waffles with whipped cream", 50.00, true, "breakfast2"},
                    {"Scrambled Eggs", "Fluffy scrambled eggs with herbs", 38.00, true, "breakfast1"},
                    {"Muffins", "Freshly baked blueberry muffins", 28.00, true, "breakfast2"},
                    {"Bacon & Eggs", "Crispy bacon with two fried eggs", 60.00, true, "breakfast1"},
                    {"Croissant", "Buttery croissant, freshly baked", 32.00, true, "breakfast2"},
                    {"Smoothie", "Mixed berry smoothie with yogurt", 42.00, true, "breakfast1"},
                    {"Granola Bowl", "Granola with milk and fresh fruits", 36.00, true, "breakfast2"},
                    {"Scones", "Warm scones with jam and cream", 33.00, true, "breakfast1"},
                    {"Cheese Sandwich", "Grilled cheese sandwich on sourdough", 48.00, true, "breakfast2"},
                    {"Classic English", "Full English breakfast with eggs and bacon", 65.00, true, "breakfast1"}
            };

            // Add each menu item to Firebase Realtime Database
            for (Object[] item : menuItems) {
                DatabaseReference newItemRef = dbRef.push(); // Generate a unique key
                Map<String, Object> menuItem = new HashMap<>();
                menuItem.put("name", item[0]);
                menuItem.put("description", item[1]);
                menuItem.put("price", item[2]);
                menuItem.put("availability", item[3]);
                menuItem.put("imageRes", item[4]);

                newItemRef.setValue(menuItem).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Log.d(TAG, "Successfully added menu item: " + item[0]);
                    } else {
                        Log.e(TAG, "Error adding menu item " + item[0] + ": " + task1.getException());
                    }
                });
            }

            // Verify the data was written
            dbRef.get().addOnCompleteListener(task1 -> {
                if (task1.isSuccessful() && task1.getResult().exists()) {
                    Log.d(TAG, "Database verification: " + task1.getResult().getChildrenCount() + " items found in menuItems/breakfast");
                } else {
                    Log.e(TAG, "Database verification failed: No items found or error occurred");
                }
            });
        });
    }
}