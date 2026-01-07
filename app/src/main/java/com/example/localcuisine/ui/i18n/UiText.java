// ui/i18n/UiText.java
package com.example.localcuisine.ui.i18n;

public class UiText {

    public static String t(UiTextKey key) {
        return UiTextProvider.get(key);
    }
}
