package com.example.urbanstylerestaurant;

public class User {
    public String name, surname, email, role;

    public User() {
        // Empty constructor needed for Firebase
    }

    public User(String name, String surname, String email, String role) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.role = role;
    }
}

