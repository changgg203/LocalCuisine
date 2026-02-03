// ui/i18n/UiTextProvider.java
package com.example.localcuisine.ui.i18n;

import com.example.localcuisine.AppContext;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UiTextProvider {

    private static final Map<String, Map<UiTextKey, String>> DATA = new HashMap<>();

    static {
        Map<UiTextKey, String> vi = new HashMap<>();
        vi.put(UiTextKey.APP_NAME, "Local Cuisine");
        vi.put(UiTextKey.HOME_TITLE, "Ẩm thực địa phương");
        vi.put(UiTextKey.HOME_EXPLORE, "Khám phá");
        vi.put(UiTextKey.SEARCH_HINT, "Tìm món ăn...");
        vi.put(UiTextKey.SEARCH_EMPTY, "Không tìm thấy món ăn");
        vi.put(UiTextKey.FAVORITE_TITLE, "Yêu thích");
        vi.put(UiTextKey.ERROR_COMMON, "Có lỗi xảy ra");
        vi.put(UiTextKey.PROFILE_DISPLAY_NAME_FALLBACK, "Người dùng");
        vi.put(UiTextKey.PROFILE_CHANGE_LANGUAGE, "Ngôn ngữ");
        vi.put(UiTextKey.PROFILE_CHANGE_PASSWORD, "Đổi mật khẩu");
        vi.put(UiTextKey.CHANGE_PASSWORD_TITLE, "Đổi mật khẩu");
        vi.put(UiTextKey.CHANGE_PASSWORD_CURRENT_HINT, "Mật khẩu hiện tại");
        vi.put(UiTextKey.CHANGE_PASSWORD_NEW_HINT, "Mật khẩu mới");
        vi.put(UiTextKey.CHANGE_PASSWORD_CONFIRM_HINT, "Xác nhận mật khẩu mới");
        vi.put(UiTextKey.CHANGE_PASSWORD_SAVE, "Lưu");
        vi.put(UiTextKey.CHANGE_PASSWORD_SUCCESS, "Đổi mật khẩu thành công");
        vi.put(UiTextKey.CHANGE_PASSWORD_ERROR, "Không thể đổi mật khẩu");
        vi.put(UiTextKey.CHANGE_PASSWORD_MISMATCH, "Mật khẩu mới không khớp");
        vi.put(UiTextKey.LANG_DIALOG_TITLE, "Thay đổi ngôn ngữ");
        vi.put(UiTextKey.LANG_DIALOG_MESSAGE, "Tính năng này sẽ được cập nhật sau.");
        vi.put(UiTextKey.LANG_DIALOG_OK, "OK");
        vi.put(UiTextKey.LOGOUT_TITLE, "Đăng xuất");
        vi.put(UiTextKey.LOGOUT_MESSAGE, "Bạn có chắc muốn đăng xuất không?");
        vi.put(UiTextKey.LOGOUT_CONFIRM, "Đăng xuất");
        vi.put(UiTextKey.LOGOUT_CANCEL, "Huỷ");
        vi.put(UiTextKey.LANG_SELECT_TITLE, "Chọn ngôn ngữ");
        vi.put(UiTextKey.LANG_VI, "Tiếng Việt");
        vi.put(UiTextKey.LANG_EN, "English");
        vi.put(UiTextKey.PROFILE_EDIT, "Chỉnh sửa hồ sơ");
        vi.put(UiTextKey.PROFILE_LOGOUT, "Đăng xuất");
        vi.put(UiTextKey.ADMIN_MANAGEMENT, "Quản lý hệ thống");

        vi.put(UiTextKey.EDIT_PROFILE_SAVE, "Lưu");
        vi.put(UiTextKey.EDIT_PROFILE_LOAD_ERROR, "Không thể tải hồ sơ");
        vi.put(UiTextKey.EDIT_PROFILE_SAVE_SUCCESS, "Đã lưu hồ sơ");
        vi.put(UiTextKey.EDIT_PROFILE_SAVE_ERROR, "Lưu hồ sơ thất bại");
        vi.put(UiTextKey.EDIT_PROFILE_NAME_REQUIRED, "Vui lòng nhập họ tên");
        vi.put(UiTextKey.EDIT_PROFILE_NAME_FALLBACK, "Người dùng");
        vi.put(UiTextKey.EDIT_PROFILE_HINT_NAME, "Họ và tên");
        vi.put(UiTextKey.EDIT_PROFILE_HINT_EMAIL, "Email");
        vi.put(UiTextKey.EDIT_PROFILE_HINT_PHONE, "Số điện thoại");
        vi.put(UiTextKey.EDIT_PROFILE_HINT_BIO, "Giới thiệu ngắn");
        vi.put(UiTextKey.HOME_SUBTITLE, "Khám phá món Việt");
        vi.put(UiTextKey.HOME_SEARCH_HINT, "Tìm món ăn...");

        vi.put(UiTextKey.DETAIL_RATE_HINT, "Chạm để đánh giá");

        vi.put(UiTextKey.DETAIL_FAVORITE_ADD, "❤️ Yêu thích");
        vi.put(UiTextKey.DETAIL_FAVORITE_REMOVE, "💔 Bỏ yêu thích");

        vi.put(UiTextKey.DETAIL_REVIEW_BUTTON, "✍️ Đánh giá");
        vi.put(UiTextKey.DETAIL_MAP_BUTTON, "📍 Xem trên bản đồ");

        vi.put(UiTextKey.DETAIL_REVIEW_SECTION_TITLE, "Đánh giá gần đây");
        vi.put(UiTextKey.DETAIL_REVIEW_SEE_MORE, "Xem thêm đánh giá");
        vi.put(UiTextKey.DETAIL_RECOMMEND_SECTION_TITLE, "Có thể bạn cũng thích");

        vi.put(UiTextKey.DETAIL_REVIEW_DIALOG_TITLE, "Đánh giá món ăn");
        vi.put(UiTextKey.DETAIL_REPLY_DIALOG_TITLE, "Phản hồi");

        vi.put(UiTextKey.DETAIL_SEND, "Gửi");
        vi.put(UiTextKey.DETAIL_CANCEL, "Huỷ");

        vi.put(UiTextKey.DETAIL_LOGIN_REQUIRED, "Bạn cần đăng nhập");
        vi.put(UiTextKey.DETAIL_LOGIN_REQUIRED_REVIEW, "Bạn cần đăng nhập để đánh giá");
        vi.put(UiTextKey.DETAIL_LOGIN_REQUIRED_REPLY, "Bạn cần đăng nhập để phản hồi");
        vi.put(UiTextKey.REVIEW_EMPTY_COMMENT, "— Không có nhận xét —");
        vi.put(UiTextKey.REVIEW_REPLY_ACTION, "Phản hồi");
        vi.put(UiTextKey.REVIEW_DIALOG_TITLE, "Đánh giá món ăn");
        vi.put(UiTextKey.REVIEW_DIALOG_HINT, "Chia sẻ cảm nhận của bạn");
        vi.put(UiTextKey.REPLY_DIALOG_HINT, "Chia sẻ cảm nhận của bạn");
        vi.put(UiTextKey.FAVORITE_SUBTITLE, "Những món bạn đã lưu");
        vi.put(UiTextKey.FAVORITE_SEARCH_HINT, "Tìm trong yêu thích...");
        vi.put(UiTextKey.FAVORITE_EMPTY, "Không có danh sách ở đây 🤍");
        vi.put(UiTextKey.NOTIF_DEFAULT_TITLE, "Thông báo");
        vi.put(UiTextKey.NOTIF_NEW, "Thông báo mới");

        vi.put(UiTextKey.NOTIF_REPLY_TITLE, "Phản hồi mới");
        vi.put(UiTextKey.NOTIF_REVIEW_TITLE, "Đánh giá mới");
        vi.put(UiTextKey.NOTIF_FAVORITE_TITLE, "Món ăn được yêu thích");

        vi.put(UiTextKey.NOTIF_REPLY_CONTENT,
                "Có người đã phản hồi về món ăn bạn quan tâm");
        vi.put(UiTextKey.NOTIF_REVIEW_CONTENT,
                "Có một đánh giá mới cho món ăn của bạn");
        vi.put(UiTextKey.NOTIF_FAVORITE_CONTENT,
                "Một món ăn bạn theo dõi vừa được yêu thích");
        vi.put(UiTextKey.NAV_HOME, "Trang chủ");
        vi.put(UiTextKey.NAV_FAVORITE, "Yêu thích");
        vi.put(UiTextKey.NAV_NOTIFICATION, "Thông báo");
        vi.put(UiTextKey.NAV_PROFILE, "Hồ sơ");
        vi.put(UiTextKey.ADMIN_DELETE_TITLE, "Xoá món ăn");
        vi.put(UiTextKey.ADMIN_DELETE_MESSAGE, "Bạn có chắc muốn xoá \"%s\"?");
        vi.put(UiTextKey.ADMIN_DELETE_CONFIRM, "Xoá");
        vi.put(UiTextKey.ADMIN_DELETE_CANCEL, "Huỷ");

        vi.put(UiTextKey.ADMIN_LOAD_ERROR, "Không tải được danh sách món");
        vi.put(UiTextKey.ADMIN_DELETE_SUCCESS, "Đã xoá món");
        vi.put(UiTextKey.ADMIN_DELETE_ERROR, "Không thể xoá món");
        vi.put(UiTextKey.ADMIN_BEST_TIME_LABEL, "Thời điểm");
        vi.put(UiTextKey.ADMIN_FOOD_NAME_REQUIRED, "Tên món không được để trống");
        vi.put(UiTextKey.ADMIN_FOOD_REGION_TIME_REQUIRED,
                "Vui lòng chọn khu vực và thời điểm");

        vi.put(UiTextKey.ADMIN_FOOD_ADD_SUCCESS, "Đã thêm món mới");
        vi.put(UiTextKey.ADMIN_FOOD_UPDATE_SUCCESS, "Đã cập nhật món");
        vi.put(UiTextKey.ADMIN_ERROR_COMMON, "Có lỗi xảy ra");

        vi.put(UiTextKey.ADMIN_BEST_TIME_MORNING, "Buổi sáng");
        vi.put(UiTextKey.ADMIN_BEST_TIME_NOON, "Buổi trưa");
        vi.put(UiTextKey.ADMIN_BEST_TIME_EVENING, "Buổi tối");
        vi.put(UiTextKey.ADMIN_FOOD_HINT_NAME, "Tên món ăn");
        vi.put(UiTextKey.ADMIN_FOOD_HINT_DESCRIPTION, "Mô tả món ăn");
        vi.put(UiTextKey.ADMIN_FOOD_HINT_TAGS, "Thẻ (phân cách bằng dấu phẩy)");
        vi.put(UiTextKey.ADMIN_FOOD_HINT_LOCATION, "Địa điểm");
        vi.put(UiTextKey.ADMIN_FOOD_HINT_IMAGE, "Tên file ảnh (assets)");
        vi.put(UiTextKey.REGION_NORTH, "Miền Bắc");
        vi.put(UiTextKey.REGION_CENTRAL, "Miền Trung");
        vi.put(UiTextKey.REGION_SOUTH, "Miền Nam");
        vi.put(UiTextKey.ADMIN_SAVE, "Lưu");
        vi.put(UiTextKey.ADMIN_CANCEL, "Hủy");

        vi.put(UiTextKey.ADMIN_IMAGE_LABEL, "Ảnh món ăn");
        vi.put(UiTextKey.ADMIN_FOOD_TYPE_LABEL, "Phân loại món");
        vi.put(UiTextKey.ADMIN_REGION_LABEL, "Khu vực");


        Map<UiTextKey, String> en = new HashMap<>();
        en.put(UiTextKey.APP_NAME, "Local Cuisine");
        en.put(UiTextKey.HOME_TITLE, "Local Cuisine");
        en.put(UiTextKey.HOME_EXPLORE, "Explore");
        en.put(UiTextKey.SEARCH_HINT, "Search food...");
        en.put(UiTextKey.SEARCH_EMPTY, "No food found");
        en.put(UiTextKey.FAVORITE_TITLE, "Favorites");
        en.put(UiTextKey.ERROR_COMMON, "Something went wrong");
        en.put(UiTextKey.LANG_SELECT_TITLE, "Select language");
        en.put(UiTextKey.LANG_VI, "Vietnamese");
        en.put(UiTextKey.LANG_EN, "English");
        en.put(UiTextKey.PROFILE_EDIT, "Edit profile");
        en.put(UiTextKey.PROFILE_CHANGE_LANGUAGE, "Language");
        en.put(UiTextKey.PROFILE_CHANGE_PASSWORD, "Change Password");
        en.put(UiTextKey.PROFILE_LOGOUT, "Log out");
        en.put(UiTextKey.PROFILE_DISPLAY_NAME_FALLBACK, "User");
        en.put(UiTextKey.LOGOUT_TITLE, "Log out");
        en.put(UiTextKey.LOGOUT_MESSAGE, "Are you sure you want to log out?");
        en.put(UiTextKey.LOGOUT_CONFIRM, "Log out");
        en.put(UiTextKey.LOGOUT_CANCEL, "Cancel");
        en.put(UiTextKey.EDIT_PROFILE_SAVE, "Save");
        en.put(UiTextKey.EDIT_PROFILE_LOAD_ERROR, "Failed to load profile");
        en.put(UiTextKey.EDIT_PROFILE_SAVE_SUCCESS, "Profile saved");
        en.put(UiTextKey.EDIT_PROFILE_SAVE_ERROR, "Failed to save profile");
        en.put(UiTextKey.EDIT_PROFILE_NAME_REQUIRED, "Please enter your name");
        en.put(UiTextKey.EDIT_PROFILE_NAME_FALLBACK, "User");
        en.put(UiTextKey.EDIT_PROFILE_HINT_NAME, "Full name");
        en.put(UiTextKey.EDIT_PROFILE_HINT_EMAIL, "Email");
        en.put(UiTextKey.EDIT_PROFILE_HINT_PHONE, "Phone number");
        en.put(UiTextKey.EDIT_PROFILE_HINT_BIO, "Short bio");
        en.put(UiTextKey.HOME_SUBTITLE, "Explore Vietnamese food");
        en.put(UiTextKey.HOME_SEARCH_HINT, "Search food...");
        en.put(UiTextKey.DETAIL_RATE_HINT, "Tap to rate");

        en.put(UiTextKey.DETAIL_FAVORITE_ADD, "❤️ Favorite");
        en.put(UiTextKey.DETAIL_FAVORITE_REMOVE, "💔 Remove favorite");

        en.put(UiTextKey.DETAIL_REVIEW_BUTTON, "✍️ Review");
        en.put(UiTextKey.DETAIL_MAP_BUTTON, "📍 View on map");

        en.put(UiTextKey.DETAIL_REVIEW_SECTION_TITLE, "Recent reviews");
        en.put(UiTextKey.DETAIL_REVIEW_SEE_MORE, "See more reviews");
        en.put(UiTextKey.DETAIL_RECOMMEND_SECTION_TITLE, "You may also like");

        en.put(UiTextKey.DETAIL_REVIEW_DIALOG_TITLE, "Review food");
        en.put(UiTextKey.DETAIL_REPLY_DIALOG_TITLE, "Reply");

        en.put(UiTextKey.DETAIL_SEND, "Send");
        en.put(UiTextKey.DETAIL_CANCEL, "Cancel");

        en.put(UiTextKey.DETAIL_LOGIN_REQUIRED, "Please log in");
        en.put(UiTextKey.DETAIL_LOGIN_REQUIRED_REVIEW, "Please log in to review");
        en.put(UiTextKey.DETAIL_LOGIN_REQUIRED_REPLY, "Please log in to reply");
        en.put(UiTextKey.REVIEW_EMPTY_COMMENT, "— No comment —");
        en.put(UiTextKey.REVIEW_REPLY_ACTION, "Reply");
        en.put(UiTextKey.REVIEW_DIALOG_TITLE, "Review this food");
        en.put(UiTextKey.REVIEW_DIALOG_HINT, "Share your thoughts");
        en.put(UiTextKey.REPLY_DIALOG_HINT, "Share your thoughts");
        en.put(UiTextKey.FAVORITE_SUBTITLE, "Saved dishes");
        en.put(UiTextKey.FAVORITE_SEARCH_HINT, "Search favorites...");
        en.put(UiTextKey.FAVORITE_EMPTY, "No items here 🤍");
        en.put(UiTextKey.NOTIF_DEFAULT_TITLE, "Notification");
        en.put(UiTextKey.NOTIF_NEW, "New notification");

        en.put(UiTextKey.NOTIF_REPLY_TITLE, "New reply");
        en.put(UiTextKey.NOTIF_REVIEW_TITLE, "New review");
        en.put(UiTextKey.NOTIF_FAVORITE_TITLE, "Food favorited");

        en.put(UiTextKey.NOTIF_REPLY_CONTENT,
                "Someone replied to a food you are interested in");
        en.put(UiTextKey.NOTIF_REVIEW_CONTENT,
                "There is a new review for your food");
        en.put(UiTextKey.NOTIF_FAVORITE_CONTENT,
                "A food you follow was just favorited");
        en.put(UiTextKey.NAV_HOME, "Home");
        en.put(UiTextKey.NAV_FAVORITE, "Favorites");
        en.put(UiTextKey.NAV_NOTIFICATION, "Notifications");
        en.put(UiTextKey.NAV_PROFILE, "Profile");
        en.put(UiTextKey.ADMIN_MANAGEMENT, "Management");
        en.put(UiTextKey.ADMIN_DELETE_TITLE, "Delete food");
        en.put(UiTextKey.ADMIN_DELETE_MESSAGE, "Are you sure you want to delete \"%s\"?");
        en.put(UiTextKey.ADMIN_DELETE_CONFIRM, "Delete");
        en.put(UiTextKey.ADMIN_DELETE_CANCEL, "Cancel");

        en.put(UiTextKey.ADMIN_LOAD_ERROR, "Failed to load food list");
        en.put(UiTextKey.ADMIN_DELETE_SUCCESS, "Food deleted");
        en.put(UiTextKey.ADMIN_DELETE_ERROR, "Failed to delete food");
        en.put(UiTextKey.ADMIN_BEST_TIME_LABEL, "Best time");
        en.put(UiTextKey.ADMIN_FOOD_NAME_REQUIRED, "Food name is required");
        en.put(UiTextKey.ADMIN_FOOD_REGION_TIME_REQUIRED,
                "Please select region and best time");

        en.put(UiTextKey.ADMIN_FOOD_ADD_SUCCESS, "Food added");
        en.put(UiTextKey.ADMIN_FOOD_UPDATE_SUCCESS, "Food updated");
        en.put(UiTextKey.ADMIN_ERROR_COMMON, "Something went wrong");

        en.put(UiTextKey.ADMIN_BEST_TIME_MORNING, "Morning");
        en.put(UiTextKey.ADMIN_BEST_TIME_NOON, "Noon");
        en.put(UiTextKey.ADMIN_BEST_TIME_EVENING, "Evening");
        en.put(UiTextKey.ADMIN_FOOD_HINT_NAME, "Food name");
        en.put(UiTextKey.ADMIN_FOOD_HINT_DESCRIPTION, "Food description");
        en.put(UiTextKey.ADMIN_FOOD_HINT_TAGS, "Tags (comma separated)");
        en.put(UiTextKey.ADMIN_FOOD_HINT_LOCATION, "Location");
        en.put(UiTextKey.ADMIN_FOOD_HINT_IMAGE, "Image file name (assets)");
        en.put(UiTextKey.REGION_NORTH, "Northern VN");
        en.put(UiTextKey.REGION_CENTRAL, "Central VN");
        en.put(UiTextKey.REGION_SOUTH, "Southern VN");
        en.put(UiTextKey.ADMIN_SAVE, "Save");
        en.put(UiTextKey.ADMIN_CANCEL, "Cancel");

        en.put(UiTextKey.ADMIN_IMAGE_LABEL, "Food image");
        en.put(UiTextKey.ADMIN_FOOD_TYPE_LABEL, "Food type");
        en.put(UiTextKey.ADMIN_REGION_LABEL, "Region");


        DATA.put("vi", vi);
        DATA.put("en", en);
    }

    // UiTextProvider.java (chỉnh get)
    public static String get(UiTextKey key) {
        String lang = new LocaleStore(AppContext.get()).getLanguage();

        if (lang == null) {
            lang = Locale.getDefault().getLanguage();
        }

        Map<UiTextKey, String> map = DATA.get(lang);
        if (map == null) {
            map = DATA.get("vi");
        }

        assert map != null;
        return map.getOrDefault(key, key.name());
    }

}
