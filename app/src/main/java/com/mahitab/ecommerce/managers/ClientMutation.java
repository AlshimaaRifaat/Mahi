package com.mahitab.ecommerce.managers;
/*
 * Copyright © 2018-present, MNK Group. All rights reserved.
 */


import com.mahitab.ecommerce.models.ProductModel;
import com.shopify.buy3.Storefront;
import com.shopify.buy3.pay.PaymentToken;
import com.shopify.graphql.support.ID;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

class ClientMutation {

    /**
     * Method used to create the Mutation Query for
     * authenticating the user's app session
     *
     * @param input the data required for the successful request
     * @return the actual Mutation Query
     */
    static Storefront.MutationQuery mutationForLoginUser(Storefront.CustomerAccessTokenCreateInput input) {
        return Storefront.mutation(
                rootQuery -> rootQuery.customerAccessTokenCreate(input,
                        accessTokenQuery -> accessTokenQuery
                                .customerAccessToken(
                                        tokenQuery -> tokenQuery
                                                .accessToken()
                                                .expiresAt()
                                )
                                .userErrors(
                                        errorsQuery -> errorsQuery
                                                .field()
                                                .message()
                                )
                )
        );
    }

    /**
     * Method used to create the Mutation Query for
     * the registration of a new user
     *
     * @param input the data required for the successful request
     * @return the actual Mutation Query
     */
    static Storefront.MutationQuery mutationForCreateUser(Storefront.CustomerCreateInput input) {
        return Storefront.mutation(
                rootQuery -> rootQuery
                        .customerCreate(
                                input, createQuery -> createQuery
                                        .customer(
                                                customerQuery -> customerQuery
                                                        .id()
                                                        .email()
                                                        .firstName()
                                                        .lastName()
                                        )
                                        .userErrors(
                                                errorsQuery -> errorsQuery
                                                        .field()
                                                        .message()
                                        )
                        )
        );
    }

    /**
     * Method used to create the Mutation Query for
     * adding a new address to the user's account
     *
     * @param token     the user's access token
     * @param firstName the user's first name
     * @param lastName  the user's last name
     * @param phone     the user's phone number
     * @param company   the selected company; not Mandatory
     * @param address1  the selected primary address
     * @param address2  the selected secondary address; not Mandatory
     * @param city      the selected city
     * @param province
     * @param country   the selected country
     * @param zip       the selected zip code   @return the actual Mutation Query
     */
    static Storefront.MutationQuery mutationForCreateAddress(
            String token, String firstName, String lastName, String phone, String company, String address1, String address2,
            String city, String province, String country,
            String zip
    ) {
        Storefront.MailingAddressInput input = new Storefront.MailingAddressInput()
                .setAddress1(address1)
                .setAddress2(address2)
                .setCity(city)
                .setCountry(country)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPhone(phone)
                .setZip(zip)
                .setProvince(province)
                .setCompany(company);

        return Storefront.mutation(
                root -> root
                        .customerAddressCreate(
                                token,
                                input,
                                result -> result
                                        .customerAddress(
                                                address -> address
                                                        .firstName()
                                                        .lastName()
                                                        .phone()
                                                        .city()
                                                        .province()
                                                        .country()
                                                        .zip()
                                                        .company()
                                                        .address1()
                                                        .address2()
                                        )
                                        .userErrors(
                                                error -> error
                                                        .field()
                                                        .message()
                                        )
                        )
        );
    }

    /**
     * Method used to create the password recovery mutation query
     *
     * @param email the user's emai address
     * @return the actual Mutation Query
     */
    static Storefront.MutationQuery mutationForRecoverPassword(String email) {
        return Storefront.mutation(
                root -> root
                        .customerRecover(
                                email,
                                result -> result
                                        .userErrors(
                                                error -> error
                                                        .field()
                                                        .message()
                                        )
                        )
        );
    }

    /**
     * Method used to create the Mutation Query for updating the
     * user's account information
     *
     * @param token     the user's access token
     * @param firstName the user's selected first name
     * @param lastName  the user's selected last name
     * @param phone     the user's selected phone number
     * @return the actual Mutation Query
     */
    static Storefront.MutationQuery mutationForUpdateUser(
            String token, String firstName, String lastName, String phone
    ) {
        Storefront.CustomerUpdateInput input = new Storefront.CustomerUpdateInput()
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPhone(phone);

        return Storefront.mutation(
                root -> root
                        .customerUpdate(
                                token,
                                input,
                                result -> result
                                        .customer(
                                                user -> user
                                                        .email()
                                                        .firstName()
                                                        .lastName()
                                                        .phone()
                                        )
                                        .userErrors(
                                                error -> error
                                                        .field()
                                                        .message()
                                        )
                        )
        );
    }

