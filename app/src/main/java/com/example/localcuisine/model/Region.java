package com.example.localcuisine.model;

public enum Region {
    NORTH("Miền Bắc"),
    CENTRAL("Miền Trung"),
    SOUTH("Miền Nam"),
    ALL("Tất cả");

    private final String displayName;

    Region(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
