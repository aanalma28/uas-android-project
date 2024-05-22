package com.example.uasproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ImageView back = findViewById(R.id.back);
        Button createCourse = findViewById(R.id.btn_tambah_pelajaran);
        Button draft = findViewById(R.id.btn_draft);
        back.setOnClickListener(v -> {
            finish();
        });
        createCourse.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, CreateCourseActivity.class);
            startActivity(intent);
        });
        draft.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, DraftActivity.class);
            startActivity(intent);
        });
    }
}