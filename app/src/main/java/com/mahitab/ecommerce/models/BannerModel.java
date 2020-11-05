package com.mahitab.ecommerce.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DatabaseReference;

public class BannerModel implements Parcelable {
    private DatabaseReference reference;
    private String id;
    private String type;
    private String image;
    private int numberOfClicks;

    public BannerModel() {
    }

    protected BannerModel(Parcel in) {
        id = in.readString();
        type = in.readString();
        image = in.readString();
        numberOfClicks = in.readInt();
    }

    public static final Creator<BannerModel> CREATOR = new Creator<BannerModel>() {
        @Override
        public BannerModel createFromParcel(Parcel in) {
            return new BannerModel(in);
        }

        @Override
        public BannerModel[] newArray(int size) {
            return new BannerModel[size];
        }
    };

    public DatabaseReference getReference() {
        return reference;
    }

    public void setReference(DatabaseReference reference) {
        this.reference = reference;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getImage() {
        return image;
    }

    public int getNumberOfClicks() {
        return numberOfClicks;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(type);
        dest.writeString(image);
        dest.writeInt(numberOfClicks);
    }
}

