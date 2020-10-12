package com.mahitab.ecommerce.managers;
/*
 * Copyright © 2018-present, MNK Group. All rights reserved.
 */


import com.mahitab.ecommerce.models.CartItemQuantity;
import com.mahitab.ecommerce.models.CartModel;
import com.mahitab.ecommerce.models.CollectionModel;
import com.mahitab.ecommerce.models.ProductModel;
import com.mahitab.ecommerce.models.ShopModel;
import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.ID;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class DataManagerHelper {

    private static class DataManagerWrapper {
        private static final DataManagerHelper INSTANCE = new DataManagerHelper();
    }

    private DataManagerHelper() {
        mCollections = new HashMap<String, CollectionModel>();
        mProductsByCollection = new HashMap<String, ArrayList<ProductModel>>();
    }

    public static DataManagerHelper getInstance() {
        return DataManagerWrapper.INSTANCE;
    }

    private static final Object mLock = new Object();

    private ShopModel mShopModel = null;
    private CartModel mCart = new CartModel();
    private HashMap<String, CollectionModel> mCollections;
    private HashMap<String, ArrayList<ProductModel>> mProductsByCollection;

    public void setShop(Storefront.Shop shop) {
        synchronized (mLock) {
            mShopModel = new ShopModel(shop);
        }
    }

    public ShopModel getShop() {
        synchronized (mLock) {
            return mShopModel;
        }
    }

    public HashMap<String, CollectionModel> getCollections() {
        return mCollections;
    }

    public ArrayList<ProductModel> getProductsByCollectionID(String collectionID) {
        return mProductsByCollection.get(collectionID);
    }

    public void createProductsListForCollectionId(String collectionID) {
        mProductsByCollection.put(collectionID, new ArrayList<ProductModel>());
    }

    public ProductModel getProductByID(String productID) {
        ProductModel product = null;
        for (Map.Entry<String, ArrayList<ProductModel>> entry : mProductsByCollection.entrySet()) {
            for (ProductModel p : entry.getValue()) {
                if (ID.equals(p.getID(), new ID(productID))) {
                    product = p;
                    break;
                }
            }
        }

        return product;
    }

    public ArrayList<ProductModel> getAllProducts() {
        ArrayList<ProductModel> products = new ArrayList<ProductModel>();
        for (Map.Entry<String, ArrayList<ProductModel>> entry : mProductsByCollection.entrySet()) {
            for (ProductModel p : entry.getValue()) {
                products.add(p);
            }
        }
        return products;
    }

    public ProductModel.ProductVariantModel getVariantByID(String variantID) {
        for (Map.Entry<String, ArrayList<ProductModel>> entry : mProductsByCollection.entrySet()) {
            for (ProductModel product : entry.getValue()) {
                for (ProductModel.ProductVariantModel variant : product.getVariants()) {
                    if (!ID.equals(variant.getID(), new ID(variantID))) {
                        continue;
                    }

                    return variant;
                }
            }
        }
        return null;
    }

    //region Cart Management
    public void addToCart(CartItemQuantity variantItem) {
        mCart.add(new CartItemQuantity(variantItem.id, variantItem.quantity, variantItem.productID));
    }

    public void emptyCart() {
        mCart = new CartModel();
    }

    public void removeFromCart(ID variantItem) {
        mCart.remove(variantItem);
    }

    public ArrayList<CartModel.CartItemWrapper> getCartProducts() {
        return mCart.getCartProducts();
    }

    public ArrayList<ProductModel.ProductVariantModel> getVariants() {
        ArrayList<ProductModel.ProductVariantModel> variants = new ArrayList<ProductModel.ProductVariantModel>();
        for (CartModel.CartItemWrapper item : mCart.getCartProducts()) {
            variants.add(item.getProduct());
        }

        return variants;
    }

    double getProductsPrice() {
        BigDecimal total = new BigDecimal("0");

        for (CartModel.CartItemWrapper item : mCart.getCartProducts()) {
            total = total.add(
                    item.getProduct().getPrice()
            );

        }

        return Double.valueOf(total.toString());
    }

    void resetCart() {
        this.mCart = new CartModel();
    }
    //endregion
}
