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

import java.text.NumberFormat;
import java.util.Locale;

public class DetailCourseActivity extends AppCompatActivity {

    TextView title, instructor, agency, price, desc;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_course);

        String titleCourse = getIntent().getStringExtra("title_course");
        String instructorCourse = getIntent().getStringExtra("instructor");
        String agencyCourse = getIntent().getStringExtra("agency");
        String descCourse = getIntent().getStringExtra("desc");
        String priceCourse = getIntent().getStringExtra("price");

        back = findViewById(R.id.back);
        title = findViewById(R.id.title_detail_course);
        instructor = findViewById(R.id.instructor_detail_course);
        agency = findViewById(R.id.agency_detail_course);
        price = findViewById(R.id.price_detail);
        desc = findViewById(R.id.desc_detail);

        title.setText(titleCourse);
        instructor.setText(instructorCourse);
        agency.setText(agencyCourse);
        price.setText(priceCourse);
        desc.setText(descCourse);

        back.setOnClickListener(v -> {
            Intent intent = new Intent(DetailCourseActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}