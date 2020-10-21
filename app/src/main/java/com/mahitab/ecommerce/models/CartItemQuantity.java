package com.mahitab.ecommerce.models;

public class CartItemQuantity {
    public int quantity;
    public double productPrice;
    public String productID;

    public CartItemQuantity(int quantity, String productID,double productPrice) {
        this.quantity = quantity;
        this.productID = productID;
        this.productPrice = productPrice;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductID() {
        return productID;
    }

    public void plusQuantity() {
        quantity = quantity + 1;
    }

    public void minQuantity() {
        if (quantity >= 1) {
            quantity = quantity - 1;
        }
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getProductPrice() {
        return productPrice;
    }
}
