package com.mahitab.ecommerce.managers.interfaces;

public interface BaseCallback {
    int RESULT_OK = 200;
    int RESULT_FAILED = 100;

    void onResponse(int status);

    void onFailure(String message);
}
