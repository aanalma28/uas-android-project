package com.example.uasproject.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uasproject.R;
import com.example.uasproject.RecycleViewInterface;
import com.example.uasproject.adapters.MateriAdapter;
import com.example.uasproject.models.Materi;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyCourseBabActivity extends AppCompatActivity implements RecycleViewInterface {
    private String course_id, bab_id, titleBab, descBab;
    private final ArrayList<Materi> materiList = new ArrayList<>();

    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_course_bab);
        ImageView back = findViewById(R.id.back);
        TextView title = findViewById(R.id.title_bab);
        TextView desc = findViewById(R.id.desc_bab);

        course_id = getIntent().getStringExtra("course_id");
        bab_id = getIntent().getStringExtra("bab_id");
        titleBab = getIntent().getStringExtra("title");
        descBab = getIntent().getStringExtra("description");

        title.setText(titleBab);
        desc.setText(descBab);

        back.setOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.recycleView_materi);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MateriAdapter materiAdapter = new MateriAdapter(materiList, this);
        recyclerView.setAdapter(materiAdapter);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("materi");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                materiList.clear();
                for (DataSnapshot materiSnapshot : snapshot.getChildren()) {
                    Materi materi = materiSnapshot.getValue(Materi.class);
                    if (materi != null && materi.getBab_id().equals(bab_id)){
                        materiList.add(materi);
                    }
                }
                materiAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyCourseBabActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, MyCourseMateriActivity.class);
        intent.putExtra("materiIndex", "Materi " + (position + 1) );
        intent.putExtra("position", position);
        intent.putParcelableArrayListExtra("materi_list", materiList);

        intent.putExtra("course_id", course_id);
        intent.putExtra("bab_id", bab_id);
        intent.putExtra("page", position+1);
        intent.putExtra("title", titleBab);
        intent.putExtra("description", descBab);

        startActivity(intent);
    }
}