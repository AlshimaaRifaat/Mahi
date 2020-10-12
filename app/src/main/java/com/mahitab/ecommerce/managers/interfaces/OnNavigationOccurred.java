package com.mahitab.ecommerce.managers.interfaces;

public interface OnNavigationOccurred {
    enum Direction {
        CUSTOMER, SHIPPING
    }

    void onNavigationOccurred(Direction direction);
}

