package com.example.uasproject;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CreateBabActivity extends AppCompatActivity {
    private String titleCourse, instructorCourse, agencyCourse, descCourse, priceCourse, imgCourse, course_id;
    private EditText edtTitle, edtDesc;
    private Button btnCreate;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_bab);

        ImageView back = findViewById(R.id.back);

        back.setOnClickListener(v -> {
            finish();
        });

        titleCourse = getIntent().getStringExtra("title_course");
        instructorCourse = getIntent().getStringExtra("instructor");
        agencyCourse = getIntent().getStringExtra("agency");
        descCourse = getIntent().getStringExtra("desc");
        priceCourse = getIntent().getStringExtra("price");
        imgCourse = getIntent().getStringExtra("img");
        course_id = getIntent().getStringExtra("course_id");

        edtTitle = findViewById(R.id.edt_nama);
        edtDesc = findViewById(R.id.edt_tentang);
        btnCreate = findViewById(R.id.btn_simpan);
        progressBar = findViewById(R.id.progressBar);

        btnCreate.setOnClickListener(v -> {
            btnCreate.setBackgroundResource(R.drawable.button_shape_off);
            btnCreate.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(getResources().getColor(R.color.bluePrimary)));

            if(TextUtils.isEmpty(edtTitle.getText()) || TextUtils.isEmpty(edtDesc.getText())){
                btnCreate.setBackgroundResource(R.drawable.button_shape);
                btnCreate.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                progressBar.setIndeterminate(false);
                Toast.makeText(this, "Input Required !", Toast.LENGTH_SHORT).show();
            }else{
                try{
                    String title = edtTitle.getText().toString();
                    String desc = edtDesc.getText().toString();
                    DBFirebase db = new DBFirebase();

                    db.createBab(course_id, title, desc);

                    Intent intent = new Intent(CreateBabActivity.this, DashboardDetailCourseActivity.class);
                    intent.putExtra("title_course", titleCourse);
                    intent.putExtra("instructor", instructorCourse);
                    intent.putExtra("agency", agencyCourse);
                    intent.putExtra("desc", descCourse);
                    intent.putExtra("price", priceCourse);
                    intent.putExtra("img", imgCourse);
                    intent.putExtra("course_id", course_id);

                    Toast.makeText(this, "Bab successfully created", Toast.LENGTH_SHORT).show();
                    btnCreate.setBackgroundResource(R.drawable.button_shape);
                    btnCreate.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    progressBar.setIndeterminate(false);

                    startActivity(intent);
                    finish();

                }catch(Exception e){
                    btnCreate.setBackgroundResource(R.drawable.button_shape);
                    btnCreate.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    progressBar.setIndeterminate(false);

                    Log.e("CreateBabActivity", String.valueOf(e));
                    Toast.makeText(this, "Oops... Something error", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }
}