package com.mahitab.ecommerce.models;

public class CartItemQuantity {
    public int quantity;
    public String productID;

    public CartItemQuantity(int quantity, String productID) {
        this.quantity = quantity;
        this.productID = productID;
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

}
