// ui/auth/RegisterActivity.java
package com.example.localcuisine.ui.auth;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.localcuisine.R;
import com.example.localcuisine.data.AuthRepository;

public class RegisterActivity extends AppCompatActivity {

    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authRepository = new AuthRepository(this);

        EditText etEmail = findViewById(R.id.etUsername); // dùng email
        EditText etPassword = findViewById(R.id.etPassword);
        EditText etConfirm = findViewById(R.id.etConfirmPassword);
        Button btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();
            String confirm = etConfirm.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                toast("Nhập đủ thông tin");
                return;
            }

            if (!password.equals(confirm)) {
                toast("Mật khẩu không khớp");
                return;
            }

            authRepository.register(
                    email,
                    password,
                    new AuthRepository.AuthCallback() {
                        @Override
                        public void onSuccess(String uid) {
                            toast("Đăng ký thành công");
                            finish(); // quay về login
                        }

                        @Override
                        public void onError(String message) {
                            toast(message);
                        }
                    }
            );
        });
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
