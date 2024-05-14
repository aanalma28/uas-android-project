package com.example.uasproject;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText edt_nama, edt_email, edt_pass, edt_conf_pass;
    private Button btn_register;
    private TextView to_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        edt_nama = findViewById(R.id.edt_nama);
        edt_email = findViewById(R.id.edt_email);
        edt_pass = findViewById(R.id.edt_pass);
        edt_conf_pass = findViewById(R.id.edt_conf_pass);
        btn_register = findViewById(R.id.btn_register);
        to_login = findViewById(R.id.to_login);

        btn_register.setOnClickListener(v -> {
            String inNama = edt_nama.getText().toString();
            String inEmail = edt_email.getText().toString();
            String inPass = edt_pass.getText().toString();
            String inConfPass = edt_conf_pass.getText().toString();

            if (!inConfPass.equals(inPass)){
                edt_conf_pass.setError("Password tidak sama");
            }else{
                try{
                    mAuth.createUserWithEmailAndPassword(inEmail, inPass).
                            addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        FirebaseAuth user = mAuth;
                                        String userId = user.getCurrentUser().getUid();
                                        String userEmail = user.getCurrentUser().getEmail();
                                        DBFirebase db = new DBFirebase();

                                        db.addUser(inNama, userEmail, inPass, userId);

                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("Create User", "createUserWithEmail:success");
                                        Toast.makeText(RegisterActivity.this, "Register berhasil!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);

                                    }else{
                                        // If sign in fails, display a message to the user.
                                        Log.w("Failed Auth", "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }catch(Exception e){
                    Toast.makeText(RegisterActivity.this, String.valueOf(e), Toast.LENGTH_SHORT).show();

                    Log.d("Debug App", "This is Debug");
                    Log.i("Debug App", "This is info");
                    Log.w("Debug App", "This is Warning");
                    Log.e("Debug Error", String.valueOf(e));
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