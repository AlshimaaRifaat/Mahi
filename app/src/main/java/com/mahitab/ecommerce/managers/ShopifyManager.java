package com.mahitab.ecommerce.managers;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.mahitab.ecommerce.activities.PaymentWebViewActivity;
import com.mahitab.ecommerce.models.AddressModel;
import com.mahitab.ecommerce.models.CartItemQuantity;
import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.QueryGraphCall;
import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.ID;
import com.shopify.graphql.support.Input;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

public class ShopifyManager {

    private static final String TAG = "ShopifyManager";

    public static final int LAUNCH_PAYMENT_ACTIVITY = 101;

    public static Observable<QueryGraphCall> getCurrentCustomer(String token) {
        Storefront.QueryRootQuery queryRootQuery = Storefront.query(rootQuery -> rootQuery
                .customer(token,
                        userQuery -> userQuery
                                .id()
                                .firstName()
                                .lastName()
                                .email()
                                .acceptsMarketing()
                                .displayName()
                                .phone()
                                .defaultAddress(
                                        address -> address
                                                .firstName()
                                                .lastName()
                                                .address1()
                                                .address2()
                                                .phone()
                                                .company()
                                                .city()
                                                .country()
                                                .province()
                                                .zip()
                                )
                                .addresses(
                                        args -> args
                                                .first(25),
                                        address -> address
                                                .edges(
                                                        edge -> edge
                                                                .node(
                                                                        node -> node
                                                                                .firstName()
                                                                                .lastName()
                                                                                .address1()
                                                                                .address2()
                                                                                .phone()
                                                                                .company()
                                                                                .city()
                                                                                .country()
                                                                                .province()
                                                                                .zip()
                                                                )
                                                )
                                )
                )
        );
        return Observable.just(GraphClientManager.mClient.queryGraph(queryRootQuery));
    }

    public static Observable<QueryGraphCall> getCustomerAddresses(String token) {
        Storefront.QueryRootQuery query = Storefront.query(root -> root
                .customer(token, customer -> customer
                        .addresses(arg -> arg.first(10), connection -> connection
                                .edges(edge -> edge
                                        .node(node -> node
                                                .address1()
                                                .address2()
                                                .city()
                                                .province()
                                                .country()
                                                .phone()

                                        )
                                )
                        )
                )
        );
        return Observable.just(GraphClientManager.mClient.queryGraph(query));
    }

