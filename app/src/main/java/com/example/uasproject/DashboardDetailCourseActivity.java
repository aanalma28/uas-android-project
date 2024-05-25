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

import com.bumptech.glide.Glide;

public class DashboardDetailCourseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_detail_course);

        String titleCourse = getIntent().getStringExtra("title_course");
        String instructorCourse = getIntent().getStringExtra("instructor");
        String agencyCourse = getIntent().getStringExtra("agency");
        String descCourse = getIntent().getStringExtra("desc");
        String priceCourse = getIntent().getStringExtra("price");
        String imgCourse = getIntent().getStringExtra("img");
        String id = getIntent().getStringExtra("course_id");

        ImageView back = findViewById(R.id.back);
        TextView title = findViewById(R.id.title_course);
        TextView instructor = findViewById(R.id.instructor_course);
        TextView agency = findViewById(R.id.agency_course);
        TextView price = findViewById(R.id.price_course);
        TextView desc = findViewById(R.id.desc_course);
        ImageView img = findViewById(R.id.img_course);
        ImageView btnEdit = findViewById(R.id.btn_edit);
        ImageView btnCreateBab = findViewById(R.id.add_bab);

        btnCreateBab.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardDetailCourseActivity.this, CreateBabActivity.class);
            startActivity(intent);
        });

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardDetailCourseActivity.this, EditCourseActivity.class);
            intent.putExtra("name", titleCourse);
            intent.putExtra("desc", descCourse);
            intent.putExtra("instructor", instructorCourse);
            intent.putExtra("price", priceCourse);
            intent.putExtra("image", imgCourse);
            intent.putExtra("course_id", id);
            startActivity(intent);
        });

        title.setText(titleCourse);
        instructor.setText(instructorCourse);
        agency.setText(agencyCourse);
        price.setText(priceCourse);
        desc.setText(descCourse);
        Glide.with(this).load(imgCourse).fitCenter().into(img);

        back.setOnClickListener(v -> {
            finish();
        });
    }
}