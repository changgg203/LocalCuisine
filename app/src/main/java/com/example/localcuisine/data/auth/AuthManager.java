package com.example.localcuisine.data.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * AuthManager
 * <p>
 * Lớp quản lý xác thực người dùng trong ứng dụng.
 * Đóng vai trò là tầng trung gian (wrapper) giữa FirebaseAuth
 * và các module nghiệp vụ / UI.
 *
 * <h3>Trách nhiệm</h3>
 * <ul>
 *     <li>Quản lý trạng thái đăng nhập hiện tại</li>
 *     <li>Cung cấp thông tin người dùng đang đăng nhập</li>
 *     <li>Thực hiện đăng xuất</li>
 * </ul>
 *
 * <h3>Nguyên tắc thiết kế</h3>
 * <ul>
 *     <li>Sử dụng Singleton để đảm bảo chỉ tồn tại một AuthManager duy nhất</li>
 *     <li>Không chứa logic UI</li>
 *     <li>Không xử lý nghiệp vụ liên quan đến người dùng (profile, preference, ...)</li>
 * </ul>
 *
 * <h3>Phạm vi</h3>
 * AuthManager chỉ chịu trách nhiệm cho <b>authentication state</b>,
 * không chịu trách nhiệm cho dữ liệu người dùng.
 */
public class AuthManager {

    /**
     * Singleton instance của AuthManager
     */
    private static AuthManager instance;

    /**
     * FirebaseAuth instance dùng để thao tác xác thực
     */
    private final FirebaseAuth auth;

    /**
     * Constructor private để đảm bảo Singleton pattern
     */
    private AuthManager() {
        auth = FirebaseAuth.getInstance();
    }

    /**
     * Lấy instance duy nhất của AuthManager
     *
     * @return AuthManager instance
     */
    public static AuthManager getInstance() {
        if (instance == null) {
            instance = new AuthManager();
        }
        return instance;
    }

    /**
     * Lấy người dùng hiện tại đang đăng nhập
     *
     * @return FirebaseUser nếu đã đăng nhập, null nếu chưa đăng nhập
     */
    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    /**
     * Kiểm tra trạng thái đăng nhập
     *
     * @return true nếu người dùng đã đăng nhập, false nếu chưa
     */
    public boolean isLoggedIn() {
        return getCurrentUser() != null;
    }

    /**
     * Lấy UID của người dùng hiện tại
     *
     * @return UID của người dùng nếu đã đăng nhập, null nếu chưa đăng nhập
     */
    public String getUid() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    /**
     * Đăng xuất người dùng hiện tại
     * <p>
     * Sau khi gọi method này:
     * <ul>
     *     <li>FirebaseAuth sẽ clear session</li>
     *     <li>getCurrentUser() sẽ trả về null</li>
     * </ul>
     */
    public void logout() {
        auth.signOut();
    }
}
