package com.mahitab.ecommerce.managers.interfaces;

import androidx.annotation.UiThread;

public interface NavigationToggleEvent {
    @UiThread
    void enableNavigation();

    @UiThread
    void disableNavigation();
}
