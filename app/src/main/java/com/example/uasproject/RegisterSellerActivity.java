package com.example.uasproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterSellerActivity extends AppCompatActivity {
    private EditText nama_bimbel, alamat, no_hp;
    private Button btn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_seller);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nama_bimbel = findViewById(R.id.edt_nama_bimbel);
        alamat = findViewById(R.id.edt_alamat);
        no_hp = findViewById(R.id.edt_nohp);

        btn = findViewById(R.id.btn_lanjut);

        btn.setOnClickListener(v -> {
            String bimbel = nama_bimbel.getText().toString();
            String almt = alamat.getText().toString();
            String hp = no_hp.getText().toString();

            if(bimbel.isEmpty() || almt.isEmpty() || hp.isEmpty()){
                Toast.makeText(this, "All field required", Toast.LENGTH_SHORT).show();
            }else{
//                Do Register
            }
        });
    }
}