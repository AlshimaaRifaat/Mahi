package com.mahitab.ecommerce.models;

public class ProductReviewModel {
    private String firstName;
    private String lastName;
    private String message;
    private float rating;

    public ProductReviewModel() {
    }

    public ProductReviewModel(String firstName, String lastName, String message, float rating) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.message = message;
        this.rating = rating;
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
}
