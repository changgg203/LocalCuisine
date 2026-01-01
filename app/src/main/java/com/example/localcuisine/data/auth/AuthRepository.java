package com.example.localcuisine.data.auth;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * AuthRepository
 * <p>
 * Repository chịu trách nhiệm xử lý các nghiệp vụ xác thực người dùng
 * (đăng ký, đăng nhập, đăng xuất) dựa trên Firebase Authentication.
 *
 * <h3>Vai trò</h3>
 * <ul>
 *     <li>Thực hiện thao tác xác thực với Firebase</li>
 *     <li>Quản lý lưu trữ trạng thái đăng nhập cục bộ thông qua SessionStore</li>
 *     <li>Cung cấp API bất đồng bộ cho tầng ViewModel / UI</li>
 * </ul>
 *
 * <h3>Thiết kế</h3>
 * <ul>
 *     <li>Áp dụng Repository Pattern</li>
 *     <li>Tách biệt logic xác thực khỏi UI</li>
 *     <li>Sử dụng callback để xử lý kết quả bất đồng bộ</li>
 * </ul>
 *
 * <h3>Phạm vi</h3>
 * AuthRepository chỉ xử lý:
 * <ul>
 *     <li>Xác thực (authentication)</li>
 *     <li>Lưu / xoá session UID</li>
 * </ul>
 * Không xử lý:
 * <ul>
 *     <li>Dữ liệu profile người dùng</li>
 *     <li>Logic điều hướng UI</li>
 * </ul>
 */
public class AuthRepository {

    /**
     * FirebaseAuth instance để thực hiện xác thực
     */
    private final FirebaseAuth firebaseAuth;

    /**
     * SessionStore dùng để lưu trạng thái đăng nhập cục bộ
     */
    private final SessionStore sessionManager;

    /**
     * Khởi tạo AuthRepository
     *
     * @param context Context dùng để khởi tạo SessionStore
     */
    public AuthRepository(@NonNull Context context) {
        firebaseAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionStore(context);
    }


    /**
     * Đăng ký tài khoản mới bằng email và mật khẩu
     *
     * @param email    Email đăng ký
     * @param password Mật khẩu
     * @param callback Callback nhận kết quả đăng ký
     */
    public void register(
            String email,
            String password,
            @NonNull AuthCallback callback
    ) {
        firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    FirebaseUser user = result.getUser();
                    if (user != null) {
                        sessionManager.saveUser(user.getUid());
                        callback.onSuccess(user.getUid());
                    }
                })
                .addOnFailureListener(e ->
                        callback.onError(e.getMessage())
                );
    }


    /**
     * Đăng nhập người dùng bằng email và mật khẩu
     *
     * @param email    Email đăng nhập
     * @param password Mật khẩu
     * @param callback Callback nhận kết quả đăng nhập
     */
    public void login(
            String email,
            String password,
            @NonNull AuthCallback callback
    ) {
        firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    FirebaseUser user = result.getUser();
                    if (user != null) {
                        sessionManager.saveUser(user.getUid());
                        callback.onSuccess(user.getUid());
                    }
                })
                .addOnFailureListener(e ->
                        callback.onError(e.getMessage())
                );
    }


    /**
     * Đăng xuất người dùng hiện tại
     * <p>
     * Thực hiện:
     * <ul>
     *     <li>Đăng xuất khỏi FirebaseAuth</li>
     *     <li>Xoá session cục bộ</li>
     * </ul>
     */
    public void logout() {
        firebaseAuth.signOut();
        sessionManager.logout();
    }


    /**
     * Kiểm tra trạng thái đăng nhập hiện tại
     *
     * @return true nếu đã đăng nhập, false nếu chưa
     */
    public boolean isLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }


    /**
     * Callback dùng cho các thao tác xác thực bất đồng bộ
     */
    public interface AuthCallback {

        /**
         * Gọi khi xác thực thành công
         *
         * @param uid UID của người dùng
         */
        void onSuccess(String uid);

        /**
         * Gọi khi xác thực thất bại
         *
         * @param message Thông báo lỗi
         */
        void onError(String message);
    }
}
