// ui/i18n/LocaleManager.java
package com.example.localcuisine.ui.i18n;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

public class LocaleManager {

    public static void apply(Context context) {
        LocaleStore store = new LocaleStore(context);
        String lang = store.getLanguage();

        if (lang == null) return;

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = context.getResources().getConfiguration();
        config.setLocale(locale);

        context.getResources().updateConfiguration(
                config,
                context.getResources().getDisplayMetrics()
        );
    }
}
