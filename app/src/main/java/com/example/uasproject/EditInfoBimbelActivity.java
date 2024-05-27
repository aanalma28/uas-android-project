package com.example.uasproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditInfoBimbelActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    private Button btnEdit;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info_bimbel);
        ImageView back = findViewById(R.id.back);
        EditText agency = findViewById(R.id.info_nama_bimbel);
        EditText address = findViewById(R.id.info_address);
        EditText phone = findViewById(R.id.info_nohp);

        sharedPreferences = getSharedPreferences("myPref", MODE_PRIVATE);

        String agencySharedPreferences = sharedPreferences.getString("agency", "");
        String addressSharedPreferences = sharedPreferences.getString("address", "");
        String phoneSharedPreferences = sharedPreferences.getString("phone", "");
        agency.setText(agencySharedPreferences);
        address.setText(addressSharedPreferences);
        phone.setText(phoneSharedPreferences);

        back.setOnClickListener(v -> {
            finish();
        });

        btnEdit = findViewById(R.id.edit_info_bimbel);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user= mAuth.getCurrentUser();

        btnEdit.setOnClickListener(v -> {
            String agencyTxt = agency.getText().toString();
            String addressTxt = address.getText().toString();
            String phoneTxt = phone.getText().toString();
            String id = user.getUid().toString();

            btnEdit.setBackgroundResource(R.drawable.button_shape_off);
            btnEdit.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(getResources().getColor(R.color.bluePrimary)));

            if(TextUtils.isEmpty(agencyTxt) || TextUtils.isEmpty(addressTxt) || TextUtils.isEmpty(phoneTxt)){
                btnEdit.setBackgroundResource(R.drawable.button_shape);
                btnEdit.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                progressBar.setIndeterminate(false);

                Toast.makeText(this, "Field Required !", Toast.LENGTH_SHORT).show();
            }else{
                try{
                    DBFirebase db = new DBFirebase();

                    db.updateUserSeller(id, agencyTxt, phoneTxt, addressTxt);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("agency", agencyTxt);
                    editor.putString("address", addressTxt);
                    editor.putString("phone", phoneTxt);
                    editor.apply();

                    btnEdit.setBackgroundResource(R.drawable.button_shape);
                    btnEdit.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    progressBar.setIndeterminate(false);

                    Intent intent = new Intent(EditInfoBimbelActivity.this, MainActivity.class);
                    Toast.makeText(this, "Edit Profile Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                }catch(Exception e){
                    btnEdit.setBackgroundResource(R.drawable.button_shape);
                    btnEdit.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    progressBar.setIndeterminate(false);

                    Toast.makeText(this, "Oops... Something Error", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }
}