package com.example.localcuisine.data.auth;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

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

    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                        createInitialUserDoc(user.getUid(), user.getEmail());
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


    public boolean isLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    /**
     * Tạo document users/{uid} lần đầu khi đăng ký (để admin list users hoạt động).
     */
    private void createInitialUserDoc(String uid, String email) {
        Map<String, Object> data = new HashMap<>();
        data.put("email", email != null ? email : "");
        data.put("displayName", "");
        data.put("isAdmin", false);
        data.put("updatedAt", FieldValue.serverTimestamp());
        db.collection("users")
                .document(uid)
                .set(data, SetOptions.merge())
                .addOnFailureListener(e -> android.util.Log.e("AuthRepository", "createInitialUserDoc failed", e));
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

    /**
     * Callback cho thao tác đổi mật khẩu
     */
    public interface PasswordChangeCallback {
        void onSuccess();
        void onError(String message);
    }

    /**
     * Đổi mật khẩu cho user hiện tại (cần reauthenticate)
     */
    public void changePassword(@NonNull String email, @NonNull String currentPassword, @NonNull String newPassword, @NonNull PasswordChangeCallback callback) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            callback.onError("Not logged in");
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);
        user.reauthenticate(credential)
                .addOnSuccessListener(aVoid ->
                        user.updatePassword(newPassword)
                                .addOnSuccessListener(v -> callback.onSuccess())
                                .addOnFailureListener(e -> callback.onError(e.getMessage()))
                )
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}
