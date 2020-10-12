package com.mahitab.ecommerce.models;
/*
 * Copyright © 2018-present, MNK Group. All rights reserved.
 */


import android.util.Log;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


/**
 * Created by Ripan Andra on 3/23/2018.
 */

public class SelectedOptions extends Observable {

    private String size = "";
    private String color = "";
    private String material = "";
    private double lowerPrice = 0.0;
    private double higherPrice = 0.0;
    private String searchCriteria = "";

    private ArrayList<Observer> mObservers = new ArrayList<>();


    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
        this.notifyObservers();
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        this.notifyObservers();
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
        this.notifyObservers();
    }

    public double getLowerPrice() {
        return lowerPrice;
    }

    public void setLowerPrice(double lowerPrice) {
        this.lowerPrice = lowerPrice;
    }

    public double getHigherPrice() {
        return higherPrice;
    }

    public void setHigherPrice(double higherPrice) {
        this.higherPrice = higherPrice;
    }

    @Override
    public synchronized void addObserver(Observer o) {
        if (!mObservers.contains(o)) {
            mObservers.add(o);
        }
        super.addObserver(o);
    }

    @Override
    public synchronized void deleteObserver(Observer o) {
        mObservers.remove(o);
        super.deleteObserver(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer o : mObservers) {
            o.update(this, null);
        }
        super.notifyObservers();
    }

    public String getSearchCriteria() {
        return searchCriteria;
    }

    public void setSearchCriteria(String searchCriteria) {
        Log.d("mohamed", "onQueryTextChange:1 ");
        this.searchCriteria = searchCriteria;
        this.notifyObservers();
    }
}

