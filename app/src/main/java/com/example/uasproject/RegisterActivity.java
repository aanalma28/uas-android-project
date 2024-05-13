package com.example.uasproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private EditText edt_nama, edt_email, edt_pass, edt_conf_pass;
    private Button btn_register;
    private TextView to_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edt_nama = findViewById(R.id.edt_nama);
        edt_email = findViewById(R.id.edt_email);
        edt_pass = findViewById(R.id.edt_pass);
        edt_conf_pass = findViewById(R.id.edt_conf_pass);
        btn_register = findViewById(R.id.btn_register);
        to_login = findViewById(R.id.to_login);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inNama = edt_nama.getText().toString();
                String inEmail = edt_email.getText().toString();
                String inPass = edt_pass.getText().toString();
                String inConfPass = edt_conf_pass.getText().toString();

                if (!inConfPass.equals(inPass)){
                    edt_conf_pass.setError("Password tidak sama");
                }else{
                    Boolean btn_register = db.saveUser(inNama, inEmail, inPass);
                    if (btn_register == true){
                        Toast.makeText(RegisterActivity.this, "Register berhasil!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(RegisterActivity.this, "Register gagal!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        to_login.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }
        );

    }
}