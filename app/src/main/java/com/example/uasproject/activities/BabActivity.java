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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uasproject.utils.DBFirebase;
import com.example.uasproject.R;
import com.example.uasproject.RecycleViewInterface;
import com.example.uasproject.adapters.MateriAdapter;
import com.example.uasproject.models.Materi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

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
            TextView alertDesc = view.findViewById(R.id.alertDesc);

            alertDesc.setText("Jika bab dihapus maka materi dan quiz yang ada di bab ini otomatis akan dihapus. Yakin hapus ?");

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
                DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();

                // Flag to track if any delete operation fails
                AtomicBoolean deleteFailed = new AtomicBoolean(false);

                dbReference.child("materi").orderByChild("bab_id").equalTo(bab_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            if(dataSnapshot.exists()){
                                dataSnapshot.getRef().removeValue().addOnSuccessListener(aVoid -> {
                                    Log.d("DeleteMateri", "Delete materi successfull");
                                }).addOnFailureListener(e -> {
                                    Log.e("DeleteMateri", String.valueOf(e));
                                    deleteFailed.set(true);  // Set the flag to true if deletion fails
                                    // Show error message and stop further execution
                                    Toast.makeText(BabActivity.this, "Gagal menghapus materi", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    layoutDialog.setVisibility(View.GONE);
                                });
                                if (deleteFailed.get()) break; // Stop if any delete operation fails
                            }
                        }

                        if(!deleteFailed.get()){
                            db.deleteBab(bab_id, new OnCompleteListener<Void>() {
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
                                        Toast.makeText(BabActivity.this, "Oops... Sepertinya ada yang salah", Toast.LENGTH_SHORT).show();
                                        alertDialog.dismiss();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("DBDeleteError", "Failed to read data.", error.toException());
                        Toast.makeText(BabActivity.this, "Gagal membaca data bab", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        layoutDialog.setVisibility(View.GONE);
                    }
                });
            });
            alertDialog.show();
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
        intent.putExtra("page", position+1);
        intent.putExtra("title", titleBab);
        intent.putExtra("description", descBab);

        startActivity(intent);
    }

}