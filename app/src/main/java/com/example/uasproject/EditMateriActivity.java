package com.example.uasproject;

import androidx.appcompat.app.AppCompatActivity;

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

public class EditMateriActivity extends AppCompatActivity {
    private EditText edtTitle, edtContent;
    private Button btnEdit;
    private ImageView btnBack;
    private ProgressBar progressBar;
    private String materi_id, title, content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_materi);

        btnBack = findViewById(R.id.back);

        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnEdit = findViewById(R.id.btn_edit);
        edtTitle = findViewById(R.id.edt_title);
        edtContent = findViewById(R.id.edt_content);
        progressBar = findViewById(R.id.progressBar);

        title = getIntent().getStringExtra("materi_title");
        content = getIntent().getStringExtra("materi_content");
        materi_id = getIntent().getStringExtra("materi_id");

        edtTitle.setText(title);
        edtContent.setText(content);

        btnEdit.setOnClickListener(v -> {
            btnEdit.setBackgroundResource(R.drawable.button_shape_off);
            btnEdit.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(getResources().getColor(R.color.bluePrimary)));

            if(TextUtils.isEmpty(edtTitle.getText()) || TextUtils.isEmpty(edtContent.getText())){
                btnEdit.setBackgroundResource(R.drawable.button_shape);
                btnEdit.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                progressBar.setIndeterminate(false);
                Toast.makeText(this, "Input Required !", Toast.LENGTH_SHORT).show();
            }else{
                try{
                    String getTitle = edtTitle.getText().toString();
                    String getContent = edtContent.getText().toString();

                    DBFirebase db = new DBFirebase();

                    db.updateMateri(materi_id, getTitle, getContent);

                    btnEdit.setBackgroundResource(R.drawable.button_shape);
                    btnEdit.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    progressBar.setIndeterminate(false);

                    Toast.makeText(this, "Edit Materi Successfully !", Toast.LENGTH_SHORT).show();
                    finish();
                }catch(Exception e){
                    btnEdit.setBackgroundResource(R.drawable.button_shape);
                    btnEdit.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    progressBar.setIndeterminate(false);

                    Toast.makeText(this, "Oopss... something seems wrong !", Toast.LENGTH_SHORT).show();
                    Log.e("EditMateri", String.valueOf(e));
                }
            }

        });

    }
}