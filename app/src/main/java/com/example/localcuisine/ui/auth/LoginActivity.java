// ui/auth/LoginActivity.java
package com.example.localcuisine.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.localcuisine.MainActivity;
import com.example.localcuisine.R;
import com.example.localcuisine.data.auth.AuthRepository;

public class LoginActivity extends AppCompatActivity {

    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authRepository = new AuthRepository(this);

        EditText etEmail = findViewById(R.id.etUsername);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView btnGoRegister = findViewById(R.id.btnGoRegister);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                toast("Nhập đủ thông tin");
                return;
            }

            authRepository.login(
                    email,
                    password,
                    new AuthRepository.AuthCallback() {
                        @Override
                        public void onSuccess(String uid) {
                            goMain();
                        }

                        @Override
                        public void onError(String message) {
                            toast("Sai tài khoản hoặc mật khẩu");
                        }
                    }
            );
        });

        btnGoRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }

    private void goMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
