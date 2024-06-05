package com.example.uasproject.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uasproject.R;
import com.example.uasproject.RecycleViewInterface;
import com.example.uasproject.adapters.BabAdapter;
import com.example.uasproject.models.Bab;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetailCourseActivity extends AppCompatActivity implements RecycleViewInterface {

    TextView title, instructor, agency, price, desc;
    ImageView back, img;
    List<Bab> babList;
    private String course_id, priceForMidtrans;
    Button beli;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_course);

        course_id = getIntent().getStringExtra("course_id");
        String titleCourse = getIntent().getStringExtra("title_course");
        String instructorCourse = getIntent().getStringExtra("instructor");
        String agencyCourse = getIntent().getStringExtra("agency");
        String descCourse = getIntent().getStringExtra("desc");
        String priceCourse = getIntent().getStringExtra("price");
        String imgCourse = getIntent().getStringExtra("img");
        priceForMidtrans = getIntent().getStringExtra("price_for_midtrans");


        back = findViewById(R.id.back);
        title = findViewById(R.id.title_course);
        instructor = findViewById(R.id.instructor_course);
        agency = findViewById(R.id.agency_course);
        price = findViewById(R.id.price_course);
        desc = findViewById(R.id.desc_course);
        img = findViewById(R.id.img_course);
        beli = findViewById(R.id.btn_beli);

        title.setText(titleCourse);
        instructor.setText(instructorCourse);
        agency.setText(agencyCourse);
        price.setText(priceCourse);
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
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
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
                Toast.makeText(DetailCourseActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });

        beli.setOnClickListener(v -> {
            Intent intent = new Intent(DetailCourseActivity.this, OrderActivity.class);
            intent.putExtra("title_course", titleCourse);
            intent.putExtra("agency_course", agencyCourse);
            intent.putExtra("price_course", priceCourse);
            intent.putExtra("price_for_midtrans", priceForMidtrans.toString());
            startActivity(intent);
        });

    }
    @Override
    public void onItemClick(int position) {
        try{
            Bab clickedBab = babList.get(position);

            Intent intent = new Intent(this, DetailBabActivity.class);
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