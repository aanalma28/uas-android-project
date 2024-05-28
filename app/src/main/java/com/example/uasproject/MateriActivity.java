package com.example.uasproject;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MateriActivity extends AppCompatActivity {

    TextView materi, title, content;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materi);
        back = findViewById(R.id.back);
        materi = findViewById(R.id.materi);
        title = findViewById(R.id.name_materi);
        content = findViewById(R.id.isi_materi);

        String materiIndex = getIntent().getStringExtra("materiIndex");
        String titleMateri = getIntent().getStringExtra("title");
        String contentMateri = getIntent().getStringExtra("description");

        materi.setText(materiIndex);
        title.setText(titleMateri);
        content.setText(contentMateri);

        back.setOnClickListener( v -> {
            finish();
        });
    }
}