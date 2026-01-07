// AppContext.java
package com.example.localcuisine;

import android.app.Application;
import android.content.Context;

import com.example.localcuisine.ui.i18n.LocaleManager;

public class AppContext extends Application {

    private static AppContext instance;

    public static Context get() {
        if (instance == null) {
            throw new IllegalStateException("AppContext not initialized");
        }
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Áp locale sớm nhất có thể
        LocaleManager.apply(this);
    }
}