    public static void checkoutAsGuest(Activity activity, List<CartItemQuantity> cartProducts) {

        ArrayList<Storefront.CheckoutLineItemInput> inputArrayList = new ArrayList<>();
        for (int i = 0; i < cartProducts.size(); i++) {
            inputArrayList.add(new Storefront.CheckoutLineItemInput(cartProducts.get(i).getQuantity(), cartProducts.get(i).getVariantId()));
        }
        Storefront.CheckoutCreateInput input = new Storefront.CheckoutCreateInput()
                .setLineItemsInput(Input.value(inputArrayList));

        Storefront.MutationQuery query = Storefront.mutation(mutationQuery -> mutationQuery
                .checkoutCreate(input, createPayloadQuery -> createPayloadQuery
                        .checkout(Storefront.CheckoutQuery::webUrl
                        )
                        .userErrors(userErrorQuery -> userErrorQuery
                                .field()
                                .message()
                        ))
        );

        GraphClientManager.mClient.mutateGraph(query).enqueue(new GraphCall.Callback<Storefront.Mutation>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.Mutation> response) {
                if (response.data() != null) {
                    if (!response.data().getCheckoutCreate().getUserErrors().isEmpty()) {
                        // handle user friendly errors
                        Log.e(TAG, "onResponse: " + response.data().getCheckoutCreate().getUserErrors());
                    } else {
                        ID checkoutId = response.data().getCheckoutCreate().getCheckout().getId();
                        String webUrl = response.data().getCheckoutCreate().getCheckout().getWebUrl();
                        Log.d(TAG, "ch id: " + checkoutId.toString());

                        Intent guestCustomerIntent = new Intent(activity, PaymentWebViewActivity.class);
                        guestCustomerIntent.putExtra("web_url", webUrl);
                        activity.startActivityForResult(guestCustomerIntent, LAUNCH_PAYMENT_ACTIVITY);
                    }
                }

            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                // handle errors
                Log.d(TAG, "onFailure: " + error.getMessage());
            }
        });
    }

    public static void checkoutAsCustomer(Activity activity, List<CartItemQuantity> cartProducts, Storefront.Customer currentCustomer, AddressModel addressModel) {
        ArrayList<Storefront.CheckoutLineItemInput> inputArrayList = new ArrayList<>();
        for (int i = 0; i < cartProducts.size(); i++) {
            inputArrayList.add(new Storefront.CheckoutLineItemInput(cartProducts.get(i).getQuantity(), cartProducts.get(i).getVariantId()));
        }
        Storefront.CheckoutCreateInput input = new Storefront.CheckoutCreateInput()
                .setLineItemsInput(Input.value(inputArrayList));

        Storefront.MutationQuery query = Storefront.mutation(mutationQuery -> mutationQuery
                .checkoutCreate(input, createPayloadQuery -> createPayloadQuery
                        .checkout(Storefront.CheckoutQuery::webUrl
                        )
                        .userErrors(userErrorQuery -> userErrorQuery
                                .field()
                                .message()
                        ))
        );

        GraphClientManager.mClient.mutateGraph(query).enqueue(new GraphCall.Callback<Storefront.Mutation>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.Mutation> response) {
                if (response.data() != null) {
                    if (!response.data().getCheckoutCreate().getUserErrors().isEmpty()) {
                        // handle user friendly errors
                        Log.e(TAG, "onResponse: " + response.data().getCheckoutCreate().getUserErrors());
                    } else {
                        ID checkoutId = response.data().getCheckoutCreate().getCheckout().getId();


                        // update email  MutationQuery for second time
                        Storefront.MutationQuery mutationQuery = Storefront.mutation(mutation -> mutation
                                .checkoutEmailUpdate(checkoutId,
                                        currentCustomer.getEmail(),
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

                        GraphClientManager.mClient.mutateGraph(mutationQuery).enqueue(new GraphCall.Callback<Storefront.Mutation>() {
                            @Override
                            public void onResponse(@NonNull GraphResponse<Storefront.Mutation> response) {

                                String strCheckoutId = null;
                                if (response.data() != null) {
                                    strCheckoutId = response.data().getCheckoutEmailUpdate().getCheckout().getId().toString();
                                }
                                Log.d(TAG, "ch id email: " + strCheckoutId);

                                if (strCheckoutId != null) {

                                    ID checkoutId = new ID(strCheckoutId);
                                    Log.d(TAG, "itt: " + checkoutId);

                                    //queryUpdateAddress(checkoutId);
                                    Storefront.MailingAddressInput inputAddress;
                                    if (addressModel != null) {
                                        inputAddress = new Storefront.MailingAddressInput()
                                                .setFirstName(currentCustomer.getFirstName())
                                                .setLastName(currentCustomer.getLastName())
                                                .setPhone(currentCustomer.getPhone())
                                                .setCity(addressModel.getCity())
                                                .setCountry(addressModel.getCountry())
                                                .setZip(addressModel.getZipCode())
                                                .setProvince(addressModel.getProvince())
                                                .setAddress1(addressModel.getAddress1())
                                                .setAddress2(addressModel.getAddress2());
                                    } else {
                                        inputAddress = new Storefront.MailingAddressInput()
                                                .setFirstName(currentCustomer.getFirstName())
                                                .setLastName(currentCustomer.getLastName())
                                                .setPhone(currentCustomer.getPhone())
                                                .setCity("")
                                                .setCountry("")
                                                .setZip("")
                                                .setProvince("")
                                                .setAddress1("")
                                                .setAddress2("");
                                    }

                                    Storefront.MutationQuery mutationQuery = Storefront.mutation(mutation -> mutation
                                            .checkoutShippingAddressUpdate(
                                                    inputAddress,
                                                    checkoutId,
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

                                    GraphClientManager.mClient.mutateGraph(mutationQuery).enqueue(new GraphCall.Callback<Storefront.Mutation>() {
                                        @Override
                                        public void onResponse(@NonNull GraphResponse<Storefront.Mutation> response) {
                                            if (response.data() != null) {
                                                String webUrl = response.data().getCheckoutShippingAddressUpdate().getCheckout().getWebUrl();
                                                Log.d(TAG, "web url: " + response.data().getCheckoutShippingAddressUpdate().getCheckout().getWebUrl());
                                                ID checkoutId = response.data().getCheckoutShippingAddressUpdate().getCheckout().getId();


                                                Intent customerIntent = new Intent(activity, PaymentWebViewActivity.class);
                                                customerIntent.putExtra("web_url", webUrl);
                                                activity.startActivityForResult(customerIntent, LAUNCH_PAYMENT_ACTIVITY);

                                                Log.d(TAG, "iddd: " + checkoutId.toString());
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NonNull GraphError error) {
                                            Log.e(TAG, "onFailure: " + error.getMessage());
                                        }
                                    });
                                }

                            }

                            @Override
                            public void onFailure(@NonNull GraphError error) {

                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                // handle errors
                Log.d(TAG, "onFailure: " + error.getMessage());
            }
        });
    }
}
