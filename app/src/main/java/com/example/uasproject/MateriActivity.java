package com.example.uasproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MateriActivity extends AppCompatActivity {
    Button prev, next;
    TextView materi, title, content;
    ImageView back;
    private ArrayList<Materi> materiList;
    private int position;
    private String materiIndex, titleMateri, contentMateri;
    private String course_id, bab_id, titleBab, descBab;
    private ImageView edit, delete;
    private Materi materiData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materi);
        back = findViewById(R.id.back);
        materi = findViewById(R.id.materi);
        title = findViewById(R.id.name_materi);
        content = findViewById(R.id.isi_materi);
        prev = findViewById(R.id.prev);
        next = findViewById(R.id.next);
        edit = findViewById(R.id.btn_edit);
        delete = findViewById(R.id.btn_delete);

        course_id = getIntent().getStringExtra("course_id");
        bab_id = getIntent().getStringExtra("bab_id");
        titleBab = getIntent().getStringExtra("title");
        descBab = getIntent().getStringExtra("description");

        materiList = getIntent().getParcelableArrayListExtra("materi_list");
        materiIndex = getIntent().getStringExtra("materiIndex");
        position = getIntent().getIntExtra("position", 0);
        materiData = materiList.get(position);

        titleMateri = materiData.getTitle();
        contentMateri = materiData.getContent();

        materi.setText(materiIndex);
        title.setText(titleMateri);
        content.setText(contentMateri);

        if(position == 0 && (materiList.size()-1) != 0){
            prev.setVisibility(View.GONE);
        }else if((materiList.size()-1) == position && (materiList.size()-1) != 0){
            next.setVisibility(View.GONE);
        }else if(materiList.size() == 1){
            prev.setVisibility(View.GONE);
            next.setVisibility(View.GONE);
        }

        back.setOnClickListener(v -> {
            finish();
        });

        edit.setOnClickListener(v -> {
            String id = materiData.getMateri_id();
            Intent intent = new Intent(this, EditMateriActivity.class);
            intent.putExtra("materi_title", titleMateri);
            intent.putExtra("materi_content", contentMateri);
            intent.putExtra("materi_id", id);

            startActivity(intent);
            finish();
        });

        prev.setOnClickListener(v -> {
            Intent intent = new Intent(MateriActivity.this, MateriActivity.class);
            intent.putParcelableArrayListExtra("materi_list", materiList);
            intent.putExtra("materiIndex", "Materi " + (position+1));
            intent.putExtra("position", position-1);

            intent.putExtra("course_id", course_id);
            intent.putExtra("bab_id", bab_id);
            intent.putExtra("title", titleBab);
            intent.putExtra("description", descBab);

            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        next.setOnClickListener(v -> {
            Intent intent = new Intent(MateriActivity.this, MateriActivity.class);
            intent.putParcelableArrayListExtra("materi_list", materiList);
            intent.putExtra("materiIndex", "Materi " + (position+1));
            intent.putExtra("position", position+1);

            intent.putExtra("course_id", course_id);
            intent.putExtra("bab_id", bab_id);
            intent.putExtra("title", titleBab);
            intent.putExtra("description", descBab);

            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }
}