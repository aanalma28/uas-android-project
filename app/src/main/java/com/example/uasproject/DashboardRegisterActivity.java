package com.example.uasproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DashboardRegisterActivity extends AppCompatActivity {

    Button btn;
    ImageView back;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_register);
        btn = findViewById(R.id.btn_regSeller);
        back = findViewById(R.id.back);
        btn.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardRegisterActivity.this, EmailVerificationActivity.class);
            startActivity(intent);
        });
        back.setOnClickListener(v -> {
            finish();
        });
    }
}