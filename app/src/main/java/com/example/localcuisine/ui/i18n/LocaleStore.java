// ui/i18n/LocaleStore.java
package com.example.localcuisine.ui.i18n;

import android.content.Context;
import android.content.SharedPreferences;

public class LocaleStore {

    private static final String PREF_NAME = "locale_pref";
    private static final String KEY_LANG = "lang";

    private final SharedPreferences prefs;

    public LocaleStore(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public String getLanguage() {
        return prefs.getString(KEY_LANG, null);
    }

    public void setLanguage(String lang) {
        prefs.edit().putString(KEY_LANG, lang).apply();
    }
}
