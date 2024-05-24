package com.example.uasproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class InfoBimbelActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_bimbel);
        ImageView back = findViewById(R.id.back);
        TextView agency = findViewById(R.id.info_nama_bimbel);
        TextView address = findViewById(R.id.info_address);
        TextView phone = findViewById(R.id.info_nohp);
        Button edit = findViewById(R.id.edit_info_bimbel);

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

        edit.setOnClickListener(v -> {
            Intent intent = new Intent(InfoBimbelActivity.this, EditInfoBimbelActivity.class);
            startActivity(intent);
        });

    }
}