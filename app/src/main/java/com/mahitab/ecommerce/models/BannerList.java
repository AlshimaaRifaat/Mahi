package com.mahitab.ecommerce.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BannerList{
    @SerializedName("banners")
    private List<BannerModel> banners = null;

    public BannerList() {
    }

    public List<BannerModel> getBanners() {
        return banners;
    }

    public void setBanners(List<BannerModel> banners) {
        this.banners = banners;
    }
}
