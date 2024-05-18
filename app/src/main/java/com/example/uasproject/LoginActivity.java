package com.example.uasproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private FirebaseAuth mAuth;
    private EditText edt_email, edt_pass;
    private Button btn_login;
    private TextView to_register;
    private ProgressBar progressBar;

    //shared pref
    public static final String SHARED_PREF_NAME = "myPref";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        edt_email = findViewById(R.id.edt_email);
        edt_pass = findViewById(R.id.edt_pass);
        btn_login = findViewById(R.id.btn_login);
        to_register = findViewById(R.id.to_register);

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        btn_login.setOnClickListener(v -> {
            db = DatabaseHelper.getInstance(this);

            String email = edt_email.getText().toString();
            String pass = edt_pass.getText().toString();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Email atau Password tidak boleh kosong", Toast.LENGTH_SHORT).show();
            }else{
//                btn_login.setBackgroundResource(R.drawable.button_shape_off);
//                btn_login.setEnabled(false);
//                progressBar.setVisibility(View.VISIBLE);
//                progressBar.setIndeterminate(true);
//                progressBar.setIndeterminateTintList(ColorStateList.valueOf(getResources().getColor(R.color.bluePrimary)));

                mAuth.signInWithEmailAndPassword(email, pass).
                        addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(LoginActivity.this, "Berhasil masuk", Toast.LENGTH_SHORT).show();
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    Log.d("Login Succes", "Login Successfully");

                                    editor.putBoolean("masuk", true);
                                    editor.apply();
                                    Intent main = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(main);
                                    finish();
//                                    try{
//                                    }catch(Exception e){
//                                        Log.e("Session error", String.valueOf(e));
//                                    }finally {
//                                        btn_login.setBackgroundResource(R.drawable.button_shape);
//                                        btn_login.setEnabled(true);
//                                        progressBar.setVisibility(View.GONE);
//                                        progressBar.setIndeterminate(false);
//                                    }
                                }else{
                                    Toast.makeText(LoginActivity.this, "Gagal Masuk", Toast.LENGTH_SHORT).show();
                                    Log.e("Login Error", String.valueOf(task.getException()));
                                }
                            }
                        });
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