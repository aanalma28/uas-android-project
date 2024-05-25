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

import java.text.NumberFormat;
import java.util.Locale;

public class DetailCourseActivity extends AppCompatActivity {

    TextView title, instructor, agency, price, desc;
    ImageView back, img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_course);

        String titleCourse = getIntent().getStringExtra("title_course");
        String instructorCourse = getIntent().getStringExtra("instructor");
        String agencyCourse = getIntent().getStringExtra("agency");
        String descCourse = getIntent().getStringExtra("desc");
        String priceCourse = getIntent().getStringExtra("price");
        String imgCourse = getIntent().getStringExtra("img");

        back = findViewById(R.id.back);
        title = findViewById(R.id.title_course);
        instructor = findViewById(R.id.instructor_course);
        agency = findViewById(R.id.agency_course);
        price = findViewById(R.id.price_course);
        desc = findViewById(R.id.desc_course);
        img = findViewById(R.id.img_course);

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