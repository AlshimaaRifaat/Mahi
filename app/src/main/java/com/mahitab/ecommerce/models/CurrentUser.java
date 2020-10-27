package com.mahitab.ecommerce.models;
/*
 * Copyright © 2018-present, MNK Group. All rights reserved.
 */


import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.ID;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;

public final class CurrentUser implements Serializable {

    private static final CurrentUser mInstance = new CurrentUser();

    private ID id = null;
    private String firstName = "";
    private String lastName = "";
    private String displayName = "";
    private String email = "";
    private String password = "";
    private String phone = "";
    private boolean acceptsMarketing = true;

    private Storefront.MailingAddress mDefaultAddress = null;
    private ArrayList<Storefront.MailingAddress> mAddresses = null;

    private String accessToken = "";
    private DateTime expiresAt = null;

    public static synchronized CurrentUser getInstance() {
        return mInstance;
    }

    public static synchronized CurrentUser getInstance(Storefront.CustomerAccessToken token) {
        mInstance.accessToken = token.getAccessToken();
        mInstance.expiresAt = token.getExpiresAt();

        return mInstance;
    }

    public static synchronized CurrentUser getInstance(Storefront.Customer customer) {
        mInstance.reset();

        mInstance.setID(customer.getId());
        mInstance.setFirstName(customer.getFirstName());
        mInstance.setLastName(customer.getLastName());
        mInstance.setDisplayName(customer.getDisplayName());
        mInstance.setPhone(customer.getPhone());
        mInstance.setEmail(customer.getEmail());
        mInstance.setAcceptsMarketing(customer.getAcceptsMarketing());

        mInstance.setDefaultAddress(customer.getDefaultAddress());
//            mInstance.addAddress(customer.getDefaultAddress(), true);
        if (customer.getAddresses() != null) {
            ArrayList<Storefront.MailingAddressEdge> edges = (ArrayList<Storefront.MailingAddressEdge>) customer.getAddresses().getEdges();
            for (Storefront.MailingAddressEdge e : edges) {
                Storefront.MailingAddress node = e.getNode();
                mInstance.addAddress(node);
            }
        }
        return mInstance;
    }

    private synchronized void reset() {
        this.setID(null);
        this.setFirstName(null);
        this.setLastName(null);
        this.setDisplayName(null);
        this.setPhone(null);
        this.setEmail(null);
        this.setAcceptsMarketing(false);
        this.mAddresses = null;
        this.mDefaultAddress = null;
    }

    private CurrentUser() {
    }

    public CurrentUser(String firstName, String lastName, String email, String password, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    public synchronized ID getId() {
        return id;
    }

    public synchronized String getFirstName() {
        return firstName;
    }

    public synchronized String getLastName() {
        return lastName;
    }

    public synchronized String getDisplayName() {
        return displayName;
    }

    public synchronized String getEmail() {
        return email;
    }

    public synchronized String getPassword() {
        return password;
    }

    public synchronized String getPhone() {
        return phone;
    }

    public synchronized boolean isAcceptsMarketing() {
        return acceptsMarketing;
    }

    private synchronized void setID(ID id) {
        this.id = id;
    }

    private synchronized void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    private synchronized void setLastName(String lastName) {
        this.lastName = lastName;
    }

    private synchronized void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public synchronized void setEmail(String email) {
        this.email = email;
    }

    private synchronized void setPassword(String password) {
        this.password = password;
    }

    private synchronized void setPhone(String phone) {
        this.phone = phone;
    }

    private synchronized void setAcceptsMarketing(boolean acceptsMarketing) {
        this.acceptsMarketing = acceptsMarketing;
    }

    public synchronized Storefront.MailingAddress getAddress() {
        return mDefaultAddress;
    }

    public synchronized Storefront.MailingAddress getAddress(ID addressId) {
        if (ID.equals(addressId, mDefaultAddress.getId())) {
            return mDefaultAddress;
        }

        for (Storefront.MailingAddress address : mAddresses) {
            if (ID.equals(address.getId(), addressId)) {
                return address;
            }
        }

        return null;
    }

    public synchronized void setAddresses(Storefront.MailingAddressConnection connection) {
        mAddresses = new ArrayList<Storefront.MailingAddress>();
        for (Storefront.MailingAddressEdge edge : connection.getEdges()) {
            mAddresses.add(edge.getNode());
        }
    }

    public synchronized ArrayList<Storefront.MailingAddress> getShippingAddresses() {
        return mAddresses;
    }

    public synchronized void addAddress(Storefront.MailingAddress address) {
        if (mAddresses == null) {
            mAddresses = new ArrayList<Storefront.MailingAddress>();
        }

        for (Storefront.MailingAddress a : mAddresses) {
            if (a.getId().toString().contentEquals(address.getId().toString())) {
                mAddresses.set(mAddresses.indexOf(a), address);
                return;
            }
        }

        mAddresses.add(address);
    }

    //  region Session Data

    public synchronized String getAccessToken() {
        return accessToken;
    }

    public synchronized void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public synchronized DateTime getExpiresAt() {
        return expiresAt;
    }

    public synchronized void setExpiresAt(DateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    //  endregion

    @Override
    public String toString() {
        return "ID: " + this.id + '\n' +
                "Email: " + this.email + '\n' +
                "FName: " + this.firstName + '\n' +
                "LName: " + this.lastName + '\n' +
                "Phone: " + this.phone + '\n' +
                "Pass: " + "********" + '\n' +
                "Token: " + this.accessToken + '\n' +
                "ExpiresAt: " + this.expiresAt.toString() + '\n' +
                "AcceptsMarketing: " + this.acceptsMarketing + '\n';
    }

    public synchronized void setDefaultAddress(Storefront.MailingAddress defaultAddress) {
        this.firstName = defaultAddress != null ? defaultAddress.getFirstName() : "";
        this.lastName = defaultAddress != null ? defaultAddress.getLastName() : "";
        this.phone = defaultAddress != null ? defaultAddress.getPhone() : "";
        mDefaultAddress = defaultAddress;
    }

    public synchronized void updateAddress(String addressId, Storefront.MailingAddress address) {
        if (ID.equals(new ID(addressId), mDefaultAddress.getId())) {
            mDefaultAddress = address;
            return;
        }

        int index = -1;
        for (int i = 0; i < mAddresses.size(); i++) {
            if (ID.equals(new ID(addressId), mAddresses.get(i).getId())) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            mAddresses.set(index, address);
        }
    }

    public void logout() {
        this.setID(null);
        this.setFirstName(null);
        this.setLastName(null);
        this.setDisplayName(null);
        this.setPhone(null);
        this.setEmail(null);
        this.setAcceptsMarketing(false);
        this.mAddresses = null;
        this.mDefaultAddress = null;
        this.accessToken = "";
        this.expiresAt = null;
    }
}
