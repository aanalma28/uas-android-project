package com.example.uasproject;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DashboardDetailCourseActivity extends AppCompatActivity implements RecycleViewInterface {
    private String titleCourse, instructorCourse, agencyCourse, descCourse, priceCourse, imgCourse, course_id;
    private List<Bab> babList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_detail_course);

        titleCourse = getIntent().getStringExtra("title_course");
        instructorCourse = getIntent().getStringExtra("instructor");
        agencyCourse = getIntent().getStringExtra("agency");
        descCourse = getIntent().getStringExtra("desc");
        priceCourse = getIntent().getStringExtra("price");
        imgCourse = getIntent().getStringExtra("img");
        course_id = getIntent().getStringExtra("course_id");

        ImageView back = findViewById(R.id.back);
        TextView title = findViewById(R.id.title_course);
        TextView instructor = findViewById(R.id.instructor_course);
        TextView agency = findViewById(R.id.agency_course);
        TextView price = findViewById(R.id.price_course);
        TextView desc = findViewById(R.id.desc_course);
        ImageView img = findViewById(R.id.img_course);
        ImageView btnEdit = findViewById(R.id.btn_edit);
        ImageView btnDelete = findViewById(R.id.btn_delete);
        ImageView btnCreateBab = findViewById(R.id.add_bab);

        btnCreateBab.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardDetailCourseActivity.this, CreateBabActivity.class);
            intent.putExtra("title_course", titleCourse);
            intent.putExtra("instructor", instructorCourse);
            intent.putExtra("agency", agencyCourse);
            intent.putExtra("desc", descCourse);
            intent.putExtra("price", priceCourse);
            intent.putExtra("img", imgCourse);
            intent.putExtra("course_id", course_id);
            startActivity(intent);
        });

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardDetailCourseActivity.this, EditCourseActivity.class);
            intent.putExtra("name", titleCourse);
            intent.putExtra("desc", descCourse);
            intent.putExtra("instructor", instructorCourse);
            intent.putExtra("price", priceCourse);
            intent.putExtra("image", imgCourse);
            intent.putExtra("course_id", course_id);
            startActivity(intent);
        });

        title.setText(titleCourse);
        instructor.setText(instructorCourse);
        agency.setText(agencyCourse);
        price.setText(priceCourse);
        desc.setText(descCourse);
        Glide.with(this).load(imgCourse).fitCenter().into(img);

        btnDelete.setOnClickListener(v -> {
            ConstraintLayout alertConstrainLayout = findViewById(R.id.alert_constrain_layout);
            View view = LayoutInflater.from(this).inflate(R.layout.alert_dialog, alertConstrainLayout, false);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(view);

            final AlertDialog alertDialog = builder.create();

            ConstraintLayout successConstrainLayout = findViewById(R.id.success_constrain_layout);
            View viewSuccess = LayoutInflater.from(this).inflate(R.layout.success_dialog, successConstrainLayout, false);

            AlertDialog.Builder builderSuccess = new AlertDialog.Builder(this);
            builderSuccess.setView(viewSuccess);

            final AlertDialog alertDialogSuccess = builderSuccess.create();

            Button btnNo = view.findViewById(R.id.alertNo);
            Button btnDone = view.findViewById(R.id.alertDone);
            LinearLayout wrapper = view.findViewById(R.id.layout_loading);
            ConstraintLayout layoutDialog = view.findViewById(R.id.layout_dialog);
            ProgressBar progressBar = view.findViewById(R.id.progressBar);

            btnNo.setOnClickListener(v1 -> {
                alertDialog.dismiss();
            });

            btnDone.setOnClickListener(v1 -> {
                wrapper.setVisibility(View.VISIBLE);
                layoutDialog.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setIndeterminate(true);
                progressBar.setIndeterminateTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.bluePrimary)));

                DBFirebase db = new DBFirebase();
                db.deleteCourse(course_id, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Button tutup = viewSuccess.findViewById(R.id.successDone);

                            tutup.setOnClickListener(v2 -> {
                                alertDialogSuccess.dismiss();
                                finish();
                            });

                            alertDialog.dismiss();
                            alertDialogSuccess.show();

                        }else{
                            Toast.makeText(DashboardDetailCourseActivity.this, "Oops... Sepertinya ada yang salah", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }
                    }
                });
            });

            alertDialog.show();
        });

        back.setOnClickListener(v -> {
            finish();
        });

        RecyclerView recyclerView = findViewById(R.id.recycleView_bab);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        babList = new ArrayList<>();
        BabAdapter babAdapter = new BabAdapter(babList, this);
        recyclerView.setAdapter(babAdapter);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("babs");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                babList.clear();
                for (DataSnapshot babSnapshot : snapshot.getChildren()) {
                    Bab bab = babSnapshot.getValue(Bab.class);
                    if (bab != null && bab.getCourse_id().equals(course_id)){
                        babList.add(bab);
                    }
                }
                babAdapter.notifyDataSetChanged();
                TextView jumlahBab = findViewById(R.id.jumlah_bab);
                jumlahBab.setText("Jumlah Bab : " + babList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardDetailCourseActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onItemClick(int position) {
        Bab clickedBab = babList.get(position);

        Intent intent = new Intent(this, BabActivity.class);
        intent.putExtra("course_id", course_id);
        intent.putExtra("bab_id", clickedBab.getBab_id());
        intent.putExtra("title", clickedBab.getName());
        intent.putExtra("description", clickedBab.getDetail());
        startActivity(intent);
    }
}