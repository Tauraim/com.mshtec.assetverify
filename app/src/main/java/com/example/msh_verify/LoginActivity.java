package com.example.msh_verify;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText username, password;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.txtUsername);
        password = findViewById(R.id.txtPassword);
        login = findViewById(R.id.btnLogin);
//Manipulation of login data
        login.setOnClickListener(v -> {
            String userStr = username.getText() != null ? username.getText().toString().trim() : "";
            String passStr = password.getText() != null ? password.getText().toString().trim() : "";

            if (userStr.isEmpty() || passStr.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(LoginActivity.this, Dashboard.class);
            intent.putExtra("USERNAME", userStr);
            startActivity(intent);
            finish();
        });
    }
}
