package com.example.uasproject.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
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
import com.example.uasproject.R;
import com.example.uasproject.RecycleViewInterface;
import com.example.uasproject.adapters.BabAdapter;
import com.example.uasproject.models.Bab;
import com.example.uasproject.utils.DBFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class DetailMyCourseActivity extends AppCompatActivity implements RecycleViewInterface {
    private String course_id;
    private List<Bab> babList;

    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_my_course);
        String titleCourse = getIntent().getStringExtra("title_course");
        String instructorCourse = getIntent().getStringExtra("instructor");
        String agencyCourse = getIntent().getStringExtra("agency");
        String descCourse = getIntent().getStringExtra("desc");
        String imgCourse = getIntent().getStringExtra("img");
        course_id = getIntent().getStringExtra("course_id");

        ImageView back = findViewById(R.id.back);
        TextView title = findViewById(R.id.title_course);
        TextView instructor = findViewById(R.id.instructor_course);
        TextView agency = findViewById(R.id.agency_course);
        TextView desc = findViewById(R.id.desc_course);
        ImageView img = findViewById(R.id.img_course);

        title.setText(titleCourse);
        instructor.setText(instructorCourse);
        agency.setText(agencyCourse);
        desc.setText(descCourse);
        Glide.with(this).load(imgCourse).fitCenter().into(img);

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
                Toast.makeText(DetailMyCourseActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onItemClick(int position) {
        try{
            Bab clickedBab = babList.get(position);

            Intent intent = new Intent(this, MyCourseBabActivity.class);
            intent.putExtra("course_id", course_id);
            intent.putExtra("bab_id", clickedBab.getBab_id());
            intent.putExtra("title", clickedBab.getName());
            intent.putExtra("description", clickedBab.getDetail());
            startActivity(intent);
        }catch(Exception e){
            Log.e("BabClick", String.valueOf(e));
        }
    }
}