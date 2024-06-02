package com.example.uasproject;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MateriActivity extends AppCompatActivity {
    Button prev, next;
    TextView materi, title, content;
    ImageView back;
    private ArrayList<Materi> materiList;
    private int position, page;
    private String materiIndex, titleMateri, contentMateri;
    private String course_id, bab_id, titleBab, descBab;
    private ImageView edit, delete;
    private Materi materiData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materi);
        back = findViewById(R.id.back);
        materi = findViewById(R.id.materi);
        title = findViewById(R.id.name_materi);
        content = findViewById(R.id.isi_materi);
        prev = findViewById(R.id.prev);
        next = findViewById(R.id.next);
        edit = findViewById(R.id.btn_edit);
        delete = findViewById(R.id.btn_delete);

        course_id = getIntent().getStringExtra("course_id");
        bab_id = getIntent().getStringExtra("bab_id");
        titleBab = getIntent().getStringExtra("title");
        descBab = getIntent().getStringExtra("description");

        materiList = getIntent().getParcelableArrayListExtra("materi_list");
        materiIndex = getIntent().getStringExtra("materiIndex");
        page = getIntent().getIntExtra("page", 0);
        position = getIntent().getIntExtra("position", 0);
        materiData = materiList.get(position);

        titleMateri = materiData.getTitle();
        contentMateri = materiData.getContent();

        materi.setText(materiIndex);
        title.setText(titleMateri);
        content.setText(contentMateri);

        if(position == 0 && (materiList.size()-1) != 0){
            prev.setVisibility(View.GONE);
        }else if((materiList.size()-1) == position && (materiList.size()-1) != 0){
            next.setVisibility(View.GONE);
        }else if(materiList.size() == 1){
            prev.setVisibility(View.GONE);
            next.setVisibility(View.GONE);
        }

        back.setOnClickListener(v -> {
            finish();
        });

        edit.setOnClickListener(v -> {
            String id = materiData.getMateri_id();
            Intent intent = new Intent(this, EditMateriActivity.class);
            intent.putExtra("materi_title", titleMateri);
            intent.putExtra("materi_content", contentMateri);
            intent.putExtra("materi_id", id);

            startActivity(intent);
            finish();
        });

        delete.setOnClickListener(v -> {
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

            alertDesc.setText("Yakin hapus materi ini ?");

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

                db.deleteMateri(materiData.getMateri_id(), new OnCompleteListener<Void>() {
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
                            Toast.makeText(MateriActivity.this, "Oops... Sepertinya ada yang salah", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }
                    }
                });
            });
            alertDialog.show();
        });

        prev.setOnClickListener(v -> {
            Intent intent = new Intent(MateriActivity.this, MateriActivity.class);
            intent.putParcelableArrayListExtra("materi_list", materiList);
            intent.putExtra("page", page-1);
            intent.putExtra("materiIndex", "Materi " + (page-1));
            intent.putExtra("position", position-1);

            intent.putExtra("course_id", course_id);
            intent.putExtra("bab_id", bab_id);
            intent.putExtra("title", titleBab);
            intent.putExtra("description", descBab);

            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        next.setOnClickListener(v -> {
            Intent intent = new Intent(MateriActivity.this, MateriActivity.class);
            intent.putParcelableArrayListExtra("materi_list", materiList);
            intent.putExtra("page", page+1);
            intent.putExtra("materiIndex", "Materi " + (page+1));
            intent.putExtra("position", position+1);

            intent.putExtra("course_id", course_id);
            intent.putExtra("bab_id", bab_id);
            intent.putExtra("title", titleBab);
            intent.putExtra("description", descBab);

            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }
}