package com.mahitab.ecommerce.models;

import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.ID;

import org.joda.time.DateTime;

public class MyOrdersModel {
    private Integer orderNumber;
    private String totalPrice;
    private String statutsUrl;
    private ID mID;
    private DateTime processedAt;


    public MyOrdersModel(Storefront.OrderEdge edge) {
        Storefront.Order order = edge.getNode();

        orderNumber = order.getOrderNumber();
        totalPrice= order.getTotalPrice().toString();
        mID=order.getId();
        statutsUrl=order.getStatusUrl();
        processedAt=order.getProcessedAt();
    }

    public MyOrdersModel(Integer orderNumber, String totalPrice, String statutsUrl, ID mID, DateTime dateTime) {
        this.orderNumber = orderNumber;
        this.totalPrice = totalPrice;
        this.statutsUrl = statutsUrl;
        this.mID = mID;
        this.processedAt = dateTime;
    }

    public DateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(DateTime processedAt) {
        this.processedAt = processedAt;
    }

    public String getStatutsUrl() {
        return statutsUrl;
    }

    public void setStatutsUrl(String statutsUrl) {
        this.statutsUrl = statutsUrl;
    }

    public ID getmID() {
        return mID;
    }

    public void setmID(ID mID) {
        this.mID = mID;
    }



    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
}