    /**
     * Method used to set an address as default Billing Address.
     *
     * @param token     the user's access token
     * @param addressId the ID of the Address to be used as default
     * @return the actual Mutation Query
     */
    static Storefront.MutationQuery mutationForUpdateBillingAddress(final String token, final ID addressId) {
        return Storefront.mutation(
                root -> root
                        .customerDefaultAddressUpdate(
                                token,
                                addressId,
                                result -> result
                                        .customer(
                                                user -> user
                                                        .defaultAddress(
                                                                address -> address
                                                                        .address1()
                                                                        .address2()
                                                                        .city()
                                                                        .country()
                                                                        .zip()
                                                                        .company()
                                                                        .province()
                                                                        .phone()
                                                                        .lastName()
                                                                        .firstName()
                                                        )
                                        )
                                        .userErrors(
                                                error -> error
                                                        .field()
                                                        .message()
                                        )
                        )
        );
    }

    /**
     * Method used to create the Mutation Query for updating an existing
     * address associated to the user
     *
     * @param token     the user's access token
     * @param addressId the ID of the address to be updated
     * @param firstName the user's selected first name
     * @param lastName  the user's selected last name
     * @param phone     the user's selected phone number
     * @param company   the user's selected company; not Mandatory
     * @param address1  the user's selected primary address
     * @param address2  the user's selected secondary address; not Mandatory
     * @param city      the user's selected city
     * @param province  the user's selected province
     * @param zip       the user's selected zip code
     * @param country   the user's selected country   @return the actual Mutation Query
     */
    static Storefront.MutationQuery mutationForUpdateAddress(
            final String token, final String addressId, String firstName,
            String lastName, String phone, String company, String address1,
            String address2, String city, String province, String zip, String country
    ) {
        final Storefront.MailingAddressInput input = new Storefront.MailingAddressInput()
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPhone(phone)
                .setCity(city)
                .setZip(zip)
                .setCountry(country)
                .setCompany(company)
                .setAddress1(address1)
                .setAddress2(address2)
                .setProvince(province);

        return Storefront.mutation(
                root -> root
                        .customerAddressUpdate(
                                token, new ID(addressId), input,
                                result -> result
                                        .customerAddress(
                                                address -> address
                                                        .address1()
                                                        .address2()
                                                        .city()
                                                        .zip()
                                                        .country()
                                                        .company()
                                                        .firstName()
                                                        .lastName()
                                                        .phone()
                                        )
                                        .userErrors(
                                                error -> error
                                                        .message()
                                                        .field()
                                        )
                        )
        );
    }


    static Storefront.MutationQuery mutationForCreateCheckout(String email) {
        ArrayList<ProductModel.ProductVariantModel> variants = DataManagerHelper.getInstance().getVariants();

        List<Storefront.CheckoutLineItemInput> lineItems = new ArrayList<Storefront.CheckoutLineItemInput>();

        for (ProductModel.ProductVariantModel variant : variants) {
            Storefront.CheckoutLineItemInput input = new Storefront.CheckoutLineItemInput(1, variant.getID());

            lineItems.add(input);
        }

        Storefront.CheckoutCreateInput input = new Storefront.CheckoutCreateInput()
                .setEmail(email)
                .setLineItems(lineItems);

        return Storefront.mutation(
                root -> root
                        .checkoutCreate(
                                input,
                                result -> result
                                        .checkout(
                                                checkout -> checkout
                                                        .createdAt()
                                                        .email()
                                                        .order(
                                                                order -> order
                                                                        .orderNumber()
                                                                        .totalShippingPrice()
                                                                        .totalPrice()
                                                        )
                                                        .webUrl()
                                                        .subtotalPrice()
                                                        .totalTax()
                                                        .totalPrice()
                                                        .lineItems(
                                                                args -> args.first(25),
                                                                items -> items
                                                                        .edges(
                                                                                edge -> edge
                                                                                        .node(
                                                                                                node -> node
                                                                                                        .quantity()
                                                                                                        .title()
                                                                                                        .variant(
                                                                                                                variant -> variant
                                                                                                                        .price()
                                                                                                                        .compareAtPrice()
                                                                                                                        .selectedOptions(
                                                                                                                                option -> option
                                                                                                                                        .name()
                                                                                                                                        .value()
                                                                                                                        )
                                                                                                        )
                                                                                        )
                                                                        )
                                                        )
                                        )
                                        .userErrors(
                                                error -> error
                                                        .field()
                                                        .message()
                                        )
                        )
        );
    }

    static Storefront.MutationQuery createUpdateCheckoutEmail(
            String checkoutId, String email
    ) {
        return Storefront.mutation(
                root -> root
                        .checkoutEmailUpdate(
                                new ID(checkoutId),
                                email,
                                result -> result
                                        .checkout(
                                                checkout -> checkout
                                                        .webUrl()
                                                        .email()
                                                        .shippingAddress(
                                                                address -> address
                                                                        .firstName()
                                                                        .lastName()
                                                                        .phone()
                                                                        .company()
                                                                        .address1()
                                                                        .address2()
                                                                        .city()
                                                                        .province()
                                                                        .country()
                                                                        .zip()
                                                        )
                                                        .createdAt()
                                        )
                                        .userErrors(
                                                error -> error
                                                        .field()
                                                        .message()
                                        )
                        )
        );
    }

