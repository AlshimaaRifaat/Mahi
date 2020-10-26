package com.mahitab.ecommerce.managers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.mahitab.ecommerce.managers.interfaces.BaseCallback;
import com.mahitab.ecommerce.models.AddressModel;
import com.mahitab.ecommerce.models.CollectionModel;
import com.mahitab.ecommerce.models.MyOrdersModel;
import com.mahitab.ecommerce.models.ProductModel;
import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.ID;

import java.util.ArrayList;
import java.util.Observable;

import static com.mahitab.ecommerce.managers.PaymentMethod.CARD_PAYMENT;


public final class DataManager extends Observable {

    private static final String TAG = DataManager.class.getSimpleName();

    //region Instance
    private Storefront.Checkout mCheckout = null;
    private Storefront.MailingAddress mBillingAddress = null;

    private final Object mLock = new Object();

    private static class InstanceHelper {

        private static final DataManager INSTANCE = new DataManager();
    }

    public synchronized static DataManager getInstance() {
        return InstanceHelper.INSTANCE;
    }

    private DataManager() {
    }
    //endregion

    private GraphClientManager mClientManager;

    public void setClientManager(Context context) {
        mClientManager = new GraphClientManager(context);
    }

 /*  public void requestShop(BaseCallback callback) {
        mClientManager.getShop(new GraphCall.Callback<Storefront.QueryRoot>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {
                if (!response.hasErrors()) {
                    assert response.data() != null;
                    Storefront.Shop shop = response.data().getShop();
                    if (shop != null) {
                        DataManagerHelper.getInstance().setShop(shop);
                        callback.onResponse(BaseCallback.RESULT_OK);
                        return;
                    } else {
                        callback.onFailure("Response: " + response.errors().get(0).message());
                    }
                }

                callback.onFailure("Response: Errors retrieving shop");
            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                callback.onFailure(error.getLocalizedMessage());
            }
        });
    }
*/
    //region User Server calls
  /*  public void login(String email, String password, BaseCallback callback) {
        mClientManager.login(email, password, new GraphCall.Callback<Storefront.Mutation>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.Mutation> response) {
                if (!response.hasErrors()) {
                    if (response.data() == null) {
                        callback.onFailure("Error logging in");
                        return;
                    }

                    Storefront.CustomerAccessTokenCreatePayload payload = null;
                    if (response.data() == null || response.data().getCustomerAccessTokenCreate() == null) {
                        callback.onFailure("An unknown error occurred while trying to sign in");
                        return;
                    } else {
                        payload = response.data().getCustomerAccessTokenCreate();
                        if (payload.getUserErrors() != null && payload.getUserErrors().size() != 0) {
                            callback.onFailure(payload.getUserErrors().get(0).getMessage());
                            return;
                        }
                    }

                    Storefront.CustomerAccessToken token = payload.getCustomerAccessToken();
                    CurrentUser.getInstance(token);

                    mClientManager.getUserDetails(token.getAccessToken(), new GraphCall.Callback<Storefront.QueryRoot>() {
                        @Override
                        public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {
                            if (!response.hasErrors()) {
                                if (response.data() == null || response.data().getCustomer() == null) {
                                    callback.onFailure("Error retrieving user data");
                                    return;
                                } else {
                                    Storefront.Customer payload = response.data().getCustomer();
                                    CurrentUser.getInstance(payload);
                                    callback.onResponse(BaseCallback.RESULT_OK);
                                    return;
                                }
                            }

                            callback.onFailure(response.errors().get(0).message());
                        }

                        @Override
                        public void onFailure(@NonNull GraphError error) {
                            callback.onFailure(error.getLocalizedMessage());
                        }
                    });
                    return;
                }

                callback.onFailure(response.errors().get(0).message());
            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                callback.onFailure(error.getLocalizedMessage());
            }
        });
    }

    public void register(
            String email, String password,
            String firstName, String lastName,
            boolean acceptsMarketing,
            BaseCallback callback
    ) {
        mClientManager.register(
                email, password,
                firstName, lastName,
                acceptsMarketing,
                new GraphCall.Callback<Storefront.Mutation>() {
                    @Override
                    public void onResponse(@NonNull GraphResponse<Storefront.Mutation> response) {
                        if (!response.hasErrors()) {
                            if (response.data() == null || response.data().getCustomerCreate() == null) {
                                callback.onFailure("Error creating user");
                                return;
                            }

                            Storefront.CustomerCreatePayload payload = response.data().getCustomerCreate();
                            if (payload.getUserErrors() != null && payload.getUserErrors().size() != 0) {
                                callback.onFailure(payload.getUserErrors().get(0).getMessage());
                                return;
                            }

                            callback.onResponse(BaseCallback.RESULT_OK);
                            return;
                        }

                        callback.onFailure("Response: " + response.errors().get(0).message());
                    }

                    @Override
                    public void onFailure(@NonNull GraphError error) {
                        callback.onFailure(error.getLocalizedMessage());
                    }
                }
        );
    }

    public void userDetails(BaseCallback callback) {
        mClientManager.getUserDetails(
                CurrentUser.getInstance().getAccessToken(),
                new GraphCall.Callback<Storefront.QueryRoot>() {
                    @Override
                    public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {
                        if (response.hasErrors()) {
                            callback.onFailure(response.errors().get(0).message());
                        } else {
                            if (response.data() == null || response.data().getCustomer() == null) {
                                callback.onFailure("Error retrieving user details");
                                return;
                            }

                            Storefront.Customer localCustomer = response.data().getCustomer();
                            CurrentUser.getInstance(localCustomer);

                            callback.onResponse(BaseCallback.RESULT_OK);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull GraphError error) {
                        callback.onFailure(error.getMessage());
                    }
                }
        );
    }

    public void createUserAddress(
            String firstName, String lastName, String phone,
            String company, String address1, String address2, String zip, String city,
            String province, String country, boolean isDefault,
            BaseCallback callback
    ) {
        mClientManager.createUserAddress(
                firstName, lastName,
                phone, company, address1, address2,
                city, province, country, zip,
                new GraphCall.Callback<Storefront.Mutation>() {
                    @Override
                    public void onResponse(@NonNull GraphResponse<Storefront.Mutation> response) {
                        if (response.hasErrors()) {
                            callback.onFailure(response.errors().get(0).message());
                            return;
                        }

                        if(response.data() == null) {
                            callback.onFailure("Null body");
                            return;
                        }

                        Storefront.CustomerAddressCreatePayload payload = response.data().getCustomerAddressCreate();
                        if (payload.getUserErrors().size() != 0) {
                            callback.onFailure(payload.getUserErrors().get(0).getMessage());
                            return;
                        }

                        Storefront.MailingAddress address = payload.getCustomerAddress();
                        if(address == null) {
                            callback.onFailure("Null address retrieved");
                            return;
                        }

                        CurrentUser.getInstance().addAddress(address);

                        if(isDefault) {
                            mClientManager.updateUserDefaultAddress(address.getId(), new GraphCall.Callback<Storefront.Mutation>() {
                                @Override
                                public void onResponse(@NonNull GraphResponse<Storefront.Mutation> response) {
                                    if (response.hasErrors()) {
                                        callback.onFailure(response.errors().get(0).message());
                                        return;
                                    }

                                    if(response.data() == null) {
                                        callback.onFailure("Data null");
                                        return;
                                    }

                                    Storefront.CustomerDefaultAddressUpdatePayload customerPayload = response.data().getCustomerDefaultAddressUpdate();
                                    if(customerPayload.getUserErrors().size() != 0) {
                                        callback.onFailure(customerPayload.getUserErrors().get(0).getMessage());
                                        return;
                                    }

                                    Storefront.Customer customer = customerPayload.getCustomer();
                                    if(customer == null) {
                                        callback.onFailure("Customer is null");
                                        return;
                                    }

                                    Storefront.MailingAddress updatedAddress = customer.getDefaultAddress();
                                    if(updatedAddress == null) {
                                        callback.onFailure("Address is null");
                                        return;
                                    }

                                    CurrentUser.getInstance().setDefaultAddress(updatedAddress);
                                    callback.onResponse(BaseCallback.RESULT_OK);
                                }

                                @Override
                                public void onFailure(@NonNull GraphError error) {
                                    callback.onFailure(error.getLocalizedMessage());
                                }
                            });
                        } else {
                            callback.onResponse(BaseCallback.RESULT_OK);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull GraphError error) {
                        callback.onFailure(error.getLocalizedMessage());
                    }
                });
    }

    public void updateUserDetails(
            String addressId, String firstName, String lastName, String phone,
            String company, String address1, String address2, String zip, String city,
            String province, String country, boolean isDefault,
            BaseCallback callback
    ) {
        if (addressId != null && !addressId.isEmpty()) {
            mClientManager.updateUserAddress(
                    addressId, firstName, lastName, phone,
                    address1, address2, company, city,
                    province, country, zip,
                    new GraphCall.Callback<Storefront.Mutation>() {
                        @Override
                        public void onResponse(@NonNull GraphResponse<Storefront.Mutation> response) {
                            if (response.hasErrors()) {
                                callback.onFailure(response.errors().get(0).message());
                                return;
                            }

                            if (response.data() == null) {
                                callback.onFailure("Null body");
                                return;
                            }

                            Storefront.CustomerAddressUpdatePayload updatePayload = response.data().getCustomerAddressUpdate();
                            if(updatePayload.getCustomerAddress() == null && updatePayload.getUserErrors() != null) {
                                callback.onFailure(updatePayload.getUserErrors().get(0).getMessage());
                                return;
                            }

                            Storefront.MailingAddress address = updatePayload.getCustomerAddress();
                            CurrentUser.getInstance().updateAddress(addressId, address);

                            DataManager.this.updateDefaultAddress(addressId, new BaseCallback() {
                                @Override
                                public void onResponse(int status) {
                                    callback.onResponse(RESULT_OK);
                                }

                                @Override
                                public void onFailure(String message) {
                                    callback.onFailure(message);
                                }
                            });
//                            callback.onResponse(BaseCallback.RESULT_OK);
                        }

                        @Override
                        public void onFailure(@NonNull GraphError error) {
                            callback.onFailure(error.getLocalizedMessage());
                        }
                    });
        } else {
            callback.onFailure("Null Address ID");
        }
    }

    public void updateDefaultAddress(String addressId, BaseCallback callback) {
        mClientManager.updateUserDefaultAddress(new ID(addressId), new GraphCall.Callback<Storefront.Mutation>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.Mutation> response) {
                if(response.hasErrors()) {
                    callback.onFailure(response.errors().get(0).message());
                    return;
                }

                if(response.data() == null) {
                    callback.onFailure("Null data");
                    return;
                }

                Storefront.CustomerDefaultAddressUpdatePayload payload = response.data().getCustomerDefaultAddressUpdate();
                if(payload == null) {
                    callback.onFailure("Null Payload");
                    return;
                }

                Storefront.MailingAddress address = payload.getCustomer().getDefaultAddress();
                if(address == null) {
                    callback.onFailure("Null Address");
                    return;
                }

                CurrentUser.getInstance().setDefaultAddress(address);
                callback.onResponse(BaseCallback.RESULT_OK);
            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                callback.onFailure(error.getLocalizedMessage());
            }
        });
    }

    public void userAddresses(BaseCallback callback) {
        mClientManager.getUserAddresses(CurrentUser.getInstance().getAccessToken(), new GraphCall.Callback<Storefront.QueryRoot>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {
                if(response.hasErrors()) {
                    callback.onFailure(response.errors().get(0).message());
                    return;
                }

                if(response.data() == null) {
                    callback.onFailure("Null data");
                    return;
                }

                Storefront.Customer customer = response.data().getCustomer();
                if(customer == null) {
                    callback.onFailure("Null customer");
                    return;
                }

                Storefront.MailingAddressConnection conn = customer.getAddresses();
                if(conn == null) {
                    callback.onFailure("Addresses null");
                    return;
                }

                CurrentUser.getInstance().setAddresses(conn);
                callback.onResponse(BaseCallback.RESULT_OK);
            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                callback.onFailure(error.getLocalizedMessage());
            }
        });
    }
    //endregion */

