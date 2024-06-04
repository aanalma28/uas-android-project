package com.example.uasproject.activities;

import androidx.appcompat.app.AppCompatActivity;

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

import com.example.uasproject.utils.DBFirebase;
import com.example.uasproject.R;

public class EditBabActivity extends AppCompatActivity {
    private EditText edtTitle, edtDesc;
    private Button btnEdit;
    private ImageView btnBack;
    private ProgressBar progressBar;
    private String bab_id, titleBab, descBab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bab);

        btnBack= findViewById(R.id.back);

        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnEdit = findViewById(R.id.btn_edit);
        edtTitle = findViewById(R.id.edt_title);
        edtDesc = findViewById(R.id.edt_description);
        progressBar = findViewById(R.id.progressBar);

        bab_id = getIntent().getStringExtra("bab_id");
        titleBab = getIntent().getStringExtra("title");
        descBab = getIntent().getStringExtra("description");

        edtTitle.setText(titleBab);
        edtDesc.setText(descBab);

        btnEdit.setOnClickListener(v -> {
            btnEdit.setBackgroundResource(R.drawable.button_shape_off);
            btnEdit.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(getResources().getColor(R.color.bluePrimary)));

            if(TextUtils.isEmpty(edtTitle.getText()) || TextUtils.isEmpty(edtDesc.getText())){
                btnEdit.setBackgroundResource(R.drawable.button_shape);
                btnEdit.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                progressBar.setIndeterminate(false);
                Toast.makeText(this, "Input Required !", Toast.LENGTH_SHORT).show();
            }else{
                try{
                    String getTitle = edtTitle.getText().toString();
                    String getDescription = edtDesc.getText().toString();

                    DBFirebase db = new DBFirebase();

                    db.updateBab(bab_id, getTitle, getDescription);

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