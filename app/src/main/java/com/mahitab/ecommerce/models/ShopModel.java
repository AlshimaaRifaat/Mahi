package com.mahitab.ecommerce.models;
/*
 * Copyright © 2018-present, MNK Group. All rights reserved.
 */


import com.shopify.buy3.Storefront;

public class ShopModel {

    private String mName;
    private String mDescription;
    private Storefront.CurrencyCode mCurrencyCode;
    private Storefront.CountryCode mCountryCode;
    private String mPrivacyPolicyUrl;
    private String mTermsOfServiceUrl;

    public ShopModel(Storefront.Shop shop) {
        this.mName = shop.getName();
        this.mDescription = shop.getDescription();
        Storefront.PaymentSettings payment = shop.getPaymentSettings();

        if (payment != null) {
            this.mCurrencyCode = shop.getPaymentSettings().getCurrencyCode();
            this.mCountryCode = shop.getPaymentSettings().getCountryCode();
        }

        // this.mPrivacyPolicyUrl = shop.getPrivacyPolicy().getUrl();
        // this.mTermsOfServiceUrl = shop.getTermsOfService().getUrl();
    }

    public String getName() {
        return mName.toLowerCase();
    }

    public String getDescription() {
        return mDescription;
    }

    public Storefront.CurrencyCode getCurrencyCode() {
        return mCurrencyCode;
    }

    public Storefront.CountryCode getCountryCode() {
        return mCountryCode;
    }

    public String getPrivacyPolicyUrl() {
        return mPrivacyPolicyUrl;
    }

    public String getTermsOfServiceUrl() {
        return mTermsOfServiceUrl;
    }
}
