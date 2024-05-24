package com.example.uasproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EditInfoBimbelActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
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
    }
}