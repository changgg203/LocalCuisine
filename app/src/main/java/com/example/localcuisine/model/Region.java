package com.example.localcuisine.model;

import com.example.localcuisine.ui.i18n.UiTextKey;
import com.example.localcuisine.ui.i18n.UiTextProvider;

public enum Region {
    NORTH("REGION_NORTH"),
    CENTRAL("REGION_CENTRAL"),
    SOUTH("REGION_SOUTH"),
    ALL("REGION_ALL");

    private final String displayName;

    Region(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return UiTextProvider.get(UiTextKey.valueOf(this.displayName));
    }
}
