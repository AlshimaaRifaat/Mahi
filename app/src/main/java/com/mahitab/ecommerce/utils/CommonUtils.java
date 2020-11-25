package com.mahitab.ecommerce.utils;

/*
 * Copyright © 2018-present, MNK Group. All rights reserved.
 */


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.mahitab.ecommerce.R;
import com.shopify.buy3.Storefront;

import java.util.Locale;

public class CommonUtils {

    /**
     * FIRST_SCREEN values should be set to:
     * - 0 : If the user should be redirected to the Home Page of the app
     * after the Splash Screen.
     * <p>
     * - 1 : If the user should be redirected to the Login Screen after
     * the Splash Screen.
     */
    public static final int FIRST_SCREEN_FLAG = 0;
    public static final AccountStatus ANONYMOUS_ACCOUNT_PERMISSION = AccountStatus.ACCOUNT_NOT_REQUIRED;

    private static final String TAG = "CommonUtils";

    public static final String USER_ADDRESS_EMAIL_KEY = "USER_ADDRESS_EMAIL_KEY";
    private static final String USER_ADDRESS_FIRST_NAME_KEY = "USER_ADDRESS_FIRST_NAME_KEY";
    private static final String USER_ADDRESS_LAST_NAME_KEY = "USER_ADDRESS_LAST_NAME_KEY";
    private static final String USER_ADDRESS_COMPANY_KEY = "USER_ADDRESS_COMPANY_KEY";
    private static final String USER_ADDRESS_CITY_KEY = "USER_ADDRESS_CITY_KEY";
    private static final String USER_ADDRESS_COUNTRY_KEY = "USER_ADDRESS_COUNTRY_KEY";
    private static final String USER_ADDRESS_STREET_1_KEY = "USER_ADDRESS_STREET_1_KEY";
    private static final String USER_ADDRESS_STREET_2_KEY = "USER_ADDRESS_STREET_2_KEY";
    private static final String USER_ADDRESS_PROVINCE_KEY = "USER_ADDRESS_PROVINCE_KEY";
    private static final String USER_ADDRESS_ZIP_KEY = "USER_ADDRESS_ZIP_KEY";
    private static final String USER_ADDRESS_PHONE_KEY = "USER_ADDRESS_PHONE_KEY";

    private static ProgressDialog dialog;

    public static View createItemOptionView(String s, Context context) {
        TextView view = new TextView(context);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        view.setText(s);
        view.setTextColor(Color.parseColor("#000000"));
        view.setTextSize(14);
        return view;
    }

    public static boolean isEmailValid(String str) {
        String emailRegex = "^((?!.*\\-{2})(?!.*\\.{2})(?!.*\\_{2})[a-zA-Z0-9_\\-\\.]*)[^\\.\\-\\_\\`\\~\\'\\\"\\!@#$%&*()^]@([a-zA-Z0-9]+)([a-zA-Z0-9_\\-\\.]*)\\.([a-zA-Z]{2,})";

        return str.matches(emailRegex);
    }

    public static boolean isPasswordValid(String str) {
        String passRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[a-zA-Z0-9]{8,}$";

        return str.matches(passRegex);
    }

    public static boolean isNameValid(String str) {
        String firstNameRegex = "[A-Z][a-zA-Z]*";//"^[^±!@£$%^&*_+§¡€#¢§¶•ªº«\\/<>?:;|=.,()]{1,20}$";
        String lastNameRegex = "[a-zA-z]+([ '-][a-zA-Z]+)*";

        return str.matches(firstNameRegex) || str.matches(lastNameRegex);
    }

    public static boolean isAlphaNumeric(String str) {
        String alphaNumericRegex = "^[a-zA-Z0-9- ]*$";

        return str.matches(alphaNumericRegex);
    }

    public static boolean isCardNoValid(String str) {
        String cardNoRegex = "^[0-9]+([0-9]+)+$";

        return str.matches(cardNoRegex);
    }

    public static boolean isNumeric(String str) {
        String numericRegex = "^[0-9]*\\.{0,1}[0-9]*$";

        return str.matches(numericRegex);
    }

    public static boolean isPhone(String str) {
        return PhoneNumberUtils.isGlobalPhoneNumber(str);
    }

    public static boolean isNotEmpty(String str) {
        return str != null && !str.isEmpty();
    }

    public static Bundle buildBundleFromAddress(Storefront.MailingAddress shippingAddress) {
        Bundle args = new Bundle();
        if (shippingAddress != null) {
            args.putString(USER_ADDRESS_FIRST_NAME_KEY, shippingAddress.getFirstName());
            args.putString(USER_ADDRESS_LAST_NAME_KEY, shippingAddress.getLastName());
            args.putString(USER_ADDRESS_STREET_1_KEY, shippingAddress.getAddress1());
            args.putString(USER_ADDRESS_STREET_2_KEY, shippingAddress.getAddress2());
            args.putString(USER_ADDRESS_CITY_KEY, shippingAddress.getCity());
            args.putString(USER_ADDRESS_COUNTRY_KEY, shippingAddress.getCountry());
            args.putString(USER_ADDRESS_COMPANY_KEY, shippingAddress.getCompany());
            args.putString(USER_ADDRESS_PROVINCE_KEY, shippingAddress.getProvince());
            args.putString(USER_ADDRESS_ZIP_KEY, shippingAddress.getZip());
            args.putString(USER_ADDRESS_PHONE_KEY, shippingAddress.getPhone());
        } else {
            args.putString(USER_ADDRESS_FIRST_NAME_KEY, "");
            args.putString(USER_ADDRESS_LAST_NAME_KEY, "");
            args.putString(USER_ADDRESS_STREET_1_KEY, "");
            args.putString(USER_ADDRESS_STREET_2_KEY, "");
            args.putString(USER_ADDRESS_CITY_KEY, "");
            args.putString(USER_ADDRESS_COUNTRY_KEY, "");
            args.putString(USER_ADDRESS_COMPANY_KEY, "");
            args.putString(USER_ADDRESS_PROVINCE_KEY, "");
            args.putString(USER_ADDRESS_ZIP_KEY, "");
            args.putString(USER_ADDRESS_PHONE_KEY, "");
        }

        return args;
    }

    public enum AccountStatus {
        ACCOUNT_REQUIRED, ACCOUNT_NOT_REQUIRED
    }

    public static void setArDefaultLocale(Activity context) {
        Locale locale = new Locale("ar");
        Configuration config = new Configuration(context.getResources().getConfiguration());
        Locale.setDefault(locale);
        config.setLocale(locale);
        context.getBaseContext().getResources().updateConfiguration(config,
                context.getBaseContext().getResources().getDisplayMetrics());
    }

    public static void showProgressDialog(Context context) {
        dialog = new ProgressDialog(context, R.style.ProgressDialog);
        dialog.setCancelable(false);
        dialog.setMessage(context.getResources().getString(R.string.loading));
        dialog.show();
    }

    public static ProgressDialog getProgressDialog() {
        return dialog;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showErrorDialog(Context context, String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage(errorMessage);
        builder.setPositiveButton(context.getResources().getString(R.string.ok), (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    public static String getImageThumbnailURL(String imageURL){
        String originalImageURL=imageURL.split("\\?v=")[0];
        int lastIndexOfDot = originalImageURL.lastIndexOf(".");
        String imageWithoutExtension=originalImageURL.substring(0,lastIndexOfDot);
        String imageExtension=originalImageURL.substring(lastIndexOfDot);
        return imageWithoutExtension+"_medium@2x.progressive"+imageExtension;
    }
}

