package com.example.uasproject;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BabActivity extends AppCompatActivity implements RecycleViewInterface {
    private String course_id, bab_id, titleBab, descBab;
    private ImageView addMateri, btnEdit, btnDelete;
    private ArrayList<Materi> materiList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bab);
        ImageView back = findViewById(R.id.back);
        TextView title = findViewById(R.id.title_bab);
        TextView desc = findViewById(R.id.desc_bab);
        btnEdit = findViewById(R.id.btn_edit);
        btnDelete = findViewById(R.id.btn_delete);

        course_id = getIntent().getStringExtra("course_id");
        bab_id = getIntent().getStringExtra("bab_id");
        titleBab = getIntent().getStringExtra("title");
        descBab = getIntent().getStringExtra("description");
        addMateri = findViewById(R.id.add_materi);

        title.setText(titleBab);
        desc.setText(descBab);

        back.setOnClickListener(v -> finish());

        addMateri.setOnClickListener(v -> {
            Intent intent = new Intent(BabActivity.this, CreateMateriActivity.class);
            intent.putExtra("course_id", course_id);
            intent.putExtra("bab_id", bab_id);
            intent.putExtra("title", titleBab);
            intent.putExtra("description", descBab);
            startActivity(intent);

        });

//        RecyclerView recyclerView = findViewById(R.id.recycleView_materi);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        materiList = new ArrayList<>();
//        MateriA babAdapter = new BabAdapter(materiList, this);
//        recyclerView.setAdapter(babAdapter);
//
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("babs");

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
                Toast.makeText(BabActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditBabActivity.class);
            intent.putExtra("bab_id", bab_id);
            intent.putExtra("title", titleBab);
            intent.putExtra("description", descBab);

            startActivity(intent);
            finish();
        });

    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, MateriActivity.class);
        intent.putExtra("materiIndex", "Materi " + (position + 1) );
        intent.putExtra("position", position);
        intent.putParcelableArrayListExtra("materi_list", materiList);

        intent.putExtra("course_id", course_id);
        intent.putExtra("bab_id", bab_id);
        intent.putExtra("title", titleBab);
        intent.putExtra("description", descBab);

        startActivity(intent);
    }

}