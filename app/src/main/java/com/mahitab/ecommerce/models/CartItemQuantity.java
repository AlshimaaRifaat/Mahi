package com.mahitab.ecommerce.models;

import com.shopify.graphql.support.ID;

public class CartItemQuantity {
    public ID id = null;
    public int quantity;
    public String productID;


    public CartItemQuantity(ID id, int quantity, String productID) {
        this.id = id;
        this.quantity = quantity;
        this.productID = productID;
    }

    public CartItemQuantity(ID id) {
        this.id = id;
    }

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    public void setproductID(String productID) {
        this.productID = productID;
    }

    public String getproductID() {
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
