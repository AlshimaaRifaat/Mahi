package com.mahitab.ecommerce.models;

import android.os.Parcel;
import android.os.Parcelable;

public class CategoryModel implements Parcelable {
    private String id;
    private String image;
    private boolean hasColor;
    private boolean hasShape;

    public CategoryModel() {
    }

    protected CategoryModel(Parcel in) {
        id = in.readString();
        image = in.readString();
        hasColor = in.readByte() != 0;
        hasShape = in.readByte() != 0;
    }

    public static final Creator<CategoryModel> CREATOR = new Creator<CategoryModel>() {
        @Override
        public CategoryModel createFromParcel(Parcel in) {
            return new CategoryModel(in);
        }

        @Override
        public CategoryModel[] newArray(int size) {
            return new CategoryModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public boolean isHasColor() {
        return hasColor;
    }

    public boolean isHasShape() {
        return hasShape;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(image);
        dest.writeByte((byte) (hasColor ? 1 : 0));
        dest.writeByte((byte) (hasShape ? 1 : 0));
    }
}
