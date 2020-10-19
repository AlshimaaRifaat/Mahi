package com.mahitab.ecommerce.models;

public class ShapeModel {
    private String id;
    private String image;
    private String title;

    public ShapeModel() {
    }

    public ShapeModel(String id, String image, String title) {
        this.id = id;
        this.image = image;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }
}
