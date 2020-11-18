package com.mahitab.ecommerce.models;

import com.google.firebase.database.Exclude;

import java.util.List;

public class Collection {
    private String id;
    @Exclude
    private String encodedId;
    private String image;
    @Exclude
    private List<BannerModel> banners;

    public Collection() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Exclude
    public String getEncodedId() {
        return encodedId;
    }

    public void setEncodedId(String encodedId) {
        this.encodedId = encodedId;
    }

    public String getImage() {
        return image;
    }

    @Exclude
    public List<BannerModel> getBanners() {
        return banners;
    }

    public void setBanners(List<BannerModel> banners) {
        this.banners = banners;
    }
}
