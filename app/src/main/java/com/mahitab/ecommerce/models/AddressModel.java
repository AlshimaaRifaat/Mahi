package com.mahitab.ecommerce.models;

import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.ID;

import java.io.Serializable;

public class AddressModel implements Serializable {

    private String address1;
    private String address2;
    private String city;
    private String zipCode;
    private String province;
    private String phone;
    private String firstName;
    private String lastName;
    private String country;
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
        firstName=mailingAddress.getFirstName();
        lastName=mailingAddress.getLastName();
        country=mailingAddress.getCountry();
    }

    public AddressModel(String address1, String address2, String city, String zipCode, String province, String phone, String firstName, String lastName, String country, ID mID) {
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.zipCode = zipCode;
        this.province = province;
        this.phone = phone;
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.mID = mID;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public ID getmID() {
        return mID;
    }

    public void setmID(ID mID) {
        this.mID = mID;
    }
}
