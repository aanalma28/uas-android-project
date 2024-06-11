package com.example.uasproject.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uasproject.R;
import com.example.uasproject.models.Materi;
import com.example.uasproject.utils.DBFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class MyCourseMateriActivity extends AppCompatActivity {

    Button prev, next;
    TextView materi, title, content;
    ImageView back;
    private ArrayList<Materi> materiList;
    private int position, page;
    private String course_id, bab_id, titleBab, descBab;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_course_materi);
        back = findViewById(R.id.back);
        materi = findViewById(R.id.materi);
        title = findViewById(R.id.name_materi);
        content = findViewById(R.id.isi_materi);
        prev = findViewById(R.id.prev);
        next = findViewById(R.id.next);

        course_id = getIntent().getStringExtra("course_id");
        bab_id = getIntent().getStringExtra("bab_id");
        titleBab = getIntent().getStringExtra("title");
        descBab = getIntent().getStringExtra("description");

        materiList = getIntent().getParcelableArrayListExtra("materi_list");
        String materiIndex = getIntent().getStringExtra("materiIndex");
        page = getIntent().getIntExtra("page", 0);
        position = getIntent().getIntExtra("position", 0);
        Materi materiData = materiList.get(position);

        String titleMateri = materiData.getTitle();
        String contentMateri = materiData.getContent();

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

        prev.setOnClickListener(v -> {
            Intent intent = new Intent(MyCourseMateriActivity.this, MyCourseMateriActivity.class);
            intent.putParcelableArrayListExtra("materi_list", materiList);
            intent.putExtra("page", page-1);
            intent.putExtra("materiIndex", "Materi " + (page-1));
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
            Intent intent = new Intent(MyCourseMateriActivity.this, MyCourseMateriActivity.class);
            intent.putParcelableArrayListExtra("materi_list", materiList);
            intent.putExtra("page", page+1);
            intent.putExtra("materiIndex", "Materi " + (page+1));
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