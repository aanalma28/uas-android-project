package com.example.uasproject.activities;

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

import androidx.appcompat.app.AppCompatActivity;

import com.example.uasproject.utils.DBFirebase;
import com.example.uasproject.R;

public class CreateMateriActivity extends AppCompatActivity {
    private String course_id, bab_id;
    private EditText edtTitle, edtContent;
    private Button btnCreate;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_materi);

        ImageView back = findViewById(R.id.back);

        back.setOnClickListener(v -> {
            finish();
        });

        course_id = getIntent().getStringExtra("course_id");
        bab_id = getIntent().getStringExtra("bab_id");

        edtTitle = findViewById(R.id.edt_nama);
        edtContent = findViewById(R.id.edt_isi);
        btnCreate = findViewById(R.id.btn_simpan);
        progressBar = findViewById(R.id.progressBar);

        btnCreate.setOnClickListener(v -> {
            btnCreate.setBackgroundResource(R.drawable.button_shape_off);
            btnCreate.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(getResources().getColor(R.color.bluePrimary)));

            if(TextUtils.isEmpty(edtTitle.getText()) || TextUtils.isEmpty(edtContent.getText())){
                btnCreate.setBackgroundResource(R.drawable.button_shape);
                btnCreate.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                progressBar.setIndeterminate(false);
                Toast.makeText(this, "Input Required !", Toast.LENGTH_SHORT).show();
            }else{
                try{
                    String title = edtTitle.getText().toString();
                    String content = edtContent.getText().toString();

                    DBFirebase db = new DBFirebase();

                    db.createMateri(bab_id, course_id, title, content);

                    btnCreate.setBackgroundResource(R.drawable.button_shape);
                    btnCreate.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    progressBar.setIndeterminate(false);

                    Toast.makeText(this, "Materi successfully created", Toast.LENGTH_SHORT).show();
                    finish();

                }catch (Exception e){
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