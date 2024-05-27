package com.example.uasproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class BabActivity extends AppCompatActivity {
    private String course_id, bab_id, titleBab, descBab;
    private ImageView addMateri;
    private List<Materi> materiList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bab);
        ImageView back = findViewById(R.id.back);
        TextView title = findViewById(R.id.title_bab);
        TextView desc = findViewById(R.id.desc_bab);

        course_id = getIntent().getStringExtra("course_id");
        bab_id = getIntent().getStringExtra("bab_id");
        titleBab = getIntent().getStringExtra("title");
        descBab = getIntent().getStringExtra("description");
        addMateri = findViewById(R.id.add_materi);

        title.setText(titleBab);
        desc.setText(descBab);

        back.setOnClickListener(v -> {
            finish();
        });

        addMateri.setOnClickListener(v -> {
            Intent intent = new Intent(BabActivity.this, CreateMateriActivity.class);
            intent.putExtra("course_id", course_id);
            intent.putExtra("bab_id", bab_id);
            intent.putExtra("title", titleBab);
            intent.putExtra("description", descBab);
            startActivity(intent);

        });

//        RecyclerView recyclerView = findViewById(R.id.recycleView_materi);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        materiList = new ArrayList<>();
//        MateriA babAdapter = new BabAdapter(materiList, this);
//        recyclerView.setAdapter(babAdapter);
//
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("babs");
    }
}