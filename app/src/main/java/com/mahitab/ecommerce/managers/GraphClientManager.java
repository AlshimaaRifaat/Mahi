package com.mahitab.ecommerce.managers;

import android.content.Context;


import com.mahitab.ecommerce.managers.ClientMutation;
import com.mahitab.ecommerce.managers.ClientQuery;
import com.mahitab.ecommerce.models.CurrentUser;
import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.HttpCachePolicy;
import com.shopify.buy3.MutationGraphCall;
import com.shopify.buy3.QueryGraphCall;
import com.shopify.buy3.RetryHandler;
import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.ID;
import com.shopify.graphql.support.Nullable;

import java.util.UUID;


//import okhttp3url.OkHttpClient;
//import okhttp3.logging.HttpLoggingInterceptor;


public final class GraphClientManager {
    private static final String SHOP_DOMAIN = "mahitab.com";
    private static final String API_KEY = "407b2b4dbc9db87e69b1a0116b1fbc87";
    public static final String MERCHANT_ID = "merchant.estore.your.id";
    public static final String PUBLIC_KEY = "BOSJcwUSqSSNm8EnmlKYBRRjeCtPvCJpSkjUxWUjOCl1G5FoI6uIzVi77dA+Rola/LphZDaLVCn7Ttd8OO3Offs=";
    public static final String IDEMPOTENCY_KEY = UUID.randomUUID().toString();

    public static GraphClient mClient;




    GraphClientManager(Context context) {
        mClient = GraphClient.builder(context)
                .shopDomain(SHOP_DOMAIN)
                .accessToken(API_KEY)
                .defaultHttpCachePolicy(HttpCachePolicy.CACHE_FIRST)
                .build();
    }

    void getShop(GraphCall.Callback<Storefront.QueryRoot> callback) {
        Storefront.QueryRootQuery query = ClientQuery.queryForShop();
        mClient
                .queryGraph(query)
                .enqueue(callback);
    }

    void getProducts(ID collectionID, GraphCall.Callback<Storefront.QueryRoot> callback) {
        Storefront.QueryRootQuery query = ClientQuery.queryProducts(collectionID);
        QueryGraphCall call = mClient.queryGraph(query);

        call.enqueue(callback);
    }

    void getCollections(GraphCall.Callback<Storefront.QueryRoot> callback) {
        Storefront.QueryRootQuery query = ClientQuery.queryCollections();
        QueryGraphCall call = mClient.queryGraph(query);

        call.enqueue(callback);
    }

    void getAllCollections(GraphCall.Callback<Storefront.QueryRoot> callback) {
        Storefront.QueryRootQuery query = ClientQuery.queryAllCollections();
        QueryGraphCall call = mClient.queryGraph(query);

        call.enqueue(callback);
    }

    void updateCheckoutEmail(ID checkoutId, String email, GraphCall.Callback<Storefront.Mutation> callback) {
        Storefront.MutationQuery query = ClientMutation.createUpdateCheckoutEmail(checkoutId.toString(), email);

        mClient.mutateGraph(query).enqueue(callback);
    }

  /*  void updateCashCheckoutEmail(ID checkoutId, String email, GraphCall.Callback<Storefront.Mutation> callback) {
        Storefront.MutationQuery query = ClientMutation.createUpdateCashCheckoutEmail(checkoutId.toString(), email);

        mClient.mutateGraph(query).enqueue(callback);
    }*/
    void updateCheckoutAddress(ID checkoutId, Storefront.MailingAddress address, GraphCall.Callback<Storefront.Mutation> callback) {
        Storefront.MutationQuery query = ClientMutation.createUpdateCheckoutAddress(
                checkoutId.toString(), address.getAddress1(),
                address.getAddress2(),address.getFirstName(),
                address.getLastName(), address.getPhone(),
                address.getZip(), address.getCity(),
                address.getCountry(), address.getProvince());

        mClient.mutateGraph(query).enqueue(callback);
    }
    void createCheckout(GraphCall.Callback<Storefront.Mutation> callback) {
        Storefront.MutationQuery query = ClientMutation.mutationForCreateCheckout(CurrentUser.getInstance().getEmail());

        mClient.mutateGraph(query).enqueue(callback);
    }






    void placeOrder(String token, PaymentMethod method, GraphCall.Callback<Storefront.Mutation> callback) {
        Storefront.MutationQuery query = null;

        switch (method) {
            case CARD_PAYMENT:
                query = ClientMutation.createCreditCardOrder(token);
                break;
            case GOOGLE_PAY:
                query = ClientMutation.createGooglePayOrder(token);
                break;
        }

        mClient.mutateGraph(query).enqueue(callback);
    }
    void register(String email, String password, String firstName, String lastName, @Nullable boolean acceptsMarketing,
                  GraphCall.Callback<Storefront.Mutation> callback) {
        Storefront.MutationQuery query = ClientQuery.mutationForCreateUser(
                email, password, firstName, lastName
        );
        MutationGraphCall call = mClient.mutateGraph(query);

        call.enqueue(callback);
    }
    void resetUserPassword(String email, GraphCall.Callback<Storefront.Mutation> callback) {
        Storefront.MutationQuery query = ClientMutation.mutationForRecoverPassword(email);

        mClient.mutateGraph(query).enqueue(callback);
    }


}
enum PaymentMethod {
    CARD_PAYMENT, GOOGLE_PAY
}
