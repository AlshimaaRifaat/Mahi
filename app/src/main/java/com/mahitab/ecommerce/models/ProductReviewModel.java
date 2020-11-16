package com.mahitab.ecommerce.models;

import com.google.firebase.database.Exclude;

public class ProductReviewModel {
    private String productId;
    private String firstName;
    private String lastName;
    @Exclude
    private String email;
    private String title;
    private String message;
    private float rating;
    private boolean accepted;

    public ProductReviewModel() {
    }

    public ProductReviewModel(String productId, String firstName, String lastName, String email, String title, String message, float rating) {
        this.productId = productId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.title = title;
        this.message = message;
        this.rating = rating;
    }

    public String getProductId() {
        return productId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Exclude
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public float getRating() {
        return rating;
    }

    public boolean isAccepted() {
        return accepted;
    }
}
