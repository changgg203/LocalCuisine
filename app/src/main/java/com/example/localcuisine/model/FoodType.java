package com.example.localcuisine.model;

public enum FoodType {

    // Dạng món
    NOODLE("Món nước"),
    DRY("Món khô"),
    SOUP("Món canh"),
    RICE("Món cơm"),
    ROLL("Món cuốn"),
    STEW("Món hầm"),

    // Vai trò bữa ăn
    MAIN("Món chính"),
    SIDE("Món phụ"),
    BREAKFAST("Ăn sáng"),
    LUNCH("Ăn trưa"),
    DINNER("Ăn tối"),
    SNACK("Ăn vặt"),

    // Hương vị
    SPICY("Cay"),
    MILD("Thanh đạm"),
    SWEET("Ngọt"),
    SAVORY("Đậm đà"),
    FATTY("Béo"),
    SOUR("Chua"),

    // Ngữ cảnh
    QUICK("Nhanh gọn"),
    HEAVY("Ăn no"),
    LIGHT("Nhẹ bụng"),
    COMFORT("Dễ ăn"),

    // Dinh dưỡng / lối sống
    VEGETARIAN("Chay"),
    SEAFOOD("Hải sản"),
    MEAT("Thịt"),
    BALANCED("Cân bằng"),

    // Văn hoá
    TRADITIONAL("Truyền thống"),
    STREET_FOOD("Món đường phố"),
    FESTIVE("Dịp lễ"),
    FAMILY("Bữa cơm gia đình"),
    LOCAL_SPECIALTY("Đặc sản địa phương"),
    OTHER("Khac");

    private final String displayName;

    FoodType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
