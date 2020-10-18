package com.mahitab.ecommerce.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.ID;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class CollectionModel implements Parcelable {
    private ID mID;
    private String mTitle;
    private String mImage;
    private DateTime mUpdatedAt;
    private ArrayList<ProductModel> mPreviewProducts = new ArrayList<>();

    private CollectionModel(ID id, String title, String image, DateTime updatedAt) {
        this.mID = id;
        this.mTitle = title;
        this.mImage = image;
        this.mUpdatedAt = updatedAt;
        this.mPreviewProducts = new ArrayList<>();
    }

    public CollectionModel(Storefront.CollectionEdge collectionEdge) {
        Storefront.Collection node = collectionEdge.getNode();
        mID = node.getId();
        mTitle = node.getTitle();
        if (null != node.getImage())
            mImage = node.getImage().getSrc();
        mUpdatedAt = node.getUpdatedAt();

        Storefront.ProductConnection products = node.getProducts();
        for (Storefront.ProductEdge edge : products.getEdges()) {
            ProductModel newProduct = new ProductModel(edge, mID.toString());

            mPreviewProducts.add(newProduct);
        }
    }

    protected CollectionModel(Parcel in) {
        mTitle = in.readString();
        mImage = in.readString();
        mPreviewProducts = in.createTypedArrayList(ProductModel.CREATOR);
    }

    public static final Creator<CollectionModel> CREATOR = new Creator<CollectionModel>() {
        @Override
        public CollectionModel createFromParcel(Parcel in) {
            return new CollectionModel(in);
        }

        @Override
        public CollectionModel[] newArray(int size) {
            return new CollectionModel[size];
        }
    };

    public ID getID() {
        return mID;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String mImage) {
        this.mImage = mImage;
    }

    public DateTime getUpdatedAt() {
        return mUpdatedAt;
    }

    public ArrayList<ProductModel> getPreviewProducts() {
        return mPreviewProducts;
    }

    private void setPreview(ArrayList<ProductModel> products) {
        this.mPreviewProducts = products;
    }

    public static CollectionModel buildCollection(CollectionModel collectionModel, ArrayList<ProductModel> collectionProducts) {
        CollectionModel newCollection = new CollectionModel(collectionModel.getID(), collectionModel.getTitle(), collectionModel.getImage(), collectionModel.getUpdatedAt());
        newCollection.setPreview(collectionProducts);

        return newCollection;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mImage);
        dest.writeTypedList(mPreviewProducts);
    }
}
