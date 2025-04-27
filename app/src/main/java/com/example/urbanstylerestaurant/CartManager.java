package com.example.urbanstylerestaurant;

import android.util.Log;
import java.util.ArrayList;

public class CartManager {
    private static final ArrayList<String> cartItems = new ArrayList<>();
    private static double totalPrice = 0;

    public static void addItem(String itemName, double price) {
        String item = itemName + " - R" + price;
        cartItems.add(item);
        totalPrice += price;
        Log.d("CartManager", "Added item: " + item + ", New total: R" + totalPrice);
    }

    public static ArrayList<String> getItems() {
        return cartItems;
    }

    public static void clearCart() {
        cartItems.clear();
        totalPrice = 0;
        Log.d("CartManager", "Cart cleared, Total: R" + totalPrice);
    }

    public static double getTotalPrice() {
        return totalPrice;
    }
}