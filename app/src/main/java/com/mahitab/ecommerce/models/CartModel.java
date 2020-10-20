package com.mahitab.ecommerce.models;
/*
 * Copyright © 2018-present, MNK Group. All rights reserved.
 */


import com.mahitab.ecommerce.managers.DataManagerHelper;
import com.shopify.graphql.support.ID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartModel {

    private HashMap<String, ArrayList<String>> mVariants = null;


    public CartModel() {
        mVariants = new HashMap<String, ArrayList<String>>();
    }

    public synchronized ArrayList<String> getVariantsForProduct(String productID) {
        return mVariants.get(productID);
    }

    public synchronized ArrayList<CartItemWrapper> getCartProducts() {
        ArrayList<CartItemWrapper> result = new ArrayList<CartItemWrapper>();
        for (Map.Entry<String, ArrayList<String>> entry : mVariants.entrySet()) {
            ProductModel product = DataManagerHelper.getInstance().getProductByID(entry.getKey());
            for (String variantId : mVariants.get(product.getID().toString())) {
                ProductModel.ProductVariantModel variant = DataManagerHelper.getInstance().getVariantByID(variantId);
                if (variant != null) {
                    CartItemWrapper newItem = new CartItemWrapper(variant);
                    result.add(newItem);
                }
            }
        }

        return result;
    }

    /**
     * Only the IDs of the products and product variants will be stored inside the CartModel as it is all the
     * required data in order to correctly track down a Product Variant.
     * <p>
     * //     * @param variantItem
     */
    public synchronized void remove(ID variantID) {
        ProductModel.ProductVariantModel variant = DataManagerHelper.getInstance().getVariantByID(variantID.toString());

        mVariants.get(variant.getProductID()).remove(variant.getID().toString());
        if (mVariants.get(variant.getProductID()).size() == 0) {
            mVariants.remove(variant.getProductID());
        }

    }

    public synchronized void add(CartItemQuantity variantItem) {
        ProductModel.ProductVariantModel variant = DataManagerHelper.getInstance().getVariantByID(variantItem.getProductID());
        ProductModel product = DataManagerHelper.getInstance().getProductByID(variant.getProductID());

        if (mVariants.containsKey(product.getID().toString())) {
            mVariants.get(product.getID().toString()).add(variantItem.getProductID());
        } else {
            ArrayList<String> prodEntries = new ArrayList<String>();
            prodEntries.add(variantItem.getProductID());
            mVariants.put(product.getID().toString(), prodEntries);
        }
    }


    public class CartItemWrapper {
        private ProductModel.ProductVariantModel mVariant = null;
       /* CartItemQuantity cartItemQuantity=null;

        public CartItemWrapper(CartItemQuantity cartItemQuantity) {
            this.cartItemQuantity = cartItemQuantity;
        }

        public CartItemQuantity getCartItemQuantity() {
            return cartItemQuantity;
        }

        public void setCartItemQuantity(CartItemQuantity cartItemQuantity) {
            this.cartItemQuantity = cartItemQuantity;
        }*/

        CartItemWrapper(ProductModel.ProductVariantModel variant) {
            mVariant = variant;
        }

        public ProductModel.ProductVariantModel getProduct() {
            return mVariant;
        }
    }
}

