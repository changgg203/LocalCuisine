package com.example.localcuisine.ui.auth;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.localcuisine.R;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText edtEmail;
    private Button btnSubmit;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        bindViews();
        bindActions();
    }

    private void bindViews() {
        edtEmail = findViewById(R.id.edtEmail);
        btnSubmit = findViewById(R.id.btnSubmit);
        progressBar = findViewById(R.id.progressBar);
    }

    private void bindActions() {
        btnSubmit.setOnClickListener(v -> submit());
    }

    private void submit() {
        String email = edtEmail.getText().toString().trim();

        if (email.isEmpty()) {
            edtEmail.setError("Email không được để trống");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email không hợp lệ");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);


        edtEmail.postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            btnSubmit.setEnabled(true);

            Toast.makeText(
                    this,
                    "Vui lòng kiểm tra email của bạn.",
                    Toast.LENGTH_LONG
            ).show();

            finish(); // quay lại login
        }, 1200);
    }
}
