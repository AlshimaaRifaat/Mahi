package com.mahitab.ecommerce.models;

import com.shopify.graphql.support.ID;

public class CartItemQuantity {
    public int quantity;
    public double productPrice;
    public ID id = null;
    public String productID;

    public CartItemQuantity(int quantity, ID id, double productPrice) {
        this.quantity=quantity;
        this.id=id;
        this.productPrice=productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
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
