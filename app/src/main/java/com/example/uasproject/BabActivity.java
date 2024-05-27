package com.example.uasproject;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BabActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bab);
        ImageView back = findViewById(R.id.back);
        TextView title = findViewById(R.id.title_bab);
        TextView desc = findViewById(R.id.desc_bab);

        String titleBab = getIntent().getStringExtra("title");
        String descBab = getIntent().getStringExtra("description");

        title.setText(titleBab);
        desc.setText(descBab);

        back.setOnClickListener(v -> {
            finish();
        });
    }
}