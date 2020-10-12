package com.mahitab.ecommerce.models;

public class ImageSliderModel {
    private String title;
    private String image;

    public ImageSliderModel() {
    }

    public ImageSliderModel(String title, String image) {
        this.title = title;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
