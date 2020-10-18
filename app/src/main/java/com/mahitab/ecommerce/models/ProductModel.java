package com.mahitab.ecommerce.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.ID;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductModel implements Parcelable {
    private ID mID;
    private String mTitle;
    private String mDescription;

    private DateTime mPublishedAt;
    private DateTime mUpdatedAt;

    private String[] mImages;
    private String[] mTags = null;

    private ArrayList<ProductVariantModel> mVariants = new ArrayList<ProductVariantModel>();
    private String mCollectionID;

    public ProductModel(Storefront.ProductEdge edge, String parentID) {
        Storefront.Product product = edge.getNode();

        mID = product.getId();
        mTitle = product.getTitle();
        mDescription = product.getDescriptionHtml();

        mPublishedAt = product.getPublishedAt();
        mUpdatedAt = product.getUpdatedAt();

        if (product.getTags() != null) {
            mTags = new String[product.getTags().size()];
            int index = 0;
            for (String tag : product.getTags()) {
                mTags[index] = tag;
                index++;
            }
        }

        if (product.getImages() != null && product.getImages().getEdges() != null) {
            mImages = new String[product.getImages().getEdges().size()];
            int index = 0;
            for (Storefront.ImageEdge imgEdge : product.getImages().getEdges()) {
                mImages[index] = imgEdge.getNode().getSrc();
                index++;
            }
        }

        List<Storefront.ProductVariantEdge> variantEdges = product.getVariants().getEdges();
        for (Storefront.ProductVariantEdge variantEdge : variantEdges) {
            ProductVariantModel variantModel = convertToVariantModel(variantEdge);
            mVariants.add(variantModel);
        }

        mCollectionID = parentID;
    }

    public ProductModel() {
    }

    protected ProductModel(Parcel in) {
        mTitle = in.readString();
        mDescription = in.readString();
        mImages = in.createStringArray();
        mTags = in.createStringArray();
        mCollectionID = in.readString();
    }

    public static final Creator<ProductModel> CREATOR = new Creator<ProductModel>() {
        @Override
        public ProductModel createFromParcel(Parcel in) {
            return new ProductModel(in);
        }

        @Override
        public ProductModel[] newArray(int size) {
            return new ProductModel[size];
        }
    };

    public ID getID() {
        return mID;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public DateTime getPublishedAt() {
        return mPublishedAt;
    }

    public DateTime getUpdatedAt() {
        return mUpdatedAt;
    }

    public String[] getImages() {
        return mImages;
    }

    public String[] getTags() {
        return mTags;
    }

    public ArrayList<ProductVariantModel> getVariants() {
        return mVariants;
    }

    public String getCollectionID() {
        return mCollectionID;
    }

    public boolean containsSelectedOption(String type, String value) {
        boolean isTrue = false;

        if (type.isEmpty()) {
            return false;
        }

        if (value.isEmpty()) {
            return true;
        }

        for (ProductVariantModel v : mVariants) {
            if (v.getSelectedOptions().containsKey(type) && v.getSelectedOptions().containsValue(value)) {
                isTrue = true;
                break;
            }
        }

        return isTrue;
    }

    public boolean between(double i, double minPrice, double maxPrice) {
        return i >= minPrice && i <= maxPrice;
    }

    public BigDecimal getPrice() {
        if (mVariants == null || mVariants.size() == 0) {
            return null;
        } else {
            return mVariants.get(0).getPrice();
        }
    }

    public ProductVariantModel getVariantByID(String variantID) {
        ProductVariantModel variant = null;
        for (ProductVariantModel v : mVariants) {
            if (ID.equals(new ID(variantID), v.getID())) {
                variant = v;
                break;
            }
        }
        return variant;
    }

    private ProductVariantModel convertToVariantModel(Storefront.ProductVariantEdge variantEdge) {
        return new ProductVariantModel(variantEdge, mID.toString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mDescription);
        dest.writeStringArray(mImages);
        dest.writeStringArray(mTags);
        dest.writeString(mCollectionID);
    }

    //region Product Variants
    public class ProductVariantModel {
        private ID mID;
        private String mTitle;
        private boolean mAvailableForSale;
        private BigDecimal mPrice;
        private BigDecimal mOldPrice;
        private String mSku;
        private Double mWeight;
        private String mWeightUnit;

        HashMap<String, String> mSelectedOptions = new HashMap<String, String>();

        private String mProductID;

        ProductVariantModel(Storefront.ProductVariantEdge edge, String productID) {
            Storefront.ProductVariant variant = edge.getNode();
            mID = variant.getId();
            mTitle = variant.getTitle();
            if (variant.getCompareAtPrice() != null && variant.getCompareAtPrice().compareTo(variant.getPrice()) > 0) {
                mOldPrice = variant.getCompareAtPrice();
                mPrice = variant.getPrice();
            } else {
                mOldPrice = null;
                mPrice = variant.getPrice();
            }

            mSku = variant.getSku();
            mWeight = variant.getWeight();
            mWeightUnit = variant.getWeightUnit().toString();
            mAvailableForSale = variant.getAvailableForSale();

            mProductID = productID;


            for (Storefront.SelectedOption option : variant.getSelectedOptions()) {
                mSelectedOptions.put(option.getName(), option.getValue());
            }
        }

        public ID getID() {
            return mID;
        }

        public String getTitle() {
            return mTitle;
        }

        public void setmTitle(String mTitle) {
            this.mTitle = mTitle;
        }

        public ProductVariantModel(String mTitle) {
            this.mTitle = mTitle;
        }

        public boolean isAvailableForSale() {
            return mAvailableForSale;
        }

        public BigDecimal getPrice() {
            return (mPrice != null) ? mPrice : mOldPrice;
        }

        public BigDecimal getOldPrice() {
            return mOldPrice;
        }

        public String getSku() {
            return mSku;
        }

        public Double getWeight() {
            return mWeight;
        }

        public String getWeightUnit() {
            return mWeightUnit;
        }

        public String getProductID() {
            return mProductID;
        }

        public HashMap<String, String> getSelectedOptions() {
            return mSelectedOptions;
        }
    }
}
