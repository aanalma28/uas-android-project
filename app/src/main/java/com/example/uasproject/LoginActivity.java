package com.example.uasproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

public class LoginActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private EditText edt_email, edt_pass;
    private Button btn_login;
    private TextView to_register;

    //shared pref
    public static final String SHARED_PREF_NAME = "myPref";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edt_email = findViewById(R.id.edt_email);
        edt_pass = findViewById(R.id.edt_pass);
        btn_login = findViewById(R.id.btn_login);
        to_register = findViewById(R.id.to_register);

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        btn_login.setOnClickListener(v -> {
            db = DatabaseHelper.getInstance(this);
            try {

                String email = edt_email.getText().toString();
                String pass = edt_pass.getText().toString();

                if (email.isEmpty() && pass.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "email atau password salah!", Toast.LENGTH_SHORT).show();
                }else{
                    Boolean login = db.checkLogin(email, pass);
                    if (login == true){
                        Boolean updateSession = db.upgradeSession("ada", 1);
                        if (updateSession == true){
                            Toast.makeText(LoginActivity.this, "Berhasil masuk", Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            editor.putBoolean("masuk", true);
                            editor.apply();
                            Intent main = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(main);
                            finish();
                        }
                    }else{
                        Toast.makeText(LoginActivity.this, "Gagal masuk", Toast.LENGTH_SHORT).show();
                    }
                }

            }catch (Exception e){
                Toast.makeText(LoginActivity.this, String.valueOf(e), Toast.LENGTH_SHORT).show();

                Log.d("Debug App", "This is Debug");
                Log.i("Debug App", "This is info");
                Log.w("Debug App", "This is Warning");
                Log.e("Debug Error", String.valueOf(e));
            }
        });

        to_register.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                        startActivity(intent);
                    }
                }
        );

    }
}