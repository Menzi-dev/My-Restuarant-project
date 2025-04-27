package com.example.urbanstylerestaurant;

public class MenuItem {
    private String itemId;
    private String name;
    private double price;
    private String imageResName;
    private String description;
    private boolean availability;

    // Constructor for the new structure
    public MenuItem(String name, double price) {
        this.name = name;
        this.price = price;
        this.availability = true;
    }

    // Constructor matching your existing adapter requirements
    public MenuItem(String itemId, String name, double price, String imageResName, String description, boolean availability) {
        this.itemId = itemId;
        this.name = name;
        this.price = price;
        this.imageResName = imageResName;
        this.description = description;
        this.availability = availability;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageResName() {
        return imageResName;
    }

    public void setImageResName(String imageResName) {
        this.imageResName = imageResName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAvailable() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }
}