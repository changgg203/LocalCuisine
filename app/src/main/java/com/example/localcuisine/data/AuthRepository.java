// data/AuthRepository.java
package com.example.localcuisine.data;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthRepository {

    private final FirebaseAuth firebaseAuth;
    private final SessionManager sessionManager;

    public AuthRepository(@NonNull Context context) {
        firebaseAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(context);
    }

    // ================= REGISTER =================

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

    // ================= LOGIN =================

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

    // ================= LOGOUT =================

    public void logout() {
        firebaseAuth.signOut();
        sessionManager.logout();
    }

    // ================= STATE =================

    public boolean isLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    // ================= CALLBACK =================

    public interface AuthCallback {
        void onSuccess(String uid);

        void onError(String message);
    }
}