    static Storefront.MutationQuery createUpdateCheckoutAddress(
            String checkoutId, String address1, String address2,
            String firstName, String lastName, String phone, String zip,
            String city, String country, String province
    ) {
        Storefront.MailingAddressInput input = new Storefront.MailingAddressInput()
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPhone(phone)
                .setCity(city)
                .setCountry(country)
                .setZip(zip)
                .setProvince(province)
                .setAddress1(address1)
                .setAddress2(address2);

        return Storefront.mutation(
                root -> root
                        .checkoutShippingAddressUpdate(
                                input,
                                new ID(checkoutId),
                                result -> result
                                        .checkout(
                                                checkout -> checkout
                                                        .email()
                                                        .webUrl()
                                                        .shippingAddress(
                                                                address -> address
                                                                        .firstName()
                                                                        .lastName()
                                                                        .phone()
                                                                        .company()
                                                                        .address1()
                                                                        .address2()
                                                                        .city()
                                                                        .province()
                                                                        .country()
                                                                        .zip()
                                                        )
                                        )
                                        .userErrors(
                                                error -> error
                                                        .field()
                                                        .message()
                                        )
                        )
        );
    }

    static Storefront.MutationQuery createGooglePayOrder(String token) {
        Storefront.MailingAddress billingAddress = DataManager.getInstance().getBillingAddress();
        final double price = DataManagerHelper.getInstance().getProductsPrice();

        final PaymentToken paymentToken = new PaymentToken(token, GraphClientManager.IDEMPOTENCY_KEY);

        return Storefront.mutation(
                root -> root
                        .checkoutCompleteWithTokenizedPayment(
                                DataManager.getInstance().getCheckout().getId(),
                                new Storefront.TokenizedPaymentInput(
                                        new BigDecimal(price),
                                        paymentToken.publicKeyHash,
                                        new Storefront.MailingAddressInput()
                                                .setFirstName(billingAddress.getFirstName())
                                                .setLastName(billingAddress.getLastName())
                                                .setPhone(billingAddress.getPhone())
                                                .setCompany(billingAddress.getCompany())
                                                .setAddress1(billingAddress.getAddress1())
                                                .setAddress2(billingAddress.getAddress2())
                                                .setCity(billingAddress.getCity())
                                                .setProvince(billingAddress.getProvince())
                                                .setZip(billingAddress.getZip())
                                                .setCountry(billingAddress.getCountry()),
                                        paymentToken.token,
                                        "android_pay"
                                ).setIdempotencyKey(paymentToken.publicKeyHash),
                                _queryBuilder -> _queryBuilder
                                        .payment(
                                                pay -> pay
                                                        .ready()
                                                        .errorMessage()
                                        )
                                        .checkout(
                                                checkout -> checkout
                                                        .email()
                                                        .webUrl()
                                                        .shippingAddress(
                                                                address -> address
                                                                        .firstName()
                                                                        .lastName()
                                                                        .phone()
                                                                        .company()
                                                                        .address1()
                                                                        .address2()
                                                                        .city()
                                                                        .province()
                                                                        .country()
                                                                        .zip()
                                                        )
                                                        .ready()
                                        )
                                        .userErrors(
                                                error -> error
                                                        .field()
                                                        .message()
                                        )
                        )
        );
    }

    static Storefront.MutationQuery createCreditCardOrder(final String vaultId) {
        Storefront.MailingAddress billingAddress = DataManager.getInstance().getBillingAddress();
        final double price = DataManagerHelper.getInstance().getProductsPrice();

        return Storefront.mutation(
                root -> root
                        .checkoutCompleteWithCreditCard(
                                DataManager.getInstance().getCheckout().getId(),
                                new Storefront.CreditCardPaymentInput(
                                        new BigDecimal(price),
                                        GraphClientManager.IDEMPOTENCY_KEY,
                                        new Storefront.MailingAddressInput()
                                                .setFirstName(billingAddress.getFirstName())
                                                .setLastName(billingAddress.getLastName())
                                                .setPhone(billingAddress.getPhone())
                                                .setCompany(billingAddress.getCompany())
                                                .setAddress1(billingAddress.getAddress1())
                                                .setAddress2(billingAddress.getAddress2())
                                                .setCity(billingAddress.getCity())
                                                .setProvince(billingAddress.getProvince())
                                                .setZip(billingAddress.getZip())
                                                .setCountry(billingAddress.getCountry()),
                                        vaultId
                                ),
                                _queryBuilder -> _queryBuilder
                                        .payment(
                                                pay -> pay
                                                        .ready()
                                                        .errorMessage()
                                        )
                                        .checkout(
                                                checkout -> checkout
                                                        .email()
                                                        .webUrl()
                                                        .shippingAddress(
                                                                address -> address
                                                                        .firstName()
                                                                        .lastName()
                                                                        .phone()
                                                                        .company()
                                                                        .address1()
                                                                        .address2()
                                                                        .city()
                                                                        .province()
                                                                        .country()
                                                                        .zip()
                                                        )
                                                        .ready()
                                        )
                                        .userErrors(
                                                error -> error
                                                        .field()
                                                        .message()
                                        )
                        )
        );
    }
}