    //region CHECKOUT
    public Storefront.Checkout getCheckout() {
        synchronized (mLock) {
            return mCheckout;
        }
    }

    public void resetCheckout() {
        mCheckout = null;
        mBillingAddress = null;
       /* if(CurrentUser.getInstance().getAccessToken().isEmpty()) {
            CurrentUser.getInstance().setEmail(null);
        }*/

        DataManagerHelper.getInstance().resetCart();
    }

    public void createCheckout(BaseCallback callback) {
        mClientManager.createCheckout(new GraphCall.Callback<Storefront.Mutation>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.Mutation> response) {
                if (response.hasErrors()) {
                    callback.onFailure(response.errors().get(0).message());
                    Log.e(TAG, "onResponse: CreateCheckout: " + response.errors().get(0).message());
                    return;
                }

                if (response.data() == null) {
                    callback.onFailure("Null body");
                    return;
                }

                Storefront.CheckoutCreatePayload payload = response.data().getCheckoutCreate();
                if (payload.getUserErrors().size() != 0) {
                    callback.onFailure(payload.getUserErrors().get(0).getMessage());
                    return;
                }

                mCheckout = payload.getCheckout();
                callback.onResponse(BaseCallback.RESULT_OK);
                DataManager.this.notifyObservers();
            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                Log.e(TAG, "onFailure: CreateCheckout: " + error.getLocalizedMessage());
                callback.onFailure(error.getLocalizedMessage());
            }
        });
    }



  /*  public void createCheckout(PaymentData paymentData, final BaseCallback callback) {
        String token = paymentData.getPaymentMethodToken().getToken() != null ?
                paymentData.getPaymentMethodToken().getToken() : "";

        CardInfo cardInfo = paymentData.getCardInfo();

        String email = paymentData.getEmail();
        final UserAddress billingAddress = cardInfo.getBillingAddress();
        final UserAddress shippingAddress = paymentData.getShippingAddress();

        CurrentUser.getInstance().setEmail(email);

        mClientManager.createCheckout(new GraphCall.Callback<Storefront.Mutation>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.Mutation> response) {
                if(response.hasErrors()) {
                    callback.onFailure(response.errors().get(0).message());
                    return;
                }

                if (response.data() == null) {
                    callback.onFailure("Null body");
                    return;
                }

                Storefront.CheckoutCreatePayload payload = response.data().getCheckoutCreate();
                if(payload.getUserErrors().size() != 0) {
                    callback.onFailure(payload.getUserErrors().get(0).getMessage());
                    return;
                }

                mCheckout = payload.getCheckout();
                DataManager.this.notifyObservers();

                String name = shippingAddress.getName();
                String firstName = name != null ? name.substring(0, name.indexOf(' ')): "";
                String lastName = name != null ? name.substring(name.indexOf(' ') + 1): "";
                Storefront.MailingAddress address = new Storefront.MailingAddress()
                        .setFirstName(firstName)
                        .setLastName(lastName)
                        .setPhone(shippingAddress.getPhoneNumber())
                        .setCompany(shippingAddress.getCompanyName())
                        .setAddress1(shippingAddress.getAddress1())
                        .setAddress2(shippingAddress.getAddress2())
                        .setCity(shippingAddress.getLocality())
                        .setCountryCode(shippingAddress.getCountryCode())
                        .setProvince(shippingAddress.getAdministrativeArea())
                        .setZip(shippingAddress.getPostalCode());

                mClientManager.updateCheckoutAddress(mCheckout.getId(), address, new GraphCall.Callback<Storefront.Mutation>() {
                    @Override
                    public void onResponse(@NonNull GraphResponse<Storefront.Mutation> response) {
                        if (response.hasErrors()) {
                            callback.onFailure(response.errors().get(0).message());
                            Log.e(TAG, "onResponse: UpdateChkAdd: " + response.errors().get(0).message());
                            return;
                        }

                        if (response.data() == null) {
                            callback.onFailure("Null body");
                            return;
                        }

                        Storefront.CheckoutShippingAddressUpdatePayload payload = response.data().getCheckoutShippingAddressUpdate();
                        if (payload.getUserErrors().size() != 0) {
                            callback.onFailure(payload.getUserErrors().get(0).getMessage());
                            Log.e(TAG, "onResponse: UpdateChkAdd: " + payload.getUserErrors().get(0).getMessage());
                            return;
                        }

                        Storefront.Checkout checkout = payload.getCheckout();
                        if (checkout == null) {
                            callback.onFailure("Null checkout");
                            return;
                        }

                        Storefront.MailingAddress address = checkout.getShippingAddress();
                        if (address == null) {
                            callback.onFailure("Null address");
                            return;
                        }

                        mCheckout = checkout;
                        DataManager.this.notifyObservers();

                        mClientManager.placeOrder(token, GOOGLE_PAY, new GraphCall.Callback<Storefront.Mutation>() {
                            @Override
                            public void onResponse(@NonNull GraphResponse<Storefront.Mutation> response) {
                                if(response.hasErrors()) {
                                    callback.onFailure(response.errors().get(0).message());
                                    return;
                                }

                                mCheckout = null;
                                DataManagerHelper.getInstance().resetCart();
                                callback.onResponse(BaseCallback.RESULT_OK);
                            }

                            @Override
                            public void onFailure(@NonNull GraphError error) {
                                callback.onFailure(error.getLocalizedMessage());
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NonNull GraphError error) {
                        callback.onFailure(error.getLocalizedMessage());
                    }
                });
            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                callback.onFailure(error.getLocalizedMessage());
            }
        });
    }*/

    public void updateCheckoutEmail(ID checkoutId, String email, BaseCallback callback) {
        mClientManager.updateCheckoutEmail(checkoutId, email, new GraphCall.Callback<Storefront.Mutation>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.Mutation> response) {
                if (response.hasErrors()) {
                    Log.e(TAG, "onResponse: UpdateChkEmail: " + response.errors().get(0).message());
                    callback.onFailure(response.errors().get(0).message());
                    return;
                }

                if (response.data() == null) {
                    callback.onFailure("Null body");
                    return;
                }

                Storefront.CheckoutEmailUpdatePayload payload = response.data().getCheckoutEmailUpdate();

                if (payload.getUserErrors().size() != 0) {
                    callback.onFailure(payload.getUserErrors().get(0).getMessage());
                    Log.e(TAG, "onResponse: UpdateChkEmail: " + payload.getUserErrors().get(0).getMessage());
                    return;
                }

                if (payload.getCheckout() == null) {
                    callback.onFailure("Null checkout");
                    return;
                }

                if (payload.getCheckout().getEmail() == null) {
                    callback.onFailure("Null email");
                    return;
                }

                mCheckout = payload.getCheckout();
                callback.onResponse(BaseCallback.RESULT_OK);
                DataManager.this.notifyObservers();
            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                Log.e(TAG, "onFailure: UpdateChkEmail: " + error.getLocalizedMessage());
                callback.onFailure(error.getLocalizedMessage());
            }
        });
    }

    public void updateCheckoutAddress(ID checkoutId, Storefront.MailingAddress address, BaseCallback callback) {
        mClientManager.updateCheckoutAddress(checkoutId, address, new GraphCall.Callback<Storefront.Mutation>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.Mutation> response) {
                synchronized (mLock) {
                    if (response.hasErrors()) {
                        callback.onFailure(response.errors().get(0).message());
                        Log.e(TAG, "onResponse: UpdateChkAdd: " + response.errors().get(0).message());
                        return;
                    }

                    if (response.data() == null) {
                        callback.onFailure("Null body");
                        return;
                    }

                    Storefront.CheckoutShippingAddressUpdatePayload payload = response.data().getCheckoutShippingAddressUpdate();
                    if (payload.getUserErrors().size() != 0) {
                        callback.onFailure(payload.getUserErrors().get(0).getMessage());
                        Log.e(TAG, "onResponse: UpdateChkAdd: " + payload.getUserErrors().get(0).getMessage());
                        return;
                    }

                    Storefront.Checkout checkout = payload.getCheckout();
                    if (checkout == null) {
                        callback.onFailure("Null checkout");
                        return;
                    }

                    Storefront.MailingAddress address = checkout.getShippingAddress();
                    if (address == null) {
                        callback.onFailure("Null address");
                        return;
                    }

                    mCheckout = checkout;
                    callback.onResponse(BaseCallback.RESULT_OK);
                    DataManager.this.notifyObservers();
                }
            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                callback.onFailure(error.getLocalizedMessage());
            }
        });
    }

    public void updateBillingAddress(Storefront.MailingAddress address, BaseCallback callback) {
        synchronized (mLock) {
            mBillingAddress = address;
            callback.onResponse(BaseCallback.RESULT_OK);
            DataManager.this.notifyObservers();
        }
    }

    public Storefront.MailingAddress getBillingAddress() {
        synchronized (mLock) {
            return mBillingAddress;
        }
    }

    public void placeOrder(String cardVault, BaseCallback callback) {
        mClientManager.placeOrder(cardVault, CARD_PAYMENT, new GraphCall.Callback<Storefront.Mutation>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.Mutation> response) {
                if (response.hasErrors()) {
                    callback.onFailure(response.errors().get(0).message());
                    Log.e(TAG, "onResponse: Order: " + response.errors().get(0).message());
                    return;
                }

                Storefront.Mutation data = response.data();
                Storefront.CheckoutCompleteWithCreditCardPayload payload = (data != null) ? data.getCheckoutCompleteWithCreditCard() : null;
                if (data == null) {
                    callback.onFailure("Data is null");
                    return;
                }

                if (payload.getUserErrors().size() != 0) {
                    Log.e(TAG, "onResponse: Order: " + payload.getUserErrors().get(0).getMessage());
                    callback.onFailure(payload.getUserErrors().get(0).getMessage());
                    return;
                }

                Storefront.Payment payment = payload.getPayment();

                if (payment.getErrorMessage() != null) {
                    callback.onFailure(payment.getErrorMessage());
                    Log.e(TAG, "onResponse: Order: " + payment.getErrorMessage());
                    return;
                }

                callback.onResponse(BaseCallback.RESULT_OK);
            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                callback.onFailure(error.getLocalizedMessage());
            }
        });
    }
    //endregion

    //region Server calls
    public void collections(BaseCallback callback) {
        mClientManager.getCollections(new GraphCall.Callback<Storefront.QueryRoot>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {
                if (!response.hasErrors()) {
                    Storefront.CollectionConnection connection = response.data().getShop().getCollections();
                    for (Storefront.CollectionEdge edge : connection.getEdges()) {
                        if (edge.getNode().getHandle().equalsIgnoreCase("frontpage")) {
                            continue;
                        }

                        CollectionModel newCollectionModel = new CollectionModel(edge);

                        DataManagerHelper.getInstance().getCollections().put(newCollectionModel.getID().toString(), newCollectionModel);
                        DataManagerHelper.getInstance().createProductsListForCollectionId(newCollectionModel.getID().toString());
                        for (ProductModel product : newCollectionModel.getPreviewProducts()) {
                            DataManagerHelper.getInstance().getProductsByCollectionID(newCollectionModel.getID().toString()).add(product);
                        }
                    }

                    callback.onResponse(BaseCallback.RESULT_OK);
                    return;
                }

                callback.onFailure(response.errors().get(0).message());
            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                callback.onFailure(error.getLocalizedMessage());
            }
        });
    }

    public void products(String collectionID, BaseCallback callback) {
        if (DataManagerHelper.getInstance().getCollections().containsKey(collectionID)) {
            callback.onResponse(BaseCallback.RESULT_OK);
        } else {
            mClientManager.getProducts(new ID(collectionID), new GraphCall.Callback<Storefront.QueryRoot>() {
                @Override
                public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {
                    if (!response.hasErrors()) {
                        Storefront.Collection collection = (Storefront.Collection) response.data().getNode();
                        Storefront.ProductConnection productConnection = collection.getProducts();

                        if (null == DataManagerHelper.getInstance().getProductsByCollectionID(collectionID) ||
                                DataManagerHelper.getInstance().getProductsByCollectionID(collectionID).size() != productConnection.getEdges().size()) {
                            if (null != DataManagerHelper.getInstance().getProductsByCollectionID(collectionID)) {
                                DataManagerHelper.getInstance().getProductsByCollectionID(collectionID).clear();
                            } else {
                                DataManagerHelper.getInstance().createProductsListForCollectionId(collectionID);
                            }
                        }

                        for (Storefront.ProductEdge edge : productConnection.getEdges()) {
                            DataManagerHelper.getInstance().getProductsByCollectionID(collectionID).add(new ProductModel(edge, collectionID));
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull GraphError error) {
                    callback.onFailure(error.getLocalizedMessage());
                }
            });
        }
    }

  /*   public ArrayList<CollectionModel> getCollectionsBySearchCriteria(String searchCriteria) {
        ArrayList<CollectionModel> result = new ArrayList<CollectionModel>();
        for(CollectionModel collectionModel: this.getCollections()) {
            ArrayList<ProductModel> collectionProducts = this.getProducts(collectionModel.getID());

            ArrayList<ProductModel> matchedProducts = new ArrayList<ProductModel>();
            for(ProductModel p: collectionProducts) {
                if(p.getTitle().toLowerCase().contains(searchCriteria.toLowerCase())) {
                    matchedProducts.add(p);
                }
            }

            if(matchedProducts.size() != 0) {
                result.add(CollectionModel.buildCollection(collectionModel, matchedProducts));
            }
        }

        return result;
    }
    //endregion

    //region  Shop management calls
    public String getShopName() {
        return DataManagerHelper.getInstance().getShop().getName();
    }

    public String getShopDescription() {
        return DataManagerHelper.getInstance().getShop().getDescription();
    }*/

   /* public String getShopCurrency() {
        return DataManagerHelper.getInstance().getShop().getCurrencyCode().toString();
    }*/

   /* public String getShopCountryCode() {
        return DataManagerHelper.getInstance().getShop().getCountryCode().toString();
    }

    public String getShopPrivacyUrl() {
        return DataManagerHelper.getInstance().getShop().getPrivacyPolicyUrl();
    }

    public String getShopTermsUrl() {
        return DataManagerHelper.getInstance().getShop().getTermsOfServiceUrl();
    }*/
    //endregion

    //region CART
    public double getProductsPrice() {
        return DataManagerHelper.getInstance().getProductsPrice();
    }

    //endregion

    //region Cached data
    public ArrayList<CollectionModel> getCollections() {
        ArrayList<CollectionModel> collections = new ArrayList<CollectionModel>();
        collections.addAll(DataManagerHelper.getInstance().getCollections().values());

        return collections;
    }

    public CollectionModel getCollectionByID(String collectionId) {
        return DataManagerHelper.getInstance().getCollections().get(collectionId);
    }
    public ArrayList<MyOrdersModel> getMyOrders() {
        ArrayList<MyOrdersModel> orders = new ArrayList<MyOrdersModel>();
        orders.addAll(DataManagerHelper.getInstance().fetchMyOrders().values());

        return orders;
    }

   /*  public ArrayList<ProductModel> getProducts(ID collectionID) {
        return DataManagerHelper.getInstance().getProductsByCollectionID(collectionID.toString());
    }*/

    public ProductModel getProductByID(String productID) {
        return DataManagerHelper.getInstance().getProductByID(productID);
    }
    //endregion

    public ArrayList<ProductModel> getAllProducts() {
        return DataManagerHelper.getInstance().getAllProducts();
    }

    public void register(
            String email, String password,
            String firstName, String lastName,
            boolean acceptsMarketing,
            BaseCallback callback) {
        mClientManager.register(
                email, password,
                firstName, lastName,
                acceptsMarketing,
                new GraphCall.Callback<Storefront.Mutation>() {
                    @Override
                    public void onResponse(@NonNull GraphResponse<Storefront.Mutation> response) {
                        if (!response.hasErrors()) {
                            if (response.data() == null || response.data().getCustomerCreate() == null) {
                                callback.onFailure("Error creating user");
                                return;
                            }

                            Storefront.CustomerCreatePayload payload = response.data().getCustomerCreate();
                            if (payload.getUserErrors() != null && payload.getUserErrors().size() != 0) {
                                callback.onFailure(payload.getUserErrors().get(0).getMessage());
                                return;
                            }

                            callback.onResponse(BaseCallback.RESULT_OK);
                            return;
                        }

                        callback.onFailure("Response: " + response.errors().get(0).message());
                    }

                    @Override
                    public void onFailure(@NonNull GraphError error) {
                        callback.onFailure(error.getLocalizedMessage());
                    }
                }
        );
    }

    public ArrayList<AddressModel> getAddresses() {
        ArrayList<AddressModel> addresses = new ArrayList<AddressModel>();
        addresses.addAll(DataManagerHelper.getInstance().fetchAddresses().values());
        return addresses;
    }
}

