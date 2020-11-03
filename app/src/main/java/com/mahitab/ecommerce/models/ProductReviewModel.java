package com.mahitab.ecommerce.models;

public class ProductReviewModel {
    private String productId;
    private String firstName;
    private String lastName;
    private String message;
    private float rating;
    private boolean accepted;

    public ProductReviewModel() {
    }

    public ProductReviewModel(String productId, String firstName, String lastName, String message, float rating) {
        this.productId = productId;
        this.firstName = firstName;
        this.lastName = lastName;
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
