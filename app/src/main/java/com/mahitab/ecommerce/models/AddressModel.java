package com.mahitab.ecommerce.models;

import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.ID;

public class AddressModel {

    private String address1;
    private String address2;
    private String city;
    private String zipCode;
    private String province;
    private String phone;
    private ID mID;


    public AddressModel(Storefront.MailingAddressEdge edge) {
        Storefront.MailingAddress mailingAddress = edge.getNode();

        address1 = mailingAddress.getAddress1();
        address2= mailingAddress.getAddress2();
        mID=mailingAddress.getId();
        city=mailingAddress.getCity();
        zipCode=mailingAddress.getZip();
        province=mailingAddress.getProvince();
        phone=mailingAddress.getPhone();
    }

    public AddressModel(String address1, String address2, String city, String zipCode, String province, String phone, ID mID) {
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.zipCode = zipCode;
        this.province = province;
        this.phone = phone;
        this.mID = mID;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getCity() {
        return city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getProvince() {
        return province;
    }

    public String getPhone() {
        return phone;
    }

    public ID getmID() {
        return mID;
    }
}
