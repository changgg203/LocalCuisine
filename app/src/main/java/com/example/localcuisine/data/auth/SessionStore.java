package com.example.localcuisine.data.auth;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.example.localcuisine.model.Region;

/**
 * SessionStore
 * <p>
 * Lớp chịu trách nhiệm lưu trữ và truy xuất trạng thái phiên đăng nhập
 * của người dùng trên thiết bị (local session).
 *
 * <h3>Vai trò</h3>
 * <ul>
 *     <li>Lưu UID người dùng đã đăng nhập</li>
 *     <li>Lưu các thiết lập ngữ cảnh người dùng (ví dụ: vùng miền)</li>
 *     <li>Cung cấp API kiểm tra trạng thái đăng nhập cục bộ</li>
 * </ul>
 *
 * <h3>Thiết kế</h3>
 * <ul>
 *     <li>Sử dụng SharedPreferences để lưu trữ dữ liệu nhẹ</li>
 *     <li>Không chứa logic xác thực</li>
 *     <li>Được sử dụng bởi AuthRepository và các tầng nghiệp vụ</li>
 * </ul>
 *
 * <h3>Phạm vi</h3>
 * SessionStore chỉ xử lý dữ liệu session cục bộ,
 * không đảm bảo tính hợp lệ của UID trên Firebase.
 */
public class SessionStore {

    /**
     * Tên file SharedPreferences
     */
    private static final String PREF_NAME = "user_session";

    /**
     * Key lưu UID Firebase của người dùng
     */
    private static final String KEY_UID = "firebase_uid";

    /**
     * Key lưu vùng miền người dùng
     */
    private static final String KEY_USER_REGION = "user_region";

    /**
     * SharedPreferences instance
     */
    private final SharedPreferences prefs;

    /**
     * Khởi tạo SessionStore
     *
     * @param context Context dùng để truy cập SharedPreferences
     */
    public SessionStore(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }


    /**
     * Lưu UID người dùng vào session
     * <p>
     * Nếu uid là null, session sẽ bị xoá (logout).
     *
     * @param uid UID Firebase của người dùng hoặc null
     */
    public void saveUser(@Nullable String uid) {
        if (uid == null) {
            logout();
        } else {
            prefs.edit().putString(KEY_UID, uid).apply();
        }
    }


    /**
     * Lấy UID người dùng hiện tại trong session
     *
     * @return UID nếu tồn tại, null nếu chưa đăng nhập
     */
    @Nullable
    public String getUserId() {
        return prefs.getString(KEY_UID, null);
    }


    /**
     * Kiểm tra trạng thái đăng nhập dựa trên session cục bộ
     *
     * @return true nếu đã có UID trong session, false nếu không
     */
    public boolean isLoggedIn() {
        return getUserId() != null;
    }


    /**
     * Xoá toàn bộ dữ liệu session
     * <p>
     * Thường được gọi khi người dùng đăng xuất.
     */
    public void logout() {
        prefs.edit().clear().apply();
    }

    /**
     * Lấy vùng miền hiện tại của người dùng
     *
     * @return Region đã lưu, mặc định là Region.ALL nếu chưa có hoặc lỗi
     */
    public Region getUserRegion() {
        String raw = prefs.getString(KEY_USER_REGION, null);
        if (raw == null) return Region.ALL;

        try {
            return Region.valueOf(raw);
        } catch (Exception e) {
            return Region.ALL;
        }
    }

    /**
     * Lưu vùng miền người dùng
     *
     * @param region Vùng miền cần lưu, mặc định là Region.ALL nếu null
     */
    public void setUserRegion(Region region) {
        if (region == null) region = Region.ALL;
        prefs.edit().putString(KEY_USER_REGION, region.name()).apply();
    }
}
