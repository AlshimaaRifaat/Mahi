package com.mahitab.ecommerce.models;

import com.shopify.graphql.support.ID;

public class CartItemQuantity {
    public int quantity;
    public double productPrice;
    public String productID;
    public ID id = null;

    public CartItemQuantity(int quantity, String productID,double productPrice, ID id) {
        this.id = id;
        this.quantity = quantity;
        this.productID = productID;
        this.productPrice = productPrice;
    }

    public ID getId() {
        return id;
    }
    public void setId(ID id) {
        this.id = id;
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
