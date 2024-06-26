package com.example.uasproject.activities;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uasproject.utils.DBFirebase;
import com.example.uasproject.utils.DatabaseHelper;
import com.example.uasproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

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
        progressBar = findViewById(R.id.progressBar);

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        btn_login.setOnClickListener(v -> {
            db = DatabaseHelper.getInstance(this);

            String email = edt_email.getText().toString();
            String pass = edt_pass.getText().toString();

            btn_login.setBackgroundResource(R.drawable.button_shape_off);
            btn_login.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(getResources().getColor(R.color.bluePrimary)));

            try{
                SharedPreferences.Editor editor = sharedPreferences.edit();
                mAuth.signInWithEmailAndPassword(email, pass).
                        addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    DBFirebase getData = new DBFirebase();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    String id = user.getUid();
                                    DatabaseReference data = getData.getUser(id);

                                    Log.d("USER", String.valueOf(id));
                                    Log.d("USER DATA", String.valueOf(data));

                                    data.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try{
                                                if(snapshot.exists()){

                                                    if(snapshot.hasChild("name")){
                                                        String nama = snapshot.child("name").getValue(String.class);
                                                        editor.putString("nama", nama);
                                                    }else{
                                                        String agency = snapshot.child("agency").getValue(String.class);
                                                        editor.putString("nama", agency);
                                                    }

                                                    String email = snapshot.child("email").getValue(String.class);
                                                    String role = snapshot.child("role").getValue(String.class);

                                                    editor.putString("email", email);
                                                    editor.putBoolean("masuk", true);
                                                    editor.putString("role", role);
                                                    editor.apply();

                                                    Intent main = new Intent(LoginActivity.this, MainActivity.class);

                                                    btn_login.setBackgroundResource(R.drawable.button_shape);
                                                    btn_login.setEnabled(true);
                                                    progressBar.setVisibility(View.GONE);
                                                    progressBar.setIndeterminate(false);

                                                    Toast.makeText(LoginActivity.this, "Berhasil masuk", Toast.LENGTH_SHORT).show();
                                                    Log.d("Login Succes", "Login Successfully");

                                                    startActivity(main);
                                                    finish();
                                                }
                                            }catch(Exception e){
                                                btn_login.setBackgroundResource(R.drawable.button_shape);
                                                btn_login.setEnabled(true);
                                                progressBar.setVisibility(View.GONE);
                                                progressBar.setIndeterminate(false);
                                                Log.e("SNAPSHOT USER", String.valueOf(e));
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            btn_login.setBackgroundResource(R.drawable.button_shape);
                                            btn_login.setEnabled(true);
                                            progressBar.setVisibility(View.GONE);
                                            progressBar.setIndeterminate(false);
                                            Log.e("SNAPSHOT DATABASE", String.valueOf(error));
                                        }
                                    });
                                }else{
                                    Toast.makeText(LoginActivity.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    Log.e("Login Error", String.valueOf(task.getException()));

                                    btn_login.setBackgroundResource(R.drawable.button_shape);
                                    btn_login.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                    progressBar.setIndeterminate(false);
                                }
                            }
                        });
            }catch(Exception e){
                Log.e("Null Error", String.valueOf(e));
                Toast.makeText(LoginActivity.this, "Email dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show();

                btn_login.setBackgroundResource(R.drawable.button_shape);
                btn_login.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                progressBar.setIndeterminate(false);
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